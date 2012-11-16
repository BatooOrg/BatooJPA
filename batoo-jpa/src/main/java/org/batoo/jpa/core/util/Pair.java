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
