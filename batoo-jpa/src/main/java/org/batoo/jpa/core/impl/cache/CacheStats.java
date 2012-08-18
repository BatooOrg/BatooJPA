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
package org.batoo.jpa.core.impl.cache;

import java.text.MessageFormat;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;

/**
 * The statistics repository for the class.
 * 
 * @author hceylan
 * @since $version
 */
public class CacheStats {

	private static final BLogger LOG = BLoggerFactory.getLogger(CacheStats.class);

	private final String name;
	private int evicts;
	private int puts;
	private int hits;
	private int misses;

	/**
	 * @param name
	 *            the name of the stats
	 * 
	 * @since $version
	 * @author hceylan
	 */
	/* package */CacheStats(String name) {
		super();

		this.name = name;
	}

	/**
	 * Increments the evicts counter.
	 * 
	 * @param primaryKey
	 *            the primary key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	/* package */void evict(Object primaryKey) {
		this.evicts++;

		CacheStats.LOG.debug("EVICT {0}:{1} | puts:{2} evicts:{3} hits:{4}, misses:{5}", this.name, primaryKey, this.puts, this.evicts, this.hits, this.misses);
	}

	/**
	 * Returns the number of evicts.
	 * 
	 * @return the number of evicts
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int evicts() {
		return this.evicts;
	}

	/**
	 * Increments the hits counter.
	 * 
	 * @param primaryKey
	 *            the primary key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	/* package */void hit(Object primaryKey) {
		this.hits++;

		CacheStats.LOG.debug("HIT {0}:{1} | puts:{2} evicts:{3} hits:{4}, misses:{5}", this.name, primaryKey, this.puts, this.evicts, this.hits, this.misses);
	}

	/**
	 * Returns the number of hits.
	 * 
	 * @return the number of hits
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int hits() {
		return this.hits;
	}

	/**
	 * Increments the missses counter.
	 * 
	 * @param primaryKey
	 *            the primary key
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	/* package */void miss(Object primaryKey) {
		this.misses++;

		CacheStats.LOG.debug("MISS {0}:{1} | puts:{2} evicts:{3} hits:{4}, misses:{5}", this.name, primaryKey, this.puts, this.evicts, this.hits, this.misses);
	}

	/**
	 * Returns the number of misses.
	 * 
	 * @return the number of misses
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int misses() {
		return this.misses;
	}

	/**
	 * Increments the puts counter.
	 * 
	 * @param primaryKey
	 *            the primary key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	/* package */void put(Object primaryKey) {
		this.puts++;

		CacheStats.LOG.debug("PUT {0}:{1} | puts:{2} evicts:{3} hits:{4}, misses:{5}", this.name, primaryKey, this.puts, this.evicts, this.hits, this.misses);
	}

	/**
	 * Returns the number of puts.
	 * 
	 * @return the number of puts
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int puts() {
		return this.puts;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return MessageFormat.format("{0} | puts:{1} evicts:{2} hits:{3}, misses:{4}", this.name, this.puts, this.evicts, this.hits, this.misses);
	}
}
