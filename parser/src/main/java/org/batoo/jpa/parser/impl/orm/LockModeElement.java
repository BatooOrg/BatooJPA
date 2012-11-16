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

import java.util.Map;

import javax.persistence.LockModeType;

/**
 * Element for <code>lock-mode</code> elements.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class LockModeElement extends ChildElement {

	private LockModeType lockMode = null;

	/**
	 * @param parent
	 *            the parent element factory
	 * @param attributes
	 *            the attributes
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public LockModeElement(ParentElement parent, Map<String, String> attributes) {
		super(parent, attributes);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void cdata(String cdata) {
		this.lockMode = LockModeType.valueOf(cdata);
	}

	/**
	 * Returns the lock mode.
	 * 
	 * @return the lock mode
	 * 
	 * @since 2.0.0
	 * @author hceylan
	 */
	public LockModeType getLockMode() {
		return this.lockMode;
	}
}
