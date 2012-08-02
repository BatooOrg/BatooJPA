/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
		this.values = values;
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
