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
package org.batoo.jpa.core.impl.criteria.path;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.criteria.Path;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.StaticTypeExpression;
import org.batoo.jpa.core.impl.criteria.join.FetchParentImpl;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.jdbc.JoinColumn;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.EmbeddedAttribute;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Physical Attribute implementation of {@link Path}.
 * 
 * @param <Z>
 *            the type of the parent path
 * @param <X>
 *            the type referenced by the path
 * 
 * @author hceylan
 * @since $version
 */
public class EmbeddedAttributePath<Z, X> extends ParentPath<Z, X> {

	private final EmbeddedMapping<? super Z, X> mapping;
	private final FetchParentImpl<Z, X> fetchRoot;

	/**
	 * @param parent
	 *            the parent path
	 * @param mapping
	 *            the embedded mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddedAttributePath(ParentPath<?, Z> parent, EmbeddedMapping<? super Z, X> mapping) {
		super(parent, mapping.getType().getJavaType());

		this.fetchRoot = parent.getFetchRoot().fetch(mapping.getAttribute());
		this.mapping = mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.getParentPath().generateJpqlRestriction(query));

		builder.append(".").append(this.mapping.getAttribute().getName());

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.getParentPath().generateJpqlSelect(query, false));

		builder.append(".").append(this.mapping.getAttribute().getName());
		if (selected && StringUtils.isNotBlank(this.getAlias())) {
			builder.append(" as ").append(this.getAlias());
		}

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		final List<String> fragments = Lists.newArrayList();

		this.generateSqlSelect(query, fragments, this.mapping.getSingularMappings());

		return Joiner.on(", ").join(fragments);
	}

	private void generateSqlSelect(AbstractCriteriaQueryImpl<?> query, final List<String> fragments, Mapping<?, ?, ?>[] mappings) {
		for (final Mapping<?, ?, ?> mapping : mappings) {
			if (mapping instanceof BasicMapping) {
				final BasicColumn column = ((BasicMapping<?, ?>) mapping).getColumn();

				final String tableAlias = this.getRootPath().getTableAlias(query, column.getTable());
				fragments.add(tableAlias + "." + column.getName());
			}

			if (mapping instanceof SingularAssociationMapping) {
				final SingularAssociationMapping<?, ?> associationMapping = (SingularAssociationMapping<?, ?>) mapping;
				for (final JoinColumn column : associationMapping.getForeignKey().getJoinColumns()) {
					final String tableAlias = this.getRootPath().getTableAlias(query, column.getTable());
					fragments.add(tableAlias + "." + column.getName());
				}
			}

			if (mapping instanceof EmbeddedMapping) {
				final EmbeddedMapping<?, ?> embeddedMapping = (EmbeddedMapping<?, ?>) mapping;
				this.generateSqlSelect(query, fragments, embeddedMapping.getSingularMappings());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FetchParentImpl<?, X> getFetchRoot() {
		return this.fetchRoot;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected <C, Y> Mapping<? super X, C, Y> getMapping(String name) {
		final Mapping<? super X, ?, ?> child = this.mapping.getChild(name);

		if (child == null) {
			throw this.cannotDereference(name);
		}

		return (Mapping<? super X, C, Y>) child;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EmbeddedAttribute<?, X> getModel() {
		return this.mapping.getAttribute();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		final List<String> restrictions = Lists.newArrayList();

		final Joinable rootPath = this.getRootPath();

		final List<BasicMapping<?, ?>> mappings = Lists.newArrayList();

		for (final Mapping<? super X, ?, ?> mapping : this.mapping.getChildren()) {
			if (mapping instanceof BasicMapping) {
				mappings.add((BasicMapping<?, ?>) mapping);
			}
		}

		Collections.sort(mappings, new Comparator<BasicMapping<?, ?>>() {

			@Override
			public int compare(BasicMapping<?, ?> o1, BasicMapping<?, ?> o2) {
				return o1.getAttribute().getAttributeId().compareTo(o2.getAttribute().getAttributeId());
			}
		});

		for (final BasicMapping<?, ?> mapping : mappings) {
			final BasicColumn column = mapping.getColumn();
			restrictions.add(rootPath.getTableAlias(query, column.getTable()) + "." + column.getName());
		}

		return restrictions.toArray(new String[restrictions.size()]);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public X handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		final Object instance = this.getParentPath().handle(query, session, row);

		return this.mapping.get(instance);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public StaticTypeExpression<X> type() {
		return new StaticTypeExpression<X>(this, this.getModel().getJavaType());
	}
}
