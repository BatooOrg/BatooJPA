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
package org.batoo.jpa.core.impl.metamodel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.batoo.jpa.core.impl.instance.Enhancer;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.jdbc.PhysicalColumn;
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
import org.batoo.jpa.core.impl.mapping.OwnerManyToOneMapping;
import org.batoo.jpa.core.impl.mapping.OwnerOneToManyMapping;
import org.batoo.jpa.core.impl.mapping.OwnerOneToOneMapping;
import org.batoo.jpa.core.impl.mapping.PersistableAssociation;
import org.batoo.jpa.core.impl.mapping.TableTemplate;
import org.batoo.jpa.core.impl.mapping.TypeFactory;
import org.batoo.jpa.core.impl.reflect.ReflectHelper;
import org.batoo.jpa.core.jdbc.IdType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Abstract implementation of {@link EntityType}.
 * 
 * @author hceylan
 * @since $version
 */
@SuppressWarnings("restriction")
abstract class AbstractEntityTypeImpl<X> extends IdentifiableTypeImpl<X> implements EntityType<X> {

	private static final BLogger LOG = BLogger.getLogger(AbstractEntityTypeImpl.class);

	private final Map<String, AbstractMapping<?, ?>> mappings = Maps.newHashMap();
	private final List<Association<?, ?>> associationList = Lists.newArrayList();
	private final List<BasicMapping<?, ?>> idMappings = Lists.newArrayList(); // FIXME Performance: Convert to array
	private BasicMapping<?, ?> identityMapping;
	private final Map<String, Column> attributeOverrides = Maps.newHashMap();

	protected EntityTable primaryTable;
	protected final Map<String, EntityTable> tables = Maps.newHashMap();
	protected final Map<String, JoinTable> joinTables = Maps.newHashMap();
	private PhysicalColumn[] basicColumns;
	protected final Map<String, TableTemplate> tableTemplates = Maps.newHashMap();

	private final IdentityHashMap<Method, Method> idMethods = Maps.newIdentityHashMap();
	private final IdentityHashMap<Method, Method> nonIdMethods = Maps.newIdentityHashMap();

	protected sun.reflect.ConstructorAccessor enhancer;

	private int dependencyCount;
	private final HashMap<EntityTypeImpl<?>, AttributeImpl<?, ?>[]> dependencyMap = Maps.newHashMap();

	private Association<?, ?>[] associations;
	private Association<?, ?>[] associationsPersistable;
	private Association<?, ?>[] associationsDetachable;
	private PersistableAssociation<?, ?>[] ownerCollections;

