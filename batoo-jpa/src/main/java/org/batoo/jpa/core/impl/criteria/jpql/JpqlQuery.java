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
package org.batoo.jpa.core.impl.criteria.jpql;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder.Trimspec;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.PluralJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.AbstractSelection;
import org.batoo.jpa.core.impl.criteria.BaseQuery;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaDeleteImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaUpdateImpl;
import org.batoo.jpa.core.impl.criteria.QueryImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.SubqueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.expression.AllAnyExpression;
import org.batoo.jpa.core.impl.criteria.expression.CaseImpl;
import org.batoo.jpa.core.impl.criteria.expression.CoalesceExpression;
import org.batoo.jpa.core.impl.criteria.expression.CollectionExpression;
import org.batoo.jpa.core.impl.criteria.expression.ConcatExpression;
import org.batoo.jpa.core.impl.criteria.expression.ConstantExpression;
import org.batoo.jpa.core.impl.criteria.expression.CountExpression;
import org.batoo.jpa.core.impl.criteria.expression.ExistsExpression;
import org.batoo.jpa.core.impl.criteria.expression.FunctionExpression;
import org.batoo.jpa.core.impl.criteria.expression.MapExpression;
import org.batoo.jpa.core.impl.criteria.expression.NullIfExpression;
import org.batoo.jpa.core.impl.criteria.expression.PredicateImpl;
import org.batoo.jpa.core.impl.criteria.expression.SimpleCaseImpl;
import org.batoo.jpa.core.impl.criteria.expression.SubstringExpression;
import org.batoo.jpa.core.impl.criteria.expression.TrimExpression;
import org.batoo.jpa.core.impl.criteria.join.AbstractFrom;
import org.batoo.jpa.core.impl.criteria.join.ListJoinImpl;
import org.batoo.jpa.core.impl.criteria.path.AbstractPath;
import org.batoo.jpa.core.impl.criteria.path.ParentPath;
import org.batoo.jpa.core.impl.manager.EntityManagerFactoryImpl;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.jpql.JpqlLexer;
import org.batoo.jpa.jpql.JpqlParser;
import org.batoo.jpa.jpql.JpqlParser.ql_statement_return;
import org.batoo.jpa.parser.metadata.NamedQueryMetadata;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The class that constructs a {@link CriteriaQuery} out of JPQL.
 * 
 * @author hceylan
 * @since $version
 */
public class JpqlQuery {

	private static final BLogger LOG = BLoggerFactory.getLogger(JpqlQuery.class);

	private final MetamodelImpl metamodel;
	private final String qlString;
	private final BaseQueryImpl<?> q;

	private final Map<BaseQuery<?>, Map<String, AbstractFrom<?, ?>>> aliasMap = Maps.newHashMap();
	private HashMap<String, Object> hints;

	private LockModeType lockMode;
	private long lastUsed;

	/**
	 * Constructor for named queries.
	 * 
	 * @param entityManagerFactory
	 *            the entity manager factory
	 * @param cb
	 *            the criteria builder
	 * @param metadata
	 *            the named query metadata
	 * @since $version
	 * @author hceylan
	 */
	public JpqlQuery(EntityManagerFactoryImpl entityManagerFactory, CriteriaBuilderImpl cb, NamedQueryMetadata metadata) {
		this(entityManagerFactory, metadata.getQuery(), cb);

		this.lastUsed = Long.MAX_VALUE;

		// force sql compilation
		this.q.getSql();

		this.lockMode = metadata.getLockMode();
		if (metadata.getHints().size() > 0) {
			this.hints = Maps.newHashMap();
			this.hints.putAll(metadata.getHints());
		}

		entityManagerFactory.addNamedQuery(metadata.getName(), this);
	}

