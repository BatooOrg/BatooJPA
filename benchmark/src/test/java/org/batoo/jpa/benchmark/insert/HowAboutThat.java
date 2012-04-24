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

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class HowAboutThat {
	private static final int COUNT = 10000;

	public static void main(String[] args) {

		final ArrayList<Integer> list = Lists.newArrayList();
		for (int i = 0; i < COUNT; i++) {
			list.add(i);
		}

		long time = System.currentTimeMillis();

		for (int i = 0; i < COUNT; i++) {
			HowAboutThat.test1(list);
		}
		System.out.println(System.currentTimeMillis() - time);
		time = System.currentTimeMillis();

		for (int i = 0; i < COUNT; i++) {
			HowAboutThat.test2(list);
		}
		System.out.println(System.currentTimeMillis() - time);
		time = System.currentTimeMillis();

		for (int i = 0; i < COUNT; i++) {
			HowAboutThat.test3(list);
		}
		System.out.println(System.currentTimeMillis() - time);
		time = System.currentTimeMillis();

		for (int i = 0; i < COUNT; i++) {
			HowAboutThat.test4(list);
		}
		System.out.println(System.currentTimeMillis() - time);
	}

	/**
	 * @param list
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private static void test1(ArrayList<Integer> list) {
		for (int i = 0; i < list.size(); i++) {
			Integer integer = list.get(i);
			integer++;
		}
	}

	/**
	 * @param list
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private static void test2(List<Integer> list) {
		for (int i = 0; i < list.size(); i++) {
			Integer integer = list.get(i);
			integer++;
		}
	}

	/**
	 * @param list
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private static void test3(ArrayList<Integer> list) {
		for (final Integer i : list) {
			Integer integer = i;
			integer++;
		}
	}

	/**
	 * @param list
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private static void test4(List<Integer> list) {
		for (final Integer i : list) {
			Integer integer = i;
			integer++;
		}
	}
}
