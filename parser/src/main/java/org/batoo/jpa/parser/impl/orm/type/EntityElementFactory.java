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
package org.batoo.jpa.parser.impl.orm.type;

import java.util.List;
import java.util.Map;

import javax.persistence.AccessType;
import javax.persistence.InheritanceType;

import org.batoo.jpa.parser.impl.orm.AssociationOverrideElement;
import org.batoo.jpa.parser.impl.orm.AttributeOverrideElement;
import org.batoo.jpa.parser.impl.orm.DiscriminatorValueElement;
import org.batoo.jpa.parser.impl.orm.Element;
import org.batoo.jpa.parser.impl.orm.ElementConstants;
import org.batoo.jpa.parser.impl.orm.EntityMappings;
import org.batoo.jpa.parser.impl.orm.ParentElement;
import org.batoo.jpa.parser.impl.orm.SecondaryTableElement;
import org.batoo.jpa.parser.impl.orm.TableElement;
import org.batoo.jpa.parser.impl.orm.attribute.AttributesElement;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.DiscriminatorColumnMetadata;
import org.batoo.jpa.parser.metadata.InheritanceMetadata;
import org.batoo.jpa.parser.metadata.SecondaryTableMetadata;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>entity</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityElementFactory extends ParentElement implements EntityMetadata {

	private String name;
	private String className;
	private boolean metadataComplete;
	private Boolean cachable;
	private AccessType accessType;
	private SequenceGeneratorMetadata sequenceGenerator;
	private TableGeneratorMetadata tableGenerator;
	private AttributesElement attrs;
	private TableMetadata table;
	private final List<SecondaryTableMetadata> secondaryTables = Lists.newArrayList();
	private final List<AttributeOverrideMetadata> attributeOverrides = Lists.newArrayList();
	private final List<AssociationMetadata> associationOverrides = Lists.newArrayList();
	private InheritanceType inheritanceType;
	private DiscriminatorColumnMetadata discriminatorColumn;
	private String discriminatorValue;

	/**
	 * Constructor for ORM File parsing
	 * 
	 * @param parent
	 *            the metamodel
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityElementFactory(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, //
			ElementConstants.ELEMENT_ACCESS, //
			ElementConstants.ELEMENT_ATTRIBUTE_OVERRIDE, //
			ElementConstants.ELEMENT_ASSOCIATION_OVERRIDE, //
			ElementConstants.ELEMENT_ATTRIBUTES, //
			ElementConstants.ELEMENT_TABLE_GENERATOR, //
			ElementConstants.ELEMENT_SEQUENCE_GENERATOR, //
			ElementConstants.ELEMENT_TABLE, //
			ElementConstants.ELEMENT_SECONDARY_TABLE, //
			ElementConstants.ELEMENT_INHERITANCE, //
			ElementConstants.ELEMENT_DISCRIMINATOR_COLUMN, //
			ElementConstants.ELEMENT_DISCRIMINATOR_VALUE);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.name = this.getAttribute(ElementConstants.ATTR_NAME, ElementConstants.EMPTY);
		this.className = this.getAttribute(ElementConstants.ATTR_CLASS, ElementConstants.EMPTY);
		this.metadataComplete = this.getAttribute(ElementConstants.ATTR_METADATA_COMPLETE, false);
		this.cachable = this.getAttribute(ElementConstants.ATTR_CACHABLE) != null
			? Boolean.valueOf(this.getAttribute(ElementConstants.ATTR_CACHABLE)) : null;
		this.accessType = this.getAttribute(ElementConstants.ATTR_ACCESS) != null
			? AccessType.valueOf(this.getAttribute(ElementConstants.ATTR_ACCESS)) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AccessType getAccessType() {
		return this.accessType != null ? this.accessType : ((EntityMappings) this.getParent()).getAccessType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AssociationMetadata> getAssociationOverrides() {
		return this.associationOverrides;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AttributeOverrideMetadata> getAttributeOverrides() {
		return this.attributeOverrides;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AttributesElement getAttributes() {
		return this.attrs;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Boolean getCacheable() {
		return this.cachable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getClassName() {
		return this.className;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public DiscriminatorColumnMetadata getDiscriminatorColumn() {
		return this.discriminatorColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getDiscriminatorValue() {
		return this.discriminatorValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public InheritanceType getInheritanceType() {
		return this.inheritanceType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<SecondaryTableMetadata> getSecondaryTables() {
		return this.secondaryTables;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SequenceGeneratorMetadata getSequenceGenerator() {
		return this.sequenceGenerator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TableMetadata getTable() {
		return this.table;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TableGeneratorMetadata getTableGenerator() {
		return this.tableGenerator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof AttributesElement) {
			this.attrs = (AttributesElement) child;
		}

		if (child instanceof SequenceGeneratorMetadata) {
			this.sequenceGenerator = (SequenceGeneratorMetadata) child;
		}

		if (child instanceof TableGeneratorMetadata) {
			this.tableGenerator = (TableGeneratorMetadata) child;
		}

		if (child instanceof TableElement) {
			this.table = (TableMetadata) child;
		}

		if (child instanceof SecondaryTableElement) {
			this.secondaryTables.add((SecondaryTableMetadata) child);
		}

		if (child instanceof AttributeOverrideElement) {
			this.attributeOverrides.add((AttributeOverrideMetadata) child);
		}

		if (child instanceof AssociationOverrideElement) {
			this.associationOverrides.add((AssociationMetadata) child);
		}

		if (child instanceof InheritanceMetadata) {
			this.inheritanceType = ((InheritanceMetadata) child).getInheritanceType();
		}

		if (child instanceof DiscriminatorColumnMetadata) {
			this.discriminatorColumn = (DiscriminatorColumnMetadata) child;
		}

		if (child instanceof DiscriminatorValueElement) {
			this.discriminatorValue = ((DiscriminatorValueElement) child).getDiscriminatorValue();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isMetadataComplete() {
		return this.metadataComplete;
	}
}
