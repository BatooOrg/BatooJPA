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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 * 
 * Entity class for courses that can be taken. Consists of a title and a code and is assigned a teacher object that teaches that course. It
 * also has a set of students that are enrolled in that course.
 * 
 * @author Andy Gibson
 * 
 */
@Entity
public class Course extends BaseEntity {

	@Column(length = 32, nullable = false)
	private String title;

	@Column(length = 8, nullable = false)
	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	private Teacher teacher;

	@ManyToMany(mappedBy = "enrolled")
	private List<Student> students = new ArrayList<Student>();

	/**
	 * 
	 * @since 2.0.0
	 */
	public Course() {
		super();
	}

	/**
	 * Returns the code of the Course.
	 * 
	 * @return the code of the Course
	 * 
	 * @since 2.0.0
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getDisplayText() {
		return this.getTitle();
	}

	/**
	 * Returns the students of the Course.
	 * 
	 * @return the students of the Course
	 * 
	 * @since 2.0.0
	 */
	public List<Student> getStudents() {
		return this.students;
	}

	/**
	 * Returns the teacher of the Course.
	 * 
	 * @return the teacher of the Course
	 * 
	 * @since 2.0.0
	 */
	public Teacher getTeacher() {
		return this.teacher;
	}

	/**
	 * Returns the title of the Course.
	 * 
	 * @return the title of the Course
	 * 
	 * @since 2.0.0
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Sets the code of the Course.
	 * 
	 * @param code
	 *            the code to set for Course
	 * 
	 * @since 2.0.0
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Sets the students of the Course.
	 * 
	 * @param students
	 *            the students to set for Course
	 * 
	 * @since 2.0.0
	 */
	public void setStudents(List<Student> students) {
		this.students = students;
	}

	/**
	 * Sets the teacher of the Course.
	 * 
	 * @param teacher
	 *            the teacher to set for Course
	 * 
	 * @since 2.0.0
	 */
	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	/**
	 * Sets the title of the Course.
	 * 
	 * @param title
	 *            the title to set for Course
	 * 
	 * @since 2.0.0
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}
