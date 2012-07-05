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
package org.batoo.jpa.benchmark.insert;

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
		this.time += used / 1000;
		if (!inDerby) {
			this.timeWithoutDerby += used / 1000;
		}
		if (self) {
			this.selfHit++;
			this.self += used / 1000;
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
		if ((depth > 0) && (this.timeWithoutDerby > 10000)) {
			rowNo++;
			final String tabs = StringUtils.repeat(" ", depth);
			System.out.println(String.format("%010d", rowNo) + //
				" " + String.format("%010d", depth) + //
				" " + String.format("%010d", this.hits) + //
				" " + String.format("%010d", this.selfHit) + //
				" " + String.format("%010d", this.time / 1000) + //
				" " + String.format("%010d", this.timeWithoutDerby / 1000) + //
				" " + String.format("%010d", this.self / 1000) + //
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
