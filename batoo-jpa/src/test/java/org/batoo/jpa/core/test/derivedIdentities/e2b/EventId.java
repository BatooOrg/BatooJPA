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

/**
 * 
 * 
 * @author hceylan
 * @since $version
 */
public class EventId {

	private String venue;
	private String eventCode;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EventId() {
		super();
	}

	/**
	 * @param venue
	 *            the venue
	 * @param eventCode
	 *            the code
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EventId(String venue, String eventCode) {
		super();

		this.venue = venue;
		this.eventCode = eventCode;
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
		if (!(obj instanceof EventId)) {
			return false;
		}
		final EventId other = (EventId) obj;
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
	 * Returns the eventCode of the EventId.
	 * 
	 * @return the eventCode of the EventId
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getEventCode() {
		return this.eventCode;
	}

	/**
	 * Returns the venue of the EventId.
	 * 
	 * @return the venue of the EventId
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getVenue() {
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
	 * Sets the eventCode of the EventId.
	 * 
	 * @param eventCode
	 *            the eventCode to set for EventId
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	/**
	 * Sets the venue of the EventId.
	 * 
	 * @param venue
	 *            the venue to set for EventId
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setVenue(String venue) {
		this.venue = venue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "EventId [venue=" + this.venue + ", code=" + this.eventCode + "]";
	}
}
