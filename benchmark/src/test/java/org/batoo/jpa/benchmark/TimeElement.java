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
package org.batoo.jpa.benchmark;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class TimeElement extends HashMap<String, TimeElement> implements Comparable<TimeElement> {

	private static final long serialVersionUID = 1L;
	private long time;
	private long self;
	private final String key;
	private int hits;
	private int selfHit;
	private long timeWithoutDerby;

	/**
	 * @param key
	 *            the key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TimeElement(String key) {
		this.key = key;
	}

	/**
	 * @param used
	 *            the time used
	 * @param self
	 *            time used by self
	 * @param inDerby
	 *            time is in derby stack
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addTime(long used, boolean self, boolean inDerby) {
		this.hits++;
		this.time += used;
		if (!inDerby) {
			this.timeWithoutDerby += used;
		}
		if (self) {
			this.selfHit++;
			this.self += used;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int compareTo(TimeElement o) {
		return this.key.compareTo(o.key);
	}

	/**
	 * @param rowNo
	 *            the row no
	 * @param depth
	 *            the depth
	 * @return the row no
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int dump(int rowNo, int depth) {
		if (depth > 0 && this.timeWithoutDerby > 10000000) {
			rowNo++;
			final String tabs = StringUtils.repeat(" ", depth);
			System.out.println(String.format("%010d", rowNo) + //
				" " + String.format("%010d", depth) + //
				" " + String.format("%010d", this.hits) + //
				" " + String.format("%010d", this.selfHit) + //
				" " + String.format("%010d", this.time / 1000000) + //
				" " + String.format("%010d", this.timeWithoutDerby / 1000000) + //
				" " + String.format("%010d", this.self / 1000000) + //
				tabs + this.key);
		}

		final List<TimeElement> children = Lists.newArrayList(this.values());
		Collections.sort(children);
		for (final TimeElement child : children) {
			rowNo = child.dump(rowNo, depth + 1);
		}

		return rowNo;
	}

	/**
	 * @param rowNo
	 *            the row no
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void dump2(int rowNo) {
		System.out.println(String.format("%010d", rowNo) + //
			" " + String.format("%010d", this.hits) + //
			" " + String.format("%010d", this.selfHit) + //
			" " + this.key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TimeElement get(Object key) {
		TimeElement timeElement = super.get(key);

		if (timeElement == null) {
			this.put((String) key, timeElement = new TimeElement((String) key));
		}

		return timeElement;
	}

	/**
	 * @return self
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Long getSelf() {
		return this.self;
	}
}
