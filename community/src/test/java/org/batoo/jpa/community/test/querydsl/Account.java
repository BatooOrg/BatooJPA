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

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.mysema.query.annotations.QueryInit;

/**
 * The Class Account.
 */
@Entity
@Table(name = "account_")
@SuppressWarnings("javadoc")
public class Account implements Serializable {

	@Transient
	public int transientField;

	@Id
	long id;

	@ManyToOne
	@QueryInit("pid")
	Person owner;

	@Embedded
	EmbeddedType embeddedData;
}
