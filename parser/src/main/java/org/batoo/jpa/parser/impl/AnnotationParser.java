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
package org.batoo.jpa.parser.impl;

import javax.persistence.Table;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.annotated.MetadataImpl;
import org.batoo.jpa.parser.impl.metadata.TableMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.type.EntityMetadataImpl;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

/**
 * PersistenceParser that joins the {@link OrmParser} metadata with annotation metadata.
 * 
 * @author hceylan
 * @since $version
 */
public class AnnotationParser {

	private final MetadataImpl metadata;

	/**
	 * The general log
	 */
	public static final BLogger LOG = BLoggerFactory.getLogger(AnnotationParser.class);

	/**
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AnnotationParser(MetadataImpl metadata) {
		super();

		this.metadata = metadata;
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void parse() {
		for (final EntityMetadata entity : this.metadata.getEntities()) {
			Class<?> clazz;
			try {
				clazz = Class.forName(entity.getClassName());
			}
			catch (final ClassNotFoundException e) {
				throw new MappingException("Class " + entity.getClassName() + " cannot be found.", entity.getLocation());
			}

			new TableMetadataImpl(clazz, clazz.getAnnotation(Table.class), entity.getTable());

			this.metadata.add(new EntityMetadataImpl(clazz, this.metadata.getAccessType(), entity));
		}
	}
}
