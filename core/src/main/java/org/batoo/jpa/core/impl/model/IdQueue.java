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
package org.batoo.jpa.core.impl.model;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;

/**
 * 
 * @author hceylan
 * @since $version
 */
public abstract class IdQueue extends LinkedBlockingQueue<Long> {

	/**
	 * 
	 * @author hceylan
	 * @since $version
	 */
	private final class TopUpTask implements Runnable {

		@Override
		public void run() {
			IdQueue.this.doTopUp(this);
		}
	}

	private static final long serialVersionUID = 1L;

	private static final BLogger LOG = BLoggerFactory.getLogger(IdQueue.class);

	private final String name;
	private final int allocationSize;

	/**
	 * @param idExecuter
	 *            the executor service to submit refill tasks
	 * @param name
	 *            the physical name of the sequence or pkvalue of the table
	 * @param allocationSize
	 *            the allocations size
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdQueue(ExecutorService idExecuter, String name, int allocationSize) {
		super();

		this.name = name;
		this.allocationSize = allocationSize;

		idExecuter.execute(new TopUpTask());
	}

	/**
	 * Tops up the queue.
	 * 
	 * @param runnable
	 *            the runnable to run to top-up
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void doTopUp(Runnable runnable) {
		while (true) {
			if (Thread.currentThread().isInterrupted()) {
				return;
			}

			if (this.size() <= (this.allocationSize)) {
				IdQueue.LOG.debug("Ids will be fetched for {0} from the database...", this.name);

				try {
					final long nextSequence = this.getNextId();
					for (int i = 0; i < this.allocationSize; i++) {
						this.put(nextSequence + i);
					}
				}
				catch (final InterruptedException e) {
					return;
				}
				catch (final Throwable e) {
					if (Thread.currentThread().isInterrupted()) {
						return;
					}

					IdQueue.LOG.fatal(e, "Cannot get next id from the database!");
				}
			}

			try {
				Thread.sleep(1);
			}
			catch (final InterruptedException e) {
				return;
			}
		}
	}

	/**
	 * Returns the next value from the database.
	 * 
	 * @return the next id
	 * @throws SQLException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected abstract Long getNextId() throws SQLException;

}
