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

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.join.Joinable;

/**
 * The abstract implementation of {@link Path}.
 * 
 * @param <X>
 *            the type of the path
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractPath<X> extends AbstractExpression<X> implements Path<X> {

	private final ParentPath<?, ?> parent;

	/**
	 * @param parent
	 *            the parent path, may be null
	 * @param javaType
	 *            the java type
	 * 
	 * @since $version
	 */
	public AbstractPath(ParentPath<?, ?> parent, Class<X> javaType) {
		super(javaType);

		this.parent = parent;
	}

	/**
	 * returns cannot dereference exception.
	 * 
	 * @param name
	 *            the attribute name
	 * @return the exception
	 * 
	 * @since $version
	 */
	protected IllegalArgumentException cannotDereference(String name) {
		return new IllegalArgumentException("Cannot dereference: " + name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <K, V, M extends Map<K, V>> Expression<M> get(MapAttribute<? super X, K, V> map) {
		throw this.cannotDereference(map.getName());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <E, C extends Collection<E>> Expression<C> get(PluralAttribute<? super X, C, E> collection) {
		throw this.cannotDereference(collection.getName());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> AbstractPath<Y> get(SingularAttribute<? super X, Y> attribute) {
		throw this.cannotDereference(attribute.getName());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> AbstractPath<Y> get(String attributeName) {
		throw this.cannotDereference(attributeName);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ParentPath<?, ?> getParentPath() {
		return this.parent;
	}

	/**
	 * Returns the root of the path.
	 * 
	 * @return the root of the path
	 * 
	 * @since $version
	 */
	public Joinable getRootPath() {
		AbstractPath<?> root = this;
		while ((root.getParentPath() != null) && !(root instanceof Joinable)) {
			root = root.getParentPath();
		}

		return (Joinable) root;
	}
}
