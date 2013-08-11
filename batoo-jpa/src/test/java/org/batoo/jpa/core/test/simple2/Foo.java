/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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
 * @since 2.0.0
 */
@Entity
public class Foo {

	@SuppressWarnings("javadoc")
	public enum E {
		ONE,
		TWO;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
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
	 * @since 2.0.0
	 */
	public BigDecimal getBigDecimalValue() {
		return this.bigDecimalValue;
	}

	/**
	 * Returns the bigIntegerValue.
	 * 
	 * @return the bigIntegerValue
	 * @since 2.0.0
	 */
	public BigInteger getBigIntegerValue() {
		return this.bigIntegerValue;
	}

	/**
	 * Returns the booleanObjectValue.
	 * 
	 * @return the booleanObjectValue
	 * @since 2.0.0
	 */
	public Boolean getBooleanObjectValue() {
		return this.booleanObjectValue;
	}

	/**
	 * Returns the byteObjectValue.
	 * 
	 * @return the byteObjectValue
	 * @since 2.0.0
	 */
	public Byte getByteObjectValue() {
		return this.byteObjectValue;
	}

	/**
	 * Returns the byteValue.
	 * 
	 * @return the byteValue
	 * @since 2.0.0
	 */
	public byte getByteValue() {
		return this.byteValue;
	}

	/**
	 * Returns the calendarValue.
	 * 
	 * @return the calendarValue
	 * @since 2.0.0
	 */
	public Calendar getCalendarValue() {
		return this.calendarValue;
	}

	/**
	 * Returns the dateValue.
	 * 
	 * @return the dateValue
	 * @since 2.0.0
	 */
	public Date getDateValue() {
		return this.dateValue;
	}

	/**
	 * Returns the doubleObjectValue.
	 * 
	 * @return the doubleObjectValue
	 * @since 2.0.0
	 */
	public Double getDoubleObjectValue() {
		return this.doubleObjectValue;
	}

	/**
	 * Returns the doubleValue.
	 * 
	 * @return the doubleValue
	 * @since 2.0.0
	 */
	public double getDoubleValue() {
		return this.doubleValue;
	}

	/**
	 * Returns the enumValue.
	 * 
	 * @return the enumValue
	 * @since 2.0.0
	 */
	public Enum<E> getEnumValue() {
		return this.enumValue;
	}

	/**
	 * Returns the floatObjectValue.
	 * 
	 * @return the floatObjectValue
	 * @since 2.0.0
	 */
	public Float getFloatObjectValue() {
		return this.floatObjectValue;
	}

	/**
	 * Returns the floatValue.
	 * 
	 * @return the floatValue
	 * @since 2.0.0
	 */
	public float getFloatValue() {
		return this.floatValue;
	}

	/**
	 * Returns the intObjectValue.
	 * 
	 * @return the intObjectValue
	 * @since 2.0.0
	 */
	public Integer getIntObjectValue() {
		return this.intObjectValue;
	}

	/**
	 * Returns the intValue.
	 * 
	 * @return the intValue
	 * @since 2.0.0
	 */
	public int getIntValue() {
		return this.intValue;
	}

	/**
	 * Returns the key.
	 * 
	 * @return the key
	 * @since 2.0.0
	 */
	public Integer getKey() {
		return this.key;
	}

	/**
	 * Returns the longObjectValue.
	 * 
	 * @return the longObjectValue
	 * @since 2.0.0
	 */
	public Long getLongObjectValue() {
		return this.longObjectValue;
	}

	/**
	 * Returns the longValue.
	 * 
	 * @return the longValue
	 * @since 2.0.0
	 */
	public long getLongValue() {
		return this.longValue;
	}

	/**
	 * Returns the shortObjectValue.
	 * 
	 * @return the shortObjectValue
	 * @since 2.0.0
	 */
	public Short getShortObjectValue() {
		return this.shortObjectValue;
	}

	/**
	 * Returns the shortValue.
	 * 
	 * @return the shortValue
	 * @since 2.0.0
	 */
	public short getShortValue() {
		return this.shortValue;
	}

	/**
	 * Returns the sqlDateValue.
	 * 
	 * @return the sqlDateValue
	 * @since 2.0.0
	 */
	public java.sql.Date getSqlDateValue() {
		return this.sqlDateValue;
	}

	/**
	 * Returns the sqlTimeStampValue.
	 * 
	 * @return the sqlTimeStampValue
	 * @since 2.0.0
	 */
	public java.sql.Timestamp getSqlTimeStampValue() {
		return this.sqlTimeStampValue;
	}

	/**
	 * Returns the sqlTimeValue.
	 * 
	 * @return the sqlTimeValue
	 * @since 2.0.0
	 */
	public java.sql.Time getSqlTimeValue() {
		return this.sqlTimeValue;
	}

	/**
	 * Returns the stringValue.
	 * 
	 * @return the stringValue
	 * @since 2.0.0
	 */
	public String getStringValue() {
		return this.stringValue;
	}

	/**
	 * Returns the booleanValue.
	 * 
	 * @return the booleanValue
	 * @since 2.0.0
	 */
	public boolean isBooleanValue() {
		return this.booleanValue;
	}

	/**
	 * Sets the bigDecimalValue.
	 * 
	 * @param bigDecimalValue
	 *            the bigDecimalValue to set
	 * @since 2.0.0
	 */
	public void setBigDecimalValue(BigDecimal bigDecimalValue) {
		this.bigDecimalValue = bigDecimalValue;
	}

