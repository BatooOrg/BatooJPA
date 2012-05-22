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

import javax.persistence.ManyToMany;

import org.batoo.jpa.parser.impl.metadata.JoinTableMetadaImpl;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.ManyToManyAttributeMetadata;

/**
 * Implementation of {@link ManyToManyAttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class ManyToManyAttributeMetadataImpl extends AssociationAttributeMetadataImpl implements ManyToManyAttributeMetadata {

	private final String mappedBy;

	/**
	 * @param member
	 *            the java member of one-to-one attribute
	 * @param metadata
	 *            the metadata definition of the one-to-one attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManyToManyAttributeMetadataImpl(Member member, ManyToManyAttributeMetadata metadata) {
		super(member, metadata);

		this.mappedBy = metadata.getMappedBy();
	}

	/**
	 * @param member
	 *            the java member of one-to-one attribute
	 * @param name
	 *            the name of the one-to-one attribute
	 * @param manyToMany
	 *            the obtained {@link ManyToMany} annotation
	 * @param joinColumns
	 *            the join columns definition
	 * @param joinTable
	 *            the join table definition
	 * @since $version
	 * @author hceylan
	 */
	public ManyToManyAttributeMetadataImpl(Member member, String name, ManyToMany manyToMany, List<JoinColumnMetadata> joinColumns,
		JoinTableMetadaImpl joinTable) {

		super(member, name, manyToMany.targetEntity().getName(), manyToMany.fetch(), manyToMany.cascade(), joinColumns, joinTable);

		this.mappedBy = manyToMany.mappedBy();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMappedBy() {
		return this.mappedBy;
	}

}
