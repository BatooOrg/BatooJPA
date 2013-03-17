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
package org.batoo.jpa.core.impl.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.BasicAttribute;
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
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.batoo.common.reflect.AbstractAccessor;
import org.batoo.common.reflect.ConstructorAccessor;
import org.batoo.common.reflect.ReflectHelper;
import org.batoo.common.util.BatooUtils;
import org.batoo.common.util.FinalWrapper;
import org.batoo.jpa.annotations.FetchStrategyType;
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
import org.batoo.jpa.core.impl.instance.Status;
import org.batoo.jpa.core.impl.manager.EntityManagerFactoryImpl;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.AssociatedSingularAttribute;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.model.mapping.AssociationMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.BasicMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.EntityMapping;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralMappingEx;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.SingularMappingEx;
import org.batoo.jpa.core.util.Pair;
import org.batoo.jpa.jdbc.AbstractColumn;
import org.batoo.jpa.jdbc.AbstractTable;
import org.batoo.jpa.jdbc.BasicColumn;
import org.batoo.jpa.jdbc.DiscriminatorColumn;
import org.batoo.jpa.jdbc.EntityTable;
import org.batoo.jpa.jdbc.IdType;
import org.batoo.jpa.jdbc.JoinColumn;
import org.batoo.jpa.jdbc.SecondaryTable;
import org.batoo.jpa.jdbc.mapping.Mapping;
import org.batoo.jpa.jdbc.mapping.MappingType;
import org.batoo.jpa.jdbc.mapping.SingularMapping;
import org.batoo.jpa.jdbc.model.EntityTypeDescriptor;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata.EntityListenerType;
import org.batoo.jpa.parser.metadata.IndexMetadata;
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
 * @since 2.0.0
 */
public class EntityTypeImpl<X> extends IdentifiableTypeImpl<X> implements EntityType<X>, EntityTypeDescriptor {

	private final EntityMetadata metadata;
	private final String name;
	private EntityTable primaryTable;
	private final Map<String, EntityTable> tableMap = Maps.newHashMap();
	private FinalWrapper<EntityTable[]> tables;
	private FinalWrapper<EntityTable[]> updateTables;
	private FinalWrapper<EntityTable[]> allTables;
	private final HashMap<String, AssociatedSingularAttribute<? super X, ?>> idMap = Maps.newHashMap();

	private final ConstructorAccessor constructor;

	private CriteriaQueryImpl<X> selectCriteria;
	private CriteriaQueryImpl<X> refreshCriteria;
	private int dependencyCount;
	private boolean canBatchRemoves;

	private final HashMap<EntityTypeImpl<?>, AssociationMappingImpl<?, ?, ?>[]> dependencyMap = Maps.newHashMap();
	private FinalWrapper<BasicMappingImpl<?, ?>[]> basicMappingImpls;

	private FinalWrapper<AbstractMapping<?, ?, ?>[]> singularMappings;
	private FinalWrapper<PluralMappingEx<?, ?, ?>[]> mappingsPluralSorted;
	private FinalWrapper<PluralMappingEx<?, ?, ?>[]> mappingsPlural;
	private FinalWrapper<JoinedMapping<?, ?, ?>[]> mappingsJoined;
	private FinalWrapper<AssociationMappingImpl<?, ?, ?>[]> associations;
	private FinalWrapper<AssociationMappingImpl<?, ?, ?>[]> associationsDetachable;
	private FinalWrapper<AssociationMappingImpl<?, ?, ?>[]> associationsJoined;
	private FinalWrapper<AssociationMappingImpl<?, ?, ?>[]> associationsNotPersistable;
	private FinalWrapper<AssociationMappingImpl<?, ?, ?>[]> associationsPersistable;
	private FinalWrapper<AssociationMappingImpl<?, ?, ?>[]> associationsRemovable;
	private FinalWrapper<PluralAssociationMappingImpl<?, ?, ?>[]> associationsPlural;
	private FinalWrapper<SingularAssociationMappingImpl<?, ?>[]> associationsSingular;
	private FinalWrapper<SingularAssociationMappingImpl<?, ?>[]> associationsSingularLazy;
	private final Map<Method, Method> idMethods = Maps.newHashMap();

	private SingularMappingEx<? super X, ?> idMapping;
	private Boolean suitableForBatchInsert;

	private Pair<SingularMapping<?, ?>, AbstractAccessor>[] idMappings;
	private InheritanceType inheritanceType;

	private final Map<String, EntityTypeImpl<? extends X>> children = Maps.newHashMap();
	private final String discriminatorValue;
	private DiscriminatorColumn discriminatorColumn;
	private EntityTypeImpl<? super X> rootType;
	private final EntityMapping<X> entityMapping;

