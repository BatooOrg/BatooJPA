package org.batoo.jpa.spec.impl.converter;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * 
 * @Target({METHOD, FIELD}) @Retention(RUNTIME)
 *                  public @interface Column {
 *                  String name() default "";
 *                  boolean unique() default false;
 *                  boolean nullable() default true;
 *                  boolean insertable() default true;
 *                  boolean updatable() default true;
 *                  String columnDefinition() default "";
 *                  String table() default "";
 *                  int length() default 255;
 *                  int precision() default 0; // decimal precision
 *                  int scale() default 0; // decimal scale
 *                  }
 * 
 * 
 * 
 *                  <p>
 *                  Java class for column complex type.
 * 
 *                  <p>
 *                  The following schema fragment specifies the expected content contained within this class.
 * 
 *                  <pre>
 * &lt;complexType name="column">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="unique" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="nullable" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="insertable" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="updatable" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="column-definition" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="table" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="length" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="precision" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="scale" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "column")
public class Column implements javax.persistence.Column {

	@XmlAttribute
	protected String name;
	@XmlAttribute
	protected Boolean unique;
	@XmlAttribute
	protected Boolean nullable;
	@XmlAttribute
	protected Boolean insertable;
	@XmlAttribute
	protected Boolean updatable;
	@XmlAttribute(name = "column-definition")
	protected String columnDefinition;
	@XmlAttribute
	protected String table;
	@XmlAttribute
	protected Integer length;
	@XmlAttribute
	protected Integer precision;
	@XmlAttribute
	protected Integer scale;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<? extends Annotation> annotationType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String columnDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the value of the columnDefinition property.
	 * 
	 * @return
	 *         possible object is {@link String }
	 * 
	 */
	public String getColumnDefinition() {
		return this.columnDefinition;
	}

	/**
	 * Gets the value of the insertable property.
	 * 
	 * @return
	 *         possible object is {@link Boolean }
	 * 
	 */
	public Boolean getInsertable() {
		return this.insertable;
	}

	/**
	 * Gets the value of the length property.
	 * 
	 * @return
	 *         possible object is {@link Integer }
	 * 
	 */
	public Integer getLength() {
		return this.length;
	}

	/**
	 * Gets the value of the name property.
	 * 
	 * @return
	 *         possible object is {@link String }
	 * 
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the value of the nullable property.
	 * 
	 * @return
	 *         possible object is {@link Boolean }
	 * 
	 */
	public Boolean getNullable() {
		return this.nullable;
	}

	/**
	 * Gets the value of the precision property.
	 * 
	 * @return
	 *         possible object is {@link Integer }
	 * 
	 */
	public Integer getPrecision() {
		return this.precision;
	}

	/**
	 * Gets the value of the scale property.
	 * 
	 * @return
	 *         possible object is {@link Integer }
	 * 
	 */
	public Integer getScale() {
		return this.scale;
	}

	/**
	 * Gets the value of the table property.
	 * 
	 * @return
	 *         possible object is {@link String }
	 * 
	 */
	public String getTable() {
		return this.table;
	}

	/**
	 * Gets the value of the unique property.
	 * 
	 * @return
	 *         possible object is {@link Boolean }
	 * 
	 */
	public Boolean getUnique() {
		return this.unique;
	}

	/**
	 * Gets the value of the updatable property.
	 * 
	 * @return
	 *         possible object is {@link Boolean }
	 * 
	 */
	public Boolean getUpdatable() {
		return this.updatable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean insertable() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int length() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean nullable() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int precision() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int scale() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Sets the value of the columnDefinition property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setColumnDefinition(String value) {
		this.columnDefinition = value;
	}

	/**
	 * Sets the value of the insertable property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setInsertable(Boolean value) {
		this.insertable = value;
	}

	/**
	 * Sets the value of the length property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setLength(Integer value) {
		this.length = value;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setName(String value) {
		this.name = value;
	}

	/**
	 * Sets the value of the nullable property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setNullable(Boolean value) {
		this.nullable = value;
	}

	/**
	 * Sets the value of the precision property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setPrecision(Integer value) {
		this.precision = value;
	}

	/**
	 * Sets the value of the scale property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setScale(Integer value) {
		this.scale = value;
	}

	/**
	 * Sets the value of the table property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTable(String value) {
		this.table = value;
	}

	/**
	 * Sets the value of the unique property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setUnique(Boolean value) {
		this.unique = value;
	}

	/**
	 * Sets the value of the updatable property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setUpdatable(Boolean value) {
		this.updatable = value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String table() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean unique() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean updatable() {
		// TODO Auto-generated method stub
		return false;
	}

}