	/**
	 * @param entityManagerFactory
	 *            the entity manager factory
	 * @param qlString
	 *            the query string
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JpqlQuery(EntityManagerFactoryImpl entityManagerFactory, String qlString) {
		this(entityManagerFactory, qlString, null);
	}

	private JpqlQuery(EntityManagerFactoryImpl entityManagerFactory, String qlString, CriteriaBuilderImpl cb) {
		super();

		this.metamodel = entityManagerFactory.getMetamodel();
		this.qlString = qlString;

		this.lastUsed = System.currentTimeMillis();

		if (cb == null) {
			cb = entityManagerFactory.getCriteriaBuilder();
		}

		this.q = this.parse(cb);
	}

	/**
	 * Constructs the query object.
	 * 
	 * @param cb
	 *            the criteria builder
	 * @param tree
	 *            the query tree
	 * 
	 * @return the constructed criteria query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private BaseQueryImpl<?> construct(CriteriaBuilderImpl cb, CommonTree tree) {
		final Tree type = tree.getChild(0);
		if (type.getType() == JpqlParser.SELECT) {
			return this.constructSelectQuery(cb, tree);
		}
		else if (type.getType() == JpqlParser.DELETE) {
			return this.constructDeleteQuery(cb, tree);
		}
		else {
			return this.constructUpdateQuery(cb, tree);
		}
	}

	/**
	 * Constructs an update query.
	 * 
	 * @param cb
	 *            the criteria builder
	 * @param tree
	 *            the tree
	 * 
	 * @since $version
	 * @author hceylan
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private CriteriaDeleteImpl constructDeleteQuery(CriteriaBuilderImpl cb, CommonTree tree) {
		final CriteriaDeleteImpl q = new CriteriaDeleteImpl(this.metamodel);

		final Tree deleteDef = tree.getChild(0);

		final Tree aliasedDef = deleteDef.getChild(0);
		final Aliased aliased = new Aliased(aliasedDef);

		final EntityTypeImpl entity = this.getEntity(aliased.getQualified().toString());
		final RootImpl<?> r = (RootImpl<?>) q.from(entity);
		this.putAlias(q, aliasedDef, aliased, r);

		if (tree.getChildCount() == 2) {
			q.where(this.constructJunction(cb, q, deleteDef.getChild(1)));
		}
		final Tree setDefs = tree.getChild(1);
		for (int i = 0; i < setDefs.getChildCount(); i++) {
			final Tree setDef = setDefs.getChild(i);
			this.getExpression(cb, q, setDef.getChild(0), null);
		}

		return q;
	}

	/**
	 * Creates the from fragment of the query.
	 * 
	 * @param cb
	 *            the criteria builder
	 * @param q
	 *            the query
	 * @param from
	 *            the from metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void constructFrom(CriteriaBuilderImpl cb, AbstractQuery<?> q, Tree froms) {
		for (int i = 0; i < froms.getChildCount(); i++) {
			final Tree from = froms.getChild(i);
			// root query from
			if (from.getType() == JpqlParser.ST_FROM) {
				final Aliased fromDef = new Aliased(from.getChild(0));

				final EntityTypeImpl<Object> entity = this.getEntity(fromDef.getQualified().toString());

				final RootImpl<Object> r = (RootImpl<Object>) q.from(entity);
				r.alias(fromDef.getAlias());

				this.putAlias((BaseQueryImpl<?>) q, from, fromDef, r);

				this.constructJoins(cb, (AbstractCriteriaQueryImpl<?>) q, r, from.getChild(1));

			}

			// in collection form
			else if (from.getType() == JpqlParser.ST_COLL) {
				final Aliased aliased = new Aliased(from.getChild(1));

				AbstractFrom<?, ?> parent = this.getAliased(q, from.getChild(0).getText());

				final LinkedList<String> segments = aliased.getQualified().getSegments();

				int depth = 0;
				for (int j = 0; j < segments.size(); j++) {
					final String segment = segments.get(j);

					if ((depth > 0) && (parent instanceof PluralJoin)) {
						throw new PersistenceException("Cannot qualify, only embeddable joins within the path allowed, " + "line " + from.getLine() + ":"
							+ from.getCharPositionInLine());
					}

					parent = parent.join(segment, JoinType.LEFT);

					depth++;
				}

				parent.alias(aliased.getAlias());

				this.putAlias((BaseQueryImpl<?>) q, from.getChild(1), aliased, parent);
			}

			// sub query from
			else {
				final Aliased fromDef = new Aliased(from);
				final EntityTypeImpl<Object> entity = this.getEntity(fromDef.getQualified().toString());

				final RootImpl<Object> r = (RootImpl<Object>) q.from(entity);
				r.alias(fromDef.getAlias());

				this.putAlias((BaseQuery<?>) q, from, fromDef, r);
			}
		}
	}

	/**
	 * Creates the group by fragment of the query.
	 * 
	 * @param cb
	 *            the criteria builder
	 * @param q
	 *            the query
	 * @param from
	 *            the from metadata
	 * @return the list of group by expressions
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private List<Expression<?>> constructGroupBy(CriteriaBuilderImpl cb, AbstractQuery<?> q, Tree groupByDef) {
		final List<Expression<?>> groupBy = Lists.newArrayList();

		for (int i = 0; i < groupByDef.getChildCount(); i++) {
			groupBy.add(this.getExpression(cb, q, groupByDef.getChild(i), null));
		}

		return groupBy;
	}

	/**
	 * Creates the from fragment of the query.
	 * 
	 * @param cb
	 *            the criteria builder
	 * @param q
	 *            the query join
	 * @param r
	 *            the root
	 * @param joins
	 *            the joins metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void constructJoins(CriteriaBuilderImpl cb, AbstractCriteriaQueryImpl<?> q, RootImpl<Object> r, Tree joins) {
		for (int i = 0; i < joins.getChildCount(); i++) {
			final Tree join = joins.getChild(i);

			final int joinType = join.getChild(0).getType();
			if (joinType == JpqlParser.FETCH) {
				FetchParent<?, ?> parent = this.getAliased(q, join.getChild(1).getText());

				final Qualified qualified = new Qualified(join.getChild(2));

				for (final String segment : qualified.getSegments()) {
					parent = parent.fetch(segment);
				}
			}
			else {
				final Aliased aliased = new Aliased(join.getChild(2));

				AbstractFrom<?, ?> parent = this.getAliased(q, join.getChild(1).getText());

				final LinkedList<String> segments = aliased.getQualified().getSegments();

				int depth = 0;
				for (int j = 0; j < segments.size(); j++) {
					final String segment = segments.get(j);

					if ((depth > 0) && (parent instanceof PluralJoin)) {
						throw new PersistenceException("Cannot qualify, only embeddable joins within the path allowed, " + "line " + join.getLine() + ":"
							+ join.getCharPositionInLine());
					}

					if (joinType == JpqlParser.LEFT) {
						parent = parent.join(segment, JoinType.LEFT);
					}
					else {
						parent = parent.join(segment, JoinType.INNER);
					}

					depth++;
				}

				parent.alias(aliased.getAlias());

				this.putAlias(q, join.getChild(1), aliased, parent);
			}
		}
	}

	/**
	 * Constructs an AND junction.
	 * 
	 * @param cb
	 *            the criteria builder
	 * @param disjunctionDef
	 *            the junction definition
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private Expression<Boolean> constructJunction(CriteriaBuilderImpl cb, Object q, Tree junctionDef) {
		final List<Expression<Boolean>> predictions = Lists.newArrayList();

		for (int i = 0; i < junctionDef.getChildCount(); i++) {
			final Tree childDef = junctionDef.getChild(i);
			if ((childDef.getType() == JpqlParser.LOR) || (childDef.getType() == JpqlParser.LAND)) {
				predictions.add(this.constructJunction(cb, q, childDef));
			}
			else {
				predictions.add(this.constructPredicate(cb, q, childDef));
			}
		}

		if (predictions.size() == 1) {
			return predictions.get(0);
		}

		if (junctionDef.getType() == JpqlParser.LOR) {
			return cb.or(predictions.toArray(new Predicate[predictions.size()]));
		}

		return cb.and(predictions.toArray(new Predicate[predictions.size()]));
	}

	/**
	 * Constructs the order by fragment of the query.
	 * 
	 * @param cb
	 *            the criteria builder
	 * @param q
	 *            the query
	 * @param orderBy
	 *            the order by definitions
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void constructOrder(CriteriaBuilderImpl cb, CriteriaQueryImpl<?> q, Tree orderBy) {
		final List<Order> orders = Lists.newArrayList();

		for (int i = 0; i < orderBy.getChildCount(); i++) {
			final Tree orderByItem = orderBy.getChild(i);
			final Order order = orderByItem.getChildCount() == 2 ? //
				cb.desc(this.getExpression(cb, q, orderByItem.getChild(0), null)) : //
				cb.asc(this.getExpression(cb, q, orderByItem.getChild(0), null));

			orders.add(order);
		}

		q.orderBy(orders);
	}

	/**
	 * Constructs a single prediction.
	 * 
	 * @param cb
	 *            the criteria builder
	 * @param predictionDef
	 *            the
	 * @return the predicate created
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <X> Expression<Boolean> constructPredicate(CriteriaBuilderImpl cb, Object q, Tree predictionDef) {
		if ((predictionDef.getType() == JpqlParser.Equals_Operator) //
			|| (predictionDef.getType() == JpqlParser.Not_Equals_Operator) //
			|| (predictionDef.getType() == JpqlParser.Greater_Than_Operator) //
			|| (predictionDef.getType() == JpqlParser.Greater_Or_Equals_Operator) //
			|| (predictionDef.getType() == JpqlParser.Less_Than_Operator) //
			|| (predictionDef.getType() == JpqlParser.Less_Or_Equals_Operator) //
			|| (predictionDef.getType() == JpqlParser.BETWEEN)) {

			final AbstractExpression<X> left;
			final AbstractExpression<X> right;

			if ((predictionDef.getChild(0).getType() == JpqlParser.ST_SUBQUERY) || (predictionDef.getChild(1).getType() == JpqlParser.ST_SUBQUERY)) {
				// left side is sub query
				if (predictionDef.getChild(0).getType() == JpqlParser.ST_SUBQUERY) {
					right = this.getExpression(cb, q, predictionDef.getChild(1), null);
					left = (AbstractExpression<X>) this.constructSubquery(cb, q, predictionDef.getChild(0), right.getJavaType());

					// right side is sub query
				}
				else if (predictionDef.getChild(1).getType() == JpqlParser.ST_SUBQUERY) {
					left = this.getExpression(cb, q, predictionDef.getChild(0), null);
					right = (AbstractExpression<X>) this.constructSubquery(cb, q, predictionDef.getChild(1), left.getJavaType());
				}
				else {
					throw new PersistenceException("Both sides of the comparison cannot be sub query, line " + predictionDef.getLine() + ":"
						+ predictionDef.getCharPositionInLine());
				}
			}
			else {
				left = this.getExpression(cb, q, predictionDef.getChild(0), null);
				right = (AbstractExpression<X>) this.getExpression(cb, q, predictionDef.getChild(1), left.getJavaType());
			}

			switch (predictionDef.getType()) {
				case JpqlParser.Equals_Operator:
					return cb.equal(left, right);

				case JpqlParser.Not_Equals_Operator:
					return cb.notEqual(left, right);

				case JpqlParser.Greater_Than_Operator:
					if (Comparable.class.isAssignableFrom(left.getJavaType())) {
						return cb.greaterThan((Expression<Comparable>) left, (Expression<Comparable>) right);
					}
					else {
						return cb.gt((Expression<? extends Number>) left, (Expression<? extends Number>) right);
					}

				case JpqlParser.Greater_Or_Equals_Operator:
					if (Comparable.class.isAssignableFrom(left.getJavaType())) {
						return cb.greaterThanOrEqualTo((Expression<Comparable>) left, (Expression<Comparable>) right);
					}
					else {
						return cb.ge((Expression<? extends Number>) left, (Expression<? extends Number>) right);
					}

				case JpqlParser.Less_Than_Operator:
					if (Comparable.class.isAssignableFrom(left.getJavaType())) {
						return cb.lessThan((Expression<Comparable>) left, (Expression<Comparable>) right);
					}
					else {
						return cb.lt((Expression<? extends Number>) left, (Expression<? extends Number>) right);
					}

				case JpqlParser.Less_Or_Equals_Operator:
					if (Comparable.class.isAssignableFrom(left.getJavaType())) {
						return cb.lessThanOrEqualTo((Expression<Comparable>) left, (Expression<Comparable>) right);
					}
					else {
						return cb.le((Expression<? extends Number>) left, (Expression<? extends Number>) right);
					}
				case JpqlParser.BETWEEN:
					final AbstractExpression<?> right2 = this.getExpression(cb, q, predictionDef.getChild(2), left.getJavaType());

					final Predicate between = cb.between((AbstractExpression) left, (AbstractExpression) right, (AbstractExpression) right2);
					if (predictionDef.getChildCount() == 4) {
						return between.not();
					}

					return between;
			}
		}

		if (predictionDef.getType() == JpqlParser.LIKE) {
			final AbstractExpression<String> inner = this.getExpression(cb, q, predictionDef.getChild(0), String.class);
			final AbstractExpression<String> pattern = this.getExpression(cb, q, predictionDef.getChild(1), String.class);

			if ((predictionDef.getChildCount() > 2) && (predictionDef.getChild(2).getType() == JpqlParser.STRING_LITERAL)) {
				final Expression<Character> escape = this.getExpression(cb, q, predictionDef.getChild(2), Character.class);

				if (predictionDef.getChild(predictionDef.getChildCount() - 1).getType() == JpqlParser.NOT) {
					return cb.notLike(inner, pattern, escape);
				}
				else {
					return cb.like(inner, pattern, escape);
				}
			}
			else {
				if (predictionDef.getChild(predictionDef.getChildCount() - 1).getType() == JpqlParser.NOT) {
					return cb.notLike(inner, pattern);
				}
				else {
					return cb.like(inner, pattern);
				}
			}
		}

		if (predictionDef.getType() == JpqlParser.ST_IN) {
			AbstractExpression<X> left = null;

			if ((predictionDef.getChild(0).getType() != JpqlParser.Named_Parameter) && (predictionDef.getChild(0).getType() != JpqlParser.Ordinal_Parameter)) {
				left = this.getExpression(cb, q, predictionDef.getChild(0), null);
			}

			final List<AbstractExpression<X>> expressions = Lists.newArrayList();

			final Tree inDefs = predictionDef.getChild(1);
			for (int i = 0; i < inDefs.getChildCount(); i++) {
				expressions.add((AbstractExpression<X>) this.getExpression(cb, q, inDefs.getChild(i), left != null ? left.getJavaType() : null));
			}

			if (left == null) {
				left = (AbstractExpression<X>) this.getExpression(cb, q, predictionDef.getChild(0), expressions.get(0).getJavaType());
			}

			return left.in(expressions);
		}

		if (predictionDef.getType() == JpqlParser.ST_NULL) {
			final AbstractExpression<Object> expr = this.getExpression(cb, q, predictionDef.getChild(0), null);

			if (predictionDef.getChildCount() == 2) {
				return cb.isNotNull(expr);
			}

			return cb.isNull(expr);
		}

		return this.getExpression(cb, q, predictionDef, Boolean.class);
	}

	/**
	 * Creates the select fragment of the query.
	 * 
	 * @param cb
	 *            the criteria builder
	 * @param q
	 *            the query
	 * @param from
	 *            the from metadata
	 * 
	 * @since $version
	 * @author hceylan
	 * @return
	 */
	private List<Selection<?>> constructSelect(CriteriaBuilderImpl cb, CriteriaQueryImpl<?> q, Tree selects) {
		final List<Selection<?>> selections = Lists.newArrayList();

		for (int i = 0; i < selects.getChildCount(); i++) {
			final Tree selectDef = selects.getChild(i);

			final AbstractSelection<?> selection = this.constructSingleSelect(cb, q, selectDef.getChild(0));

			if (selectDef.getChildCount() == 2) {
				selection.alias(selectDef.getChild(1).getText());
			}

			selections.add(selection);
		}

		q.updateResultClass(selections);

		return selections;
	}

