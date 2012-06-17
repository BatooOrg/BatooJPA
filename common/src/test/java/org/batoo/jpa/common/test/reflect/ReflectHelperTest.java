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
package org.batoo.jpa.common.test.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.reflect.ReflectHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;

/**
 * 
 * Tests for {@link org.batoo.jpa.common.reflect.ReflectHelper}.
 * 
 * @author hceylan
 * @since $version
 */
public class ReflectHelperTest {

	@SuppressWarnings({ "rawtypes", "javadoc" })
	public static class Clazz {

		@Version
		@Temporal(TemporalType.TIMESTAMP)
		public Integer field;

		@Id
		@AttributeOverride(name = "field", column = @Column())
		public Integer field2;

		public List<String> field3;
		public List field4;
		public Map<String, Integer> field5;
		public Map field6;

		public String getField() {
			return this.field.toString();
		}

		public List<String> getField3() {
			return this.field3;
		}

		public List getField4() {
			return this.field4;
		}
	}

	@Mock
	private BLogger logger;

	private Field field;
	private Field field2;
	private Field field3;
	private Field field4;
	private Field field5;
	private Field field6;

	private Method method;
	private Method method3;
	private Method method4;

	/**
	 * Initializes the test.
	 * 
	 * @throws Exception
	 *             thrown in case of an error in the test
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Before
	public void init() throws Exception {
		this.field = Clazz.class.getField("field");
		this.field2 = Clazz.class.getField("field2");
		this.field3 = Clazz.class.getField("field3");
		this.field4 = Clazz.class.getField("field4");
		this.field5 = Clazz.class.getField("field5");
		this.field6 = Clazz.class.getField("field6");

		this.method = Clazz.class.getMethod("getField");
		this.method3 = Clazz.class.getMethod("getField3");
		this.method4 = Clazz.class.getMethod("getField4");
	}

	/**
	 * Initializes the mocks.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Tests {@link ReflectHelper#createMemberName(java.lang.reflect.Member)} with a field.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testCreateMemberNameWithField() {
		Assert.assertEquals("org.batoo.jpa.common.test.reflect.ReflectHelperTest$Clazz.field", ReflectHelper.createMemberName(this.field));
	}

	/**
	 * Tests {@link ReflectHelper#createMemberName(java.lang.reflect.Member)} with a method.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testCreateMemberNameWithMethod() {
		Assert.assertEquals("org.batoo.jpa.common.test.reflect.ReflectHelperTest$Clazz.getField", ReflectHelper.createMemberName(this.method));
	}

	/**
	 * 
	 * Tests {@link ReflectHelper#getAnnotation(java.lang.reflect.Member, Class)} with an existing annotation.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testGetAnnotationWithExisting() {
		Assert.assertNotNull(ReflectHelper.getAnnotation(this.field, Version.class));
	}

	/**
	 * 
	 * Tests {@link ReflectHelper#getAnnotation(java.lang.reflect.Member, Class)} without an existing annotation.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testGetAnnotationWithoutExisting() {
		Assert.assertNull(ReflectHelper.getAnnotation(this.field2, Version.class));
	}

	/**
	 * Tests {@link ReflectHelper#getGenericType(java.lang.reflect.Member, int)}.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testGetGenericType() {
		// test fields
		Assert.assertEquals(String.class, ReflectHelper.getGenericType(this.field3, 0));
		Assert.assertNull(ReflectHelper.getGenericType(this.field4, 0));
		Assert.assertEquals(String.class, ReflectHelper.getGenericType(this.field5, 0));
		Assert.assertEquals(Integer.class, ReflectHelper.getGenericType(this.field5, 1));
		Assert.assertNull(ReflectHelper.getGenericType(this.field5, 2));
		Assert.assertNull(ReflectHelper.getGenericType(this.field6, 0));
		Assert.assertNull(ReflectHelper.getGenericType(this.field6, 1));

		// test methods
		Assert.assertEquals(String.class, ReflectHelper.getGenericType(this.method3, 0));
		Assert.assertNull(ReflectHelper.getGenericType(this.method4, 0));
	}

	/**
	 * 
	 * Tests {@link ReflectHelper#getMemberType(java.lang.reflect.Member)} with a field.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testGetMemberTypeWithField() {
		Assert.assertEquals(Integer.class, ReflectHelper.getMemberType(this.field));
	}

	/**
	 * 
	 * Tests {@link ReflectHelper#getMemberType(java.lang.reflect.Member)} with a method.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testGetMemberTypeWithMethod() {
		Assert.assertEquals(String.class, ReflectHelper.getMemberType(this.method));
	}

	/**
	 * Tests {@link ReflectHelper#isCollection(Class)}.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testIsCollection() {
		Assert.assertTrue(ReflectHelper.isCollection(List.class));
		Assert.assertTrue(ReflectHelper.isCollection(Set.class));
		Assert.assertTrue(ReflectHelper.isCollection(Map.class));
		Assert.assertTrue(ReflectHelper.isCollection(Collection.class));

		Assert.assertFalse(ReflectHelper.isCollection(ArrayList.class));
		Assert.assertFalse(ReflectHelper.isCollection(HashSet.class));
		Assert.assertFalse(ReflectHelper.isCollection(HashMap.class));
	}

	/**
	 * Tests {@link ReflectHelper#warnAnnotations(BLogger, java.lang.reflect.Member, java.util.Set)} with the <code>field2</code> that
	 * should not trigger a warning.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testWarnAnnotationsShouldNotWarn() {
		ReflectHelper.warnAnnotations(this.logger, this.field, Sets.newHashSet(Version.class, Temporal.class, Basic.class));

		Mockito.verify(this.logger, Mockito.times(0)).warn(Matchers.anyString());
	}

	/**
	 * Tests {@link ReflectHelper#warnAnnotations(BLogger, java.lang.reflect.Member, java.util.Set)} with the <code>field2</code> that
	 * should trigger a warning.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testWarnAnnotationsShouldWarn() {
		ReflectHelper.warnAnnotations(this.logger, this.field2, Sets.newHashSet(Version.class, Temporal.class, Basic.class));

		Mockito.verify(this.logger, Mockito.times(1)).warn(Matchers.anyString(), Matchers.any(), Matchers.any());
	}
}
