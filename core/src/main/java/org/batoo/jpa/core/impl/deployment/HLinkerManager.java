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

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;

/**
 * A Manager that links persistent classes horizontally.
 * 
 * @author hceylan
 * @since $version
 */
public class HLinkerManager extends DeploymentManager<EntityTypeImpl<?>> {

	private static final BLogger LOG = BLogger.getLogger(HLinkerManager.class);

	public static void link(MetamodelImpl metamodel, DataSourceImpl datasource) throws BatooException {
		new HLinkerManager(metamodel, datasource, true).perform();
		new HLinkerManager(metamodel, datasource, false).perform();
	}

	private final boolean firstPass;

	private final DataSourceImpl datasource;

	public HLinkerManager(MetamodelImpl metamodel, DataSourceImpl datasource, boolean firstPass) {
		super(LOG, "HLinker", metamodel, Context.ENTITIES);

		this.datasource = datasource;
		this.firstPass = firstPass;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Void perform(EntityTypeImpl<?> type) throws BatooException {
		type.link(this.datasource, this.firstPass);

		return null;
	}

}
