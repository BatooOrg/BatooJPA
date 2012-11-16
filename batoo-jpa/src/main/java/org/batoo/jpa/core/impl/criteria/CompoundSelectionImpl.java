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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Tuple;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.Selection;

import org.batoo.common.reflect.ConstructorAccessor;
import org.batoo.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.parser.MappingException;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * The implementation of {@link CompoundSelection}.
 * 
 * @param <X>
 *            the type of the selection
 * 
 * @since 2.0.0
 */
public class CompoundSelectionImpl<X> extends AbstractSelection<X> implements CompoundSelection<X> {

	private final List<AbstractSelection<?>> selections = Lists.newArrayList();
	private ConstructorAccessor constructor;
	private List<String> aliases;

	/**
	 * @param javaType
	 *            the java type
	 * @param selections
	 *            the selections
	 * 
	 * @since 2.0.0
	 */
	public CompoundSelectionImpl(Class<X> javaType, List<Selection<?>> selections) {
		super(javaType);

		for (final Selection<?> selection : selections) {
			this.selections.add((AbstractSelection<?>) selection);
		}

		if ((javaType != Object[].class) && (javaType != Tuple.class)) {
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
	 * @since 2.0.0
	 */
	public CompoundSelectionImpl(Class<X> javaType, Selection<?>... selections) {
		this(javaType, Lists.newArrayList(selections));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, final boolean selected) {
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
	public String generateSqlSelect(final AbstractCriteriaQueryImpl<?> query, final boolean selected) {
		return Joiner.on(",\n").join(Lists.transform(this.selections, new Function<AbstractSelection<?>, String>() {

			@Override
			public String apply(AbstractSelection<?> input) {
				return input.generateSqlSelect(query, selected);
			}
		}));
	}

	/**
	 * Returns the alias.
	 * 
	 * @return the alias
	 * 
	 * @since 2.0.0
	 */
	private List<String> getAliases() {
		if (this.aliases != null) {
			return this.aliases;
		}

		this.aliases = Lists.newArrayList();
		for (final AbstractSelection<?> selection : this.selections) {
			this.aliases.add(selection.getAlias());
		}

		return this.aliases;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public X handle(QueryImpl<?> query, SessionImpl session, ResultSet row) throws SQLException {
		final Object[] values = new Object[this.selections.size()];

		for (int i = 0; i < this.selections.size(); i++) {
			values[i] = this.selections.get(i).handle(query, session, row);
		}

		if (this.getJavaType() == Tuple.class) {
			return (X) new TupleImpl(this.getAliases(), this.selections, values);
		}

		try {
			return (X) (this.constructor != null ? this.constructor.newInstance(values) : values);
		}
		catch (final Exception e) {
			throw new PersistenceException("Cannot construct result object", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isEntityList() {
		for (final AbstractSelection<?> selection : this.selections) {
			if (!selection.isEntityList()) {
				return false;
			}
		}

		return true;
	}
}
