/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AssociatedSingularAttribute;
import org.batoo.jpa.jdbc.ForeignKey;
import org.batoo.jpa.jdbc.IdType;
import org.batoo.jpa.jdbc.JoinTable;
import org.batoo.jpa.jdbc.Joinable;
import org.batoo.jpa.jdbc.mapping.MappingType;
import org.batoo.jpa.jdbc.mapping.SingularAssociationMapping;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.AssociationMetadata;

/**
 * Mappings for one-to-one and many-to-one associations.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the destination type
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class SingularAssociationMappingImpl<Z, X> extends AssociationMappingImpl<Z, X, X> implements SingularAssociationMapping<Z, X>, SingularMappingEx<Z, X> {

	private final AssociatedSingularAttribute<? super Z, X> attribute;
	private final JoinTable joinTable;
	private final ForeignKey foreignKey;
	private EntityTypeImpl<X> type;
	private AssociationMappingImpl<?, ?, ?> inverse;

	/**
	 * @param parent
	 *            the parent mapping
	 * @param attribute
	 *            the attribute
	 * 
	 * @since 2.0.0
	 */
	public SingularAssociationMappingImpl(AbstractParentMapping<?, Z> parent, AssociatedSingularAttribute<? super Z, X> attribute) {
		super(parent, attribute.getMetadata(), attribute);

		this.attribute = attribute;

		final AssociationMetadata metadata = this.getAssociationMetadata();

		if (this.isOwner()) {
			if (metadata.getJoinTable() != null) {
				this.joinTable = new JoinTable(attribute.getMetamodel().getJdbcAdaptor(), (EntityTypeImpl<?>) attribute.getDeclaringType(), this,
					metadata.getJoinTable());
				this.foreignKey = null;
			}
			else {
				this.foreignKey = new ForeignKey(this.getAttribute().getMetamodel().getJdbcAdaptor(), this, metadata.getJoinColumns());
				this.joinTable = null;
			}
		}
		else {
			this.joinTable = null;
			this.foreignKey = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void checkTransient(ManagedInstance<?> managedInstance) {
		final X instance = this.get(managedInstance.getInstance());
		if (instance != null) {
			final Object instanceId = this.type.getInstanceId(instance);
			if (instanceId == null) {
				throw new PersistenceException("Instance " + instance + " is not managed");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object extractKey(Object value) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean fillValue(EntityTypeImpl<?> type, ManagedInstance<?> managedInstance, Object instance) {
		return false; // noop
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void flush(Connection connection, ManagedInstance<?> managedInstance, boolean removals, boolean force) throws SQLException {
		if (this.getJoinTable() != null) {
			if (!removals) {
				final X entity = this.get(managedInstance.getInstance());
				if (entity != null) {
					final Joinable[] batch = new Joinable[] { new Joinable(null, entity, 0) };
					this.joinTable.performInsert(connection, managedInstance.getInstance(), batch, 1);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AssociatedSingularAttribute<? super Z, X> getAttribute() {
		return this.attribute;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ForeignKey getForeignKey() {
		return this.foreignKey;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public IdType getIdType() {
		if (this.attribute.isId()) {
			return IdType.MANUAL;
		}

		final AbstractParentMapping<?, Z> parent = this.getParent();
		if (parent instanceof EmbeddedMappingImpl) {
			return ((EmbeddedMappingImpl<?, Z>) parent).getIdType();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AssociationMappingImpl<?, ?, ?> getInverse() {
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MappingType getMappingType() {
		return MappingType.SINGULAR_ASSOCIATION;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMapsId() {
		return this.attribute.getMapsId();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<X> getType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void initialize(ManagedInstance<?> managedInstance) {
		final EntityManagerImpl em = managedInstance.getSession().getEntityManager();
		final QueryImpl<X> q = em.createQuery(this.getSelectCriteria());

		q.setParameter(1, managedInstance.getInstance());

		try {
			final X child = q.getSingleResult();

			final Object instance = managedInstance.getInstance();

			this.set(instance, child);

			if (this.getInverse() != null) {
				final Object newParent = this.getInverse().get(child);
				if ((newParent == null) && (newParent != this)) {
					this.getInverse().set(child, instance);
				}
			}
		}
		catch (final NoResultException e) {}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isAssociation() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isId() {
		return this.getIdType() != null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isJoined() {
		return (this.joinTable != null) || (this.foreignKey != null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isMap() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void link() throws MappingException {
		final MetamodelImpl metamodel = this.getAttribute().getMetamodel();
		this.type = metamodel.entity(this.attribute.getBindableJavaType());

		if (!this.isOwner()) {
			this.inverse = (AssociationMappingImpl<?, ?, ?>) this.type.getRootMapping().getMapping(this.getMappedBy());

			if (this.inverse == null) {
				throw new MappingException("Cannot find the mappedBy attribute " + this.getMappedBy() + " specified on " + this.attribute.getJavaMember());
			}

			this.inverse.setInverse(this);
		}
		else {
			final EntityTypeImpl<?> entity = (EntityTypeImpl<?>) this.getRoot().getType();

			// initialize the join table
			if (this.joinTable != null) {
				this.joinTable.link(entity, this.type);
			}

			// initialize the foreign key
			else {
				this.foreignKey.link(this, this.type);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void mergeWith(EntityManagerImpl entityManager, ManagedInstance<?> instance, Object entity, MutableBoolean requiresFlush,
		IdentityHashMap<Object, Object> processed, LinkedList<ManagedInstance<?>> instances) {
		// get the new value as merged
		final X newEntity = entityManager.mergeImpl(this.get(entity), requiresFlush, processed, instances, this.cascadesMerge());

		// get the old value
		final X oldEntity = this.get(instance.getInstance());

		// if no change nothing to do here
		if (oldEntity == newEntity) {
			return;
		}

		// handle the remove orphans and inverse
		if ((oldEntity != null) && (this.removesOrphans() || (this.inverse != null))) {
			// handle orphan removal
			if (this.removesOrphans()) {
				entityManager.remove(oldEntity);
			}

			// update the other side of the relation
			if ((this.inverse != null) && (this.inverse.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_ONE)) {
				final ManagedInstance<X> oldInstance = instance.getSession().get(oldEntity);
				this.inverse.set(oldInstance.getInstance(), null);
			}
		}

		// set the new value
		this.set(instance.getInstance(), newEntity);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean references(Object instance, Object reference) {
		return this.get(instance) == reference;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refresh(ManagedInstance<?> instance, Set<Object> processed) {
		this.initialize(instance);

		if (this.cascadesRefresh()) {
			instance.getSession().getEntityManager().refreshImpl(instance.getInstance(), null, processed);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setInverse(AssociationMappingImpl<?, ?, ?> inverse) {
		this.inverse = inverse;
	}
}
