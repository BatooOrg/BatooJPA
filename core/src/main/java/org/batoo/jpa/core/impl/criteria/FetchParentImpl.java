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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.PkColumn;
import org.batoo.jpa.core.impl.jdbc.SecondaryTable;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.attribute.AssociatedAttribute;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.SingularAttributeImpl;
import org.batoo.jpa.core.util.BatooUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link FetchParent}.
 * 
 * @param <Z>
 *            the source type
 * @param <X>
 *            the target type
 * 
 * @author hceylan
 * @since $version
 */
public class FetchParentImpl<Z, X> implements FetchParent<Z, X> {

	private final EntityTypeImpl<X> entity;
	private final List<FetchImpl<X, ?>> fetches = Lists.newArrayList();

	private String alias;
	private String primaryTableAlias;
	private final HashBiMap<String, SecondaryTable> tableAliases = HashBiMap.create();

	private final HashBiMap<String, PkColumn> idFields = HashBiMap.create();
	private final HashBiMap<String, AbstractColumn> fields = HashBiMap.create();
	private int nextTableAlias = 0;

	/**
	 * @param entity
	 *            the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public FetchParentImpl(EntityTypeImpl<X> entity) {
		super();

		this.entity = entity;
	}

	/**
	 * Returns the description of the fetch.
	 * 
	 * @param parent
	 *            the parent
	 * @return the description of the fetch *
	 */
	public String describe(final String parent) {
		final List<String> fetches = Lists.transform(this.fetches, new Function<FetchImpl<X, ?>, String>() {

			@Override
			public String apply(FetchImpl<X, ?> input) {
				return input.describe(parent);
			}
		});

		return BatooUtils.indent(Joiner.on("\n").join(fetches));
	}

