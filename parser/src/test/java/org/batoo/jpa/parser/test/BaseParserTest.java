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
package org.batoo.jpa.parser.test;

import java.util.List;

import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.test.BaseTest;
import org.batoo.jpa.parser.PersistenceParser;
import org.batoo.jpa.parser.metadata.Metadata;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.AttributesMetadata;
import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedIdAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.IdAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.ManyToManyAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.ManyToOneAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.OneToManyAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.OneToOneAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.TransientAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.VersionAttributeMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;
import org.batoo.jpa.parser.metadata.type.ManagedTypeMetadata;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Base test instrumentation class for PersistenceParser Tests.
 * 
 * @author hceylan
 * @since $version
 */
public class BaseParserTest extends BaseTest {

	private static final String DEFAULT = "default";

	/**
	 * Rule to get Persistence XML File name.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Rule
	public final TestWatcher watchman = new TestWatcher() {

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		protected void starting(Description description) {
			BaseParserTest.this.persistenceUnitName = BaseParserTest.DEFAULT;

			final PersistenceContext persistenceContext = description.getAnnotation(PersistenceContext.class);
			if (persistenceContext != null) {
				// if unit name is not the default set the unit name
				if (StringUtils.isNotBlank(persistenceContext.unitName())) {
					BaseParserTest.this.persistenceUnitName = persistenceContext.unitName();
				}
			}
		}
	};

	private final String ENTITY = "Entity";
	private final String ATTRIBUTE = "attribute";

	private PersistenceParser persistenceParser;
	private ClassLoader oldContextClassLoader;
	private String persistenceUnitName;

	/**
	 * Returns the attribute name <code>attribute</code> of the entity named <code>Entity</code>.
	 * 
	 * @return the attribute name <code>attribute</code> of the entity named <code>Entity</code>, or <code>null</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected AttributeMetadata a() {
		return this.a(this.ATTRIBUTE);
	}

	/**
	 * Returns the attribute name <code>attributeName</code> of the entity <code>entity</code>.
	 * 
	 * @param entity
	 *            the entity metadata
	 * @param attributeName
	 *            the name of the attribute
	 * @return the attribute name <code>attributeName</code> of the entity <code>entity</code>, or <code>null</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected AttributeMetadata a(EntityMetadata entity, String attributeName) {
		if (entity == null) {
			return null;
		}

		final AttributesMetadata attributes = entity.getAttributes();

		for (final IdAttributeMetadata attribute : attributes.getIds()) {
			if (attributeName.equals(attribute.getName())) {
				return attribute;
			}
		}

		for (final EmbeddedIdAttributeMetadata attribute : attributes.getEmbeddedIds()) {
			if (attributeName.equals(attribute.getName())) {
				return attribute;
			}
		}

		for (final BasicAttributeMetadata attribute : attributes.getBasics()) {
			if (attributeName.equals(attribute.getName())) {
				return attribute;
			}
		}

		for (final ManyToManyAttributeMetadata attribute : attributes.getManyToManies()) {
			if (attributeName.equals(attribute.getName())) {
				return attribute;
			}
		}

		for (final ManyToOneAttributeMetadata attribute : attributes.getManyToOnes()) {
			if (attributeName.equals(attribute.getName())) {
				return attribute;
			}
		}

		for (final OneToManyAttributeMetadata attribute : attributes.getOneToManies()) {
			if (attributeName.equals(attribute.getName())) {
				return attribute;
			}
		}

		for (final OneToOneAttributeMetadata attribute : attributes.getOneToOnes()) {
			if (attributeName.equals(attribute.getName())) {
				return attribute;
			}
		}

		for (final TransientAttributeMetadata attribute : attributes.getTransients()) {
			if (attributeName.equals(attribute.getName())) {
				return attribute;
			}
		}

		for (final VersionAttributeMetadata attribute : attributes.getVersions()) {
			if (attributeName.equals(attribute.getName())) {
				return attribute;
			}
		}

		return null;
	}

	/**
	 * Returns the attribute name <code>name</code> of the entity named <code>Entity</code>.
	 * 
	 * @param name
	 *            the name of the attribute
	 * @return the attribute name <code>name</code> of the entity named <code>Entity</code>, or <code>null</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected AttributeMetadata a(String name) {
		return this.a(this.e(), name);
	}

	/**
	 * Returns the attribute named <code>attributeName</code> of the entity named <code>entityName</code>.
	 * 
	 * @param entityName
	 *            the name of the entity
	 * @param attributeName
	 *            the name of the attribute
	 * @return the attribute name <code>attributeName</code> of the entity named <code>entityName</code>, or <code>null</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected AttributeMetadata a(String entityName, String attributeName) {
		return this.a(this.e(entityName), attributeName);
	}

	/**
	 * Returns the entity named <code>Entity</code>.
	 * 
	 * @return the entity named <code>Entity</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected final EntityMetadata e() {
		return this.e(this.ENTITY);
	}

	/**
	 * Returns the entity named <code>name</code>.
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity named <code>name</code>
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected EntityMetadata e(String name) {
		final List<ManagedTypeMetadata> entities = this.mm().getEntityMappings();
		for (final ManagedTypeMetadata type : entities) {
			if (type instanceof EntityMetadata) {
				final EntityMetadata entity = (EntityMetadata) type;
				if (name.equals(entity.getName())) {
					return entity;
				}
			}
		}

		return null;
	}

	/**
	 * Returns the metadata.
	 * 
	 * @return the metadata
	 * @since $version
	 */
	protected final Metadata mm() {
		return this.persistenceParser.getMetadata();
	}

	/**
	 * Consumes the Persistence XML.
	 * <p>
	 * The Persistence XML consumed is indicated by optional {@link PersistenceXml} annotation. Of the annotation does not exists then the
	 * class'es simple name is used.
	 * 
	 * @throws Exception
	 *             thrown in failures to set up.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Before
	@SuppressWarnings("javadoc")
	public void setUp() throws Exception {

		final Thread currentThread = Thread.currentThread();
		this.oldContextClassLoader = currentThread.getContextClassLoader();

		final TestClassLoader cl = new TestClassLoader(this.oldContextClassLoader);
		currentThread.setContextClassLoader(cl);
		cl.setRoot(this.getClass().getPackage().getName());

		this.persistenceParser = new PersistenceParser(this.persistenceUnitName);
	}

	/**
	 * Cleans up the test.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@After
	public void tearDown() {
		if (this.oldContextClassLoader != null) {
			Thread.currentThread().setContextClassLoader(this.oldContextClassLoader);
			this.oldContextClassLoader = null;
		}

	}
}
