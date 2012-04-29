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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.BasicResolver;
import org.batoo.jpa.core.impl.instance.InstanceInvoker;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.jdbc.PhysicalColumn;
import org.batoo.jpa.core.impl.jdbc.RefreshHelper;
import org.batoo.jpa.core.impl.jdbc.SelectHelper;
import org.batoo.jpa.core.impl.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.mapping.AbstractPhysicalMapping;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.BasicColumnTemplate;
import org.batoo.jpa.core.impl.mapping.BasicMapping;
import org.batoo.jpa.core.impl.mapping.ColumnTemplate;
import org.batoo.jpa.core.impl.mapping.EntityInheritence;
import org.batoo.jpa.core.impl.mapping.JoinColumnTemplate;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.mapping.OwnerAssociationMapping;
import org.batoo.jpa.core.impl.mapping.OwnerManyToManyMapping;
import org.batoo.jpa.core.impl.mapping.OwnerOneToManyMapping;
import org.batoo.jpa.core.impl.mapping.TableTemplate;
import org.batoo.jpa.core.impl.mapping.TypeFactory;
import org.batoo.jpa.core.jdbc.DDLMode;
import org.batoo.jpa.core.jdbc.IdType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
	private final List<BasicMapping<?, ?>> idMappings = Lists.newArrayList();
	private final Map<String, BasicMapping<?, ?>> identityMappings = Maps.newHashMap();
	private final Map<String, Column> attributeOverrides = Maps.newHashMap();

	private EntityInheritence inheritance;

	private EntityTable primaryTable;

	private final Map<String, EntityTable> tables = Maps.newHashMap();
	private final Map<String, JoinTable> joinTables = Maps.newHashMap();
	private Object topType;

	private final Map<String, TableTemplate> tableTemplates = Maps.newHashMap();

	private final SelectHelper<X> selectHelper;
	private final RefreshHelper<X> refreshHelper;

	private Object discriminatorValue;

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
		this.refreshHelper = new RefreshHelper<X>(this);
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
	 * @param id
	 *            if the mapping is an id mapping
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addMapping(AbstractMapping<?, ?> mapping, boolean id) throws MappingException {
		final String name = mapping.getDeclaringAttribute().getName();

		if (mapping instanceof BasicMapping) {
			final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) mapping;
			final SingularAttributeImpl<?, ?> singularAttribute = basicMapping.getDeclaringAttribute();

			// Separate out the identity attributes
			if (id || singularAttribute.isId()) {
				this.idMappings.add(basicMapping);
			}

			// Separate out the identity id attributes
			if ((singularAttribute.getIdType() == IdType.IDENTITY)) {
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
	 * 
	 * Returns the discriminator value for the entity.
	 * 
	 * @return the discriminator value for the entity
	 * @since $version
	 * @author hceylan
	 */
	public Object getDiscriminatorValue() {
		return this.discriminatorValue != null ? this.discriminatorValue : this.name;
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
		return this.idMappings;
	}

	/**
	 * Returns the inheritance.
	 * 
	 * @return the inheritance
	 * @since $version
	 */
	public EntityInheritence getInheritance() {
		return this.inheritance;
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
	public ManagedId<X> getManagedId(SessionImpl session, final Object id) {
		if (id == null) {
			throw new NullPointerException();
		}

		final X instance = this.newInstance();

		final List<BasicMapping<?, ?>> idMappings = this.getTopType().idMappings;
		final BasicResolver[] resolvers = new BasicResolver[idMappings.size()];
		for (int i = 0; i < idMappings.size(); i++) {
			resolvers[i] = idMappings.get(i).createResolver(instance);
		}

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
	public ManagedId<X> getManagedIdForInstance(SessionImpl session, final X instance) {
		if (instance == null) {
			throw new NullPointerException();
		}

		final List<BasicMapping<?, ?>> idMappings = this.getTopType().idMappings;
		final BasicResolver[] resolvers = new BasicResolver[idMappings.size()];
		for (int i = 0; i < idMappings.size(); i++) {
			resolvers[i] = idMappings.get(i).createResolver(instance);
		}

		return new ManagedId<X>(this, session, instance, resolvers);
	}

	/**
	 * Returns the managed instance for the instance.
	 * 
	 * @param instance
	 *            the instance to create managed instance
	 * @param session
	 *            the session the instance belongs to
	 * @param newInstance
	 *            if the instance is new
	 * @return managed instance for the instance
	 * 
	 * @since $version
	 * @author hceylan
	 * @param <Y>
	 */
	public ManagedInstance<X> getManagedInstance(SessionImpl session, final X instance) {
		return new ManagedInstance<X>(this, session, this.getManagedIdForInstance(session, instance));
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

		for (final Attribute<? super X, ?> attribute : this.attributes.values()) {
			final boolean association = attribute.getPersistentAttributeType() != PersistentAttributeType.BASIC;

			if (association ^ basic) {
				((AttributeImpl<? super X, ?>) attribute).link(new LinkedList<AttributeImpl<?, ?>>(), this.attributeOverrides);
			}
		}

		if (!basic && (this.getTopType() == null)) {
			if (this.idJavaType == null) {
				if (!this.hasSingleIdAttribute()) {
					throw new MappingException(this.javaType
						+ " violates id specification. Unless specified by IdClass, there can only be one id attribute");
				}

				final SingularAttributeImpl<?, ?> attribute = this.idAttributes[0];
				this.idType = this.metaModel.getType(attribute.getJavaType());
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
	public ManagedInstance<X> newInstanceWithId(final SessionImpl session, final ManagedId<X> managedId, boolean lazy) {
		if (!lazy) {
			return new ManagedInstance<X>(this, session, managedId);
		}

		InstanceInvoker.<X> createInvoker(this.javaType.getClassLoader(), this, session, managedId);

		return new ManagedInstance<X>(this, session, managedId);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void parse(Set<Class<? extends Annotation>> parsed) throws BatooException {
		this.parsePrimaryTable(parsed);
		this.parseSecondaryTables(parsed);

		super.parse(parsed);

		final Class<X> type = this.getJavaType();

		final Entity entity = type.getAnnotation(Entity.class);
		if (entity == null) {
			throw new MappingException("Type is not an entity " + type);
		}
		parsed.add(Entity.class);

		this.parseAttributeOverrides(type, parsed);
		this.parseInheritence(type, parsed);

		this.performClassChecks(type);
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

	private void parseInheritence(Class<X> type, Set<Class<? extends Annotation>> parsed) throws MappingException {
		final DiscriminatorColumn discriminatorColumn = type.getAnnotation(DiscriminatorColumn.class);
		if (discriminatorColumn != null) {
			parsed.add(DiscriminatorColumn.class);
		}

		final Inheritance inheritance = type.getAnnotation(Inheritance.class);
		if (inheritance != null) {
			parsed.add(Inheritance.class);
		}

		if ((discriminatorColumn != null) || (inheritance != null)) {
			this.inheritance = new EntityInheritence(discriminatorColumn, inheritance);
		}

		final DiscriminatorValue discriminatorValue = type.getAnnotation(DiscriminatorValue.class);
		if (discriminatorValue != null) {
			if (this.getTopType().inheritance == null) {
				throw new MappingException("DiscriminatorValue is not allowed for " + this.javaType + " as it is not part of inheritence");
			}

			final String value = discriminatorValue.value();
			switch (this.getTopType().inheritance.getType()) {
				case CHAR:
					if (value.length() != 1) {
						throw new MappingException("DiscriminatorValue should be exactly 1 character long for " + this.javaType
							+ ". Value specified is " + value);
					}
					this.discriminatorValue = new Character(value.charAt(0));
					break;
				case INTEGER:
					try {
						final Integer intValue = Integer.valueOf(value);
						this.discriminatorValue = intValue;
					}
					catch (final NumberFormatException e) {
						throw new MappingException("DiscriminatorValue should be an integer for " + this.javaType + ". Value specified is "
							+ value);
					}
					break;
				default:
					this.discriminatorValue = value;
					if (value.isEmpty()) {
						throw new MappingException("DiscriminatorValue cannot be empty for " + this.javaType);
					}
			}

			parsed.add(DiscriminatorValue.class);
		}
	}

	private void parsePrimaryTable(final Set<Class<? extends Annotation>> annotations) throws MappingException {
		final boolean singleTableInheritence = (this.getTopType() != this)
			&& (this.getTopType().inheritance.getInheritenceType() == InheritanceType.SINGLE_TABLE);

		final Table table = this.getJavaType().getAnnotation(Table.class);
		if (table != null) {
			if (singleTableInheritence) {
				throw new MappingException("On " + this.javaType
					+ ", Table definition is not allowed, as root type sets the inheritence to SINGLE_TABLE");
			}

			this.putTable(true, table.schema(), table.name(), table.uniqueConstraints(), null);

			annotations.add(Table.class);
		}
		else if (!singleTableInheritence) {
			this.putTable(true, "", this.name, null, null);
		}
	}

	private void parseSecondaryTables(final Set<Class<? extends Annotation>> annotations) throws MappingException {
		final SecondaryTables secondaryTables = this.getJavaType().getAnnotation(SecondaryTables.class);
		if (secondaryTables != null) {
			for (final SecondaryTable secondaryTable : secondaryTables.value()) {
				this.putTable(false, secondaryTable.schema(), secondaryTable.name(), secondaryTable.uniqueConstraints(),
					secondaryTable.pkJoinColumns());
			}

			annotations.add(SecondaryTables.class);
		}

		final SecondaryTable secondaryTable = this.getJavaType().getAnnotation(SecondaryTable.class);
		if (secondaryTable != null) {
			this.putTable(false, secondaryTable.schema(), secondaryTable.name(), secondaryTable.uniqueConstraints(),
				secondaryTable.pkJoinColumns());

			annotations.add(SecondaryTable.class);
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
	 * Performs refresh from each table for the managed instance.
	 * 
	 * @param session
	 *            the session to use
	 * @param managedInstance
	 *            the managed instance to perform refresh for
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performRefresh(SessionImpl session, ManagedInstance<X> managedInstance) throws SQLException {
		this.refreshHelper.refresh(session, managedInstance);
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
	 * Adds the table to the list of the tables this entity has. may not be blank
	 * 
	 * @param primary
	 *            if the table is primary
	 * @param schema
	 *            the name of the schema, may be null
	 * @param name
	 *            the name of the table
	 * @param uniqueConstraints
	 *            the unique constraints
	 * @param primaryKeyJoinColumns
	 *            the primary key join columns, only required for secondary tables.
	 * @throws MappingException
	 *             thrown in case of a mapping error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void putTable(boolean primary, String schema, String name, UniqueConstraint[] uniqueConstraints,
		PrimaryKeyJoinColumn[] primaryKeyJoinColumns) throws MappingException {
		if (StringUtils.isBlank(name)) {
			throw new MappingException("Table name cannot be null, spesified on " + this.javaType);
		}

		TableTemplate table;
		if (primary) {
			table = new TableTemplate(this, schema, name, uniqueConstraints);
		}
		else {
			table = new TableTemplate(this, schema, name, uniqueConstraints, primaryKeyJoinColumns);
		}

		this.tableTemplates.put(schema + "." + name, table);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "EntityTypeImpl [name=" + this.name + ", IdAttributes=" + this.idAttributes + ", attributes=" + this.attributes.values()
			+ "]";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void vlink() throws BatooException {
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
		for (final TableTemplate template : this.tableTemplates.values()) {
			final EntityTable table = new EntityTable(this, template, this.metaModel.getJdbcAdapter());
			if (template.isPrimary()) {
				this.primaryTable = table;
			}

			this.tables.put(table.getName(), table);
		}

		// Then add in the tables defined by the super types
		final EntityTypeImpl<? super X> superType = this.getTopType();
		if (superType != this) {
			switch (superType.getTopType().getInheritance().getInheritenceType()) {
				case SINGLE_TABLE:
					this.primaryTable = superType.getPrimaryTable();
					break;

				case TABLE_PER_CLASS:
					throw new MappingException("TABLE_PER_CLASS inheritence not yet supported");
			}
		}

		if (this.inheritance != null) {
			// create the discriminator column
			new PhysicalColumn(this.primaryTable, this.inheritance);
		}
	}
}
