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
import java.util.List;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.AbstractTypeExpression;
import org.batoo.jpa.core.impl.criteria.expression.EntityTypeExpression;
import org.batoo.jpa.core.impl.criteria.expression.StaticTypeExpression;
import org.batoo.jpa.core.impl.criteria.join.AbstractFrom;
import org.batoo.jpa.core.impl.criteria.join.FetchImpl;
import org.batoo.jpa.core.impl.criteria.join.FetchParentImpl;
import org.batoo.jpa.core.impl.criteria.join.Joinable;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl.MapSelectType;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

import com.google.common.collect.Lists;

/**
 * Entity type attribute implementation of {@link Path}.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the target type
 * 
 * @author hceylan
 * @since $version
 */
public class EntityPath<Z, X> extends ParentPath<Z, X> implements Joinable {

	private final SingularAssociationMapping<?, Z> mapping;
	private final String pathName;
	private final EntityTypeImpl<X> entity;

	private FetchImpl<Z, X> fetchRoot;

	/**
	 * @param parent
	 *            the parent path
	 * @param mapping
	 *            the path name
	 * @param entity
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public EntityPath(ParentPath<?, Z> parent, SingularAssociationMapping<?, Z> mapping, EntityTypeImpl<X> entity) {
		super(parent, entity.getJavaType());

		this.mapping = mapping;
		this.pathName = mapping.getAttribute().getName();
		this.entity = entity;

		if (!this.mapping.isOwner() || (this.mapping.getForeignKey() == null)) {
			this.fetchRoot = (FetchImpl<Z, X>) this.getParentPath().getFetchRoot().join(this.pathName, JoinType.LEFT);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		final StringBuilder builder = new StringBuilder();

		builder.append(this.getParentPath().generateJpqlRestriction(query));

		builder.append(".").append(this.pathName);

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		final StringBuilder builder = new StringBuilder();

		if ((this.getParentPath() instanceof AbstractFrom) && StringUtils.isNotBlank(this.getParentPath().getAlias())) {
			builder.append(this.getParentPath().getAlias());
		}
		else {
			builder.append(this.getParentPath().generateJpqlSelect(null, false));
		}

		builder.append(".").append(this.pathName);
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
		return this.getFetchRoot().generateSqlSelect(query, selected, false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<?> getEntity() {
		return this.entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public FetchParentImpl<?, X> getFetchRoot() {
		if (this.fetchRoot == null) {
			this.fetchRoot = (FetchImpl<Z, X>) this.getParentPath().getFetchRoot().join(this.pathName, JoinType.LEFT);
		}

		return this.fetchRoot;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected <C, Y> Mapping<? super X, C, Y> getMapping(String name) {
		final Mapping<? super X, C, Y> mapping = (Mapping<? super X, C, Y>) this.entity.getRootMapping().getChild(name);

		if (mapping == null) {
			throw this.cannotDereference(name);
		}

		return mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<X> getModel() {
		return this.entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		if (!this.mapping.isOwner() || (this.mapping.getForeignKey() == null)) {
			return this.getFetchRoot().getSqlRestrictionFragments(query, MapSelectType.VALUE);
		}

		final List<String> restrictions = Lists.newArrayList();

		final ForeignKey foreignKey = this.mapping.getForeignKey();
		final String tableAlias = this.getParentPath().getFetchRoot().getTableAlias(query, foreignKey.getTable());

		for (final AbstractColumn column : foreignKey.getJoinColumns()) {
			restrictions.add(tableAlias + "." + column.getName());
		}

		return restrictions.toArray(new String[restrictions.size()]);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getTableAlias(BaseQueryImpl<?> query, AbstractTable table) {
		return this.getFetchRoot().getTableAlias(query, table);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public X handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		return this.fetchRoot.handle(session, row);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractTypeExpression<X> type() {
		if (this.entity.getRootType().getInheritanceType() != null) {
			return new EntityTypeExpression<X>(this, this.entity.getRootType().getDiscriminatorColumn());
		}

		return new StaticTypeExpression<X>(this, this.getModel().getBindableJavaType());
	}
}
