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
package org.batoo.jpa.core.impl.model.attribute;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinColumn;
import org.batoo.jpa.core.impl.metamodel.MetamodelImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link SingularAttribute} representing types of ManyToOne and OneToOne
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * @author hceylan
 * @since $version
 */
public abstract class AssociatedSingularAttribute<X, T> extends SingularAttributeImpl<X, T> implements SingularAttribute<X, T>,
	AssociatedAttribute<X, T> {

	private final String inverseName;

	private final boolean cascadesDetach;
	private final boolean cascadesMerge;
	private final boolean cascadesPersist;
	private final boolean cascadesRefresh;
	private final boolean cascadesRemove;

	private EntityTypeImpl<T> type;
	private AssociatedAttribute<T, X> inverse;
	private final List<JoinColumn> joinColumns = Lists.newArrayList();

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * @param mappedBy
	 *            the mapped by attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociatedSingularAttribute(ManagedTypeImpl<X> declaringType, AssociationAttributeMetadata metadata, String mappedBy) {
		super(declaringType, metadata);

		this.inverseName = mappedBy;

		this.cascadesDetach = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.DETACH);
		this.cascadesMerge = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.MERGE);
		this.cascadesPersist = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.PERSIST);
		this.cascadesRefresh = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.REFRESH);
		this.cascadesRemove = metadata.getCascades().contains(CascadeType.ALL) || metadata.getCascades().contains(CascadeType.REMOVE);

		this.initColumn(metadata);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadesDetach() {
		return this.cascadesDetach;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadesMerge() {
		return this.cascadesMerge;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadesPersist() {
		return this.cascadesPersist;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadesRefresh() {
		return this.cascadesRefresh;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadesRemove() {
		return this.cascadesRemove;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<T> getType() {
		return this.type;
	}

	/**
	 * Initializes the column for the attribute.
	 * 
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void initColumn(AssociationAttributeMetadata metadata) {
		final JdbcAdaptor jdbcAdaptor = this.getDeclaringType().getMetamodel().getJdbcAdaptor();

		// if metadata defines the join columns then use the information provided
		if ((metadata != null) && (metadata.getJoinColumns().size() > 0)) {
			for (final JoinColumnMetadata column : metadata.getJoinColumns()) {
				this.joinColumns.add(new JoinColumn(jdbcAdaptor, this, column));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isAssociation() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isId() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOwner() {
		return this.inverseName == null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isVersion() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void link() throws MappingException {
		final MetamodelImpl metamodel = this.getDeclaringType().getMetamodel();

		this.type = metamodel.entity(this.getJavaType());

		if (this.inverseName != null) {
			this.inverse = (AssociatedAttribute<T, X>) this.type.getAttribute(this.inverseName);

			if (this.inverse == null) {
				throw new MappingException("Cannot find the mappedBy attribute " + this.inverseName + " specified on "
					+ this.getJavaMember());
			}

			this.inverse.setInverse(this);
		}

		final EntityTypeImpl<X> entity = (EntityTypeImpl<X>) this.getDeclaringType();

		final ForeignKey foreignKey = new ForeignKey(table, referencedTable, columnMappings);

		entity.getPrimaryTable().addForeignKey(foreignKey);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean references(ManagedInstance<?> source, ManagedInstance<?> associate) {
		return this.get(source) == associate;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setInverse(AssociatedAttribute<T, X> inverse) {
		this.inverse = inverse;
	}
}
