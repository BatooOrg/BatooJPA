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
package org.batoo.jpa.core.test.enhance;

import java.sql.SQLException;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;

/**
 * A Sample class for the enhanced type
 * 
 * @author hceylan
 * @since $version
 */
public class Person$Enhanced extends Person implements EnhancedInstance {

	private static final long serialVersionUID = 1L;

	private boolean __enhanced__$$__initialized;
	private transient final Object __enhanced__$$__id;
	private transient final Class<?> __enhanced_$$__type;
	private transient final SessionImpl __enhanced_$$__session;

	public Person$Enhanced() {
		super();

		this.__enhanced__$$__id = null;
		this.__enhanced_$$__type = null;
		this.__enhanced_$$__session = null;
	}

	public Person$Enhanced(Class<?> type, SessionImpl session, Object id, boolean initialized) {
		super();

		this.__enhanced_$$__type = type;
		this.__enhanced_$$__session = session;
		this.__enhanced__$$__id = id;
		this.__enhanced__$$__initialized = initialized;
	}

	@Override
	public boolean __enhanced__$$__isInitialized() {
		return this.__enhanced__$$__initialized;
	}

	private void __enhanced_$$__check() {
		if (!this.__enhanced__$$__initialized) {
			this.__enhanced_$$__session.getEntityManager().find(this.__enhanced_$$__type, this.__enhanced__$$__id);
			this.__enhanced__$$__initialized = true;
		}
	}

	@Override
	public Object get__enhanced__$$__id() {
		return this.__enhanced__$$__id;
	}

	@Override
	public boolean[] getBooleanPropertyArray() {
		this.__enhanced_$$__check();
		return super.getBooleanPropertyArray();
	}

	@Override
	public byte getByteProperty() {
		this.__enhanced_$$__check();
		return super.getByteProperty();
	}

	@Override
	public byte[] getBytePropertyArray() {
		this.__enhanced_$$__check();
		return super.getBytePropertyArray();
	}

	@Override
	public char getCharProperty() {
		this.__enhanced_$$__check();
		return super.getCharProperty();
	}

	@Override
	public char[] getCharPropertyArray() {
		this.__enhanced_$$__check();
		return super.getCharPropertyArray();
	}

	@Override
	public double getDoubleProperty() {
		this.__enhanced_$$__check();
		return super.getDoubleProperty();
	}

	@Override
	public double[] getDoublePropertyArray() {
		this.__enhanced_$$__check();
		return super.getDoublePropertyArray();
	}

	@Override
	public float getFloatProperty() {
		this.__enhanced_$$__check();
		return super.getFloatProperty();
	}

	@Override
	public float[] getFloatPropertyArray() {
		this.__enhanced_$$__check();
		return super.getFloatPropertyArray();
	}

	@Override
	public Integer getId() {
		this.__enhanced_$$__check();
		return super.getId();
	}

	@Override
	public int getIntProperty() {
		this.__enhanced_$$__check();
		return super.getIntProperty();
	}

	@Override
	public int[] getIntPropertyArray() {
		this.__enhanced_$$__check();
		return super.getIntPropertyArray();
	}

	@Override
	public long getLongProperty() {
		this.__enhanced_$$__check();
		return super.getLongProperty();
	}

	@Override
	public long[] getLongPropertyArray() {
		this.__enhanced_$$__check();
		return super.getLongPropertyArray();
	}

	@Override
	public short getShortProperty() {
		this.__enhanced_$$__check();
		return super.getShortProperty();
	}

	@Override
	public short[] getShortPropertyArray() {
		this.__enhanced_$$__check();
		return super.getShortPropertyArray();
	}

	@Override
	public String getStringProperty() {
		this.__enhanced_$$__check();
		return super.getStringProperty();
	}

	@Override
	public String getStringPropertyArray() {
		this.__enhanced_$$__check();
		return super.getStringPropertyArray();
	}

	@Override
	public boolean isBooleanProperty() {
		this.__enhanced_$$__check();
		return super.isBooleanProperty();
	}

	@Override
	public void setBooleanProperty(boolean booleanProperty) {
		this.__enhanced_$$__check();
		super.setBooleanProperty(booleanProperty);
	}

	@Override
	public void setBooleanPropertyArray(boolean[] booleanPropertyArray) {
		this.__enhanced_$$__check();
		super.setBooleanPropertyArray(booleanPropertyArray);
	}

	@Override
	public void setByteProperty(byte byteProperty) {
		this.__enhanced_$$__check();
		super.setByteProperty(byteProperty);
	}

	@Override
	public void setBytePropertyArray(byte[] bytePropertyArray) {
		this.__enhanced_$$__check();
		super.setBytePropertyArray(bytePropertyArray);
	}

	@Override
	public void setCharProperty(char charProperty) {
		this.__enhanced_$$__check();
		super.setCharProperty(charProperty);
	}

	@Override
	public void setCharPropertyArray(char[] charPropertyArray) {
		this.__enhanced_$$__check();
		super.setCharPropertyArray(charPropertyArray);
	}

	@Override
	public void setDoubleProperty(double doubleProperty) {
		this.__enhanced_$$__check();
		super.setDoubleProperty(doubleProperty);
	}

	@Override
	public void setDoublePropertyArray(double[] doublePropertyArray) {
		this.__enhanced_$$__check();
		super.setDoublePropertyArray(doublePropertyArray);
	}

	@Override
	public void setFloatProperty(float floatProperty) {
		this.__enhanced_$$__check();
		super.setFloatProperty(floatProperty);
	}

	@Override
	public void setFloatPropertyArray(float[] floatPropertyArray) {
		this.__enhanced_$$__check();
		super.setFloatPropertyArray(floatPropertyArray);
	}

	@Override
	public void setId(Integer id) {
		this.__enhanced_$$__check();
		super.setId(id);
	}

	@Override
	public void setIntProperty(int intProperty) {
		this.__enhanced_$$__check();
		super.setIntProperty(intProperty);
	}

	@Override
	public void setIntPropertyArray(int[] intPropertyArray) {
		this.__enhanced_$$__check();
		super.setIntPropertyArray(intPropertyArray);
	}

	@Override
	public void setLongProperty(long longProperty) {
		this.__enhanced_$$__check();
		super.setLongProperty(longProperty);
	}

	@Override
	public void setLongPropertyArray(long[] longPropertyArray) {
		this.__enhanced_$$__check();
		super.setLongPropertyArray(longPropertyArray);
	}

	@Override
	public void setShortProperty(short shortProperty) {
		this.__enhanced_$$__check();
		super.setShortProperty(shortProperty);
	}

	@Override
	public void setShortPropertyArray(short[] shortPropertyArray) {
		this.__enhanced_$$__check();
		super.setShortPropertyArray(shortPropertyArray);
	}

	@Override
	public void setStringProperty(String stringProperty) {
		this.__enhanced_$$__check();
		super.setStringProperty(stringProperty);
	}

	@Override
	public void setStringPropertyArray(String stringPropertyArray) {
		this.__enhanced_$$__check();
		super.setStringPropertyArray(stringPropertyArray);
	}

	@Override
	public Object someBusinessMethod(boolean arg0, char arg1, byte arg2, short arg3, int arg4, long arg5, float arg6, double arg7,
		String arg8, Integer[] arg9, long[] arg10) throws SQLException, MappingException {
		this.__enhanced_$$__check();
		return super.someBusinessMethod(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
	}
}
