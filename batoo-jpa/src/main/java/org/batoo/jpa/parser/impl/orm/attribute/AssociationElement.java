/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;

import org.batoo.jpa.annotations.FetchStrategyType;
import org.batoo.jpa.parser.impl.orm.CascadesElement;
import org.batoo.jpa.parser.impl.orm.Element;
import org.batoo.jpa.parser.impl.orm.ElementConstants;
import org.batoo.jpa.parser.impl.orm.JoinColumnElement;
import org.batoo.jpa.parser.impl.orm.JoinTableElement;
import org.batoo.jpa.parser.impl.orm.ParentElement;
import org.batoo.jpa.parser.impl.orm.PrimaryKeyJoinColumnElement;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.JoinTableMetadata;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.AssociationAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>one-to-one</code> elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class AssociationElement extends AttributeElement implements AssociationAttributeMetadata {

	private String targetEntity;
	private FetchType fetchType;

	private Set<CascadeType> cascades;
	private final List<JoinColumnMetadata> joinColumns = Lists.newArrayList();
	private JoinTableMetadata joinTable;
	private final List<PrimaryKeyJoinColumnMetadata> primaryKeyJoinColumns = Lists.newArrayList();

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * @param defaultFetchType
	 *            the default fetch type
	 * @param expectedChildElements
	 *            the name of the elements expected
	 * 
	 * @since 2.0.0
	 */
	public AssociationElement(ParentElement parent, Map<String, String> attributes, FetchType defaultFetchType, String... expectedChildElements) {
		super(parent, attributes, ParentElement.join(expectedChildElements, //
			ElementConstants.ELEMENT_CASCADE,//
			ElementConstants.ELEMENT_JOIN_COLUMN, //
			ElementConstants.ELEMENT_JOIN_TABLE, //
			ElementConstants.ELEMENT_PRIMARY_KEY_JOIN_COLUMN));

		this.fetchType = defaultFetchType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		super.generate();

		this.targetEntity = this.getAttribute(ElementConstants.ATTR_TARGET_ENTITY, ElementConstants.EMPTY);
		this.fetchType = FetchType.valueOf(this.getAttribute(ElementConstants.ATTR_FETCH, this.fetchType.name()));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<CascadeType> getCascades() {
		return this.cascades;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FetchStrategyType getFetchStrategy() {
		return null;
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
	public int getMaxFetchDepth() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getTargetEntity() {
		return this.targetEntity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof CascadesElement) {
			this.cascades = ((CascadesElement) child).getCascades();
		}

		if (child instanceof JoinColumnElement) {
			this.joinColumns.add((JoinColumnMetadata) child);
		}

		if (child instanceof JoinTableElement) {
			this.joinTable = (JoinTableMetadata) child;
		}

		if (child instanceof PrimaryKeyJoinColumnElement) {
			this.primaryKeyJoinColumns.add((PrimaryKeyJoinColumnMetadata) child);
		}
	}
}
