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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
@Entity
@IdClass(PerformanceId.class)
public class Performance {

	@Id
	@ManyToOne
	private Event event;

	@Id
	private String performanceCode;

	private String name;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Performance() {
		super();
	}

	/**
	 * @param event
	 *            the event
	 * @param performanceCode
	 *            the code
	 * @param name
	 *            the name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Performance(Event event, String performanceCode, String name) {
		super();

		this.event = event;
		this.performanceCode = performanceCode;
		this.name = name;
	}

	/**
	 * Returns the event of the Performance.
	 * 
	 * @return the event of the Performance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Event getEvent() {
		return this.event;
	}

	/**
	 * Returns the name of the Event.
	 * 
	 * @return the name of the Event
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the performanceCode of the Performance.
	 * 
	 * @return the performanceCode of the Performance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getPerformanceCode() {
		return this.performanceCode;
	}

	/**
	 * Sets the event of the Performance.
	 * 
	 * @param event
	 *            the event to set for Performance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * Sets the name of the Event.
	 * 
	 * @param name
	 *            the name to set for Event
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the performanceCode of the Performance.
	 * 
	 * @param performanceCode
	 *            the performanceCode to set for Performance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setPerformanceCode(String performanceCode) {
		this.performanceCode = performanceCode;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Performance [event=" + this.event + ", eventCode=" + this.performanceCode + ", name=" + this.name + "]";
	}
}
