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

import java.util.LinkedList;

import org.antlr.runtime.tree.Tree;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class Qualified {

	private final LinkedList<String> segments = Lists.newLinkedList();

	/**
	 * @param tree
	 *            the tree
	 * 
	 * @since $version
	 */
	public Qualified(Tree tree) {
		super();

		int i = 0;

		for (; i < tree.getChildCount(); i++) {
			final Tree child = tree.getChild(i);
			this.getSegments().add(child.getText());
		}
	}

	/**
	 * Returns the segments of the Qualified.
	 * 
	 * @return the segments of the Qualified
	 * 
	 * @since $version
	 */
	public LinkedList<String> getSegments() {
		return this.segments;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return Joiner.on(".").join(this.getSegments());
	}
}
