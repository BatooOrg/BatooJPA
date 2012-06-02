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
import org.batoo.jpa.core.impl.metamodel.MetamodelImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.attribute.AssociatedAttribute;

/**
 * A Manager that performs the association linking operations.
 * 
 * @author hceylan
 * @since $version
 */
public class LinkManager extends DeploymentManager<EntityTypeImpl<?>> {

	private static final BLogger LOG = BLoggerFactory.getLogger(LinkManager.class);

	/**
	 * Performs the association linking operations.
	 * 
	 * @param metamodel
	 *            the metamodel
	 * @throws BatooException
	 *             thrown in case of an underlying exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static void perform(MetamodelImpl metamodel) throws BatooException {
		new LinkManager(metamodel).perform();
	}

	private LinkManager(MetamodelImpl metamodel) {
		super(LinkManager.LOG, "Link Manager", metamodel, Context.ENTITIES);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Void perform(EntityTypeImpl<?> type) throws BatooException {
		final AssociatedAttribute<?, ?>[] associations = type.getAssociations();
		for (final AssociatedAttribute<?, ?> attribute : associations) {
			attribute.link();
		}

		return null;
	}
}
