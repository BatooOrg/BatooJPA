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
package org.batoo.jpa.core.impl.model.mapping;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.batoo.common.util.BatooUtils;
import org.batoo.jpa.annotations.FetchStrategyType;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.expression.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.expression.PredicateImpl;
import org.batoo.jpa.core.impl.criteria.join.AbstractJoin;
import org.batoo.jpa.core.impl.criteria.path.AbstractPath;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.SingularAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.type.MappedSuperclassTypeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.MappableAssociationAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.OrphanableAssociationAttributeMetadata;

import com.google.common.base.Splitter;

/**
 * Mapping for associations.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * @param <Y>
 *            the attribute type
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AssociationMapping<Z, X, Y> extends Mapping<Z, X, Y> implements JoinedMapping<Z, X, Y> {

	private final boolean eager;
	private final boolean cascadesDetach;
	private final boolean cascadesMerge;
	private final boolean cascadesPersist;
	private final boolean cascadesRefresh;
	private final boolean cascadesRemove;
	private final String mappedBy;

	private final boolean removesOrphans;
	private final int maxFetchDepth;

	private final FetchStrategyType fetchStrategy;

	private CriteriaQueryImpl<Y> selectCriteria;
	private boolean ownerSelect;

	/**
	 * @param parent
	 *            the parent mapping
	 * @param metadata
	 *            the metadata
	 * @param attribute
	 *            the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationMapping(ParentMapping<?, Z> parent, AssociationAttributeMetadata metadata, AttributeImpl<? super Z, X> attribute) {
		super(parent, attribute, attribute.getJavaType(), attribute.getName());

		if ((metadata instanceof MappableAssociationAttributeMetadata)
			&& StringUtils.isNotBlank(((MappableAssociationAttributeMetadata) metadata).getMappedBy())) {
			this.mappedBy = ((MappableAssociationAttributeMetadata) metadata).getMappedBy();
		}
		else {
			this.mappedBy = null;
		}

		this.eager = attribute.isCollection() || (this.mappedBy == null) ? metadata.getFetchType() == FetchType.EAGER : true;

		this.maxFetchDepth = metadata.getMaxFetchDepth();
		this.fetchStrategy = metadata.getFetchStrategy();

		if (metadata instanceof OrphanableAssociationAttributeMetadata) {
			this.removesOrphans = ((OrphanableAssociationAttributeMetadata) metadata).removesOrphans();
		}
		else {
			this.removesOrphans = false;
		}

		this.cascadesDetach = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.DETACH);
		this.cascadesMerge = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.MERGE);
		this.cascadesPersist = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.PERSIST);
		this.cascadesRefresh = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.REFRESH);
		this.cascadesRemove = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.REMOVE);
	}

	/**
	 * Returns if the type cascades detach operations.
	 * 
	 * @return true if the type cascades detach operations, false otherwise.
	 * @since $version
	 */
	public final boolean cascadesDetach() {
		return this.cascadesDetach;
	}

	/**
	 * Returns if the type cascades merge operations.
	 * 
	 * @return true if the type cascades merge operations, false otherwise.
	 * @since $version
	 */
	public final boolean cascadesMerge() {
		return this.cascadesMerge;
	}

	/**
	 * Returns if the type cascades persist operations.
	 * 
	 * @return true if the type cascades persist operations, false otherwise.
	 * @since $version
	 */
	public final boolean cascadesPersist() {
		return this.cascadesPersist;
	}

	/**
	 * Returns if the type cascades refresh operations.
	 * 
	 * @return true if the type cascades refresh operations, false otherwise.
	 * @since $version
	 */
	public final boolean cascadesRefresh() {
		return this.cascadesRefresh;
	}

	/**
	 * Returns if the type cascades remove operations.
	 * 
	 * @return true if the type cascades remove operations, false otherwise.
	 * @since $version
	 */
	public final boolean cascadesRemove() {
		return this.cascadesRemove;
	}

	/**
	 * Checks that the association references not a transient instance
	 * 
	 * @param managedInstance
	 *            the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void checkTransient(ManagedInstance<?> managedInstance);

	private CriteriaQueryImpl<Y> generateMappedSelectCriteria(MetamodelImpl metamodel, CriteriaBuilderImpl cb, Class<Y> bindableType, EntityTypeImpl<Y> entity) {
		final CriteriaQueryImpl<Y> q = cb.createQuery(bindableType);
		q.internal();

		final RootImpl<Y> r = q.from(entity);
		r.alias(BatooUtils.acronym(entity.getName()).toLowerCase());

		q.select(r);

		final Iterator<String> pathIterator = Splitter.on(".").split(this.getInverse().getPath()).iterator();

		// Drop the root part
		pathIterator.next();

		AbstractPath<?> path = null;
		while (pathIterator.hasNext()) {
			path = path == null ? r.get(pathIterator.next()) : path.get(pathIterator.next());
		}

		entity.prepareEagerJoins(r, 0, this);

		final ParameterExpressionImpl<?> pe = cb.parameter(this.getInverse().getJavaType());
		final PredicateImpl predicate = cb.equal(path, pe);
		return this.selectCriteria = q.where(predicate);
	}

	@SuppressWarnings("unchecked")
	private CriteriaQueryImpl<Y> generateOwnerSelectCriteria(final MetamodelImpl metamodel, final CriteriaBuilderImpl cb, Class<Y> bindableType,
		EntityTypeImpl<Y> entity) {
		final CriteriaQueryImpl<Y> q = cb.createQuery(bindableType);
		q.internal();

		final EntityTypeImpl<?> type = (EntityTypeImpl<?>) this.getRoot().getType();

		final RootImpl<?> r = q.from(type);
		r.alias(BatooUtils.acronym(type.getName()).toLowerCase());

		final Iterator<String> pathIterator = Splitter.on(".").split(this.getPath()).iterator();

		// Drop the root part
		pathIterator.next();

		AbstractJoin<?, ?> join = null;
		while (pathIterator.hasNext()) {
			join = join == null ? r.<Y> join(pathIterator.next()) : join.join(pathIterator.next());
		}

		q.select((Selection<? extends Y>) join);

		entity.prepareEagerJoins(join, 0, this);

		return this.selectCriteria = q.where(cb.equal(r, cb.parameter(type.getJavaType())));
	}

	/**
	 * Returns the effective association metadata for the attribute checking with the parent mappings and entities.
	 * 
	 * @return the column metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	protected AssociationMetadata getAssociationMetadata() {
		AssociationMetadata metadata = null;

		final String path = this.getParent().getRootPath(this.getAttribute().getName());
		final AttributeImpl<?, ?> rootAttribute = this.getParent().getRootAttribute(this.getAttribute());

		/**
		 * The priorities are like below:
		 * 
		 * 1. If the root attribute is defined in the root type (thus the entity) then locate the association override on the attribute
		 * chain<br />
		 * 2. If the root attribute is defined in a parent mapped super class then locate the association on the entity<br />
		 * 3. If the parent is an embeddable mapping then locate the attribute override again on the association chain<br />
		 * 4. return the association metadata from the attribute<br />
		 */

		// Clause 1
		if ((rootAttribute.getDeclaringType() == this.getRoot().getType()) && (this.getParent() instanceof EmbeddedMapping)) {
			metadata = ((EmbeddedMapping<?, ?>) this.getParent()).getAssociationOverride(path);
			if (metadata != null) {
				return metadata;
			}
		}

		// Clause 2
		if (rootAttribute.getDeclaringType() instanceof MappedSuperclassTypeImpl) {
			metadata = ((EntityTypeImpl<Y>) this.getRoot().getType()).getAssociationOverride(path);
			if (metadata != null) {
				return metadata;
			}
		}

		// Clause 3
		if (this.getParent() instanceof EmbeddedMapping) {
			metadata = ((EmbeddedMapping<?, ?>) this.getParent()).getAssociationOverride(path);
			if (metadata != null) {
				return metadata;
			}
		}

		// Clause 4: fall back to attribute's column metadata
		return (AssociationMetadata) this.getAttribute().getMetadata();
	}

	/**
	 * Returns the Fetching strategy of the association
	 * 
	 * @return the Fetching strategy of the association
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	public FetchStrategyType getFetchStrategy() {
		return this.fetchStrategy;
	}

	/**
	 * Returns the foreign key of the mapping.
	 * 
	 * @return the foreign key of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract ForeignKey getForeignKey();

	/**
	 * Returns the inverse attribute.
	 * 
	 * @return the inverse attribute or null
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract AssociationMapping<?, ?, ?> getInverse();

	/**
	 * Returns the mappedBy of the mapping.
	 * 
	 * @return the mappedBy of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getMappedBy() {
		return this.mappedBy;
	}

	/**
	 * Returns the max allowed depth for the fetch join.
	 * 
	 * @return the max allowed depth for the fetch join
	 * 
	 * @since $version
	 * @author asimarslan
	 */
	public int getMaxFetchJoinDepth() {
		return this.maxFetchDepth;
	}

	/**
	 * Returns the select criteria.
	 * 
	 * @return the select criteria
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected CriteriaQueryImpl<Y> getSelectCriteria() {
		if (this.selectCriteria != null) {
			return this.selectCriteria;
		}

		synchronized (this) {
			// other thread prepared before this one
			if (this.selectCriteria != null) {
				return this.selectCriteria;
			}

			final MetamodelImpl metamodel = this.getRoot().getType().getMetamodel();
			final CriteriaBuilderImpl cb = metamodel.getEntityManagerFactory().getCriteriaBuilder();

			@SuppressWarnings("unchecked")
			final Class<Y> bindableType = (Class<Y>) (this.getAttribute() instanceof PluralAttributeImpl
				? ((PluralAttributeImpl<?, ?, ?>) this.getAttribute()).getBindableJavaType()
				: ((SingularAttributeImpl<?, ?>) this.getAttribute()).getBindableJavaType());

			final EntityTypeImpl<Y> entity = metamodel.entity(bindableType);

			this.ownerSelect = this.isOwner() || (this.getAttribute().getPersistentAttributeType() == PersistentAttributeType.MANY_TO_MANY);
			if (this.ownerSelect) {
				return this.generateOwnerSelectCriteria(metamodel, cb, bindableType, entity);
			}
			else {
				return this.generateMappedSelectCriteria(metamodel, cb, bindableType, entity);
			}
		}
	}

	/**
	 * Returns the join table of the mapping.
	 * 
	 * @return the join table of the mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	public abstract JoinTable getTable();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public abstract EntityTypeImpl<Y> getType();

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean isEager() {
		return this.eager;
	}

	/**
	 * Returns if the mapping is an id mapping.
	 * 
	 * @return true if the mapping is an id mapping, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isId() {
		return this.getIdType() != null;
	}

	/**
	 * Returns if the association is the owner side.
	 * 
	 * @return true if the association is the owner side
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final boolean isOwner() {
		return this.mappedBy == null;
	}

	/**
	 * Returns if the selection is owned.
	 * 
	 * @return true if the selection is owned, false otherwise
	 * 
	 * @since $version
	 */
	protected boolean isOwnerSelect() {
		return this.ownerSelect;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String join(String parentAlias, String alias, JoinType joinType) {
		if (this.getForeignKey() != null) {
			return this.getForeignKey().createDestinationJoin(joinType, parentAlias, alias);
		}
		else if ((this.getInverse() != null) && (this.getInverse().getForeignKey() != null)) {
			return this.getInverse().getForeignKey().createSourceJoin(joinType, parentAlias, alias);
		}
		else if (this.getTable() != null) {
			return this.getTable().createJoin(joinType, parentAlias, alias, true);
		}
		else {
			return this.getInverse().getTable().createJoin(joinType, parentAlias, alias, false);
		}
	}

	/**
	 * Links the attribute to its associate entity type and inverse attribute if bidirectional.
	 * 
	 * @throws MappingException
	 *             thrown in case of a linkage error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void link() throws MappingException;

	/**
	 * Merges the association of the instance with the entity.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @param instance
	 *            the instance
	 * @param entity
	 *            the entity
	 * @param requiresFlush
	 *            if an implicit flush is required
	 * @param processed
	 *            registry of processed entities
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void mergeWith(EntityManagerImpl entityManager, ManagedInstance<?> instance, Object entity, MutableBoolean requiresFlush,
		IdentityHashMap<Object, Object> processed);

	/**
	 * @param instance
	 *            the source instance
	 * @param reference
	 *            the associate instance
	 * @return true if source contains reference to the associate, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract boolean references(Object instance, Object reference);

	/**
	 * Refreshes the association
	 * 
	 * @param instance
	 *            the instance that is the owner of the association
	 * @param processed
	 *            the set of processed instances
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void refresh(ManagedInstance<?> instance, Set<Object> processed);

	/**
	 * Returns the if the mapping removes orphans.
	 * 
	 * @return true if the mapping removes orphans, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean removesOrphans() {
		return this.removesOrphans;
	}

	/**
	 * Sets the inverse attribute.
	 * 
	 * @param inverse
	 *            the inverse association
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void setInverse(AssociationMapping<?, ?, ?> inverse);
}
