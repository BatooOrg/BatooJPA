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

import java.util.Collection;
import java.util.Map;

import javax.persistence.criteria.Path;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.PluralAttribute.CollectionType;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.expression.CollectionExpression;
import org.batoo.jpa.core.impl.criteria.expression.MapExpression;
import org.batoo.jpa.core.impl.criteria.join.FetchParentImpl;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.MapAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.SingularAttributeImpl;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

import com.google.common.collect.Maps;

/**
 * Abstract implementation of {@link Path}.
 * 
 * @param <Z>
 *            the type of the parent path
 * @param <X>
 *            the type referenced by the path
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class ParentPath<Z, X> extends AbstractPath<X> implements Path<X> {

	final Map<String, AbstractExpression<?>> children = Maps.newHashMap();

	/**
	 * @param parent
	 *            the parent path, may be null
	 * @param javaType
	 *            the java type
	 * 
	 * @since 2.0.0
	 */
	public ParentPath(ParentPath<?, Z> parent, Class<X> javaType) {
		super(parent, javaType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <K, V, M extends Map<K, V>> MapExpression<M, K, V> get(MapAttribute<? super X, K, V> map) {
		return new MapExpression<M, K, V>(this.<Map<K, V>, V> getMapping(map.getName()));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <E, C extends Collection<E>> CollectionExpression<C, E> get(PluralAttribute<? super X, C, E> collection) {
		return new CollectionExpression<C, E>(this, this.<Collection<E>, E> getMapping(collection.getName()));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final <Y> AbstractPath<Y> get(SingularAttribute<? super X, Y> attribute) {
		final Mapping<? super X, Y, Y> mapping = this.getMapping(attribute.getName());

		switch (attribute.getPersistentAttributeType()) {
			case EMBEDDED:
				return new EmbeddedAttributePath<X, Y>(this, (EmbeddedMapping<? super X, Y>) mapping);
			case BASIC:
				return new BasicPath<Y>(this, (BasicMapping<? super X, Y>) mapping);
			default:
				return new EntityPath<X, Y>(this, (SingularAssociationMapping<Z, X>) mapping, (EntityTypeImpl<Y>) attribute.getType());
		}

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final <Y> AbstractPath<Y> get(String name) {
		// try to resolve from existing children
		final AbstractExpression<Y> path = (AbstractExpression<Y>) this.children.get(name);
		if (path != null) {
			return (AbstractPath<Y>) path;
		}

		final Mapping<? super X, ?, Y> mapping = this.getMapping(name);
		final AttributeImpl<? super X, Y> attribute = (AttributeImpl<? super X, Y>) mapping.getAttribute();

		if (attribute.isCollection()) {
			throw new IllegalArgumentException("Cannot deference a plural attribute as path: " + name);
		}

		return this.get((SingularAttribute<? super X, Y>) attribute);
	}

	/**
	 * Returns the table alias for the column.
	 * 
	 * @param column
	 *            the column
	 * @param query
	 *            the query
	 * @return the table alias for the column
	 * 
	 * @since 2.0.0
	 */
	public String getColumnAlias(BaseQueryImpl<?> query, AbstractColumn column) {
		return null;
	}

	/**
	 * Returns the expression corresponding to the attribute name.
	 * 
	 * @param attributeName
	 *            the name of the attribute
	 * @return the expression
	 * @param <Y>
	 *            the type of the path
	 * @param <C>
	 *            the element type
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public <Y, C extends Collection<Y>> AbstractExpression<Y> getExpression(String attributeName) {
		final AttributeImpl<? super X, Y> attribute = (AttributeImpl<? super X, Y>) this.getMapping(attributeName).getAttribute();

		if (!attribute.isCollection()) {
			return this.get((SingularAttributeImpl<? super X, Y>) attribute);
		}

		if (((PluralAttributeImpl<? super X, Y, ?>) attribute).getCollectionType() == CollectionType.MAP) {
			return (AbstractExpression<Y>) this.get((MapAttributeImpl<? super X, Y, ?>) attribute);
		}

		return (AbstractExpression<Y>) this.get((PluralAttribute<? super X, C, Y>) attribute);
	}

	/**
	 * Returns the fetch root of the path.
	 * 
	 * @return the fetch root
	 * 
	 * @since 2.0.0
	 */
	public abstract FetchParentImpl<?, X> getFetchRoot();

	/**
	 * Returns the child mapping for the name.
	 * 
	 * @param name
	 *            the name of the child mapping
	 * @param <C>
	 *            the collection type of the child mapping
	 * @param <Y>
	 *            the type of the child mapping
	 * @return the child mapping
	 * 
	 * @since 2.0.0
	 */
	protected abstract <C, Y> Mapping<? super X, C, Y> getMapping(String name);
}
