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
package org.batoo.jpa.core.test.enhance;

import java.sql.SQLException;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.batoo.jpa.parser.MappingException;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class Person {

	/**
	 * Sample static method.
	 * 
	 * @return the person
	 * 
	 * @since 2.0.0
	 */
	public static Person create() {
		return new Person();
	}

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
	 * @since 2.0.0
	 */
	public boolean[] getBooleanPropertyArray() {
		return this.booleanPropertyArray;
	}

	/**
	 * Returns the byteProperty.
	 * 
	 * @return the byteProperty
	 * @since 2.0.0
	 */
	public byte getByteProperty() {
		return this.byteProperty;
	}

	/**
	 * Returns the bytePropertyArray.
	 * 
	 * @return the bytePropertyArray
	 * @since 2.0.0
	 */
	public byte[] getBytePropertyArray() {
		return this.bytePropertyArray;
	}

	/**
	 * Returns the charProperty.
	 * 
	 * @return the charProperty
	 * @since 2.0.0
	 */
	public char getCharProperty() {
		return this.charProperty;
	}

	/**
	 * Returns the charPropertyArray.
	 * 
	 * @return the charPropertyArray
	 * @since 2.0.0
	 */
	public char[] getCharPropertyArray() {
		return this.charPropertyArray;
	}

	/**
	 * Returns the doubleProperty.
	 * 
	 * @return the doubleProperty
	 * @since 2.0.0
	 */
	public double getDoubleProperty() {
		return this.doubleProperty;
	}

	/**
	 * Returns the doublePropertyArray.
	 * 
	 * @return the doublePropertyArray
	 * @since 2.0.0
	 */
	public double[] getDoublePropertyArray() {
		return this.doublePropertyArray;
	}

	/**
	 * Returns the floatProperty.
	 * 
	 * @return the floatProperty
	 * @since 2.0.0
	 */
	public float getFloatProperty() {
		return this.floatProperty;
	}

	/**
	 * Returns the floatPropertyArray.
	 * 
	 * @return the floatPropertyArray
	 * @since 2.0.0
	 */
	public float[] getFloatPropertyArray() {
		return this.floatPropertyArray;
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 * @since 2.0.0
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Returns the intProperty.
	 * 
	 * @return the intProperty
	 * @since 2.0.0
	 */
	public int getIntProperty() {
		return this.intProperty;
	}

	/**
	 * Returns the intPropertyArray.
	 * 
	 * @return the intPropertyArray
	 * @since 2.0.0
	 */
	public int[] getIntPropertyArray() {
		return this.intPropertyArray;
	}

	/**
	 * Returns the longProperty.
	 * 
	 * @return the longProperty
	 * @since 2.0.0
	 */
	public long getLongProperty() {
		return this.longProperty;
	}

	/**
	 * Returns the longPropertyArray.
	 * 
	 * @return the longPropertyArray
	 * @since 2.0.0
	 */
	public long[] getLongPropertyArray() {
		return this.longPropertyArray;
	}

	/**
	 * Returns the shortProperty.
	 * 
	 * @return the shortProperty
	 * @since 2.0.0
	 */
	public short getShortProperty() {
		return this.shortProperty;
	}

	/**
	 * Returns the shortPropertyArray.
	 * 
	 * @return the shortPropertyArray
	 * @since 2.0.0
	 */
	public short[] getShortPropertyArray() {
		return this.shortPropertyArray;
	}

	/**
	 * Returns the stringProperty.
	 * 
	 * @return the stringProperty
	 * @since 2.0.0
	 */
	public String getStringProperty() {
		return this.stringProperty;
	}

	/**
	 * Returns the stringPropertyArray.
	 * 
	 * @return the stringPropertyArray
	 * @since 2.0.0
	 */
	public String getStringPropertyArray() {
		return this.stringPropertyArray;
	}

	/**
	 * Returns the booleanProperty.
	 * 
	 * @return the booleanProperty
	 * @since 2.0.0
	 */
	public boolean isBooleanProperty() {
		return this.booleanProperty;
	}

	/**
	 * Sets the booleanProperty.
	 * 
	 * @param booleanProperty
	 *            the booleanProperty to set
	 * @since 2.0.0
	 */
	public void setBooleanProperty(boolean booleanProperty) {
		this.booleanProperty = booleanProperty;
	}

	/**
	 * Sets the booleanPropertyArray.
	 * 
	 * @param booleanPropertyArray
	 *            the booleanPropertyArray to set
	 * @since 2.0.0
	 */
	public void setBooleanPropertyArray(boolean[] booleanPropertyArray) {
		this.booleanPropertyArray = booleanPropertyArray;
	}

	/**
	 * Sets the byteProperty.
	 * 
	 * @param byteProperty
	 *            the byteProperty to set
	 * @since 2.0.0
	 */
	public void setByteProperty(byte byteProperty) {
		this.byteProperty = byteProperty;
	}

	/**
	 * Sets the bytePropertyArray.
	 * 
	 * @param bytePropertyArray
	 *            the bytePropertyArray to set
	 * @since 2.0.0
	 */
	public void setBytePropertyArray(byte[] bytePropertyArray) {
		this.bytePropertyArray = bytePropertyArray;
	}

	/**
	 * Sets the charProperty.
	 * 
	 * @param charProperty
	 *            the charProperty to set
	 * @since 2.0.0
	 */
	public void setCharProperty(char charProperty) {
		this.charProperty = charProperty;
	}

	/**
	 * Sets the charPropertyArray.
	 * 
	 * @param charPropertyArray
	 *            the charPropertyArray to set
	 * @since 2.0.0
	 */
	public void setCharPropertyArray(char[] charPropertyArray) {
		this.charPropertyArray = charPropertyArray;
	}

	/**
	 * Sets the doubleProperty.
	 * 
	 * @param doubleProperty
	 *            the doubleProperty to set
	 * @since 2.0.0
	 */
	public void setDoubleProperty(double doubleProperty) {
		this.doubleProperty = doubleProperty;
	}

	/**
	 * Sets the doublePropertyArray.
	 * 
	 * @param doublePropertyArray
	 *            the doublePropertyArray to set
	 * @since 2.0.0
	 */
	public void setDoublePropertyArray(double[] doublePropertyArray) {
		this.doublePropertyArray = doublePropertyArray;
	}

	/**
	 * Sets the floatProperty.
	 * 
	 * @param floatProperty
	 *            the floatProperty to set
	 * @since 2.0.0
	 */
	public void setFloatProperty(float floatProperty) {
		this.floatProperty = floatProperty;
	}

	/**
	 * Sets the floatPropertyArray.
	 * 
	 * @param floatPropertyArray
	 *            the floatPropertyArray to set
	 * @since 2.0.0
	 */
	public void setFloatPropertyArray(float[] floatPropertyArray) {
		this.floatPropertyArray = floatPropertyArray;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 * @since 2.0.0
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Sets the intProperty.
	 * 
	 * @param intProperty
	 *            the intProperty to set
	 * @since 2.0.0
	 */
	public void setIntProperty(int intProperty) {
		this.intProperty = intProperty;
	}

	/**
	 * Sets the intPropertyArray.
	 * 
	 * @param intPropertyArray
	 *            the intPropertyArray to set
	 * @since 2.0.0
	 */
	public void setIntPropertyArray(int[] intPropertyArray) {
		this.intPropertyArray = intPropertyArray;
	}

	/**
	 * Sets the longProperty.
	 * 
	 * @param longProperty
	 *            the longProperty to set
	 * @since 2.0.0
	 */
	public void setLongProperty(long longProperty) {
		this.longProperty = longProperty;
	}

	/**
	 * Sets the longPropertyArray.
	 * 
	 * @param longPropertyArray
	 *            the longPropertyArray to set
	 * @since 2.0.0
	 */
	public void setLongPropertyArray(long[] longPropertyArray) {
		this.longPropertyArray = longPropertyArray;
	}

	/**
	 * Sets the shortProperty.
	 * 
	 * @param shortProperty
	 *            the shortProperty to set
	 * @since 2.0.0
	 */
	public void setShortProperty(short shortProperty) {
		this.shortProperty = shortProperty;
	}

	/**
	 * Sets the shortPropertyArray.
	 * 
	 * @param shortPropertyArray
	 *            the shortPropertyArray to set
	 * @since 2.0.0
	 */
	public void setShortPropertyArray(short[] shortPropertyArray) {
		this.shortPropertyArray = shortPropertyArray;
	}

	/**
	 * Sets the stringProperty.
	 * 
	 * @param stringProperty
	 *            the stringProperty to set
	 * @since 2.0.0
	 */
	public void setStringProperty(String stringProperty) {
		this.stringProperty = stringProperty;
	}

	/**
	 * Sets the stringPropertyArray.
	 * 
	 * @param stringPropertyArray
	 *            the stringPropertyArray to set
	 * @since 2.0.0
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
	 * @since 2.0.0
	 */
	public Object someBusinessMethod(boolean arg0, char arg1, byte arg2, short arg3, int arg4, long arg5, float arg6, double arg7, String arg8, Integer[] arg9,
		long[] arg10) throws SQLException, MappingException {
		return null;
	}
}
