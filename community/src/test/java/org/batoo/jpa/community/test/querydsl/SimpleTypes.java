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

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "simpletypes_")
@SuppressWarnings("javadoc")
public class SimpleTypes {
	transient int test;

	@Id
	long id;

	BigDecimal bigDecimal;

	Byte bbyte;

	byte bbyte2;

	Character cchar;

	char cchar2;

	Double ddouble;

	double ddouble2;

	Float ffloat;

	float ffloat2;

	Integer iint;

	int iint2;

	Locale llocale;

	Long llong;

	long llong2;

	String sstring;

	@Temporal(TemporalType.DATE)
	Date date;

	java.sql.Time time;

	java.sql.Timestamp timestamp;
}
