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

import javax.persistence.Column;

import org.batoo.jpa.parser.impl.annotated.JavaLocator;
import org.batoo.jpa.parser.metadata.ColumnMetadata;

/**
 * The annotated definition of columns.
 * 
 * @author hceylan
 * @since $version
 */
public class ColumnMetadataImpl extends BaseColumnMetadataImpl implements ColumnMetadata {

	private final Column column;

	/**
	 * @param locator
	 *            the java locator
	 * @param column
	 *            the column annotation obtained
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ColumnMetadataImpl(JavaLocator locator, Column column) {
		super(locator, column.table(), column.name(), column.columnDefinition(), column.insertable(), column.nullable(),
			column.updatable(), column.unique());

		this.column = column;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getLength() {
		return this.column.length();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getPrecision() {
		return this.column.precision();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int getScale() {
		return this.column.scale();
	}

}
