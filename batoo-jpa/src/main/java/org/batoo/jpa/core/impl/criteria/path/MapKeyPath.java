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

package org.batoo.jpa.core.impl.criteria.path;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.criteria.Expression;
import javax.persistence.metamodel.Type.PersistenceType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.StaticTypeExpression;
import org.batoo.jpa.core.impl.criteria.join.FetchParentImpl;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl.MapSelectType;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.attribute.MapAttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.model.mapping.ElementMappingImpl;

/**
 * Path for Map join keys.
 * 
 * @param <Z>
 *            the type of the parent path
 * @param <X>
 *            the type of the key
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class MapKeyPath<Z, X> extends ParentPath<Z, X> {

	private final MapJoinImpl<?, X, ?> mapJoin;
	private ElementMappingImpl<X> elementMappingImpl;

	/**
	 * @param mapJoin
	 *            the map parent
	 * @param javaType
	 *            the java type of the key
	 * 
	 * @since 2.0.0
	 */
	public MapKeyPath(MapJoinImpl<Z, X, ?> mapJoin, Class<X> javaType) {
		super(mapJoin.getParent(), javaType);

		this.mapJoin = mapJoin;
		if (this.mapJoin.getModel().getKeyType().getPersistenceType() == PersistenceType.EMBEDDABLE) {
			this.elementMappingImpl = new ElementMappingImpl<X>(null, (EmbeddableTypeImpl<X>) this.mapJoin.getModel().getKeyType());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlRestriction(BaseQueryImpl<?> query) {
		return "key(" + this.mapJoin.generateJpqlRestriction(query) + ")";
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		if (StringUtils.isNotBlank(this.getAlias())) {
			return this.generateJpqlRestriction(query) + " as " + this.getAlias();
		}

		return this.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		return this.mapJoin.generateSqlSelect(query, selected, MapSelectType.KEY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FetchParentImpl<?, X> getFetchRoot() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <C, Y> AbstractMapping<? super X, C, Y> getMapping(String name) {
		if (this.mapJoin.getModel().getKeyType() instanceof EmbeddableTypeImpl) {
			final AbstractMapping<? super X, ?, ?> child = this.elementMappingImpl.getChild(name);

			if (child != null) {
				return (AbstractMapping<? super X, C, Y>) child;
			}
		}

		throw this.cannotDereference(name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public MapAttributeImpl<?, ?, X> getModel() {
		return (MapAttributeImpl<?, ?, X>) this.mapJoin.getModel();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		return this.mapJoin.getSqlRestrictionFragments(query, MapSelectType.KEY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public X handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		final X value = (X) this.mapJoin.handle(session, row, MapSelectType.KEY);

		return (X) (this.getConverter() != null ? this.getConverter().convert(value) : value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Expression<Class<? extends X>> type() {
		return new StaticTypeExpression<X>(this, this.mapJoin.getModel().getKeyJavaType());
	}
}
