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
package org.batoo.common.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Miscellaneous object utility methods.
 * 
 * <p>
 * Mainly for internal use within the framework; consider <a href="http://jakarta.apache.org/commons/lang/">Jakarta's Commons Lang</a> for a
 * more comprehensive suite of object utilities.
 * 
 * <p>
 * The original code is based on org.springframework.util.ObjectUtils
 * 
 * @author hceylan
 * @since $version
 * @see org.apache.commons.lang.ObjectUtils
 */
public abstract class ObjectUtils {

	private static final int INITIAL_HASH = 7;
	private static final int MULTIPLIER = 31;

	private static final String EMPTY_STRING = "";
	private static final String NULL_STRING = "null";
	private static final String ARRAY_START = "{";
	private static final String ARRAY_END = "}";
	private static final String EMPTY_ARRAY = ObjectUtils.ARRAY_START + ObjectUtils.ARRAY_END;
	private static final String ARRAY_ELEMENT_SEPARATOR = ", ";

	/**
	 * Append the given object to the given array, returning a new array consisting of the input array contents plus the given object.
	 * 
	 * @param array
	 *            the array to append to (can be <code>null</code>)
	 * @param obj
	 *            the object to append
	 * @return the new array (of the same component type; never <code>null</code>)
	 * @param <A>
	 *            the type of the array
	 * @param <O>
	 *            the type of the object
	 */
	public static <A, O extends A> A[] addObjectToArray(A[] array, O obj) {
		Class<?> compType = Object.class;
		if (array != null) {
			compType = array.getClass().getComponentType();
		}
		else if (obj != null) {
			compType = obj.getClass();
		}
		final int newArrLength = (array != null ? array.length + 1 : 1);
		@SuppressWarnings("unchecked")
		final A[] newArr = (A[]) Array.newInstance(compType, newArrLength);
		if (array != null) {
			System.arraycopy(array, 0, newArr, 0, array.length);
		}
		newArr[newArr.length - 1] = obj;
		return newArr;
	}

	/**
	 * Case insensitive alternative to {@link Enum#valueOf(Class, String)}.
	 * 
	 * @param <E>
	 *            the concrete Enum type
	 * @param enumValues
	 *            the array of all Enum constants in question, usually per Enum.values()
	 * @param constant
	 *            the constant to get the enum value of
	 * @return the type of the array
	 * @throws IllegalArgumentException
	 *             if the given constant is not found in the given array of enum values. Use {@link #containsConstant(Enum[], String)} as a
	 *             guard to avoid this exception.
	 */
	public static <E extends Enum<?>> E caseInsensitiveValueOf(E[] enumValues, String constant) {
		for (final E candidate : enumValues) {
			if (candidate.toString().equalsIgnoreCase(constant)) {
				return candidate;
			}
		}
		throw new IllegalArgumentException(String.format("constant [%s] does not exist in enum type %s", constant,
			enumValues.getClass().getComponentType().getName()));
	}

	/**
	 * Check whether the given array of enum constants contains a constant with the given name, ignoring case when determining a match.
	 * 
	 * @param enumValues
	 *            the enum values to check, typically the product of a call to MyEnum.values()
	 * @param constant
	 *            the constant name to find (must not be null or empty string)
	 * @return whether the constant has been found in the given array
	 */
	public static boolean containsConstant(Enum<?>[] enumValues, String constant) {
		return ObjectUtils.containsConstant(enumValues, constant, false);
	}

