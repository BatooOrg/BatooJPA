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
package org.batoo.jpa.core.impl.criteria.join;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.Attribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping;

/**
 * Abstract implementation of joins.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the target type
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class AbstractJoin<Z, X> extends AbstractFrom<Z, X> implements Join<Z, X> {

	private final JoinedMapping<? super Z, ?, X> mapping;
	private final JoinType joinType;
	private final AbstractFrom<?, Z> parent;

	/**
	 * @param parent
	 *            the parent
	 * @param mapping
	 *            the mapping
	 * @param jointType
	 *            the join type
	 * 
	 * @since 2.0.0
	 */
	public AbstractJoin(AbstractFrom<?, Z> parent, JoinedMapping<? super Z, ?, X> mapping, JoinType jointType) {
		super(parent, mapping.getType(), mapping, jointType);

		this.parent = parent;
		this.mapping = mapping;
		this.joinType = jointType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlJoins(AbstractCriteriaQueryImpl<?> criteriaQuery) {
		this.ensureAlias(criteriaQuery);

		final StringBuilder builder = new StringBuilder();

		builder.append(this.joinType == JoinType.LEFT ? "left" : "inner");
		builder.append(" join ") //
		.append(this.getParent().getAlias()).append(".").append(this.mapping.getAttribute().getName()) //
		.append(" as ").append(this.getAlias());

		final String joins = super.generateJpqlJoins(criteriaQuery);
		if (StringUtils.isNotBlank(joins)) {
			builder.append("\n").append(joins);
		}

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		super.generateJpqlSelect(query, selected);

		final StringBuilder builder = new StringBuilder();

		builder.append(this.getParent().generateJpqlSelect(query, false));
		builder.append(".").append(this.mapping.getAttribute().getName());
		if (StringUtils.isNotBlank(this.getAlias())) {
			builder.append(" as ").append(this.getAlias());
		}

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Attribute<? super Z, ?> getAttribute() {
		return this.mapping.getAttribute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinType getJoinType() {
		return this.joinType;
	}

	/**
	 * Returns the mapping.
	 * 
	 * @return the mapping
	 * 
	 * @since 2.0.0
	 */
	protected JoinedMapping<? super Z, ?, X> getMapping() {
		return this.mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractFrom<?, Z> getParent() {
		return this.parent;
	}

	/**
	 * Returns a new {@link UnsupportedOperationException}.
	 * 
	 * @return the exception
	 * 
	 * @since 2.0.0
	 */
	protected UnsupportedOperationException notSupported() {
		return new UnsupportedOperationException("on() operations not supported");
	}
}
