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

import org.batoo.jpa.parser.impl.orm.AttributeOverrideElement;
import org.batoo.jpa.parser.impl.orm.Element;
import org.batoo.jpa.parser.impl.orm.ElementConstants;
import org.batoo.jpa.parser.impl.orm.MapKeyAttributeOverrideElement;
import org.batoo.jpa.parser.impl.orm.MapKeyClassElement;
import org.batoo.jpa.parser.impl.orm.MapKeyColumnElement;
import org.batoo.jpa.parser.impl.orm.MapKeyElement;
import org.batoo.jpa.parser.impl.orm.MapKeyEnumeratedElement;
import org.batoo.jpa.parser.impl.orm.MapKeyTemporalElement;
import org.batoo.jpa.parser.impl.orm.OrderByElement;
import org.batoo.jpa.parser.impl.orm.OrderColumnElement;
import org.batoo.jpa.parser.impl.orm.ParentElement;
import org.batoo.jpa.parser.impl.orm.PrimaryKeyJoinColumnElement;
import org.batoo.jpa.parser.metadata.AttributeOverrideMetadata;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.OneToManyAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>one-to-many</code> elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class OneToManyAttributeElement extends AssociationElement implements OneToManyAttributeMetadata {

	private boolean orphanRemoval;
	private String mappedBy;

	private String mapKey;
	private String orderBy;
	private ColumnMetadata mapKeyColumn;
	private String mapKeyClassName;
	private EnumType mapKeyEnumType;
	private TemporalType mapKeyTemporalType;
	private OrderColumnElement orderColumn;
	private final List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns = Lists.newArrayList();
	private final List<AttributeOverrideMetadata> mapKeyAttributeOverrides = Lists.newArrayList();

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since 2.0.0
	 */
	public OneToManyAttributeElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, FetchType.LAZY, //
			ElementConstants.ELEMENT_CASCADE,//
			ElementConstants.ELEMENT_JOIN_COLUMN, //
			ElementConstants.ELEMENT_JOIN_TABLE, //
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

		this.orphanRemoval = this.getAttribute(ElementConstants.ATTR_ORPHAN_REMOVAL, Boolean.FALSE);
		this.mappedBy = this.getAttribute(ElementConstants.ATTR_MAPPED_BY, ElementConstants.EMPTY);
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
	 * Returns the mapKeyClass of the OneToManyAttributeElement.
	 * 
	 * @return the mapKeyClass of the OneToManyAttributeElement
	 * 
	 * @since 2.0.0
	 */
	public String getMapKeyClass() {
		return this.mapKeyClassName;
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
	public String getMappedBy() {
		return this.mappedBy;
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
	protected void handleChild(Element child) {
		super.handleChild(child);

		if (child instanceof PrimaryKeyJoinColumnElement) {
			this.primaryKeyJoinColumns.add((PrimaryKeyJoinColumnMetadata) child);
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

		if (child instanceof MapKeyColumnElement) {
			this.mapKeyColumn = (ColumnMetadata) child;
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
	public boolean removesOrphans() {
		return this.orphanRemoval;
	}
}
