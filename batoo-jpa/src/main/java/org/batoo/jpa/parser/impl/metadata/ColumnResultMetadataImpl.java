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
package org.batoo.jpa.parser.impl.metadata;

import javax.persistence.ColumnResult;

import org.batoo.jpa.parser.AbstractLocator;
import org.batoo.jpa.parser.metadata.ColumnResultMetadata;

/**
 * 
 * @author asimarslan
 * @since 2.0.1
 */
public class ColumnResultMetadataImpl implements ColumnResultMetadata {
	private final AbstractLocator locator;
	private final String name;

	/**
	 * 
	 * @param locator
	 *            the java locator
	 * @param annotation
	 *            the annotation
	 * @since 2.0.1
	 */
	public ColumnResultMetadataImpl(AbstractLocator locator, ColumnResult annotation) {
		super();

		this.name = annotation.name();
		this.locator = locator;
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
	public String getName() {
		return this.name;
	}

}
