/*
 * Copyright 2011, Mysema Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.batoo.jpa.community.test.t5;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The Class Animal.
 */
@Entity
@Table(name = "animal_")
@DiscriminatorValue("A")
@SuppressWarnings("javadoc")
public class Animal {
	private boolean alive;

	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date birthdate;

	private int weight, toes;

	private double bodyWeight;

	private float floatProperty;

	private Color color;

	private java.sql.Date dateField;

	@Id
	private int id;

	private String name;

	private java.sql.Time timeField;

	public Animal() {
	}

	public Animal(int id) {
		this.setId(id);
	}

	public java.util.Date getBirthdate() {
		return new Date(this.birthdate.getTime());
	}

	public double getBodyWeight() {
		return this.bodyWeight;
	}

	public Color getColor() {
		return this.color;
	}

	public java.sql.Date getDateField() {
		return new java.sql.Date(this.dateField.getTime());
	}

	public float getFloatProperty() {
		return this.floatProperty;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public java.sql.Time getTimeField() {
		return this.timeField;
	}

	public int getToes() {
		return this.toes;
	}

	public int getWeight() {
		return this.weight;
	}

	public boolean isAlive() {
		return this.alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public void setBirthdate(java.util.Date birthdate) {
		this.birthdate = new java.util.Date(birthdate.getTime());
	}

	public void setBodyWeight(double bodyWeight) {
		this.bodyWeight = bodyWeight;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setDateField(java.sql.Date dateField) {
		this.dateField = new java.sql.Date(dateField.getTime());
	}

	public void setFloatProperty(float floatProperty) {
		this.floatProperty = floatProperty;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTimeField(java.sql.Time timeField) {
		this.timeField = timeField;
	}

	public void setToes(int toes) {
		this.toes = toes;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

}
