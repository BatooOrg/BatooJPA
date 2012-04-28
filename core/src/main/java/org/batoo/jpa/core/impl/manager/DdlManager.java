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
package org.batoo.jpa.core.impl.manager;

import java.util.Set;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.DDLMode;

/**
 * A Manager that performs the DDL operations.
 * 
 * @author hceylan
 * @since $version
 */
public class DdlManager extends DeploymentManager<EntityTypeImpl<?>> {

	private static final BLogger LOG = BLogger.getLogger(DdlManager.class);

	public static void perform(DataSourceImpl datasource, MetamodelImpl metamodel, Set<String> schemas, DDLMode ddlMode)
		throws BatooException {
		new DdlManager(datasource, metamodel, schemas, ddlMode, true).perform();
		new DdlManager(datasource, metamodel, schemas, ddlMode, false).perform();
	}

	private final DataSourceImpl datasource;
	private final Set<String> schemas;
	private final DDLMode ddlMode;
	private final boolean firstPass;

	private DdlManager(DataSourceImpl datasource, MetamodelImpl metamodel, Set<String> schemas, DDLMode ddlMode, boolean firstPass) {
		super(LOG, "DDL Manager", metamodel, Context.ENTITIES);

		this.datasource = datasource;
		this.schemas = schemas;
		this.ddlMode = ddlMode;
		this.firstPass = firstPass;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Void perform(EntityTypeImpl<?> type) throws BatooException {
		type.ddl(DdlManager.this.datasource, this.schemas, DdlManager.this.ddlMode, this.firstPass);

		return null;
	}

}
