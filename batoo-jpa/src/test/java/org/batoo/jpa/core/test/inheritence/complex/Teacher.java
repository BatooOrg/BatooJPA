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

package org.batoo.jpa.core.test.inheritence.complex;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * 
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
@DiscriminatorValue("TEA")
public class Teacher extends Person {

	@OneToMany(mappedBy = "teacher")
	private List<Course> coursesTaught;

	/**
	 * 
	 * @since 2.0.0
	 */
	public Teacher() {
		super();
	}

	/**
	 * @param firstName
	 *            the first name
	 * @param lastName
	 *            the last name
	 * 
	 * @since 2.0.0
	 */
	public Teacher(String firstName, String lastName) {
		super(firstName, lastName);
	}

	/**
	 * Returns the coursesTaught of the Teacher.
	 * 
	 * @return the coursesTaught of the Teacher
	 * 
	 * @since 2.0.0
	 */
	public List<Course> getCoursesTaught() {
		return this.coursesTaught;
	}

	/**
	 * Sets the coursesTaught of the Teacher.
	 * 
	 * @param coursesTaught
	 *            the coursesTaught to set for Teacher
	 * 
	 * @since 2.0.0
	 */
	public void setCoursesTaught(List<Course> coursesTaught) {
		this.coursesTaught = coursesTaught;
	}
}
