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

package org.batoo.jpa.core.impl.criteria.join;

import java.util.List;

import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping;
import org.batoo.jpa.jdbc.mapping.MappingType;

/**
 * Implementation of {@link Fetch}.
 * 
 * @param <Z>
 *            the source type of the fetch
 * @param <X>
 *            the target type of the fetch
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class FetchImpl<Z, X> extends FetchParentImpl<Z, X> implements Fetch<Z, X> {

	private final FetchParentImpl<?, Z> parent;
	private final JoinedMapping<? super Z, ?, X> mapping;
	private final JoinType joinType;

	/**
	 * @param parent
	 *            the parent of the join
	 * @param mapping
	 *            the mapping of the join
	 * @param joinType
	 *            the join type
	 * 
	 * @since 2.0.0
	 */
	public FetchImpl(FetchParentImpl<?, Z> parent, JoinedMapping<? super Z, ?, X> mapping, JoinType joinType) {
		super(mapping);

		this.parent = parent;
		this.mapping = mapping;
		this.joinType = joinType;
	}

	/**
	 * Returns the description of the fetch.
	 * 
	 * @param parent
	 *            the parent
	 * @return the description of the fetch
	 * 
	 * @since 2.0.0
	 */
	@Override
	public String generateJpqlFetches(String parent) {
		final StringBuilder builder = new StringBuilder();

		builder.append("left join fetch ");

		builder.append(parent).append(".").append(this.mapping.getAttribute().getName());

		final String children = super.generateJpqlFetches(parent + "." + this.mapping.getAttribute().getName());
		if (StringUtils.isNotBlank(children)) {
			builder.append("\n").append(children);
		}

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void generateSqlJoins(AbstractCriteriaQueryImpl<?> query, List<String> selfJoins) {
		final String parentAlias = this.getParent().getPrimaryTableAlias(query);
		final String alias = this.getPrimaryTableAlias(query);

		selfJoins.add(this.mapping.join(parentAlias, alias, this.joinType));

		super.generateSqlJoins(query, selfJoins);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AttributeImpl<? super Z, ?> getAttribute() {
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinedMapping<? super Z, ?, X> getMapping() {
		return this.mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FetchParentImpl<?, Z> getParent() {
		return this.parent;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getPrimaryTableAlias(BaseQueryImpl<?> query) {
		if (this.mapping.getMappingType() == MappingType.EMBEDDABLE) {
			return this.parent.getPrimaryTableAlias(query);
		}

		return super.getPrimaryTableAlias(query);
	}
}
