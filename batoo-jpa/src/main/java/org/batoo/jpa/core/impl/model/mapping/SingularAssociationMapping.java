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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.IdentityHashMap;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.jdbc.Joinable;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AssociatedSingularAttribute;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.util.Pair;
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
 * @since $version
 */
public class SingularAssociationMapping<Z, X> extends AssociationMapping<Z, X, X> {

	private final AssociatedSingularAttribute<? super Z, X> attribute;
	private final JoinTable joinTable;
	private final ForeignKey foreignKey;
	private EntityTypeImpl<X> type;
	private AssociationMapping<?, ?, ?> inverse;

	/**
	 * @param parent
	 *            the parent mapping
	 * @param attribute
	 *            the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SingularAssociationMapping(ParentMapping<?, Z> parent, AssociatedSingularAttribute<? super Z, X> attribute) {
		super(parent, attribute.getMetadata(), attribute);

		this.attribute = attribute;

		final AssociationMetadata metadata = this.getAssociationMetadata();

		if (this.isOwner()) {
			if (metadata.getJoinTable() != null) {
				this.joinTable = new JoinTable((EntityTypeImpl<?>) this.getRoot().getType(), metadata.getJoinTable());
				this.foreignKey = null;
			}
			else {
				this.foreignKey = new ForeignKey(this.getAttribute().getMetamodel().getJdbcAdaptor(), metadata.getJoinColumns());
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
	public void flush(Connection connection, ManagedInstance<?> managedInstance, boolean removals, boolean force) throws SQLException {
		if (this.getTable() != null) {
			if (!removals) {
				final X entity = this.get(managedInstance.getInstance());
				final Joinable[] batch = new Joinable[] { new Joinable(null, entity, 0) };
				this.joinTable.performInsert(connection, managedInstance.getInstance(), batch, 1);
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
	public AssociationMapping<?, ?, ?> getInverse() {
		return this.inverse;
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
	public JoinTable getTable() {
		return this.joinTable;
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

		final EntityTypeImpl<?> rootType = managedInstance.getType();

		final Object id = managedInstance.getId().getId();

		// if has single id then pass it on
		if (rootType.hasSingleIdAttribute()) {
			q.setParameter(0, id);
		}
		else {
			int i = 0;
			for (final Pair<?, BasicAttribute<?, ?>> pair : rootType.getIdMappings()) {
				q.setParameter(i++, pair.getSecond().get(id));
			}
		}

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
		this.type = metamodel.entity(this.attribute.getJavaType());

		if (!this.isOwner()) {
			this.inverse = (AssociationMapping<?, ?, ?>) this.type.getRootMapping().getMapping(this.getMappedBy());

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
		IdentityHashMap<Object, Object> processed) {
		// get the new value as merged
		final X newEntity = entityManager.mergeImpl(this.get(entity), requiresFlush, processed, this.cascadesMerge());

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
	public void setInverse(AssociationMapping<?, ?, ?> inverse) {
		this.inverse = inverse;
	}
}