	/**
	 * @param metaModel
	 *            the meta model of the persistence
	 * @param supertype
	 *            the mapped super class that this type is extending
	 * @param javaType
	 *            the javatype of the entity
	 * @param name
	 *            name of the entity
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractEntityTypeImpl(MetamodelImpl metaModel, IdentifiableTypeImpl<? super X> supertype, final Class<X> javaType)
		throws MappingException {
		super(metaModel, supertype, javaType);

		metaModel.addEntity(this);
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
		mapping.getDeclaringAttribute().getName();

		if (mapping instanceof BasicMapping) {
			final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) mapping;
			final SingularAttributeImpl<?, ?> singularAttribute = basicMapping.getDeclaringAttribute();

			// Separate out the identity attributes
			if (id || singularAttribute.isId()) {
				this.idMappings.add(basicMapping);
			}

			// Separate out the identity id attributes
			if ((singularAttribute.getIdType() == IdType.IDENTITY)) {
				if (this.identityMapping != null) {
					throw new MappingException("Multiple identity properties on " + this.javaType);
				}
				this.identityMapping = basicMapping;
			}

			for (final ColumnTemplate<?, ?> template : basicMapping.getColumnTemplates()) {
				this.addColumn(template, basicMapping);
			}
		}

		if (mapping instanceof Association) {
			final Association<?, ?> association = (Association<?, ?>) mapping;

			this.metaModel.addAssociation(association);
			this.associationList.add(association);

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

		this.mappings.put(mapping.getPathAsString(), mapping);
	}

	protected void enhanceIfNeccessary() {
		if (this.enhancer == null) {
			synchronized (this) {
				if (this.enhancer == null) {
					try {
						final Class<X> enhancedClass = Enhancer.enhance(this);
						final Constructor<X> enhancedConstructor = enhancedClass.getConstructor(Class.class, SessionImpl.class,
							Object.class, Boolean.TYPE);

						final Class<?> magClass = Class.forName("sun.reflect.MethodAccessorGenerator");
						final Constructor<?> c = magClass.getDeclaredConstructors()[0];
						final Method generateMethod = magClass.getMethod("generateConstructor", Class.class, Class[].class, Class[].class,
							Integer.TYPE);

						ReflectHelper.setAccessible(c, true);
						ReflectHelper.setAccessible(generateMethod, true);

						try {
							final Object mag = c.newInstance();
							this.enhancer = (sun.reflect.ConstructorAccessor) generateMethod.invoke(mag,
								enhancedConstructor.getDeclaringClass(), enhancedConstructor.getParameterTypes(),
								enhancedConstructor.getExceptionTypes(), enhancedConstructor.getModifiers());
						}
						finally {
							ReflectHelper.setAccessible(c, false);
							ReflectHelper.setAccessible(generateMethod, false);
						}
					}
					catch (final Exception e) {
						throw new RuntimeException("Cannot enhance class: " + this.javaType, e);
					}
				}
			}
		}
	}

	/**
	 * Returns the collection of associations.
	 * 
	 * @return the collection of associations
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Association<?, ?>[] getAssociations() {
		if (this.associations != null) {
			return this.associations;
		}

		synchronized (this) {
			if (this.associations != null) {
				return this.associations;
			}

			this.associations = new Association[this.associationList.size()];
			this.associationList.toArray(this.associations);
		}

		return this.associations;
	}

	/**
	 * Returns the array of associations where persists are cascaded.
	 * 
	 * @return the array of associations where persists are cascaded
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Association<?, ?>[] getAssociationsDetachable() {
		if (this.associationsDetachable != null) {
			return this.associationsDetachable;
		}

		synchronized (this) {
			if (this.associationsDetachable != null) {
				return this.associationsDetachable;
			}

			final List<Association<?, ?>> associationsDetachable = Lists.newArrayList();
			for (final Association<?, ?> association : this.getAssociations()) {
				if (association.cascadeDetach()) {
					associationsDetachable.add(association);
				}
			}

			this.associationsDetachable = new Association[associationsDetachable.size()];
			this.associationsDetachable = associationsDetachable.toArray(this.associationsDetachable);
		}

		return this.associationsDetachable;
	}

	/**
	 * Returns the array of associations where persists are cascaded.
	 * 
	 * @return the array of associations where persists are cascaded
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Association<?, ?>[] getAssociationsPersistable() {
		if (this.associationsPersistable != null) {
			return this.associationsPersistable;
		}

		synchronized (this) {
			if (this.associationsPersistable != null) {
				return this.associationsPersistable;
			}

			final List<Association<?, ?>> associationsPersistable = Lists.newArrayList();
			for (final Association<?, ?> association : this.getAssociations()) {
				if (association.cascadePersist()) {
					associationsPersistable.add(association);
				}
			}

			this.associationsPersistable = new Association[associationsPersistable.size()];
			this.associationsPersistable = associationsPersistable.toArray(this.associationsPersistable);
		}

		return this.associationsPersistable;
	}

	/**
	 * Returns list of physical columns for basic attributes.
	 * 
	 * @return list of physical columns for basic attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PhysicalColumn[] getBasicColumns() {
		if (this.basicColumns != null) {
			return this.basicColumns;
		}

		final ConcurrentMap<String, PhysicalColumn> basicColumns = Maps.newConcurrentMap();

		// add all the tables in the hierarchy
		AbstractEntityTypeImpl<? super X> currentType = this;
		while (currentType != null) {
			for (final AbstractMapping<?, ?> mapping : currentType.mappings.values()) {
				if (mapping instanceof BasicMapping) {
					final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) mapping;

					// skip id attributes
					if (!basicMapping.getDeclaringAttribute().isId()) {
						// basic mappings always have single column
						final PhysicalColumn column = basicMapping.getPhysicalColumns()[0];
						basicColumns.putIfAbsent(mapping.getPathAsString(), column);
					}
				}
			}

			if (currentType.getSupertype() instanceof AbstractEntityTypeImpl) {
				currentType = (AbstractEntityTypeImpl<? super X>) currentType.getSupertype();
			}
			else {
				break;
			}
		}

		this.basicColumns = basicColumns.values().toArray(new PhysicalColumn[basicColumns.size()]);

		return this.basicColumns;
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
	 * Returns the dependencies for the associate type
	 * 
	 * @param associate
	 *            the associate type
	 * @return the array of associations for the associate
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AttributeImpl<?, ?>[] getDependenciesFor(EntityTypeImpl<?> associate) {
		final AttributeImpl<?, ?>[] dependencies = this.dependencyMap.get(associate);

		if (dependencies != null) {
			return dependencies;
		}

		return this.getDependenciesFor0(associate);
	}

	private AttributeImpl<?, ?>[] getDependenciesFor0(EntityTypeImpl<?> associate) {
		AttributeImpl<?, ?>[] dependencies;
		synchronized (this) {
			dependencies = this.dependencyMap.get(associate);

			if (dependencies != null) { // other thread got it done!
				return dependencies;
			}

			// prepare the related associations
			final HashSet<AttributeImpl<?, ?>> attributes = Sets.newHashSet();

			final Association<?, ?>[] associations = this.getAssociations();
			for (final Association<?, ?> association : associations) {

				// determine the java type
				final AttributeImpl<?, ?> attribute = association.getDeclaringAttribute();

				// only owner associations impose priority
				if (!association.isOwner()) {
					continue;
				}

				// only relations kept in the row impose priority
				if (!(association instanceof OwnerManyToOneMapping) //
					&& !(association instanceof OwnerOneToOneMapping)) {
					continue;
				}

				final Class<?> javaType = attribute.getJavaType();

				if (javaType.isAssignableFrom(associate.getBindableJavaType())) {
					attributes.add(attribute);
				}
			}

			dependencies = new AttributeImpl[attributes.size()];
			attributes.toArray(dependencies);

			this.dependencyCount += dependencies.length;

			this.dependencyMap.put(associate, dependencies);

			return dependencies;
		}
	}

	/**
	 * Returns the dependencyCount.
	 * 
	 * @return the dependencyCount
	 * @since $version
	 */
	public int getDependencyCount() {
		return this.dependencyCount;
	}

