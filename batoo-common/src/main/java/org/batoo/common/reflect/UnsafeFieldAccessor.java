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

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Accessor implementation of {@link AbstractAccessor} for the members of {@link Field}s.
 * 
 * @author hceylan
 * @since 2.0.1
 */
public class UnsafeFieldAccessor extends AbstractAccessor {

	private enum PrimitiveType {
		BOOLEAN(Boolean.TYPE),

		INTEGER(Integer.TYPE),

		FLOAT(Float.TYPE),

		DOUBLE(Double.TYPE),

		BYTE(Byte.TYPE),

		SHORT(Short.TYPE),

		LONG(Long.TYPE),

		CHAR(Character.TYPE);

		private final Class<?> clazz;

		PrimitiveType(Class<?> clazz) {
			this.clazz = clazz;
		}
	}

	private final Field field;
	private final long fieldOffset;
	private final PrimitiveType primitiveType;

	private Class<?> numberType;

	/**
	 * @param field
	 *            the field to access
	 * 
	 * @since 2.0.1
	 */
	@SuppressWarnings("restriction")
	public UnsafeFieldAccessor(Field field) {
		super();

		this.field = field;
		AccessController.doPrivileged(new PrivilegedAction<Void>() {

			@Override
			public Void run() {
				UnsafeFieldAccessor.this.field.setAccessible(true);

				return null;
			}
		});

		this.primitiveType = this.getPrimitiveType();
		this.fieldOffset = ReflectHelper.unsafe.objectFieldOffset(field);

		if (Number.class.isAssignableFrom(this.field.getType()) //
			|| (this.field.getType() == Byte.TYPE) //
			|| (this.field.getType() == Short.TYPE) //
			|| (this.field.getType() == Integer.TYPE) //
			|| (this.field.getType() == Long.TYPE)) {

			this.numberType = this.field.getType();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings({ "restriction" })
	public Object get(Object instance) {
		if (this.primitiveType == null) {
			return ReflectHelper.unsafe.getObject(instance, this.fieldOffset);
		}

		switch (this.primitiveType) {
			case BOOLEAN:
				return Boolean.valueOf(ReflectHelper.unsafe.getBoolean(instance, this.fieldOffset));
			case INTEGER:
				return Integer.valueOf(ReflectHelper.unsafe.getInt(instance, this.fieldOffset));
			case FLOAT:
				return Float.valueOf(ReflectHelper.unsafe.getFloat(instance, this.fieldOffset));
			case DOUBLE:
				return Double.valueOf(ReflectHelper.unsafe.getDouble(instance, this.fieldOffset));
			case LONG:
				return Long.valueOf(ReflectHelper.unsafe.getLong(instance, this.fieldOffset));
			case SHORT:
				return Short.valueOf(ReflectHelper.unsafe.getShort(instance, this.fieldOffset));
			case BYTE:
				return Byte.valueOf(ReflectHelper.unsafe.getByte(instance, this.fieldOffset));
			default: // CHAR
				return Character.valueOf(ReflectHelper.unsafe.getChar(instance, this.fieldOffset));
		}
	}

	/**
	 * @return
	 * 
	 * @since 2.0.1
	 */
	private PrimitiveType getPrimitiveType() {
		final Class<?> type = this.field.getType();
		if (type.isPrimitive()) {
			for (final PrimitiveType primitiveType : PrimitiveType.values()) {
				if (primitiveType.clazz == type) {
					return primitiveType;
				}
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("restriction")
	public void set(Object instance, Object value) {
		if (instance == null) {
			throw new NullPointerException();
		}

		if (this.primitiveType == null) {
			if ((this.numberType != null) && (value != null) && (this.numberType != value.getClass())) {
				final Number number = ReflectHelper.convertNumber((Number) value, this.numberType);

				ReflectHelper.unsafe.putObject(instance, this.fieldOffset, number);
			}
			else {
				ReflectHelper.unsafe.putObject(instance, this.fieldOffset, value);
			}
		}
		else {
			switch (this.primitiveType) {
				case BOOLEAN:
					if (value instanceof Number) {
						ReflectHelper.unsafe.putBoolean(instance, this.fieldOffset, ((Number) value).byteValue() == 0 ? false : true);
					}
					else {
						ReflectHelper.unsafe.putBoolean(instance, this.fieldOffset, (Boolean) value);
					}
					break;
				case INTEGER:
					ReflectHelper.unsafe.putInt(instance, this.fieldOffset, ((Number) value).intValue());
					break;
				case FLOAT:
					ReflectHelper.unsafe.putFloat(instance, this.fieldOffset, ((Number) value).floatValue());
					break;
				case DOUBLE:
					ReflectHelper.unsafe.putDouble(instance, this.fieldOffset, ((Number) value).doubleValue());
					break;
				case LONG:
					ReflectHelper.unsafe.putLong(instance, this.fieldOffset, ((Number) value).longValue());
					break;
				case SHORT:
					ReflectHelper.unsafe.putShort(instance, this.fieldOffset, ((Number) value).shortValue());
					break;
				case BYTE:
					ReflectHelper.unsafe.putByte(instance, this.fieldOffset, ((Number) value).byteValue());
					break;
				default: // CHAR
					if (value == null) {
						ReflectHelper.unsafe.putChar(instance, this.fieldOffset, '\u0000');
					}
					else {
						ReflectHelper.unsafe.putChar(instance, this.fieldOffset, (Character) value);
					}
					break;
			}
		}
	}

}
