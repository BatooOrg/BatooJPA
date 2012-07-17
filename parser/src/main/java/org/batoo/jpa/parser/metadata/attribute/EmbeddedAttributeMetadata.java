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
package org.batoo.jpa.parser.metadata.attribute;

import java.util.List;

import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;

/**
 * The definition of embedded attributes.
 * 
 * @author hceylan
 * @since $version
 */
public interface EmbeddedAttributeMetadata extends AttributeMetadata {

	/**
	 * Returns the list of association overrides for the embedded attribute.
	 * 
	 * @return the list of association overrides for the embedded attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<AssociationMetadata> getAssociationOverrides();

	/**
	 * Returns the list of attribute overrides for the embedded attribute.
	 * 
	 * @return the list of attribute overrides for the embedded attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<AttributeOverrideMetadata> getAttributeOverrides();
}
