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

/**
 * A utility class to hold two values together.
 * 
 * @param <F>
 *            the first type
 * @param <S>
 *            the second type
 * 
 * @author hceylan
 * @since $version
 */
public class Pair<F, S> {

	/**
	 * Creates a pair.
	 * 
	 * @param first
	 *            the first value
	 * @param second
	 *            the second value
	 * @param <F>
	 *            the first type
	 * @param <S>
	 *            the second type
	 * @return the pair created
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static <F, S> Pair<F, S> create(F first, S second) {
		return new Pair<F, S>(first, second);
	}

	private final F first;

	private final S second;

	/**
	 * @param first
	 *            the first
	 * @param second
	 *            the second
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Pair(F first, S second) {
		super();

		this.first = first;
		this.second = second;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
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
		final Pair<?, ?> other = (Pair<?, ?>) obj;
		if (this.first == null) {
			if (other.first != null) {
				return false;
			}
		}
		else if (!this.first.equals(other.first)) {
			return false;
		}
		if (this.second == null) {
			if (other.second != null) {
				return false;
			}
		}
		else if (!this.second.equals(other.second)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the first.
	 * 
	 * @return the first
	 * @since $version
	 */
	public F getFirst() {
		return this.first;
	}

	/**
	 * Returns the second.
	 * 
	 * @return the second
	 * @since $version
	 */
	public S getSecond() {
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
