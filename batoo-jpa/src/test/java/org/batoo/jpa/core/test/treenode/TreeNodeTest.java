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

package org.batoo.jpa.core.test.treenode;

import java.util.List;

import javax.persistence.EntityManager;
import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * @author kozzi11
 * 
 * @since 2.0.1
 */
public class TreeNodeTest extends BaseCoreTest {

	private Node generateBinaryTree() {
		Node root = new Node(1);
		List<Node> children = Lists.newArrayList();
		children.add(new Node(2, root));
		children.add(new Node(3, root));
		root.setChildren(children);

		return root;
	}
	
	/**
	 * 
	 * @since 2.0.1
	 */
	@Before
	public void prepareCountries() {
		this.persist(this.generateBinaryTree());
		this.commit();
	}

	/**
	 * Tests to {@link EntityManager#remove(Object)} root with remove children.
	 * 
	 * @since 2.0.1
	 */
	@Test
	public void testUpdateAndDelete() {
		final Node root = this.find(Node.class, 1);
		
		Node newRoot = new Node(1);
		this.persist(newRoot);
		
		List<Node> children = root.getChildren();
		root.setChildren(null);
		
		for(Node child: children) {
			child.setParent(newRoot);
		}
		
		newRoot.setChildren(children);
		this.remove(root);
		
		this.commit();
		this.close();

		final Node newRoot2 = this.find(Node.class, newRoot.getId());
		Assert.assertEquals(newRoot2.getMyValue(), newRoot.getMyValue());
		Assert.assertEquals(newRoot.getChildren().size(), newRoot2.getChildren().size());
	}
}
