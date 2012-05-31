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
package org.batoo.jpa.core.test.simple2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Foo {

	// TODO enable BigInteger when it was fixed

	private enum E {
		ONE,
		TWO;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer key;

	private boolean booleanValue;

	private byte byteValue;
	private short shortValue;
	private int intValue;
	private long longValue;

	private float floatValue;
	private double doubleValue;

	private Boolean booleanObjectValue;
	private Byte byteObjectValue;
	private Short shortObjectValue;
	private Integer intObjectValue;
	private Long longObjectValue;

	private Float floatObjectValue;
	private Double doubleObjectValue;

	private Enum<E> enumValue;

	private String stringValue;

	private BigInteger bigIntegerValue;
	private BigDecimal bigDecimalValue;

	private Calendar calendarValue;
	private Date dateValue;

	private java.sql.Date sqlDateValue;
	private java.sql.Time sqlTimeValue;
	private java.sql.Timestamp sqlTimeStampValue;

	/**
	 * Returns the bigDecimalValue.
	 * 
	 * @return the bigDecimalValue
	 * @since $version
	 */
	public BigDecimal getBigDecimalValue() {
		return this.bigDecimalValue;
	}

	/**
	 * Returns the bigIntegerValue.
	 * 
	 * @return the bigIntegerValue
	 * @since $version
	 */
	public BigInteger getBigIntegerValue() {
		return this.bigIntegerValue;
	}

	/**
	 * Returns the booleanObjectValue.
	 * 
	 * @return the booleanObjectValue
	 * @since $version
	 */
	public Boolean getBooleanObjectValue() {
		return this.booleanObjectValue;
	}

	/**
	 * Returns the byteObjectValue.
	 * 
	 * @return the byteObjectValue
	 * @since $version
	 */
	public Byte getByteObjectValue() {
		return this.byteObjectValue;
	}

	/**
	 * Returns the byteValue.
	 * 
	 * @return the byteValue
	 * @since $version
	 */
	public byte getByteValue() {
		return this.byteValue;
	}

	/**
	 * Returns the calendarValue.
	 * 
	 * @return the calendarValue
	 * @since $version
	 */
	public Calendar getCalendarValue() {
		return this.calendarValue;
	}

	/**
	 * Returns the dateValue.
	 * 
	 * @return the dateValue
	 * @since $version
	 */
	public Date getDateValue() {
		return this.dateValue;
	}

	/**
	 * Returns the doubleObjectValue.
	 * 
	 * @return the doubleObjectValue
	 * @since $version
	 */
	public Double getDoubleObjectValue() {
		return this.doubleObjectValue;
	}

	/**
	 * Returns the doubleValue.
	 * 
	 * @return the doubleValue
	 * @since $version
	 */
	public double getDoubleValue() {
		return this.doubleValue;
	}

	/**
	 * Returns the enumValue.
	 * 
	 * @return the enumValue
	 * @since $version
	 */
	public Enum<E> getEnumValue() {
		return this.enumValue;
	}

	/**
	 * Returns the floatObjectValue.
	 * 
	 * @return the floatObjectValue
	 * @since $version
	 */
	public Float getFloatObjectValue() {
		return this.floatObjectValue;
	}

	/**
	 * Returns the floatValue.
	 * 
	 * @return the floatValue
	 * @since $version
	 */
	public float getFloatValue() {
		return this.floatValue;
	}

	/**
	 * Returns the intObjectValue.
	 * 
	 * @return the intObjectValue
	 * @since $version
	 */
	public Integer getIntObjectValue() {
		return this.intObjectValue;
	}

	/**
	 * Returns the intValue.
	 * 
	 * @return the intValue
	 * @since $version
	 */
	public int getIntValue() {
		return this.intValue;
	}

	/**
	 * Returns the key.
	 * 
	 * @return the key
	 * @since $version
	 */
	public Integer getKey() {
		return this.key;
	}

	/**
	 * Returns the longObjectValue.
	 * 
	 * @return the longObjectValue
	 * @since $version
	 */
	public Long getLongObjectValue() {
		return this.longObjectValue;
	}

	/**
	 * Returns the longValue.
	 * 
	 * @return the longValue
	 * @since $version
	 */
	public long getLongValue() {
		return this.longValue;
	}

	/**
	 * Returns the shortObjectValue.
	 * 
	 * @return the shortObjectValue
	 * @since $version
	 */
	public Short getShortObjectValue() {
		return this.shortObjectValue;
	}

	/**
	 * Returns the shortValue.
	 * 
	 * @return the shortValue
	 * @since $version
	 */
	public short getShortValue() {
		return this.shortValue;
	}

	/**
	 * Returns the sqlDateValue.
	 * 
	 * @return the sqlDateValue
	 * @since $version
	 */
	public java.sql.Date getSqlDateValue() {
		return this.sqlDateValue;
	}

	/**
	 * Returns the sqlTimeStampValue.
	 * 
	 * @return the sqlTimeStampValue
	 * @since $version
	 */
	public java.sql.Timestamp getSqlTimeStampValue() {
		return this.sqlTimeStampValue;
	}

	/**
	 * Returns the sqlTimeValue.
	 * 
	 * @return the sqlTimeValue
	 * @since $version
	 */
	public java.sql.Time getSqlTimeValue() {
		return this.sqlTimeValue;
	}

	/**
	 * Returns the stringValue.
	 * 
	 * @return the stringValue
	 * @since $version
	 */
	public String getStringValue() {
		return this.stringValue;
	}

	/**
	 * Returns the booleanValue.
	 * 
	 * @return the booleanValue
	 * @since $version
	 */
	public boolean isBooleanValue() {
		return this.booleanValue;
	}

	/**
	 * Sets the bigDecimalValue.
	 * 
	 * @param bigDecimalValue
	 *            the bigDecimalValue to set
	 * @since $version
	 */
	public void setBigDecimalValue(BigDecimal bigDecimalValue) {
		this.bigDecimalValue = bigDecimalValue;
	}

	/**
	 * Sets the bigIntegerValue.
	 * 
	 * @param bigIntegerValue
	 *            the bigIntegerValue to set
	 * @since $version
	 */
	public void setBigIntegerValue(BigInteger bigIntegerValue) {
		this.bigIntegerValue = bigIntegerValue;
	}

	/**
	 * Sets the booleanObjectValue.
	 * 
	 * @param booleanObjectValue
	 *            the booleanObjectValue to set
	 * @since $version
	 */
	public void setBooleanObjectValue(Boolean booleanObjectValue) {
		this.booleanObjectValue = booleanObjectValue;
	}

	/**
	 * Sets the booleanValue.
	 * 
	 * @param booleanValue
	 *            the booleanValue to set
	 * @since $version
	 */
	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	/**
	 * Sets the byteObjectValue.
	 * 
	 * @param byteObjectValue
	 *            the byteObjectValue to set
	 * @since $version
	 */
	public void setByteObjectValue(Byte byteObjectValue) {
		this.byteObjectValue = byteObjectValue;
	}

	/**
	 * Sets the byteValue.
	 * 
	 * @param byteValue
	 *            the byteValue to set
	 * @since $version
	 */
	public void setByteValue(byte byteValue) {
		this.byteValue = byteValue;
	}

	/**
	 * Sets the calendarValue.
	 * 
	 * @param calendarValue
	 *            the calendarValue to set
	 * @since $version
	 */
	public void setCalendarValue(Calendar calendarValue) {
		this.calendarValue = calendarValue;
	}

	/**
	 * Sets the dateValue.
	 * 
	 * @param dateValue
	 *            the dateValue to set
	 * @since $version
	 */
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	/**
	 * Sets the doubleObjectValue.
	 * 
	 * @param doubleObjectValue
	 *            the doubleObjectValue to set
	 * @since $version
	 */
	public void setDoubleObjectValue(Double doubleObjectValue) {
		this.doubleObjectValue = doubleObjectValue;
	}

	/**
	 * Sets the doubleValue.
	 * 
	 * @param doubleValue
	 *            the doubleValue to set
	 * @since $version
	 */
	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}

	/**
	 * Sets the enumValue.
	 * 
	 * @param enumValue
	 *            the enumValue to set
	 * @since $version
	 */
	public void setEnumValue(Enum<E> enumValue) {
		this.enumValue = enumValue;
	}

	/**
	 * Sets the floatObjectValue.
	 * 
	 * @param floatObjectValue
	 *            the floatObjectValue to set
	 * @since $version
	 */
	public void setFloatObjectValue(Float floatObjectValue) {
		this.floatObjectValue = floatObjectValue;
	}

	/**
	 * Sets the floatValue.
	 * 
	 * @param floatValue
	 *            the floatValue to set
	 * @since $version
	 */
	public void setFloatValue(float floatValue) {
		this.floatValue = floatValue;
	}

	/**
	 * Sets the intObjectValue.
	 * 
	 * @param intObjectValue
	 *            the intObjectValue to set
	 * @since $version
	 */
	public void setIntObjectValue(Integer intObjectValue) {
		this.intObjectValue = intObjectValue;
	}

	/**
	 * Sets the intValue.
	 * 
	 * @param intValue
	 *            the intValue to set
	 * @since $version
	 */
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	/**
	 * Sets the key.
	 * 
	 * @param key
	 *            the key to set
	 * @since $version
	 */
	public void setKey(Integer key) {
		this.key = key;
	}

	/**
	 * Sets the longObjectValue.
	 * 
	 * @param longObjectValue
	 *            the longObjectValue to set
	 * @since $version
	 */
	public void setLongObjectValue(Long longObjectValue) {
		this.longObjectValue = longObjectValue;
	}

	/**
	 * Sets the longValue.
	 * 
	 * @param longValue
	 *            the longValue to set
	 * @since $version
	 */
	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}

	/**
	 * Sets the shortObjectValue.
	 * 
	 * @param shortObjectValue
	 *            the shortObjectValue to set
	 * @since $version
	 */
	public void setShortObjectValue(Short shortObjectValue) {
		this.shortObjectValue = shortObjectValue;
	}

	/**
	 * Sets the shortValue.
	 * 
	 * @param shortValue
	 *            the shortValue to set
	 * @since $version
	 */
	public void setShortValue(short shortValue) {
		this.shortValue = shortValue;
	}

	/**
	 * Sets the sqlDateValue.
	 * 
	 * @param sqlDateValue
	 *            the sqlDateValue to set
	 * @since $version
	 */
	public void setSqlDateValue(java.sql.Date sqlDateValue) {
		this.sqlDateValue = sqlDateValue;
	}

	/**
	 * Sets the sqlTimeStampValue.
	 * 
	 * @param sqlTimeStampValue
	 *            the sqlTimeStampValue to set
	 * @since $version
	 */
	public void setSqlTimeStampValue(java.sql.Timestamp sqlTimeStampValue) {
		this.sqlTimeStampValue = sqlTimeStampValue;
	}

	/**
	 * Sets the sqlTimeValue.
	 * 
	 * @param sqlTimeValue
	 *            the sqlTimeValue to set
	 * @since $version
	 */
	public void setSqlTimeValue(java.sql.Time sqlTimeValue) {
		this.sqlTimeValue = sqlTimeValue;
	}

	/**
	 * Sets the stringValue.
	 * 
	 * @param stringValue
	 *            the stringValue to set
	 * @since $version
	 */
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

}
