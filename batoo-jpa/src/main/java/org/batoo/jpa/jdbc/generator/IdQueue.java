/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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
package org.batoo.jpa.jdbc.generator;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class IdQueue extends LinkedBlockingQueue<Long> {

	/**
	 * 
	 * @author hceylan
	 * @since 2.0.0
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

	private final ExecutorService idExecuter;

	/**
	 * @param idExecuter
	 *            the executor service to submit refill tasks
	 * @param name
	 *            the physical name of the sequence or pkvalue of the table
	 * @param allocationSize
	 *            the allocations size
	 * 
	 * @since 2.0.0
	 */
	public IdQueue(ExecutorService idExecuter, String name, int allocationSize) {
		super();

		this.idExecuter = idExecuter;
		this.name = name;
		this.allocationSize = allocationSize;

		this.idExecuter.execute(new TopUpTask());
	}

	/**
	 * Tops up the queue.
	 * 
	 * @param runnable
	 *            the runnable to run to top-up
	 * 
	 * @since 2.0.0
	 */
	protected void doTopUp(Runnable runnable) {
		if (this.idExecuter.isShutdown()) {
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
			catch (final Exception e) {
				try {
					Thread.currentThread();
					Thread.sleep(1000);
				}
				catch (final InterruptedException e1) {}

				if (this.idExecuter.isShutdown()) {
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

	/**
	 * Returns the next value from the database.
	 * 
	 * @return the next id
	 * @throws SQLException
	 * 
	 * @since 2.0.0
	 */
	protected abstract Long getNextId() throws SQLException;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Long poll(long timeout, TimeUnit unit) throws InterruptedException {
		if (this.size() < (this.allocationSize * 2)) {
			this.idExecuter.execute(new TopUpTask());
		}

		return super.poll(timeout, unit);
	}
}
