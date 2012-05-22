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
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;

/**
 * The common definition of association attributes.
 * 
 * @author hceylan
 * @since $version
 */
public interface AssociationAttributeMetadata extends AttributeMetadata {

	/**
	 * Returns the cascades of the association attribute.
	 * 
	 * @return the cascades of the association attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	Set<CascadeType> getCascades();

	/**
	 * Returns the fetch type of the association attribute.
	 * 
	 * @return the fetch type of the association attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	FetchType getFetchType();

	/**
	 * Returns the join column definition of the association attribute.
	 * 
	 * @return the join column definition of the association attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	List<JoinColumnMetadata> getJoinColumns();

	/**
	 * Returns the join table definition of the association attribute.
	 * 
	 * @return the join table definition of the association attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	JoinTableMetadata getJoinTable();

	/**
	 * Returns the name of the target entity of the association attribute.
	 * 
	 * @return the name of the target entity of the association attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	String getTargetEntity();
}