	private final List<IndexMetadata> indexes;
	private final int maxFetchJoinDepth;

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
	 * @since 2.0.0
	 */
	public EntityTypeImpl(MetamodelImpl metamodel, IdentifiableTypeImpl<? super X> parent, Class<X> javaType, EntityMetadata metadata) {
		super(metamodel, parent, javaType, metadata);

		this.name = metadata.getName();
		this.metadata = metadata;
		this.indexes = metadata.getIndexes();
		this.inheritanceType = metadata.getInheritanceType();
		this.discriminatorValue = StringUtils.isNotBlank(metadata.getDiscriminatorValue()) ? metadata.getDiscriminatorValue() : this.name;
		this.maxFetchJoinDepth = metamodel.getEntityManagerFactory().getMaxFetchJoinDepth();

		this.addAttributes(metadata);
		this.initTables(metadata);
		this.entityMapping = new EntityMapping<X>(this);
		this.linkMappings();
		this.initIndexes();

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
			if (StringUtils.isNotBlank(singularAttribute.getMapsId())) {
				this.idMap.put(singularAttribute.getMapsId(), singularAttribute);
			}
		}
	}

	/**
	 * Returns if remove operation can be combined into a batch.
	 * <p>
	 * The remove operation can be combined into a batch provided:
	 * <ul>
	 * <li>the entity has no version attribute
	 * <li>the entity has single basic id type.
	 * 
	 * @return <code>true</code> if remove operation can be combined into a batch, <code>false</code> otherwise
	 * 
	 * @since 2.0.1
	 */
	public boolean canBatchRemoves() {
		return this.canBatchRemoves;
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
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public void fireCallbacks(Object instance, EntityListenerType type) {
		this.fireCallbacks(true, instance, type);
	}

	/**
	 * Returns all the tables in the inheritance chain.
	 * 
	 * @return the array of tables
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public AssociationMappingImpl<?, ?, ?>[] getAssociations() {
		FinalWrapper<AssociationMappingImpl<?, ?, ?>[]> wrapper = this.associations;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associations == null) {

					final List<AssociationMappingImpl<?, ?, ?>> _associations = Lists.newArrayList();

					this.entityMapping.addAssociations(_associations);

					final AssociationMappingImpl<?, ?, ?>[] __associatedAttributes = new AssociationMappingImpl[_associations.size()];
					_associations.toArray(__associatedAttributes);

					this.associations = new FinalWrapper<AssociationMappingImpl<?, ?, ?>[]>(__associatedAttributes);
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
	 * @since 2.0.0
	 */
	public AssociationMappingImpl<?, ?, ?>[] getAssociationsDetachable() {
		FinalWrapper<AssociationMappingImpl<?, ?, ?>[]> wrapper = this.associationsDetachable;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsDetachable == null) {

					final List<AssociationMappingImpl<?, ?, ?>> _associationsDetachable = Lists.newArrayList();

					for (final AssociationMappingImpl<?, ?, ?> association : this.getAssociations()) {
						if (association.cascadesDetach()) {
							_associationsDetachable.add(association);
						}
					}

					final AssociationMappingImpl<?, ?, ?>[] __associationsDetachable = new AssociationMappingImpl[_associationsDetachable.size()];
					_associationsDetachable.toArray(__associationsDetachable);

					this.associationsDetachable = new FinalWrapper<AssociationMappingImpl<?, ?, ?>[]>(__associationsDetachable);
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
	 * @since 2.0.0
	 */
	public AssociationMappingImpl<?, ?, ?>[] getAssociationsJoined() {
		final FinalWrapper<AssociationMappingImpl<?, ?, ?>[]> wrapper = this.associationsJoined;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsJoined == null) {

					final List<AssociationMappingImpl<?, ?, ?>> joinedAssociations = Lists.newArrayList();

					for (final AssociationMappingImpl<?, ?, ?> association : this.getAssociations()) {
						if (association.getJoinTable() != null) {
							joinedAssociations.add(association);
						}
					}

					final AssociationMappingImpl<?, ?, ?>[] __joinedAssociations = new AssociationMappingImpl[joinedAssociations.size()];
					joinedAssociations.toArray(__joinedAssociations);

					this.associationsJoined = new FinalWrapper<AssociationMappingImpl<?, ?, ?>[]>(__joinedAssociations);
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
	 * @since 2.0.0
	 */
	public AssociationMappingImpl<?, ?, ?>[] getAssociationsNotPersistable() {
		FinalWrapper<AssociationMappingImpl<?, ?, ?>[]> wrapper = this.associationsNotPersistable;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsNotPersistable == null) {

					final List<AssociationMappingImpl<?, ?, ?>> _associationsNotPersistable = Lists.newArrayList();
					for (final AssociationMappingImpl<?, ?, ?> mapping : this.getAssociations()) {
						// skip persistable associations
						if (mapping.cascadesPersist()) {
							continue;
						}

						_associationsNotPersistable.add(mapping);
					}

					final AssociationMappingImpl<?, ?, ?>[] __associationsNotPersistable = new AssociationMappingImpl[_associationsNotPersistable.size()];
					_associationsNotPersistable.toArray(__associationsNotPersistable);

					this.associationsNotPersistable = new FinalWrapper<AssociationMappingImpl<?, ?, ?>[]>(__associationsNotPersistable);
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
	 * @since 2.0.0
	 */
	public AssociationMappingImpl<?, ?, ?>[] getAssociationsPersistable() {
		FinalWrapper<AssociationMappingImpl<?, ?, ?>[]> wrapper = this.associationsPersistable;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsPersistable == null) {

					final List<AssociationMappingImpl<?, ?, ?>> _associationsPersistable = Lists.newArrayList();

					for (final AssociationMappingImpl<?, ?, ?> association : this.getAssociations()) {
						if (association.cascadesPersist()) {
							_associationsPersistable.add(association);
						}
					}

					final AssociationMappingImpl<?, ?, ?>[] __associationsPersistable = new AssociationMappingImpl[_associationsPersistable.size()];
					_associationsPersistable.toArray(__associationsPersistable);

					this.associationsPersistable = new FinalWrapper<AssociationMappingImpl<?, ?, ?>[]>(__associationsPersistable);
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
	 * @since 2.0.0
	 */
	public PluralAssociationMappingImpl<?, ?, ?>[] getAssociationsPlural() {
		FinalWrapper<PluralAssociationMappingImpl<?, ?, ?>[]> wrapper = this.associationsPlural;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsPlural == null) {

					final List<PluralAssociationMappingImpl<?, ?, ?>> _associationsPlural = Lists.newArrayList();
					for (final AssociationMappingImpl<?, ?, ?> mapping : this.getAssociations()) {
						if (mapping instanceof PluralAssociationMappingImpl) {
							_associationsPlural.add((PluralAssociationMappingImpl<?, ?, ?>) mapping);
						}
					}

					final PluralAssociationMappingImpl<?, ?, ?>[] __associationsPlural = new PluralAssociationMappingImpl[_associationsPlural.size()];
					_associationsPlural.toArray(__associationsPlural);

					this.associationsPlural = new FinalWrapper<PluralAssociationMappingImpl<?, ?, ?>[]>(__associationsPlural);
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
	 * @since 2.0.0
	 */
	public AssociationMappingImpl<?, ?, ?>[] getAssociationsRemovable() {
		FinalWrapper<AssociationMappingImpl<?, ?, ?>[]> wrapper = this.associationsRemovable;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsRemovable == null) {

					final List<AssociationMappingImpl<?, ?, ?>> _associationsRemovable = Lists.newArrayList();

					for (final AssociationMappingImpl<?, ?, ?> association : this.getAssociations()) {
						if (association.cascadesRemove() || association.removesOrphans()) {
							_associationsRemovable.add(association);
						}
					}

					final AssociationMappingImpl<?, ?, ?>[] __associationsRemovable = new AssociationMappingImpl[_associationsRemovable.size()];
					_associationsRemovable.toArray(__associationsRemovable);

					this.associationsRemovable = new FinalWrapper<AssociationMappingImpl<?, ?, ?>[]>(__associationsRemovable);
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
	 * @since 2.0.0
	 */
	public SingularAssociationMappingImpl<?, ?>[] getAssociationsSingular() {
		FinalWrapper<SingularAssociationMappingImpl<?, ?>[]> wrapper = this.associationsSingular;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsSingular == null) {

					final List<SingularAssociationMappingImpl<?, ?>> _associationsSingular = Lists.newArrayList();

					for (final AssociationMappingImpl<?, ?, ?> association : this.getAssociations()) {
						if (association instanceof SingularAssociationMappingImpl) {
							_associationsSingular.add((SingularAssociationMappingImpl<?, ?>) association);
						}
					}

					final SingularAssociationMappingImpl<?, ?>[] __associationsSingular = new SingularAssociationMappingImpl[_associationsSingular.size()];
					_associationsSingular.toArray(__associationsSingular);

					this.associationsSingular = new FinalWrapper<SingularAssociationMappingImpl<?, ?>[]>(__associationsSingular);
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
	 * @since 2.0.0
	 */
	public SingularAssociationMappingImpl<?, ?>[] getAssociationsSingularOwnerLazy() {
		FinalWrapper<SingularAssociationMappingImpl<?, ?>[]> wrapper = this.associationsSingularLazy;

		if (wrapper == null) {
			synchronized (this) {
				if (this.associationsSingularLazy == null) {

					final List<SingularAssociationMappingImpl<?, ?>> _associationsSingularLazy = Lists.newArrayList();
					for (final AssociationMappingImpl<?, ?, ?> mapping : this.getAssociations()) {
						if (mapping instanceof SingularAssociationMappingImpl) {
							final SingularAssociationMappingImpl<?, ?> singularMapping = (SingularAssociationMappingImpl<?, ?>) mapping;
							if (singularMapping.isOwner() && !singularMapping.isEager()) {
								_associationsSingularLazy.add(singularMapping);
							}
						}
					}

					final SingularAssociationMappingImpl<?, ?>[] __associationsSingularLazy = new SingularAssociationMappingImpl[_associationsSingularLazy.size()];
					_associationsSingularLazy.toArray(__associationsSingularLazy);

					this.associationsSingularLazy = new FinalWrapper<SingularAssociationMappingImpl<?, ?>[]>(__associationsSingularLazy);
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
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public BasicMappingImpl<?, ?>[] getBasicMappings() {
		FinalWrapper<BasicMappingImpl<?, ?>[]> wrapper = this.basicMappingImpls;

		if (wrapper == null) {
			synchronized (this) {
				if (this.basicMappingImpls == null) {

					final List<BasicMappingImpl<?, ?>> _basicMappings = Lists.newArrayList();

					this.entityMapping.addBasicMappings(_basicMappings);

					final BasicMappingImpl<?, ?>[] __basicMappings = new BasicMappingImpl[_basicMappings.size()];
					_basicMappings.toArray(__basicMappings);

					this.basicMappingImpls = new FinalWrapper<BasicMappingImpl<?, ?>[]>(__basicMappings);
				}

				wrapper = this.basicMappingImpls;
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

	/**
	 * Returns the child based on the <code>discriminatorValue</code> value.
	 * 
	 * @param discriminatorValue
	 *            the discriminator value of the child
	 * @return the child type
	 * 
	 * @since 2.0.0
	 */
	public EntityTypeImpl<? extends X> getChildType(String discriminatorValue) {
		if (discriminatorValue.equals(this.discriminatorValue)) {
			return this;
		}

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
				final SingularMappingEx<? super X, ?> idMapping = this.getRootType().getIdMapping();
				final ParameterExpressionImpl<?> pe = cb.parameter(idMapping.getAttribute().getJavaType());
				final Path<?> path = r.get(idMapping.getAttribute().getName());
				final PredicateImpl predicate = cb.equal(path, pe);

				return this.refreshCriteria = q.where(predicate);
			}

			// has multiple id mappings
			final List<PredicateImpl> predicates = Lists.newArrayList();
			for (final Pair<SingularMapping<?, ?>, AbstractAccessor> pair : this.getIdMappings()) {
				final SingularMapping<?, ?> _idMapping = pair.getFirst();
				final ParameterExpressionImpl<?> pe = cb.parameter(_idMapping.getJavaType());

				final Path<?> path = r.get(_idMapping.getName());
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
				final SingularMappingEx<? super X, ?> _idMapping = this.getRootType().getIdMapping();
				final ParameterExpressionImpl<?> pe = cb.parameter(_idMapping.getAttribute().getJavaType());
				final Path<?> path = r.get(_idMapping.getAttribute().getName());
				final PredicateImpl predicate = cb.equal(path, pe);

				this.selectCriteria = q.where(predicate);

				return this.selectCriteria;
			}

			// has multiple id mappings
			final List<PredicateImpl> predicates = Lists.newArrayList();
			for (final Pair<SingularMapping<?, ?>, AbstractAccessor> pair : this.getIdMappings()) {
				final SingularMapping<?, ?> _idMapping = pair.getFirst();
				final ParameterExpressionImpl<?> pe = cb.parameter(_idMapping.getJavaType());

				final Path<?> path = r.get(_idMapping.getName());
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
	 * @since 2.0.0
	 */
	public AssociationMappingImpl<?, ?, ?>[] getDependenciesFor(EntityTypeImpl<?> associate) {
		return this.dependencyMap.get(associate);
	}

	/**
	 * Returns the dependencyCount.
	 * 
	 * @return the dependencyCount
	 * @since 2.0.0
	 */
	public int getDependencyCount() {
		return this.dependencyCount;
	}

	/**
	 * Returns the discriminator column of the entity.
	 * 
	 * @return the discriminator column of the entity
	 * 
	 * @since 2.0.0
	 */
	public DiscriminatorColumn getDiscriminatorColumn() {
		return this.discriminatorColumn;
	}

	/**
	 * Returns the set of discriminator values in the range of this entity's hierarchy.
	 * 
	 * @return the set of discriminator values
	 * 
	 * @since 2.0.0
	 */
	public Set<String> getDiscriminators() {
		return this.children.keySet();
	}

	/**
	 * Returns the discriminatorValue of the EntityTypeImpl.
	 * 
	 * @return the discriminatorValue of the EntityTypeImpl
	 * 
	 * @since 2.0.0
	 */
	@Override
	public String getDiscriminatorValue() {
		return this.discriminatorValue;
	}

	/**
	 * Returns the id of the entity from the instance.
	 * 
	 * @param instance
	 *            the instance
	 * @return the managedId or null
	 * 
	 * @since 2.0.0
	 */
	public ManagedId<X> getId(Object instance) {
		Object id;
		final MutableBoolean allNull = new MutableBoolean(true);

		if (this.hasSingleIdAttribute()) {
			id = this.getIdImpl(instance, this.getIdMapping(), allNull);
		}
		else {
			// create the id class
			id = this.newCompositeId();

			for (final Pair<SingularMapping<?, ?>, AbstractAccessor> pair : this.getIdMappings()) {
				final SingularMapping<?, ?> child = pair.getFirst();

				final Object childId = this.getIdImpl(instance, child, allNull);
				if (childId != null) {
					allNull.setValue(false);
				}

				pair.getSecond().set(id, childId);
			}
		}

		if (allNull.booleanValue()) {
			return null;
		}

		return new ManagedId<X>(id, this);
	}

	/**
	 * Returns the id of the entity from the resultset row.
	 * 
	 * @param session
	 *            the session
	 * @param row
	 *            the row
	 * @return the managedId or null
	 * @throws SQLException
	 *             if an SQL error occurrs
	 * 
	 * @since 2.0.0
	 */
	public ManagedId<X> getId(SessionImpl session, ResultSet row) throws SQLException {
		return this.getId(session, row, this.getPrimaryTable().getIdFields());
	}

	/**
	 * Returns the id of the entity from the resultset row.
	 * 
	 * @param session
	 *            the session
	 * @param row
	 *            the row
	 * @param idFields
	 *            the id fields
	 * @return the managedId or null
	 * @throws SQLException
	 *             if an SQL error occurrs
	 * 
	 * @since 2.0.0
	 */
	public ManagedId<X> getId(SessionImpl session, ResultSet row, HashMap<AbstractColumn, String> idFields) throws SQLException {
		Object id;
		final MutableBoolean allNull = new MutableBoolean(true);

		if (this.hasSingleIdAttribute()) {
			id = this.getIdImpl(session, row, idFields, this.getIdMapping(), allNull);
		}
		else {
			// create the id class
			id = this.newCompositeId();

			for (final Pair<SingularMapping<?, ?>, AbstractAccessor> pair : this.getIdMappings()) {
				final SingularMapping<?, ?> child = pair.getFirst();

				final Object childId = this.getIdImpl(session, row, idFields, child, allNull);
				if (childId != null) {
					allNull.setValue(false);
				}

				pair.getSecond().set(id, childId);
			}
		}

		if (allNull.booleanValue()) {
			return null;
		}

		return new ManagedId<X>(id, this);
	}

	private Object getIdImpl(Object instance, SingularMapping<?, ?> idMapping, MutableBoolean allNull) {
		// handle basic mapping
		if (idMapping instanceof BasicMappingImpl) {
			final Object value = idMapping.get(instance);
			if (value != null) {
				allNull.setValue(false);
			}

			return value;
		}

		// handle embedded id
		if (idMapping instanceof EmbeddedMappingImpl) {
			final EmbeddedMappingImpl<?, ?> embeddedMapping = (EmbeddedMappingImpl<?, ?>) idMapping;
			final Object id = embeddedMapping.getAttribute().newInstance();

			for (final Mapping<?, ?, ?> child : embeddedMapping.getChildren()) {
				final Object childId = this.getIdImpl(instance, (SingularMappingEx<?, ?>) child, allNull);
				if (childId != null) {
					allNull.setValue(false);
				}

				((AbstractMapping<?, ?, ?>) child).getAttribute().set(id, childId);
			}

			return allNull.booleanValue() ? null : id;
		}

		// handle singular associated
		final SingularAssociationMappingImpl<?, ?> singularAssociationMapping = (SingularAssociationMappingImpl<?, ?>) idMapping;

		final Object associate = idMapping.get(instance);
		final ManagedId<?> id = singularAssociationMapping.getType().getId(associate);

		return id != null ? id.getId() : null;
	}

	private Object getIdImpl(SessionImpl session, ResultSet row, HashMap<AbstractColumn, String> idFields, SingularMapping<?, ?> idMapping,
		MutableBoolean allNull) throws SQLException {

		// handle basic mapping
		if (idMapping instanceof BasicMappingImpl) {
			final BasicColumn column = ((BasicMappingImpl<?, ?>) idMapping).getColumn();
			final String field = idFields.get(column);

			final Object value = row.getObject(field);
			if (value != null) {
				allNull.setValue(false);
			}

			return value;
		}

		// handle embedded id
		if (idMapping instanceof EmbeddedMappingImpl) {
			final EmbeddedMappingImpl<?, ?> embeddedMapping = (EmbeddedMappingImpl<?, ?>) idMapping;
			final Object id = embeddedMapping.getAttribute().newInstance();

			for (final Mapping<?, ?, ?> child : embeddedMapping.getChildren()) {
				Object childId = this.getIdImpl(session, row, idFields, (SingularMappingEx<?, ?>) child, allNull);

				final AttributeImpl<?, ?> attribute = ((AbstractMapping<?, ?, ?>) child).getAttribute();
				final PersistentAttributeType attributeType = attribute.getPersistentAttributeType();
				if ((attributeType == PersistentAttributeType.MANY_TO_ONE) //
					|| (attributeType == PersistentAttributeType.ONE_TO_ONE)) {
					childId = session.getEntityManager().getReference(attribute.getJavaType(), childId);
				}

				attribute.set(id, childId);
			}

			return allNull.booleanValue() ? null : id;
		}

		// handle singular associated
		final SingularAssociationMappingImpl<?, ?> singularAssociationMapping = (SingularAssociationMappingImpl<?, ?>) idMapping;

		final HashMap<AbstractColumn, String> translatedIdFields = Maps.newHashMap();
		for (final JoinColumn joinColumn : singularAssociationMapping.getForeignKey().getJoinColumns()) {
			translatedIdFields.put(joinColumn.getReferencedColumn(), idFields.get(joinColumn));
		}

		final ManagedId<?> id = singularAssociationMapping.getType().getId(session, row, translatedIdFields);

		return id != null ? id.getId() : null;
	}

	/**
	 * Returns the single id mapping.
	 * 
	 * @return the single id mapping
	 * 
	 * @since 2.0.0
	 */
	@Override
	public SingularMappingEx<? super X, ?> getIdMapping() {
		if (this.idMapping != null) {
			return this.idMapping;
		}

		synchronized (this) {
			if (this.idMapping != null) {
				return this.idMapping;
			}

			for (final Mapping<? super X, ?, ?> mapping : this.entityMapping.getChildren()) {
				if ((mapping instanceof SingularMappingEx) && ((SingularMappingEx<? super X, ?>) mapping).getAttribute().isId()) {
					this.idMapping = (SingularMappingEx<? super X, ?>) mapping;

					return this.idMapping;
				}
			}

			throw new NullPointerException(); // impossible
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Pair<SingularMapping<?, ?>, AbstractAccessor>[] getIdMappings() {
		if (this.idMappings != null) {
			return this.idMappings;
		}

		// populate the id attributes with the inheritance
		synchronized (this) {
			if (this.idMappings != null) {
				return this.idMappings;
			}

			final List<Pair<SingularMapping<?, ?>, AbstractAccessor>> _idMappings = Lists.newArrayList();
			for (final Mapping<? super X, ?, ?> mapping : this.entityMapping.getChildren()) {
				// only interested in id mappings
				if (!(mapping instanceof SingularMappingEx) || !((SingularMappingEx<? super X, ?>) mapping).getAttribute().isId()) {
					continue;
				}

				// must have a corresponding field
				Field field;
				try {
					field = this.getIdClass().getDeclaredField(mapping.getName());
				}
				catch (final Exception e) {
					throw new MappingException("Attribute not found: " + this.getIdClass().getName() + "." + mapping.getName(), mapping.getLocator());
				}

				final Class<?> javaType;
				if (mapping instanceof SingularAssociationMappingImpl) {
					final EntityTypeImpl<?> type = ((SingularAssociationMappingImpl<? super X, ?>) mapping).getType();

					if (type.hasSingleIdAttribute()) {
						javaType = type.getIdType().getJavaType();
					}
					else {
						javaType = type.getIdClass();
					}
				}
				else {
					javaType = mapping.getJavaType();
				}

				if (field.getType() != javaType) {
					throw new MappingException("Attribute types mismatch: " + field + ", " + mapping.getJavaType(), mapping.getLocator());
				}

				final SingularMappingEx<? super X, ?> singularMapping = (SingularMappingEx<? super X, ?>) mapping;
				final AbstractAccessor accessor = ReflectHelper.getAccessor(field);

				_idMappings.add(new Pair<SingularMapping<?, ?>, AbstractAccessor>(singularMapping, accessor));
			}

			final Pair<SingularMapping<?, ?>, AbstractAccessor>[] idMappings0 = new Pair[_idMappings.size()];
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public PluralMappingEx<?, ?, ?>[] getMappingsPlural() {
		FinalWrapper<PluralMappingEx<?, ?, ?>[]> wrapper = this.mappingsPlural;

		if (wrapper == null) {
			synchronized (this) {
				if (this.mappingsPlural == null) {

					final List<PluralMappingEx<?, ?, ?>> _mappingsPlural = Lists.newArrayList();
					this.entityMapping.addPluralMappings(_mappingsPlural);

					final PluralMappingEx<?, ?, ?>[] __mappingsPlural = new PluralMappingEx[_mappingsPlural.size()];
					_mappingsPlural.toArray(__mappingsPlural);

					this.mappingsPlural = new FinalWrapper<PluralMappingEx<?, ?, ?>[]>(__mappingsPlural);
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
	 * @since 2.0.0
	 */
	public PluralMappingEx<?, ?, ?>[] getMappingsPluralSorted() {
		FinalWrapper<PluralMappingEx<?, ?, ?>[]> wrapper = this.mappingsPluralSorted;

		if (wrapper == null) {
			synchronized (this) {
				if (this.mappingsPluralSorted == null) {

					final List<PluralMappingEx<?, ?, ?>> _mappingsPluralSorted = Lists.newArrayList();
					for (final PluralMappingEx<?, ?, ?> mapping : this.getMappingsPlural()) {
						if (mapping.getOrderBy() != null) {
							_mappingsPluralSorted.add(mapping);
						}
					}

					final PluralMappingEx<?, ?, ?>[] __mappingsPluralSorted = new PluralMappingEx[_mappingsPluralSorted.size()];
					_mappingsPluralSorted.toArray(__mappingsPluralSorted);

					this.mappingsPluralSorted = new FinalWrapper<PluralMappingEx<?, ?, ?>[]>(__mappingsPluralSorted);
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
	 * @since 2.0.0
	 */
	public AbstractMapping<?, ?, ?>[] getMappingsSingular() {
		FinalWrapper<AbstractMapping<?, ?, ?>[]> wrapper = this.singularMappings;

		if (wrapper == null) {
			synchronized (this) {
				if (this.singularMappings == null) {

					final List<AbstractMapping<?, ?, ?>> _singularMappings = Lists.newArrayList();

					this.entityMapping.addSingularMappings(_singularMappings);

					final AbstractMapping<?, ?, ?>[] __singularMappings = new AbstractMapping[_singularMappings.size()];
					_singularMappings.toArray(__singularMappings);

					this.singularMappings = new FinalWrapper<AbstractMapping<?, ?, ?>[]>(__singularMappings);
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
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	@Override
	public EntityTable getPrimaryTable() {
		return this.primaryTable;
	}

	/**
	 * Returns the entityMapping of the EntityTypeImpl.
	 * 
	 * @return the entityMapping of the EntityTypeImpl
	 * 
	 * @since 2.0.0
	 */
	public EntityMapping<X> getRootMapping() {
		return this.entityMapping;
	}

	/**
	 * Returns the root type of the hierarchy.
	 * 
	 * @return the root type of the hierarchy
	 * 
	 * @since 2.0.0
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
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
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	private void initCustomIndexes() {
		for (final IndexMetadata index : this.indexes) {
			final EntityTable table = StringUtils.isNotBlank(index.getTable()) ? this.tableMap.get(index.getTable()) : this.primaryTable;
			if (table == null) {
				throw new MappingException("Cannot locate table for index " + index.getName(), index.getLocator());
			}

			final List<BasicColumn> columns = Lists.newArrayList();
			for (final String path : index.getColumnNames()) {
				final AbstractMapping<?, ?, ?> mapping = this.getRootMapping().getMapping(path);
				if (!(mapping instanceof BasicMappingImpl)) {
					throw new MappingException("Cannot locate the basic path " + path + " for index " + index.getName(), index.getLocator());
				}

				columns.add(((BasicMappingImpl<?, ?>) mapping).getColumn());
			}

			table.addIndex(index.getName(), columns.toArray(new BasicColumn[columns.size()]));
		}
	}

	/**
	 * Initializes the indexes
	 * 
	 * @since 2.0.0
	 */
	private void initIndexes() {
		for (final BasicMappingImpl<?, ?> basicMapping : this.getBasicMappings()) {
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
	 * @since 2.0.0
	 * @param metadata
	 */
	private void initTables(EntityMetadata metadata) {
		if (this.getRootType() != this) {
			if (this.getRootType().getInheritanceType() == null) {
				this.getRootType().setInherited();
			}

			switch (this.getRootType().getInheritanceType()) {
				case SINGLE_TABLE:
					// if this is the root, create the primary table
					if (this.getRootType() == this) {
						this.primaryTable = new EntityTable(this.getMetamodel().getJdbcAdaptor(), this, metadata.getTable());

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
						this.primaryTable = new EntityTable(this.getMetamodel().getJdbcAdaptor(), this, metadata.getTable());

						this.tableMap.put(this.primaryTable.getName(), this.primaryTable);
					}
					// else map all the parent tables and create the primary table as secondary table
					else {
						final EntityTypeImpl<? super X> supertype = (EntityTypeImpl<? super X>) this.getSupertype();
						this.tableMap.putAll(supertype.tableMap);

						this.primaryTable = new SecondaryTable(this.getMetamodel().getJdbcAdaptor(), this, metadata.getTable());
						this.tableMap.put(this.primaryTable.getName(), this.primaryTable);
					}
					break;
				case TABLE_PER_CLASS:
					throw new MappingException("TABLE_PER_CLASS inheritence type is not yet supported", this.getRootType().getLocator());
			}
		}
		// create the primary table
		else {
			this.primaryTable = new EntityTable(this.getMetamodel().getJdbcAdaptor(), this, metadata.getTable());

			this.tableMap.put(this.primaryTable.getName(), this.primaryTable);
		}

		for (final SecondaryTableMetadata secondaryTableMetadata : metadata.getSecondaryTables()) {
			final SecondaryTable secondaryTable = new SecondaryTable(this.getMetamodel().getJdbcAdaptor(), this, secondaryTableMetadata);
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
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public boolean isRoot() {
		return this.getRootType() == this;
	}

	/**
	 * Returns if the entity is suitable for batch insert, that is not of {@link IdType#IDENTITY}.
	 * 
	 * @return true if the entity is suitable for batch insert, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean isSuitableForBatchInsert() {
		if (this.suitableForBatchInsert != null) {
			return this.suitableForBatchInsert;
		}

		return this.suitableForBatchInsert = this.hasSingleIdAttribute() && (this.idMapping instanceof BasicMappingImpl)
			&& (((BasicMappingImpl<? super X, ?>) this.idMapping).getAttribute().getIdType() != IdType.IDENTITY);
	}

	/**
	 * Links the entity's attribute mappings.
	 * 
	 * @since 2.0.0
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
			this.discriminatorColumn = new DiscriminatorColumn(this.getMetamodel().getJdbcAdaptor(), this.primaryTable, this.metadata.getDiscriminatorColumn());
		}

		this.entityMapping.createMappings();

		// link the secondary tables
		for (final EntityTable table : this.tableMap.values()) {
			if (table instanceof SecondaryTable) {
				((SecondaryTable) table).link();
			}
		}

		this.canBatchRemoves = (this.getVersionAttribute() == null) && this.hasSingleIdAttribute() && (this.getIdMapping() instanceof BasicAttribute);
	}

	/**
	 * Performs inserts to each table for the managed instance.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstances
	 *            the managed instances to perform insert for
	 * @param size
	 *            the size of the batch
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since 2.0.0
	 */
	public void performInsert(Connection connection, ManagedInstance<?>[] managedInstances, int size) throws SQLException {
		final Object[] instances = new Object[size];
		for (int i = 0; i < size; i++) {
			instances[i] = managedInstances[i].getInstance();
		}

		for (final EntityTable table : this.getTables()) {
			table.performInsert(connection, this, instances, size);
		}

		for (int i = 0; i < size; i++) {
			managedInstances[i].setStatus(Status.MANAGED);
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
	 * @param processed
	 *            the set of processed instances
	 * 
	 * @since 2.0.0
	 */
	public void performRefresh(Connection connection, ManagedInstance<X> instance, LockModeType lockMode, Set<Object> processed) {
		final SessionImpl session = instance.getSession();

		final QueryImpl<X> q = session.getEntityManager().createQuery(this.getCriteriaRefresh());

		if (processed.size() == 0) {
			q.setLockMode(lockMode);
		}

		final Object id = instance.getId().getId();

		// if has single id then pass it on
		if (this.hasSingleIdAttribute()) {
			q.setParameter(1, id);
		}
		else {
			int i = 1;
			for (final Pair<SingularMapping<?, ?>, AbstractAccessor> pair : this.getIdMappings()) {
				q.setParameter(i++, pair.getSecond().get(id));
			}
		}

		instance.setRefreshing(true);

		try {
			q.getSingleResult();
		}
		finally {
			instance.setRefreshing(false);
		}
	}

	/**
	 * @param connection
	 *            the connection to use
	 * @param managedInstances
	 *            the managed instance to perform remove for
	 * @param size
	 *            the size of the batch
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since 2.0.0
	 */
	public void performRemove(Connection connection, ManagedInstance<?>[] managedInstances, int size) throws SQLException {
		final Object[] instances = new Object[size];
		for (int i = 0; i < size; i++) {
			instances[i] = managedInstances[i].getInstance();
		}

		for (final EntityTable table : this.getTables()) {
			if (table == this.primaryTable) {
				continue;
			}

			table.performRemove(connection, instances, size);
		}

		this.primaryTable.performRemove(connection, instances, size);
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
	 * @since 2.0.0
	 */
	public X performSelect(EntityManagerImpl entityManager, Object id, LockModeType lockMode) {
		final QueryImpl<X> q = entityManager.createQuery(this.getCriteriaSelect());

		q.setLockMode(lockMode);

		// if has single id then pass it on
		if (this.hasSingleIdAttribute()) {
			q.setParameter(1, id);
		}
		else {
			int i = 1;
			for (final Pair<SingularMapping<?, ?>, AbstractAccessor> pair : this.getIdMappings()) {
				q.setParameter(i++, pair.getSecond().get(id));
			}
		}

		return q.getSingleResult();
	}

	/**
	 * Performs the update for the instance.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance to perform update for
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since 2.0.0
	 */
	public void performUpdate(Connection connection, ManagedInstance<?> managedInstance) throws SQLException {
		FinalWrapper<EntityTable[]> wrapper = this.updateTables;

		final Object instance = managedInstance.getInstance();
		final Object oldVersion = managedInstance.getOldVersion();

		if (wrapper == null) {
			synchronized (this) {
				if (this.updateTables == null) {
					final List<EntityTable> _updateTables = Lists.newArrayList(this.getTables());
					for (final Iterator<EntityTable> i = _updateTables.iterator(); i.hasNext();) {
						if (!i.next().performUpdateWithUpdatability(connection, this, managedInstance.getInstance(), oldVersion)) {
							i.remove();
						}
					}

					this.updateTables = new FinalWrapper<EntityTable[]>(_updateTables.toArray(new EntityTable[_updateTables.size()]));
				}

				wrapper = this.updateTables;
			}
		}
		else {
			for (final EntityTable table : wrapper.value) {
				table.performUpdate(connection, this, instance, oldVersion);
			}
		}
	}

	/**
	 * Performs the version update for the instance.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param instance
	 *            the managed instance to perform update for
	 * @param oldVersion
	 *            the old version value
	 * @param newVersion
	 *            the new version value
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since 2.0.0
	 */
	public void performVersionUpdate(Connection connection, ManagedInstance<? extends X> instance, Object oldVersion, Object newVersion) throws SQLException {
		this.getTables()[0].performVersionUpdate(connection, instance.getInstance(), oldVersion, newVersion);
	}

	/**
	 * Prepares the dependencies for the associate.
	 * 
	 * @param associate
	 *            the associate
	 * 
	 * @since 2.0.0
	 */
	public void prepareDependenciesFor(EntityTypeImpl<?> associate) {
		// prepare the related associations
		final Set<AssociationMappingImpl<?, ?, ?>> attributes = Sets.newHashSet();

		for (final AssociationMappingImpl<?, ?, ?> association : this.getAssociations()) {

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

		final AssociationMappingImpl<?, ?, ?>[] dependencies = new AssociationMappingImpl[attributes.size()];
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
	 * @since 2.0.0
	 */
	public void prepareEagerJoins(FetchParent<?, ?> r, int depth, AssociationMappingImpl<?, ?, ?> parent) {
		if (depth < this.maxFetchJoinDepth) {
			this.prepareEagerJoins(r, depth, parent, this.entityMapping.getEagerMappings());
		}
	}

	private void prepareEagerJoins(FetchParent<?, ?> r, int depth, AssociationMappingImpl<?, ?, ?> parent, JoinedMapping<?, ?, ?>[] mappings) {
		for (final JoinedMapping<?, ?, ?> mapping : mappings) {
			// Element collection
			if (mapping.getMappingType() == MappingType.ELEMENT_COLLECTION) {
				r.fetch(mapping.getAttribute().getName(), JoinType.LEFT);
				continue;
			}
			// embeddable
			else if (mapping.getMappingType() == MappingType.EMBEDDABLE) {
				final Fetch<?, Object> r2 = r.fetch(mapping.getAttribute().getName(), JoinType.LEFT);

				this.prepareEagerJoins(r2, depth, parent, ((EmbeddedMappingImpl<?, ?>) mapping).getEagerMappings());

				continue;
			}
			// association
			else {
				final AssociationMappingImpl<?, ?, ?> association = (AssociationMappingImpl<?, ?, ?>) mapping;
				// if we are coming from the inverse side and inverse side is not many-to-one then skip
				if ((parent != null) && //
					(association.getInverse() == parent) && //
					(parent.getAttribute().getPersistentAttributeType() != PersistentAttributeType.MANY_TO_ONE)) {
					continue;
				}

				// check association's fetch strategy and max depth
				if ((association.getMaxFetchJoinDepth() < depth) || (association.getFetchStrategy() == FetchStrategyType.SELECT)) {
					continue;
				}

				final Fetch<?, Object> r2 = r.fetch(((AbstractMapping<?, ?, ?>) mapping).getAttribute().getName(), JoinType.LEFT);
				final EntityTypeImpl<?> type = association.getType();
				type.prepareEagerJoins(r2, depth + 1, association);
			}
		}
	}

	/**
	 * Runs the validators for the instance.
	 * 
	 * @param entityManagerFactory
	 *            the entity manager factory
	 * @param instance
	 *            the instance
	 * @return the set of validation errors
	 * 
	 * @since 2.0.0
	 */
	public Set<ConstraintViolation<Object>> runValidators(EntityManagerFactoryImpl entityManagerFactory, ManagedInstance<?> instance) {
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

		return validator.validate((Object) instance.getInstance(), groups);
	}

	/**
	 * Sets the id of the entity from the instance.
	 * 
	 * @param session
	 *            the session
	 * @param instance
	 *            the instance
	 * @param id
	 *            the id
	 * 
	 * @since 2.0.0
	 */
	public void setId(SessionImpl session, Object instance, Object id) {
		if (this.hasSingleIdAttribute()) {
			this.setIdImpl(session, instance, id, this.getIdMapping());
		}
		else {
			for (final Pair<SingularMapping<?, ?>, AbstractAccessor> pair : this.getIdMappings()) {
				final SingularMapping<?, ?> child = pair.getFirst();
				final AbstractAccessor accessor = pair.getSecond();

				final Object childId = id != null ? accessor.get(id) : null;

				this.setIdImpl(session, instance, childId, child);
			}
		}
	}

	private void setIdImpl(SessionImpl session, Object instance, Object id, SingularMapping<?, ?> idMapping) {
		// handle basic mapping
		if (idMapping instanceof BasicMappingImpl) {
			idMapping.set(instance, id);
		}

		// handle embedded id
		else if (idMapping instanceof EmbeddedMappingImpl) {
			final EmbeddedMappingImpl<?, ?> embeddedMapping = (EmbeddedMappingImpl<?, ?>) idMapping;

			if (id != null) {
				for (final Mapping<?, ?, ?> child : embeddedMapping.getChildren()) {
					final Object childId = ((AbstractMapping<?, ?, ?>) child).getAttribute().get(id);

					this.setIdImpl(session, instance, childId, (SingularMappingEx<?, ?>) child);
				}
			}
		}

		// handle singular associated
		else {
			final SingularAssociationMappingImpl<?, ?> singularAssociationMapping = (SingularAssociationMappingImpl<?, ?>) idMapping;
			final EntityTypeImpl<?> type = singularAssociationMapping.getType();

			if ((id == null) || type.getJavaType().isAssignableFrom(id.getClass())) {
				idMapping.set(instance, id);
			}
			else {
				idMapping.set(instance, session.getEntityManager().getReference(type.getJavaType(), id));
			}
		}
	}

	private synchronized void setInherited() {
		if (this.inheritanceType == null) {
			this.inheritanceType = InheritanceType.SINGLE_TABLE;

			if (this.discriminatorColumn == null) {
				this.discriminatorColumn = new DiscriminatorColumn(this.getMetamodel().getJdbcAdaptor(), this.primaryTable,
					this.metadata.getDiscriminatorColumn());
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
