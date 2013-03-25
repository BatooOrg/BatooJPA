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
package org.batoo.jpa.core.impl.criteria.join;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type.PersistenceType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.EntryImpl;
import org.batoo.jpa.core.impl.criteria.join.MapJoinImpl.MapSelectType;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.TypeImpl;
import org.batoo.jpa.core.impl.model.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.model.mapping.AssociationMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.BasicMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.ElementCollectionMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMappingImpl;
import org.batoo.jpa.jdbc.AbstractColumn;
import org.batoo.jpa.jdbc.AbstractTable;
import org.batoo.jpa.jdbc.CollectionTable;
import org.batoo.jpa.jdbc.DiscriminatorColumn;
import org.batoo.jpa.jdbc.ElementColumn;
import org.batoo.jpa.jdbc.EntityTable;
import org.batoo.jpa.jdbc.JoinColumn;
import org.batoo.jpa.jdbc.SecondaryTable;
import org.batoo.jpa.jdbc.mapping.MappingType;
import org.batoo.jpa.parser.metadata.ColumnTransformerMetadata;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
 * @since 2.0.0
 */
public class FetchParentImpl<Z, X> implements FetchParent<Z, X>, Joinable {

	private final EntityTypeImpl<X> entity;
	private final TypeImpl<X> type;
	private JoinedMapping<? super Z, ?, X> mapping;

	private final HashMap<AbstractMapping<?, ?, ?>, FetchImpl<X, ?>> fetches = Maps.newHashMap();
	private final ArrayList<FetchImpl<X, ?>> joins = Lists.newArrayList();

	private String alias;
	private String primaryTableAlias;
	private String discriminatorAlias;

	private final HashMap<SecondaryTable, String> tableAliases = Maps.newHashMap();
	private final HashMap<AbstractColumn, String> idFields = Maps.newHashMap();
	private final HashMap<AbstractColumn, String> joinFields = Maps.newHashMap();
	private final Set<SingularAssociationMappingImpl<?, ?>> singularJoins = Sets.newHashSet();

	private int nextTableAlias = 1;
	private AbstractColumn[] columns;
	private String[] fields;
	private String keyColumnAlias;
	private AbstractColumn keyColumn;

	/**
	 * @param entity
	 *            the entity
	 * 
	 * @since 2.0.0
	 */
	public FetchParentImpl(EntityTypeImpl<X> entity) {
		super();

		this.entity = entity;
		this.type = null;
	}

