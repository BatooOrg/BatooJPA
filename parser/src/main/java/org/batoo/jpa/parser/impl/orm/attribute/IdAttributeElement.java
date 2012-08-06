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

import org.batoo.jpa.parser.impl.orm.Element;
import org.batoo.jpa.parser.impl.orm.ElementConstants;
import org.batoo.jpa.parser.impl.orm.GeneratedValueElement;
import org.batoo.jpa.parser.impl.orm.ParentElement;
import org.batoo.jpa.parser.impl.orm.SequenceGeneratorElement;
import org.batoo.jpa.parser.impl.orm.TableGeneratorElement;
import org.batoo.jpa.parser.metadata.GeneratedValueMetadata;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.attribute.IdAttributeMetadata;

/**
 * Element for <code>id</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class IdAttributeElement extends PhysicalAttributeElement implements IdAttributeMetadata {

	private GeneratedValueMetadata generatedValue;
	private SequenceGeneratorMetadata sequenceGenerator;
	private TableGeneratorMetadata tableGenerator;

	/**
	 * @param parent
	 *            the metamodel
	 * @param attributes
	 *            the attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdAttributeElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes, ElementConstants.ELEMENT_TEMPORAL, //
			ElementConstants.ELEMENT_COLUMN,//
			ElementConstants.ELEMENT_GENERATED_VALUE, //
			ElementConstants.ELEMENT_SEQUENCE_GENERATOR, //
			ElementConstants.ELEMENT_TABLE_GENERATOR);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public GeneratedValueMetadata getGeneratedValue() {
		return this.generatedValue;
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
	public TableGeneratorMetadata getTableGenerator() {
		return this.tableGenerator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		super.handleChild(child);

		if (child instanceof GeneratedValueElement) {
			this.generatedValue = (GeneratedValueElement) child;
		}

		if (child instanceof SequenceGeneratorElement) {
			this.sequenceGenerator = (SequenceGeneratorElement) child;
		}

		if (child instanceof TableGeneratorElement) {
			this.tableGenerator = (TableGeneratorElement) child;
		}
	}
}
