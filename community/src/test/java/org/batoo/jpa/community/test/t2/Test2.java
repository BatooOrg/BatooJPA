/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
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
package org.batoo.jpa.community.test.t2;

import javax.persistence.PersistenceException;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.junit.Test;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class Test2 extends BaseCoreTest {

	/**
	 * Ref: http://stackoverflow.com/questions/12795407/jpa-how-to-select-objects-wich-has-no-multiple-attributes
	 * 
	 * @since $version
	 */
	@Test
	public void test1() {
		this.cq("select ent from OpSubject_vr ent where ent.okved_id_mult is empty");
	}

	/**
	 * Ref: http://stackoverflow.com/questions/12795407/jpa-how-to-select-objects-wich-has-no-multiple-attributes
	 * 
	 * @since $version
	 */
	@Test(expected = PersistenceException.class)
	public void test2() {
		this.begin();

		this.cq("select ent from OpSubject_vr ent where ent.okved_id_mult is null");
	}

	/**
	 * Ref: http://stackoverflow.com/questions/12795407/jpa-how-to-select-objects-wich-has-no-multiple-attributes
	 * 
	 * @since $version
	 */
	@Test
	public void test3() {
		this.cq("select distinct ent from OpSubject_vr ent left join ent.okved_id_mult i1 where i1.code is null");
	}
}
