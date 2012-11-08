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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "book_")
@SuppressWarnings("javadoc")
public class Book implements Serializable {

	private static final long serialVersionUID = -9029792723035681319L;

	@Id
	@GeneratedValue
	private Long id;

	private String title;

	@ManyToOne
	@JoinColumn(name = "AUTHOR_ID")
	private Author author;

	public Author getAuthor() {
		return this.author;
	}

	public Long getId() {
		return this.id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
