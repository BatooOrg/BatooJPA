/*
 * Copyright (c) 2012 - Batoo Software Software Foundation.
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
package org.batoo.jpa.community.test.querydsl;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * The Class Employee.
 */
@Entity
@Table(name = "employee_")
@SuppressWarnings("javadoc")
public class Employee {

	@ManyToOne
	public Company company;

	@OneToOne
	public User user;

	public String firstName, lastName;

	@Id
	public int id;

	@Enumerated(EnumType.STRING)
	@Column(name = "jobfunction")
	@ElementCollection(fetch = FetchType.EAGER)
	public Collection<JobFunction> jobFunctions = new HashSet<JobFunction>();
}
