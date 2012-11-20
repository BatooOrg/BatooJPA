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
import org.apache.commons.lang.mutable.MutableLong;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class TimeElement extends HashMap<String, TimeElement> implements Comparable<TimeElement> {

	private static final long serialVersionUID = 1L;
	private long time;
	private long self;
	private final String key;
	private int hits;
	private int selfHit;
	private long timeWithoutDb;

	/**
	 * @param key
	 *            the key
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public void addTime(long used, boolean self, boolean inDerby) {
		this.hits++;
		this.time += used;
		if (!inDerby) {
			this.timeWithoutDb += used;
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
	 * Dumps the summary of the test.
	 * 
	 * @param type
	 *            the benchmark type
	 * @param jpaTotalTime
	 *            the toal JPA time
	 * @param dbTotalTime
	 *            the total DB time
	 * @param fullSummary
	 *            if only number should be printed
	 * 
	 * @since 2.0.0
	 */
	public void dump0(Type type, MutableLong dbTotalTime, MutableLong jpaTotalTime, boolean fullSummary) {
		final int nameStart = this.key.indexOf("doBenchmark");

		if (nameStart > -1) {
			final long dbTime = this.timeWithoutDb / 1000000;
			final long jpaTime = (this.time - this.timeWithoutDb) / 1000000;

			dbTotalTime.add(dbTime);
			jpaTotalTime.add(jpaTime);

			if (fullSummary) {
				System.out.println(//
				String.format("%08d", dbTime) + //
					" \t" + String.format("%08d", jpaTime));
			}
			else {
				System.out.println(//
				this.key.substring(nameStart + 11) + " Test" + //
					" \t" + String.format("%08d", dbTime) + //
					" \t" + String.format("%08d", jpaTime));
			}
		}

		final List<TimeElement> children = Lists.newArrayList(this.values());
		Collections.sort(children);
		for (final TimeElement child : children) {
			child.dump0(type, dbTotalTime, jpaTotalTime, fullSummary);
		}
	}

	/**
	 * @param rowNo
	 *            the row no
	 * @param depth
	 *            the depth
	 * @return the row no
	 * 
	 * @since 2.0.0
	 */
	public int dump1(int rowNo, int depth) {
		if ((depth > 0) && (this.timeWithoutDb > 10000000)) {
			rowNo++;
			final String tabs = StringUtils.repeat(" ", depth);
			System.out.println(String.format("%010d", rowNo) + //
				" " + String.format("%010d", depth) + //
				" " + String.format("%010d", this.hits) + //
				" " + String.format("%010d", this.selfHit) + //
				" " + String.format("%010d", this.time / 1000000) + //
				" " + String.format("%010d", this.timeWithoutDb / 1000000) + //
				" " + String.format("%010d", this.self / 1000000) + //
				tabs + this.key);
		}

		final List<TimeElement> children = Lists.newArrayList(this.values());
		Collections.sort(children);
		for (final TimeElement child : children) {
			rowNo = child.dump1(rowNo, depth + 1);
		}

		return rowNo;
	}

	/**
	 * @param rowNo
	 *            the row no
	 * 
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public Long getSelf() {
		return this.self;
	}
}
