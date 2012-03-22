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
package org.batoo.jpa.core.impl.reflect;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.mapping.TypeFactory;
import org.batoo.jpa.core.util.Pair;
import org.reflections.util.ClasspathHelper;

import sun.misc.Unsafe;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author hceylan
 * 
 * @since $version
 */
@SuppressWarnings("restriction")
public class ReflectHelper {

	private static final ClassLoader CLASS_LOADER = ReflectHelper.class.getClassLoader();

	static final Unsafe unsafe;
	static {
		try {
			final Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			unsafe = (Unsafe) field.get(null);
		}
		catch (final Exception e) {
			throw new AssertionError(e);
		}
	}

	private static Map<Class<?>, ObjectConstructor<?>> CONSTRUCTORS = ReflectHelper.createConstructors();

	/**
	 * Detects and returns conflicting annotations.
	 * 
	 * @param javaType
	 *            the type to test for conflicts
	 * @param parsed
	 *            the set of annotations parsed
	 * @throws MappingException
	 *             thrown if check fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static void checkAnnotations(Class<?> javaType, Set<Class<? extends Annotation>> parsed) throws MappingException {
		final Set<Pair<Class<? extends Annotation>>> conflicts = Sets.newHashSet();

		final List<Annotation> annotations = Lists.newArrayList(javaType.getAnnotations());
		final String name = javaType.toString();

		ReflectHelper.checkAnnotations(parsed, conflicts, annotations, name);
	}

	/**
	 * Detects and returns conflicting annotations.
	 * 
	 * @param member
	 *            the member to test for conflicts
	 * @param parsed
	 *            the set of annotations parsed
	 * @throws MappingException
	 *             thrown if checks fails
	 * @since $version
	 * @author hceylan
	 */
	public static void checkAnnotations(Member member, Set<Class<? extends Annotation>> parsed) throws MappingException {
		final Set<Pair<Class<? extends Annotation>>> conflicts = Sets.newHashSet();

		final List<Annotation> annotations = Lists.newArrayList(ReflectHelper.getAnnotations(member));
		final String name = member.toString();

		ReflectHelper.checkAnnotations(parsed, conflicts, annotations, name);
	}

	private static void checkAnnotations(Set<Class<? extends Annotation>> parsed, final Set<Pair<Class<? extends Annotation>>> conflicts,
		final List<Annotation> annotationList, String name) throws MappingException {

		final List<Class<? extends Annotation>> annotations = Lists.transform(annotationList,
			new Function<Annotation, Class<? extends Annotation>>() {

				@SuppressWarnings("unchecked")
				@Override
				public Class<? extends Annotation> apply(Annotation input) {
					return (Class<? extends Annotation>) input.getClass().getInterfaces()[0];
				}
			});

		for (final Class<? extends Annotation> annotation : annotations) {
			final Set<Class<? extends Annotation>> forbidden = TypeFactory.conflictsFor(annotation);
			forbidden.retainAll(annotations);

			for (final Class<? extends Annotation> conflict : forbidden) {
				conflicts.add(new Pair<Class<? extends Annotation>>(annotation, conflict));
			}
		}

		if (conflicts.size() > 0) {
			throw new MappingException(name, conflicts);
		}

		for (final Iterator<Class<? extends Annotation>> i = annotations.iterator(); i.hasNext();) {
			if (parsed.contains(i.next())) {
				i.remove();
			}
		}

		if (annotations.size() > 0) {
			throw new MappingException(name, annotationList);
		}
	}

