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

import javax.sql.DataSource;

import org.batoo.jpa.common.BatooException;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.DDLMode;

/**
 * A Manager that performs the DDL operations.
 * 
 * @author hceylan
 * @since $version
 */
public class DdlManager extends DeploymentManager<EntityTypeImpl<?>> {

	/**
	 * The phase of the DDL Operation
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public enum DdlPhase {
		/**
		 * The tables phase
		 */
		TABLES,

		/**
		 * The foreign keys phase
		 */
		FOREIGN_KEYS
	}

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
	public static void perform(DataSource datasource, MetamodelImpl metamodel, DDLMode ddlMode) throws BatooException {
		new DdlManager(datasource, metamodel, ddlMode, DdlPhase.TABLES).perform();
		new DdlManager(datasource, metamodel, ddlMode, DdlPhase.FOREIGN_KEYS).perform();
	}

	private final DataSource datasource;
	private final DDLMode ddlMode;
	private final DdlPhase ddlPhase;

	private DdlManager(DataSource datasource, MetamodelImpl metamodel, DDLMode ddlMode, DdlPhase ddlPhase) {
		super(DdlManager.LOG, "DDL Manager", metamodel, Context.ENTITIES);

		this.datasource = datasource;
		this.ddlMode = ddlMode;
		this.ddlPhase = ddlPhase;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Void perform(EntityTypeImpl<?> type) throws BatooException {
		switch (this.ddlPhase) {
			case TABLES:
				this.getMetamodel().performTablesDdl(this.datasource, this.ddlMode, type);
				break;
			case FOREIGN_KEYS:
				this.getMetamodel().performForeignKeysDdl(this.datasource, this.ddlMode, type);
				break;
		}

		return null;
	}
}
