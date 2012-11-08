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

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "author_")
@SuppressWarnings("javadoc")
public class Author implements Serializable {

	private static final long serialVersionUID = -1893968697250846661L;

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	@OneToMany(mappedBy = "author")
	@OrderBy("title")
	private Set<Book> books;

	public Set<Book> getBooks() {
		return this.books;
	}

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public void setBooks(Set<Book> books) {
		this.books = books;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
}
