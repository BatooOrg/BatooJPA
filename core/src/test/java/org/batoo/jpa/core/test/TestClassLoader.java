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
package org.batoo.jpa.core.test;

import java.io.InputStream;

/**
 * A test class loader to map test persistence.xml's
 * 
 * @author hceylan
 * 
 * @since $version
 */
public class TestClassLoader extends ClassLoader {

	private String root;

	/**
	 * @since $version
	 * @author hceylan
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
	 * @since $version
	 * @author hceylan
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
		if (name.startsWith("META-INF")) {
			name = this.root + name.substring(8);
			return super.getResourceAsStream(name);
		}

		return super.getResourceAsStream(name);
	}

	/**
	 * Sets the root of the test.
	 * 
	 * @param root
	 *            the root of the test
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setRoot(String root) {
		this.root = root.replace('.', '/');
	}
}
