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
