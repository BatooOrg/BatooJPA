package org.batoo.jpa.core.test.inheritence.complex;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@DiscriminatorValue("STU")
public class Student extends Person {

	private Float gpa;

	@ManyToMany(fetch = FetchType.EAGER)
	private final List<Course> enrolled = new ArrayList<Course>();

	/**
	 * 
	 * @since $version
	 */
	public Student() {
		super();
	}

	/**
	 * @param firstName
	 *            the first name
	 * @param lastName
	 *            the last name
	 * 
	 * @since $version
	 */
	public Student(String firstName, String lastName) {
		super(firstName, lastName);
	}

	/**
	 * Returns the enrolled of the Student.
	 * 
	 * @return the enrolled of the Student
	 * 
	 * @since $version
	 */
	public List<Course> getEnrolled() {
		return this.enrolled;
	}

	/**
	 * Returns the gpa of the Student.
	 * 
	 * @return the gpa of the Student
	 * 
	 * @since $version
	 */
	public Float getGpa() {
		return this.gpa;
	}

	/**
	 * Sets the gpa of the Student.
	 * 
	 * @param gpa
	 *            the gpa to set for Student
	 * 
	 * @since $version
	 */
	public void setGpa(Float gpa) {
		this.gpa = gpa;
	}
}
