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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.Selection;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.parser.MappingException;

import sun.reflect.ConstructorAccessor;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * The implementation of {@link CompoundSelection}.
 * 
 * @param <X>
 *            the type of the selection
 * 
 * @author hceylan
 * @since $version
 */
@SuppressWarnings("restriction")
public class CompoundSelectionImpl<X> extends AbstractSelection<X> implements CompoundSelection<X> {

	private final List<AbstractSelection<?>> selections = Lists.newArrayList();
	private ConstructorAccessor constructor;

	/**
	 * @param javaType
	 *            the java type
	 * @param selections
	 *            the selections
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CompoundSelectionImpl(Class<X> javaType, List<Selection<?>> selections) {
		super(javaType);

		for (final Selection<?> selection : selections) {
			this.selections.add((AbstractSelection<?>) selection);
		}

		if (javaType != Object[].class) {
			try {
				final Class<?>[] parameters = new Class[this.selections.size()];
				for (int i = 0; i < this.selections.size(); i++) {
					parameters[i] = this.selections.get(i).getJavaType();
				}

				this.constructor = ReflectHelper.createConstructor(javaType.getConstructor(parameters));
			}
			catch (final Exception e) {
				throw new MappingException("Embeddable type does not have a default constructor");
			}
		}
	}

	/**
	 * constructJoins
	 * 
	 * @param javaType
	 *            the java type
	 * @param selections
	 *            the selections
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CompoundSelectionImpl(Class<X> javaType, Selection<?>... selections) {
		this(javaType, Lists.newArrayList(selections));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(CriteriaQueryImpl<?> query, final boolean selected) {
		return Joiner.on(", ").join(Lists.transform(this.selections, new Function<AbstractSelection<?>, String>() {

			@Override
			public String apply(AbstractSelection<?> input) {
				return input.generateJpqlSelect(null, selected);
			}
		}));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(final CriteriaQueryImpl<?> query, final boolean selected) {
		return Joiner.on(",\n").join(Lists.transform(this.selections, new Function<AbstractSelection<?>, String>() {

			@Override
			public String apply(AbstractSelection<?> input) {
				return input.generateSqlSelect(query, selected);
			}
		}));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(CriteriaQueryImpl<?> query) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public X handle(TypedQueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		final Object[] values = new Object[this.selections.size()];

		for (int i = 0; i < this.selections.size(); i++) {
			values[i] = this.selections.get(i).handle(query, session, row);
		}

		try {
			return (X) (this.constructor != null ? this.constructor.newInstance(values) : values);
		}
		catch (final Exception e) {
			throw new PersistenceException("Cannot construct result object", e);
		}
	}
}
