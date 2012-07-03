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
package org.batoo.jpa.core.impl.model.mapping;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.PredicateImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.util.Pair;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * 
 * @param <Z>
 *            the source type
 * @param <E>
 *            the element type
 * @param <C>
 *            the collection type
 * 
 * @author hceylan
 * @since $version
 */
public class PluralAssociationMapping<Z, C, E> extends AssociationMapping<Z, C> {

	private final PluralAttributeImpl<? super Z, C, E> attribute;
	private final JoinTable joinTable;
	private EntityTypeImpl<E> type;
	private AssociationMapping<?, ?> inverse;
	private CriteriaQueryImpl<E> selectCriteria;

	/**
	 * @param parent
	 *            the parent mapping
	 * @param attribute
	 *            the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PluralAssociationMapping(ParentMapping<?, Z> parent, PluralAttributeImpl<? super Z, C, E> attribute) {
		super(parent, (AssociationAttributeMetadata) attribute.getMetadata(), attribute);

		this.attribute = attribute;

		final AssociationMetadata metadata = this.getAssociationMetadata();

		if (this.isOwner()) {
			if ((this.attribute.getPersistentAttributeType() == PersistentAttributeType.MANY_TO_MANY) || (metadata.getJoinColumns().size() == 0)) {
				this.joinTable = new JoinTable(this.getRoot().getType(), metadata.getJoinTable());
			}
			else {
				this.joinTable = null;
			}
		}
		else {
			this.joinTable = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void checkTransient(ManagedInstance<?> managedInstance) {
		final Object values = this.get(managedInstance.getInstance());

		final SessionImpl session = managedInstance.getSession();

		if (values instanceof Collection) {
			for (final E entity : (Collection<E>) values) {
				session.checkTransient(entity);
			}
		}
		else if (values instanceof Map) {
			for (final E entity : ((Map<?, E>) values).values()) {
				session.checkTransient(entity);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void flush(ConnectionImpl connection, ManagedInstance<?> managedInstance) throws SQLException {
		final Object values = this.get(managedInstance.getInstance());

		final SessionImpl session = managedInstance.getSession();

		if (values instanceof Collection) {
			for (final E entity : (Collection<E>) values) {
				this.getJoinTable().performInsert(session, connection, managedInstance.getInstance(), entity);
			}
		}
		else if (values instanceof Map) {
			for (final E entity : ((Map<?, E>) values).values()) {
				this.getJoinTable().performInsert(session, connection, managedInstance.getInstance(), entity);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PluralAttributeImpl<? super Z, C, E> getAttribute() {
		return this.attribute;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ForeignKey getForeignKey() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AssociationMapping<?, ?> getInverse() {
		return this.inverse;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinTable getJoinTable() {
		return this.joinTable;
	}

	@SuppressWarnings("unchecked")
	private CriteriaQueryImpl<E> getSelectCriteria() {
		if (this.selectCriteria != null) {
			return this.selectCriteria;
		}

		synchronized (this) {
			// other thread prepared before this one
			if (this.selectCriteria != null) {
				return this.selectCriteria;
			}

			if (this.isOwner()) {
				final MetamodelImpl metamodel = this.getRoot().getType().getMetamodel();
				final CriteriaBuilderImpl cb = metamodel.getEntityManagerFactory().getCriteriaBuilder();

				final EntityTypeImpl<E> entity = metamodel.entity(this.attribute.getBindableJavaType());

				CriteriaQueryImpl<E> q = cb.createQuery(this.attribute.getBindableJavaType());
				final RootImpl<?> r = q.from(this.getRoot().getType());
				q = q.select((Selection<? extends E>) r.<C> get(this.getPath()));

				entity.prepareEagerAssociations(r, 0, null);

				// has single id mapping
				final EntityTypeImpl<?> rootType = this.getRoot().getType();
				if (rootType.hasSingleIdAttribute()) {
					final SingularMapping<?, ?> idMapping = rootType.getIdMapping();
					final ParameterExpressionImpl<?> pe = cb.parameter(idMapping.getAttribute().getJavaType());
					final Path<?> path = r.get(idMapping.getAttribute().getName());
					final PredicateImpl predicate = cb.equal(path, pe);

					return this.selectCriteria = q.where(predicate);
				}

				// has multiple id mappings
				final List<PredicateImpl> predicates = Lists.newArrayList();
				for (final Pair<?, BasicAttribute<?, ?>> pair : rootType.getIdMappings()) {
					final BasicMapping<?, ?> idMapping = (BasicMapping<?, ?>) pair.getFirst();
					final ParameterExpressionImpl<?> pe = cb.parameter(idMapping.getAttribute().getJavaType());
					final Path<?> path = r.get(idMapping.getAttribute().getName());
					final PredicateImpl predicate = cb.equal(path, pe);

					predicates.add(predicate);
				}

				return this.selectCriteria = q.where(predicates.toArray(new PredicateImpl[predicates.size()]));
			}

			return this.selectCriteria = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<E> getType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void link() throws MappingException {
		final EntityTypeImpl<?> entity = this.getRoot().getType();
		entity.getMetamodel();

		this.type = (EntityTypeImpl<E>) this.attribute.getElementType();

		if (!this.isOwner()) {
			this.inverse = (AssociationMapping<?, ?>) this.type.getRootMapping().getMapping(this.getMappedBy());

			if (this.inverse == null) {
				throw new MappingException("Cannot find the mappedBy attribute " + this.getMappedBy() + " specified on " + this.attribute.getJavaMember());
			}

			this.inverse.setInverse(this);
		}
		else {
			// initialize the join table
			if (this.getJoinTable() != null) {
				this.getJoinTable().link(entity, this.type);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void load(ManagedInstance<?> instance) {
		// TODO Auto-generated method stub
	}

	/**
	 * Loads and returns the collection.
	 * 
	 * @param managedInstance
	 *            the managed instance owning the collection
	 * @return the loaded collection
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Collection<? extends E> loadCollection(ManagedInstance<?> managedInstance) {
		final EntityManagerImpl em = managedInstance.getSession().getEntityManager();
		final TypedQueryImpl<E> q = em.createQuery(this.getSelectCriteria());

		final EntityTypeImpl<?> rootType = managedInstance.getType();

		// if has single id then pass it on
		if (rootType.hasSingleIdAttribute()) {
			q.setParameter(1, managedInstance.getId());
		}
		else {
			int i = 1;
			final Object id = managedInstance.getId();
			for (final Pair<?, BasicAttribute<?, ?>> pair : rootType.getIdMappings()) {
				q.setParameter(i++, pair.getSecond().get(id));
			}
		}

		return q.getResultList();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean references(Object instance, Object reference) {
		final Object values = this.get(instance);

		if (values == null) {
			return false;
		}

		if (values instanceof Collection) {
			return ((Collection<?>) values).contains(reference);
		}

		return ((Map<?, ?>) values).containsValue(reference);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void set(ManagedInstance<?> managedInstance, Object instance, Object value) {
		C collection;

		if (value instanceof ManagedCollection) {
			collection = (C) value;
		}
		else {
			collection = this.attribute.newCollection(this, managedInstance, (Collection<? extends E>) value);
		}

		super.set(managedInstance, instance, collection);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setInverse(AssociationMapping<?, ?> inverse) {
		this.inverse = inverse;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setLazy(ManagedInstance<?> instance) {
		this.set(instance, this.attribute.newCollection(this, instance));
	}
}