	/**
	 * Constructs an select query.
	 * 
	 * @param cb
	 *            the criteria builder
	 * @param tree
	 *            the tree
	 * @return the constructed query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private CriteriaQueryImpl constructSelectQuery(CriteriaBuilderImpl cb, CommonTree tree) {
		final CriteriaQueryImpl q = new CriteriaQueryImpl(this.metamodel);

		this.constructFrom(cb, q, tree.getChild(1));

		final Tree select = tree.getChild(0);
		final List<Selection<?>> selections = this.constructSelect(cb, q, select.getChild(select.getChildCount() - 1));

		if (selections.size() == 1) {
			q.select(selections.get(0));
		}
		else {
			q.multiselect(selections);
		}

		if (select.getChild(0).getType() == JpqlParser.DISTINCT) {
			q.distinct(true);
		}

		int i = 2;
		while (true) {
			final Tree child = tree.getChild(i);

			// end of query
			if (child.getType() == JpqlParser.EOF) {
				break;
			}

			// where fragment
			if (child.getType() == JpqlParser.WHERE) {
				q.where(this.constructJunction(cb, q, child.getChild(0)));
			}

			// group by fragment
			if (child.getType() == JpqlParser.LGROUP_BY) {
				q.groupBy(this.constructGroupBy(cb, q, child));
			}

			// having fragment
			if (child.getType() == JpqlParser.HAVING) {
				q.having(this.constructJunction(cb, q, child.getChild(0)));
			}

			// order by fragment
			if (child.getType() == JpqlParser.LORDER) {
				this.constructOrder(cb, q, child);
			}

			i++;
			continue;
		}

		return q;
	}

	/**
	 * Creates a single select item.
	 * 
	 * @param cb
	 *            the criteria builder
	 * @param q
	 *            the query
	 * @param selectDef
	 *            the select definition
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private AbstractSelection<?> constructSingleSelect(CriteriaBuilderImpl cb, CriteriaQueryImpl<?> q, Tree selectDef) {
		// constructor select
		if (selectDef.getType() == JpqlParser.NEW) {
			final String className = new Qualified(selectDef.getChild(0)).toString();

			final List<Selection<?>> childSelections = Lists.newArrayList();
			final Tree arguments = selectDef.getChild(1);
			for (int i = 0; i < arguments.getChildCount(); i++) {
				final Tree argumentDef = arguments.getChild(i);

				childSelections.add(this.getExpression(cb, q, argumentDef, null));
			}

			try {
				return cb.construct(Class.forName(className.toString()), //
					childSelections.toArray(new Selection[childSelections.size()]));
			}
			catch (final ClassNotFoundException e) {
				throw new PersistenceException("Cannot load class: " + className + ", line " + selectDef.getLine() + ":" + selectDef.getCharPositionInLine());
			}
		}

		// object type
		if (selectDef.getType() == JpqlParser.OBJECT) {
			final String alias = selectDef.getChild(0).getText();
			return this.getAliased(q, alias);
		}

		return this.getExpression(cb, q, selectDef, null);
	}

	/**
	 * @param cb
	 *            the crtieria builder
	 * @param q
	 *            the critieria query
	 * @param subQueryDef
	 *            the sub query definition
	 * @return the sub query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private <T> SubqueryImpl<T> constructSubquery(CriteriaBuilderImpl cb, Object q, Tree subQueryDef, Class<T> javaType) {
		final SubqueryImpl<T> s;

		if (q instanceof CriteriaQueryImpl) {
			s = ((CriteriaQueryImpl<?>) q).subquery(javaType);
		}
		else if (q instanceof CriteriaUpdateImpl) {
			s = ((CriteriaUpdateImpl<?>) q).subquery(javaType);
		}
		else {
			s = ((CriteriaDeleteImpl<?>) q).subquery(javaType);
		}

		final Tree type = subQueryDef.getChild(0);
		if (type.getType() == JpqlParser.SELECT) {
			this.constructFrom(cb, s, subQueryDef.getChild(1));

			final Tree selectDef = subQueryDef.getChild(0).getChild(0);

			s.select(this.getExpression(cb, s, selectDef, javaType));

			if (subQueryDef.getChild(1).getType() == JpqlParser.DISTINCT) {
				s.distinct(true);
			}

			int i = 2;
			while (true) {
				final Tree child = subQueryDef.getChild(i);

				// en of sub query
				if (child == null) {
					break;
				}

				// where fragment
				if (child.getType() == JpqlParser.WHERE) {
					s.where(this.constructJunction(cb, s, child.getChild(0)));
				}

				// group by fragment
				if (child.getType() == JpqlParser.LGROUP_BY) {
					s.groupBy(this.constructGroupBy(cb, s, child));
				}

				// having fragment
				if (child.getType() == JpqlParser.HAVING) {
					s.having(this.constructJunction(cb, s, child.getChild(0)));
				}

				i++;
				continue;
			}
		}

		return s;
	}

	/**
	 * Constructs an update query.
	 * 
	 * @param cb
	 *            the criteria builder
	 * @param tree
	 *            the tree
	 * 
	 * @since $version
	 * @author hceylan
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private CriteriaUpdateImpl<?> constructUpdateQuery(CriteriaBuilderImpl cb, CommonTree tree) {
		final CriteriaUpdateImpl<?> q = new CriteriaUpdateImpl(this.metamodel);

		final Tree deleteDef = tree.getChild(0);

		final Tree aliasedDef = deleteDef.getChild(0);
		final Aliased aliased = new Aliased(aliasedDef);

		final EntityTypeImpl entity = this.getEntity(aliased.getQualified().toString());
		final RootImpl<?> r = (RootImpl<?>) q.from(entity);
		this.putAlias(q, aliasedDef, aliased, r);

		final Tree setDefs = tree.getChild(1);
		for (int i = 0; i < setDefs.getChildCount(); i++) {
			final Tree setDef = setDefs.getChild(i);
			this.getExpression(cb, q, setDef.getChild(0), null);
		}

		return q;
	}

	/**
	 * Creates a typed query for the JPQL.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @param <T>
	 *            the result type
	 * @return the typed query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public <T> QueryImpl<T> createTypedQuery(EntityManagerImpl entityManager) {
		if (this.lastUsed != Long.MAX_VALUE) {
			this.lastUsed = System.currentTimeMillis();
		}

		final QueryImpl<T> typedQuery = new QueryImpl<T>((BaseQueryImpl<T>) this.q, entityManager);

		if (this.lockMode != null) {
			typedQuery.setLockMode(this.lockMode);
		}

		if (this.hints != null) {
			for (final Entry<String, Object> entry : this.hints.entrySet()) {
				typedQuery.setHint(entry.getKey(), entry.getValue());
			}
		}

		return typedQuery;
	}

	private AbstractFrom<?, ?> getAliased(Object q, String alias) {
		final Map<String, AbstractFrom<?, ?>> aliasMap = this.aliasMap.get(q);

		if (aliasMap != null) {
			final AbstractFrom<?, ?> from = aliasMap.get(alias);

			if (from != null) {
				return from;
			}
		}

		if (q instanceof Subquery) {
			final SubqueryImpl<?> s = (SubqueryImpl<?>) q;
			final AbstractFrom<?, ?> aliased = this.getAliased(s.getParent(), alias);

			if (aliased instanceof RootImpl) {
				s.correlate((RootImpl<?>) aliased);
			}

			return aliased;
		}

		throw new PersistenceException("Alias is not bound: " + alias);
	}

	private EntityTypeImpl<Object> getEntity(String entityName) {
		final EntityTypeImpl<Object> entity = this.metamodel.entity(entityName);

		if (entity == null) {
			throw new PersistenceException("Type is not managed: " + entityName);
		}

		return entity;
	}

	/**
	 * Constructs and returns the expression.
	 * 
	 * @param cb
	 *            the criteria builder
	 * @param exprDef
	 *            the definition of the expression
	 * @return the expression
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	private <X, C extends Collection<E>, E> AbstractExpression<X> getExpression(CriteriaBuilderImpl cb, Object q, Tree exprDef, Class<X> javaType) {
		// identification variable
		if (exprDef.getType() == JpqlParser.ID) {
			return (AbstractExpression<X>) this.getAliased(q, exprDef.getText());
		}

		// single valued state field expression
		if (exprDef.getType() == JpqlParser.ST_PARENTED) {
			AbstractSelection<?> expression = this.getAliased(q, exprDef.getChild(0).getText());

			final Qualified qualified = new Qualified(exprDef.getChild(1));

			final LinkedList<String> segments = qualified.getSegments();

			for (int i = 0; i < segments.size(); i++) {
				final String segment = segments.get(i);

				if (expression instanceof ParentPath) {
					expression = ((ParentPath<?, ?>) expression).getExpression(segment);
				}
				else {
					throw new PersistenceException("Cannot dereference: " + segment + ", line " + exprDef.getLine() + ":" + exprDef.getCharPositionInLine());
				}

			}

			return (AbstractExpression<X>) expression;
		}

		// negation
		if (exprDef.getType() == JpqlParser.ST_NEGATION) {
			return (AbstractExpression<X>) cb.neg(this.<Number, Collection<Object>, Object> getExpression(cb, q, exprDef.getChild(0), null));
		}

		if (exprDef.getType() == JpqlParser.Named_Parameter) {
			return cb.parameter(javaType, exprDef.getText().substring(1));
		}

		if (exprDef.getType() == JpqlParser.Ordinal_Parameter) {
			return cb.parameter(javaType, exprDef.getText().substring(1));
		}

		// arithmetic operation
		if ((exprDef.getType() == JpqlParser.Plus_Sign) //
			|| (exprDef.getType() == JpqlParser.Minus_Sign) //
			|| (exprDef.getType() == JpqlParser.Multiplication_Sign) //
			|| (exprDef.getType() == JpqlParser.Division_Sign)) {

			final AbstractExpression<Number> left = this.getExpression(cb, q, exprDef.getChild(0), null);
			final AbstractExpression<? extends Number> right = this.getExpression(cb, q, exprDef.getChild(1), left.getJavaType());

			switch (exprDef.getType()) {
				case JpqlParser.Plus_Sign:
					return (AbstractExpression<X>) cb.sum(left, right);

				case JpqlParser.Minus_Sign:
					return (AbstractExpression<X>) cb.diff(left, right);

				case JpqlParser.Multiplication_Sign:
					return (AbstractExpression<X>) cb.prod(left, right);

				case JpqlParser.Division_Sign:
					return (AbstractExpression<X>) cb.quot(left, right);
			}
		}

		if (exprDef.getType() == JpqlParser.ST_BOOLEAN) {
			return (AbstractExpression<X>) this.getExpression(cb, q, exprDef, Boolean.class);
		}

		if (exprDef.getType() == JpqlParser.NUMERIC_LITERAL) {
			return (AbstractExpression<X>) new ConstantExpression<Long>(this.metamodel.createBasicType(Long.class), Long.valueOf(exprDef.getText()));
		}

		// string literal
		if (exprDef.getType() == JpqlParser.STRING_LITERAL) {
			if (javaType == Character.class) {
				return (AbstractExpression<X>) new ConstantExpression<Character>(this.metamodel.type(Character.class), //
					exprDef.getText().substring(1, 2).toCharArray()[0]);
			}

			return (AbstractExpression<X>) new ConstantExpression<String>(this.metamodel.type(String.class), //
				exprDef.getText().substring(1, exprDef.getText().length() - 1));
		}

		// functions returning string
		if ((exprDef.getType() == JpqlParser.UPPER) //
			|| (exprDef.getType() == JpqlParser.LOWER) //
			|| (exprDef.getType() == JpqlParser.SUBSTRING)) {

			final AbstractExpression<String> argument = this.getExpression(cb, q, exprDef.getChild(0), null);

			switch (exprDef.getType()) {
				case JpqlParser.UPPER:
					return (AbstractExpression<X>) cb.upper(argument);

				case JpqlParser.LOWER:
					return (AbstractExpression<X>) cb.lower(argument);

				case JpqlParser.SUBSTRING:
					final AbstractExpression<Integer> start = this.getExpression(cb, q, exprDef.getChild(1), Integer.class);
					final AbstractExpression<Integer> end = exprDef.getChildCount() == 3 ? //
						this.getExpression(cb, q, exprDef.getChild(2), Integer.class) : null;

					return (AbstractExpression<X>) new SubstringExpression(argument, start, end);
			}
		}

		// concat function
		if (exprDef.getType() == JpqlParser.CONCAT) {
			final List<Expression<String>> arguments = Lists.newArrayList();
			for (int i = 0; i < exprDef.getChildCount(); i++) {
				arguments.add(this.getExpression(cb, q, exprDef.getChild(i), String.class));
			}

			return (AbstractExpression<X>) new ConcatExpression(arguments.toArray(new Expression[arguments.size()]));
		}

		// trim function
		if (exprDef.getType() == JpqlParser.TRIM) {
			Trimspec trimspec = null;
			Expression<Character> trimChar = null;
			Expression<String> inner = null;

			int i = 0;
			final int type = exprDef.getChild(i).getType();

			// trim spec
			if (type == JpqlParser.BOTH) {
				trimspec = Trimspec.BOTH;
				i++;
			}
			else if (type == JpqlParser.LEADING) {
				trimspec = Trimspec.LEADING;
				i++;
			}
			else if (type == JpqlParser.TRAILING) {
				trimspec = Trimspec.TRAILING;
				i++;
			}

			if (exprDef.getChildCount() > (i + 1)) {
				trimChar = this.getExpression(cb, q, exprDef.getChild(i), Character.class);
				inner = this.getExpression(cb, q, exprDef.getChild(i + 1), String.class);
			}
			else {
				inner = this.getExpression(cb, q, exprDef.getChild(i), String.class);
			}

			return (AbstractExpression<X>) new TrimExpression(trimspec, trimChar, inner);
		}

		// type functions
		if ((exprDef.getType() == JpqlParser.TYPE) || (exprDef.getType() == JpqlParser.ST_ENTITY_TYPE)) {
			switch (exprDef.getType()) {
				case JpqlParser.TYPE:
					final AbstractExpression<?> inner = this.getExpression(cb, q, exprDef.getChild(0), null);

					return (AbstractExpression<X>) ((AbstractPath<?>) inner).type();

				case JpqlParser.ST_ENTITY_TYPE:
					final EntityTypeImpl<?> entity = this.getEntity(exprDef.getChild(0).getText());
					if (entity.getRootType().getInheritanceType() == null) {
						throw new PersistenceException("Entity does not have inheritence: " + entity.getName() + ", line " + exprDef.getLine() + ":"
							+ exprDef.getCharPositionInLine());
					}

					return (AbstractExpression<X>) new ConstantExpression<String>(null, entity.getDiscriminatorValue());
			}
		}

		// date time functions
		switch (exprDef.getType()) {
			case JpqlParser.CURRENT_DATE:
				return (AbstractExpression<X>) cb.currentDate();

			case JpqlParser.CURRENT_TIME:
				return (AbstractExpression<X>) cb.currentTime();

			case JpqlParser.CURRENT_TIMESTAMP:
				return (AbstractExpression<X>) cb.currentTimestamp();
		}

		// arithmetic functions
		switch (exprDef.getType()) {
			case JpqlParser.ABS:
				return (AbstractExpression<X>) cb.abs(this.getExpression(cb, q, exprDef.getChild(0), Number.class));

			case JpqlParser.SQRT:
				return (AbstractExpression<X>) cb.sqrt(this.getExpression(cb, q, exprDef.getChild(0), Number.class));

			case JpqlParser.MOD:
				return (AbstractExpression<X>) cb.mod(//
					this.getExpression(cb, q, exprDef.getChild(0), Integer.class), //
					this.getExpression(cb, q, exprDef.getChild(1), Integer.class));

			case JpqlParser.LOCATE:
				if (exprDef.getChildCount() == 3) {
					return (AbstractExpression<X>) cb.locate(//
						this.getExpression(cb, q, exprDef.getChild(0), String.class), //
						this.getExpression(cb, q, exprDef.getChild(1), String.class), //
						this.getExpression(cb, q, exprDef, Integer.class));
				}

				return (AbstractExpression<X>) cb.locate(//
					this.getExpression(cb, q, exprDef.getChild(0), String.class), //
					this.getExpression(cb, q, exprDef.getChild(1), String.class));

			case JpqlParser.LENGTH:
				return (AbstractExpression<X>) cb.length(this.getExpression(cb, q, exprDef.getChild(0), String.class));
		}

		// aggregate functions
		switch (exprDef.getType()) {
			case JpqlParser.AVG:
				return (AbstractExpression<X>) cb.avg(this.getExpression(cb, q, exprDef.getChild(0), Number.class));
			case JpqlParser.SUM:
				return (AbstractExpression<X>) cb.sum(this.getExpression(cb, q, exprDef.getChild(0), Long.class));
			case JpqlParser.MAX:
				return (AbstractExpression<X>) cb.max(this.getExpression(cb, q, exprDef.getChild(0), Number.class));
			case JpqlParser.MIN:
				return (AbstractExpression<X>) cb.min(this.getExpression(cb, q, exprDef.getChild(0), Number.class));
		}

		// count function
		if (exprDef.getType() == JpqlParser.COUNT) {
			if (exprDef.getChildCount() == 2) {
				return (AbstractExpression<X>) new CountExpression(this.getExpression(cb, q, exprDef.getChild(1), null), true);
			}

			return (AbstractExpression<X>) new CountExpression(this.getExpression(cb, q, exprDef.getChild(0), null), false);
		}

		// all or any operator
		if (exprDef.getType() == JpqlParser.ST_ALL_OR_ANY) {
			// all, any, some expressions
			switch (exprDef.getChild(0).getType()) {
				case JpqlParser.ALL:
					return new AllAnyExpression<X>(true, this.constructSubquery(cb, q, exprDef.getChild(1), javaType));

				case JpqlParser.ANY:
				case JpqlParser.SOME:
					return new AllAnyExpression<X>(false, this.constructSubquery(cb, q, exprDef.getChild(1), javaType));
			}
		}

		// exists operator
		if (exprDef.getType() == JpqlParser.EXISTS) {
			return (AbstractExpression<X>) new ExistsExpression(this.constructSubquery(cb, q, exprDef.getChild(0), javaType));
		}

		// not operator
		if (exprDef.getType() == JpqlParser.NOT) {
			return (AbstractExpression<X>) new PredicateImpl(true, BooleanOperator.AND, this.getExpression(cb, q, exprDef.getChild(0), Boolean.class));
		}

		// general case
		if (exprDef.getType() == JpqlParser.ST_GENERAL_CASE) {
			final CaseImpl<Object> caseExpr = cb.selectCase();

			for (int i = 0; i < exprDef.getChildCount(); i++) {
				final Tree caseDef = exprDef.getChild(i);

				if (caseDef.getType() == JpqlParser.WHEN) {
					caseExpr.when(this.constructJunction(cb, q, caseDef.getChild(0)), this.getExpression(cb, q, caseDef.getChild(1), null));
				}
				else {
					caseExpr.otherwise(this.getExpression(cb, q, caseDef, null));
				}
			}

			return (AbstractExpression<X>) caseExpr;
		}

		// simple case
		if (exprDef.getType() == JpqlParser.CASE) {
			final AbstractExpression<X> expression = this.getExpression(cb, q, exprDef.getChild(0), null);
			final SimpleCaseImpl<X, Object> caseExpr = cb.selectCase(expression);

			for (int i = 1; i < exprDef.getChildCount(); i++) {
				final Tree caseDef = exprDef.getChild(i);

				if (caseDef.getType() == JpqlParser.WHEN) {
					final AbstractExpression<Object> result = this.getExpression(cb, q, caseDef.getChild(1), null);

					final AbstractExpression<X> condition;

					if (exprDef.getChild(0).getType() == JpqlParser.TYPE) {
						final EntityTypeImpl<Object> entity = this.getEntity(caseDef.getChild(0).getText());

						if (entity.getRootType().getInheritanceType() == null) {
							throw new PersistenceException("Entity does not have inheritence: " + entity.getName() + ", line " + exprDef.getLine() + ":"
								+ exprDef.getCharPositionInLine());
						}

						condition = (AbstractExpression<X>) new ConstantExpression<String>(null, entity.getDiscriminatorValue());
					}
					else {
						condition = this.getExpression(cb, q, caseDef.getChild(0), null);
					}

					caseExpr.when(condition, result);
				}
				else {
					caseExpr.otherwise(this.getExpression(cb, q, caseDef, null));
				}
			}

			return (AbstractExpression<X>) caseExpr;
		}

		// nullif function
		if (exprDef.getType() == JpqlParser.NULLIF) {
			final AbstractExpression<X> left = this.getExpression(cb, q, exprDef.getChild(0), null);
			final AbstractExpression<?> right = this.getExpression(cb, q, exprDef.getChild(1), null);

			return new NullIfExpression<X>(left, right);
		}

		// coalesce function
		if (exprDef.getType() == JpqlParser.ST_COALESCE) {
			final CoalesceExpression<X> coalesce = cb.coalesce();
			for (int i = 0; i < exprDef.getChildCount(); i++) {
				coalesce.value(this.getExpression(cb, q, exprDef.getChild(i), javaType));
			}

			return coalesce;
		}

		// db func
		if (exprDef.getType() == JpqlParser.FUNC) {
			final List<AbstractExpression<?>> arguments = Lists.newArrayList();
			final String function = exprDef.getChild(0).getText();

			for (int i = 1; i < exprDef.getChildCount(); i++) {
				arguments.add(this.getExpression(cb, q, exprDef.getChild(i), null));
			}

			return new FunctionExpression<X>((Class<X>) (javaType != null ? javaType : Object.class), //
				function, arguments.toArray(new Expression<?>[arguments.size()]));
		}

		// index expression
		if (exprDef.getType() == JpqlParser.INDEX) {
			final AbstractExpression<Object> expression = this.getExpression(cb, q, exprDef.getChild(0), null);

			if (expression instanceof ListJoinImpl) {
				return (AbstractExpression<X>) ((ListJoinImpl<?, ?>) expression).index();
			}

			throw new PersistenceException("Reference is not a list join, line " + exprDef.getLine() + ":" + exprDef.getCharPositionInLine());
		}

		// empty operation
		if (exprDef.getType() == JpqlParser.ST_EMPTY) {
			AbstractExpression<?> expression = this.getExpression(cb, q, exprDef.getChild(0), null);

			if (expression instanceof MapExpression) {
				expression = ((MapExpression<Map<?, ?>, ?, ?>) expression).values();
			}

			if (!(expression instanceof CollectionExpression<?, ?>)) {
				throw new PersistenceException("Reference is not a collection, line " + exprDef.getLine() + ":" + exprDef.getCharPositionInLine());
			}

			if (exprDef.getChildCount() == 2) {
				return (AbstractExpression<X>) cb.isNotEmpty((Expression<Collection<?>>) expression);
			}
			else {
				return (AbstractExpression<X>) cb.isEmpty((Expression<Collection<?>>) expression);
			}
		}

		// member of operation
		if (exprDef.getType() == JpqlParser.ST_MEMBER) {
			final AbstractExpression<?> expression = this.getExpression(cb, q, exprDef.getChild(1), null);
			if (!(expression instanceof CollectionExpression)) {
				throw new PersistenceException("Member of expression must evaluate to a collection expression, " + exprDef.getLine() + ":"
					+ exprDef.getCharPositionInLine());
			}

			final CollectionExpression<C, E> collection = (CollectionExpression<C, E>) expression;
			final PluralAttributeImpl<?, C, E> attribute = (PluralAttributeImpl<?, C, E>) collection.getMapping().getAttribute();

			final AbstractExpression<E> elem = this.getExpression(cb, q, exprDef.getChild(0), attribute.getElementType().getJavaType());

			if (exprDef.getChildCount() == 3) {
				return (AbstractExpression<X>) cb.isNotMember(elem, collection);
			}
			else {
				return (AbstractExpression<X>) cb.isMember(elem, collection);
			}
		}

		// size operation
		if (exprDef.getType() == JpqlParser.SIZE) {
			final AbstractExpression<?> expression = this.getExpression(cb, q, exprDef.getChild(0), null);
			if (!(expression instanceof CollectionExpression)) {
				throw new PersistenceException("Member of expression must evaluate to a collection expression, " + exprDef.getLine() + ":"
					+ exprDef.getCharPositionInLine());
			}

			final CollectionExpression<C, E> collection = (CollectionExpression<C, E>) expression;

			return (AbstractExpression<X>) cb.size(collection);
		}

		throw new PersistenceException("Unhandled expression: " + exprDef.toStringTree() + ", line " + exprDef.getLine() + ":"
			+ exprDef.getCharPositionInLine());
	}

	/**
	 * Returns the time query last used.
	 * 
	 * @return the the time query last used
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public long getLastUsed() {
		return this.lastUsed;
	}

	/**
	 * Returns the query.
	 * 
	 * @return the query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getQueryString() {
		return this.qlString;
	}

	/**
	 * Parses the JPQL.
	 * 
	 * @param cb
	 *            the criteria builder
	 * @return the criteria constructed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private BaseQueryImpl<?> parse(CriteriaBuilderImpl cb) {
		final CommonTree tree = this.parse(this.qlString);

		JpqlQuery.LOG.debug("Parsed query successfully {0}", //
			JpqlQuery.LOG.lazyBoxed(this.qlString, new Object[] { tree.toStringTree() }));

		return this.construct(cb, tree);
	}

	private CommonTree parse(String query) {
		try {
			final JpqlLexer lexer = new JpqlLexer(new ANTLRStringStream(query));
			final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
			final JpqlParser parser = new JpqlParser(tokenStream);

			final ql_statement_return ql_statement = parser.ql_statement();
			final CommonTree tree = (CommonTree) ql_statement.getTree();

			final List<String> errors = parser.getErrors();
			if (errors.size() > 0) {
				final String errorMsg = Joiner.on("\n\t").join(errors);

				JpqlQuery.LOG.error("Cannot parse query: {0}", //
					JpqlQuery.LOG.boxed(query, //
						new Object[] { "\n\t" + errorMsg, "\n\n" + tree.toStringTree() + "\n" }));

				throw new PersistenceException("cannot parse the query:\n " + errorMsg);
			}

			return tree;
		}
		catch (final Exception e) {
			throw new PersistenceException("Cannot parse jpql: " + e.getMessage(), e);
		}
	}

	private void putAlias(BaseQuery<?> q, Tree aliasedDef, final Aliased aliased, final AbstractFrom<?, ?> r) {
		Map<String, AbstractFrom<?, ?>> aliasMap = this.aliasMap.get(q);
		if (aliasMap == null) {
			aliasMap = Maps.newHashMap();
			this.aliasMap.put(q, aliasMap);
		}

		final String alias = aliased.getAlias();
		if (aliasMap.containsKey(alias)) {
			throw new PersistenceException("Alias already exists: " + alias + ", line " + aliasedDef.getLine() + ":" + aliasedDef.getCharPositionInLine());
		}

		aliasMap.put(aliased.getAlias(), r);
	}
}
