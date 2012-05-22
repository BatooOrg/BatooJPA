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
package org.batoo.jpa.parser.impl.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.List;
import java.util.Set;

import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;

import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.metadata.PrimaryKeyJoinColumnMetadaImpl;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;

import com.google.common.collect.Lists;

/**
 * PersistenceParser for {@link PrimaryKeyJoinColumns} and {@link PrimaryKeyJoinColumn} annotations.
 * 
 * @author hceylan
 * @since $version
 */
public class PrimaryKeyJoinColumnsParser {

	private final List<PrimaryKeyJoinColumnMetadata> pkJoinColumns = Lists.newArrayList();

	/**
	 * @param member
	 *            the java member
	 * @param parsed
	 *            the set of annotations parsed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PrimaryKeyJoinColumnsParser(Member member, Set<Class<? extends Annotation>> parsed) {
		super();

		final JavaLocator locator = new JavaLocator(member, null);

		final PrimaryKeyJoinColumns pkJoinColumns = ReflectHelper.getAnnotation(member, PrimaryKeyJoinColumns.class);
		if (pkJoinColumns != null) {

			for (final PrimaryKeyJoinColumn pkJoinColumn : pkJoinColumns.value()) {
				this.getPkJoinColumns().add(new PrimaryKeyJoinColumnMetadaImpl(locator, pkJoinColumn));
			}

			parsed.add(PrimaryKeyJoinColumns.class);
		}
		else {
			final PrimaryKeyJoinColumn pkJoinColumn = ReflectHelper.getAnnotation(member, PrimaryKeyJoinColumn.class);
			if (pkJoinColumn != null) {
				this.getPkJoinColumns().add(new PrimaryKeyJoinColumnMetadaImpl(locator, pkJoinColumn));

				parsed.add(PrimaryKeyJoinColumn.class);
			}
		}
	}

	/**
	 * Returns the primary key join columns.
	 * 
	 * @return the primary key join columns
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<PrimaryKeyJoinColumnMetadata> getPkJoinColumns() {
		return this.pkJoinColumns;
	}

}
