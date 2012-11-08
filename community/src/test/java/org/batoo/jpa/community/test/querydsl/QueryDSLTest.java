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

import java.util.Arrays;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.junit.Test;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class QueryDSLTest extends BaseCoreTest {

	/**
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testQueryDSL() {
		// this.cq("select employee\n" + //
		// "from Employee employee\n" + //
		// "where exists (select employee34b11\n" + //
		// "from Employee employee34b11\n" + //
		// "  inner join employee34b11.jobFunctions as employee_jobFunctionse0181\n" + //
		// "where employee34b11 = employee and employee_jobFunctionse0181 in ?1)");

		// this.cq("select cat\n" + //
		// "from Cat cat\n" + //
		// "inner join fetch cat.mate as mate");
		//
		// this.cq("select count(cat)\n" + "from Cat cat\n" + "where cat.id in ?1");
		//
		// this.cq("select cat.name, otherCat.name\n" + //
		// "from Cat cat, Cat otherCat\n" + //
		// "where length(cat.name) > ?1 and length(otherCat.name) > ?1 and not (cat.name is null) and not otherCat.kittens is empty");
		//
		// this.cq("select count(cat)\n" + //
		// "from Cat cat, Cat otherCat\n" + //
		// "where length(cat.name) > ?1 and length(otherCat.name) > ?1 and (cat.name is null or otherCat.kittens is empty)") //
		// .setParameter(1, 1).getResultList();

		// this.cq("select count(cat)\n" + //
		// "from Cat cat, Cat otherCat\n" + //
		// "  left join cat.kittens as cat_kittens_0\n" + //
		// "where index(cat_kittens_0) = ?1 and length(cat.name) > ?1 and length(otherCat.name) > ?1 and cat_kittens_0 = ?2") //
		// .setParameter(1, 0).setParameter(2, new Cat()).getResultList();

		// this.cq("select count(show)\n" + //
		// "from Show show\n" + //
		// "where containsKey(show.acts,?1)");

		this.cq("select c from Cat c where c.id in (?1)")//
		// .setParameter(1, 0)//
		.setParameter(1, Arrays.asList(1, 2))//
		// .setParameter(3, 10)//
		.getResultList();
	}
}
