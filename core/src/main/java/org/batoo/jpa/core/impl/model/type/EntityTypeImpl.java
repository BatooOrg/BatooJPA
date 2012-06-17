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
import java.util.Arrays;
import java.util.Comparator;
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
import javax.persistence.metamodel.SingularAttribute;

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
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.attribute.EmbeddedAttribute;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.util.Pair;
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
@SuppressWarnings("restriction")
public class EntityTypeImpl<X> extends IdentifiableTypeImpl<X> implements EntityType<X> {

	private static final int MAX_DEPTH = 5;

	private final EntityMetadata metadata;
	private final String name;
	private EntityTable primaryTable;
	private final Map<String, EntityTable> tableMap = Maps.newHashMap();
	private EntityTable[] tables;
	private EntityTable[] allTables;

	private sun.reflect.ConstructorAccessor constructor;

	private CriteriaQueryImpl<X> selectCriteria;

	private int dependencyCount;
	private final HashMap<EntityTypeImpl<?>, AssociationMapping<? super X, ?, ?>[]> dependencyMap = Maps.newHashMap();

	private AssociationMapping<?, ?, ?>[] associations;
	private AssociationMapping<?, ?, ?>[] associationsPersistable;
	private AssociationMapping<?, ?, ?>[] associationsEager;
	private AssociationMapping<?, ?, ?>[] associationsJoined;
	private PluralAssociationMapping<?, ?, ?>[] associationsPlural;
	private SingularAssociationMapping<?, ?>[] associationsSingularLazy;

	private final Map<Method, Method> idMethods = Maps.newHashMap();

	private final Map<String, AbstractMapping<? super X, ?>> mappings = Maps.newHashMap();
	private SingularMapping<? super X, ?> idMapping;
	private Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>>[] idMappings;

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

