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
@IdClass(EventId.class)
public class Event {

	@Id
	@ManyToOne
	private Venue venue;

	@Id
	private String eventCode;

	private String name;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Event() {
		super();
	}

	/**
	 * @param venue
	 *            the venue
	 * @param eventCode
	 *            the code
	 * @param name
	 *            the name
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Event(Venue venue, String eventCode, String name) {
		super();

		this.venue = venue;
		this.eventCode = eventCode;
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Event)) {
			return false;
		}
		final Event other = (Event) obj;
		if (this.eventCode == null) {
			if (other.eventCode != null) {
				return false;
			}
		}
		else if (!this.eventCode.equals(other.eventCode)) {
			return false;
		}
		if (this.venue == null) {
			if (other.venue != null) {
				return false;
			}
		}
		else if (!this.venue.equals(other.venue)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the eventCode of the Event.
	 * 
	 * @return the eventCode of the Event
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getEventCode() {
		return this.eventCode;
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
	 * Returns the venue of the Event.
	 * 
	 * @return the venue of the Event
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Venue getVenue() {
		return this.venue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.eventCode == null) ? 0 : this.eventCode.hashCode());
		result = (prime * result) + ((this.venue == null) ? 0 : this.venue.hashCode());
		return result;
	}

	/**
	 * Sets the eventCode of the Event.
	 * 
	 * @param eventCode
	 *            the eventCode to set for Event
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
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
	 * Sets the venue of the Event.
	 * 
	 * @param venue
	 *            the venue to set for Event
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "Event [venue=" + this.venue + ", eventCode=" + this.eventCode + ", name=" + this.name + "]";
	}
}
