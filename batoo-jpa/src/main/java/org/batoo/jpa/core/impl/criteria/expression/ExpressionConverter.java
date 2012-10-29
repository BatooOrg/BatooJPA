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
package org.batoo.jpa.core.impl.criteria.expression;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Converter for expressions types.
 * 
 * @param <N>
 *            the to type
 * 
 * @author hceylan
 * @since $version
 */
public abstract class ExpressionConverter<N> {

	/**
	 * {@link BigDecimal} converter.
	 * 
	 */
	public static final ExpressionConverter<BigDecimal> BIG_DECIMAL = new ExpressionConverter<BigDecimal>() {

		@Override
		public BigDecimal convertImpl(Object original) {
			return ExpressionConverter.convertExpressionImpl(original);
		}
	};

	/**
	 * {@link BigInteger} converter.
	 * 
	 */
	public static final ExpressionConverter<BigInteger> BIG_INTEGER = new ExpressionConverter<BigInteger>() {

		@Override
		public BigInteger convertImpl(Object value) {
			if (value instanceof BigInteger) {
				return (BigInteger) value;
			}

			// TODO How to do that?
			throw new IllegalArgumentException("Cannot convert from " + value);
		}
	};

	/**
	 * {@link Double} converter.
	 */
	public static final ExpressionConverter<Double> DOUBLE = new ExpressionConverter<Double>() {

		@Override
		protected Double convertImpl(Object value) {
			if (value instanceof Double) {
				return (Double) value;
			}

			return new Double(((Number) value).doubleValue());
		}
	};

	/**
	 * {@link Float} converter.
	 */
	public static final ExpressionConverter<Float> FLOAT = new ExpressionConverter<Float>() {

		@Override
		protected Float convertImpl(Object value) {
			if (value instanceof Float) {
				return (Float) value;
			}

			return new Float(((Number) value).floatValue());
		}
	};

	/**
	 * {@link Short} converter.
	 */
	public static final ExpressionConverter<Short> SHORT = new ExpressionConverter<Short>() {

		@Override
		protected Short convertImpl(Object value) {
			if (value instanceof Short) {
				return (Short) value;
			}

			return Short.valueOf(((Number) value).shortValue());
		}
	};

	/**
	 * {@link Long} converter.
	 */
	public static final ExpressionConverter<Long> LONG = new ExpressionConverter<Long>() {

		@Override
		protected Long convertImpl(Object value) {
			if (value instanceof Long) {
				return (Long) value;
			}

			return Long.valueOf(((Number) value).longValue());
		}
	};

	/**
	 * {@link Integer} converter.
	 */
	public static final ExpressionConverter<Integer> INTEGER = new ExpressionConverter<Integer>() {

		@Override
		protected Integer convertImpl(Object value) {
			if (value instanceof Integer) {
				return (Integer) value;
			}

			return Integer.valueOf(((Number) value).intValue());
		}
	};

	/**
	 * {@link Integer} converter.
	 */
	public static final ExpressionConverter<String> STRING = new ExpressionConverter<String>() {

		@Override
		protected String convertImpl(Object value) {
			if (value instanceof String) {
				return (String) value;
			}

			if (value instanceof Character) {
				return ((Character) value).toString();
			}

			throw new IllegalArgumentException("Cannot convert from " + value);
		}
	};

	private static BigDecimal convertExpressionImpl(Object original) {
		final Number value = (Number) original;

		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		}

		if (value instanceof Float) {
			return new BigDecimal(value.floatValue());
		}

		if (value instanceof Double) {
			return new BigDecimal(value.doubleValue());
		}

		if (value instanceof Byte) {
			return new BigDecimal(value.byteValue());
		}

		if (value instanceof Integer) {
			return new BigDecimal(value.intValue());
		}

		if (value instanceof Long) {
			return new BigDecimal(value.longValue());
		}

		if (value instanceof Short) {
			return new BigDecimal(value.shortValue());
		}

		if (value instanceof BigInteger) {
			return new BigDecimal((BigInteger) value);
		}

		throw new IllegalArgumentException("Cannot convert from " + value);
	}

	/**
	 * Converts the number to <code>T</code> type.
	 * 
	 * @param value
	 *            the from value
	 * @return the converted <code>T</code> value
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public N convert(Object value) {
		if (value == null) {
			return null;
		}

		return this.convertImpl(value);
	}

	@SuppressWarnings("javadoc")
	protected abstract N convertImpl(Object value);
}
