/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Transient 2.0 (the
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

import javax.persistence.Transient;

import org.batoo.jpa.parser.metadata.attribute.TransientAttributeMetadata;

/**
 * The implementation of the {@link TransientAttributeMetadata}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class TransientAttributeMetadataImpl extends AttributeMetadataImpl implements TransientAttributeMetadata {

	/**
	 * @param member
	 *            the java member of transient attribute
	 * @param name
	 *            the name of the transient attribute
	 * @param parsed
	 *            the annotations parsed
	 * 
	 * @since 2.0.0
	 */
	public TransientAttributeMetadataImpl(Member member, String name, Set<Class<? extends Annotation>> parsed) {
		super(member, name);

		parsed.add(Transient.class);
	}

	/**
	 * @param member
	 *            the java member of transient attribute
	 * @param metadata
	 *            the metadata definition of the transient attribute
	 * 
	 * @since 2.0.0
	 */
	public TransientAttributeMetadataImpl(Member member, TransientAttributeMetadata metadata) {
		super(member, metadata);
	}

}
