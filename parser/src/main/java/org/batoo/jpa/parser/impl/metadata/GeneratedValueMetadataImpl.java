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
package org.batoo.jpa.parser.impl.metadata;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.GeneratedValueMetadata;

/**
 * Annotated definition of generated values.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class GeneratedValueMetadataImpl implements GeneratedValueMetadata {

	private final AbstractLocator locator;
	private final GeneratedValue generatedValue;

	/**
	 * @param locator
	 *            the java locator
	 * @param generatedValue
	 *            the {@link GeneratedValue} annotation
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public GeneratedValueMetadataImpl(AbstractLocator locator, GeneratedValue generatedValue) {
		super();

		this.locator = locator;
		this.generatedValue = generatedValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getGenerator() {
		return this.generatedValue.generator();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public GenerationType getStrategy() {
		return this.generatedValue.strategy();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return this.generatedValue.toString();
	}
}
