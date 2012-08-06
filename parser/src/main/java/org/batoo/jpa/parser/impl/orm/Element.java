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

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.orm.CascadesElement.CascadeAllElement;
import org.batoo.jpa.parser.impl.orm.CascadesElement.CascadeDetachElement;
import org.batoo.jpa.parser.impl.orm.CascadesElement.CascadeMergeElement;
import org.batoo.jpa.parser.impl.orm.CascadesElement.CascadePersistElement;
import org.batoo.jpa.parser.impl.orm.CascadesElement.CascadeRefreshElement;
import org.batoo.jpa.parser.impl.orm.CascadesElement.CascadeRemoveElement;
import org.batoo.jpa.parser.impl.orm.EntityListenerElement.PostLoadElement;
import org.batoo.jpa.parser.impl.orm.EntityListenerElement.PostPersistElement;
import org.batoo.jpa.parser.impl.orm.EntityListenerElement.PostRemoveElement;
import org.batoo.jpa.parser.impl.orm.EntityListenerElement.PostUpdateElement;
import org.batoo.jpa.parser.impl.orm.EntityListenerElement.PrePersistElement;
import org.batoo.jpa.parser.impl.orm.EntityListenerElement.PreRemoveElement;
import org.batoo.jpa.parser.impl.orm.EntityListenerElement.PreUpdateElement;
import org.batoo.jpa.parser.impl.orm.attribute.AttributesElement;
import org.batoo.jpa.parser.impl.orm.attribute.BasicAttributeElement;
import org.batoo.jpa.parser.impl.orm.attribute.ElementCollectionAttributeElement;
import org.batoo.jpa.parser.impl.orm.attribute.EmbeddedAttributeElement;
import org.batoo.jpa.parser.impl.orm.attribute.EmbeddedIdAttributeElement;
import org.batoo.jpa.parser.impl.orm.attribute.IdAttributeElement;
import org.batoo.jpa.parser.impl.orm.attribute.ManyToManyAttributeElement;
import org.batoo.jpa.parser.impl.orm.attribute.ManyToOneAttributeElement;
import org.batoo.jpa.parser.impl.orm.attribute.OneToManyAttributeElement;
import org.batoo.jpa.parser.impl.orm.attribute.OneToOneAttributeElement;
import org.batoo.jpa.parser.impl.orm.attribute.TransientElement;
import org.batoo.jpa.parser.impl.orm.attribute.VersionAttributeElement;
import org.batoo.jpa.parser.impl.orm.type.EmbeddableElementFactory;
import org.batoo.jpa.parser.impl.orm.type.EntityElementFactory;
import org.batoo.jpa.parser.impl.orm.type.MappedSuperclassElementFactory;
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
public abstract class Element extends ElementConstants implements LocatableMatadata {

	private static final BLogger LOG = BLoggerFactory.getLogger(Element.class);

	private static final Map<String, Class<?>> factoryMap = Maps.newHashMap();

