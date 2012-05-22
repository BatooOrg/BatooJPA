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
package org.batoo.jpa.parser.impl.orm.attribute;

import java.util.List;
import java.util.Map;

import org.batoo.jpa.parser.impl.orm.ElementFactory;
import org.batoo.jpa.parser.impl.orm.ElementFactoryConstants;
import org.batoo.jpa.parser.impl.orm.ParentElementFactory;
import org.batoo.jpa.parser.metadata.attribute.AttributesMetadata;
import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;
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
 * Element factory for <code>attributes</code> elements.
 * 
 * @author hceylan
 * @since $version
 */
public class AttributesElementFactory extends ParentElementFactory implements AttributesMetadata {

	private final List<TransientAttributeMetadata> transients = Lists.newArrayList();
	private final List<IdAttributeMetadata> ids = Lists.newArrayList();
	private final List<EmbeddedIdAttributeMetadata> embeddedIds = Lists.newArrayList();
	private final List<EmbeddedAttributeMetadata> embeddeds = Lists.newArrayList();
	private final List<BasicAttributeMetadata> basics = Lists.newArrayList();
	private final List<VersionAttributeMetadata> versions = Lists.newArrayList();
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
	 * @since $version
	 * @author hceylan
	 */
	public AttributesElementFactory(ParentElementFactory parent, Map<String, String> attributes) {
		super(parent, attributes,//
			ElementFactoryConstants.ELEMENT_TRANSIENT, //
			ElementFactoryConstants.ELEMENT_ID, //
			ElementFactoryConstants.ELEMENT_EMBEDDED_ID, //
			ElementFactoryConstants.ELEMENT_EMBEDDED, //
			ElementFactoryConstants.ELEMENT_VERSION, //
			ElementFactoryConstants.ELEMENT_BASIC, //
			ElementFactoryConstants.ELEMENT_ONE_TO_ONE, //
			ElementFactoryConstants.ELEMENT_MANY_TO_ONE, //
			ElementFactoryConstants.ELEMENT_ONE_TO_MANY, //
			ElementFactoryConstants.ELEMENT_MANY_TO_MANY);
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
	protected void handleChild(ElementFactory child) {
		if (child instanceof TransientElementFactory) {
			this.getTransients().add((TransientAttributeMetadata) child);
		}

		if (child instanceof IdAttributeElementFactory) {
			this.ids.add((IdAttributeElementFactory) child);
		}

		if (child instanceof EmbeddedIdAttributeElementFactory) {
			this.embeddedIds.add((EmbeddedIdAttributeElementFactory) child);
		}

		if (child instanceof VersionAttributeElementFactory) {
			this.versions.add((VersionAttributeElementFactory) child);
		}

		if (child instanceof BasicAttributeElementFactory) {
			this.basics.add((BasicAttributeElementFactory) child);
		}

		if (child instanceof EmbeddedAttributeElementFactory) {
			this.embeddeds.add((EmbeddedAttributeMetadata) child);
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