	/**
	 * Creates and returns a base query that selects the entity with its eager associations.
	 * 
	 * @return the created base query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CriteriaQueryImpl<X> createBaseQuery() {
		final CriteriaBuilderImpl cb = this.getMetamodel().getEntityManagerFactory().getCriteriaBuilder();
		CriteriaQueryImpl<X> q = cb.createQuery(this.getJavaType());
		final RootImpl<X> r = q.from(this);
		q = q.select(r);

		this.prepareEagerAssociations(r, 0, null);

		return q;
	}

	/**
	 * Returns if this entity extends the parent entity.
	 * 
	 * @param parent
	 *            the parent to test
	 * @return true if this entity extends the parent entity, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean doesExtend(EntityTypeImpl<?> parent) {
		IdentifiableTypeImpl<? super X> supertype = this;

		do {
			if (supertype == parent) {
				return true;
			}
			supertype = supertype.getSupertype();
		}
		while (supertype != null);

		return false;
	}

	private void enhanceIfNeccessary() {
		if (this.constructor != null) {
			return;
		}

		synchronized (this) {
			// other thread got it before us?
			if (this.constructor != null) {
				return;
			}

			if (this.constructor == null) {
				try {
					final Class<X> enhancedClass = Enhancer.enhance(this);
					final Constructor<X> constructor = enhancedClass.getConstructor(Class.class, // type
						SessionImpl.class, // session
						SingularAssociationMapping.class, // mapping
						Object.class, // mappingId
						Object.class, // id
						Boolean.TYPE); // initialized

					this.constructor = ReflectHelper.createConstructor(constructor);
				}
				catch (final Exception e) {
					throw new RuntimeException("Cannot enhance class: " + this.getJavaType(), e);
				}
			}
		}
	}

	/**
	 * Returns all the tables in the inheritance chain.
	 * 
	 * @return the array of tables
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTable[] getAllTables() {
		if (this.allTables != null) {
			return this.allTables;

		}

		synchronized (this) {
			if (this.allTables != null) {
				return this.allTables;
			}

			final Map<String, EntityTable> tableMap = Maps.newHashMap();
			this.getAllTables(tableMap);

			final EntityTable[] tables = new EntityTable[tableMap.size()];
			tableMap.values().toArray(tables);

			Arrays.sort(tables, new Comparator<EntityTable>() {

				@Override
				public int compare(EntityTable o1, EntityTable o2) {
					if ((o1 instanceof SecondaryTable) && !(o2 instanceof SecondaryTable)) {
						return 1;
					}

					if ((o2 instanceof SecondaryTable) && !(o1 instanceof SecondaryTable)) {
						return -1;
					}

					return o1.getName().compareTo(o2.getName());
				}
			});

			return this.allTables = tables;
		}
	}

	private void getAllTables(final Map<String, EntityTable> tableMap) {
		tableMap.putAll(this.tableMap);

		for (final EntityTypeImpl<? extends X> child : this.children.values()) {
			if (child != this) {
				child.getAllTables(tableMap);
				tableMap.putAll(child.tableMap);
			}
		}
	}

	/**
	 * Returns if attribute with the <code>path</code> is overridden by the entity.
	 * 
	 * @param path
	 *            the path of the attribute
	 * @return the association metadata or <code>null</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationMetadata getAssociationOverride(String path) {
		for (final AssociationMetadata override : this.metadata.getAssociationOverrides()) {
			if (override.getName().equals(path)) {
				return override;
			}
		}

		return null;
	}

	/**
	 * Returns the associations of the type.
	 * 
	 * @return the associations of the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AssociationMapping<?, ?, ?>[] getAssociations() {
		if (this.associations != null) {
			return this.associations;
		}

		synchronized (this) {
			if (this.associations != null) {
				return this.associations;
			}

			final List<AssociationMapping<?, ?, ?>> associations = Lists.newArrayList();

			for (final AbstractMapping<? super X, ?> mapping : this.mappings.values()) {
				if (mapping instanceof AssociationMapping) {
					associations.add((AssociationMapping<? super X, ?, ?>) mapping);
				}

				if (mapping instanceof EmbeddedMapping) {
					final EmbeddedMapping<? super X, ?> embeddedMapping = (EmbeddedMapping<? super X, ?>) mapping;
					embeddedMapping.addAssociations(associations);
				}
			}

			final AssociationMapping<? super X, ?, ?>[] associatedAttributes0 = new AssociationMapping[associations.size()];
			associations.toArray(associatedAttributes0);

			return this.associations = associatedAttributes0;
		}
	}

	/**
	 * Returns the associated attributes that are eager.
	 * 
	 * @return the associated attributes that are eager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationMapping<?, ?, ?>[] getAssociationsEager() {
		if (this.associationsEager != null) {
			return this.associationsEager;
		}

		synchronized (this) {
			if (this.associationsEager != null) {
				return this.associationsEager;
			}

			final List<AssociationMapping<?, ?, ?>> eagerAssociations = Lists.newArrayList();

			for (final AssociationMapping<?, ?, ?> association : this.getAssociations()) {
				if (association.isEager()) {
					eagerAssociations.add(association);
				}
			}

			final AssociationMapping<?, ?, ?>[] eagerAssociations0 = new AssociationMapping[eagerAssociations.size()];
			eagerAssociations.toArray(eagerAssociations0);

			return this.associationsEager = eagerAssociations0;
		}
	}

	/**
	 * Returns the associated attributes that are joined.
	 * 
	 * @return the associated attributes that are joined
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationMapping<?, ?, ?>[] getAssociationsJoined() {
		if (this.associationsJoined != null) {
			return this.associationsJoined;
		}

		synchronized (this) {
			if (this.associationsJoined != null) {
				return this.associationsJoined;
			}

			final List<AssociationMapping<?, ?, ?>> joinedAssociations = Lists.newArrayList();

			for (final AssociationMapping<?, ?, ?> association : this.getAssociations()) {
				if (association.getJoinTable() != null) {
					joinedAssociations.add(association);
				}
			}

			final AssociationMapping<?, ?, ?>[] joinedAssociations0 = new AssociationMapping[joinedAssociations.size()];
			joinedAssociations.toArray(joinedAssociations0);

			return this.associationsJoined = joinedAssociations0;
		}
	}

	/**
	 * Returns the associated attributes that are persistable by the type.
	 * 
	 * @return the associated attributes that are persistable by the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationMapping<?, ?, ?>[] getAssociationsPersistable() {
		if (this.associationsPersistable != null) {
			return this.associationsPersistable;
		}

		synchronized (this) {
			if (this.associationsPersistable != null) {
				return this.associationsPersistable;
			}

			final List<AssociationMapping<?, ?, ?>> persistableAssociations = Lists.newArrayList();

			for (final AssociationMapping<?, ?, ?> association : this.getAssociations()) {
				if (association.cascadesPersist()) {
					persistableAssociations.add(association);
				}
			}

			final AssociationMapping<?, ?, ?>[] persistableAssociations0 = new AssociationMapping[persistableAssociations.size()];
			persistableAssociations.toArray(persistableAssociations0);

			return this.associationsPersistable = persistableAssociations0;
		}
	}

	/**
	 * Returns the plural associations.
	 * 
	 * @return the plural associations
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	private PluralAssociationMapping<?, ?, ?>[] getAssociationsPlural() {
		if (this.associationsPlural != null) {
			return this.associationsPlural;
		}

		synchronized (this) {
			if (this.associationsPlural != null) {
				return this.associationsPlural;
			}

			final List<PluralAssociationMapping<?, ?, ?>> associationsPlural = Lists.newArrayList();
			for (final AssociationMapping<?, ?, ?> mapping : this.getAssociations()) {
				if (mapping instanceof PluralAssociationMapping) {
					associationsPlural.add((PluralAssociationMapping<? super X, ?, ?>) mapping);
				}
			}

			final PluralAssociationMapping<?, ?, ?>[] associationsPlural0 = new PluralAssociationMapping[associationsPlural.size()];
			associationsPlural.toArray(associationsPlural0);

			return this.associationsPlural = associationsPlural0;
		}
	}

	/**
	 * Returns the array of singular owner lazy association of the type.
	 * 
	 * @return the array of singular owner lazy associations of the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SingularAssociationMapping<?, ?>[] getAssociationsSingularOwnerLazy() {
		if (this.associationsSingularLazy != null) {
			return this.associationsSingularLazy;
		}

		synchronized (this) {
			if (this.associationsSingularLazy != null) {
				return this.associationsSingularLazy;
			}

			final List<SingularAssociationMapping<?, ?>> associationsSingularLazy = Lists.newArrayList();
			for (final AssociationMapping<?, ?, ?> mapping : this.getAssociations()) {
				if (mapping instanceof SingularAssociationMapping) {
					final SingularAssociationMapping<?, ?> singularMapping = (SingularAssociationMapping<?, ?>) mapping;
					if (singularMapping.isOwner() && !singularMapping.isEager()) {
						associationsSingularLazy.add(singularMapping);
					}
				}
			}

			final SingularAssociationMapping<?, ?>[] associationsSingularLazy0 = new SingularAssociationMapping[associationsSingularLazy.size()];
			associationsSingularLazy.toArray(associationsSingularLazy0);

			return this.associationsSingularLazy = associationsSingularLazy0;
		}
	}

	/**
	 * Returns if attribute with the <code>path</code> is overridden by the entity.
	 * 
	 * @param path
	 *            the path of the attribute
	 * @return the column metadata or <code>null</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ColumnMetadata getAttributeOverride(String path) {
		for (final AttributeOverrideMetadata override : this.metadata.getAttributeOverrides()) {
			if (override.getName().equals(path)) {
				return override.getColumn();
			}
		}

		return null;
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
	 * Returns the single id mapping.
	 * 
	 * @return the single id mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public SingularMapping<? super X, ?> getIdMapping() {
		if (this.idMapping != null) {
			return this.idMapping;
		}

		synchronized (this) {
			if (this.idMapping != null) {
				return this.idMapping;
			}

			for (final AbstractMapping<? super X, ?> mapping : this.mappings.values()) {
				if ((mapping.getAttribute() instanceof SingularAttribute) && ((SingularAttribute<? super X, ?>) mapping.getAttribute()).isId()) {
					return this.idMapping = (SingularMapping<? super X, ?>) mapping;
				}
			}

			throw new NullPointerException(); // impossible
		}
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
	public Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>>[] getIdMappings() {
		if (this.idMappings != null) {
			return this.idMappings;
		}

		// populate the id attributes with the inheritance
		synchronized (this) {
			if (this.idMappings != null) {
				return this.idMappings;
			}

			final EmbeddableTypeImpl<?> idType = (EmbeddableTypeImpl<?>) this.getIdType();
			final List<Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>>> idMappings = Lists.newArrayList();

			for (final AbstractMapping<? super X, ?> mapping : this.mappings.values()) {
				// only interested in id mappings
				if (!(mapping.getAttribute() instanceof SingularAttribute) || !((SingularAttribute<? super X, ?>) mapping.getAttribute()).isId()) {
					continue;
				}

				// mapping must be of basic type
				final BasicMapping<? super X, ?> basicMapping = (BasicMapping<? super X, ?>) mapping;

				// must have a corresponding attribute
				final AttributeImpl<?, ?> attribute = idType.getAttribute(mapping.getAttribute().getName());
				if ((attribute == null) || (attribute.getClass() != mapping.getAttribute().getClass())) {
					throw new MappingException("Attribute types mismatch: " + attribute.getJavaMember() + ", " + mapping.getAttribute().getJavaMember(),
						attribute.getLocator(), mapping.getAttribute().getLocator());
				}

				// attribute must be of basic type
				final BasicAttribute<?, ?> basicAttribute = (BasicAttribute<?, ?>) attribute;

				idMappings.add(new Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>>(basicMapping, basicAttribute));
			}

			final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>>[] idMappings0 = new Pair[idMappings.size()];
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
		return this.getManagedInstanceById(session, null, null, id, false);
	}

	/**
	 * Creates a new managed instance with the id.
	 * 
	 * @param session
	 *            the session
	 * @param mapping
	 *            the mapping
	 * @param mappingId
	 *            the mapping id
	 * @param id
	 *            the primary key
	 * @param lazy
	 *            if the instance is lazy
	 * @return the managed instance created
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked" })
	public ManagedInstance<X> getManagedInstanceById(SessionImpl session, SingularAssociationMapping<?, ?> mapping, Object mappingId, Object id, boolean lazy) {
		this.enhanceIfNeccessary();

		try {
			final X instance = (X) this.constructor.newInstance(new Object[] { this.getJavaType(), session, mapping, mappingId, id, !lazy });

			final ManagedInstance<X> managedInstance = new ManagedInstance<X>(this, session, instance, id);

			((EnhancedInstance) instance).__enhanced__$$__setManagedInstance(managedInstance);

			return managedInstance;
		}
		catch (final Exception e) {} // not possible

		return null;
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
	 * Returns the parent of the entity.
	 * 
	 * @return the parent entity or <code>null</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeImpl<? super X> getParent() {
		if (this.isRoot()) {
			return null;
		}

		return (EntityTypeImpl<? super X>) this.getSupertype();
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

	private CriteriaQueryImpl<X> getSelectCriteria() {
		if (this.selectCriteria != null) {
			return this.selectCriteria;
		}

		synchronized (this) {
			// other thread prepared before this one
			if (this.selectCriteria != null) {
				return this.selectCriteria;
			}

			final CriteriaBuilderImpl cb = this.getMetamodel().getEntityManagerFactory().getCriteriaBuilder();
			CriteriaQueryImpl<X> q = cb.createQuery(this.getJavaType());
			final RootImpl<X> r = q.from(this);
			q = q.select(r);

			this.prepareEagerAssociations(r, 0, null);

			// has single id mapping
			if (this.getRootType().hasSingleIdAttribute()) {
				final SingularMapping<? super X, ?> idMapping = this.getRootType().getIdMapping();
				final ParameterExpressionImpl<?> pe = cb.parameter(idMapping.getAttribute().getJavaType());
				final Path<?> path = r.get(idMapping.getAttribute());
				final PredicateImpl predicate = cb.equal(path, pe);

				return this.selectCriteria = q.where(predicate);
			}

			// has multiple id mappings
			final List<PredicateImpl> predicates = Lists.newArrayList();
			for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> pair : this.getIdMappings()) {
				final BasicMapping<? super X, ?> idMapping = pair.getFirst();
				final ParameterExpressionImpl<?> pe = cb.parameter(idMapping.getAttribute().getJavaType());
				final Path<?> path = r.get(idMapping.getAttribute());
				final PredicateImpl predicate = cb.equal(path, pe);

				predicates.add(predicate);
			}

			return this.selectCriteria = q.where(predicates.toArray(new PredicateImpl[predicates.size()]));
		}
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

			Arrays.sort(tables, new Comparator<EntityTable>() {

				@Override
				public int compare(EntityTable o1, EntityTable o2) {
					if ((o1 instanceof SecondaryTable) && !(o2 instanceof SecondaryTable)) {
						return 1;
					}

					if ((o2 instanceof SecondaryTable) && !(o1 instanceof SecondaryTable)) {
						return -1;
					}

					return o1.getName().compareTo(o2.getName());
				}
			});

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
		if (this.getRootType().getInheritanceType() != null) {
			switch (this.getRootType().getInheritanceType()) {
				case SINGLE_TABLE:
					// if this is the root, create the primary table
					if (this.getRootType() == this) {
						this.primaryTable = new EntityTable(this, metadata.getTable());

						this.tableMap.put(this.primaryTable.getName(), this.primaryTable);
					}
					// else map the primary key to the root type's primary table and the tables from the parent
					else {
						final EntityTypeImpl<? super X> supertype = (EntityTypeImpl<? super X>) this.getSupertype();

						this.primaryTable = supertype.primaryTable;
						this.tableMap.putAll(supertype.tableMap);
					}
					break;
				case JOINED:
					// if this is the root, create the primary table
					if (this.getRootType() == this) {
						this.primaryTable = new EntityTable(this, metadata.getTable());

						this.tableMap.put(this.primaryTable.getName(), this.primaryTable);
					}
					// else map all the parent tables and create the primary table as secondary table
					else {
						final EntityTypeImpl<? super X> supertype = (EntityTypeImpl<? super X>) this.getSupertype();
						this.tableMap.putAll(supertype.tableMap);

						this.primaryTable = new SecondaryTable(this, metadata.getTable());
						this.tableMap.put(this.primaryTable.getName(), this.primaryTable);
					}
					break;
				case TABLE_PER_CLASS:
					throw new MappingException("TABLE_PER_CLASS inheritence type is not yet supported", this.getRootType().getLocator());
			}
		}
		// create the primary table
		else {
			this.primaryTable = new EntityTable(this, metadata.getTable());

			this.tableMap.put(this.primaryTable.getName(), this.primaryTable);
		}

		for (final SecondaryTableMetadata secondaryTableMetadata : metadata.getSecondaryTables()) {
			final SecondaryTable secondaryTable = new SecondaryTable(this, secondaryTableMetadata);
			this.tableMap.put(secondaryTableMetadata.getName(), secondaryTable);
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
		if (this.idMethods.containsKey(method)) { // if known id method, let go
			return true;
		}

		final String methodName = method.getName();
		if (methodName.startsWith("get") && (methodName.length() > 3)) { // check if id method
			for (final SingularAttribute<? super X, ?> attribute : this.getSingularAttributes()) {
				final String getterName = "get" + StringUtils.capitalize(attribute.getName());
				if (attribute.isId() && getterName.equals(method.getName())) {
					this.idMethods.put(method, method);
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns if the entity is the root of the hierarchy.
	 * 
	 * @return true if the entity is the root of the hierarchy, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isRoot() {
		return this.getRootType() == this;
	}

	/**
	 * Links the entity's attribute mappings.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void linkMappings() {
		if (this.getRootType().getInheritanceType() != null) {
			if (this.getRootType() != this) {
				// inherit mappings from the parent
				this.mappings.putAll(((EntityTypeImpl<? super X>) this.getSupertype()).mappings);

			}

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
					BasicMapping mapping;

					mapping = new BasicMapping(null, this, (BasicAttribute) attribute);
					this.mappings.put(attribute.getName(), mapping);

					final BasicColumn basicColumn = ((BasicMapping<? super X, ?>) mapping).getColumn();
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
					this.mappings.put(attribute.getName(), new SingularAssociationMapping(null, this, (AssociatedSingularAttribute) attribute));
					break;
				case MANY_TO_MANY:
				case ONE_TO_MANY:
					this.mappings.put(attribute.getName(), new PluralAssociationMapping(null, this, (PluralAttributeImpl) attribute));
					break;
				case EMBEDDED:
					this.mappings.put(attribute.getName(), new EmbeddedMapping(null, this, (EmbeddedAttribute) attribute));
				default:
					break;
			}
		}

		// link the secondary tables
		for (final EntityTable table : this.tableMap.values()) {
			if (table instanceof SecondaryTable) {
				((SecondaryTable) table).link();
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
	public void performInsert(ConnectionImpl connection, EntityTransactionImpl transaction, ManagedInstance<X> managedInstance) throws SQLException {
		for (final EntityTable table : this.getTables()) {
			table.performInsert(connection, managedInstance);
		}

		for (final PluralAssociationMapping<?, ?, ?> association : this.getAssociationsPlural()) {
			association.set(managedInstance, association.get(managedInstance.getInstance()));
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
	public void performRemove(ConnectionImpl connection, EntityTransactionImpl transaction, ManagedInstance<X> instance) throws SQLException {
	}

	/**
	 * Performs select to find the instance.
	 * 
	 * @param entityManager
	 *            the entity manager to use
	 * @param managedInstance
	 *            the managed instance to perform insert for
	 * @return the instance found or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public X performSelect(EntityManagerImpl entityManager, ManagedInstance<X> managedInstance) {
		final TypedQueryImpl<X> q = entityManager.createQuery(this.getSelectCriteria());

		// if has single id then pass it on
		if (this.hasSingleIdAttribute()) {
			q.setParameter(1, managedInstance.getId());
		}
		else {
			int i = 1;
			final Object id = managedInstance.getId();
			for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> pair : this.getIdMappings()) {
				q.setParameter(i++, pair.getSecond().get(id));
			}
		}

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
	public void performUpdate(ConnectionImpl connection, EntityTransactionImpl transaction, ManagedInstance<X> instance) throws SQLException {
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
		final Set<AssociationMapping<?, ?, ?>> attributes = Sets.newHashSet();

		for (final AssociationMapping<?, ?, ?> association : this.getAssociations()) {

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

			for (final AssociationMapping<?, ?, ?> association : this.getAssociationsEager()) {

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

	/**
	 * Processes the associations of the entity after it has been loaded.
	 * 
	 * @param instance
	 *            the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void processAssociations(ManagedInstance<?> instance) {
		final Set<AssociationMapping<?, ?, ?>> associationsLoaded = instance.getAssociationsLoaded();
		for (final AssociationMapping<?, ?, ?> association : this.getAssociations()) {
			if (!associationsLoaded.contains(association)) {
				if (association.isEager()) {
					association.load(instance);
				}
				else {
					association.setLazy(instance);
				}
			}
		}
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
