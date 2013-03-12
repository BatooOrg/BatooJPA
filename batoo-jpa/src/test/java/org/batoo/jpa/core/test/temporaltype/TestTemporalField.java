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
package org.batoo.jpa.core.test.temporaltype;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * Test for the issue calendar Temporal Field
 * 
 * @author ylemoigne
 * @since $version
 */
public class TestTemporalField extends BaseCoreTest {

	/**
	 * Test for temporal Calendar and date type
	 */
	@Test
	public void testTemporal() {
		final Calendar testStartInstant = Calendar.getInstance();

		final TemporalFieldEntity mainEntity = new TemporalFieldEntity();
		mainEntity.setCalendar(testStartInstant);

		mainEntity.setDate(testStartInstant.getTime());

		persist(mainEntity);
		this.commit();

		this.close();

		final TemporalFieldEntity mainEntityReloaded = find(TemporalFieldEntity.class, mainEntity.getId());
		assertEquals(mainEntity.getId(), mainEntityReloaded.getId());
		assertEquals(testStartInstant.getTime(), mainEntityReloaded.getDate());
		assertEquals(testStartInstant, mainEntityReloaded.getCalendar());
	}
}
