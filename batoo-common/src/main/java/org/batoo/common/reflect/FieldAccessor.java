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

import org.batoo.common.BatooException;

/**
 * Accessor implementation of {@link AbstractAccessor} for the members of {@link Field}s.
 * 
 * @author hceylan
 * @since 2.0.1
 */
public class FieldAccessor extends AbstractAccessor {

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
	private final PrimitiveType primitiveType;
	private Class<?> numberType;

	/**
	 * @param field
	 *            the field to access
	 * 
	 * @since 2.0.1
	 */
	public FieldAccessor(Field field) {
		super();

		this.field = field;
		AccessController.doPrivileged(new PrivilegedAction<Void>() {

			@Override
			public Void run() {
				FieldAccessor.this.field.setAccessible(true);

				return null;
			}
		});

		this.primitiveType = this.getPrimitiveType();

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
	public Object get(Object instance) {
		try {
			return this.field.get(instance);
		}
		catch (final Exception e) {
			throw new BatooException("Cannot get field value: " + this.field, e);
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
	public void set(Object instance, Object value) {
		if (instance == null) {
			throw new NullPointerException();
		}

		try {
			if (this.primitiveType == null) {
				if ((this.numberType != null) && (value != null) && (this.numberType != value.getClass())) {
					final Number number = ReflectHelper.convertNumber((Number) value, this.numberType);
					this.field.set(instance, number);
				}
				else {
					this.field.set(instance, value);
				}
			}
			else {
				switch (this.primitiveType) {
					case BOOLEAN:
						if (value instanceof Number) {
							this.field.set(instance, ((Number) value).byteValue() == 0 ? false : true);
						}
						else {
							this.field.set(instance, value);
						}
						break;
					case INTEGER:
						this.field.set(instance, value);
						break;
					case FLOAT:
						this.field.set(instance, ((Number) value).floatValue());
						break;
					case DOUBLE:
						this.field.set(instance, value);
						break;
					case LONG:
						this.field.set(instance, value);
						break;
					case SHORT:
						this.field.set(instance, ((Number) value).shortValue());
						break;
					case BYTE:
						this.field.set(instance, ((Number) value).byteValue());
						break;
					default: // CHAR
						if (value == null) {
							this.field.set(instance, '\u0000');
						}
						else {
							this.field.set(instance, value);
						}
						break;
				}
			}
		}
		catch (final Exception e) {
			throw new RuntimeException("Cannot set field value: " + this.field, e);
		}
	}
}
