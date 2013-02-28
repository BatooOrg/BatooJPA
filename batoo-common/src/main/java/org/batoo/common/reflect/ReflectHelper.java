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
package org.batoo.common.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.batoo.common.BatooException;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
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

	private static final BLogger LOG = BLoggerFactory.getLogger(ReflectHelper.class);

	private static final String GET_PREFIX = "get";
	private static final String IS_PREFIX = "is";

	static final sun.misc.Unsafe unsafe;

	static {
		unsafe = ReflectHelper.getUnSafe();
	}

	private static Class<?> checkAndReturn(Class<?> originalType, Method m, Class<?> actualType) {
		if (!originalType.isAssignableFrom(actualType)) {
			ReflectHelper.LOG.warn("Method {0} must return type of {1}", m, originalType.getName());
		}

		return actualType;
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
	 */
	public static Number convertNumber(Number value, Class<?> numberType) {
		if (value == null) {
			return null;
		}

		if (numberType.isAssignableFrom(value.getClass())) {
			return value;
		}

		if ((numberType == Integer.class) || (numberType == Integer.TYPE)) {
			return value.intValue();
		}

		if ((numberType == Long.class) || (numberType == Long.TYPE)) {
			return value.longValue();
		}

		if ((numberType == Short.class) || (numberType == Short.TYPE)) {
			return value.shortValue();
		}

		if ((numberType == Byte.class) || (numberType == Byte.TYPE)) {
			return value.byteValue();
		}

		if ((numberType == Float.class) || (numberType == Float.TYPE)) {
			return value.floatValue();
		}

		if ((numberType == Double.class) || (numberType == Double.TYPE)) {
			return value.doubleValue();
		}

		if (numberType == BigDecimal.class) {
			return BigDecimal.valueOf(value.doubleValue());
		}

		if (numberType == BigInteger.class) {
			return BigInteger.valueOf(value.longValue());
		}

		throw new IllegalArgumentException(numberType + " not supported");
	}

	/**
	 * Creates and returns a fast constructor accessor.
	 * 
	 * @param constructor
	 *            the original constructor
	 * @return the constructor accessor
	 * 
	 * @since $version
	 */
	public static ConstructorAccessor createConstructor(Constructor<?> constructor) {
		try {
			Class.forName("sun.reflect.ConstructorAccessor");
		}
		catch (final ClassNotFoundException e) {
			return new SimpleConstructorAccessor(constructor);
		}

		return ReflectHelper.createConstructorImpl(constructor);
	}

	private static ConstructorAccessor createConstructorImpl(Constructor<?> constructor) {
		try {
			final Class<?> magClass = Class.forName("sun.reflect.MethodAccessorGenerator");
			final Constructor<?> c = magClass.getDeclaredConstructors()[0];
			final Method generateMethod = magClass.getMethod("generateConstructor", Class.class, Class[].class, Class[].class, Integer.TYPE);

			ReflectHelper.setAccessible(c, true);
			ReflectHelper.setAccessible(generateMethod, true);

			try {
				final Object mag = c.newInstance();

				return new SunConstructorAccessor(generateMethod.invoke(mag, constructor.getDeclaringClass(), constructor.getParameterTypes(),
					constructor.getExceptionTypes(), constructor.getModifiers()));
			}
			finally {
				ReflectHelper.setAccessible(c, false);
				ReflectHelper.setAccessible(generateMethod, false);
			}
		}
		catch (final Exception e) {
			throw new RuntimeException("Constructor generation failed", e);
		}
	}

	/**
	 * Returns the qualified name for the member.
	 * 
	 * @param member
	 *            the member to qualify
	 * @return the qualified name for the member
	 * 
	 * @since $version
	 */
	public static String createMemberName(Member member) {
		return member.getDeclaringClass().getName() + "." + member.getName();
	}

	/**
	 * Returns the accessor for the member
	 * 
	 * @param javaMember
	 *            the java member
	 * @return the accessor
	 * 
	 * @since $version
	 */
	public static AbstractAccessor getAccessor(Member javaMember) {
		if (javaMember instanceof Field) {
			return ReflectHelper.unsafe != null ? new UnsafeFieldAccessor((Field) javaMember) : new FieldAccessor((Field) javaMember);
		}
		else {
			String name = javaMember.getName().startsWith(ReflectHelper.IS_PREFIX) ? javaMember.getName().substring(2) : javaMember.getName().substring(3);

			name = StringUtils.uncapitalize(name);

			final Class<?> declaringClass = javaMember.getDeclaringClass();
			final PropertyDescriptor[] properties = ReflectHelper.getProperties(declaringClass);
			for (final PropertyDescriptor descriptor : properties) {
				if (descriptor.getName().equals(name)) {
					return new PropertyAccessor(descriptor);
				}
			}

			Field field;
			try {
				field = javaMember.getDeclaringClass().getDeclaredField(StringUtils.uncapitalize(name));
				return new FieldAccessor(field);
			}
			catch (final Exception e) {
				throw new BatooException("Cannot find instance variable field " + javaMember.getDeclaringClass().getName() + "." + name);
			}
		}
	}

	/**
	 * Returns the actual type of the attribute, resolving generic types if necessary.
	 * 
	 * @param clazz
	 *            the actual class
	 * @param attributeName
	 *            the name of the attribute
	 * @param originalType
	 *            th original type of the attribute
	 * @return the actual type of the attribute
	 * @param <T>
	 *            the xpected type
	 * 
	 * @since $version
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getActualType(Class<?> clazz, String attributeName, Class<?> originalType) {
		if (originalType.isPrimitive()) {
			return (Class<T>) originalType;
		}

		final String methodName = "get" + StringUtils.capitalize(attributeName);

		final Class<T> actualType = (Class<T>) ReflectHelper.getTypeImpl(clazz, originalType, methodName, Lists.<Type> newArrayList());

		return actualType;
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
	 */
	public static <A extends Annotation> A getAnnotation(final Member member, final Class<A> annotation) {
		if (member instanceof Field) {
			return ((Field) member).getAnnotation(annotation);
		}

		if (member instanceof Method) {
			return ((Method) member).getAnnotation(annotation);
		}

		throw new IllegalArgumentException("Member is neither field nor method: " + member);
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
	 * @param <X>
	 *            the type of the class
	 * 
	 * @since $version
	 */
	@SuppressWarnings("unchecked")
	public static <X> Class<X> getGenericType(Member member, int index) {
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

		final Type[] types = parameterizedType.getActualTypeArguments();

		return (Class<X>) ((types != null) && (index < types.length) ? types[index] : null);
	}

	/**
	 * Returns the type class of the <code>member</code>.
	 * 
	 * @param member
	 *            the member
	 * @return the <code>member</code>'s type as java class
	 * 
	 * @since $version
	 */
	public static Class<?> getMemberType(Member member) {
		if (member instanceof Field) {
			return ((Field) member).getType();
		}

		return ((Method) member).getReturnType();
	}

	/**
	 * Returns the property descriptors for the class.
	 * 
	 * @param clazz
	 *            the class
	 * @return the property descriptors
	 * 
	 * @since $version
	 */
	public static PropertyDescriptor[] getProperties(Class<?> clazz) {
		final List<PropertyDescriptor> properties = Lists.newArrayList();

		final Method[] methodList = clazz.getDeclaredMethods();

		// check each method.
		for (final Method method : methodList) {
			if (method == null) {
				continue;
			}

			// skip static and private methods.
			final int mods = method.getModifiers();
			if (Modifier.isStatic(mods) || !Modifier.isPublic(mods) || method.isBridge() || method.isSynthetic()) {
				continue;
			}

			final String name = method.getName();

			if (method.getParameterTypes().length == 0) {
				if (name.startsWith(ReflectHelper.GET_PREFIX)) {
					properties.add(new PropertyDescriptor(clazz, name.substring(3), method));
				}
				else if ((method.getReturnType() == boolean.class) && name.startsWith(ReflectHelper.IS_PREFIX)) {
					properties.add(new PropertyDescriptor(clazz, name.substring(2), method));
				}
			}
		}

		return properties.toArray(new PropertyDescriptor[properties.size()]);
	}

	private static Class<?> getTypeImpl(Class<?> clazz, Class<?> originalType, String methodName, List<Type> arguments) {
		final Map<TypeVariable<?>, Type> typeMap = Maps.newHashMap();

		if (arguments.size() > 0) {
			final TypeVariable<?>[] typeParameters = clazz.getTypeParameters();
			for (int i = 0; i < typeParameters.length; i++) {
				typeMap.put(typeParameters[i], arguments.get(i));
			}
		}

		try {
			final Method m = clazz.getDeclaredMethod(methodName);

			final Type type = typeMap.get(m.getGenericReturnType());
			if (type != null) {
				return ReflectHelper.checkAndReturn(originalType, m, (Class<?>) type);
			}

			return ReflectHelper.checkAndReturn(originalType, m, m.getReturnType());
		}
		catch (final NoSuchMethodException e) {
			final List<Type> typeArguments = Lists.newArrayList();

			if (clazz.getGenericSuperclass() instanceof ParameterizedType) {
				final ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();

				for (final Type typeArgument : parameterizedType.getActualTypeArguments()) {
					if (typeArgument instanceof TypeVariable) {
						typeArguments.add(typeMap.get(typeArgument));
					}
					else {
						typeArguments.add(typeArgument);
					}
				}
			}

			final Class<?> superclass = clazz.getSuperclass();

			if (superclass == Object.class) {
				return originalType;
			}

			return ReflectHelper.getTypeImpl(superclass, originalType, methodName, typeArguments);
		}
	}

	private static sun.misc.Unsafe getUnSafe() {
		try {
			ReflectHelper.LOG.debug("Loading direct access library....");

			final Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			try {
				return (sun.misc.Unsafe) field.get(null);
			}
			finally {
				ReflectHelper.LOG.debug("Direct access library loaded successfully");
			}
		}
		catch (final NoClassDefFoundError e) {}
		catch (final Exception e) {}

		ReflectHelper.LOG.warn("Direct access library cannot be loaded! Direct access library only adds some performance, but not necessary to operate. It is safe to ignore...");

		return null;
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
	 */
	public static boolean isCollection(Class<?> type) {
		return (List.class == type) || (Collection.class == type) || (Set.class == type) || (Map.class == type);
	}

	/**
	 * Sets the member's accessibility status.
	 * 
	 * @param member
	 *            the member of which to set accessibility status
	 * @param accessible
	 *            true to set accessible, false to make it not accessible
	 * 
	 * @since $version
	 */
	public static void setAccessible(final Member member, final boolean accessible) {
		AccessController.doPrivileged(new PrivilegedAction<Void>() {

			@Override
			public Void run() {
				if (member instanceof Field) {
					((Field) member).setAccessible(accessible);
				}

				else if (member instanceof Method) {
					((Method) member).setAccessible(accessible);
				}

				else {
					((Constructor<?>) member).setAccessible(accessible);
				}

				return null;
			}
		});
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
	 */
	public static void warnAnnotations(BLogger logger, Member member, final Set<Class<? extends Annotation>> annotations) {
		final Set<Annotation> existing;

		if (member instanceof Field) {
			existing = Sets.newHashSet(((Field) member).getAnnotations());
		}
		else {
			existing = Sets.newHashSet(((Method) member).getAnnotations());
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
			logger.warn("Following annotations on {0} were ignored: {1}", ReflectHelper.createMemberName(member), Joiner.on(", ").join(filtered));
		}
	}
}
