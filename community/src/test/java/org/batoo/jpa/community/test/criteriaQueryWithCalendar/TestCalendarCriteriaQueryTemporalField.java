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
package org.batoo.jpa.community.test.criteriaQueryWithCalendar;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.batoo.jpa.community.test.BaseCoreTest;
import org.batoo.jpa.community.test.NoDatasource;
import org.junit.Test;

/**
 * Test for the issue criteria query on calendarTemporalField
 * 
 * @author ylemoigne
 * @since 2.0.1
 */
@SuppressWarnings("javadoc")
public class TestCalendarCriteriaQueryTemporalField extends BaseCoreTest {
	@Test
	@NoDatasource
	public void testCalendar() {
		final Calendar testStartInstant = Calendar.getInstance();

		final CalendarTemporalFieldEntity mainEntity = new CalendarTemporalFieldEntity();
		mainEntity.setCalendar(testStartInstant);

		mainEntity.setDate(testStartInstant.getTime());

		persist(mainEntity);
		this.commit();

		this.close();

		CriteriaBuilder cBuilder = em().getCriteriaBuilder();
		CriteriaQuery<CalendarTemporalFieldEntity> query = cBuilder.createQuery(CalendarTemporalFieldEntity.class);
		Root<CalendarTemporalFieldEntity> root = query.from(CalendarTemporalFieldEntity.class);
		query.where(cBuilder.equal(root.get(CalendarTemporalFieldEntity_.calendar), testStartInstant));
		query.select(root);
		final CalendarTemporalFieldEntity mainEntityReloaded = em().createQuery(query).getSingleResult();
		
		assertEquals(mainEntity.getId(), mainEntityReloaded.getId());
		assertEquals(testStartInstant.getTime(), mainEntityReloaded.getDate());
		assertEquals(testStartInstant, mainEntityReloaded.getCalendar());
	}
	
	@Test
	@NoDatasource
	public void testTime() {
		final Calendar testStartInstant = Calendar.getInstance();

		final CalendarTemporalFieldEntity mainEntity = new CalendarTemporalFieldEntity();
		mainEntity.setCalendar(testStartInstant);

		mainEntity.setDate(testStartInstant.getTime());

		persist(mainEntity);
		this.commit();

		this.close();

		CriteriaBuilder cBuilder = em().getCriteriaBuilder();
		CriteriaQuery<CalendarTemporalFieldEntity> query = cBuilder.createQuery(CalendarTemporalFieldEntity.class);
		Root<CalendarTemporalFieldEntity> root = query.from(CalendarTemporalFieldEntity.class);
		query.where(cBuilder.equal(root.get(CalendarTemporalFieldEntity_.date), testStartInstant.getTime()));
		query.select(root);
		final CalendarTemporalFieldEntity mainEntityReloaded = em().createQuery(query).getSingleResult();
		
		assertEquals(mainEntity.getId(), mainEntityReloaded.getId());
		assertEquals(testStartInstant.getTime(), mainEntityReloaded.getDate());
		assertEquals(testStartInstant, mainEntityReloaded.getCalendar());
	}
}
