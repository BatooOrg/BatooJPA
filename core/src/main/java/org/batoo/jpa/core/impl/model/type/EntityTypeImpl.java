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
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.PredicateImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.Enhancer;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.DiscriminatorColumn;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.SecondaryTable;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.RootMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.util.BatooUtils;
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
	private CriteriaQueryImpl<X> refreshCriteria;

	private int dependencyCount;
	private final HashMap<EntityTypeImpl<?>, AssociationMapping<?, ?, ?>[]> dependencyMap = Maps.newHashMap();

	private BasicMapping<?, ?>[] basicMappings;
	private Mapping<?, ?, ?>[] singularMappings;
	private AssociationMapping<?, ?, ?>[] associations;
	private AssociationMapping<?, ?, ?>[] associationsDetachable;
	private AssociationMapping<?, ?, ?>[] associationsEager;
	private AssociationMapping<?, ?, ?>[] associationsJoined;
	private AssociationMapping<?, ?, ?>[] associationsNotPersistable;
	private AssociationMapping<?, ?, ?>[] associationsPersistable;
	private AssociationMapping<?, ?, ?>[] associationsRemovable;
	private PluralAssociationMapping<?, ?, ?>[] associationsPlural;
	private SingularAssociationMapping<?, ?>[] associationsSingularLazy;

	private final Map<Method, Method> idMethods = Maps.newHashMap();

	private SingularMapping<? super X, ?> idMapping;
	private Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>>[] idMappings;

	private final InheritanceType inheritanceType;
	private final Map<String, EntityTypeImpl<? extends X>> children = Maps.newHashMap();
	private final String discriminatorValue;
	private DiscriminatorColumn discriminatorColumn;
	private EntityTypeImpl<? super X> rootType;
	private final RootMapping<X> rootMapping;

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
		this.rootMapping = new RootMapping<X>(this);
		this.linkMappings();
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
	 * Returns if this entity extends the parent entity.
	 * 
	 * @param parent
	 *            the parent to test
	 * @return true if this entity extends the parent entity, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean extendz(EntityTypeImpl<?> parent) {
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
	public AssociationMapping<?, ?, ?>[] getAssociations() {
		if (this.associations != null) {
			return this.associations;
		}

		synchronized (this) {
			if (this.associations != null) {
				return this.associations;
			}

			final List<AssociationMapping<?, ?, ?>> associations = Lists.newArrayList();

			this.rootMapping.addAssociations(associations);

			final AssociationMapping<?, ?, ?>[] associatedAttributes0 = new AssociationMapping[associations.size()];
			associations.toArray(associatedAttributes0);

			return this.associations = associatedAttributes0;
		}
	}

	/**
	 * Returns the associated attributes that are detachable by the type.
	 * 
	 * @return the associated attributes that are detachable by the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationMapping<?, ?, ?>[] getAssociationsDetachable() {
		if (this.associationsDetachable != null) {
			return this.associationsDetachable;
		}

		synchronized (this) {
			if (this.associationsDetachable != null) {
				return this.associationsDetachable;
			}

			final List<AssociationMapping<?, ?, ?>> associationsDetachable = Lists.newArrayList();

			for (final AssociationMapping<?, ?, ?> association : this.getAssociations()) {
				if (association.cascadesDetach()) {
					associationsDetachable.add(association);
				}
			}

			final AssociationMapping<?, ?, ?>[] associationsDetachable0 = new AssociationMapping[associationsDetachable.size()];
			associationsDetachable.toArray(associationsDetachable0);

			return this.associationsDetachable = associationsDetachable0;
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
	 * Returns the plural associations that are not persistable.
	 * 
	 * @return the plural associations that are not persistable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationMapping<?, ?, ?>[] getAssociationsNotPersistable() {
		if (this.associationsNotPersistable != null) {
			return this.associationsNotPersistable;
		}

		synchronized (this) {
			if (this.associationsNotPersistable != null) {
				return this.associationsNotPersistable;
			}

			final List<AssociationMapping<?, ?, ?>> associationsNotPersistable = Lists.newArrayList();
			for (final AssociationMapping<?, ?, ?> mapping : this.getAssociations()) {
				// skip persistable associations
				if (mapping.cascadesPersist()) {
					continue;
				}

				associationsNotPersistable.add(mapping);
			}

			final AssociationMapping<?, ?, ?>[] associationsNotPersistable0 = new AssociationMapping[associationsNotPersistable.size()];
			associationsNotPersistable.toArray(associationsNotPersistable0);

			return this.associationsNotPersistable = associationsNotPersistable0;
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
	public PluralAssociationMapping<?, ?, ?>[] getAssociationsPlural() {
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
					associationsPlural.add((PluralAssociationMapping<?, ?, ?>) mapping);
				}
			}

			final PluralAssociationMapping<?, ?, ?>[] associationsPlural0 = new PluralAssociationMapping[associationsPlural.size()];
			associationsPlural.toArray(associationsPlural0);

			return this.associationsPlural = associationsPlural0;
		}
	}

	/**
	 * Returns the associated attributes that are removable by the type.
	 * 
	 * @return the associated attributes that are removable by the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationMapping<?, ?, ?>[] getAssociationsRemovable() {
		if (this.associationsRemovable != null) {
			return this.associationsRemovable;
		}

		synchronized (this) {
			if (this.associationsRemovable != null) {
				return this.associationsRemovable;
			}

			final List<AssociationMapping<?, ?, ?>> associationsRemovable = Lists.newArrayList();

			for (final AssociationMapping<?, ?, ?> association : this.getAssociations()) {
				if (association.cascadesRemove() || association.removesOrphans()) {
					associationsRemovable.add(association);
				}
			}

			final AssociationMapping<?, ?, ?>[] associationsRemovable0 = new AssociationMapping[associationsRemovable.size()];
			associationsRemovable.toArray(associationsRemovable0);

			return this.associationsRemovable = associationsRemovable0;
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
	 * Returns the basic mappings of the type.
	 * 
	 * @return the basic mappings of the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicMapping<?, ?>[] getBasicMappings() {
		if (this.basicMappings != null) {
			return this.basicMappings;
		}

		synchronized (this) {
			if (this.basicMappings != null) {
				return this.basicMappings;
			}

			final List<BasicMapping<?, ?>> basicMappings = Lists.newArrayList();

			this.rootMapping.addBasicMappings(basicMappings);

			final BasicMapping<?, ?>[] basicMappings0 = new BasicMapping[basicMappings.size()];
			basicMappings.toArray(basicMappings0);

			return this.basicMappings = basicMappings0;
		}
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

	private CriteriaQueryImpl<X> getCriteriaRefresh() {
		if (this.refreshCriteria != null) {
			return this.refreshCriteria;
		}

		synchronized (this) {
			// other thread prepared before this one
			if (this.refreshCriteria != null) {
				return this.refreshCriteria;
			}

			final CriteriaBuilderImpl cb = this.getMetamodel().getEntityManagerFactory().getCriteriaBuilder();
			CriteriaQueryImpl<X> q = cb.createQuery(this.getJavaType());
			q.internal();
			final RootImpl<X> r = q.from(this);
			q = q.select(r);
			r.alias(BatooUtils.acronym(this.name).toLowerCase());

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

			return this.refreshCriteria = q.where(predicates.toArray(new PredicateImpl[predicates.size()]));
		}
	}

	private CriteriaQueryImpl<X> getCriteriaSelect() {
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
			q.internal();
			final RootImpl<X> r = q.from(this);
			q = q.select(r);
			r.alias(BatooUtils.acronym(this.name).toLowerCase());

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
	 * Returns the dependencies for the associate type
	 * 
	 * @param associate
	 *            the associate type
	 * @return the array of associations for the associate
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationMapping<?, ?, ?>[] getDependenciesFor(EntityTypeImpl<?> associate) {
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

			for (final Mapping<? super X, ?, ?> mapping : this.rootMapping.getChildren()) {
				if ((mapping instanceof SingularMapping) && ((SingularMapping<? super X, ?>) mapping).getAttribute().isId()) {
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

			for (final Mapping<? super X, ?, ?> mapping : this.rootMapping.getChildren()) {
				// only interested in id mappings
				if (!(mapping instanceof SingularMapping) || !((SingularMapping<? super X, ?>) mapping).getAttribute().isId()) {
					continue;
				}

				// mapping must be of basic type
				final BasicMapping<? super X, ?> basicMapping = (BasicMapping<? super X, ?>) mapping;

				// must have a corresponding attribute
				final AttributeImpl<?, ?> attribute = idType.getAttribute(mapping.getName());
				if ((attribute == null) || (attribute.getJavaType() != mapping.getJavaType())) {
					throw new MappingException("Attribute types mismatch: " + attribute.getJavaMember() + ", " + mapping.getJavaType(), attribute.getLocator(),
						basicMapping.getAttribute().getLocator());
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
	 * Returns the id of the instance.
	 * 
	 * @param instance
	 *            the instance
	 * @return the id of the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Object getInstanceId(X instance) {
		if (this.getIdMapping() != null) {
			return this.idMapping.get(instance);
		}

		final Object id = ((EmbeddableTypeImpl<?>) this.getIdType()).newInstance();
		for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> pair : this.getIdMappings()) {
			final Object value = pair.getSecond().get(instance);
			pair.getFirst().getAttribute().set(id, value);
		}

		return id;
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
	public ManagedInstance<X> getManagedInstance(SessionImpl session, X instance) {
		if (instance == null) {
			throw new NullPointerException();
		}

		return new ManagedInstance<X>(this, session, instance);
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
	@SuppressWarnings({ "unchecked" })
	public ManagedInstance<X> getManagedInstanceById(SessionImpl session, ManagedId<X> id, boolean lazy) {
		this.enhanceIfNeccessary();

		try {
			final X instance = (X) this.constructor.newInstance(new Object[] { this.getJavaType(), session, id.getId(), !lazy });

			final ManagedInstance<X> managedInstance = new ManagedInstance<X>(this, session, instance, id);

			((EnhancedInstance) instance).__enhanced__$$__setManagedInstance(managedInstance);

			return managedInstance;
		}
		catch (final Exception e) {} // not possible

		return null;
	}

	/**
	 * Returns the singular mappings.
	 * 
	 * @return the singular mappings
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Mapping<?, ?, ?>[] getMappingsSingular() {
		if (this.singularMappings != null) {
			return this.singularMappings;
		}

		synchronized (this) {
			if (this.singularMappings != null) {
				return this.singularMappings;
			}

			final List<Mapping<?, ?, ?>> singularMappings = Lists.newArrayList();

			this.rootMapping.addSingularMappings(singularMappings);

			final Mapping<?, ?, ?>[] singularMappings0 = new Mapping[singularMappings.size()];
			singularMappings.toArray(singularMappings0);

			return this.singularMappings = singularMappings0;
		}
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
	 * Returns the rootMapping of the EntityTypeImpl.
	 * 
	 * @return the rootMapping of the EntityTypeImpl
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public RootMapping<X> getRootMapping() {
		return this.rootMapping;
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
	private void linkMappings() {
		if (this.getRootType().getInheritanceType() != null) {
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

		this.rootMapping.createMappings();

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
	 * @param instance
	 *            the managed instance to perform insert for
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performInsert(ConnectionImpl connection, ManagedInstance<X> instance) throws SQLException {
		for (final EntityTable table : this.getTables()) {
			table.performInsert(connection, instance);
		}
	}

	/**
	 * Performs refresh for the instance
	 * 
	 * @param connection
	 *            the connection
	 * @param instance
	 *            the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performRefresh(ConnectionImpl connection, ManagedInstance<X> instance) {
		final SessionImpl session = instance.getSession();

		final TypedQueryImpl<X> q = session.getEntityManager().createQuery(this.getCriteriaRefresh());

		final Object id = instance.getId().getId();

		// if has single id then pass it on
		if (this.hasSingleIdAttribute()) {
			q.setParameter(1, id);
		}
		else {
			int i = 1;
			for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> pair : this.getIdMappings()) {
				q.setParameter(i++, pair.getSecond().get(id));
			}
		}

		instance.setRefreshing(true);

		q.getSingleResult();
	}

	/**
	 * @param connection
	 *            the connection to use
	 * @param instance
	 *            the managed instance to perform remove for
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performRemove(ConnectionImpl connection, ManagedInstance<X> instance) throws SQLException {
		for (final EntityTable table : this.getTables()) {
			table.performRemove(connection, instance);
		}
	}

	/**
	 * Performs select to find the instance.
	 * 
	 * @param entityManager
	 *            the entity manager to use
	 * @param id
	 *            the id of the instance to select
	 * @return the instance found or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public X performSelect(EntityManagerImpl entityManager, Object id) {
		final TypedQueryImpl<X> q = entityManager.createQuery(this.getCriteriaSelect());

		// if has single id then pass it on
		if (this.hasSingleIdAttribute()) {
			q.setParameter(1, id);
		}
		else {
			int i = 1;
			for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> pair : this.getIdMappings()) {
				q.setParameter(i++, pair.getSecond().get(id));
			}
		}

		return q.getSingleResult();
	}

	/**
	 * @param connection
	 *            the connection to use
	 * @param instance
	 *            the managed instance to perform update for
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performUpdate(ConnectionImpl connection, ManagedInstance<X> instance) throws SQLException {
		for (final EntityTable table : this.getTables()) {
			table.performUpdate(connection, instance);
		}
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

		final AssociationMapping<?, ?, ?>[] dependencies = new AssociationMapping[attributes.size()];
		attributes.toArray(dependencies);

		this.dependencyCount += dependencies.length;

		this.dependencyMap.put(associate, dependencies);
	}

	/**
	 * @param r
	 *            the fetch parent
	 * @param depth
	 *            the depth
	 * @param parent
	 *            the parent
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void prepareEagerAssociations(FetchParent<?, ?> r, int depth, AssociationMapping<?, ?, ?> parent) {
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "EntityTypeImpl [name=" + this.name + "]";
	}
}
