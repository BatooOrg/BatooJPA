/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.core.impl.model.type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.InheritanceType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.reflect.ConstructorAccessor;
import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.expression.PredicateImpl;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.Enhancer;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.jdbc.DiscriminatorColumn;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.SecondaryTable;
import org.batoo.jpa.core.impl.manager.EntityManagerFactoryImpl;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AssociatedSingularAttribute;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.EntityMapping;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping.MappingType;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.core.util.Pair;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata.EntityListenerType;
import org.batoo.jpa.parser.metadata.IndexMetadata;
import org.batoo.jpa.parser.metadata.SecondaryTableMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;
import org.batoo.jpa.util.BatooUtils;
import org.batoo.jpa.util.FinalWrapper;

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
	private FinalWrapper<EntityTable[]> tables;
	private FinalWrapper<EntityTable[]> updateTables;
	private FinalWrapper<EntityTable[]> allTables;
	private final HashMap<String, AssociatedSingularAttribute<? super X, ?>> idMap = Maps.newHashMap();
	private final boolean cachable;

	private final ConstructorAccessor constructor;

	private CriteriaQueryImpl<X> selectCriteria;
	private CriteriaQueryImpl<X> refreshCriteria;
	private int dependencyCount;

	private final HashMap<EntityTypeImpl<?>, AssociationMapping<?, ?, ?>[]> dependencyMap = Maps.newHashMap();
	private FinalWrapper<BasicMapping<?, ?>[]> basicMappings;

	private FinalWrapper<Mapping<?, ?, ?>[]> singularMappings;
	private FinalWrapper<PluralMapping<?, ?, ?>[]> mappingsPluralSorted;
	private FinalWrapper<PluralMapping<?, ?, ?>[]> mappingsPlural;
	private FinalWrapper<JoinedMapping<?, ?, ?>[]> mappingsJoined;
	private FinalWrapper<AssociationMapping<?, ?, ?>[]> associations;
	private FinalWrapper<AssociationMapping<?, ?, ?>[]> associationsDetachable;
	private FinalWrapper<AssociationMapping<?, ?, ?>[]> associationsJoined;
	private FinalWrapper<AssociationMapping<?, ?, ?>[]> associationsNotPersistable;
	private FinalWrapper<AssociationMapping<?, ?, ?>[]> associationsPersistable;
	private FinalWrapper<AssociationMapping<?, ?, ?>[]> associationsRemovable;
	private FinalWrapper<PluralAssociationMapping<?, ?, ?>[]> associationsPlural;
	private FinalWrapper<SingularAssociationMapping<?, ?>[]> associationsSingular;
	private FinalWrapper<SingularAssociationMapping<?, ?>[]> associationsSingularLazy;
	private final Map<Method, Method> idMethods = Maps.newHashMap();

	private SingularMapping<? super X, ?> idMapping;
	private Boolean suitableForBatchInsert;

	private Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>>[] idMappings;
	private final InheritanceType inheritanceType;

	private final Map<String, EntityTypeImpl<? extends X>> children = Maps.newHashMap();
	private final String discriminatorValue;
	private DiscriminatorColumn discriminatorColumn;
	private EntityTypeImpl<? super X> rootType;
	private final EntityMapping<X> entityMapping;

	private final List<IndexMetadata> indexes;

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
		this.indexes = metadata.getIndexes();
		this.inheritanceType = metadata.getInheritanceType();
		this.discriminatorValue = StringUtils.isNotBlank(metadata.getDiscriminatorValue()) ? metadata.getDiscriminatorValue() : this.name;

		this.addAttributes(metadata);
		this.initTables(metadata);
		this.entityMapping = new EntityMapping<X>(this);
		this.linkMappings();
		this.initIndexes();

		this.cachable = this.getCachable(metamodel.getEntityManagerFactory(), metadata);

		if (metadata.getTableGenerator() != null) {
			metamodel.addTableGenerator(metadata.getTableGenerator());
		}

		if (metadata.getSequenceGenerator() != null) {
			metamodel.addSequenceGenerator(metadata.getSequenceGenerator());
		}

		this.constructor = this.enhance();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void addAttribute(AttributeImpl<? super X, ?> attribute) {
		super.addAttribute(attribute);

		if ((attribute.getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE)
			|| (attribute.getPersistentAttributeType() == PersistentAttributeType.ONE_TO_ONE)) {
			final AssociatedSingularAttribute<? super X, ?> singularAttribute = (AssociatedSingularAttribute<? super X, ?>) attribute;
			if (singularAttribute.getMapsId() != null) {
				this.idMap.put(singularAttribute.getMapsId(), singularAttribute);
			}
		}
	}

	private ConstructorAccessor enhance() {
		try {
			final Class<X> enhancedClass = Enhancer.enhance(this);
			final Constructor<X> constructor = enhancedClass.getConstructor(Class.class, // type
				SessionImpl.class, // session
				Object.class, // id
				Boolean.TYPE); // initialized

			return ReflectHelper.createConstructor(constructor);
		}
		catch (final Exception e) {
			throw new RuntimeException("Cannot enhance class: " + this.getJavaType(), e);
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
	 * Fires the callbacks.
	 * 
	 * @param instance
	 *            the instance
	 * @param type
	 *            the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void fireCallbacks(Object instance, EntityListenerType type) {
		this.fireCallbacks(true, instance, type);
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
		FinalWrapper<EntityTable[]> wrapper = this.allTables;

		if (wrapper == null) {
			synchronized (this) {
				if (this.allTables == null) {

					final Map<String, EntityTable> _tableMap = Maps.newHashMap();
					this.getAllTables(_tableMap);

					final EntityTable[] _tables = new EntityTable[_tableMap.size()];
					_tableMap.values().toArray(_tables);

					Arrays.sort(_tables, new Comparator<EntityTable>() {

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

					this.allTables = new FinalWrapper<EntityTable[]>(_tables);
				}

				wrapper = this.allTables;
			}
		}

		return wrapper.value;
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
		FinalWrapper<AssociationMapping<?, ?, ?>[]> wrapper = this.associations;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associations == null) {

					final List<AssociationMapping<?, ?, ?>> _associations = Lists.newArrayList();

					this.entityMapping.addAssociations(_associations);

					final AssociationMapping<?, ?, ?>[] __associatedAttributes = new AssociationMapping[_associations.size()];
					_associations.toArray(__associatedAttributes);

					this.associations = new FinalWrapper<AssociationMapping<?, ?, ?>[]>(__associatedAttributes);
				}

				wrapper = this.associations;
			}
		}

		return wrapper.value;
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
		FinalWrapper<AssociationMapping<?, ?, ?>[]> wrapper = this.associationsDetachable;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsDetachable == null) {

					final List<AssociationMapping<?, ?, ?>> _associationsDetachable = Lists.newArrayList();

					for (final AssociationMapping<?, ?, ?> association : this.getAssociations()) {
						if (association.cascadesDetach()) {
							_associationsDetachable.add(association);
						}
					}

					final AssociationMapping<?, ?, ?>[] __associationsDetachable = new AssociationMapping[_associationsDetachable.size()];
					_associationsDetachable.toArray(__associationsDetachable);

					this.associationsDetachable = new FinalWrapper<AssociationMapping<?, ?, ?>[]>(__associationsDetachable);
				}

				wrapper = this.associationsDetachable;
			}
		}

		return wrapper.value;
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
		final FinalWrapper<AssociationMapping<?, ?, ?>[]> wrapper = this.associationsJoined;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsJoined == null) {

					final List<AssociationMapping<?, ?, ?>> joinedAssociations = Lists.newArrayList();

					for (final AssociationMapping<?, ?, ?> association : this.getAssociations()) {
						if (association.getTable() != null) {
							joinedAssociations.add(association);
						}
					}

					final AssociationMapping<?, ?, ?>[] __joinedAssociations = new AssociationMapping[joinedAssociations.size()];
					joinedAssociations.toArray(__joinedAssociations);

					this.associationsJoined = new FinalWrapper<AssociationMapping<?, ?, ?>[]>(__joinedAssociations);
				}
			}
		}

		return wrapper.value;
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
		FinalWrapper<AssociationMapping<?, ?, ?>[]> wrapper = this.associationsNotPersistable;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsNotPersistable == null) {

					final List<AssociationMapping<?, ?, ?>> _associationsNotPersistable = Lists.newArrayList();
					for (final AssociationMapping<?, ?, ?> mapping : this.getAssociations()) {
						// skip persistable associations
						if (mapping.cascadesPersist()) {
							continue;
						}

						_associationsNotPersistable.add(mapping);
					}

					final AssociationMapping<?, ?, ?>[] __associationsNotPersistable = new AssociationMapping[_associationsNotPersistable.size()];
					_associationsNotPersistable.toArray(__associationsNotPersistable);

					this.associationsNotPersistable = new FinalWrapper<AssociationMapping<?, ?, ?>[]>(__associationsNotPersistable);
				}

				wrapper = this.associationsNotPersistable;
			}
		}

		return wrapper.value;
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
		FinalWrapper<AssociationMapping<?, ?, ?>[]> wrapper = this.associationsPersistable;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsPersistable == null) {

					final List<AssociationMapping<?, ?, ?>> _associationsPersistable = Lists.newArrayList();

					for (final AssociationMapping<?, ?, ?> association : this.getAssociations()) {
						if (association.cascadesPersist()) {
							_associationsPersistable.add(association);
						}
					}

					final AssociationMapping<?, ?, ?>[] __associationsPersistable = new AssociationMapping[_associationsPersistable.size()];
					_associationsPersistable.toArray(__associationsPersistable);

					this.associationsPersistable = new FinalWrapper<AssociationMapping<?, ?, ?>[]>(__associationsPersistable);
				}

				wrapper = this.associationsPersistable;
			}
		}

		return wrapper.value;
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
		FinalWrapper<PluralAssociationMapping<?, ?, ?>[]> wrapper = this.associationsPlural;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsPlural == null) {

					final List<PluralAssociationMapping<?, ?, ?>> _associationsPlural = Lists.newArrayList();
					for (final AssociationMapping<?, ?, ?> mapping : this.getAssociations()) {
						if (mapping instanceof PluralAssociationMapping) {
							_associationsPlural.add((PluralAssociationMapping<?, ?, ?>) mapping);
						}
					}

					final PluralAssociationMapping<?, ?, ?>[] __associationsPlural = new PluralAssociationMapping[_associationsPlural.size()];
					_associationsPlural.toArray(__associationsPlural);

					this.associationsPlural = new FinalWrapper<PluralAssociationMapping<?, ?, ?>[]>(__associationsPlural);
				}

				wrapper = this.associationsPlural;
			}
		}

		return wrapper.value;
	}

	/**
	 * Returns the associated mappings that are removable by the type.
	 * 
	 * @return the associated mappings that are removable by the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationMapping<?, ?, ?>[] getAssociationsRemovable() {
		FinalWrapper<AssociationMapping<?, ?, ?>[]> wrapper = this.associationsRemovable;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsRemovable == null) {

					final List<AssociationMapping<?, ?, ?>> _associationsRemovable = Lists.newArrayList();

					for (final AssociationMapping<?, ?, ?> association : this.getAssociations()) {
						if (association.cascadesRemove() || association.removesOrphans()) {
							_associationsRemovable.add(association);
						}
					}

					final AssociationMapping<?, ?, ?>[] __associationsRemovable = new AssociationMapping[_associationsRemovable.size()];
					_associationsRemovable.toArray(__associationsRemovable);

					this.associationsRemovable = new FinalWrapper<AssociationMapping<?, ?, ?>[]>(__associationsRemovable);
				}

				wrapper = this.associationsRemovable;
			}
		}

		return wrapper.value;
	}

	/**
	 * Returns the singular associated mappings.
	 * 
	 * @return the singular associated mappings
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SingularAssociationMapping<?, ?>[] getAssociationsSingular() {
		FinalWrapper<SingularAssociationMapping<?, ?>[]> wrapper = this.associationsSingular;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsSingular == null) {

					final List<SingularAssociationMapping<?, ?>> _associationsSingular = Lists.newArrayList();

					for (final AssociationMapping<?, ?, ?> association : this.getAssociations()) {
						if (association instanceof SingularAssociationMapping) {
							_associationsSingular.add((SingularAssociationMapping<?, ?>) association);
						}
					}

					final SingularAssociationMapping<?, ?>[] __associationsSingular = new SingularAssociationMapping[_associationsSingular.size()];
					_associationsSingular.toArray(__associationsSingular);

					this.associationsSingular = new FinalWrapper<SingularAssociationMapping<?, ?>[]>(__associationsSingular);
				}

				wrapper = this.associationsSingular;
			};
		}

		return wrapper.value;
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
		FinalWrapper<SingularAssociationMapping<?, ?>[]> wrapper = this.associationsSingularLazy;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsSingularLazy == null) {

					final List<SingularAssociationMapping<?, ?>> _associationsSingularLazy = Lists.newArrayList();
					for (final AssociationMapping<?, ?, ?> mapping : this.getAssociations()) {
						if (mapping instanceof SingularAssociationMapping) {
							final SingularAssociationMapping<?, ?> singularMapping = (SingularAssociationMapping<?, ?>) mapping;
							if (singularMapping.isOwner() && !singularMapping.isEager()) {
								_associationsSingularLazy.add(singularMapping);
							}
						}
					}

					final SingularAssociationMapping<?, ?>[] __associationsSingularLazy = new SingularAssociationMapping[_associationsSingularLazy.size()];
					_associationsSingularLazy.toArray(__associationsSingularLazy);

					this.associationsSingularLazy = new FinalWrapper<SingularAssociationMapping<?, ?>[]>(__associationsSingularLazy);
				}

				wrapper = this.associationsSingularLazy;
			}
		}

		return wrapper.value;
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
		FinalWrapper<BasicMapping<?, ?>[]> wrapper = this.basicMappings;

		if (wrapper == null) {
			synchronized (this) {
				if (this.basicMappings == null) {

					final List<BasicMapping<?, ?>> _basicMappings = Lists.newArrayList();

					this.entityMapping.addBasicMappings(_basicMappings);

					final BasicMapping<?, ?>[] __basicMappings = new BasicMapping[_basicMappings.size()];
					_basicMappings.toArray(__basicMappings);

					this.basicMappings = new FinalWrapper<BasicMapping<?, ?>[]>(__basicMappings);
				}

				wrapper = this.basicMappings;
			}
		}

		return wrapper.value;
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

	private boolean getCachable(EntityManagerFactoryImpl emf, EntityMetadata metadata) {
		switch (emf.getCache().getCacheMode()) {
			case ALL:
				return true;
			case DISABLE_SELECTIVE:
				if ((metadata.getCacheable() != null) && !metadata.getCacheable().booleanValue()) {
					return false;
				}

				return true;
			case UNSPECIFIED:
			case ENABLE_SELECTIVE:
				if ((metadata.getCacheable() != null) && metadata.getCacheable().booleanValue()) {
					return true;
				}

				return false;
			default:
				return false;
		}
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

			this.prepareEagerJoins(r, 0, null);

			// has single id mapping
			if (this.getRootType().hasSingleIdAttribute()) {
				final SingularMapping<? super X, ?> _idMapping = this.getRootType().getIdMapping();
				final ParameterExpressionImpl<?> pe = cb.parameter(_idMapping.getAttribute().getJavaType());
				final Path<?> path = r.get(_idMapping.getAttribute());
				final PredicateImpl predicate = cb.equal(path, pe);

				this.selectCriteria = q.where(predicate);

				return this.selectCriteria;
			}

			// has multiple id mappings
			final List<PredicateImpl> predicates = Lists.newArrayList();
			for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> pair : this.getIdMappings()) {
				final BasicMapping<? super X, ?> _idMapping = pair.getFirst();
				final ParameterExpressionImpl<?> pe = cb.parameter(_idMapping.getAttribute().getJavaType());
				final Path<?> path = r.get(_idMapping.getAttribute());
				final PredicateImpl predicate = cb.equal(path, pe);

				predicates.add(predicate);
			}

			this.selectCriteria = q.where(predicates.toArray(new PredicateImpl[predicates.size()]));

			return this.selectCriteria;
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

			for (final Mapping<? super X, ?, ?> mapping : this.entityMapping.getChildren()) {
				if ((mapping instanceof SingularMapping) && ((SingularMapping<? super X, ?>) mapping).getAttribute().isId()) {
					this.idMapping = (SingularMapping<? super X, ?>) mapping;

					return this.idMapping;
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
			final List<Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>>> _idMappings = Lists.newArrayList();

			for (final Mapping<? super X, ?, ?> mapping : this.entityMapping.getChildren()) {
				// only interested in id mappings
				if (!(mapping instanceof SingularMapping) || !((SingularMapping<? super X, ?>) mapping).getAttribute().isId()) {
					continue;
				}

				// mapping must be of basic type
				final BasicMapping<? super X, ?> basicMapping = (BasicMapping<? super X, ?>) mapping;

				// must have a corresponding attribute
				final AttributeImpl<?, ?> attribute = idType.getAttribute(mapping.getName());

				if (attribute == null) {
					throw new MappingException("Attribute not found: " + mapping.getJavaType(), basicMapping.getAttribute().getLocator());
				}

				if (attribute.getJavaType() != mapping.getJavaType()) {
					throw new MappingException("Attribute types mismatch: " + attribute.getJavaMember() + ", " + mapping.getJavaType(), attribute.getLocator(),
						basicMapping.getAttribute().getLocator());
				}

				// attribute must be of basic type
				final BasicAttribute<?, ?> basicAttribute = (BasicAttribute<?, ?>) attribute;

				_idMappings.add(new Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>>(basicMapping, basicAttribute));
			}

			final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>>[] idMappings0 = new Pair[_idMappings.size()];
			_idMappings.toArray(idMappings0);

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
		return this.getIdMapping().get(instance);
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
		try {
			final X instance = (X) this.constructor.newInstance(new Object[] { this.getJavaType(), session, id.getId(), !lazy });

			final ManagedInstance<X> managedInstance = new ManagedInstance<X>(this, session, instance, id);

			((EnhancedInstance) instance).__enhanced__$$__setManagedInstance(managedInstance);

			return managedInstance;
		}
		catch (final Exception e) {
			throw new PersistenceException("Cannot create instance " + id, e);
		} // not possible
	}

	/**
	 * Returns the mapped id.
	 * 
	 * @param name
	 *            thename of the id field
	 * @param instance
	 *            the instance
	 * @return the id
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Object getMappedId(String name, Object instance) {
		final AssociatedSingularAttribute<? super X, ?> attribute = this.idMap.get(name);
		if (attribute == null) {
			return null;
		}

		final Object mappedEntity = attribute.get(instance);
		if (mappedEntity == null) {
			return null;
		}

		final EntityTypeImpl<?> entity = this.getMetamodel().entity(mappedEntity.getClass());
		if (entity.hasSingleIdAttribute()) {
			return entity.getIdMapping().get(mappedEntity);
		}

		return null;
	}

	/**
	 * Retuns the element collection mappings.
	 * 
	 * @return the element collection mappings
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinedMapping<?, ?, ?>[] getMappingsJoined() {
		FinalWrapper<JoinedMapping<?, ?, ?>[]> wrapper = this.mappingsJoined;

		if (wrapper == null) {
			synchronized (this) {
				if (this.mappingsJoined == null) {

					final List<JoinedMapping<?, ?, ?>> _mappingsJoined = Lists.newArrayList();
					this.entityMapping.addJoinedMappings(_mappingsJoined);

					final JoinedMapping<?, ?, ?>[] __mappingsJoined = new JoinedMapping[_mappingsJoined.size()];
					_mappingsJoined.toArray(__mappingsJoined);

					this.mappingsJoined = new FinalWrapper<JoinedMapping<?, ?, ?>[]>(__mappingsJoined);
				}

				wrapper = this.mappingsJoined;
			}
		}

		return wrapper.value;
	}

	/**
	 * Retuns the element collection mappings.
	 * 
	 * @return the element collection mappings
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PluralMapping<?, ?, ?>[] getMappingsPlural() {
		FinalWrapper<PluralMapping<?, ?, ?>[]> wrapper = this.mappingsPlural;

		if (wrapper == null) {
			synchronized (this) {
				if (this.mappingsPlural == null) {

					final List<PluralMapping<?, ?, ?>> _mappingsPlural = Lists.newArrayList();
					this.entityMapping.addPluralMappings(_mappingsPlural);

					final PluralMapping<?, ?, ?>[] __mappingsPlural = new PluralMapping[_mappingsPlural.size()];
					_mappingsPlural.toArray(__mappingsPlural);

					this.mappingsPlural = new FinalWrapper<PluralMapping<?, ?, ?>[]>(__mappingsPlural);
				}

				wrapper = this.mappingsPlural;
			}
		}

		return wrapper.value;
	}

	/**
	 * Returns the sorted plural associations.
	 * 
	 * @return the sorted plural associations
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PluralMapping<?, ?, ?>[] getMappingsPluralSorted() {
		FinalWrapper<PluralMapping<?, ?, ?>[]> wrapper = this.mappingsPluralSorted;

		if (wrapper == null) {
			synchronized (this) {
				if (this.mappingsPluralSorted == null) {

					final List<PluralMapping<?, ?, ?>> _mappingsPluralSorted = Lists.newArrayList();
					for (final PluralMapping<?, ?, ?> mapping : this.getMappingsPlural()) {
						if (StringUtils.isNotBlank(mapping.getOrderBy())) {
							_mappingsPluralSorted.add(mapping);
						}
					}

					final PluralMapping<?, ?, ?>[] __mappingsPluralSorted = new PluralMapping[_mappingsPluralSorted.size()];
					_mappingsPluralSorted.toArray(__mappingsPluralSorted);

					this.mappingsPluralSorted = new FinalWrapper<PluralMapping<?, ?, ?>[]>(__mappingsPluralSorted);
				}

				wrapper = this.mappingsPluralSorted;
			}
		}

		return wrapper.value;
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
		FinalWrapper<Mapping<?, ?, ?>[]> wrapper = this.singularMappings;

		if (wrapper == null) {
			synchronized (this) {
				if (this.singularMappings == null) {

					final List<Mapping<?, ?, ?>> _singularMappings = Lists.newArrayList();

					this.entityMapping.addSingularMappings(_singularMappings);

					final Mapping<?, ?, ?>[] __singularMappings = new Mapping[_singularMappings.size()];
					_singularMappings.toArray(__singularMappings);

					this.singularMappings = new FinalWrapper<Mapping<?, ?, ?>[]>(__singularMappings);
				}

				wrapper = this.singularMappings;
			}
		}

		return wrapper.value;
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
	 * Returns the entityMapping of the EntityTypeImpl.
	 * 
	 * @return the entityMapping of the EntityTypeImpl
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityMapping<X> getRootMapping() {
		return this.entityMapping;
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
		FinalWrapper<EntityTable[]> wrapper = this.tables;

		if (wrapper == null) {
			synchronized (this) {
				if (this.tables == null) {

					final EntityTable[] _tables = new EntityTable[this.tableMap.size()];
					this.tableMap.values().toArray(_tables);

					Arrays.sort(_tables, new Comparator<EntityTable>() {

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

					this.tables = new FinalWrapper<EntityTable[]>(_tables);
				}

				wrapper = this.tables;
			}
		}

		return wrapper.value;
	}

	/**
	 * Initializes the custom indexes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void initCustomIndexes() {
		for (final IndexMetadata index : this.indexes) {
			final EntityTable table = StringUtils.isNotBlank(index.getTable()) ? this.tableMap.get(index.getTable()) : this.primaryTable;
			if (table == null) {
				throw new MappingException("Cannot locate table for index " + index.getName(), index.getLocator());
			}

			final List<BasicColumn> columns = Lists.newArrayList();
			for (final String path : index.getColumnNames()) {
				final Mapping<?, ?, ?> mapping = this.getRootMapping().getMapping(path);
				if (!(mapping instanceof BasicMapping)) {
					throw new MappingException("Cannot locate the basic path " + path + " for index " + index.getName(), index.getLocator());
				}

				columns.add(((BasicMapping<?, ?>) mapping).getColumn());
			}

			table.addIndex(index.getName(), columns.toArray(new BasicColumn[columns.size()]));
		}
	}

	/**
	 * Initializes the indexes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void initIndexes() {
		for (final BasicMapping<?, ?> basicMapping : this.getBasicMappings()) {
			final IndexMetadata index = basicMapping.getAttribute().getIndex();
			if (index != null) {
				final EntityTable table = StringUtils.isNotBlank(index.getTable()) ? this.tableMap.get(index.getTable()) : this.primaryTable;
				if (table == null) {
					throw new MappingException("Cannot locate table for index " + index.getName(), index.getLocator());
				}

				if (table.addIndex(index.getName(), basicMapping.getColumn())) {
					throw new MappingException("Duplicate index with the same name " + index.getName(), index.getLocator());
				}
			}
		}

		this.initCustomIndexes();
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
	 * Returns if the entity is cachable.
	 * 
	 * @return true if the entity is cachable, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isCachable() {
		return this.cachable;
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
	 * Returns if the entity is suitable for batch insert, that is not of {@link IdType#IDENTITY}.
	 * 
	 * @return true if the entity is suitable for batch insert, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isSuitableForBatchInsert() {
		if (this.suitableForBatchInsert != null) {
			return this.suitableForBatchInsert;
		}

		return this.suitableForBatchInsert = this.hasSingleIdAttribute() && (this.idMapping instanceof BasicMapping)
			&& (((BasicMapping<? super X, ?>) this.idMapping).getAttribute().getIdType() != IdType.IDENTITY);
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

		this.entityMapping.createMappings();

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
	 * @param instances
	 *            the managed instances to perform insert for
	 * @param size
	 *            the size of the batch
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performInsert(Connection connection, ManagedInstance<?>[] instances, int size) throws SQLException {
		for (final EntityTable table : this.getTables()) {
			table.performInsert(connection, instances, size);
		}
	}

	/**
	 * Performs refresh for the instance
	 * 
	 * @param connection
	 *            the connection
	 * @param instance
	 *            the managed instance
	 * @param lockMode
	 *            the lock mode
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performRefresh(Connection connection, ManagedInstance<X> instance, LockModeType lockMode) {
		final SessionImpl session = instance.getSession();

		final QueryImpl<X> q = session.getEntityManager().createQuery(this.getCriteriaRefresh());
		q.setLockMode(lockMode);

		final Object id = instance.getId().getId();

		// if has single id then pass it on
		if (this.hasSingleIdAttribute()) {
			q.setParameter(0, id);
		}
		else {
			int i = 0;
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
	 * @param instances
	 *            the managed instance to perform remove for
	 * @param size
	 *            the size of the batch
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performRemove(Connection connection, ManagedInstance<?>[] instances, int size) throws SQLException {
		for (final EntityTable table : this.getTables()) {
			table.performRemove(connection, instances, size);
		}
	}

	/**
	 * Performs select to find the instance.
	 * 
	 * @param entityManager
	 *            the entity manager to use
	 * @param id
	 *            the id of the instance to select
	 * @param lockMode
	 *            the lock mode
	 * @return the instance found or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public X performSelect(EntityManagerImpl entityManager, Object id, LockModeType lockMode) {
		final QueryImpl<X> q = entityManager.createQuery(this.getCriteriaSelect());

		q.setLockMode(lockMode);

		// if has single id then pass it on
		if (this.hasSingleIdAttribute()) {
			q.setParameter(0, id);
		}
		else {
			int i = 0;
			for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> pair : this.getIdMappings()) {
				q.setParameter(i++, pair.getSecond().get(id));
			}
		}

		return q.getSingleResult();
	}

	/**
	 * Selects the version for the instance.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param instance
	 *            the managed instance to perform update for
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * @return returns the current version
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Object performSelectVersion(Connection connection, ManagedInstance<? extends X> instance) throws SQLException {
		return this.getTables()[0].performSelectVersion(connection, instance);
	}

	/**
	 * Performs the update for the instance.
	 * 
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
	public void performUpdate(Connection connection, ManagedInstance<?> instance) throws SQLException {
		FinalWrapper<EntityTable[]> wrapper = this.updateTables;

		if (wrapper == null) {
			synchronized (this) {
				if (this.updateTables == null) {
					final List<EntityTable> _updateTables = Lists.newArrayList(this.getTables());
					for (final Iterator<EntityTable> i = _updateTables.iterator(); i.hasNext();) {
						if (!i.next().performUpdateWithUpdatability(connection, instance)) {
							i.remove();
						}
					}

					this.updateTables = new FinalWrapper<EntityTable[]>(_updateTables.toArray(new EntityTable[_updateTables.size()]));
				}

				wrapper = this.updateTables;
			}
		}

		for (final EntityTable table : wrapper.value) {
			table.performUpdate(connection, instance);
		}
	}

	/**
	 * Performs the version update for the instance.
	 * 
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
	public void performVersionUpdate(Connection connection, ManagedInstance<? extends X> instance) throws SQLException {
		this.getTables()[0].performVersionUpdate(connection, instance);
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
	public void prepareEagerJoins(FetchParent<?, ?> r, int depth, AssociationMapping<?, ?, ?> parent) {
		if (depth < EntityTypeImpl.MAX_DEPTH) {
			this.prepareEagerJoins(r, depth, parent, this.entityMapping.getEagerMappings());
		}
	}

	private void prepareEagerJoins(FetchParent<?, ?> r, int depth, AssociationMapping<?, ?, ?> parent, JoinedMapping<?, ?, ?>[] mappings) {
		for (final JoinedMapping<?, ?, ?> mapping : mappings) {
			// Element collection
			if (mapping.getMappingType() == MappingType.ELEMENT_COLLECTION) {
				r.fetch(mapping.getAttribute().getName(), JoinType.LEFT);
				continue;
			}
			// embeddable
			else if (mapping.getMappingType() == MappingType.EMBEDDABLE) {
				final Fetch<?, Object> r2 = r.fetch(mapping.getAttribute().getName(), JoinType.LEFT);

				this.prepareEagerJoins(r2, depth, parent, ((EmbeddedMapping<?, ?>) mapping).getEagerMappings());

				continue;
			}
			// association
			else {
				final AssociationMapping<?, ?, ?> association = (AssociationMapping<?, ?, ?>) mapping;
				// if we are coming from the inverse side and inverse side is not many-to-one then skip
				if ((parent != null) && //
					(association.getInverse() == parent) && //
					(parent.getAttribute().getPersistentAttributeType() != PersistentAttributeType.MANY_TO_ONE)) {
					continue;
				}

				final Fetch<?, Object> r2 = r.fetch(mapping.getAttribute().getName(), JoinType.LEFT);

				final EntityTypeImpl<?> type = association.getType();

				type.prepareEagerJoins(r2, depth + 1, association);
			}
		}
	}

	/**
	 * Runs the validators for the instance
	 * 
	 * @param entityManagerFactory
	 *            the entity manager factory
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void runValidators(EntityManagerFactoryImpl entityManagerFactory, ManagedInstance<?> instance) {
		final ValidatorFactory factory = entityManagerFactory.getValidationFactory();

		final Validator validator = factory.usingContext().getValidator();

		Class<?>[] groups;

		switch (instance.getStatus()) {
			case NEW:
				groups = entityManagerFactory.getPersistValidators();
				break;
			case MANAGED:
				groups = entityManagerFactory.getUpdateValidators();
				break;
			default:
				groups = entityManagerFactory.getRemoveValidators();
				break;
		}

		final Set<?> result = validator.validate(instance.getInstance(), groups);
		if ((result != null) && (result.size() > 0)) {
			final Set<ConstraintViolation<?>> violations = Sets.newHashSet();
			for (final Object violation : result) {
				violations.add((ConstraintViolation<?>) violation);
			}
			throw new ConstraintViolationException(violations);
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
