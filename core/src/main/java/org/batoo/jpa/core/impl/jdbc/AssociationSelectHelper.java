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
package org.batoo.jpa.core.impl.jdbc;

import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.ManagedId;
import org.batoo.jpa.core.impl.mapping.Association;
import org.batoo.jpa.core.impl.mapping.CollectionMapping;
import org.batoo.jpa.core.impl.mapping.Mapping;
import org.batoo.jpa.core.impl.mapping.OwnedAssociation;
import org.batoo.jpa.core.impl.mapping.OwnerAssociation;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

/**
 * Select helper to select against the owner entity's primary key
 * 
 * @param <X>
 * @author hceylan
 * @since $version
 */
public class AssociationSelectHelper<X, C, E> extends BaseSelectHelper<E> {

	private final CollectionMapping<X, C, E> mapping;

	/**
	 * @param mapping
	 *            the type to select against
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AssociationSelectHelper(CollectionMapping<X, C, E> mapping) {
		super((EntityTypeImpl<E>) mapping.getDeclaringAttribute().getElementType(), (Mapping<E, ?>) mapping);

		this.mapping = mapping;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected boolean cascades(Association<?, ?> association) {
		return association.isEager();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected String getWhere() {
		// Generate the where statement
		// T1_F1 = ? [AND T1_F2 = ? [...]]
		return Joiner.on(" AND ").join(//
			Collections2.transform(this.predicates, new Function<PhysicalColumn, String>() {

				@Override
				public String apply(PhysicalColumn input) {
					return "T0." + input.getPhysicalName() + " = ?";
				}
			}));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void preparePredicates() {
		OwnerAssociation<?, ?> ownerAssociation;
		if (this.mapping instanceof OwnerAssociation) {
			ownerAssociation = (OwnerAssociation<?, ?>) this.mapping;
		}
		else {
			ownerAssociation = ((OwnedAssociation<?, ?>) this.mapping).getOpposite();
		}

		for (final PhysicalColumn column : ownerAssociation.getPhysicalColumns()) {
			this.predicates.add(column);
		}
	}

	/**
	 * Performs the select over an association and populates the managed instances.
	 * 
	 * @param session
	 *            the session
	 * @param managedId
	 *            the managed id of the owner side of the association
	 * @param association
	 *            the association to select against
	 * @return the collection of entities
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Collection<E> select(SessionImpl session, final ManagedId<?> managedId) throws SQLException {
		// Do not inline, generation of the select SQL will initialize the predicates!
		final String selectSql = this.getSelectSql();

		final Collection<Object> params = Collections2.transform(this.predicates, new Function<PhysicalColumn, Object>() {
			@Override
			public Object apply(PhysicalColumn input) {
				return input.getReferencedColumn().getPhysicalValue(managedId.getSession(), managedId.getInstance());
			}
		});

		final SelectHandler<E> rsHandler = new SelectHandler<E>(session, this.type, this.columnAliases, this.entityPaths);

		return new QueryRunner().query(session.getConnection(), selectSql, rsHandler, params.toArray());
	}
}
