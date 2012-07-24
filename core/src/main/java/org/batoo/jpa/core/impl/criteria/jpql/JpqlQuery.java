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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
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
import org.batoo.jpa.core.impl.criteria.join.AbstractFrom;
import org.batoo.jpa.core.impl.criteria.path.AbstractPath;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.jpql.JpqlLexer;
import org.batoo.jpa.jpql.JpqlParser;
import org.batoo.jpa.jpql.JpqlParser.ql_statement_return;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The class that constructs a {@link CriteriaQuery} out of JPQL.
 * 
 * @param <T>
 *            The result class
 * 
 * @author hceylan
 * @since $version
 */
public class JpqlQuery<T> {

	private static final BLogger LOG = BLoggerFactory.getLogger(JpqlQuery.class);

	private final EntityManagerImpl entityManager;
	private final MetamodelImpl metamodel;
	private final String qlString;
	private final Class<T> resultClass;

	private final CriteriaQueryImpl<T> criteriaQuery;
	private final Map<String, AbstractFrom<?, ?>> aliasMap = Maps.newHashMap();
	private final Map<String, AbstractSelection<?>> selectMap = Maps.newHashMap();

	/**
	 * @param entityManager
	 *            the entity manager
	 * @param qlString
	 *            the query string
	 * @param resultClass
	 *            the result class
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JpqlQuery(EntityManagerImpl entityManager, String qlString, Class<T> resultClass) {
		super();

		this.entityManager = entityManager;
		this.metamodel = entityManager.getMetamodel();
		this.qlString = qlString;
		this.resultClass = resultClass;

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
	private CriteriaQueryImpl<T> construct(CommonTree tree) {
		final CriteriaBuilderImpl cb = this.metamodel.getEntityManagerFactory().getCriteriaBuilder();
		final CriteriaQueryImpl<T> q = cb.createQuery(this.resultClass);

		final Tree type = tree.getChild(0);
		if (type.getType() == JpqlParser.SELECT) {
			JpqlQuery.LOG.debug("This is a select query");

			if (tree.getChild(1).getType() == JpqlParser.DISTINCT) {
				JpqlQuery.LOG.debug("Distinct is set");
			}

			this.constructFrom(cb, q, tree.getChild(1));
			this.constructSelect(cb, q, tree.getChild(0).getChild(0));
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
	private void constructFrom(CriteriaBuilderImpl cb, CriteriaQueryImpl<T> q, Tree froms) {
		for (int i = 0; i < froms.getChildCount(); i++) {
			final Tree from = froms.getChild(i);
			final Aliased fromDef = new Aliased(this.aliasMap, from.getChild(0), false);

			final EntityTypeImpl<Object> entity = this.metamodel.entity(fromDef.getQualified().toString());
			final RootImpl<Object> r = q.from(entity);
			r.alias(fromDef.getAlias());

			this.aliasMap.put(fromDef.getAlias(), r);
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

		if (junctionDef.getType() == JpqlParser.OR) {
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
	private <X> Expression<Boolean> constructPredicate(CriteriaBuilderImpl cb, Tree predictionDef) {
		if (predictionDef.getChildCount() == 1) {
			return this.<Boolean> getExpression(cb, predictionDef.getChild(0), Boolean.class);
		}

		final AbstractExpression<X> left = this.<X> getExpression(cb, predictionDef.getChild(0), null);
		final AbstractExpression<? extends X> right = this.getExpression(cb, predictionDef.getChild(1), left.getJavaType());

		if (predictionDef.getType() == JpqlParser.Equals_Operator) {
			return cb.equal(left, right);
		}

		return null;
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
	 */
	@SuppressWarnings("unchecked")
	private void constructSelect(CriteriaBuilderImpl cb, CriteriaQuery<T> q, Tree selects) {
		final List<Selection<?>> selections = Lists.newArrayList();

		for (int i = 0; i < selects.getChildCount(); i++) {
			final Tree selectDef = selects.getChild(i);

			AbstractSelection<?> selection = null;

			final Aliased aliased = new Aliased(this.selectMap, selectDef.getChild(0), true);

			final AbstractSelection<?> parent = this.aliasMap.get(aliased.getParent());
			if (aliased.getQualified() != null) {
				if (parent instanceof AbstractFrom) {
					final AbstractFrom<?, ?> from = (AbstractFrom<?, ?>) parent;
					selection = from.get(aliased.getQualified().toString());
					if (aliased.getAlias() != null) {
						selection.alias(aliased.getAlias());
					}
				}
			}
			else {
				selection = parent;
			}

			selections.add(selection);
		}

		if (selections.size() == 1) {
			q.select((Selection<? extends T>) selections.get(0));
		}
		else {
			q.multiselect(selections);
		}
	}

	/**
	 * Returns the typed query for the query.
	 * 
	 * @return the typed query
	 * @since $version
	 * @author hceylan
	 */
	public TypedQueryImpl<T> createTypedQuery() {
		return new TypedQueryImpl<T>(this.criteriaQuery, this.entityManager);
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
		if (exprDef.getType() == JpqlParser.Ordinal_Parameter) {
			return null;
		}
		else if (exprDef.getType() == JpqlParser.Named_Parameter) {
			return cb.parameter(javaType, exprDef.getText().substring(1));
		}
		else if (exprDef.getType() == JpqlParser.ID) {
			return (AbstractExpression<X>) this.aliasMap.get(exprDef.getText());
		}

		// fall back to path expression
		final Qualified qualified = new Qualified(exprDef, true);
		AbstractPath<?> expression = this.aliasMap.get(qualified.getId());

		final Iterator<String> i = qualified.getSegments().iterator();
		while (i.hasNext()) {
			final String segment = i.next();
			if (i.hasNext()) {
				expression = expression.get(segment);
			}
			else {
				expression = expression.get(segment);
				break;
			}
		}

		return (AbstractExpression<X>) expression;
	}

	/**
	 * Parses the JPQL.
	 * 
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private CriteriaQueryImpl<T> parse() {
		final CommonTree tree = this.parse(this.qlString);

		JpqlQuery.LOG.debug("Parsed query successfully {0}", JpqlQuery.LOG.lazyBoxed(this.qlString, new Object[] { tree.toStringTree() }));

		return this.construct(tree);
	}

	private CommonTree parse(String query) {
		try {
			final JpqlLexer lexer = new JpqlLexer(new ANTLRStringStream(query));
			final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
			final JpqlParser parser = new JpqlParser(tokenStream);

			final ql_statement_return ql_statement = parser.ql_statement();

			return (CommonTree) ql_statement.getTree();
		}
		catch (final Exception e) {
			throw new PersistenceException("Cannot parse qpql: " + e.getMessage(), e);
		}
	}
}
