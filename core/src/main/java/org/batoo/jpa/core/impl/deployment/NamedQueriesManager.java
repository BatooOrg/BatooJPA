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
package org.batoo.jpa.core.impl.deployment;

import org.batoo.jpa.common.BatooException;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
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
	 * @author hceylan
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
