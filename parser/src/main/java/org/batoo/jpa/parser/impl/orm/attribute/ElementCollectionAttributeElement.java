/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.parser.impl.orm.attribute;

import java.util.List;
import java.util.Map;

import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.TemporalType;

import org.batoo.jpa.parser.impl.orm.AssociationOverrideElement;
import org.batoo.jpa.parser.impl.orm.AttributeOverrideElement;
import org.batoo.jpa.parser.impl.orm.Element;
import org.batoo.jpa.parser.impl.orm.ElementConstants;
import org.batoo.jpa.parser.impl.orm.EnumeratedElement;
import org.batoo.jpa.parser.impl.orm.LobElement;
import org.batoo.jpa.parser.impl.orm.MapKeyAttributeOverrideElement;
import org.batoo.jpa.parser.impl.orm.MapKeyClassElement;
import org.batoo.jpa.parser.impl.orm.MapKeyColumnElement;
import org.batoo.jpa.parser.impl.orm.MapKeyElement;
import org.batoo.jpa.parser.impl.orm.MapKeyEnumeratedElement;
import org.batoo.jpa.parser.impl.orm.MapKeyTemporalElement;
import org.batoo.jpa.parser.impl.orm.OrderByElement;
import org.batoo.jpa.parser.impl.orm.OrderColumnElement;
import org.batoo.jpa.parser.impl.orm.ParentElement;
import org.batoo.jpa.parser.impl.orm.TemporalElement;
import org.batoo.jpa.parser.metadata.AssociationMetadata;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.CollectionTableMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.ElementCollectionAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>element-collection</code> elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class ElementCollectionAttributeElement extends AttributeElement implements ElementCollectionAttributeMetadata {

	private String mapKey;
	private String orderBy;
	private OrderColumnElement orderColumn;
	private final List<AttributeOverrideMetadata> attributeOverrides = Lists.newArrayList();
	private final List<AttributeOverrideMetadata> mapKeyAttributeOverrides = Lists.newArrayList();
	private final List<AssociationMetadata> associationOverrides = Lists.newArrayList();
	private CollectionTableMetadata collectionTable;
	private ColumnMetadata column;
	private EnumType enumType;
	private boolean lob;
	private String mapKeyClassName;
	private EnumType mapKeyEnumType;
	private TemporalType mapKeyTemporalType;
	private String targetClass;
	private FetchType fetchType;
	private TemporalType temporalType;
	private ColumnMetadata mapKeyColumn;

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since 2.0.0
	 */
	public ElementCollectionAttributeElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, //
			ElementConstants.ELEMENT_ASSOCIATION_OVERRIDE, //
			ElementConstants.ELEMENT_ATTRIBUTE_OVERRIDE, //
			ElementConstants.ELEMENT_COLLECTION_TABLE, //
			ElementConstants.ELEMENT_COLUMN, //
			ElementConstants.ELEMENT_ENUMERATED, //
			ElementConstants.ELEMENT_TEMPORAL, //
			ElementConstants.ELEMENT_LOB, //
			ElementConstants.ELEMENT_MAP_KEY, //
			ElementConstants.ELEMENT_MAP_KEY_ATTRIBUTE_OVERRIDE, //
			ElementConstants.ELEMENT_MAP_KEY_CLASS, //
			ElementConstants.ELEMENT_MAP_KEY_COLUMN, //
			ElementConstants.ELEMENT_MAP_KEY_ENUMERATED, //
			ElementConstants.ELEMENT_MAP_KEY_TEMPORAL, //
			ElementConstants.ELEMENT_ORDER_BY, //
			ElementConstants.ELEMENT_ORDER_COLUMN);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		super.generate();

		this.targetClass = this.getAttribute(ElementConstants.ATTR_TARGET_CLASS);
		this.fetchType = FetchType.valueOf(this.getAttribute(ElementConstants.ATTR_FETCH, FetchType.LAZY.name()));
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
	public CollectionTableMetadata getCollectionTable() {
		return this.collectionTable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ColumnMetadata getColumn() {
		return this.column;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EnumType getEnumType() {
		return this.enumType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FetchType getFetchType() {
		return this.fetchType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMapKey() {
		return this.mapKey;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<AttributeOverrideMetadata> getMapKeyAttributeOverrides() {
		return this.mapKeyAttributeOverrides;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getMapKeyClassName() {
		return this.mapKeyClassName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ColumnMetadata getMapKeyColumn() {
		return this.mapKeyColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EnumType getMapKeyEnumType() {
		return this.mapKeyEnumType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TemporalType getMapKeyTemporalType() {
		return this.mapKeyTemporalType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getOrderBy() {
		return this.orderBy;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public ColumnMetadata getOrderColumn() {
		return this.orderColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getTargetClass() {
		return this.targetClass;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TemporalType getTemporalType() {
		return this.temporalType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof AttributeOverrideElement) {
			this.attributeOverrides.add((AttributeOverrideElement) child);
		}

		if (child instanceof AssociationOverrideElement) {
			this.associationOverrides.add((AssociationOverrideElement) child);
		}

		if (child instanceof CollectionTableMetadata) {
			this.collectionTable = (CollectionTableMetadata) child;
		}

		if (child instanceof MapKeyColumnElement) {
			this.mapKeyColumn = (ColumnMetadata) child;
		}

		if (child instanceof ColumnMetadata) {
			this.column = (ColumnMetadata) child;
		}

		if (child instanceof EnumeratedElement) {
			this.enumType = ((EnumeratedElement) child).getEnumType();
		}

		if (child instanceof TemporalElement) {
			this.temporalType = ((TemporalElement) child).getTemporalType();
		}

		if (child instanceof LobElement) {
			this.lob = true;
		}

		if (child instanceof MapKeyElement) {
			this.mapKey = ((MapKeyElement) child).getName();
		}

		if (child instanceof MapKeyAttributeOverrideElement) {
			this.mapKeyAttributeOverrides.add((AttributeOverrideElement) child);
		}

		if (child instanceof MapKeyClassElement) {
			this.mapKeyClassName = ((MapKeyClassElement) child).getClazz();
		}

		if (child instanceof MapKeyEnumeratedElement) {
			this.mapKeyEnumType = ((MapKeyEnumeratedElement) child).getEnumType();
		}

		if (child instanceof MapKeyTemporalElement) {
			this.mapKeyTemporalType = ((MapKeyTemporalElement) child).getTemporalType();
		}

		if (child instanceof OrderByElement) {
			this.orderBy = ((OrderByElement) child).getOrderBy();
		}

		if (child instanceof OrderColumnElement) {
			this.orderColumn = ((OrderColumnElement) child);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isLob() {
		return this.lob;
	}
}
