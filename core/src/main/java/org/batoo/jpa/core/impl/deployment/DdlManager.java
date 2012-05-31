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
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.metamodel.MetamodelImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.DDLMode;

/**
 * A Manager that performs the DDL operations.
 * 
 * @author hceylan
 * @since $version
 */
public class DdlManager extends DeploymentManager<EntityTypeImpl<?>> {

	private static final BLogger LOG = BLoggerFactory.getLogger(DdlManager.class);

	/**
	 * Performs the DDL operations.
	 * 
	 * @param datasource
	 *            the datasource
	 * @param metamodel
	 *            the metamodel
	 * @param ddlMode
	 *            the DDL Mode
	 * @throws BatooException
	 *             thrown in case of an underlying exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static void perform(DataSourceImpl datasource, MetamodelImpl metamodel, DDLMode ddlMode) throws BatooException {
		new DdlManager(datasource, metamodel, ddlMode, true).perform();
		new DdlManager(datasource, metamodel, ddlMode, false).perform();
	}

	private final DataSourceImpl datasource;
	private final DDLMode ddlMode;
	private final boolean firstPass;

	private DdlManager(DataSourceImpl datasource, MetamodelImpl metamodel, DDLMode ddlMode, boolean firstPass) {
		super(DdlManager.LOG, "DDL Manager", metamodel, Context.ENTITIES);

		this.datasource = datasource;
		this.ddlMode = ddlMode;
		this.firstPass = firstPass;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Void perform(EntityTypeImpl<?> type) throws BatooException {
		if (this.firstPass) {
			this.getMetamodel().performTablesDddl(this.datasource, this.ddlMode, type);
		}
		else {
			this.getMetamodel().performForeignKeysDdl(this.datasource, this.ddlMode, type);
		}

		return null;
	}
}
