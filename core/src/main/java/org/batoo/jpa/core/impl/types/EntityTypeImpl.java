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
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.AbstractResolver;
import org.batoo.jpa.core.impl.instance.BasicResolver;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.PhysicalColumn;
import org.batoo.jpa.core.impl.jdbc.PhysicalTable;
import org.batoo.jpa.core.impl.jdbc.SelectHelper;
import org.batoo.jpa.core.impl.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.mapping.AbstractPhysicalMapping;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.BasicColumnTemplate;
import org.batoo.jpa.core.impl.mapping.BasicMapping;
import org.batoo.jpa.core.impl.mapping.ColumnTemplate;
import org.batoo.jpa.core.impl.mapping.JoinColumnTemplate;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.mapping.OwnedAssociation;
import org.batoo.jpa.core.impl.mapping.OwnerAssociationMapping;
import org.batoo.jpa.core.impl.mapping.TableTemplate;
import org.batoo.jpa.core.impl.mapping.TypeFactory;
import org.batoo.jpa.core.impl.reflect.ReflectHelper;
import org.batoo.jpa.core.jdbc.DDLMode;
import org.batoo.jpa.core.jdbc.IdType;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
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
	private final Map<String, OwnedAssociation<?, ?>> ownedAssociations = Maps.newHashMap();
	private final Map<String, BasicMapping<?, ?>> idMappings = Maps.newHashMap();
	private final Map<String, BasicMapping<?, ?>> identityMappings = Maps.newHashMap();

	private final Constructor<X> constructor;

	private final Set<PhysicalColumn> columns = Sets.newHashSet();

	private PhysicalTable primaryTable;
	private final Map<String, PhysicalTable> tables = Maps.newHashMap();

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

		this.constructor = this.getDefaultConstructor(javaType);
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
		final PhysicalTable table = StringUtils.isNotBlank(template.getTableName()) ? this.tables.get(template.getTableName())
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
		final String tableName = association.getType().getPrimaryTable().getQualifiedPhysicalName();
		final String columnNames = Joiner.on("_").join(Lists.transform(foreignKeyColumns, new Function<PhysicalColumn, String>() {

			@Override
			public String apply(PhysicalColumn input) {
				return input.getReferencedColumn().getPhysicalName();
			}
		}));

		final String foreignKeyName = tableName + "_" + columnNames;
		final PhysicalTable table = foreignKeyColumns.get(0).getTable();

		table.addForeignKey(new ForeignKey(table.getPhysicalName(), foreignKeyName, tableName, foreignKeyColumns));
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

			if (mapping instanceof OwnedAssociation) {
				this.ownedAssociations.put(name, (OwnedAssociation<?, ?>) mapping);
			}
			else {
				final List<PhysicalColumn> keyColumns = Lists.newArrayList();

				final OwnerAssociationMapping<?, ?> associationMapping = (OwnerAssociationMapping<?, ?>) association;
				for (final ColumnTemplate<?, ?> template : associationMapping.getColumnTemplates()) {
					keyColumns.add(this.addColumn(template, associationMapping));
				}

				this.addForeignKey(association, keyColumns);
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
			for (final PhysicalTable table : this.tables.values()) {
				if (firstPass) {
					if (!table.isPrimary()) {
						for (final PhysicalColumn column : this.primaryTable.getPrimaryKeys()) {
							new PhysicalColumn(table, column);
						}
					}
				}

				table.ddl(this.metaModel.getJdbcAdapter(), datasource, ddlMode, schemas, firstPass);
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

	private Constructor<X> getDefaultConstructor(final Class<X> javaType) throws MappingException {
		final Constructor<X> constructor = AccessController.doPrivileged(new PrivilegedAction<Constructor<X>>() {
			@Override
			public Constructor<X> run() {
				try {
					return javaType.getConstructor();
				}
				catch (final NoSuchMethodException e) {
					return null;
				}
				catch (final SecurityException e) {
					return null;
				}
			}
		});

		if (constructor == null) {
			throw new MappingException("Class " + javaType + " must have a default constructor");
		}

		ReflectHelper.makeAccessible(constructor);

		return constructor;
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
		final Map<String, AbstractResolver<X>> resolvers = Maps.transformValues(this.mappings,
			new Function<AbstractMapping<?, ?>, AbstractResolver<X>>() {

				private AbstractResolver<X> resolver;

				@Override
				public AbstractResolver<X> apply(AbstractMapping<?, ?> input) {
					if (this.resolver == null) {
						this.resolver = input.createResolver(instance);
					}

					return this.resolver;
				}

			});

		return new ManagedInstance<X>(this, session, instance, this.getManagedIdForInstance(session, instance), resolvers);
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
	public PhysicalTable getPrimaryTable() {
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
	public PhysicalTable getTable(String tableName) {
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
	public Map<String, PhysicalTable> getTables() {
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
				((AttributeImpl<? super X, ?>) attribute).link(new LinkedList<AttributeImpl<?, ?>>());
			}
		}
	}

	/**
	 * Creates a new instance.
	 * 
	 * @return a new instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public X newInstance() {
		try {
			return this.constructor.newInstance();
		}
		catch (final Exception e) {
			// Not possible
		}

		return null;
	}

	/**
	 * Returns a new managed instance with its id populated from id.
	 * 
	 * @param id
	 *            the id for the new managed instance
	 * @return the new managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedInstance<? super X> newInstanceWithId(SessionImpl session, Object id) {
		final X instance = this.newInstance();

		final ManagedInstance<? super X> managedInstance = this.getManagedInstance(session, instance);
		managedInstance.getId().populate(id);

		return managedInstance;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<Class<? extends Annotation>> parse() throws BatooException {
		final Set<Class<? extends Annotation>> annotations = super.parse();

		final Class<X> type = this.getJavaType();

		final Entity entity = type.getAnnotation(Entity.class);
		if (entity == null) {
			throw new MappingException("Type is not an entity " + type);
		}
		annotations.add(Entity.class);

		final String error = AccessController.doPrivileged(new PrivilegedAction<String>() {

			@Override
			public String run() {
				try {
					type.getConstructor();
				}
				catch (final SecurityException e) {
					// not likely
				}
				catch (final NoSuchMethodException e) {
					return "Type " + type + " does not have a default constructor";
				}

				if (type.isInterface() || type.isAnnotation() || type.isEnum()) {
					return "Type " + type + " is not a regular class";
				}

				if (Modifier.isFinal(type.getModifiers())) {
					return "Type " + type + " must not be final";
				}

				return null;
			}
		});

		if (error != null) {
			throw new MappingException(error);
		}

		return annotations;
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
		for (final PhysicalTable table : this.tables.values()) {
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
	public ManagedInstance<X> performSelect(SessionImpl session, ManagedId<X> managedId) throws SQLException {
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
		// vlink this entities tables
		for (final TableTemplate template : this.getTableTemplates()) {
			final PhysicalTable table = new PhysicalTable(this, template, this.metaModel.getJdbcAdapter());
			if (table.isPrimary()) {
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

				final PhysicalTable table = new PhysicalTable(this, template, this.metaModel.getJdbcAdapter());
				if (table.isPrimary()) {
					this.primaryTable = table;
				}

				this.tables.put(table.getName(), table);
			}
		}

	}
}
