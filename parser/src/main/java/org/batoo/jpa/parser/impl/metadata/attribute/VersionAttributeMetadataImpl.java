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

import javax.persistence.Column;
import javax.persistence.Temporal;

import org.batoo.jpa.parser.metadata.attribute.VersionAttributeMetadata;

/**
 * The implementation of the {@link VersionAttributeMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class VersionAttributeMetadataImpl extends BasicSingularAttributeMetadataImpl implements VersionAttributeMetadata {

	/**
	 * @param member
	 *            the java member of version attribute
	 * @param name
	 *            the name of the version attribute
	 * @param column
	 *            the column definition of the version attribute
	 * @param temporal
	 *            the temporal definition of the version attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public VersionAttributeMetadataImpl(Member member, String name, Column column, Temporal temporal) {
		super(member, name, column, null, temporal);
	}

	/**
	 * @param member
	 *            the java member of version attribute
	 * @param metadata
	 *            the metadata definition of the version attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public VersionAttributeMetadataImpl(Member member, VersionAttributeMetadata metadata) {
		super(member, metadata);
	}

}
