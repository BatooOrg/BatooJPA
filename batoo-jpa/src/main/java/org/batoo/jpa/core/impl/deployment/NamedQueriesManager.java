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
package org.batoo.jpa.core.impl.deployment;

import org.batoo.common.BatooException;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.jpql.JpqlQuery;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.parser.metadata.NamedQueryMetadata;

/**
 * Deployment manager to deploy named queries.
 * 
 * @author hceylan
 * @since $version
 */
public class NamedQueriesManager extends DeploymentManager<NamedQueryMetadata> {

	private static final BLogger LOG = BLoggerFactory.getLogger(NamedQueriesManager.class);

	/**
	 * Performs the association linking operations.
	 * 
	 * @param metamodel
	 *            the metamodel
	 * @param criteriaBuilder
	 *            the criteria builder
	 * @throws BatooException
	 *             thrown in case of an underlying exception
	 * 
	 * @since $version
	 */
	public static void perform(MetamodelImpl metamodel, CriteriaBuilderImpl criteriaBuilder) throws BatooException {
		new NamedQueriesManager(metamodel, criteriaBuilder).perform();
	}

	private final CriteriaBuilderImpl criteriaBuilder;

	private NamedQueriesManager(MetamodelImpl metamodel, CriteriaBuilderImpl criteriaBuilder) {
		super(NamedQueriesManager.LOG, "Query Manager", metamodel, Context.NAMED_QUERIES);

		this.criteriaBuilder = criteriaBuilder;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Void perform(NamedQueryMetadata namedQuery) throws BatooException {
		NamedQueriesManager.LOG.debug("Compiling named query {0}...", namedQuery.getName());

		new JpqlQuery(this.getMetamodel().getEntityManagerFactory(), this.criteriaBuilder, namedQuery);

		NamedQueriesManager.LOG.debug("Successfully compiled named query {0}.", namedQuery.getName());

		return null;
	}
}
