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
package org.batoo.jpa.parser.impl.orm;

import java.util.List;
import java.util.Map;

import org.batoo.jpa.parser.metadata.EntityResultMetadata;
import org.batoo.jpa.parser.metadata.FieldResultMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>entity-result</code>
 * 
 * @author asimarslan
 * @since $version
 */
public class EntityResultElement extends ParentElement implements EntityResultMetadata {

	private String entityClass;// Class<?>
	private String discriminatorColumn;

	private final List<FieldResultMetadata> fields = Lists.newArrayList();

	/**
	 * 
	 * @param parent
	 * @param attributes
	 * @param expectedChildElements
	 * @since $version
	 */
	public EntityResultElement(ParentElement parent, Map<String, String> attributes, String... expectedChildElements) {
		super(parent, attributes, ParentElement.join(expectedChildElements, //
			ElementConstants.ELEMENT_FIELD_RESULT));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void generate() {
		this.entityClass = this.getAttribute(ElementConstants.ATTR_ENTITY_CLASS, ElementConstants.EMPTY);
		this.discriminatorColumn = this.getAttribute(ElementConstants.ATTR_DISCRIMINATOR_COLUMN, ElementConstants.EMPTY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getDiscriminatorColumn() {
		return this.discriminatorColumn;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getEntityClass() {
		return this.entityClass;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<FieldResultMetadata> getFields() {
		return this.fields;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof FieldResultElement) {
			this.fields.add((FieldResultMetadata) child);
		}
	}

}