	static {
		// metamodel
		Element.factoryMap.put(ElementConstants.ELEMENT_ENTITY_MAPPINGS, EntityMappings.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_CATALOG, InheritanceElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_SCHEMA, InheritanceElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_ACCESS, AccessElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_PERSISTENT_UNIT_METADATA, PersistenceUnitMetadataElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_XML_MAPPING_METADATA_COMPLETE, XmlMappingMetadataCompleteElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_NAMED_QUERY, NamedQueryElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_NAMED_NATIVE_QUERY, NamedNativeQueryElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_LOCK_MODE, LockModeElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_QUERY, QueryElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_HINT, HintElement.class);

		// listeners
		Element.factoryMap.put(ElementConstants.ELEMENT_EXCLUDE_DEFAULT_LISTENERS, ExcludeDefaultListenersElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_EXCLUDE_SUPERCLASS_LISTENERS, ExcludeSuperclassListenersElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_ENTITY_LISTENERS, EntityListenersElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_ENTITY_LISTENER, EntityListenerElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_POST_LOAD, PostLoadElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_POST_PERSIST, PostPersistElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_POST_REMOVE, PostRemoveElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_POST_UPDATE, PostUpdateElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_PRE_PERSIST, PrePersistElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_PRE_REMOVE, PreRemoveElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_PRE_UPDATE, PreUpdateElement.class);

		// managed types
		Element.factoryMap.put(ElementConstants.ELEMENT_MAPPED_SUPERCLASS, MappedSuperclassElementFactory.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_EMBEDDABLE, EmbeddableElementFactory.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_ENTITY, EntityElementFactory.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_INHERITANCE, InheritanceElement.class);

		// attributes
		Element.factoryMap.put(ElementConstants.ELEMENT_ATTRIBUTES, AttributesElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_TRANSIENT, TransientElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_ID, IdAttributeElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_ID_CLASS, IdClassElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_EMBEDDED, IdAttributeElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_EMBEDDED_ID, EmbeddedIdAttributeElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_VERSION, VersionAttributeElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_BASIC, BasicAttributeElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_EMBEDDED, EmbeddedAttributeElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_ELEMENT_COLLECTION, ElementCollectionAttributeElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_ONE_TO_ONE, OneToOneAttributeElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_ONE_TO_MANY, OneToManyAttributeElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_MANY_TO_MANY, ManyToManyAttributeElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_MANY_TO_ONE, ManyToOneAttributeElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_MAP_KEY, MapKeyElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_MAP_KEY_CLASS, MapKeyElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_MAP_KEY_ENUMERATED, MapKeyEnumeratedElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_MAP_KEY_TEMPORAL, MapKeyTemporalElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_ORDER_BY, OrderByElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_ORDER_COLUMN, OrderColumnElement.class);

		// id
		Element.factoryMap.put(ElementConstants.ELEMENT_GENERATED_VALUE, GeneratedValueElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_SEQUENCE_GENERATOR, SequenceGeneratorElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_TABLE_GENERATOR, TableGeneratorElement.class);

		// enum types
		Element.factoryMap.put(ElementConstants.ELEMENT_TEMPORAL, TemporalElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_ENUMERATED, EnumeratedElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_LOB, LobElement.class);

		// overrides
		Element.factoryMap.put(ElementConstants.ELEMENT_ASSOCIATION_OVERRIDE, AssociationOverrideElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_ATTRIBUTE_OVERRIDE, AttributeOverrideElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_MAP_KEY_ATTRIBUTE_OVERRIDE, MapKeyAttributeOverrideElement.class);

		// cascades
		Element.factoryMap.put(ElementConstants.ELEMENT_CASCADE, CascadesElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_CASCADE_ALL, CascadeAllElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_CASCADE_DETACH, CascadeDetachElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_CASCADE_MERGE, CascadeMergeElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_CASCADE_PERSIST, CascadePersistElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_CASCADE_REFRESH, CascadeRefreshElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_CASCADE_REMOVE, CascadeRemoveElement.class);

		// columns
		Element.factoryMap.put(ElementConstants.ELEMENT_COLUMN, ColumnElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_MAP_KEY_COLUMN, MapKeyColumnElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_COLUMN_NAME, ColumnNameElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_INVERSE_JOIN_COLUMN, InverseJoinColumnElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_JOIN_COLUMN, JoinColumnElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_PRIMARY_KEY_JOIN_COLUMN, PrimaryKeyJoinColumnElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_DISCRIMINATOR_COLUMN, DiscriminatorColumnElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_DISCRIMINATOR_VALUE, DiscriminatorValueElement.class);

		// tables
		Element.factoryMap.put(ElementConstants.ELEMENT_TABLE, TableElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_SECONDARY_TABLE, SecondaryTableElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_JOIN_TABLE, JoinTableElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_COLLECTION_TABLE, JoinTableElement.class);
		Element.factoryMap.put(ElementConstants.ELEMENT_UNIQUE_CONSTRAINT, UniqueConstraintsElement.class);
	}

	/**
	 * Constructs and Element for the <code>element</code>
	 * 
	 * @param parent
	 *            the parent element
	 * @param attributes
	 *            the attributes of the element
	 * @param xmlLocator
	 *            the XML locator of the element
	 * @return the element created
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static Element forElement(Element parent, Map<String, String> attributes, XmlLocator xmlLocator) {
		final Class<?> clazz = Element.factoryMap.get(xmlLocator.getLocalName());

		try {
			final Constructor<?> constructor = clazz.getConstructor(ParentElement.class, Map.class);
			final Element element = (Element) constructor.newInstance(parent, attributes);

			element.setLocator(xmlLocator);

			return element;
		}
		catch (final Exception e) {} // not possible

		// unreachable
		return null;
	}

	private final ParentElement parent;
	private final Map<String, String> attributes;
	private final Set<String> expectedChildElements;
	private XmlLocator locator;

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
	public Element(ParentElement parent, Map<String, String> attributes, String[] expectedChildElements) {
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
	 * if the parent is null, that the factory is {@link EntityMappings}, the model element is added to the metamodel.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void end() {
		this.generate();

		if (this.parent != null) {
			Element.LOG.trace("Parsed child {0}", this);

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
			throw new MappingException("Unexpected element '" + element + "' encountered.", this.locator);
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
	 * Returns the expectedChildElements of the Element.
	 * 
	 * @return the expectedChildElements of the Element
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
	public XmlLocator getLocator() {
		return this.locator;
	}

	/**
	 * Returns the parent of the Element.
	 * 
	 * @return the parent of the Element
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ParentElement getParent() {
		return this.parent;
	}

	/**
	 * Sets the XML location of the element
	 * 
	 * @param xmlLocator
	 *            the XML location to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void setLocator(XmlLocator xmlLocator) {
		this.locator = xmlLocator;
	}
}