	@SuppressWarnings("unchecked")
	private <Y> FetchImpl<X, Y> fetch(AttributeImpl<? super X, Y> attribute, JoinType jt) {
		Type<?> type;

		if (attribute instanceof SingularAttributeImpl) {
			type = ((SingularAttributeImpl<? super X, ?>) attribute).getType();
		}
		else {
			final PluralAttributeImpl<? super X, ?, ?> pluralAttribute = (PluralAttributeImpl<? super X, ?, ?>) attribute;
			type = pluralAttribute.getElementType();
		}

		if (!(type instanceof EntityType)) {
			throw new IllegalArgumentException("Cannot dereference");
		}

		final FetchImpl<X, Y> fetch = new FetchImpl<X, Y>(this, (AssociatedAttribute<? super X, Y, ?>) attribute, jt);
		this.fetches.add(fetch);

		return fetch;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute) {
		return this.fetch(attribute, JoinType.INNER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute, JoinType jt) {
		return this.fetch((AttributeImpl<? super X, Y>) attribute, jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attribute) {
		return this.fetch(attribute, JoinType.LEFT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attribute, JoinType jt) {
		return this.fetch((AttributeImpl<? super X, Y>) attribute, jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <XX, Y> Fetch<XX, Y> fetch(String attributeName) {
		return this.fetch(attributeName, JoinType.INNER);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final <XX, Y> Fetch<XX, Y> fetch(String attributeName, JoinType jt) {
		final AttributeImpl<? super X, ?> attribute = this.entity.getAttribute(attributeName);
		if (attribute != null) {
			return (Fetch<XX, Y>) this.fetch(attribute, jt);
		}

		throw new IllegalArgumentException("Cannot dereference");
	}

	/**
	 * Returns the generated SQL fragment.
	 * 
	 * @param query
	 *            the query
	 * @return the generated SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generate(CriteriaQueryImpl<?> query) {
		final List<String> selects = Lists.newArrayList();
		selects.add(this.generateImpl(query));

		for (final FetchImpl<X, ?> fetch : this.fetches) {
			selects.add(fetch.generate(query));
		}

		return Joiner.on(",\n\t").join(selects);
	}

	private String generateImpl(CriteriaQueryImpl<?> query) {
		final List<String> fields = Lists.newArrayList();

		for (final EntityTable table : this.entity.getTables()) {
			int fieldNo = 0;

			final String tableAlias = this.getTableAlias(query, table);

			final Collection<AbstractColumn> columns = table.getColumns();
			for (final AbstractColumn column : columns) {

				final String fieldAlias = tableAlias + "_F" + fieldNo++;

				final String field = Joiner.on(".").skipNulls().join(tableAlias, column.getName());
				if (column instanceof PkColumn) {
					this.idFields.put(fieldAlias, (PkColumn) column);
				}
				else {
					this.fields.put(fieldAlias, column);
				}

				fields.add(field + " AS " + fieldAlias);
			}
		}

		return Joiner.on(", ").join(fields);
	}

	/**
	 * Returns the generated joins SQL fragment.
	 * 
	 * @param query
	 *            the query
	 * @return the generated joins SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String generateJoins(CriteriaQueryImpl<?> query) {
		final List<String> joins = Lists.newArrayList();

		for (final Entry<String, SecondaryTable> e : this.tableAliases.entrySet()) {
			final String alias = e.getKey();
			final SecondaryTable table = e.getValue();

			joins.add("\t" + table.joinPrimary(this.primaryTableAlias, alias));
		}

		for (final FetchImpl<X, ?> fetch : this.fetches) {
			joins.add(fetch.generateJoins(query));
		}

		return Joiner.on("\n").join(joins);
	}

	/**
	 * Returns the alias of the fetch.
	 * 
	 * @return the alias of the fetch
	 * 
	 * @since $version
	 * @author hceylan
	 * @param query
	 */
	private String getAlias(CriteriaQueryImpl<?> query) {
		if (this.alias == null) {
			this.alias = query.generateAlias();
		}

		return this.alias;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Set<Fetch<X, ?>> getFetches() {
		final Set<Fetch<X, ?>> fetches = Sets.newHashSet();
		fetches.addAll(this.fetches);

		return fetches;
	}

	private Object getId(Map<String, Object> row) {
		if (this.entity.hasSingleIdAttribute()) {
			final String field = this.idFields.keySet().iterator().next();
			return row.get(field);
		}

		return null;
	}

	/**
	 * Returns the managed instance based on the id.
	 * 
	 * @param session
	 *            the session
	 * @param row
	 *            the data row
	 * @return the managed instance or null if the id is null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedInstance<X> getInstance(SessionImpl session, Map<String, Object> row) {
		// get the id of for the instance
		final Object id = this.getId(row);

		if (id == null) {
			return null;
		}

		return this.entity.getManagedInstanceById(session, id);
	}

	/**
	 * Returns the alias of the primary table.
	 * 
	 * @param query
	 *            the query
	 * @return the alias of the primary table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getPrimaryTableAlias(CriteriaQueryImpl<?> query) {
		if (this.primaryTableAlias == null) {
			this.primaryTableAlias = this.getAlias(query) + "_P";
		}
		return this.primaryTableAlias;
	}

	/**
	 * Returns the alias for the table.
	 * <p>
	 * if table does not have an alias, it is generated.
	 * 
	 * @param query
	 *            the query
	 * @param table
	 *            the table
	 * @return the alias for the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getTableAlias(CriteriaQueryImpl<?> query, EntityTable table) {
		if (table instanceof SecondaryTable) {
			String alias = this.tableAliases.inverse().get(table);
			if (alias == null) {
				alias = this.alias + "_S" + this.nextTableAlias++;
				this.tableAliases.put(alias, (SecondaryTable) table);
			}

			return alias;
		}

		return this.getPrimaryTableAlias(query);
	}

	/**
	 * Handles the row.
	 * <p>
	 * The default implementation does nothing.
	 * 
	 * @param session
	 *            the session
	 * @param query
	 *            the query
	 * @param data
	 *            the resultset data
	 * @param rowNo
	 *            the current row no
	 * @param jumpSize
	 *            the jump size
	 * @return the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<X> handle(SessionImpl session, BaseTypedQueryImpl<?> query, List<Map<String, Object>> data, MutableInt rowNo, int jumpSize) {
		final List<ManagedInstance<X>> instances = Lists.newArrayList();

		while ((rowNo.intValue() < data.size())) {
			int leap = jumpSize;

			final Map<String, Object> row = data.get(rowNo.intValue());

			// if id is null then break
			final ManagedInstance<X> instance = this.getInstance(session, row);
			if (instance == null) {
				return null;
			}

			// if different parent then break
			if (!this.shouldContinue(session, row)) {
				break;
			}

			// if we processed the same instance then break
			if (instances.contains(instance)) {
				break;
			}

			final ManagedInstance<X> managedInstance = session.get(instance);
			if (managedInstance != null) {
				instances.add(managedInstance);
				rowNo.add(leap);

				continue;
			}

			for (final Entry<String, AbstractColumn> entry : this.fields.entrySet()) {
				final String field = entry.getKey();
				final AbstractColumn column = entry.getValue();
				column.setValue(instance, row.get(field));
			}
			instances.add(instance);
			session.put(instance);

			for (int i = this.fetches.size() - 1; i >= 0; i--) {
				final FetchImpl<X, ?> fetch = this.fetches.get(i);
				final MutableInt subRowNo = new MutableInt(rowNo);
				final List<?> value = fetch.handle(session, query, data, subRowNo, leap, instance);
				final AssociatedAttribute<? super X, ?, ?> attribute = fetch.getAttribute();

				if (value != null) {
					leap = subRowNo.intValue() - rowNo.intValue();
					if (attribute.isCollection()) {
						attribute.set(instance, value);
					}
					else {
						if (value.size() > 0) {
							attribute.set(instance, value.get(0));
						}
					}
				}
			}

			rowNo.add(leap);
		}

		return Lists.transform(instances, new Function<ManagedInstance<X>, X>() {

			@Override
			public X apply(ManagedInstance<X> input) {
				return input.getInstance();
			}
		});
	}

	/**
	 * Returns whether the handling should continue.
	 * <p>
	 * For Fetch parents this is always true, where as Fetch checks if row still belongs to the parent
	 * 
	 * @param session
	 *            the session
	 * @param row
	 *            the data row
	 * @return true if the handling should continue, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected boolean shouldContinue(SessionImpl session, Map<String, Object> row) {
		return true;
	}
}