	/**
	 * @param mapping
	 *            the mapping
	 * 
	 * @since 2.0.0
	 */
	public FetchParentImpl(JoinedMapping<? super Z, ?, X> mapping) {
		super();

		this.mapping = mapping;

		if (mapping.getType() instanceof EntityTypeImpl) {
			this.entity = (EntityTypeImpl<X>) mapping.getType();
			this.type = null;
		}
		else {
			this.type = mapping.getType();
			this.entity = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> FetchImpl<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute) {
		return this.fetch(attribute, JoinType.LEFT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> FetchImpl<X, Y> fetch(PluralAttribute<? super X, ?, Y> attribute, JoinType jt) {
		return this.fetch(attribute.getName(), jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> FetchImpl<X, Y> fetch(SingularAttribute<? super X, Y> attribute) {
		return this.fetch(attribute, JoinType.LEFT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final <Y> FetchImpl<X, Y> fetch(SingularAttribute<? super X, Y> attribute, JoinType jt) {
		return this.fetch(attribute.getName(), jt);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final <Y> FetchImpl<X, Y> fetch(String attributeName) {
		return (FetchImpl<X, Y>) this.fetch(attributeName, JoinType.LEFT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> FetchImpl<X, Y> fetch(String attributeName, JoinType jt) {
		final AbstractMapping<?, ?, ?> _mapping = this.getMapping(attributeName);

		if (!(_mapping instanceof JoinedMapping)) {
			throw new IllegalArgumentException("Cannot dereference attribute " + attributeName);
		}

		if (this.fetches.get(_mapping) != null) {
			return (FetchImpl<X, Y>) this.fetches.get(_mapping);
		}

		final FetchImpl<X, Y> fetch = new FetchImpl<X, Y>(this, (JoinedMapping<? super X, ?, Y>) _mapping, jt);
		this.fetches.put(_mapping, fetch);

		return fetch;
	}

	/**
	 * Returns the restriction based on discrimination.
	 * 
	 * @param noQualification
	 *            if the fields should not be quelified
	 * @return the restriction based on discrimination, <code>null</code>
	 * 
	 * @since 2.0.0
	 */
	public String generateDiscrimination(boolean noQualification) {
		if ((this.entity.getRootType().getInheritanceType() == null) || (this.entity == this.entity.getRootType())) {
			return null;
		}

		final Collection<String> discriminators = Collections2.transform(this.entity.getDiscriminators(), new Function<String, String>() {

			@Override
			public String apply(String input) {
				return "'" + input + "'";
			}
		});

		if (noQualification) {
			return this.entity.getRootType().getDiscriminatorColumn().getName() + " IN (" + Joiner.on(",").join(discriminators) + ")";
		}

		return this.primaryTableAlias + "." + this.entity.getRootType().getDiscriminatorColumn().getName() + " IN (" + Joiner.on(",").join(discriminators)
			+ ")";
	}

	/**
	 * Returns the description of the fetch.
	 * 
	 * @param parent
	 *            the parent
	 * @return the description of the fetch *
	 */
	public String generateJpqlFetches(final String parent) {
		final StringBuilder description = new StringBuilder();

		final Collection<String> _fetches = Collections2.transform(this.fetches.values(), new Function<FetchImpl<X, ?>, String>() {

			@Override
			public String apply(FetchImpl<X, ?> input) {
				if (input.getMapping().getMappingType() == MappingType.EMBEDDABLE) {
					return null;
				}

				return input.generateJpqlFetches(parent);
			}
		});

		description.append(Joiner.on("\n").skipNulls().join(_fetches));

		return description.toString();
	}

	/**
	 * Generates the self SQL joins fragments.
	 * 
	 * @param query
	 *            the query
	 * @param selfJoins
	 *            the list of self joins
	 * 
	 * @since 2.0.0
	 */
	public void generateSqlJoins(AbstractCriteriaQueryImpl<?> query, final List<String> selfJoins) {
		for (final Entry<SecondaryTable, String> e : this.tableAliases.entrySet()) {
			final String _alias = e.getValue();
			final SecondaryTable table = e.getKey();

			selfJoins.add(table.joinPrimary(this.getPrimaryTableAlias(query), _alias));
		}
	}

	/**
	 * Generates joins SQL fragment for the fetch chain.
	 * 
	 * @param query
	 *            the query
	 * @param joins
	 *            the map of joins
	 * @param recurse
	 *            recurse to fetch children
	 * 
	 * @since 2.0.0
	 */
	public void generateSqlJoins(AbstractCriteriaQueryImpl<?> query, Map<Joinable, String> joins, boolean recurse) {
		final List<String> selfJoins = Lists.newArrayList();

		this.generateSqlJoins(query, selfJoins);

		final String join = Joiner.on("\n").skipNulls().join(selfJoins);
		if ((join != null) && (join.trim().length() > 0)) {
			joins.put(this, join);
		}
		else {
			joins.put(this, null);
		}

		for (final FetchImpl<X, ?> child : this.joins) {
			child.generateSqlJoins(query, joins, false);
		}

		if (recurse) {
			for (final FetchImpl<X, ?> fetch : this.fetches.values()) {
				fetch.generateSqlJoins(query, joins, true);
			}
		}
	}

	/**
	 * Returns the generated SQL fragment.
	 * 
	 * @param query
	 *            the query
	 * @param selected
	 *            if the item is selected
	 * @param root
	 *            if the generation is at root
	 * @return the generated SQL fragment
	 * 
	 * @since 2.0.0
	 */
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected, boolean root) {
		return this.generateSqlSelect(query, selected, root, MapSelectType.VALUE);
	}

	/**
	 * Returns the generated SQL fragment.
	 * 
	 * @param query
	 *            the query
	 * @param selected
	 *            if the item is selected
	 * @param root
	 *            if the generation is at root
	 * @param selectType
	 *            the select type
	 * @return the generated SQL fragment
	 * 
	 * @since 2.0.0
	 */
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected, boolean root, MapSelectType selectType) {
		final List<String> selects = Lists.newArrayList();

		// skip the embeddable mappings
		if (!(this.getMapping() instanceof EmbeddedMappingImpl)) {
			selects.add(this.generateSqlSelectImpl(query, selected, root, selectType));
		}

		for (final FetchImpl<X, ?> fetch : this.fetches.values()) {
			final String select = fetch.generateSqlSelect(query, selected, false);
			if (StringUtils.isNotBlank(select)) {
				selects.add(select);
			}
		}

		return Joiner.on(",\n").join(selects);
	}

	private void generateSqlSelectForElementCollection(AbstractCriteriaQueryImpl<?> query, boolean selected, List<String> selects,
		Map<AbstractColumn, String> fieldMap, MapSelectType selectType) {

		final List<String> fields1 = Lists.newArrayList();

		final CollectionTable table = (CollectionTable) this.mapping.getJoinTable();
		final String tableAlias = this.getTableAlias(query, table);

		for (final AbstractColumn column : table.getColumns()) {
			// we are not insterested in join columns at all
			if (column instanceof JoinColumn) {
				continue;
			}

			// if we are selecting only keys and the there is a key column, we are only interested in key column
			if ((selectType == MapSelectType.KEY) && (table.getKeyColumn() != null) && (table.getKeyColumn() != column)) {
				continue;
			}

			// if we are selecting only values and the there is a key column, we are only interested in value columns
			// if ((selectType == MapSelectType.VALUE) && (table.getKeyColumn() == column)) {
			// continue;
			// }

			final String fieldAlias = tableAlias + "_F" + query.getFieldAlias(tableAlias, column);
			final String field = Joiner.on(".").skipNulls().join(tableAlias, column.getName());

			if (selected) {
				fields1.add(field + " AS " + fieldAlias);
			}
			else {
				fields1.add(field);
			}

			// seperate out the key column from the rest
			if (column == table.getKeyColumn()) {
				this.keyColumnAlias = fieldAlias;
			}
			else {
				fieldMap.put(column, fieldAlias);
			}
		}

		selects.add(Joiner.on(", ").join(fields1));
	}

	private void generateSqlSelectForEntityTable(AbstractCriteriaQueryImpl<?> query, boolean selected, boolean root, final List<String> selects,
		final Map<AbstractColumn, String> fieldMap, final EntityTable table) {
		final List<String> fields1 = Lists.newArrayList();

		final String tableAlias = this.getTableAlias(query, table);

		for (final AbstractColumn column : table.getColumns()) {
			final String fieldAlias;
			final String field;

			if (column.isPrimaryKey()) {
				fieldAlias = tableAlias + "_F" + query.getFieldAlias(tableAlias, column);
				field = Joiner.on(".").skipNulls().join(tableAlias, column.getName());

				this.idFields.put(column, fieldAlias);
			}
			else if (column instanceof DiscriminatorColumn) {
				fieldAlias = tableAlias + "_F" + query.getFieldAlias(tableAlias, column);
				field = Joiner.on(".").skipNulls().join(tableAlias, column.getName());

				this.discriminatorAlias = fieldAlias;
			}
			else if (column.getMapping() instanceof SingularAssociationMappingImpl) {
				final SingularAssociationMappingImpl<?, ?> _mapping = (SingularAssociationMappingImpl<?, ?>) column.getMapping();

				// if we are on a join on that path then ignore
				if (!root && this.ignoreJoin(_mapping)) {
					continue;
				}

				fieldAlias = tableAlias + "_F" + query.getFieldAlias(tableAlias, column);
				field = Joiner.on(".").skipNulls().join(tableAlias, column.getName());

				this.singularJoins.add(_mapping);
				this.joinFields.put(column, fieldAlias);
			}
			else {
				fieldAlias = tableAlias + "_F" + query.getFieldAlias(tableAlias, column);

				if ((column.getMapping() instanceof BasicMappingImpl)
					&& (((BasicMappingImpl<?, ?>) column.getMapping()).getAttribute().getColumnTransformer() != null)) {
					final ColumnTransformerMetadata columnTransformer = ((BasicMappingImpl<?, ?>) column.getMapping()).getAttribute().getColumnTransformer();
					final String columnSql = Strings.isNullOrEmpty(columnTransformer.getRead()) ? column.getName() : columnTransformer.getRead();
					field = columnSql.replace(column.getName(), Joiner.on(".").skipNulls().join(tableAlias, column.getName()));
				}
				else {
					field = Joiner.on(".").skipNulls().join(tableAlias, column.getName());
				}

				fieldMap.put(column, fieldAlias);
			}

			if (selected) {
				fields1.add(field + " AS " + fieldAlias);
			}
			else {
				fields1.add(field);
			}
		}

		selects.add(Joiner.on(", ").join(fields1));
	}

	private String generateSqlSelectImpl(AbstractCriteriaQueryImpl<?> query, boolean selected, boolean root, MapSelectType selectType) {
		final List<String> selects = Lists.newArrayList();
		final Map<AbstractColumn, String> fieldMap = Maps.newHashMap();

		if (this.entity != null) {
			for (final EntityTable table : this.entity.getAllTables()) {
				this.generateSqlSelectForEntityTable(query, selected, root, selects, fieldMap, table);
			}

			for (final SingularAssociationMappingImpl<?, ?> associationMapping : this.entity.getAssociationsSingular()) {
				if ((associationMapping.getForeignKey() != null) && associationMapping.getForeignKey().isReadOnly()) {
					this.singularJoins.add(associationMapping);
				}
			}
		}
		else {
			this.generateSqlSelectForElementCollection(query, selected, selects, fieldMap, selectType);
		}

		this.columns = new AbstractColumn[fieldMap.size()];
		this.fields = new String[fieldMap.size()];

		int i = 0;
		for (final Entry<AbstractColumn, String> entry : fieldMap.entrySet()) {
			this.columns[i] = entry.getKey();
			this.fields[i] = entry.getValue();
			i++;
		}

		return Joiner.on(",\n\t").join(selects);
	}

	/**
	 * Returns the alias of the fetch.
	 * 
	 * @param query
	 *            the query
	 * @return the alias of the fetch
	 * 
	 * @since 2.0.0
	 */
	private String getAlias(BaseQueryImpl<?> query) {
		if (this.alias == null) {
			this.alias = query.generateTableAlias(this.entity != null);
		}

		return this.alias;
	}

	/**
	 * creates a managed Id using joinColumns instead of Id Columns
	 * 
	 * @param session
	 * @param row
	 *            Sql result row
	 * @param mapping
	 *            the singular mapping
	 * @return managedId
	 * 
	 * @throws SQLException
	 * @since 2.0.1
	 */
	private ManagedId<?> getAssociatedId(SessionImpl session, ResultSet row, SingularAssociationMappingImpl<?, ?> mapping) throws SQLException {
		final HashMap<AbstractColumn, String> translatedIdFields = Maps.newHashMap();
		for (final JoinColumn joinColumn : mapping.getForeignKey().getJoinColumns()) {

			String field = null;

			if (!joinColumn.isVirtual()) {
				field = this.joinFields.get(joinColumn);
			}
			else {
				final AbstractColumn masterColumn = joinColumn.getMasterColumn();
				for (int i = 0; i < this.columns.length; i++) {
					if (this.columns[i] == masterColumn) {
						field = this.fields[i];
						break;
					}
				}
			}
			translatedIdFields.put(joinColumn.getReferencedColumn(), field);
		}
		return mapping.getType().getId(session, row, translatedIdFields);
	}

	/**
	 * 
	 * @return the discriminatorAlias
	 * 
	 * @since 2.0.1
	 */
	public String getDiscriminatorAlias() {
		return this.discriminatorAlias;
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
	public final Set<Fetch<X, ?>> getFetches() {
		final Set<Fetch<X, ?>> _fetches = Sets.newHashSet();
		_fetches.addAll(this.fetches.values());

		return _fetches;
	}

	/**
	 * Returns the managed instance based on the id.
	 * 
	 * @param session
	 *            the session
	 * @param row
	 *            the data row
	 * @return the managed instance or null if the id is null
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	private <Y extends X> ManagedInstance<Y> getInstance(SessionImpl session, ResultSet row) throws SQLException {
		// get the id of for the instance
		final ManagedId<X> managedId = this.entity.getId(session, row, this.idFields);
		if (managedId == null) {
			return null;
		}

		// look for it in the session
		ManagedInstance<Y> instance = session.get(managedId);

		// if found then return it
		if (instance != null) {
			// if it is a new instance simply return it
			if (!(instance.getInstance() instanceof EnhancedInstance)) {
				return instance;
			}

			final EnhancedInstance enhancedInstance = (EnhancedInstance) instance.getInstance();

			// if it is a lazy instance mark as loading and initialize
			if (!enhancedInstance.__enhanced__$$__isInitialized()) {
				this.initializeInstance(session, row, instance);

				session.lazyInstanceLoading(instance);
				enhancedInstance.__enhanced__$$__setInitialized();
			}

			return instance;
		}

		// if no inheritance then initialize and return
		if (this.entity.getInheritanceType() == null) {
			instance = (ManagedInstance<Y>) this.entity.getManagedInstanceById(session, managedId, false);
		}
		// inheritance is in place then locate the correct child type
		else {
			final String discriminatorValue = row.getObject(this.discriminatorAlias).toString();

			// check if we have a legal discriminator value
			final EntityTypeImpl<Y> effectiveType = (EntityTypeImpl<Y>) this.entity.getChildType(discriminatorValue);
			if (effectiveType == null) {
				throw new IllegalArgumentException("Discriminator " + discriminatorValue + " not found in the type " + this.entity.getName());
			}

			// initialize and return
			instance = effectiveType.getManagedInstanceById(session, (ManagedId<Y>) managedId, false);
		}

		this.initializeInstance(session, row, instance);
		session.put(instance);

		return instance;
	}

	/**
	 * Returns the mapping of the fetch.
	 * 
	 * @return the mapping of the fetch or <code>null</code> if the fetch is root
	 * 
	 * @since 2.0.0
	 */
	public JoinedMapping<? super Z, ?, X> getMapping() {
		return null;
	}

	@SuppressWarnings("unchecked")
	private AbstractMapping<?, ?, ?> getMapping(String attributeName) {
		if (this.entity != null) {
			return this.entity.getRootMapping().getMapping(attributeName);
		}

		if (this.type instanceof EmbeddableTypeImpl) {
			if (this.getMapping() instanceof EmbeddedMappingImpl) {
				return ((EmbeddedMappingImpl<? super Z, X>) this.getMapping()).getChild(attributeName);
			}
			return ((ElementCollectionMappingImpl<? super Z, ?, X>) this.getMapping()).getMapping(attributeName);
		}

		return null;
	}

	/**
	 * Returns the alias of the primary table.
	 * 
	 * @param query
	 *            the query
	 * @return the alias of the primary table
	 * 
	 * @since 2.0.0
	 */
	public String getPrimaryTableAlias(BaseQueryImpl<?> query) {
		if (this.primaryTableAlias == null) {
			this.primaryTableAlias = this.getAlias(query) + "_P";
		}

		return this.primaryTableAlias;
	}

	/**
	 * Returns the SQL restriction in pairs of table alias and column.
	 * 
	 * @param query
	 *            the query
	 * @param selectType
	 *            the select type
	 * @return the SQL restriction in pairs of table alias and column
	 * 
	 * @since 2.0.0
	 */
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query, MapSelectType selectType) {
		final List<String> restrictions = Lists.newArrayList();

		if (this.entity != null) {
			for (final AbstractColumn column : this.entity.getRootType().getPrimaryTable().getPkColumns()) {
				if (query.isQuery()) {
					restrictions.add(this.getPrimaryTableAlias(query) + "." + column.getName());
				}
				else {
					restrictions.add(column.getName());
				}
			}
		}

		if (this.mapping instanceof ElementCollectionMappingImpl) {
			final ElementCollectionMappingImpl<?, ?, ?> elementCollectionMapping = (ElementCollectionMappingImpl<?, ?, ?>) this.mapping;
			final CollectionTable collectionTable = elementCollectionMapping.getCollectionTable();
			final ElementColumn elementColumn = collectionTable.getElementColumn();

			restrictions.add(this.getPrimaryTableAlias(query) + "." + elementColumn.getName());
		}

		return restrictions.toArray(new String[restrictions.size()]);
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
	 * @since 2.0.0
	 */
	@Override
	public String getTableAlias(BaseQueryImpl<?> query, AbstractTable table) {
		if (table instanceof SecondaryTable) {
			String _alias = this.tableAliases.get(table);
			if (_alias == null) {
				_alias = this.alias + "_S" + this.nextTableAlias++;
				this.tableAliases.put((SecondaryTable) table, _alias);
			}

			return _alias;
		}

		return this.getPrimaryTableAlias(query);
	}

	/**
	 * Handles the row.
	 * 
	 * @param session
	 *            the session
	 * @param row
	 *            the current row
	 * @return the instance
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public X handle(SessionImpl session, ResultSet row) throws SQLException {
		return (X) this.handle(session, row, MapSelectType.VALUE).getValue().getInstance();
	}

	/**
	 * Handles the row.
	 * 
	 * @param session
	 *            the session
	 * @param row
	 *            the current row
	 * @param selectType
	 *            the map select type
	 * @return the instance
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since 2.0.0
	 */
	public EntryImpl<Object, ManagedInstance<?>> handle(SessionImpl session, ResultSet row, MapSelectType selectType) throws SQLException {
		return this.handleFetch(session, row, selectType);
	}

	private void handleAssociationFetch(SessionImpl session, final ResultSet row, Object instance, final FetchImpl<X, ?> fetch) throws SQLException {
		final EntryImpl<Object, ManagedInstance<?>> pair = fetch.handleFetch(session, row, MapSelectType.ENTRY);
		// if null then continue
		if (pair == null) {
			return;
		}

		final ManagedInstance<?> child = pair.getValue();

		final AssociationMappingImpl<? super X, ?, ?> _mapping = (AssociationMappingImpl<? super X, ?, ?>) fetch.getMapping();

		if (_mapping.getMappingType() == MappingType.PLURAL_ASSOCIATION) {
			// if it is a plural association then we will test if we processed the child
			// if it is a one-to-many mapping and has an inverse then set the inverses
			if (_mapping.get(instance) != null && ((ManagedCollection<?>) _mapping.get(instance)).addChild(pair) && (_mapping.getInverse() != null) && //
				(_mapping.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_MANY)) {
				_mapping.getInverse().set(child.getInstance(), instance);
				child.setJoinLoaded(_mapping.getInverse());
			}
		}
		// if it is singular association then set the instance
		else {
			_mapping.set(instance, child.getInstance());

			// if this is a one-to-one mapping and has an inverse then set it
			if ((_mapping.getInverse() != null) && (_mapping.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_ONE)) {
				_mapping.getInverse().set(child.getInstance(), instance);
				child.setJoinLoaded(_mapping);
			}
		}
	}

	@SuppressWarnings({ "unchecked" })
	private X handleElement(ResultSet row) throws SQLException {
		if (this.type.getPersistenceType() == PersistenceType.BASIC) {
			if (this.type.getJavaType().isEnum() && this.columns.length == 1) {
				return (X) this.columns[0].convertValueForSet(row.getObject(this.fields[0]));
			}
			return (X) row.getObject(this.fields[0]);
		}

		final X instance = ((EmbeddableTypeImpl<X>) this.type).newInstance();
		for (int i = 0; i < this.fields.length; i++) {
			this.columns[i].setValue(instance, row.getObject(this.fields[i]));
		}

		return instance;
	}

	private void handleElementCollectionFetch(ResultSet row, Object instance, FetchImpl<X, ?> fetch) throws SQLException {
		final ElementCollectionMappingImpl<? super X, ?, ?> _mapping = (ElementCollectionMappingImpl<? super X, ?, ?>) fetch.getMapping();
		final EntryImpl<Object, ?> child = fetch.handleElementFetch(row, MapSelectType.ENTRY);

		// if it is a plural association then we will test if we processed the child
		((ManagedCollection<?>) _mapping.get(instance)).addElement(child);
	}

	/**
	 * Handles the row
	 * 
	 * @param row
	 *            the row
	 * @return the collection element
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since 2.0.0
	 */
	public EntryImpl<Object, X> handleElementFetch(ResultSet row) throws SQLException {
		return this.handleElementFetch(row, MapSelectType.VALUE);
	}

	/**
	 * Handles the row
	 * 
	 * @param row
	 *            the row
	 * @param selectType
	 *            the select type
	 * @return the collection element
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since 2.0.0
	 */
	public EntryImpl<Object, X> handleElementFetch(ResultSet row, MapSelectType selectType) throws SQLException {
		if ((selectType == MapSelectType.VALUE) || !this.mapping.isMap()) {
			return new EntryImpl<Object, X>(null, this.handleElement(row));
		}

		if (this.keyColumnAlias != null) {
			if (this.keyColumn == null) {
				final ElementCollectionMappingImpl<? super X, ?, ?> _mapping = (ElementCollectionMappingImpl<? super X, ?, ?>) this.mapping;
				if (_mapping.getCollectionTable() != null) {
					this.keyColumn = _mapping.getCollectionTable().getKeyColumn();
				}
			}

			final Object object = (this.keyColumn != null) ? this.keyColumn.convertValueForSet(row.getObject(this.keyColumnAlias))
				: row.getObject(this.keyColumnAlias);
			if (selectType == MapSelectType.KEY) {
				return new EntryImpl<Object, X>(object, null);
			}
			else {
				return new EntryImpl<Object, X>(object, this.handleElement(row));
			}
		}

		final X value = this.handleElement(row);
		final Object key = ((ElementCollectionMappingImpl<? super Z, ?, X>) this.mapping).extractKey(value);

		return new EntryImpl<Object, X>(key, value);
	}

	/**
	 * Handles the row.
	 * 
	 * @param session
	 *            the session
	 * @param row
	 *            the current row
	 * @param selectType
	 *            the map select type
	 * @return the instance
	 * @throws SQLException
	 *             thrown in case of an underlying SQL Error
	 * 
	 * @since 2.0.0
	 */
	public EntryImpl<Object, ManagedInstance<?>> handleFetch(SessionImpl session, ResultSet row, MapSelectType selectType) throws SQLException {
		// if instance is null then break
		final ManagedInstance<? extends X> instance = this.getInstance(session, row);
		if (instance == null) {
			return null;
		}

		// if instance is refreshing then re-initialize instance
		if (instance.isRefreshing()) {
			this.initializeInstance(session, row, instance);
		}

		// if the instance is loading then continue processing
		if (instance.isLoading()) {
			this.handleFetches(session, row, instance.getInstance());
		}

		if ((selectType == MapSelectType.VALUE) || (this.mapping == null) || !this.mapping.isMap()) {
			return new EntryImpl<Object, ManagedInstance<?>>(null, instance);
		}

		if (this.keyColumnAlias != null) {
			final Object key = row.getObject(this.keyColumnAlias);
			return new EntryImpl<Object, ManagedInstance<?>>(key, instance);
		}

		final Object key = this.mapping.extractKey(instance.getInstance());
		return new EntryImpl<Object, ManagedInstance<?>>(key, instance);
	}

	void handleFetches(SessionImpl session, final ResultSet row, Object instance) throws SQLException {
		for (final FetchImpl<X, ?> fetch : this.fetches.values()) {
			final MappingType mappingType = fetch.getMapping().getMappingType();

			switch (mappingType) {
				case EMBEDDABLE:
					fetch.handleFetches(session, row, instance);
					break;
				case ELEMENT_COLLECTION:
					this.handleElementCollectionFetch(row, instance, fetch);
					break;
				default:
					this.handleAssociationFetch(session, row, instance, fetch);
			}
		}
	}

	/**
	 * Returns if the join should be ignored
	 * 
	 * @param mapping
	 *            the mapping
	 * @return true if the join should be ignored, false otherwise
	 * 
	 * @since 2.0.0
	 */
	private boolean ignoreJoin(SingularAssociationMappingImpl<?, ?> mapping) {
		// if we are joined on this mapping then ignore
		if ((mapping.getInverse() != null) && (mapping.getInverse() == this.getMapping())) {
			return true;
		}

		// if we will join on that mapping then ignore
		for (final FetchImpl<X, ?> fetch : this.fetches.values()) {
			if (fetch.getMapping() == mapping) {
				return true;
			}
		}

		return false;
	}

	private void initializeInstance(SessionImpl session, ResultSet row, ManagedInstance<? extends X> managedInstance) throws SQLException {
		managedInstance.setLoading(true);

		final X instance = managedInstance.getInstance();

		for (int i = 0; i < this.fields.length; i++) {
			this.columns[i].setValue(instance, row.getObject(this.fields[i]));
		}

		// initializing the singular joins
		for (final SingularAssociationMappingImpl<?, ?> _mapping : this.singularJoins) {

			// HANDLE INHERITANCE / DISCRIMINATOR VALUE
			final EntityTypeImpl<?> _type = _mapping.getType();
			final EntityTypeImpl<?> effectiveType;
			if (_type.getInheritanceType() == null) {
				effectiveType = _mapping.getType();
			}
			else {
				final FetchImpl<X, ?> fetchImpl = this.fetches.get(_mapping);
				final String discriminatorValue = row.getObject(fetchImpl.getDiscriminatorAlias()).toString();

				// check if we have a legal discriminator value
				effectiveType = _type.getChildType(discriminatorValue);
				if (effectiveType == null) {
					throw new IllegalArgumentException("Discriminator " + discriminatorValue + " not found in the type " + _type.getName());
				}
			}
			// ////////////

			final ManagedId<?> managedId = getAssociatedId(session, row, _mapping);

			if (managedId != null && managedId.getId() != null) {
				final Object reference = session.getEntityManager().getReference(effectiveType.getJavaType(), managedId.getId());
				_mapping.set(instance, reference);

				managedInstance.setJoinLoaded(_mapping);
			}
		}

		for (final FetchImpl<X, ?> fetch : this.fetches.values()) {
			final JoinedMapping<? super X, ?, ?> _mapping = fetch.getMapping();
			if (!(_mapping instanceof SingularAssociationMappingImpl)) {
				_mapping.initialize(managedInstance);
			}

			managedInstance.setJoinLoaded(_mapping);
		}
	}

	/**
	 * Joins instead of fetch.
	 * 
	 * @param attributeName
	 *            the name of the attribute
	 * @param jt
	 *            the join type
	 * @param <Y>
	 *            the type of the return join
	 * @return the fetch for the join
	 * 
	 * @since 2.0.0
	 */
	@SuppressWarnings("unchecked")
	public <Y> FetchImpl<X, Y> join(String attributeName, JoinType jt) {
		final AbstractMapping<?, ?, ?> _mapping = this.getMapping(attributeName);

		if (!(_mapping instanceof JoinedMapping)) {
			throw new IllegalArgumentException("Cannot dereference attribute " + attributeName);
		}

		final FetchImpl<X, Y> fetch = new FetchImpl<X, Y>(this, (JoinedMapping<? super X, ?, Y>) _mapping, jt);
		this.joins.add(fetch);

		return fetch;
	}
}