	/**
	 * Sets the bigIntegerValue.
	 * 
	 * @param bigIntegerValue
	 *            the bigIntegerValue to set
	 * @since 2.0.0
	 */
	public void setBigIntegerValue(BigInteger bigIntegerValue) {
		this.bigIntegerValue = bigIntegerValue;
	}

	/**
	 * Sets the booleanObjectValue.
	 * 
	 * @param booleanObjectValue
	 *            the booleanObjectValue to set
	 * @since 2.0.0
	 */
	public void setBooleanObjectValue(Boolean booleanObjectValue) {
		this.booleanObjectValue = booleanObjectValue;
	}

	/**
	 * Sets the booleanValue.
	 * 
	 * @param booleanValue
	 *            the booleanValue to set
	 * @since 2.0.0
	 */
	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	/**
	 * Sets the byteObjectValue.
	 * 
	 * @param byteObjectValue
	 *            the byteObjectValue to set
	 * @since 2.0.0
	 */
	public void setByteObjectValue(Byte byteObjectValue) {
		this.byteObjectValue = byteObjectValue;
	}

	/**
	 * Sets the byteValue.
	 * 
	 * @param byteValue
	 *            the byteValue to set
	 * @since 2.0.0
	 */
	public void setByteValue(byte byteValue) {
		this.byteValue = byteValue;
	}

	/**
	 * Sets the calendarValue.
	 * 
	 * @param calendarValue
	 *            the calendarValue to set
	 * @since 2.0.0
	 */
	public void setCalendarValue(Calendar calendarValue) {
		this.calendarValue = calendarValue;
	}

	/**
	 * Sets the dateValue.
	 * 
	 * @param dateValue
	 *            the dateValue to set
	 * @since 2.0.0
	 */
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	/**
	 * Sets the doubleObjectValue.
	 * 
	 * @param doubleObjectValue
	 *            the doubleObjectValue to set
	 * @since 2.0.0
	 */
	public void setDoubleObjectValue(Double doubleObjectValue) {
		this.doubleObjectValue = doubleObjectValue;
	}

	/**
	 * Sets the doubleValue.
	 * 
	 * @param doubleValue
	 *            the doubleValue to set
	 * @since 2.0.0
	 */
	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}

	/**
	 * Sets the enumValue.
	 * 
	 * @param enumValue
	 *            the enumValue to set
	 * @since 2.0.0
	 */
	public void setEnumValue(Enum<E> enumValue) {
		this.enumValue = enumValue;
	}

	/**
	 * Sets the floatObjectValue.
	 * 
	 * @param floatObjectValue
	 *            the floatObjectValue to set
	 * @since 2.0.0
	 */
	public void setFloatObjectValue(Float floatObjectValue) {
		this.floatObjectValue = floatObjectValue;
	}

	/**
	 * Sets the floatValue.
	 * 
	 * @param floatValue
	 *            the floatValue to set
	 * @since 2.0.0
	 */
	public void setFloatValue(float floatValue) {
		this.floatValue = floatValue;
	}

	/**
	 * Sets the intObjectValue.
	 * 
	 * @param intObjectValue
	 *            the intObjectValue to set
	 * @since 2.0.0
	 */
	public void setIntObjectValue(Integer intObjectValue) {
		this.intObjectValue = intObjectValue;
	}

	/**
	 * Sets the intValue.
	 * 
	 * @param intValue
	 *            the intValue to set
	 * @since 2.0.0
	 */
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	/**
	 * Sets the key.
	 * 
	 * @param key
	 *            the key to set
	 * @since 2.0.0
	 */
	public void setKey(Integer key) {
		this.key = key;
	}

	/**
	 * Sets the longObjectValue.
	 * 
	 * @param longObjectValue
	 *            the longObjectValue to set
	 * @since 2.0.0
	 */
	public void setLongObjectValue(Long longObjectValue) {
		this.longObjectValue = longObjectValue;
	}

	/**
	 * Sets the longValue.
	 * 
	 * @param longValue
	 *            the longValue to set
	 * @since 2.0.0
	 */
	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}

	/**
	 * Sets the shortObjectValue.
	 * 
	 * @param shortObjectValue
	 *            the shortObjectValue to set
	 * @since 2.0.0
	 */
	public void setShortObjectValue(Short shortObjectValue) {
		this.shortObjectValue = shortObjectValue;
	}

	/**
	 * Sets the shortValue.
	 * 
	 * @param shortValue
	 *            the shortValue to set
	 * @since 2.0.0
	 */
	public void setShortValue(short shortValue) {
		this.shortValue = shortValue;
	}

	/**
	 * Sets the sqlDateValue.
	 * 
	 * @param sqlDateValue
	 *            the sqlDateValue to set
	 * @since 2.0.0
	 */
	public void setSqlDateValue(java.sql.Date sqlDateValue) {
		this.sqlDateValue = sqlDateValue;
	}

	/**
	 * Sets the sqlTimeStampValue.
	 * 
	 * @param sqlTimeStampValue
	 *            the sqlTimeStampValue to set
	 * @since 2.0.0
	 */
	public void setSqlTimeStampValue(java.sql.Timestamp sqlTimeStampValue) {
		this.sqlTimeStampValue = sqlTimeStampValue;
	}

	/**
	 * Sets the sqlTimeValue.
	 * 
	 * @param sqlTimeValue
	 *            the sqlTimeValue to set
	 * @since 2.0.0
	 */
	public void setSqlTimeValue(java.sql.Time sqlTimeValue) {
		this.sqlTimeValue = sqlTimeValue;
	}

	/**
	 * Sets the stringValue.
	 * 
	 * @param stringValue
	 *            the stringValue to set
	 * @since 2.0.0
	 */
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

}
