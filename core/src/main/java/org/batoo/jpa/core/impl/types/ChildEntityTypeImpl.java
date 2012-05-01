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
package org.batoo.jpa.core.impl.types;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;

/**
 * Entity type that extends another entity.
 * 
 * @author hceylan
 * @since $version
 */
public class ChildEntityTypeImpl<X> extends EntityTypeImpl<X> {

	/**
	 * @param metaModel
	 *            the meta model of the persistence
	 * @param supertype
	 *            the entity type that this type is extending
	 * @param javaType
	 *            the java type of the entity
	 * @param name
	 *            name of the entity
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ChildEntityTypeImpl(MetamodelImpl metaModel, EntityTypeImpl<? super X> supertype, Class<X> javaType) throws MappingException {
		super(metaModel, supertype, javaType);
	}

}
