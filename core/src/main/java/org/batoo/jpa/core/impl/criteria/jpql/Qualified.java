/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
	 * @author hceylan
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
	 * @author hceylan
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
