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
package org.batoo.jpa.core.impl.model.type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.InheritanceType;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.PredicateImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.Enhancer;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.DiscriminatorColumn;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.SecondaryTable;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.EntityTransactionImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AssociatedSingularAttribute;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.IdAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.PhysicalAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.VersionAttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.IdMapping;
import org.batoo.jpa.core.impl.model.mapping.PhysicalMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.VersionMapping;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.SecondaryTableMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link EntityType}.
 * 
 * @param <X>
 *            The represented entity type
 * 
 * @author hceylan
 * @since $version
 */
public class EntityTypeImpl<X> extends IdentifiableTypeImpl<X> implements EntityType<X> {

	private static final int MAX_DEPTH = 5;

	private final EntityMetadata metadata;
	private final String name;
	private EntityTable primaryTable;
	private final Map<String, EntityTable> tableMap = Maps.newHashMap();

	@SuppressWarnings("restriction")
	private sun.reflect.ConstructorAccessor enhancer;

	private EntityTable[] tables;
	private CriteriaQueryImpl<X> selectCriteria;

	private int dependencyCount;
	private final HashMap<EntityTypeImpl<?>, AssociationMapping<? super X, ?, ?>[]> dependencyMap = Maps.newHashMap();

	private AssociationMapping<? super X, ?, ?>[] associations;
	private AssociationMapping<? super X, ?, ?>[] associationsPersistable;
	private AssociationMapping<? super X, ?, ?>[] associationsEager;
	private AssociationMapping<? super X, ?, ?>[] associationsJoined;

	private final Map<String, AbstractMapping<? super X, ?>> mappings = Maps.newHashMap();
	private IdMapping<? super X, ?>[] idMappings;

	private final InheritanceType inheritanceType;
	private final Map<String, EntityTypeImpl<? extends X>> children = Maps.newHashMap();
	private final String discriminatorValue;
	private DiscriminatorColumn discriminatorColumn;
	private EntityTypeImpl<? super X> rootType;

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param parent
	 *            the parent type
	 * @param javaType
	 *            the java type of the managed type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeImpl(MetamodelImpl metamodel, IdentifiableTypeImpl<? super X> parent, Class<X> javaType, EntityMetadata metadata) {
		super(metamodel, parent, javaType, metadata);

		this.name = metadata.getName();
		this.metadata = metadata;
		this.inheritanceType = metadata.getInheritanceType();
		this.discriminatorValue = StringUtils.isNotBlank(metadata.getDiscriminatorValue()) ? metadata.getDiscriminatorValue() : this.name;

		this.addAttributes(metadata);
		this.initTables(metadata);

		this.linkMappings();
	}

