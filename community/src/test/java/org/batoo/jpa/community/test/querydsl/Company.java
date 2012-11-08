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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.batoo.jpa.annotations.Index;

/**
 * The Class Company.
 */
@Entity
@Table(name = "company_")
@SuppressWarnings("javadoc")
public class Company {

	public enum Rating {
		A,
		AA,
		AAA
	}

	@Enumerated
	public Rating ratingOrdinal;

	@Enumerated(EnumType.STRING)
	public Rating ratingString;

	@ManyToOne
	public Employee ceo;

	@OneToMany
	@Index(name = "_index")
	public List<Department> departments;

	@Id
	public int id;

	public String name;
}
