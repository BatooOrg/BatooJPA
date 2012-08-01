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
package org.batoo.jpa.core.test.q;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.QueryHint;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.common.collect.Lists;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@NamedQueries(@NamedQuery(name = "theOldestGuys", //
	query = "select p from Person p order by p.age desc",
	hints = { //
	@QueryHint(name = "hint1", value = "value1"), @QueryHint(name = "hint2", value = "value2") }))
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
	private final List<Address> addresses = Lists.newArrayList();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person", fetch = FetchType.EAGER)
	private final List<HomePhone> phones = Lists.newArrayList();

	@OrderColumn
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<WorkPhone> workPhones = Lists.newArrayList();

	private int age;

	private String name;

	@Temporal(TemporalType.DATE)
	private Date startDate;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public Person() {
		super();
	}

	/**
	 * @param name
	 *            the name
	 * @param age
	 *            the age
	 * @param startDate
	 *            the start date
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Person(String name, int age, Date startDate) {
		super();

		this.name = name;
		this.age = age;
		this.startDate = startDate;
	}

	/**
	 * Returns the addresses.
	 * 
	 * @return the addresses
	 * @since $version
	 */
	public List<Address> getAddresses() {
		return this.addresses;
	}

	/**
	 * Returns the age of the Person.
	 * 
	 * @return the age of the Person
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getAge() {
		return this.age;
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
	 * Returns the name.
	 * 
	 * @return the name
	 * @since $version
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the phones.
	 * 
	 * @return the phones
	 * @since $version
	 */
	public List<HomePhone> getPhones() {
		return this.phones;
	}

	/**
	 * Returns the startDate of the Person.
	 * 
	 * @return the startDate of the Person
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Date getStartDate() {
		return this.startDate;
	}

	/**
	 * Returns the workPhones.
	 * 
	 * @return the workPhones
	 * @since $version
	 */
	public List<WorkPhone> getWorkPhones() {
		return this.workPhones;
	}

	/**
	 * Sets the age of the Person.
	 * 
	 * @param age
	 *            the age to set for Person
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the name to set
	 * @since $version
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the startDate of the Person.
	 * 
	 * @param startDate
	 *            the startDate to set for Person
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