	/**
	 * Perform resolution of a class name.
	 * <p/>
	 * Same as {@link #classForName(String, Class)} except that here we delegate to {@link Class#forName(String)} if the context classloader
	 * lookup is unsuccessful.
	 * 
	 * @param name
	 *            The class name
	 * @return The class reference.
	 * @throws ClassNotFoundException
	 *             From {@link Class#forName(String)}.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static Class<?> classForName(String name) throws ClassNotFoundException {
		try {
			final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			if (contextClassLoader != null) {
				return contextClassLoader.loadClass(name);
			}
		}
		catch (final Throwable ignore) {
			// noop
		}

		return Class.forName(name);
	}

	/**
	 * Converts the number into number Type
	 * 
	 * @param value
	 * @param numberType
	 * @return
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

	static Map<Class<?>, ObjectConstructor<?>> createConstructors() {
		final Map<Class<?>, ObjectConstructor<?>> constructors = Maps.newHashMap();

		constructors.put(Byte.class, ByteObjectConstructor.INSTANCE);
		constructors.put(Short.class, ShortObjectConstructor.INSTANCE);
		constructors.put(Integer.class, IntegerObjectConstructor.INSTANCE);
		constructors.put(Long.class, LongObjectConstructor.INSTANCE);

		return constructors;
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
	 * Loads the class
	 * 
	 * @param name
	 *            the fully qualified name of the class
	 * @return the loaded class
	 * @throws ClassNotFoundException
	 *             if the class cannot be loaded
	 * 
	 * @since $version
	 * @author hceylan
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <X> Class<X> forName(String name) throws ClassNotFoundException {
		try {
			return (Class<X>) Thread.currentThread().getContextClassLoader().loadClass(name);
		}
		catch (final ClassNotFoundException e) {}

		return (Class<X>) ReflectHelper.class.getClassLoader().loadClass(name);
	}

	/**
	 * Returns the transformed value
	 * 
	 * @param clazz
	 *            transformed type
	 * @return the value as clazz
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> clazz, Object value) {
		return (T) CONSTRUCTORS.get(clazz).from(value);
	}

	/**
	 * Returns the annotation instance if the member has the annotation.
	 * 
	 * @param member
	 *            the member
	 * @param annotation
	 *            the annotation instance to return
	 * @return the annotation instance if the member has the annotation or null
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
	 * Returns the array of annotations the member has.
	 * 
	 * @param member
	 *            the member
	 * @return the array of annotations the member has
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private static Annotation[] getAnnotations(Member member) {
		if (member instanceof Field) {
			return ((Field) member).getAnnotations();
		}

		if (member instanceof Method) {
			return ((Method) member).getAnnotations();
		}

		throw new IllegalArgumentException("Member is neither field nor method: " + member);
	}

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

			TypeFactory.LOG.warn("Skipping classpath url {0}", item);
			i.remove();
		}

		return urls;
	}

	/**
	 * Returns the actual generic type of a class's type parameter
	 * 
	 * @param member
	 *            the member
	 * @param i
	 *            the order number of the generic parameter
	 * @return the class of generic type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static Class<?> getGenericType(Member member, int i) {
		if (member instanceof Field) {
			final Field field = (Field) member;
			final ParameterizedType type = (ParameterizedType) field.getGenericType();
			return (Class<?>) type.getActualTypeArguments()[i];
		}

		return null;
	}

	/**
	 * @param member
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private static Class<?> getMemberType(Member member) {
		if (member instanceof Field) {
			return ((Field) member).getType();
		}

		return ((Method) member).getReturnType();
	}

	/**
	 * Returns the plural attribute type for the field
	 * 
	 * @param field
	 *            the field
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static PersistentAttributeType getPluralType(Field field) {
		if (field.isAnnotationPresent(OneToMany.class)) {
			return PersistentAttributeType.ONE_TO_MANY;
		}

		if (field.isAnnotationPresent(ManyToMany.class)) {
			return PersistentAttributeType.MANY_TO_MANY;
		}

		if (field.isAnnotationPresent(ManyToOne.class)) {
			return PersistentAttributeType.MANY_TO_ONE;
		}

		return PersistentAttributeType.ONE_TO_ONE;
	}

	public static <T> T getProxyInstance(Class<T> _interface, InvocationHandler h) {
		return ProxyCache.INSTANCE.getProxyInstance(CLASS_LOADER, _interface, h);
	}

	/**
	 * Makes the member accessible
	 * 
	 * @param member
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static void makeAccessible(final Member member) {
		AccessController.doPrivileged(new PrivilegedAction<Void>() {

			@Override
			public Void run() {
				if (member instanceof Field) {
					((Field) member).setAccessible(true);
				}

				else if (member instanceof Method) {
					((Method) member).setAccessible(true);
				}

				else {
					((Constructor<?>) member).setAccessible(true);
				}

				return null;
			}
		});
	}

	public static void validateColumnParameters(Member member, int precision, int scale, int length) throws MappingException {
		if (scale < 0) {
			throw new MappingException("Scale must be a a positive value: " + ReflectHelper.createMemberName(member));
		}
		else if (precision < 0) {
			throw new MappingException("Precision must be a a positive value: " + ReflectHelper.createMemberName(member));
		}
		else if (length < 0) {
			throw new MappingException("Length must be a a positive value: " + ReflectHelper.createMemberName(member));
		}

		if (scale > 0) {
			final Class<?> clazz = ReflectHelper.getMemberType(member);

			if (BigDecimal.class.isAssignableFrom(clazz) || BigInteger.class.isAssignableFrom(clazz)) {
				if (precision <= 0) {
					throw new MappingException("Scale must be a a positive value when scale is set: "
						+ ReflectHelper.createMemberName(member));
				}
			}
		}
	}

}
