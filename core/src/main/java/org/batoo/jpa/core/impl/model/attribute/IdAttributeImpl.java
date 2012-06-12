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
package org.batoo.jpa.core.impl.model.attribute;

import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.AbstractGenerator;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.metadata.GeneratedValueMetadata;
import org.batoo.jpa.parser.metadata.attribute.IdAttributeMetadata;

/**
 * Implementation of {@link SingularAttribute} for basic attributes.
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * 
 * @author hceylan
 * @since $version
 */
public class IdAttributeImpl<X, T> extends PhysicalAttributeImpl<X, T> {

	private final String generator;
	private final IdType idType;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdAttributeImpl(ManagedTypeImpl<X> declaringType, IdAttributeMetadata metadata) {
		super(declaringType, metadata);

		final JdbcAdaptor jdbcAdaptor = declaringType.getMetamodel().getJdbcAdaptor();
		final MetamodelImpl metamodel = declaringType.getMetamodel();

		final GeneratedValueMetadata generatedValue = metadata.getGeneratedValue();
		if (generatedValue != null) {
			this.idType = jdbcAdaptor.supports(generatedValue.getStrategy());

			// if generator is not specified then assign the default name
			if (StringUtils.isNotBlank(generatedValue.getGenerator())) {
				this.generator = generatedValue.getGenerator();
			}
			else {
				this.generator = AbstractGenerator.DEFAULT_NAME;

				if (this.idType == IdType.SEQUENCE) {
					metamodel.addSequenceGenerator(null);
				}
				else {
					metamodel.addTableGenerator(null);
				}
			}

			// add sequence generator if defined
			if (metadata.getSequenceGenerator() != null) {
				metamodel.addSequenceGenerator(metadata.getSequenceGenerator());
			}

			// add table generator if defined
			if (metadata.getTableGenerator() != null) {
				metamodel.addTableGenerator(metadata.getTableGenerator());
			}
		}
		else {
			this.generator = null;
			this.idType = IdType.MANUAL;
		}
	}

	/**
	 * Fills the sequence / table generated value.
	 * <p>
	 * The operation returns false if at least one entity needs to obtain identity from the database.
	 * 
	 * @param managedInstance
	 *            the instance to fill ids.
	 * @return false if all OK, true if if at least one entity needs to obtain identity from the database
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean fillValue(ManagedInstance<? extends X> managedInstance) {
		if (!this.isId()) {
			throw new IllegalStateException("Not an id attribute");
		}

		final T value = this.get(managedInstance);

		// if the attribute already has value, bail out
		if (value != null) {
			return true;
		}

		// fill the id
		switch (this.idType) {
			case IDENTITY:
				// indicate a requirement for an implicit flush
				return false;
			case MANUAL:
				// only check if the id is not null
				if (value == null) {
					throw new NullPointerException();
				}
			case SEQUENCE:
				// fill with the sequence
				this.set(managedInstance, this.getMetamodel().getNextSequence(this.generator));
				break;
			case TABLE:
				// fill with the next table generator id
				this.set(managedInstance, this.getMetamodel().getNextTableValue(this.generator));
				break;
		}

		return true;
	}

	/**
	 * Returns the idType.
	 * 
	 * @return the idType
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdType getIdType() {
		return this.idType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isId() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isVersion() {
		return false;
	}
}
