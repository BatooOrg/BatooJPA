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

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class Pair<T> {

	private final T first;
	private final T second;

	/**
	 * @param first
	 * @param second
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Pair(T first, T second) {
		super();
		this.first = first;
		this.second = second;
	}

	/**
	 * @return the pair as set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public Set<T> asSet() {
		return Sets.newHashSet(this.first, this.second);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		return this.asSet().equals(((Pair<T>) obj).asSet());
	}

	/**
	 * Returns the first.
	 * 
	 * @return the first
	 * @since $version
	 */
	public T getFirst() {
		return this.first;
	}

	/**
	 * Returns the second.
	 * 
	 * @return the second
	 * @since $version
	 */
	public T getSecond() {
		return this.second;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.first == null) ? 0 : this.first.hashCode());
		result = (prime * result) + ((this.second == null) ? 0 : this.second.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "[" + this.first + ", " + this.second + "]";
	}
}
