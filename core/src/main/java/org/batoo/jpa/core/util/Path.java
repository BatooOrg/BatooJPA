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
package org.batoo.jpa.core.util;

import java.util.LinkedList;

import org.batoo.jpa.core.impl.mapping.Association;

/**
 * Representation of a path in a select statement
 * 
 * @author hceylan
 * @since $version
 */
public class Path extends LinkedList<Association<?, ?>> {

	private static final long serialVersionUID = 1L;

	private boolean inverse;
	private boolean lazy;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Path() {
		super();
	}

	/**
	 * @param path
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Path(Path path) {
		this.addAll(path);
	}

	/**
	 * Returns if the path is inverse.
	 * 
	 * @return the inverse
	 * @since $version
	 */
	public boolean isInverse() {
		return this.inverse;
	}

	/**
	 * Returns if the path is lazy.
	 * 
	 * @return true if the path is lazy
	 * @since $version
	 */
	public boolean isLazy() {
		return this.lazy;
	}

	/**
	 * Marks the path as inverse path.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setInverse() {
		this.inverse = true;
	}

	/**
	 * Marks the path as lazy.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setLazy() {
		this.lazy = true;
	}

}
