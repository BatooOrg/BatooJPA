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

import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.JoinColumn;

import org.batoo.jpa.parser.impl.annotated.JavaLocator;
import org.batoo.jpa.parser.metadata.AssociationOverrideMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.JoinTableMetadata;

import com.google.common.collect.Lists;

/**
 * Annotated definition of {@link AttributeOverrideMetadata}.
 * 
 * @author hceylan
 * @since $version
 */
public class AssociationOverrideMetadataImpl implements AssociationOverrideMetadata {

	private final JavaLocator locator;
	private final AssociationOverride associationOverride;
	private final List<JoinColumnMetadata> joinColumns = Lists.newArrayList();
	private final JoinTableMetadata joinTable;

	/**
	 * @param locator
	 *            the java locator
	 * @param associationOverride
	 *            the obtained {@link AssociationOverride} annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociationOverrideMetadataImpl(JavaLocator locator, AssociationOverride associationOverride) {
		super();

		this.locator = locator;
		this.associationOverride = associationOverride;

		// based on join columns?
		if (associationOverride.joinColumns().length > 0) {
			for (final JoinColumn joinColumn : associationOverride.joinColumns()) {
				this.joinColumns.add(new JoinColumnMetadataImpl(locator, joinColumn));
			}

			this.joinTable = null;
		}
		else {
			this.joinTable = new JoinTableMetadaImpl(locator, associationOverride.joinTable());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<JoinColumnMetadata> getJoinColumns() {
		return this.joinColumns;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinTableMetadata getJoinTable() {
		return this.joinTable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getLocation() {
		return this.locator.getLocation();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.associationOverride.name();
	}

}
