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

import org.batoo.jpa.parser.impl.orm.Element;
import org.batoo.jpa.parser.impl.orm.ElementConstants;
import org.batoo.jpa.parser.impl.orm.ParentElement;
import org.batoo.jpa.parser.metadata.attribute.AttributesMetadata;
import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.ElementCollectionAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedIdAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.IdAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.ManyToManyAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.ManyToOneAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.OneToManyAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.OneToOneAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.TransientAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.VersionAttributeMetadata;

import com.google.common.collect.Lists;

/**
 * Element for <code>attributes</code> elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class AttributesElement extends ParentElement implements AttributesMetadata {

	private final List<TransientAttributeMetadata> transients = Lists.newArrayList();
	private final List<IdAttributeMetadata> ids = Lists.newArrayList();
	private final List<EmbeddedIdAttributeMetadata> embeddedIds = Lists.newArrayList();
	private final List<EmbeddedAttributeMetadata> embeddeds = Lists.newArrayList();
	private final List<BasicAttributeMetadata> basics = Lists.newArrayList();
	private final List<VersionAttributeMetadata> versions = Lists.newArrayList();
	private final List<ElementCollectionAttributeMetadata> elementCollections = Lists.newArrayList();
	private final List<OneToOneAttributeMetadata> oneToOnes = Lists.newArrayList();
	private final List<ManyToOneAttributeMetadata> manyToOnes = Lists.newArrayList();
	private final List<OneToManyAttributeMetadata> oneToManies = Lists.newArrayList();
	private final List<ManyToManyAttributeMetadata> manyToManies = Lists.newArrayList();

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public AttributesElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes,//
			ElementConstants.ELEMENT_TRANSIENT, //
			ElementConstants.ELEMENT_ID, //
			ElementConstants.ELEMENT_EMBEDDED_ID, //
			ElementConstants.ELEMENT_EMBEDDED, //
			ElementConstants.ELEMENT_VERSION, //
			ElementConstants.ELEMENT_BASIC, //
			ElementConstants.ELEMENT_ELEMENT_COLLECTION, //
			ElementConstants.ELEMENT_ONE_TO_ONE, //
			ElementConstants.ELEMENT_MANY_TO_ONE, //
			ElementConstants.ELEMENT_ONE_TO_MANY, //
			ElementConstants.ELEMENT_MANY_TO_MANY);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<BasicAttributeMetadata> getBasics() {
		return this.basics;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<ElementCollectionAttributeMetadata> getElementCollections() {
		return this.elementCollections;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<EmbeddedIdAttributeMetadata> getEmbeddedIds() {
		return this.embeddedIds;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<EmbeddedAttributeMetadata> getEmbeddeds() {
		return this.embeddeds;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<IdAttributeMetadata> getIds() {
		return this.ids;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<ManyToManyAttributeMetadata> getManyToManies() {
		return this.manyToManies;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<ManyToOneAttributeMetadata> getManyToOnes() {
		return this.manyToOnes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<OneToManyAttributeMetadata> getOneToManies() {
		return this.oneToManies;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<OneToOneAttributeMetadata> getOneToOnes() {
		return this.oneToOnes;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<TransientAttributeMetadata> getTransients() {
		return this.transients;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public List<VersionAttributeMetadata> getVersions() {
		return this.versions;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected void handleChild(Element child) {
		if (child instanceof TransientElement) {
			this.getTransients().add((TransientAttributeMetadata) child);
		}

		if (child instanceof IdAttributeElement) {
			this.ids.add((IdAttributeElement) child);
		}

		if (child instanceof EmbeddedIdAttributeElement) {
			this.embeddedIds.add((EmbeddedIdAttributeElement) child);
		}

		if (child instanceof VersionAttributeElement) {
			this.versions.add((VersionAttributeElement) child);
		}

		if (child instanceof BasicAttributeElement) {
			this.basics.add((BasicAttributeElement) child);
		}

		if (child instanceof EmbeddedAttributeElement) {
			this.embeddeds.add((EmbeddedAttributeMetadata) child);
		}

		if (child instanceof ElementCollectionAttributeElement) {
			this.elementCollections.add((ElementCollectionAttributeMetadata) child);
		}

		if (child instanceof OneToOneAttributeMetadata) {
			this.oneToOnes.add((OneToOneAttributeMetadata) child);
		}

		if (child instanceof OneToManyAttributeMetadata) {
			this.oneToManies.add((OneToManyAttributeMetadata) child);
		}

		if (child instanceof ManyToOneAttributeMetadata) {
			this.manyToOnes.add((ManyToOneAttributeMetadata) child);
		}

		if (child instanceof ManyToManyAttributeMetadata) {
			this.manyToManies.add((ManyToManyAttributeMetadata) child);
		}
	}
}
