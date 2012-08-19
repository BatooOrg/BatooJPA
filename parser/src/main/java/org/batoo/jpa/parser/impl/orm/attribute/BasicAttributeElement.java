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

import java.util.Map;

import javax.persistence.EnumType;
import javax.persistence.FetchType;

import org.batoo.jpa.parser.impl.orm.Element;
import org.batoo.jpa.parser.impl.orm.ElementConstants;
import org.batoo.jpa.parser.impl.orm.EnumeratedElement;
import org.batoo.jpa.parser.impl.orm.LobElement;
import org.batoo.jpa.parser.impl.orm.ParentElement;
import org.batoo.jpa.parser.metadata.IndexMetadata;
import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;

/**
 * Element for <code>basic</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class BasicAttributeElement extends PhysicalAttributeElement implements BasicAttributeMetadata {

	private EnumType enumType = EnumType.ORDINAL;
	private boolean lob = false;
	private boolean optional = true;
	private FetchType fetchType;

	/**
	 * @param parent
	 *            the metamodel
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicAttributeElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, //
			ElementConstants.ELEMENT_TEMPORAL, //
			ElementConstants.ELEMENT_ENUMERATED, //
			ElementConstants.ELEMENT_COLUMN, //
			ElementConstants.ELEMENT_LOB);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		super.generate();

		this.fetchType = FetchType.valueOf(this.getAttribute(ElementConstants.ATTR_FETCH, FetchType.EAGER.name()));
		this.optional = this.getAttribute(ElementConstants.ATTR_OPTIONAL, true);
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
	public IndexMetadata getIndex() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		super.handleChild(child);

		if (child instanceof EnumeratedElement) {
			this.enumType = ((EnumeratedElement) child).getEnumType();
		}

		if (child instanceof LobElement) {
			this.lob = true;
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

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return this.optional;
	}
}