	/**
	 * Returns the id mappings of the entity.
	 * 
	 * @return the id mappings of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<BasicMapping<?, ?>> getIdMappings() {
		return this.idMappings;
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
	 * Returns the array of associations that are owner and persistable.
	 * 
	 * @return the array of associations that are owner and persistable.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PersistableAssociation<?, ?>[] getOwnerCollections() {
		if (this.ownerCollections != null) {
			return this.ownerCollections;
		}

		synchronized (this) {
			if (this.ownerCollections != null) {
				return this.ownerCollections;
			}

			final List<Association<?, ?>> associationsOwnedPersistable = Lists.newArrayList();
			for (final Association<?, ?> association : this.getAssociations()) {
				if (association instanceof PersistableAssociation) {
					associationsOwnedPersistable.add(association);
				}
			}

			this.ownerCollections = new PersistableAssociation[associationsOwnedPersistable.size()];
			this.ownerCollections = associationsOwnedPersistable.toArray(this.ownerCollections);
		}

		return this.ownerCollections;
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

	public abstract EntityTypeImpl<? super X> getRoot();

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
	 * Returns if the entity has any identity attribute.
	 * 
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean hasIdentityAttribute() {
		return this.identityMapping != null;
	}

	/**
	 * @param method
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isIdMethod(Method method) {
		if (this.idMethods.containsKey(method)) { // if known id method, let go
			return true;
		}

		if (this.nonIdMethods.containsKey(method)) {
			return false;
		}

		final String methodName = method.getName();
		if (methodName.startsWith("get") && (methodName.length() > 3)) { // check if id method
			for (final SingularAttributeImpl<?, ?> attribute : this.idAttributes) {
				if (attribute.getGetterName().equals(method.getName())) {
					this.idMethods.put(method, method);
					return true;
				}
			}
		}

		this.nonIdMethods.put(method, method);

		return false;
	}

	/**
	 * Horizontally links the entity.
	 * 
	 * @param dataSource
	 *            the datasource to use
	 * @param firstPass
	 *            true if first pass, false for second pass
	 * @throws BatooException
	 *             thrown in case of a mapping error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void link(DataSource dataSource, boolean first) throws BatooException {
		LOG.debug("Horizontally linking {0}, {1} pass", this, first ? "first" : "second");

		for (final Attribute<? super X, ?> attribute : this.attributes.values()) {
			final boolean association = attribute.getPersistentAttributeType() != PersistentAttributeType.BASIC;

			if (association ^ first) {
				((AttributeImpl<? super X, ?>) attribute).link(new LinkedList<AttributeImpl<?, ?>>(), this.attributeOverrides);
			}
		}

		if (!first) {
			// pre-prepare the dependencies
			for (final EntityType<?> type : this.metaModel.getEntities()) {
				this.getDependenciesFor((EntityTypeImpl<?>) type);
			}

			if (this.getRoot() == null) {
				if (this.idJavaType == null) {
					if (!this.hasSingleIdAttribute()) {
						throw new MappingException("IdClass need to specified for type " + this.javaType
							+ " as it has composite primary key");
					}

					final SingularAttributeImpl<?, ?> attribute = this.idAttributes[0];
					this.idType = this.metaModel.getType(attribute.getJavaType());
				}

			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void parse(Set<Class<? extends Annotation>> parsed) throws BatooException {
		final Class<X> type = this.getJavaType();

		this.performClassChecks(type);

		this.parsePrimaryTable(parsed);
		this.parseSecondaryTables(parsed);

		super.parse(parsed);

		final Entity entity = type.getAnnotation(Entity.class);
		if (entity == null) {
			throw new MappingException("Type is not an entity " + type);
		}
		parsed.add(Entity.class);

		this.parseAttributeOverrides(type, parsed);
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

	private void parsePrimaryTable(final Set<Class<? extends Annotation>> annotations) throws MappingException {
		final boolean singleTableInheritence = (this.getRoot() != this)
			&& (this.getRoot().inheritance.getInheritenceType() == InheritanceType.SINGLE_TABLE);

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
			name = this.name;
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
}
