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

package org.batoo.jpa.core.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * A test class loader to map test persistence.xml's
 * 
 * @author hceylan
 * 
 * @since 2.0.0
 */
public class TestClassLoader extends ClassLoader {

	private static final String PERSISTENCE_XML = "persistence.xml";
	private static final String ORM_XML = "orm.xml";

	private static final String FULL_PERSISTENCE_XML = "META-INF/" + TestClassLoader.PERSISTENCE_XML;
	private static final String FULL_ORM_XML = "META-INF/" + TestClassLoader.ORM_XML;

	private String root;

	/**
	 * @since 2.0.0
	 */
	public TestClassLoader() {
		super();
	}

	/**
	 * Sets the parent class loader.
	 * 
	 * @param parent
	 *            the parent class loader
	 * 
	 * @since 2.0.0
	 */
	public TestClassLoader(ClassLoader parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public InputStream getResourceAsStream(String name) {
		if (name.equals(TestClassLoader.FULL_PERSISTENCE_XML)) {
			name = this.root + "/" + TestClassLoader.PERSISTENCE_XML;
			return super.getResourceAsStream(name);
		}

		if (name.equals(TestClassLoader.FULL_ORM_XML)) {
			name = this.root + "/" + TestClassLoader.ORM_XML;
			return super.getResourceAsStream(name);
		}

		return super.getResourceAsStream(name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		if (name.equals(TestClassLoader.FULL_PERSISTENCE_XML)) {
			name = this.root + "/" + TestClassLoader.PERSISTENCE_XML;
			return super.getResources(name);
		}

		if (name.equals(TestClassLoader.FULL_ORM_XML)) {
			name = this.root + "/" + TestClassLoader.ORM_XML;
			return super.getResources(name);
		}

		return super.getResources(name);
	}

	/**
	 * Sets the root of the test.
	 * 
	 * @param root
	 *            the root of the test
	 * 
	 * @since 2.0.0
	 */
	public void setRoot(String root) {
		this.root = root.replace('.', '/');
	}
}
