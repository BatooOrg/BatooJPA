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
package org.batoo.jpa.benchmark.insert;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
@SuppressWarnings("javadoc")
public class HowAboutThat {
	private static final int COUNT = 10000;

	public static void main(String[] args) {
		final long time = System.currentTimeMillis();
		for (int i = 0; i < (250000 * 50); i++) {
			new ArrayList<HowAboutThat>();
		}

		System.out.println(System.currentTimeMillis() - time);
	}

	public static void main1(String[] args) {

		final ArrayList<Integer> list = Lists.newArrayList();
		for (int i = 0; i < HowAboutThat.COUNT; i++) {
			list.add(i);
		}

		long time = System.currentTimeMillis();

		for (int i = 0; i < HowAboutThat.COUNT; i++) {
			HowAboutThat.test1(list);
		}
		System.out.println(System.currentTimeMillis() - time);
		time = System.currentTimeMillis();

		for (int i = 0; i < HowAboutThat.COUNT; i++) {
			HowAboutThat.test2(list);
		}
		System.out.println(System.currentTimeMillis() - time);
		time = System.currentTimeMillis();

		for (int i = 0; i < HowAboutThat.COUNT; i++) {
			HowAboutThat.test3(list);
		}
		System.out.println(System.currentTimeMillis() - time);
		time = System.currentTimeMillis();

		for (int i = 0; i < HowAboutThat.COUNT; i++) {
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
