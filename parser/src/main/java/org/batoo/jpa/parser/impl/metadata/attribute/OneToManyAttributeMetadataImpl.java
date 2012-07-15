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
package org.batoo.jpa.parser.impl.metadata.attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Set;

import javax.persistence.OneToMany;

import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.OneToManyAttributeMetadata;

/**
 * Implementation of {@link OneToManyAttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class OneToManyAttributeMetadataImpl extends AssociationAttributeMetadataImpl implements OneToManyAttributeMetadata {

	private final String mappedBy;
	private final boolean removesOprhans;
	private final String mapKey;
	private final ColumnMetadata orderColumn;
	private final String orderBy;

	/**
	 * @param member
	 *            the java member of one-to-one attribute
	 * @param metadata
	 *            the metadata definition of the one-to-many attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public OneToManyAttributeMetadataImpl(Member member, OneToManyAttributeMetadata metadata) {
		super(member, metadata);

		this.mappedBy = metadata.getMappedBy();
		this.removesOprhans = metadata.removesOrphans();
		this.mapKey = metadata.getMapKey();
		this.orderBy = metadata.getOrderBy();
		this.orderColumn = metadata.getOrderColumn();
	}

	/**
	 * @param member
	 *            the java member of one-to-one attribute
	 * @param name
	 *            the name of the one-to-one attribute
	 * @param oneToMany
	 *            the obtained {@link OneToMany} annotation
	 * @param parsed
	 *            set of annotations parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public OneToManyAttributeMetadataImpl(Member member, String name, OneToMany oneToMany, Set<Class<? extends Annotation>> parsed) {
		super(member, name, parsed, oneToMany.targetEntity().getName(), oneToMany.fetch(), oneToMany.cascade());

		parsed.add(OneToMany.class);

		this.mappedBy = oneToMany.mappedBy();
		this.removesOprhans = oneToMany.orphanRemoval();
		this.mapKey = this.handleMapKey(member, parsed);
		this.orderBy = this.handleOrderBy(member, parsed);
		this.orderColumn = this.handleOrderColumn(member, parsed);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMapKey() {
		return this.mapKey;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMappedBy() {
		return this.mappedBy;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getOrderBy() {
		return this.orderBy;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ColumnMetadata getOrderColumn() {
		return this.orderColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean removesOrphans() {
		return this.removesOprhans;
	}
}
