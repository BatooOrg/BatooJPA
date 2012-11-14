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
public class PerformanceId {

	private EventId event;
	private String performanceCode;

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PerformanceId() {
		super();
	}

	/**
	 * @param event
	 *            the event
	 * @param performanceCode
	 *            the code
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public PerformanceId(EventId event, String performanceCode) {
		super();

		this.event = event;
		this.performanceCode = performanceCode;
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
		if (!(obj instanceof PerformanceId)) {
			return false;
		}
		final PerformanceId other = (PerformanceId) obj;
		if (this.performanceCode == null) {
			if (other.performanceCode != null) {
				return false;
			}
		}
		else if (!this.performanceCode.equals(other.performanceCode)) {
			return false;
		}
		if (this.event == null) {
			if (other.event != null) {
				return false;
			}
		}
		else if (!this.event.equals(other.event)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the event of the PerformanceId.
	 * 
	 * @return the event of the PerformanceId
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EventId getEvent() {
		return this.event;
	}

	/**
	 * Returns the performanceCode of the PerformanceId.
	 * 
	 * @return the performanceCode of the PerformanceId
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getPerformanceCode() {
		return this.performanceCode;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.performanceCode == null) ? 0 : this.performanceCode.hashCode());
		result = (prime * result) + ((this.event == null) ? 0 : this.event.hashCode());
		return result;
	}

	/**
	 * Sets the event of the PerformanceId.
	 * 
	 * @param event
	 *            the event to set for PerformanceId
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setEvent(EventId event) {
		this.event = event;
	}

	/**
	 * Sets the performanceCode of the PerformanceId.
	 * 
	 * @param performanceCode
	 *            the performanceCode to set for PerformanceId
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
		return "PerformanceId [event=" + this.event + ", code=" + this.performanceCode + "]";
	}
}
