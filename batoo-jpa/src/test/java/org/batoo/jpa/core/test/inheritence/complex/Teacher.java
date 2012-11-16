package org.batoo.jpa.core.test.inheritence.complex;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@DiscriminatorValue("TEA")
public class Teacher extends Person {

	@OneToMany(mappedBy = "teacher")
	private List<Course> coursesTaught;

	/**
	 * 
	 * @since $version
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
	 * @since $version
	 */
	public Teacher(String firstName, String lastName) {
		super(firstName, lastName);
	}

	/**
	 * Returns the coursesTaught of the Teacher.
	 * 
	 * @return the coursesTaught of the Teacher
	 * 
	 * @since $version
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
	 * @since $version
	 */
	public void setCoursesTaught(List<Course> coursesTaught) {
		this.coursesTaught = coursesTaught;
	}
}
