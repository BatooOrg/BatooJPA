/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

package org.batoo.jpa.community.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class HashMapBug {

	/**
	 * Test to demonstrate the hash map bug
	 * 
	 * @since 2.0.0
	 */
	@Test
	@Ignore
	public void hashMapBug() {
		final Map<String, Integer> map1 = new HashMap<String, Integer>();
		final Map<String, Integer> map2 = new HashMap<String, Integer>();

		map1.put("1", 1);
		map1.put("2", 2);

		map2.put("1", 1);
		map2.put("2", 2);

		Assert.assertEquals(map1, map2);
		Assert.assertEquals(map1.keySet(), map2.keySet());
		Assert.assertEquals(map1.entrySet(), map2.entrySet());
		Assert.assertEquals(map1.values(), map2.values());
	}

}
