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

import javax.persistence.Entity;
import javax.persistence.Id;

import org.batoo.jpa.parser.MappingException;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Person {

	@Id
	private Integer id;

	private boolean booleanProperty;
	private char charProperty;
	private byte byteProperty;
	private short shortProperty;
	private int intProperty;
	private long longProperty;
	private float floatProperty;
	private double doubleProperty;

	private boolean[] booleanPropertyArray;
	private char[] charPropertyArray;
	private byte[] bytePropertyArray;
	private short[] shortPropertyArray;
	private int[] intPropertyArray;
	private long[] longPropertyArray;
	private float[] floatPropertyArray;
	private double[] doublePropertyArray;

	private String stringProperty;
	private String stringPropertyArray;

	/**
	 * Returns the booleanPropertyArray.
	 * 
	 * @return the booleanPropertyArray
	 * @since $version
	 */
	public boolean[] getBooleanPropertyArray() {
		return this.booleanPropertyArray;
	}

	/**
	 * Returns the byteProperty.
	 * 
	 * @return the byteProperty
	 * @since $version
	 */
	public byte getByteProperty() {
		return this.byteProperty;
	}

	/**
	 * Returns the bytePropertyArray.
	 * 
	 * @return the bytePropertyArray
	 * @since $version
	 */
	public byte[] getBytePropertyArray() {
		return this.bytePropertyArray;
	}

	/**
	 * Returns the charProperty.
	 * 
	 * @return the charProperty
	 * @since $version
	 */
	public char getCharProperty() {
		return this.charProperty;
	}

	/**
	 * Returns the charPropertyArray.
	 * 
	 * @return the charPropertyArray
	 * @since $version
	 */
	public char[] getCharPropertyArray() {
		return this.charPropertyArray;
	}

	/**
	 * Returns the doubleProperty.
	 * 
	 * @return the doubleProperty
	 * @since $version
	 */
	public double getDoubleProperty() {
		return this.doubleProperty;
	}

	/**
	 * Returns the doublePropertyArray.
	 * 
	 * @return the doublePropertyArray
	 * @since $version
	 */
	public double[] getDoublePropertyArray() {
		return this.doublePropertyArray;
	}

	/**
	 * Returns the floatProperty.
	 * 
	 * @return the floatProperty
	 * @since $version
	 */
	public float getFloatProperty() {
		return this.floatProperty;
	}

	/**
	 * Returns the floatPropertyArray.
	 * 
	 * @return the floatPropertyArray
	 * @since $version
	 */
	public float[] getFloatPropertyArray() {
		return this.floatPropertyArray;
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 * @since $version
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the intProperty.
	 * 
	 * @return the intProperty
	 * @since $version
	 */
	public int getIntProperty() {
		return this.intProperty;
	}

	/**
	 * Returns the intPropertyArray.
	 * 
	 * @return the intPropertyArray
	 * @since $version
	 */
	public int[] getIntPropertyArray() {
		return this.intPropertyArray;
	}

	/**
	 * Returns the longProperty.
	 * 
	 * @return the longProperty
	 * @since $version
	 */
	public long getLongProperty() {
		return this.longProperty;
	}

	/**
	 * Returns the longPropertyArray.
	 * 
	 * @return the longPropertyArray
	 * @since $version
	 */
	public long[] getLongPropertyArray() {
		return this.longPropertyArray;
	}

	/**
	 * Returns the shortProperty.
	 * 
	 * @return the shortProperty
	 * @since $version
	 */
	public short getShortProperty() {
		return this.shortProperty;
	}

	/**
	 * Returns the shortPropertyArray.
	 * 
	 * @return the shortPropertyArray
	 * @since $version
	 */
	public short[] getShortPropertyArray() {
		return this.shortPropertyArray;
	}

	/**
	 * Returns the stringProperty.
	 * 
	 * @return the stringProperty
	 * @since $version
	 */
	public String getStringProperty() {
		return this.stringProperty;
	}

	/**
	 * Returns the stringPropertyArray.
	 * 
	 * @return the stringPropertyArray
	 * @since $version
	 */
	public String getStringPropertyArray() {
		return this.stringPropertyArray;
	}

	/**
	 * Returns the booleanProperty.
	 * 
	 * @return the booleanProperty
	 * @since $version
	 */
	public boolean isBooleanProperty() {
		return this.booleanProperty;
	}

	/**
	 * Sets the booleanProperty.
	 * 
	 * @param booleanProperty
	 *            the booleanProperty to set
	 * @since $version
	 */
	public void setBooleanProperty(boolean booleanProperty) {
		this.booleanProperty = booleanProperty;
	}

	/**
	 * Sets the booleanPropertyArray.
	 * 
	 * @param booleanPropertyArray
	 *            the booleanPropertyArray to set
	 * @since $version
	 */
	public void setBooleanPropertyArray(boolean[] booleanPropertyArray) {
		this.booleanPropertyArray = booleanPropertyArray;
	}

	/**
	 * Sets the byteProperty.
	 * 
	 * @param byteProperty
	 *            the byteProperty to set
	 * @since $version
	 */
	public void setByteProperty(byte byteProperty) {
		this.byteProperty = byteProperty;
	}

	/**
	 * Sets the bytePropertyArray.
	 * 
	 * @param bytePropertyArray
	 *            the bytePropertyArray to set
	 * @since $version
	 */
	public void setBytePropertyArray(byte[] bytePropertyArray) {
		this.bytePropertyArray = bytePropertyArray;
	}

	/**
	 * Sets the charProperty.
	 * 
	 * @param charProperty
	 *            the charProperty to set
	 * @since $version
	 */
	public void setCharProperty(char charProperty) {
		this.charProperty = charProperty;
	}

	/**
	 * Sets the charPropertyArray.
	 * 
	 * @param charPropertyArray
	 *            the charPropertyArray to set
	 * @since $version
	 */
	public void setCharPropertyArray(char[] charPropertyArray) {
		this.charPropertyArray = charPropertyArray;
	}

	/**
	 * Sets the doubleProperty.
	 * 
	 * @param doubleProperty
	 *            the doubleProperty to set
	 * @since $version
	 */
	public void setDoubleProperty(double doubleProperty) {
		this.doubleProperty = doubleProperty;
	}

	/**
	 * Sets the doublePropertyArray.
	 * 
	 * @param doublePropertyArray
	 *            the doublePropertyArray to set
	 * @since $version
	 */
	public void setDoublePropertyArray(double[] doublePropertyArray) {
		this.doublePropertyArray = doublePropertyArray;
	}

	/**
	 * Sets the floatProperty.
	 * 
	 * @param floatProperty
	 *            the floatProperty to set
	 * @since $version
	 */
	public void setFloatProperty(float floatProperty) {
		this.floatProperty = floatProperty;
	}

	/**
	 * Sets the floatPropertyArray.
	 * 
	 * @param floatPropertyArray
	 *            the floatPropertyArray to set
	 * @since $version
	 */
	public void setFloatPropertyArray(float[] floatPropertyArray) {
		this.floatPropertyArray = floatPropertyArray;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 * @since $version
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Sets the intProperty.
	 * 
	 * @param intProperty
	 *            the intProperty to set
	 * @since $version
	 */
	public void setIntProperty(int intProperty) {
		this.intProperty = intProperty;
	}

	/**
	 * Sets the intPropertyArray.
	 * 
	 * @param intPropertyArray
	 *            the intPropertyArray to set
	 * @since $version
	 */
	public void setIntPropertyArray(int[] intPropertyArray) {
		this.intPropertyArray = intPropertyArray;
	}

	/**
	 * Sets the longProperty.
	 * 
	 * @param longProperty
	 *            the longProperty to set
	 * @since $version
	 */
	public void setLongProperty(long longProperty) {
		this.longProperty = longProperty;
	}

	/**
	 * Sets the longPropertyArray.
	 * 
	 * @param longPropertyArray
	 *            the longPropertyArray to set
	 * @since $version
	 */
	public void setLongPropertyArray(long[] longPropertyArray) {
		this.longPropertyArray = longPropertyArray;
	}

	/**
	 * Sets the shortProperty.
	 * 
	 * @param shortProperty
	 *            the shortProperty to set
	 * @since $version
	 */
	public void setShortProperty(short shortProperty) {
		this.shortProperty = shortProperty;
	}

	/**
	 * Sets the shortPropertyArray.
	 * 
	 * @param shortPropertyArray
	 *            the shortPropertyArray to set
	 * @since $version
	 */
	public void setShortPropertyArray(short[] shortPropertyArray) {
		this.shortPropertyArray = shortPropertyArray;
	}

	/**
	 * Sets the stringProperty.
	 * 
	 * @param stringProperty
	 *            the stringProperty to set
	 * @since $version
	 */
	public void setStringProperty(String stringProperty) {
		this.stringProperty = stringProperty;
	}

	/**
	 * Sets the stringPropertyArray.
	 * 
	 * @param stringPropertyArray
	 *            the stringPropertyArray to set
	 * @since $version
	 */
	public void setStringPropertyArray(String stringPropertyArray) {
		this.stringPropertyArray = stringPropertyArray;
	}

	/**
	 * @param arg0
	 *            the arg
	 * @param arg1
	 *            the arg
	 * @param arg2
	 *            the arg
	 * @param arg3
	 *            the arg
	 * @param arg4
	 *            the arg
	 * @param arg5
	 *            the arg
	 * @param arg6
	 *            the arg
	 * @param arg7
	 *            the arg
	 * @param arg8
	 *            the arg
	 * @param arg9
	 *            the arg
	 * @param arg10
	 *            the arg
	 * @return the return value
	 * @throws SQLException
	 *             some exception
	 * @throws MappingException
	 *             some exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Object someBusinessMethod(boolean arg0, char arg1, byte arg2, short arg3, int arg4, long arg5, float arg6, double arg7, String arg8, Integer[] arg9,
		long[] arg10) throws SQLException, MappingException {
		return null;
	}
}
