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
package org.batoo.jpa.core.test.derivedIdentities.e2b;

import junit.framework.Assert;

import org.batoo.jpa.core.test.BaseCoreTest;
import org.junit.Test;

/**
 * JPA Spec 2.4.1.3 test.
 * 
 * @author asimarslan
 * @since $version
 */
public class DerivedIdsTest extends BaseCoreTest {

	/**
	 * 
	 * Example-2 Case (a):
	 * <p>
	 * The parent entity uses IdClass.
	 * <p>
	 * The dependent entity uses IdClass.
	 * 
	 * @author asimarslan
	 * @since $version
	 */
	@Test
	public void test2b() {
		final Venue venue = new Venue("IN", "Inonu Stadium");
		final Event event = new Event(venue, "BJK12", "Beşiktaş 2012 - 2013 Season");
		final Performance performance = new Performance(event, "001", "Beşiktaş JK - Barcelona FC");

		this.persist(venue);
		this.persist(event);
		this.persist(performance);

		this.commit();
		this.close();

		final Performance performance2 = this.cq("select p FROM Performance p WHERE p.event.venue.venueCode = 'IN'", Performance.class).getSingleResult();

		Assert.assertEquals(performance, performance2);
	}
}
