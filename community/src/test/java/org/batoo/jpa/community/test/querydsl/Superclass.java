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

import javax.persistence.MappedSuperclass;

import com.mysema.query.annotations.PropertyType;
import com.mysema.query.annotations.QueryType;

@MappedSuperclass
@SuppressWarnings("javadoc")
public class Superclass {
	String superclassProperty;

	@QueryType(PropertyType.SIMPLE)
	private String stringAsSimple;

	public String getStringAsSimple() {
		return this.stringAsSimple;
	}

	public void setStringAsSimple(String stringAsSimple) {
		this.stringAsSimple = stringAsSimple;
	}
}
