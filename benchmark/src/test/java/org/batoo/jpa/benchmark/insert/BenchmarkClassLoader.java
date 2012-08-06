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
package org.batoo.jpa.benchmark.insert;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;

/**
 * A test class loader to map test persistence.xml's
 * 
 * @author hceylan
 * 
 * @since $version
 */
public class BenchmarkClassLoader extends ClassLoader {

	/**
	 * Benchmark type.
	 * 
	 * @author hceylan
	 * @since $version
	 */
	public enum Type {
		/**
		 * Batoo
		 */
		BATOO,

		/**
		 * Hibernate
		 */
		HIBERNATE
	}

	private final Type type;
	private final String xml;

	/**
	 * @param parent
	 *            the parent class loader
	 * @param type
	 *            the type
	 * @param xml
	 *            the xml
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BenchmarkClassLoader(ClassLoader parent, Type type, String xml) {
		super(parent);

		this.type = type;
		this.xml = xml;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public URL getResource(String name) {
		if ("META-INF/persistence.xml".equals(name)) {
			return super.getResource(this.type.name().toLowerCase(Locale.ENGLISH) + "-" + this.xml + ".xml");
		}

		return super.getResource(name);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		if ("META-INF/persistence.xml".equals(name)) {
			return super.getResources(this.type.name().toLowerCase(Locale.ENGLISH) + "-" + this.xml + ".xml");
		}

		return super.getResources(name);
	}
}
