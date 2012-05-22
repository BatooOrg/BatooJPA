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
package org.batoo.jpa.parser.impl.metadata;

import javax.persistence.JoinColumn;

import org.batoo.jpa.parser.impl.annotated.JavaLocator;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;

/**
 * The implementation of {@link JoinColumnMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class JoinColumnMetadataImpl extends BaseColumnMetadataImpl implements JoinColumnMetadata {

	private final JoinColumn joinColumn;

	/**
	 * @param locator
	 *            the java locator
	 * @param column
	 *            the obtained {@link JoinColumn} annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JoinColumnMetadataImpl(JavaLocator locator, JoinColumn column) {
		super(locator, column.table(), column.name(), column.columnDefinition(), column.insertable(), column.nullable(),
			column.updatable(), column.unique());

		this.joinColumn = column;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getReferencedColumnName() {
		return this.joinColumn.referencedColumnName();
	}

}