	/**
	 * Check whether the given array of enum constants contains a constant with the given name.
	 * 
	 * @param enumValues
	 *            the enum values to check, typically the product of a call to MyEnum.values()
	 * @param constant
	 *            the constant name to find (must not be null or empty string)
	 * @param caseSensitive
	 *            whether case is significant in determining a match
	 * @return whether the constant has been found in the given array
	 */
	public static boolean containsConstant(Enum<?>[] enumValues, String constant, boolean caseSensitive) {
		for (final Enum<?> candidate : enumValues) {
			if (caseSensitive ? candidate.toString().equals(constant) : candidate.toString().equalsIgnoreCase(constant)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given array contains the given element.
	 * 
	 * @param array
	 *            the array to check (may be <code>null</code>, in which case the return value will always be <code>false</code>)
	 * @param element
	 *            the element to check for
	 * @return whether the element has been found in the given array
	 */
	public static boolean containsElement(Object[] array, Object element) {
		if (array == null) {
			return false;
		}
		for (final Object arrayEle : array) {
			if (ObjectUtils.nullSafeEquals(arrayEle, element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return a content-based String representation if <code>obj</code> is not <code>null</code>; otherwise returns an empty String.
	 * <p>
	 * Differs from {@link #nullSafeToString(Object)} in that it returns an empty String rather than "null" for a <code>null</code> value.
	 * 
	 * @param obj
	 *            the object to build a display String for
	 * @return a display String representation of <code>obj</code>
	 * @see #nullSafeToString(Object)
	 */
	public static String getDisplayString(Object obj) {
		if (obj == null) {
			return ObjectUtils.EMPTY_STRING;
		}
		return ObjectUtils.nullSafeToString(obj);
	}

	/**
	 * Return a hex String form of an object's identity hash code.
	 * 
	 * @param obj
	 *            the object
	 * @return the object's identity code in hex notation
	 */
	public static String getIdentityHexString(Object obj) {
		return Integer.toHexString(System.identityHashCode(obj));
	}

	/**
	 * Generates a hash code for a sequence of input values.
	 * 
	 * The hash code is generated as if all the input values were placed into an array, and that array were hashed by calling
	 * {@link Arrays#hashCode(Object[])}.
	 * 
	 * <b>Warning: When a single object reference is supplied, the returned value does not equal the hash code of that object reference.</b>
	 * This value can be computed by calling {@link #hashCode(Object)}.
	 * 
	 * @param values
	 *            the values to be hashed
	 * @return a hash value of the sequence of input values
	 */
	public static int hash(Object... values) {
		return Arrays.hashCode(values);
	}

	/**
	 * Nullsafe hashCode.
	 * 
	 * @param o
	 * @return that hash of o if o is not null, 0 otherwise.
	 */
	public static int hashCode(Object o) {
		return o != null ? o.hashCode() : 0;
	}

	/**
	 * Return a String representation of an object's overall identity.
	 * 
	 * @param obj
	 *            the object (may be <code>null</code>)
	 * @return the object's identity as String representation, or an empty String if the object was <code>null</code>
	 */
	public static String identityToString(Object obj) {
		if (obj == null) {
			return ObjectUtils.EMPTY_STRING;
		}
		return obj.getClass().getName() + "@" + ObjectUtils.getIdentityHexString(obj);
	}

	/**
	 * Determine whether the given object is an array: either an Object array or a primitive array.
	 * 
	 * @param obj
	 *            the object to check
	 * @return <code>true</code> of object is an array <code>false</code> otherwise
	 */
	public static boolean isArray(Object obj) {
		return ((obj != null) && obj.getClass().isArray());
	}

	/**
	 * Return whether the given throwable is a checked exception: that is, neither a RuntimeException nor an Error.
	 * 
	 * @param ex
	 *            the throwable to check
	 * @return whether the throwable is a checked exception
	 * @see java.lang.Exception
	 * @see java.lang.RuntimeException
	 * @see java.lang.Error
	 */
	public static boolean isCheckedException(Throwable ex) {
		return !((ex instanceof RuntimeException) || (ex instanceof Error));
	}

	/**
	 * Check whether the given exception is compatible with the exceptions declared in a throws clause.
	 * 
	 * @param ex
	 *            the exception to checked
	 * @param declaredExceptions
	 *            the exceptions declared in the throws clause
	 * @return whether the given exception is compatible
	 */
	public static boolean isCompatibleWithThrowsClause(Throwable ex, Class<?>[] declaredExceptions) {
		if (!ObjectUtils.isCheckedException(ex)) {
			return true;
		}
		if (declaredExceptions != null) {
			int i = 0;
			while (i < declaredExceptions.length) {
				if (declaredExceptions[i].isAssignableFrom(ex.getClass())) {
					return true;
				}
				i++;
			}
		}
		return false;
	}

	/**
	 * Determine whether the given array is empty: i.e. <code>null</code> or of zero length.
	 * 
	 * @param array
	 *            the array to check
	 * @return <code>true</code> if the array is empty, <code>false</code> otherwise
	 */
	public static boolean isEmpty(Object[] array) {
		return ((array == null) || (array.length == 0));
	}

	/**
	 * Determine the class name for the given object.
	 * <p>
	 * Returns <code>"null"</code> if <code>obj</code> is <code>null</code>.
	 * 
	 * @param obj
	 *            the object to introspect (may be <code>null</code>)
	 * @return the corresponding class name
	 */
	public static String nullSafeClassName(Object obj) {
		return (obj != null ? obj.getClass().getName() : ObjectUtils.NULL_STRING);
	}

	/**
	 * Determine if the given objects are equal, returning <code>true</code> if both are <code>null</code> or <code>false</code> if only one
	 * is <code>null</code>.
	 * <p>
	 * Compares arrays with <code>Arrays.equals</code>, performing an equality check based on the array elements rather than the array
	 * reference.
	 * 
	 * @param o1
	 *            first Object to compare
	 * @param o2
	 *            second Object to compare
	 * @return whether the given objects are equal
	 * @see java.util.Arrays#equals
	 */
	public static boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if ((o1 == null) || (o2 == null)) {
			return false;
		}
		if (o1.equals(o2)) {
			return true;
		}
		if (o1.getClass().isArray() && o2.getClass().isArray()) {
			if ((o1 instanceof Object[]) && (o2 instanceof Object[])) {
				return Arrays.equals((Object[]) o1, (Object[]) o2);
			}
			if ((o1 instanceof boolean[]) && (o2 instanceof boolean[])) {
				return Arrays.equals((boolean[]) o1, (boolean[]) o2);
			}
			if ((o1 instanceof byte[]) && (o2 instanceof byte[])) {
				return Arrays.equals((byte[]) o1, (byte[]) o2);
			}
			if ((o1 instanceof char[]) && (o2 instanceof char[])) {
				return Arrays.equals((char[]) o1, (char[]) o2);
			}
			if ((o1 instanceof double[]) && (o2 instanceof double[])) {
				return Arrays.equals((double[]) o1, (double[]) o2);
			}
			if ((o1 instanceof float[]) && (o2 instanceof float[])) {
				return Arrays.equals((float[]) o1, (float[]) o2);
			}
			if ((o1 instanceof int[]) && (o2 instanceof int[])) {
				return Arrays.equals((int[]) o1, (int[]) o2);
			}
			if ((o1 instanceof long[]) && (o2 instanceof long[])) {
				return Arrays.equals((long[]) o1, (long[]) o2);
			}
			if ((o1 instanceof short[]) && (o2 instanceof short[])) {
				return Arrays.equals((short[]) o1, (short[]) o2);
			}
		}
		return false;
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements, enclosed in curly braces (<code>"{}"</code> ). Adjacent
	 * elements are separated by the characters <code>", "</code> (a comma followed by a space). Returns <code>"null"</code> if
	 * <code>array</code> is <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(boolean[] array) {
		if (array == null) {
			return ObjectUtils.NULL_STRING;
		}
		final int length = array.length;
		if (length == 0) {
			return ObjectUtils.EMPTY_ARRAY;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ObjectUtils.ARRAY_START);
			}
			else {
				sb.append(ObjectUtils.ARRAY_ELEMENT_SEPARATOR);
			}

			sb.append(array[i]);
		}
		sb.append(ObjectUtils.ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements, enclosed in curly braces (<code>"{}"</code> ). Adjacent
	 * elements are separated by the characters <code>", "</code> (a comma followed by a space). Returns <code>"null"</code> if
	 * <code>array</code> is <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(byte[] array) {
		if (array == null) {
			return ObjectUtils.NULL_STRING;
		}
		final int length = array.length;
		if (length == 0) {
			return ObjectUtils.EMPTY_ARRAY;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ObjectUtils.ARRAY_START);
			}
			else {
				sb.append(ObjectUtils.ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ObjectUtils.ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements, enclosed in curly braces (<code>"{}"</code> ). Adjacent
	 * elements are separated by the characters <code>", "</code> (a comma followed by a space). Returns <code>"null"</code> if
	 * <code>array</code> is <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(char[] array) {
		if (array == null) {
			return ObjectUtils.NULL_STRING;
		}
		final int length = array.length;
		if (length == 0) {
			return ObjectUtils.EMPTY_ARRAY;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ObjectUtils.ARRAY_START);
			}
			else {
				sb.append(ObjectUtils.ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append("'").append(array[i]).append("'");
		}
		sb.append(ObjectUtils.ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements, enclosed in curly braces (<code>"{}"</code> ). Adjacent
	 * elements are separated by the characters <code>", "</code> (a comma followed by a space). Returns <code>"null"</code> if
	 * <code>array</code> is <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(double[] array) {
		if (array == null) {
			return ObjectUtils.NULL_STRING;
		}
		final int length = array.length;
		if (length == 0) {
			return ObjectUtils.EMPTY_ARRAY;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ObjectUtils.ARRAY_START);
			}
			else {
				sb.append(ObjectUtils.ARRAY_ELEMENT_SEPARATOR);
			}

			sb.append(array[i]);
		}
		sb.append(ObjectUtils.ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements, enclosed in curly braces (<code>"{}"</code> ). Adjacent
	 * elements are separated by the characters <code>", "</code> (a comma followed by a space). Returns <code>"null"</code> if
	 * <code>array</code> is <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(float[] array) {
		if (array == null) {
			return ObjectUtils.NULL_STRING;
		}
		final int length = array.length;
		if (length == 0) {
			return ObjectUtils.EMPTY_ARRAY;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ObjectUtils.ARRAY_START);
			}
			else {
				sb.append(ObjectUtils.ARRAY_ELEMENT_SEPARATOR);
			}

			sb.append(array[i]);
		}
		sb.append(ObjectUtils.ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements, enclosed in curly braces (<code>"{}"</code> ). Adjacent
	 * elements are separated by the characters <code>", "</code> (a comma followed by a space). Returns <code>"null"</code> if
	 * <code>array</code> is <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(int[] array) {
		if (array == null) {
			return ObjectUtils.NULL_STRING;
		}
		final int length = array.length;
		if (length == 0) {
			return ObjectUtils.EMPTY_ARRAY;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ObjectUtils.ARRAY_START);
			}
			else {
				sb.append(ObjectUtils.ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ObjectUtils.ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements, enclosed in curly braces (<code>"{}"</code> ). Adjacent
	 * elements are separated by the characters <code>", "</code> (a comma followed by a space). Returns <code>"null"</code> if
	 * <code>array</code> is <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(long[] array) {
		if (array == null) {
			return ObjectUtils.NULL_STRING;
		}
		final int length = array.length;
		if (length == 0) {
			return ObjectUtils.EMPTY_ARRAY;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ObjectUtils.ARRAY_START);
			}
			else {
				sb.append(ObjectUtils.ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ObjectUtils.ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the specified Object.
	 * <p>
	 * Builds a String representation of the contents in case of an array. Returns <code>"null"</code> if <code>obj</code> is
	 * <code>null</code>.
	 * 
	 * @param obj
	 *            the object to build a String representation for
	 * @return a String representation of <code>obj</code>
	 */
	public static String nullSafeToString(Object obj) {
		if (obj == null) {
			return ObjectUtils.NULL_STRING;
		}
		if (obj instanceof String) {
			return (String) obj;
		}
		if (obj instanceof Object[]) {
			return ObjectUtils.nullSafeToString((Object[]) obj);
		}
		if (obj instanceof boolean[]) {
			return ObjectUtils.nullSafeToString((boolean[]) obj);
		}
		if (obj instanceof byte[]) {
			return ObjectUtils.nullSafeToString((byte[]) obj);
		}
		if (obj instanceof char[]) {
			return ObjectUtils.nullSafeToString((char[]) obj);
		}
		if (obj instanceof double[]) {
			return ObjectUtils.nullSafeToString((double[]) obj);
		}
		if (obj instanceof float[]) {
			return ObjectUtils.nullSafeToString((float[]) obj);
		}
		if (obj instanceof int[]) {
			return ObjectUtils.nullSafeToString((int[]) obj);
		}
		if (obj instanceof long[]) {
			return ObjectUtils.nullSafeToString((long[]) obj);
		}
		if (obj instanceof short[]) {
			return ObjectUtils.nullSafeToString((short[]) obj);
		}
		final String str = obj.toString();
		return (str != null ? str : ObjectUtils.EMPTY_STRING);
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements, enclosed in curly braces (<code>"{}"</code> ). Adjacent
	 * elements are separated by the characters <code>", "</code> (a comma followed by a space). Returns <code>"null"</code> if
	 * <code>array</code> is <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(Object[] array) {
		if (array == null) {
			return ObjectUtils.NULL_STRING;
		}
		final int length = array.length;
		if (length == 0) {
			return ObjectUtils.EMPTY_ARRAY;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ObjectUtils.ARRAY_START);
			}
			else {
				sb.append(ObjectUtils.ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(String.valueOf(array[i]));
		}
		sb.append(ObjectUtils.ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements, enclosed in curly braces (<code>"{}"</code> ). Adjacent
	 * elements are separated by the characters <code>", "</code> (a comma followed by a space). Returns <code>"null"</code> if
	 * <code>array</code> is <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(short[] array) {
		if (array == null) {
			return ObjectUtils.NULL_STRING;
		}
		final int length = array.length;
		if (length == 0) {
			return ObjectUtils.EMPTY_ARRAY;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ObjectUtils.ARRAY_START);
			}
			else {
				sb.append(ObjectUtils.ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ObjectUtils.ARRAY_END);
		return sb.toString();
	}

	/**
	 * Convert the given array (which may be a primitive array) to an object array (if necessary of primitive wrapper objects).
	 * <p>
	 * A <code>null</code> source value will be converted to an empty Object array.
	 * 
	 * @param source
	 *            the (potentially primitive) array
	 * @return the corresponding object array (never <code>null</code>)
	 * @throws IllegalArgumentException
	 *             if the parameter is not an array
	 */
	public static Object[] toObjectArray(Object source) {
		if (source instanceof Object[]) {
			return (Object[]) source;
		}
		if (source == null) {
			return new Object[0];
		}
		if (!source.getClass().isArray()) {
			throw new IllegalArgumentException("Source is not an array: " + source);
		}
		final int length = Array.getLength(source);
		if (length == 0) {
			return new Object[0];
		}
		final Class<?> wrapperType = Array.get(source, 0).getClass();
		final Object[] newArray = (Object[]) Array.newInstance(wrapperType, length);
		for (int i = 0; i < length; i++) {
			newArray[i] = Array.get(source, i);
		}
		return newArray;
	}

}
