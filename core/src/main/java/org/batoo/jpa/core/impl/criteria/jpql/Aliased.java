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
package org.batoo.jpa.core.impl.criteria.jpql;

import org.antlr.runtime.tree.Tree;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class Aliased {

	private final Qualified qualified;
	private final String alias;

	/**
	 * @param aliased
	 *            aliased tree
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Aliased(Tree aliased) {
		super();

		this.qualified = new Qualified(aliased.getChild(0));

		if (aliased.getChildCount() > 1) {
			this.alias = aliased.getChild(1).getText();
		}
		else {
			this.alias = null;
		}
	}

	/**
	 * Returns the alias of the Aliased.
	 * 
	 * @return the alias of the Aliased
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getAlias() {
		return this.alias;
	}

	/**
	 * Returns the qualified of the Aliased.
	 * 
	 * @return the qualified of the Aliased
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Qualified getQualified() {
		return this.qualified;
	}
}
