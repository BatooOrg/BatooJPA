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

import java.lang.reflect.Field;

/**
 * Accessor implemention for the {@link Field}s.
 * 
 * @author hceylan
 * @since $version
 */
public class FieldAccessor<Y> implements Accessor<Y> {

	private enum PrimitiveType {
		BOOLEAN(boolean.class),

		INTEGER(int.class),

		FLOAT(float.class),

		DOUBLE(double.class),

		BYTE(byte.class),

		SHORT(short.class),

		LONG(long.class),

		CHAR(char.class);

		private final Class<?> clazz;

		PrimitiveType(Class<?> clazz) {
			this.clazz = clazz;
		}
	}

	private static final Object INT_0 = new Integer(0);

	private final Field field;
	private final long fieldOffset;
	private final PrimitiveType primitiveType;

	private Class<?> numberType;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 * @param field
	 */
	@SuppressWarnings("restriction")
	public FieldAccessor(Field field) {
		super();

		this.field = field;
		this.primitiveType = this.getPrimitiveType();
		this.fieldOffset = ReflectHelper.unsafe.objectFieldOffset(field);

		if (Number.class.isAssignableFrom(this.field.getType())) {
			this.numberType = this.field.getType();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings({ "unchecked", "restriction" })
	public Y get(Object instance) {
		if (this.primitiveType == null) {
			return (Y) ReflectHelper.unsafe.getObject(instance, this.fieldOffset);
		}

		switch (this.primitiveType) {
			case BOOLEAN:
				return (Y) Boolean.valueOf(ReflectHelper.unsafe.getBoolean(instance, this.fieldOffset));
			case INTEGER:
				return (Y) Integer.valueOf(ReflectHelper.unsafe.getInt(instance, this.fieldOffset));
			case FLOAT:
				return (Y) Float.valueOf(ReflectHelper.unsafe.getFloat(instance, this.fieldOffset));
			case DOUBLE:
				return (Y) Double.valueOf(ReflectHelper.unsafe.getDouble(instance, this.fieldOffset));
			case LONG:
				return (Y) Long.valueOf(ReflectHelper.unsafe.getLong(instance, this.fieldOffset));
			case SHORT:
				return (Y) Boolean.valueOf(ReflectHelper.unsafe.getBoolean(instance, this.fieldOffset));
			case BYTE:
				return (Y) Byte.valueOf(ReflectHelper.unsafe.getByte(instance, this.fieldOffset));
			default: // CHAR
				return (Y) Character.valueOf(ReflectHelper.unsafe.getChar(instance, this.fieldOffset));
		}
	}

	/**
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
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
	public void set(Object instance, Y value) {
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
					if (value instanceof Integer) {
						ReflectHelper.unsafe.putBoolean(instance, this.fieldOffset, value.equals(INT_0) ? false : true);
					}
					else {
						ReflectHelper.unsafe.putBoolean(instance, this.fieldOffset, (Boolean) value);
					}
					break;
				case INTEGER:
					ReflectHelper.unsafe.putInt(instance, this.fieldOffset, (Integer) value);
					break;
				case FLOAT:
					if (value instanceof Double) {
						ReflectHelper.unsafe.putFloat(instance, this.fieldOffset, ((Double) value).floatValue());
					}
					else {
						ReflectHelper.unsafe.putFloat(instance, this.fieldOffset, (Float) value);
					}
					break;
				case DOUBLE:
					ReflectHelper.unsafe.putDouble(instance, this.fieldOffset, (Double) value);
					break;
				case LONG:
					ReflectHelper.unsafe.putLong(instance, this.fieldOffset, (Long) value);
					break;
				case SHORT:
					if (value instanceof Integer) {
						ReflectHelper.unsafe.putShort(instance, this.fieldOffset, ((Integer) value).shortValue());
					}
					else {
						ReflectHelper.unsafe.putShort(instance, this.fieldOffset, (Short) value);
					}
					break;
				case BYTE:
					if (value instanceof Integer) {
						ReflectHelper.unsafe.putByte(instance, this.fieldOffset, ((Integer) value).byteValue());
					}
					else {
						ReflectHelper.unsafe.putByte(instance, this.fieldOffset, (Byte) value);
					}
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
