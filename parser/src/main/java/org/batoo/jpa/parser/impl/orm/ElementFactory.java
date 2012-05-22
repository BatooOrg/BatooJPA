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
package org.batoo.jpa.parser.impl.orm;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.orm.CascadesElementFactory.CascadeAllElementFactory;
import org.batoo.jpa.parser.impl.orm.CascadesElementFactory.CascadeDetachElementFactory;
import org.batoo.jpa.parser.impl.orm.CascadesElementFactory.CascadeMergeElementFactory;
import org.batoo.jpa.parser.impl.orm.CascadesElementFactory.CascadePersistElementFactory;
import org.batoo.jpa.parser.impl.orm.CascadesElementFactory.CascadeRefreshElementFactory;
import org.batoo.jpa.parser.impl.orm.CascadesElementFactory.CascadeRemoveElementFactory;
import org.batoo.jpa.parser.impl.orm.attribute.AttributesElementFactory;
import org.batoo.jpa.parser.impl.orm.attribute.BasicAttributeElementFactory;
import org.batoo.jpa.parser.impl.orm.attribute.EmbeddedAttributeElementFactory;
import org.batoo.jpa.parser.impl.orm.attribute.EmbeddedIdAttributeElementFactory;
import org.batoo.jpa.parser.impl.orm.attribute.IdAttributeElementFactory;
import org.batoo.jpa.parser.impl.orm.attribute.ManyToManyAttributeElementFactory;
import org.batoo.jpa.parser.impl.orm.attribute.ManyToOneAttributeElementFactory;
import org.batoo.jpa.parser.impl.orm.attribute.OneToManyAttributeElementFactory;
import org.batoo.jpa.parser.impl.orm.attribute.OneToOneAttributeElementFactory;
import org.batoo.jpa.parser.impl.orm.attribute.TransientElementFactory;
import org.batoo.jpa.parser.impl.orm.attribute.VersionAttributeElementFactory;
import org.batoo.jpa.parser.impl.orm.type.EntityElementFactory;
import org.batoo.jpa.parser.metadata.LocatableMatadata;
import org.xml.sax.Locator;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Base class for element factories.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class ElementFactory extends ElementFactoryConstants implements LocatableMatadata {

	private static final BLogger LOG = BLoggerFactory.getLogger(ElementFactory.class);

	private static final Map<String, Class<?>> factoryMap = Maps.newHashMap();

	static {
		// metamodel
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_ENTITY_MAPPINGS, EntityMappingsFactory.class);

		// managed types
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_ENTITY, EntityElementFactory.class);

		// attributes
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_ATTRIBUTES, AttributesElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_TRANSIENT, TransientElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_EMBEDDED, IdAttributeElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_EMBEDDED_ID, EmbeddedIdAttributeElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_VERSION, VersionAttributeElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_BASIC, BasicAttributeElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_EMBEDDED, EmbeddedAttributeElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_ONE_TO_ONE, OneToOneAttributeElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_ONE_TO_MANY, OneToManyAttributeElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_MANY_TO_MANY, ManyToManyAttributeElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_MANY_TO_ONE, ManyToOneAttributeElementFactory.class);

		// id
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_GENERATED_VALUE, GeneratedValueElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_SEQUENCE_GENERATOR, SequenceGeneratorElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_TABLE_GENERATOR, TableGeneratorElementFactory.class);

		// enum types
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_ACCESS, AccessElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_TEMPORAL, TemporalElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_ENUMERATED, EnumeratedElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_LOB, LobElementFactory.class);

		// overrides
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_ASSOCIATION_OVERRIDE, AssociationOverrideElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_ATTRIBUTE_OVERRIDE, AttributeOverrideElementFactory.class);

		// cascades
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_CASCADE, CascadesElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_CASCADE_ALL, CascadeAllElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_CASCADE_DETACH, CascadeDetachElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_CASCADE_MERGE, CascadeMergeElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_CASCADE_PERSIST, CascadePersistElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_CASCADE_REFRESH, CascadeRefreshElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_CASCADE_REMOVE, CascadeRemoveElementFactory.class);

		// columns
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_COLUMN, ColumnElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_COLUMN_NAME, ColumnNameElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_INVERSE_JOIN_COLUMN, InverseJoinColumnElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_JOIN_COLUMN, JoinColumnElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_PRIMARY_KEY_JOIN_COLUMN, PrimaryKeyJoinColumnElementFactory.class);

		// tables
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_JOIN_TABLE, JoinTableElementFactory.class);
		ElementFactory.factoryMap.put(ElementFactoryConstants.ELEMENT_UNIQUE_CONSTRAINT, UniqueConstraintsElementFactory.class);
	}

	/**
	 * Constructs and element factory for the <code>element</code>
	 * 
	 * @param parent
	 *            the parent element
	 * @param attributes
	 *            the attributes of the element
	 * @param xmlLocation
	 *            the xml location of the element
	 * @return the element factory created
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static ElementFactory forElement(ElementFactory parent, Map<String, String> attributes, XmlLocation xmlLocation) {
		final Class<?> clazz = ElementFactory.factoryMap.get(xmlLocation.getLocalName());

		try {
			final Constructor<?> constructor = clazz.getConstructor(ParentElementFactory.class, Map.class);
			final ElementFactory factory = (ElementFactory) constructor.newInstance(parent, attributes);

			factory.setLocation(xmlLocation);

			return factory;
		}
		catch (final Exception e) {} // not possible

		// unreachable
		return null;
	}

	private final ParentElementFactory parent;
	private final Map<String, String> attributes;
	private final Set<String> expectedChildElements;
	private XmlLocation location;

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * @param expectedChildElements
	 *            the name of the elements expected
	 * @since $version
	 * @author hceylan
	 */
	public ElementFactory(ParentElementFactory parent, Map<String, String> attributes, String[] expectedChildElements) {
		super();

		this.parent = parent;
		this.attributes = attributes;
		this.expectedChildElements = Sets.newHashSet(expectedChildElements);
	}

	/**
	 * Registers the character data of the element with the element factory;
	 * 
	 * @param cdata
	 *            the character data
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void cdata(String cdata) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates the element and hands over to the parent.
	 * <p>
	 * if the parent is null, that the factory is {@link EntityMappingsFactory}, the model element is added to the metamodel.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void end() {
		this.generate();

		if (this.parent != null) {
			ElementFactory.LOG.trace("Parsed child {0}", this);

			this.parent.handleChild(this);
		}
	}

	/**
	 * Checks if the <code>element</code> is expected.
	 * 
	 * @param element
	 *            the name of the element
	 * @param locator
	 *            the XML locator
	 * @throws MappingException
	 *             thrown if the <code>element</code> is not expected.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void expected(String element, Locator locator) throws MappingException {
		if (!this.expectedChildElements.contains(element)) {
			throw new MappingException("Unexpected element '" + element + "' encountered.", this.location.toString());
		}
	}

	/**
	 * Generates the elements artifact.
	 * <p>
	 * Element factories implement the method to generate their artifacts.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void generate() {
		// noop
	}

	/**
	 * Returns the attribute value for the <code>name</code>.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the value or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getAttribute(String name) {
		return this.attributes.get(name);
	}

	/**
	 * Returns the boolean attribute value for the <code>name</code>.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @param devault
	 *            the default value
	 * @return the integer value or default
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected boolean getAttribute(String name, boolean devault) {
		final String value = this.attributes.get(name);

		return value != null ? Boolean.valueOf(value) : devault;
	}

	/**
	 * Returns the integer attribute value for the <code>name</code>.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @param devault
	 *            the default value
	 * @return the integer value or default
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected int getAttribute(String name, int devault) {
		final String value = this.attributes.get(name);

		return value != null ? Integer.valueOf(value) : devault;
	}

	/**
	 * Returns the attribute value for the <code>name</code>.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @param devault
	 *            the default value
	 * @return the value or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getAttribute(String name, String devault) {
		final String value = this.attributes.get(name);

		return value != null ? value : devault;
	}

	/**
	 * Returns the expectedChildElements of the ElementFactory.
	 * 
	 * @return the expectedChildElements of the ElementFactory
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Set<String> getExpectedChildElements() {
		return this.expectedChildElements;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getLocation() {
		return this.location.toString();
	}

	/**
	 * Returns the parent of the ElementFactory.
	 * 
	 * @return the parent of the ElementFactory
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ParentElementFactory getParent() {
		return this.parent;
	}

	/**
	 * Sets the XML location of the element
	 * 
	 * @param xmlLocation
	 *            the XML location to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void setLocation(XmlLocation xmlLocation) {
		this.location = xmlLocation;
	}
}
