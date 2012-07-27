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
package org.batoo.jpa.core.impl.criteria.jpql;

import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.criteria.AbstractSelection;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.criteria.expression.AbstractExpression;
import org.batoo.jpa.core.impl.criteria.expression.ConcatExpression;
import org.batoo.jpa.core.impl.criteria.expression.ConstantExpression;
import org.batoo.jpa.core.impl.criteria.expression.SubstringExpression;
import org.batoo.jpa.core.impl.criteria.join.AbstractFrom;
import org.batoo.jpa.core.impl.criteria.path.AbstractPath;
import org.batoo.jpa.core.impl.manager.EntityManagerFactoryImpl;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.jpql.JpqlLexer;
import org.batoo.jpa.jpql.JpqlParser;
import org.batoo.jpa.jpql.JpqlParser.ql_statement_return;

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

	private final CriteriaQueryImpl<?> criteriaQuery;
	private final Map<String, AbstractFrom<?, ?>> aliasMap = Maps.newHashMap();

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
		super();

		this.metamodel = entityManagerFactory.getMetamodel();
		this.qlString = qlString;

		this.criteriaQuery = this.parse();
	}

	/**
	 * Constructs the query object
	 * 
	 * @param tree
	 * 
	 * @since $version
	 * @author hceylan
	 * @return
	 */
	private CriteriaQueryImpl<Object> construct(CommonTree tree) {
		final CriteriaBuilderImpl cb = this.metamodel.getEntityManagerFactory().getCriteriaBuilder();
		final CriteriaQueryImpl<Object> q = cb.createQuery(Object.class);

		final Tree type = tree.getChild(0);
		if (type.getType() == JpqlParser.SELECT) {
			JpqlQuery.LOG.debug("This is a select query");

			if (tree.getChild(1).getType() == JpqlParser.DISTINCT) {
				JpqlQuery.LOG.debug("Distinct is set");
			}

			this.constructFrom(cb, q, tree.getChild(1));

			final List<Selection<?>> selections = this.constructSelect(cb, q, tree.getChild(0).getChild(0));

			if (selections.size() == 1) {
				q.select(selections.get(0));
			}
			else {
				q.multiselect(selections);
			}

			if (tree.getChildCount() > 2) {
				final Tree child = tree.getChild(2);
				if (child.getType() == JpqlParser.WHERE) {
					q.where(this.constructJunction(cb, tree.getChild(2).getChild(0)));
				}
			}
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
	private void constructFrom(CriteriaBuilderImpl cb, CriteriaQueryImpl<?> q, Tree froms) {
		for (int i = 0; i < froms.getChildCount(); i++) {
			final Tree from = froms.getChild(i);
			final Aliased fromDef = new Aliased(this.aliasMap, from.getChild(0), false);

			final EntityTypeImpl<Object> entity = this.metamodel.entity(fromDef.getQualified().toString());
			final RootImpl<Object> r = q.from(entity);
			r.alias(fromDef.getAlias());

			this.aliasMap.put(fromDef.getAlias(), r);

			this.constructJoins(cb, q, r, from.getChild(1));
		}
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
	private void constructJoins(CriteriaBuilderImpl cb, CriteriaQueryImpl<?> q, RootImpl<Object> r, Tree joins) {
		for (int i = 0; i < joins.getChildCount(); i++) {
			final Tree join = joins.getChild(i);

			final int joinType = join.getChild(0).getType();
			if (joinType == JpqlParser.FETCH) {
				FetchParent<?, ?> parent = this.aliasMap.get(join.getChild(1).getText());

				final Qualified qualified = new Qualified(join.getChild(2), false);

				for (final String segment : qualified.getSegments()) {
					parent = parent.fetch(segment);
				}
			}
			else {
				final Aliased aliased = new Aliased(this.aliasMap, join.getChild(2), false);

				AbstractFrom<?, ?> parent = this.getAliased(join.getChild(1).getText());

				for (final String segment : aliased.getQualified().getSegments()) {
					if (joinType == JpqlParser.LEFT) {
						parent = parent.join(segment, JoinType.LEFT);
					}
					else {
						parent = parent.join(segment, JoinType.INNER);
					}
				}

				parent.alias(aliased.getAlias());

				this.aliasMap.put(aliased.getAlias(), parent);
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
	private Expression<Boolean> constructJunction(CriteriaBuilderImpl cb, Tree junctionDef) {
		final List<Expression<Boolean>> predictions = Lists.newArrayList();

		for (int i = 0; i < junctionDef.getChildCount(); i++) {
			final Tree childDef = junctionDef.getChild(i);
			if ((childDef.getType() == JpqlParser.LOR) || (childDef.getType() == JpqlParser.LAND)) {
				predictions.add(this.constructJunction(cb, childDef));
			}
			else {
				predictions.add(this.constructPredicate(cb, childDef));
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
	private <X> Expression<Boolean> constructPredicate(CriteriaBuilderImpl cb, Tree predictionDef) {
		if ((predictionDef.getType() == JpqlParser.Equals_Operator) //
			|| (predictionDef.getType() == JpqlParser.Not_Equals_Operator) //
			|| (predictionDef.getType() == JpqlParser.Greater_Than_Operator) //
			|| (predictionDef.getType() == JpqlParser.Greater_Or_Equals_Operator) //
			|| (predictionDef.getType() == JpqlParser.Less_Than_Operator) //
			|| (predictionDef.getType() == JpqlParser.Less_Or_Equals_Operator) //
			|| (predictionDef.getType() == JpqlParser.BETWEEN) //
			|| (predictionDef.getType() == JpqlParser.LIKE)) {

			final AbstractExpression<X> left = this.<X> getExpression(cb, predictionDef.getChild(0), null);
			final AbstractExpression<? extends X> right = this.getExpression(cb, predictionDef.getChild(1), left.getJavaType());

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
					final AbstractExpression<?> right2 = this.getExpression(cb, predictionDef.getChild(2), left.getJavaType());

					final Predicate between = cb.between((AbstractExpression) left, (AbstractExpression) right, (AbstractExpression) right2);
					if (predictionDef.getChildCount() == 4) {
						return between.not();
					}

					return between;

				case JpqlParser.LIKE:
					final Predicate like = cb.like((Expression<String>) left, (Expression<String>) right);

					if (predictionDef.getChildCount() == 3) {
						return like.not();
					}

					return like;
			}
		}

		if (predictionDef.getType() == JpqlParser.ST_IN) {
			final AbstractExpression<?> left = this.<X> getExpression(cb, predictionDef.getChild(0), null);

			final List<AbstractExpression<?>> expressions = Lists.newArrayList();

			final Tree inDefs = predictionDef.getChild(1);
			for (int i = 0; i < inDefs.getChildCount(); i++) {
				expressions.add(this.getExpression(cb, inDefs.getChild(i), left.getJavaType()));
			}

			return left.in(expressions);
		}

		if (predictionDef.getType() == JpqlParser.ST_NULL) {
			final AbstractExpression<Object> expr = this.getExpression(cb, predictionDef.getChild(0), null);

			if (predictionDef.getChildCount() == 2) {
				return cb.isNotNull(expr);
			}

			return cb.isNull(expr);
		}

		return this.getBooleanExpression(predictionDef);
	}

	/**
	 * Creates the select fragment of the query
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
	private List<Selection<?>> constructSelect(CriteriaBuilderImpl cb, CriteriaQueryImpl<Object> q, Tree selects) {
		final List<Selection<?>> selections = Lists.newArrayList();

		for (int i = 0; i < selects.getChildCount(); i++) {
			final Tree selectDef = selects.getChild(i);

			final AbstractSelection<?> selection = this.constructSingleSelect(cb, selectDef.getChild(0));

			if (selectDef.getChildCount() == 2) {
				selection.alias(selectDef.getChild(1).getText());
			}

			selections.add(selection);
		}

		return selections;
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
	private AbstractSelection<?> constructSingleSelect(CriteriaBuilderImpl cb, Tree selectDef) {
		// constructor select
		if (selectDef.getType() == JpqlParser.NEW) {
			final String className = new Qualified(selectDef.getChild(0)).toString();

			final List<Selection<?>> childSelections = Lists.newArrayList();
			final Tree arguments = selectDef.getChild(1);
			for (int i = 0; i < arguments.getChildCount(); i++) {
				final Tree argumentDef = arguments.getChild(i);

				childSelections.add(this.getExpression(cb, argumentDef, null));
			}

			try {
				return cb.construct(Class.forName(className.toString()), //
					childSelections.toArray(new Selection[childSelections.size()]));
			}
			catch (final ClassNotFoundException e) {
				throw new PersistenceException("Cannot load class: " + className);
			}
		}

		// object type
		if (selectDef.getType() == JpqlParser.OBJECT) {
			final String alias = selectDef.getChild(0).getText();
			final AbstractFrom<?, ?> from = this.getAliased(alias);

			return (AbstractSelection<?>) from.type();
		}

		return this.getExpression(cb, selectDef, null);
	}

	/**
	 * Creates a typed query for the JPQL.
	 * 
	 * @param entityManager
	 *            the entity manager
	 * @param resutClass
	 *            the result class
	 * @param <T>
	 *            the result type
	 * @return the typed query
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public <T> TypedQueryImpl<T> createTypedQuery(EntityManagerImpl entityManager, Class<T> resutClass) {
		return new TypedQueryImpl<T>((CriteriaQueryImpl<T>) this.criteriaQuery, entityManager);
	}

	private AbstractFrom<?, ?> getAliased(String alias) {
		final AbstractFrom<?, ?> from = this.aliasMap.get(alias);
		if (from == null) {
			throw new PersistenceException("Alias is not bound: " + alias);
		}

		return from;
	}

	@SuppressWarnings("unchecked")
	private Expression<Boolean> getBooleanExpression(Tree predictionDef) {
		AbstractPath<?> expr = this.getAliased(predictionDef.getChild(0).getText());

		final Qualified qualified = new Qualified(predictionDef.getChild(1));
		for (final String segment : qualified.getSegments()) {
			expr = expr.get(segment);
		}

		return (Expression<Boolean>) expr;
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
	private <X> AbstractExpression<X> getExpression(CriteriaBuilderImpl cb, Tree exprDef, Class<X> javaType) {
		// identification variable
		if (exprDef.getType() == JpqlParser.ID) {
			return (AbstractExpression<X>) this.getAliased(exprDef.getText());
		}

		// single valued state field expression
		if (exprDef.getType() == JpqlParser.ST_PARENTED) {
			AbstractSelection<?> selection = this.getAliased(exprDef.getChild(0).getText());

			final Qualified qualified = new Qualified(exprDef.getChild(1));
			for (final String segment : qualified.getSegments()) {
				if (selection instanceof AbstractFrom) {
					selection = ((AbstractFrom<?, ?>) selection).get(segment);
				}
				else if (selection instanceof AbstractPath) {
					selection = ((AbstractPath<?>) selection).get(segment);
				}
				else {
					throw new IllegalArgumentException("Cannot dereference: " + segment);
				}

			}

			return (AbstractExpression<X>) selection;
		}

		// negation
		if (exprDef.getType() == JpqlParser.ST_NEGATION) {
			return (AbstractExpression<X>) cb.neg(this.<Number> getExpression(cb, exprDef.getChild(0), null));
		}

		if (exprDef.getType() == JpqlParser.Named_Parameter) {
			return cb.parameter(javaType, exprDef.getText().substring(1));
		}

		// arithmetic operation
		if ((exprDef.getType() == JpqlParser.Plus_Sign) //
			|| (exprDef.getType() == JpqlParser.Minus_Sign) //
			|| (exprDef.getType() == JpqlParser.Multiplication_Sign) //
			|| (exprDef.getType() == JpqlParser.Division_Sign)) {

			final AbstractExpression<Number> left = this.getExpression(cb, exprDef.getChild(0), null);
			final AbstractExpression<? extends Number> right = this.getExpression(cb, exprDef.getChild(1), left.getJavaType());

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
			return (AbstractExpression<X>) this.getBooleanExpression(exprDef);
		}

		if (exprDef.getType() == JpqlParser.Ordinal_Parameter) {
			// TODO Handle
			return null;
		}

		if (exprDef.getType() == JpqlParser.NUMERIC_LITERAL) {
			return (AbstractExpression<X>) new ConstantExpression<Long>(this.metamodel.createBasicType(Long.class), Long.valueOf(exprDef.getText()));
		}

		// functions returning string
		if ((exprDef.getType() == JpqlParser.UPPER) //
			|| (exprDef.getType() == JpqlParser.LOWER) //
			|| (exprDef.getType() == JpqlParser.SUBSTRING)) {

			final AbstractExpression<String> argument = this.getExpression(cb, exprDef.getChild(0), null);

			switch (exprDef.getType()) {
				case JpqlParser.UPPER:
					return (AbstractExpression<X>) cb.upper(argument);

				case JpqlParser.LOWER:
					return (AbstractExpression<X>) cb.lower(argument);

				case JpqlParser.SUBSTRING:
					final AbstractExpression<Number> start = this.getExpression(cb, exprDef.getChild(1), Number.class);
					final AbstractExpression<Number> end = exprDef.getChildCount() == 3 ? this.getExpression(cb, exprDef.getChild(2), Number.class) : null;

					return (AbstractExpression<X>) new SubstringExpression(argument, start, end);
			}
		}

		if (exprDef.getType() == JpqlParser.CONCAT) {
			final List<Expression<String>> arguments = Lists.newArrayList();
			for (int i = 0; i < exprDef.getChildCount(); i++) {
				arguments.add(this.getExpression(cb, exprDef.getChild(i), String.class));
			}

			return (AbstractExpression<X>) new ConcatExpression(arguments.toArray(new Expression[arguments.size()]));
		}

		if (exprDef.getType() == JpqlParser.STRING_LITERAL) {
			return (AbstractExpression<X>) new ConstantExpression<String>(this.metamodel.type(String.class), //
				exprDef.getText().substring(1, exprDef.getText().length() - 1));
		}

		throw new PersistenceException("Unhandled expression: " + exprDef.toStringTree());
	}

	/**
	 * Parses the JPQL.
	 * 
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private CriteriaQueryImpl<?> parse() {
		final CommonTree tree = this.parse(this.qlString);

		JpqlQuery.LOG.debug("Parsed query successfully {0}", //
			JpqlQuery.LOG.lazyBoxed(this.qlString, new Object[] { tree.toStringTree() }));

		return this.construct(tree);
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
						new Object[] { errorMsg, "\n\n" + tree.toStringTree() }));

				throw new PersistenceException("cannot parse the query:\n " + errorMsg);
			}

			return tree;
		}
		catch (final Exception e) {
			throw new PersistenceException("Cannot parse qpql: " + e.getMessage(), e);
		}
	}
}
