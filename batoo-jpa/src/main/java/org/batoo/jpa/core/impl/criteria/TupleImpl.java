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
package org.batoo.jpa.core.impl.criteria;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;

import com.google.common.collect.Lists;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class TupleImpl implements Tuple {

	private final List<String> aliases;
	private final List<AbstractSelection<?>> selections;
	private final Object[] values;

	/**
	 * @param aliases
	 *            aliases
	 * @param selections
	 *            the seections
	 * @param values
	 *            the values
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TupleImpl(List<String> aliases, List<AbstractSelection<?>> selections, Object[] values) {
		super();
		this.aliases = aliases;

		this.selections = selections;
		this.values = values != null ? Arrays.copyOf(values, values.length) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object get(int i) {
		return this.values[i];
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <X> X get(int i, Class<X> type) {
		return (X) this.values[i];
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object get(String alias) {
		final int index = this.aliases.indexOf(alias);
		if (index == -1) {
			throw new NullPointerException();
		}

		return this.values[index];
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <X> X get(String alias, Class<X> type) {
		final int index = this.aliases.indexOf(alias);
		if (index == -1) {
			throw new NullPointerException();
		}

		return (X) this.values[index];
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <X> X get(TupleElement<X> tupleElement) {
		final int index = this.selections.indexOf(tupleElement);
		if (index == -1) {
			throw new NullPointerException();
		}

		return (X) this.values[index];
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<TupleElement<?>> getElements() {
		final List<TupleElement<?>> tuples = Lists.newArrayList();

		tuples.addAll(this.selections);

		return tuples;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object[] toArray() {
		return Arrays.copyOf(this.values, this.values.length);
	}
}
