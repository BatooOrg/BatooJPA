/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core.impl.types;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.proxy.Invoker;
import org.apache.commons.proxy.factory.javassist.JavassistProxyFactory;
import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.AbstractResolver;
import org.batoo.jpa.core.impl.instance.BasicResolver;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.jdbc.PhysicalColumn;
import org.batoo.jpa.core.impl.jdbc.SelectHelper;
import org.batoo.jpa.core.impl.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.mapping.AbstractPhysicalMapping;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.BasicColumnTemplate;
import org.batoo.jpa.core.impl.mapping.BasicMapping;
import org.batoo.jpa.core.impl.mapping.ColumnTemplate;
import org.batoo.jpa.core.impl.mapping.JoinColumnTemplate;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.mapping.OwnerAssociationMapping;
import org.batoo.jpa.core.impl.mapping.OwnerManyToManyMapping;
import org.batoo.jpa.core.impl.mapping.OwnerOneToManyMapping;
import org.batoo.jpa.core.impl.mapping.TableTemplate;
import org.batoo.jpa.core.impl.mapping.TypeFactory;
import org.batoo.jpa.core.jdbc.DDLMode;
import org.batoo.jpa.core.jdbc.IdType;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link EntityType}.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityTypeImpl<X> extends IdentifiableTypeImpl<X> implements EntityType<X>, Comparable<EntityType<X>> {

	private static final BLogger LOG = BLogger.getLogger(EntityTypeImpl.class);

	private static final Object NO_TOP_TYPE = new Object();

	private final Map<String, AbstractMapping<?, ?>> mappings = Maps.newHashMap();
	private final Map<String, Association<?, ?>> associations = Maps.newHashMap();
	private final Map<String, BasicMapping<?, ?>> idMappings = Maps.newHashMap();
	private final Map<String, BasicMapping<?, ?>> identityMappings = Maps.newHashMap();
	private final Map<String, Column> attributeOverrides = Maps.newHashMap();

	private final Set<PhysicalColumn> columns = Sets.newHashSet();

	private EntityTable primaryTable;
	private final Map<String, EntityTable> tables = Maps.newHashMap();
	private final Map<String, JoinTable> joinTables = Maps.newHashMap();

	private Object topType;

	private final SelectHelper<X> selectHelper;

	/**
	 * @param metaModel
	 *            the meta model of the persistence
	 * @param javaType
	 *            the javatype of the entity
	 * @param name
	 *            name of the entity
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeImpl(MetamodelImpl metaModel, final Class<X> javaType) throws MappingException {
		super(metaModel, javaType);

		metaModel.addEntity(this);

		this.selectHelper = new SelectHelper<X>(this);
	}

	/**
	 * Generates the actual physical column from the template.
	 * 
	 * @param template
	 *            the template to use
	 * @param mapping
	 *            the mapping for the column
	 * @return the physical column created
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized PhysicalColumn addColumn(ColumnTemplate<?, ?> template, AbstractPhysicalMapping<?, ?> mapping)
		throws MappingException {
		// Determine the table name
		final EntityTable table = StringUtils.isNotBlank(template.getTableName()) ? this.tables.get(template.getTableName())
			: this.getPrimaryTable();

		if (table == null) {
			throw new MappingException("Table " + template.getTableName() + " not found on " + this.javaType);
		}

		PhysicalColumn physicalColumn;
		if (template instanceof BasicColumnTemplate) { // Basic Column
			final BasicColumnTemplate<?, ?> basic = (BasicColumnTemplate<?, ?>) template;

			final AttributeImpl<?, ?> attribute = template.getAttribute();
			final int sqlType = TypeFactory.getSqlType(attribute.getJavaType(), attribute.getTemporalType(), attribute.getEnumType(),
				attribute.isLob());

			physicalColumn = new PhysicalColumn(mapping, table, basic, sqlType);
		}
		else { // Join Column
			final JoinColumnTemplate<?, ?> join = (JoinColumnTemplate<?, ?>) template;
			final BasicMapping<?, ?> referencedMapping = join.getReferencedMapping();
			final SingularAttributeImpl<?, ?> attribute = referencedMapping.getDeclaringAttribute();

			final int sqlType = TypeFactory.getSqlType(attribute.getJavaType(), attribute.getTemporalType(), attribute.getEnumType(),
				attribute.isLob());
			final BasicColumnTemplate<?, ?> referencedColumn = (BasicColumnTemplate<?, ?>) attribute.getColumns().iterator().next();

			physicalColumn = new PhysicalColumn(mapping, table, join, referencedColumn.getPhysicalColumn(), sqlType);
		}

		this.columns.add(physicalColumn);

		return physicalColumn;
	}

	private void addForeignKey(final Association<?, ?> association, final List<PhysicalColumn> foreignKeyColumns) {
		final String tableName = association.getType().getPrimaryTable().getQualifiedName();
		final EntityTable table = (EntityTable) foreignKeyColumns.get(0).getTable();

		table.addForeignKey(new ForeignKey(table.getName(), tableName, foreignKeyColumns));
	}

	/**
	 * Adds a join table to the type
	 * 
	 * @param joinTable
	 *            the join table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addJoinTable(JoinTable joinTable) {
		this.joinTables.put(joinTable.getName(), joinTable);
	}

	/**
	 * Adds the mapping to the list of mappings.
	 * 
	 * @param mapping
	 *            the mapping to add
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addMapping(AbstractMapping<?, ?> mapping) throws MappingException {
		final String name = mapping.getDeclaringAttribute().getName();

		if (mapping instanceof BasicMapping) {
			final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) mapping;
			final SingularAttributeImpl<?, ?> singularAttribute = basicMapping.getDeclaringAttribute();

			// Separate out the identity attributes
			if (singularAttribute.isId()) {
				this.idMappings.put(name, basicMapping);
			}

			// Separate out the identity id attributes
			if (singularAttribute.getIdType() == IdType.IDENTITY) {
				this.identityMappings.put(name, basicMapping);
			}

			for (final ColumnTemplate<?, ?> template : basicMapping.getColumnTemplates()) {
				this.addColumn(template, basicMapping);
			}
		}

		if (mapping instanceof Association) {
			final Association<?, ?> association = (Association<?, ?>) mapping;

			this.metaModel.addAssociation(association);
			this.associations.put(name, association);

			if ((mapping instanceof OwnerAssociationMapping) && !(mapping instanceof OwnerOneToManyMapping)) {
				final OwnerAssociationMapping<?, ?> associationMapping = (OwnerAssociationMapping<?, ?>) association;

				final List<PhysicalColumn> keyColumns = Lists.newArrayList();
				for (final ColumnTemplate<?, ?> template : associationMapping.getColumnTemplates()) {
					keyColumns.add(this.addColumn(template, associationMapping));
				}

				if (!(association instanceof OwnerManyToManyMapping)) {
					this.addForeignKey(association, keyColumns);
				}
			}
		}

		this.mappings.put(name, mapping);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int compareTo(EntityType<X> o) {
		return this.getName().compareTo(o.getName());
	}

	@SuppressWarnings("unchecked")
	private X createProxy(final SessionImpl session, final X instance, final Object id) {
		return (X) new JavassistProxyFactory().createInvokerProxy(this.javaType.getClassLoader(), new Invoker() {

			private X initialized;
			private final List<String> idMethods = Lists.newArrayList();

			@Override
			public Object invoke(Object proxy, final Method method, Object[] arguments) throws Throwable {
				if (!method.isAccessible()) {
					AccessController.doPrivileged(new PrivilegedAction<Void>() {

						@Override
						public Void run() {
							method.setAccessible(true);
							return null;
						}
					});
				}

				// if initialized, let go
				if (this.initialized != null) {
					return method.invoke(this.initialized, arguments);
				}

				method.setAccessible(true);

				if (arguments.length == 0) {
					final String methodName = method.getName();
					if (this.idMethods.contains(methodName)) { // if known id method, let go
						return method.invoke(instance, arguments);
					}

					if (methodName.startsWith("get") && (methodName.length() > 3)) { // check if id method
						for (final SingularAttributeImpl<? super X, ?> attribute : EntityTypeImpl.this.idAttributes.values()) {
							if (attribute.getGetterName().equals(method.getName())) {
								this.idMethods.add(methodName);
								return method.invoke(instance, arguments);
							}
						}
					}
				}

				this.initialized = session.getEntityManager().find(EntityTypeImpl.this.getJavaType(), id);

				return method.invoke(this.initialized, arguments);
			}
		}, new Class[] { this.javaType });
	}

	/**
	 * Performs the ddl operations for the entity.
	 * <p>
	 * On the first pass the table is created.
	 * <p>
	 * On the second pass the foreign keys are created.
	 * 
	 * @param datasource
	 *            the datasource
	 * @param schemas
	 *            set of schemas
	 * @param ddlMode
	 *            the ddl mode
	 * 
	 * @throws BatooException
	 *             thrown if DDL operations cannot be executed
	 * @param firstPass
	 *            if first pass
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void ddl(DataSourceImpl datasource, Set<String> schemas, DDLMode ddlMode, boolean firstPass) throws BatooException {
		LOG.info("Performing DDL operations for {0}, mode {2}", this, ddlMode);

		try {
			for (final EntityTable table : this.tables.values()) {
				if (firstPass) {
					if (!table.isPrimary()) {
						final List<PhysicalColumn> keyColumns = Lists.newArrayList();
						for (final PhysicalColumn column : this.primaryTable.getPrimaryKeys()) {
							keyColumns.add(new PhysicalColumn(table, column));
						}

						table.addForeignKey(new ForeignKey(table.getName(), this.primaryTable.getName(), keyColumns));
					}
				}

				table.ddl(this.metaModel.getJdbcAdapter(), datasource, ddlMode, schemas, firstPass);
			}

			for (final JoinTable joinTable : this.joinTables.values()) {
				joinTable.ddl(this.metaModel.getJdbcAdapter(), datasource, ddlMode, schemas, firstPass);
			}
		}
		catch (final SQLException e) {
			throw new MappingException("DDL operation failed on " + this.getName(), e);
		}
	}

	/**
	 * Returns the collection of associations.
	 * 
	 * @return the collection of associations
	 * 
	 * @since $version
	 * @author hceylan
	 * @return
	 */
	public Collection<Association<?, ?>> getAssociations() {
		return this.associations.values();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<X> getBindableJavaType() {
		return this.getJavaType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BindableType getBindableType() {
		return BindableType.ENTITY_TYPE;
	}

	/**
	 * Returns the id mappings of the entity.
	 * 
	 * @return the id mappings of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Collection<BasicMapping<?, ?>> getIdMappings() {
		return this.idMappings.values();
	}

	/**
	 * Returns the managed id for the id.
	 * 
	 * @param instance
	 *            the instance to create managed id or null
	 * @return managed id for the instance
	 * @throws NullPointerException
	 *             thrown if the id is null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedId<? super X> getManagedId(SessionImpl session, final Object id) {
		if (id == null) {
			throw new NullPointerException();
		}

		if ((this.getTopType() != null) && (this.getTopType() != this)) {
			return this.getTopType().getManagedId(session, id);
		}

		final X instance = this.newInstance();

		final Map<String, BasicResolver<X>> resolvers = Maps.transformValues(this.idMappings,
			new Function<BasicMapping<?, ?>, BasicResolver<X>>() {

				private BasicResolver<X> resolver;

				@Override
				public BasicResolver<X> apply(BasicMapping<?, ?> input) {
					if (this.resolver == null) {
						this.resolver = input.createResolver(instance);
					}

					return this.resolver;
				}

			});

		final ManagedId<X> managedId = new ManagedId<X>(this, session, instance, resolvers);

		managedId.populate(id);

		return managedId;
	}

	/**
	 * Returns the managed id for the instance.
	 * 
	 * @param instance
	 *            the instance to create managed id or null
	 * @return managed id for the instance
	 * @throws NullPointerException
	 *             thrown if the instance is null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedId<? super X> getManagedIdForInstance(SessionImpl session, final X instance) {
		if (instance == null) {
			throw new NullPointerException();
		}

		if ((this.getTopType() != null) && (this.getTopType() != this)) {
			return this.getTopType().getManagedIdForInstance(session, instance);
		}

		final Map<String, BasicResolver<X>> resolvers = Maps.transformValues(this.idMappings,
			new Function<BasicMapping<?, ?>, BasicResolver<X>>() {

				private BasicResolver<X> resolver;

				@Override
				public BasicResolver<X> apply(BasicMapping<?, ?> input) {
					if (this.resolver == null) {
						this.resolver = input.createResolver(instance);
					}

					return this.resolver;
				}

			});

		return new ManagedId<X>(this, session, instance, resolvers);
	}

	/**
	 * Returns the managed instance for the instance.
	 * 
	 * @param instance
	 *            the instance to create managed instance
	 * @param session
	 *            the session the instance belongs to
	 * @return managed instance for the instance
	 * 
	 * @since $version
	 * @author hceylan
	 * @param <Y>
	 */
	public ManagedInstance<X> getManagedInstance(SessionImpl session, final X instance) {
		return this.getManagedInstance0(session, instance, this.getManagedIdForInstance(session, instance));
	}

	private ManagedInstance<X> getManagedInstance0(SessionImpl session, final X instance, ManagedId<? super X> managedId) {
		final Map<String, AbstractResolver<X>> resolvers = Maps.transformValues(this.mappings,
			new Function<AbstractMapping<?, ?>, AbstractResolver<X>>() {

				@Override
				public AbstractResolver<X> apply(AbstractMapping<?, ?> input) {
					return input.createResolver(instance);
				}

			});

		return new ManagedInstance<X>(this, session, instance, managedId, resolvers);
	}

	/**
	 * Returns the mapping with the name
	 * 
	 * @param name
	 *            the name of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 * @return
	 */
	public AbstractMapping<?, ?> getMapping(String name) {
		return this.mappings.get(name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistenceType getPersistenceType() {
		return PersistenceType.ENTITY;
	}

	/**
	 * Returns the primary table
	 * 
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTable getPrimaryTable() {
		if (this.primaryTable != null) {
			return this.primaryTable;
		}

		final IdentifiableTypeImpl<? super X> supertype = this.getSupertype();
		if (supertype instanceof EntityTypeImpl) {
			return ((EntityTypeImpl<? super X>) supertype).getPrimaryTable();
		}

		return null;
	}

	/**
	 * @param tableName
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTable getTable(String tableName) {
		if (StringUtils.isBlank(tableName)) {
			return this.getPrimaryTable();
		}

		return this.tables.get(tableName);
	}

	/**
	 * Returns the tables.
	 * 
	 * @return the tables
	 * @since $version
	 */
	public Map<String, EntityTable> getTables() {
		return this.tables;
	}

	/**
	 * Returns the top most entity.
	 * 
	 * @return the top most entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public EntityTypeImpl<? super X> getTopType() {
		if (this.topType == null) {
			final IdentifiableTypeImpl<? super X> supertype = this.getSupertype();

			if (supertype instanceof EntityTypeImpl) {
				this.topType = ((EntityTypeImpl<? super X>) supertype).getTopType();
			}

			if (this.topType == null) {
				this.topType = this;
			}
		}

		return this.topType != NO_TOP_TYPE ? (EntityTypeImpl<? super X>) this.topType : null;
	}

	/**
	 * Returns if the entity has any identity attribute.
	 * 
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean hasIdentityAttribute() {
		return this.identityMappings.size() > 0;
	}

	/**
	 * Horizontally links the entity.
	 * 
	 * @param dataSource
	 *            the datasource to use
	 * @param basic
	 *            if basic only (first and second pass)
	 * @throws BatooException
	 *             thrown in case of a mapping error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void link(DataSource dataSource, boolean basic) throws BatooException {
		LOG.debug("Horizontally linking {0}, {1} pass", this, basic ? "first" : "second");

		for (final Attribute<? super X, ?> attribute : this.setAttributes) {
			final boolean association = attribute.getPersistentAttributeType() != PersistentAttributeType.BASIC;

			if (association ^ basic) {
				((AttributeImpl<? super X, ?>) attribute).link(new LinkedList<AttributeImpl<?, ?>>(), this.attributeOverrides);
			}
		}
	}

	/**
	 * Returns a new managed instance with its id populated from id.
	 * <p>
	 * if lazy is true Lazy properties will be proxied.
	 * 
	 * @param managedId
	 *            the id for the new managed instance
	 * @param session
	 *            the session
	 * @param lazy
	 *            if the instance will be lazy
	 * @return the new managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public ManagedInstance<? super X> newInstanceWithId(final SessionImpl session, final ManagedId<? super X> managedId, boolean lazy) {
		if (!lazy) {
			return this.getManagedInstance0(session, (X) managedId.getInstance(), managedId);
		}

		final X proxy = this.createProxy(session, (X) managedId.getInstance(), managedId.getId());
		managedId.proxify(proxy);

		return this.getManagedInstance0(session, (X) managedId.getInstance(), managedId);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Class<? extends Annotation>> parse() throws BatooException {
		final Set<Class<? extends Annotation>> parsed = super.parse();

		final Class<X> type = this.getJavaType();

		final Entity entity = type.getAnnotation(Entity.class);
		if (entity == null) {
			throw new MappingException("Type is not an entity " + type);
		}
		parsed.add(Entity.class);

		this.parseAttributeOverrides(type, parsed);

		this.performClassChecks(type);

		return parsed;
	}

	private void parseAttributeOverrides(Class<X> type, final Set<Class<? extends Annotation>> parsed) {
		final AttributeOverrides attributeOverrides = type.getAnnotation(AttributeOverrides.class);
		if (attributeOverrides != null) {
			for (final AttributeOverride attributeOverride : attributeOverrides.value()) {
				this.attributeOverrides.put(attributeOverride.name(), attributeOverride.column());
			}

			parsed.add(AttributeOverrides.class);
		}

		final AttributeOverride attributeOverride = type.getAnnotation(AttributeOverride.class);
		if (attributeOverride != null) {
			this.attributeOverrides.put(attributeOverride.name(), attributeOverride.column());

			parsed.add(AttributeOverride.class);
		}
	}

	/**
	 * Performs inserts to each table for the managed instance.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance to perform insert for
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performInsert(Connection connection, ManagedInstance<X> managedInstance) throws SQLException {
		// first insert to the primary table
		this.primaryTable.performInsert(connection, managedInstance);
		managedInstance.setExecuted();

		// then to the remaining tables
		for (final EntityTable table : this.tables.values()) {
			if (table.isPrimary()) {
				continue;
			}

			table.performInsert(connection, managedInstance);
		}
	}

	/**
	 * Performs select from each table for the managed instance.
	 * 
	 * @param session
	 *            the session to use
	 * @param managedId
	 *            the managed id to perform select for
	 * @return returns the managed instance
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public X performSelect(SessionImpl session, ManagedId<X> managedId) throws SQLException {
		return this.selectHelper.select(session, managedId);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "EntityTypeImpl [name=" + this.name + ", IdAttributes=" + this.idAttributes.values() + ", attributes=" + this.setAttributes
			+ "]";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void vlink() throws BatooException {
		// TODO Check clashes from MappedSuperclasses and super entities, during inheritance implementation.
		super.vlink();

		this.vlinkTables();
	}

	/**
	 * VLinks tables from super type.
	 * 
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void vlinkTables() throws MappingException {
		// first do the self defined tables
		for (final TableTemplate template : this.getTableTemplates()) {
			final EntityTable table = new EntityTable(this, template, this.metaModel.getJdbcAdapter());
			if (template.isPrimary()) {
				this.primaryTable = table;
			}

			this.tables.put(table.getName(), table);
		}

		final IdentifiableTypeImpl<? super X> supertype = this.getSupertype();
		if ((supertype != null) && (supertype instanceof MappedSuperclassTypeImpl)) {
			for (final TableTemplate template : supertype.getTableTemplates()) {

				// is the table overridden
				if (this.tables.containsKey(template.getName())) {
					continue;
				}

				final EntityTable table = new EntityTable(this, template, this.metaModel.getJdbcAdapter());
				if (table.isPrimary()) {
					this.primaryTable = table;
				}

				this.tables.put(table.getName(), table);
			}
		}

	}
}
