package org.batoo.jpa.core.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

public class HashMapBug {

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
