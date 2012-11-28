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

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The Class Bar.
 */
@Entity
@Table(name = "foo_")
@SuppressWarnings("javadoc")
public class Foo {
	public String bar;

	@Id
	public int id;

	@ElementCollection
	@CollectionTable(name = "foo_names", joinColumns = { @JoinColumn(name = "foo_id") })
	public List<String> names;

	@Temporal(TemporalType.DATE)
	public java.util.Date startDate;
}
