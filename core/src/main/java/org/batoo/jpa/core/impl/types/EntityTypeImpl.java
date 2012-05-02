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
package org.batoo.jpa.core.impl.types;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.InheritanceType;
import javax.persistence.metamodel.EntityType;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.AbstractTable.TableType;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.RefreshHelper;
import org.batoo.jpa.core.impl.jdbc.SelectHelper;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link EntityType} interfacing to the rest of the container.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityTypeImpl<X> extends BaseEntityTypeImpl<X> {

	private final SelectHelper<X> selectHelper;
	private final RefreshHelper<X> refreshHelper;
	protected EntityTypeImpl<? super X> root;
	protected final EntityTypeImpl<? super X> parent;
	private final List<EntityTypeImpl<? extends X>> children = Lists.newArrayList();

	/**
	 * @param metaModel
	 *            the meta model of the persistence
	 * @param supertype
	 *            the mapped super class that this type is extending
	 * @param javaType
	 *            the javatype of the entity
	 * @param name
	 *            name of the entity
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeImpl(MetamodelImpl metaModel, IdentifiableTypeImpl<? super X> supertype, Class<X> javaType) throws MappingException {
		super(metaModel, supertype, javaType);

		if (supertype instanceof EntityTypeImpl) {
			this.parent = (EntityTypeImpl<? super X>) supertype;
			this.parent.children.add(this);

			this.root = this.parent.getRoot();
		}
		else {
			this.parent = null;
			this.root = this;
		}

		this.selectHelper = new SelectHelper<X>(this);
		this.refreshHelper = new RefreshHelper<X>(this);
	}

	/**
	 * Returns the identity root for the type.
	 * 
	 * @return the identity root for the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeImpl<? super X> getIdentityRoot() {
		if ((this.parent == null) || (this.root.inheritance.getInheritenceType() == InheritanceType.TABLE_PER_CLASS)) {
			return this;
		}

		return this.root;
	}

	/**
	 * Returns the managed id for the instance.
	 * 
	 * @param instance
	 *            the instance to create managed id or null
	 * @return managed id for the instance
	 * @throws NullPointerException
	 *             thrown if the instance is null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedId<X> getManagedIdForInstance(SessionImpl session, final X instance) {
		if (instance == null) {
			throw new NullPointerException();
		}

		return new ManagedId<X>(this, session, instance);
	}

	/**
	 * Returns the root type of the entity.
	 * <p>
	 * if entity is not a child entity then returns itself.
	 * 
	 * @return the root type of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Override
	@SuppressWarnings("unchecked")
	public EntityTypeImpl<? super X> getRoot() {
		return (EntityTypeImpl<? super X>) (this.root != null ? this.root : this);
	}

	/**
	 * Returns the managed id for the id.
	 * 
	 * @param instance
	 *            the instance to create managed id or null
	 * @param lazy
	 *            if the instance is lazy
	 * @return managed id for the instance
	 * @throws NullPointerException
	 *             thrown if the id is null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "unchecked", "restriction" })
	public ManagedId<X> newManagedId(SessionImpl session, final Object id, boolean lazy) {
		if (id == null) {
			throw new NullPointerException();
		}

		this.enhanceIfNeccessary();

		X instance = null;
		try {
			instance = (X) this.enhancer.newInstance(new Object[] { this.javaType, session, id, !lazy });
		}
		catch (final InvocationTargetException e) {} // not possible
		catch (final IllegalArgumentException e) {} // not possible
		catch (final InstantiationException e) {} // not possible

		final ManagedId<X> managedId = new ManagedId<X>(this, session, instance);

		managedId.populate(id);

		return managedId;
	}

	/**
	 * Performs inserts to each table for the managed instance.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param managedInstance
	 *            the managed instance to perform insert for
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performInsert(Connection connection, ManagedInstance<X> managedInstance) throws SQLException {
		// first insert to the primary table
		this.primaryTable.performInsert(connection, managedInstance);
		managedInstance.setExecuted();

		// then to the remaining tables
		for (final EntityTable table : this.tables.values()) {
			if (table.getTableType() == TableType.PRIMARY) {
				continue;
			}

			table.performInsert(connection, managedInstance);
		}
	}

	/**
	 * Performs refresh from each table for the managed instance.
	 * 
	 * @param session
	 *            the session to use
	 * @param managedInstance
	 *            the managed instance to perform refresh for
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performRefresh(SessionImpl session, ManagedInstance<X> managedInstance) throws SQLException {
		this.refreshHelper.refresh(session, managedInstance);
	}

	/**
	 * Performs select from each table for the managed instance.
	 * 
	 * @param session
	 *            the session to use
	 * @param managedId
	 *            the managed id to perform select for
	 * @return returns the managed instance
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public X performSelect(SessionImpl session, ManagedId<X> managedId) throws SQLException {
		return this.selectHelper.select(session, managedId);
	}
}
