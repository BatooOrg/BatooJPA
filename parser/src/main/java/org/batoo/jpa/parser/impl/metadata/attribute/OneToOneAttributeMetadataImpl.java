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

import java.lang.reflect.Member;
import java.util.List;

import javax.persistence.OneToOne;

import org.batoo.jpa.parser.impl.metadata.JoinTableMetadaImpl;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.OneToOneAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link OneToOneAttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class OneToOneAttributeMetadataImpl extends AssociationAttributeMetadataImpl implements OneToOneAttributeMetadata {

	private final String mappedBy;
	private final boolean removesOprhans;
	private final boolean optional;
	private final List<PrimaryKeyJoinColumnMetadata> pkJoinColumns;

	/**
	 * @param member
	 *            the java member of one-to-one attribute
	 * @param metadata
	 *            the metadata definition of the one-to-one attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public OneToOneAttributeMetadataImpl(Member member, OneToOneAttributeMetadata metadata) {
		super(member, metadata);

		this.mappedBy = metadata.getMappedBy();
		this.optional = metadata.isOptional();
		this.removesOprhans = metadata.removesOprhans();
		this.pkJoinColumns = Lists.newArrayList(metadata.getPrimaryKeyJoinColumns());
	}

	/**
	 * @param member
	 *            the java member of one-to-one attribute
	 * @param name
	 *            the name of the one-to-one attribute
	 * @param oneToOne
	 *            the obtained {@link OneToOne} annotation
	 * @param pkJoinColumns
	 *            the primary key join columns definition
	 * @param joinColumns
	 *            the join columns definition
	 * @param joinTable
	 *            the join table definition
	 * @since $version
	 * @author hceylan
	 */
	public OneToOneAttributeMetadataImpl(Member member, String name, OneToOne oneToOne, List<PrimaryKeyJoinColumnMetadata> pkJoinColumns,
		List<JoinColumnMetadata> joinColumns, JoinTableMetadaImpl joinTable) {

		super(member, name, oneToOne.targetEntity().getName(), oneToOne.fetch(), oneToOne.cascade(), joinColumns, joinTable);

		this.mappedBy = oneToOne.mappedBy();
		this.optional = oneToOne.optional();
		this.removesOprhans = oneToOne.orphanRemoval();
		this.pkJoinColumns = pkJoinColumns;
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
	public List<PrimaryKeyJoinColumnMetadata> getPrimaryKeyJoinColumns() {
		return this.pkJoinColumns;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return this.optional;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean removesOprhans() {
		return this.removesOprhans;
	}

}
