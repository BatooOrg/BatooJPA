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
package org.batoo.jpa.parser.metadata;

import java.util.List;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public interface AttributesMetadata {

	/**
	 * Returns the basic attributes.
	 * 
	 * @return the basic attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<BasicAttributeMetadata> getBasics();

	/**
	 * Returns the embedded id attributes.
	 * 
	 * @return the embedded id attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<EmbeddedIdAttributeMetadata> getEmbeddedIds();

	/**
	 * Returns the embedded attributes.
	 * 
	 * @return the embedded attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<EmbeddedAttributeMetadata> getEmbeddeds();

	/**
	 * Returns the id attributes.
	 * 
	 * @return the id attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<IdAttributeMetadata> getIds();

	/**
	 * Returns the many-to-many attributes.
	 * 
	 * @return the many-to-many attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<ManyToManyAttributeMetadata> getManyToManies();

	/**
	 * Returns the many-to-one attributes.
	 * 
	 * @return the many-to-one attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<ManyToOneAttributeMetadata> getManyToOnes();

	/**
	 * Returns the one-to-many attributes.
	 * 
	 * @return the one-to-many attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<OneToManyAttributeMetadata> getOneToManies();

	/**
	 * Returns the one-to-one attributes.
	 * 
	 * @return the one-to-one attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<OneToOneAttributeMetadata> getOneToOnes();

	/**
	 * Returns the transient attributes.
	 * 
	 * @return the transient attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<TransientAttributeMetadata> getTransients();

	/**
	 * Returns the version attributes.
	 * 
	 * @return the version attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<VersionAttributeMetadata> getVersions();

}
