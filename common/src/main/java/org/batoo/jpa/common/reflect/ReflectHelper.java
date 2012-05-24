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
package org.batoo.jpa.common.reflect;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.reflections.util.ClasspathHelper;

import sun.misc.Unsafe;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

/**
 * @author hceylan
 * 
 * @since $version
 */
@SuppressWarnings("restriction")
public class ReflectHelper {

	private static final BLogger LOG = BLoggerFactory.getLogger(ReflectHelper.class);

	static final Unsafe unsafe;

	static {
		try {
			ReflectHelper.LOG.debug("Loading direct access library....");

			final Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			unsafe = (Unsafe) field.get(null);

			ReflectHelper.LOG.debug("Direct access library loaded successfully");
		}
		catch (final Exception e) {
			throw new AssertionError("Direct access library cannot be loaded", e);
		}
	}

	/**
	 * Converts the number into number Type
	 * 
	 * @param value
	 *            the number value
	 * @param numberType
	 *            the number type
	 * @return the converted number value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static Number convertNumber(Number value, Class<?> numberType) {
		if (numberType == Integer.class) {
			return value.intValue();
		}

		if (numberType == Long.class) {
			return value.longValue();
		}

		if (numberType == Short.class) {
			return value.shortValue();
		}

		if (numberType == Byte.class) {
			return value.byteValue();
		}

		if (numberType == Float.class) {
			return value.floatValue();
		}

		if (numberType == Double.class) {
			return value.doubleValue();
		}

		throw new IllegalArgumentException(numberType + " not supported");
	}

	/**
	 * Returns the qualified name for the member.
	 * 
	 * @param member
	 *            the member to qualify
	 * @return the qualified name for the member
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static String createMemberName(Member member) {
		return member.getDeclaringClass().getName() + "." + member.getName();
	}

	/**
	 * Returns the annotation instance if the <code>member</code> has the <code>annotation</code>.
	 * 
	 * @param member
	 *            the member
	 * @param annotation
	 *            the type of the annotation instance to return
	 * @return the <code>annotation</code> instance if the <code>member</code> has the annotation or null
	 * 
	 * @param <A>
	 *            the class of annotation
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static <A extends Annotation> A getAnnotation(Member member, Class<A> annotation) {
		if (member instanceof Field) {
			return ((Field) member).getAnnotation(annotation);
		}

		if (member instanceof Method) {
			return ((Method) member).getAnnotation(annotation);
		}

		throw new IllegalArgumentException("Member is neither field nor method: " + member);
	}

	/**
	 * Returns the class path URLSs.
	 * 
	 * @return the class path URLSs
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static Set<URL> getClasspathUrls() {
		final Set<URL> urls = ClasspathHelper.forClassLoader(ClasspathHelper.defaultClassLoaders);
		for (final Iterator<URL> i = urls.iterator(); i.hasNext();) {
			final URL item = i.next();
			try {
				final File file = new File(item.toURI());
				if (file.isDirectory() || file.getAbsolutePath().endsWith(".jar")) {
					continue;
				}
			}
			catch (final URISyntaxException e) {}

			ReflectHelper.LOG.warn("Skipping classpath url {0}", item);
			i.remove();
		}

		return urls;
	}

	/**
	 * Returns the actual generic type of a class's type parameter of the <code>member</code>.
	 * <p>
	 * if the <code>member</code> is a field then field's generic types are checked. Otherwise the <code>member</code> is treated a a method
	 * and its return type's is checked.
	 * <p>
	 * 
	 * @param member
	 *            the member
	 * @param index
	 *            the index number of the generic parameter
	 * @return the class of generic type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static Class<?> getGenericType(Member member, int index) {
		Type type;

		if (member instanceof Field) {
			final Field field = (Field) member;
			type = field.getGenericType();

		}
		else {
			final Method method = (Method) member;
			type = method.getGenericReturnType();
		}

		// if not a parameterized type return null
		if (!(type instanceof ParameterizedType)) {
			return null;
		}

		final ParameterizedType parameterizedType = (ParameterizedType) type;

		final Type[] types = parameterizedType != null ? parameterizedType.getActualTypeArguments() : null;

		return (Class<?>) ((types != null) && (index < types.length) ? types[index] : null);
	}

	/**
	 * Returns the type class of the <code>member</code>.
	 * 
	 * @param member
	 *            the member
	 * @return the <code>member</code>'s type as java class
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static Class<?> getMemberType(Member member) {
		if (member instanceof Field) {
			return ((Field) member).getType();
		}

		return ((Method) member).getReturnType();
	}

	/**
	 * Returns if the <code>type</code> is a collection type.
	 * <p>
	 * Note that this method uses equality, not assignability to test.
	 * 
	 * @param type
	 *            the type to check if it is collection
	 * @return true if the <code>type</code> is a collection type, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static boolean isCollection(Class<?> type) {
		return (List.class == type) || (Collection.class == type) || (Set.class == type) || (Map.class == type);
	}

	/**
	 * Logs warnings for annotations that were ignored.
	 * 
	 * @param logger
	 *            the logger to log the warnings.
	 * @param member
	 *            the member
	 * @param annotations
	 *            the set of annotations allowed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static void warnAnnotations(BLogger logger, Member member, final Set<Class<? extends Annotation>> annotations) {
		final Set<Annotation> existing;

		if (member instanceof Field) {
			existing = Sets.newHashSet(((Field) member).getAnnotations());
		}
		else {
			existing = Sets.newHashSet(((Field) member).getAnnotations());
		}

		final Set<Annotation> filtered = Sets.filter(existing, new Predicate<Annotation>() {

			@Override
			public boolean apply(Annotation input) {
				for (final Annotation annotation : existing) {
					if (annotations.contains(annotation.annotationType())) {
						return false;
					}
				}

				return true;
			}
		});

		if (filtered.size() > 0) {
			logger.warn("Following annotations on {0} were ignored: {1}", ReflectHelper.createMemberName(member),
				Joiner.on(", ").join(filtered));
		}
	}

}
