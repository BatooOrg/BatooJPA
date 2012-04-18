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

	public TimeElement(String key) {
		this.key = key;
	}

	public void addTime(long used, boolean self, boolean inDerby) {
		this.hits++;
		this.time += used / 1000000;
		if (!inDerby) {
			this.timeWithoutDerby += used / 1000000;
		}
		if (self) {
			this.selfHit++;
			this.self += used / 1000000;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int compareTo(TimeElement o) {
		return this.time > o.time ? -1 : o.time > this.time ? 1 : 0;
	}

	public void dump(int depth) {
		if ((depth == 0) || (this.time > 10)) {
			if (depth > 0) {
				final String tabs = StringUtils.repeat(" ", depth);
				System.out.println(String.format("%04d", this.hits) + //
					" " + String.format("%04d", this.selfHit) + //
					" " + String.format("%08d", this.time) + //
					" " + String.format("%08d", this.timeWithoutDerby) + //
					" " + String.format("%05d", this.self / this.hits) + //
					" " + String.format("%08d", this.self) + tabs + this.key);
			}

			final List<TimeElement> children = Lists.newArrayList(this.values());
			Collections.sort(children);
			for (final TimeElement child : children) {
				child.dump(depth + 1);
			}
		}
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
}