	@SuppressWarnings("restriction")
	private void enhanceIfNeccessary() {
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
						throw new RuntimeException("Cannot enhance class: " + this.getJavaType(), e);
					}
				}
			}
		}
	}

	/**
	 * @param attribute
	 *            the attribute
	 * @param path
	 *            the path of the attribute
	 * @return the column metadata or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationMetadata getAssociationOverride(AttributeImpl<? super X, ?> attribute, String path) {
		for (final AssociationMetadata override : this.metadata.getAssociationOverrides()) {
			if (override.getName().equals(path)) {
				return override;
			}
		}

		return (AssociationMetadata) attribute.getMetadata();
	}

	/**
	 * Returns the associatedAttributes of the type.
	 * 
	 * @return the associatedAttributes of the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AssociationMapping<? super X, ?, ?>[] getAssociations() {
		if (this.associations != null) {
			return this.associations;
		}

		synchronized (this) {
			if (this.associations != null) {
				return this.associations;
			}

			final List<AssociationMapping<? super X, ?, ?>> associations = Lists.newArrayList();

			for (final AbstractMapping<? super X, ?> attribute : this.mappings.values()) {
				if (attribute instanceof AssociationMapping) {
					associations.add((AssociationMapping<? super X, ?, ?>) attribute);
				}
			}

			final AssociationMapping<? super X, ?, ?>[] associatedAttributes0 = new AssociationMapping[associations.size()];
			associations.toArray(associatedAttributes0);

			this.associations = associatedAttributes0;
		}

		return this.associations;
	}

	/**
	 * Returns the associated attributes that are eager.
	 * 
	 * @return the associated attributes that are eager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AssociationMapping<? super X, ?, ?>[] getAssociationsEager() {
		if (this.associationsEager != null) {
			return this.associationsEager;
		}

		synchronized (this) {
			if (this.associationsEager != null) {
				return this.associationsEager;
			}

			final List<AssociationMapping<? super X, ?, ?>> eagerAssociations = Lists.newArrayList();

			for (final AssociationMapping<? super X, ?, ?> association : this.getAssociations()) {
				if (association.isEager()) {
					eagerAssociations.add(association);
				}
			}

			final AssociationMapping<? super X, ?, ?>[] eagerAssociations0 = new AssociationMapping[eagerAssociations.size()];
			eagerAssociations.toArray(eagerAssociations0);

			this.associationsEager = eagerAssociations0;
		}

		return this.associationsEager;
	}

	/**
	 * Returns the associated attributes that are joined.
	 * 
	 * @return the associated attributes that are joined
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AssociationMapping<? super X, ?, ?>[] getAssociationsJoined() {
		if (this.associationsJoined != null) {
			return this.associationsJoined;
		}

		synchronized (this) {
			if (this.associationsJoined != null) {
				return this.associationsJoined;
			}

			final List<AssociationMapping<? super X, ?, ?>> joinedAssociations = Lists.newArrayList();

			for (final AssociationMapping<? super X, ?, ?> association : this.getAssociations()) {
				if (association.getJoinTable() != null) {
					joinedAssociations.add(association);
				}
			}

			final AssociationMapping<? super X, ?, ?>[] joinedAssociations0 = new AssociationMapping[joinedAssociations.size()];
			joinedAssociations.toArray(joinedAssociations0);

			this.associationsJoined = joinedAssociations0;
		}

		return this.associationsJoined;
	}

	/**
	 * Returns the associated attributes that are persistable by the type.
	 * 
	 * @return the associated attributes that are persistable by the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AssociationMapping<? super X, ?, ?>[] getAssociationsPersistable() {
		if (this.associationsPersistable != null) {
			return this.associationsPersistable;
		}

		synchronized (this) {
			if (this.associationsPersistable != null) {
				return this.associationsPersistable;
			}

			final List<AssociationMapping<? super X, ?, ?>> persistableAssociations = Lists.newArrayList();

			for (final AssociationMapping<? super X, ?, ?> association : this.getAssociations()) {
				if (association.cascadesPersist()) {
					persistableAssociations.add(association);
				}
			}

			final AssociationMapping<? super X, ?, ?>[] persistableAssociations0 = new AssociationMapping[persistableAssociations.size()];
			persistableAssociations.toArray(persistableAssociations0);

			this.associationsPersistable = persistableAssociations0;
		}

		return this.associationsPersistable;
	}

	/**
	 * @param attribute
	 *            the attribute
	 * @param path
	 *            the path of the attribute
	 * @return the column metadata or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ColumnMetadata getAttributeOverride(PhysicalAttributeImpl<? super X, ?> attribute, String path) {
		for (final AttributeOverrideMetadata override : this.metadata.getAttributeOverrides()) {
			if (override.getName().equals(path)) {
				return override.getColumn();
			}
		}

		return attribute.getMetadata().getColumn();
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
	 * Returns the child based on the <code>discriminatorValue</code> value.
	 * 
	 * @param discriminatorValue
	 *            the discriminator value of the child
	 * @return the child type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeImpl<? extends X> getChildType(Object discriminatorValue) {
		return this.children.get(discriminatorValue);
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
	public AssociationMapping<? super X, ?, ?>[] getDependenciesFor(EntityTypeImpl<?> associate) {
		return this.dependencyMap.get(associate);
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
	 * Returns the discriminator column of the entity.
	 * 
	 * @return the discriminator column of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public DiscriminatorColumn getDiscriminatorColumn() {
		return this.discriminatorColumn;
	}

	/**
	 * Returns the set of discriminator values in the range of this entity's hierarchy.
	 * 
	 * @return the set of discriminator values
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Set<String> getDiscriminators() {
		return this.children.keySet();
	}

	/**
	 * Returns the discriminatorValue of the EntityTypeImpl.
	 * 
	 * @return the discriminatorValue of the EntityTypeImpl
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getDiscriminatorValue() {
		return this.discriminatorValue;
	}

	/**
	 * Returns an array of id attributes.
	 * 
	 * @return an array of id attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public IdMapping<? super X, ?>[] getIdMappings() {
		if (this.idMappings != null) {
			return this.idMappings;
		}

		// populate the id attributes with the inheritance
		synchronized (this) {
			if (this.idMappings != null) {
				return this.idMappings;
			}

			final List<IdMapping<? super X, ?>> idMappings = Lists.newArrayList();
			for (final AbstractMapping<? super X, ?> mapping : this.mappings.values()) {
				if (mapping instanceof IdMapping) {
					idMappings.add((IdMapping<? super X, ?>) mapping);
				}
			}

			final IdMapping<? super X, ?>[] idMappings0 = new IdMapping[idMappings.size()];
			idMappings.toArray(idMappings0);

			this.idMappings = idMappings0;
		}

		return this.idMappings;
	}

	/**
	 * Returns the inheritance type of the entity.
	 * 
	 * @return the inheritance type of the entity or <code>null</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public InheritanceType getInheritanceType() {
		return this.inheritanceType;
	}

	/**
	 * Returns the managed instance for the instance.
	 * 
	 * @param instance
	 *            the instance to create managed instance for
	 * @param session
	 *            the session
	 * @return managed id for the instance
	 * @throws NullPointerException
	 *             thrown if the instance is null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public ManagedInstance<X> getManagedInstance(SessionImpl session, Object instance) {
		if (instance == null) {
			throw new NullPointerException();
		}

		return new ManagedInstance<X>(this, session, (X) instance);
	}

	/**
	 * Creates a new managed instance with the id.
	 * 
	 * @param session
	 *            the session
	 * @param id
	 *            the primary key
	 * @return the managed instance created
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedInstance<X> getManagedInstanceById(SessionImpl session, Object id) {
		return this.getManagedInstanceById(session, id, false);
	}

	/**
	 * Creates a new managed instance with the id.
	 * 
	 * @param session
	 *            the session
	 * @param id
	 *            the primary key
	 * @param lazy
	 *            if the instance is lazy
	 * @return the managed instance created
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked", "restriction" })
	public ManagedInstance<X> getManagedInstanceById(SessionImpl session, Object id, boolean lazy) {
		this.enhanceIfNeccessary();

		X instance = null;
		try {
			instance = (X) this.enhancer.newInstance(new Object[] { this.getJavaType(), session, id, !lazy });
		}
		catch (final Exception e) {
			e.printStackTrace();
		} // not possible

		final ManagedInstance<X> managedInstance = new ManagedInstance<X>(this, session, instance, id);

		((EnhancedInstance) instance).__enhanced__$$__setManagedInstance(managedInstance);

		return managedInstance;
	}

	/**
	 * Returns the mapping with the <code>name</code>.
	 * 
	 * @param name
	 *            the qualified name of the mapping
	 * @return the mapping with the <code>name</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractMapping<? super X, ?> getMapping(String name) {
		return this.mappings.get(name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
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
	 * Returns the primary table of the type
	 * 
	 * @return the primary table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTable getPrimaryTable() {
		return this.primaryTable;
	}

	/**
	 * Returns the root type of the hierarchy.
	 * 
	 * @return the root type of the hierarchy
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeImpl<? super X> getRootType() {
		if (this.rootType != null) {
			return this.rootType;
		}

		EntityTypeImpl<? super X> supertype = this;

		while (supertype.getSupertype() instanceof EntityTypeImpl) {
			supertype = (EntityTypeImpl<? super X>) supertype.getSupertype();
		}

		this.rootType = supertype;

		return this.rootType;
	}

	/**
	 * Returns the table with the name.
	 * <p>
	 * If the <code>tableName</code> is blank then the primary table is returned
	 * 
	 * @param tableName
	 *            the name of the table, may be blank
	 * @return the table or null if not found
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractTable getTable(String tableName) {
		if (StringUtils.isBlank(tableName)) {
			return this.primaryTable;
		}

		return this.tableMap.get(tableName);
	}

	/**
	 * Returns the tables of the type, starting from the top of the hierarchy.
	 * 
	 * @return the tables of the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTable[] getTables() {
		if (this.tables != null) {
			return this.tables;

		}

		synchronized (this) {
			if (this.tables != null) {
				return this.tables;
			}

			final EntityTable[] tables = new EntityTable[this.tableMap.size()];
			this.tableMap.values().toArray(tables);

			return this.tables = tables;
		}
	}

	/**
	 * Initializes the tables.
	 * 
	 * @since $version
	 * @author hceylan
	 * @param metadata
	 */
	private void initTables(EntityMetadata metadata) {
		if ((this.getRootType().inheritanceType != InheritanceType.SINGLE_TABLE) || (this.getRootType() == this)) {
			this.primaryTable = new EntityTable(this, metadata.getTable());

			this.tableMap.put(this.primaryTable.getName(), this.primaryTable);

			if (this.getRootType() != this) {
				this.tableMap.putAll(this.getRootType().tableMap);
			}

			for (final SecondaryTableMetadata secondaryTableMetadata : metadata.getSecondaryTables()) {
				this.tableMap.put(secondaryTableMetadata.getName(), new SecondaryTable(this, secondaryTableMetadata));
			}
		}
		else {
			this.primaryTable = this.getRootType().primaryTable;
			this.tableMap.putAll(this.getRootType().tableMap);
		}
	}

	/**
	 * Returns if the method is an id method.
	 * 
	 * @param method
	 *            the method
	 * @return if the method is an id method
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isIdMethod(Method method) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Links the entity's attribute mappings.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void linkMappings() {
		if ((this.getRootType() != this) && (this.getRootType().getInheritanceType() != null)) {
			// inherit mappings from the parent
			this.mappings.putAll(((EntityTypeImpl<? super X>) this.getSupertype()).mappings);

			// register the discriminator value
			IdentifiableTypeImpl<? super X> parent = this;
			do {
				((EntityTypeImpl<? super X>) parent).children.put(this.discriminatorValue, this);
				parent = parent.getSupertype();
			}
			while (parent instanceof EntityTypeImpl);
		}

		// if the root type then create the discriminator column
		if ((this.getRootType() == this) && (this.inheritanceType != null)) {
			this.discriminatorColumn = new DiscriminatorColumn(this, this.metadata.getDiscriminatorColumn());
		}

		for (final Attribute<? super X, ?> attribute : this.getAttributes()) {

			// should we process the attribute
			final boolean process = (attribute.getDeclaringType() == this) //
				|| (attribute.getDeclaringType() instanceof MappedSuperclassTypeImpl);

			if (!process) {
				continue;
			}

			switch (attribute.getPersistentAttributeType()) {
				case BASIC:
					final PhysicalAttributeImpl<? super X, ?> physicalAttribute = (PhysicalAttributeImpl<? super X, ?>) attribute;
					PhysicalMapping mapping;

					if (physicalAttribute.isVersion()) {
						mapping = new VersionMapping(this, (VersionAttributeImpl<? super X, ?>) attribute);
						this.mappings.put(attribute.getName(), mapping);
					}
					else if (physicalAttribute.isId()) {
						mapping = new IdMapping(this, (IdAttributeImpl<? super X, ?>) attribute);
						this.mappings.put(attribute.getName(), mapping);
					}
					else {
						mapping = new BasicMapping(this, (BasicAttributeImpl<? super X, ?>) attribute);
						this.mappings.put(attribute.getName(), mapping);
					}

					final BasicColumn basicColumn = ((PhysicalMapping<? super X, ?>) mapping).getColumn();
					final String tableName = basicColumn.getTableName();

					// if table name is blank, it means the column should belong to the primary table
					if (StringUtils.isBlank(tableName)) {
						basicColumn.setTable(this.getPrimaryTable());
					}
					// otherwise locate the table
					else {
						final AbstractTable table = this.tableMap.get(tableName);
						if (table == null) {
							throw new MappingException("Table " + tableName + " could not be found", basicColumn.getLocator());
						}

						basicColumn.setTable(table);
					}

					break;
				case ONE_TO_ONE:
				case MANY_TO_ONE:
					this.mappings.put(attribute.getName(), new SingularAssociationMapping(this, (AssociatedSingularAttribute) attribute));
					break;
				case MANY_TO_MANY:
				case ONE_TO_MANY:
					this.mappings.put(attribute.getName(), new PluralAssociationMapping(this, (PluralAttributeImpl) attribute));
					break;
				default:
					break;
			}
		}
	}

	/**
	 * Performs inserts to each table for the managed instance.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param transaction
	 *            the transaction for which the insert will be performed
	 * @param managedInstance
	 *            the managed instance to perform insert for
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performInsert(ConnectionImpl connection, EntityTransactionImpl transaction, ManagedInstance<X> managedInstance)
		throws SQLException {
		for (final EntityTable table : this.getTables()) {
			table.performInsert(connection, managedInstance);
		}

		for (final PluralAttributeImpl<? super X, ?, ?> attribute : this.getPluralAttributes0().values()) {
			attribute.set(managedInstance, attribute.get(managedInstance.getInstance()));
		}

		managedInstance.setTransaction(transaction);
	}

	/**
	 * @param connection
	 *            the connection to use
	 * @param transaction
	 *            the transaction for which the remove will be performed
	 * @param instance
	 *            the managed instance to perform remove for
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performRemove(ConnectionImpl connection, EntityTransactionImpl transaction, ManagedInstance<X> instance)
		throws SQLException {
	}

	/**
	 * Performs select to find the instance.
	 * 
	 * @param entityManager
	 *            the entity manager to use
	 * @param instance
	 *            the managed instance to perform insert for
	 * @return the instance found or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public X performSelect(EntityManagerImpl entityManager, ManagedInstance<X> instance) {
		if (this.selectCriteria == null) {
			this.prepareSelectCriteria(entityManager);
		}

		final TypedQueryImpl<X> q = entityManager.createQuery(this.selectCriteria);
		q.setParameter(1, instance.getId());

		return q.getSingleResult();
	}

	/**
	 * @param connection
	 *            the connection to use
	 * @param transaction
	 *            the transaction for which the update will be performed
	 * @param instance
	 *            the managed instance to perform update for
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performUpdate(ConnectionImpl connection, EntityTransactionImpl transaction, ManagedInstance<X> instance)
		throws SQLException {
	}

	/**
	 * Prepares the dependencies for the associate.
	 * 
	 * @param associate
	 *            the associate
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public void prepareDependenciesFor(EntityTypeImpl<?> associate) {
		// prepare the related associations
		final Set<AssociationMapping<? super X, ?, ?>> attributes = Sets.newHashSet();

		for (final AssociationMapping<? super X, ?, ?> association : this.getAssociations()) {

			// only owner associations impose priority
			if (!association.isOwner()) {
				continue;
			}

			// only relations kept in the row impose priority
			if ((association.getAttribute().getPersistentAttributeType() != PersistentAttributeType.ONE_TO_ONE) && //
				(association.getAttribute().getPersistentAttributeType() != PersistentAttributeType.MANY_TO_ONE)) {
				continue;
			}

			final Class<?> javaType = association.getAttribute().getJavaType();

			if (javaType.isAssignableFrom(associate.getBindableJavaType())) {
				attributes.add(association);
			}
		}

		final AssociationMapping<? super X, ?, ?>[] dependencies = new AssociationMapping[attributes.size()];
		attributes.toArray(dependencies);

		this.dependencyCount += dependencies.length;

		this.dependencyMap.put(associate, dependencies);
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 * @param entityTypeImpl
	 */
	private void prepareEagerAssociations(FetchParent<?, ?> r, int depth, AssociationMapping<?, ?, ?> parent) {
		if (depth < EntityTypeImpl.MAX_DEPTH) {

			for (final AssociationMapping<? super X, ?, ?> association : this.getAssociationsEager()) {

				// if we are coming from the inverse side and inverse side is not many-to-one then skip
				if ((parent != null) && //
					(association.getInverse() == parent) && //
					(parent.getAttribute().getPersistentAttributeType() != PersistentAttributeType.MANY_TO_ONE)) {
					continue;
				}

				final Fetch<?, Object> r2 = r.fetch(association.getAttribute().getName(), JoinType.LEFT);

				final EntityTypeImpl<?> type = association.getType();

				type.prepareEagerAssociations(r2, depth + 1, association);
			}
		}
	}

	private synchronized CriteriaQueryImpl<X> prepareSelectCriteria(EntityManagerImpl entityManager) {
		// other thread prepared before this one
		if (this.selectCriteria != null) {
			return this.selectCriteria;
		}

		final CriteriaBuilderImpl cb = entityManager.getCriteriaBuilder();
		CriteriaQueryImpl<X> q = cb.createQuery(this.getJavaType());
		final RootImpl<X> r = q.from(this);
		q = q.select(r);

		this.prepareEagerAssociations(r, 0, null);

		// add id fields to the restriction
		final List<PredicateImpl> predicates = Lists.newArrayList();
		for (final IdMapping<? super X, ?> idMapping : this.getRootType().getIdMappings()) {
			final ParameterExpressionImpl<?> pe = cb.parameter(idMapping.getAttribute().getJavaType());
			final Path<?> path = r.get(idMapping.getAttribute());
			final PredicateImpl predicate = cb.equal(path, pe);

			predicates.add(predicate);
		}

		return this.selectCriteria = q.where(predicates.toArray(new PredicateImpl[predicates.size()]));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "EntityTypeImpl [name=" + this.name + "]";
	}
}
