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
package org.batoo.jpa.core.impl.deployment;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.metamodel.ManagedType;

import org.batoo.common.BatooException;
import org.batoo.common.log.BLogger;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.TypeImpl;
import org.batoo.jpa.core.util.IncrementalNamingThreadFactory;
import org.batoo.jpa.parser.metadata.NamedQueryMetadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Abstract base class for Deployment Managers.
 * <p>
 * Facilitates a unique exception handling and thread management.
 * 
 * @param <X>
 *            the base type for the operation
 * 
 * @author hceylan
 * @since 2.0.0
 */
public abstract class DeploymentManager<X> {

	/**
	 * The context for the operation
	 * 
	 * @author hceylan
	 * @since 2.0.0
	 */
	protected enum Context {
		/**
		 * Perform for all the managed types
		 */
		MANAGED_TYPES,

		/**
		 * Perform for all the identifiable types
		 */
		IDENTIFIABLE_TYPES,

		/**
		 * Perform for entities only
		 */
		ENTITIES,

		/**
		 * Perform for named queries
		 */
		NAMED_QUERIES
	}

	private static class DeploymentUnitFuture extends FutureTask<Void> implements Comparable<DeploymentUnitFuture> {

		private final DeploymentUnitTask task;

		/**
		 * @param task
		 *            the deployment unit task.
		 * 
		 * @since 2.0.0
		 */
		public DeploymentUnitFuture(DeploymentUnitTask task) {
			super(task);

			this.task = task;
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public int compareTo(DeploymentUnitFuture o) {
			return this.task.compareTo(o.task);
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

			if (this.getClass() != obj.getClass()) {
				return false;
			}

			final DeploymentUnitFuture other = (DeploymentUnitFuture) obj;
			if (this.task == null) {
				if (other.task != null) {
					return false;
				}
			}
			else if (!this.task.equals(other.task)) {
				return false;
			}

			return true;
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + ((this.task == null) ? 0 : this.task.hashCode());
			return result;
		}
	}

	private final BLogger log;
	private final MetamodelImpl metamodel;
	private final List<ManagedType<?>> types = Lists.newArrayList();
	private final Collection<NamedQueryMetadata> namedQueries = Lists.newArrayList();

	private final Set<TypeImpl<?>> performed = Collections.synchronizedSet(Sets.<TypeImpl<?>> newHashSet());
	private final ThreadPoolExecutor executer;
	private final Context context;

	/**
	 * @param log
	 *            the log to use
	 * @param name
	 *            the name of the deployment manager.
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @param context
	 *            the context for the operation
	 * @since 2.0.0
	 */
	@SuppressWarnings({ "unchecked" })
	public DeploymentManager(BLogger log, String name, MetamodelImpl metamodel, Context context) {
		super();

		this.log = log;
		this.metamodel = metamodel;
		this.context = context;

		switch (context) {
			case MANAGED_TYPES:
				this.types.addAll(this.metamodel.getManagedTypes());
				break;
			case IDENTIFIABLE_TYPES:
				this.types.addAll(this.metamodel.getIdentifiables());
				break;
			case ENTITIES:
				this.types.addAll(this.metamodel.getEntities());
				break;
			case NAMED_QUERIES:
				this.namedQueries.addAll(this.metamodel.getNamedQueries());
		}

		final int nThreads = Runtime.getRuntime().availableProcessors() * 2;
		this.executer = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(),
			new IncrementalNamingThreadFactory(name)) {
			/**
			 * {@inheritDoc}
			 * 
			 */
			@Override
			protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
				return (RunnableFuture<T>) new DeploymentUnitFuture((DeploymentUnitTask) callable);
			}
		};

		this.log.debug("Number of threads is {0}", nThreads);
	}

	/**
	 * Returns the metamodel.
	 * 
	 * @return the metamodel
	 * @since 2.0.0
	 */
	public MetamodelImpl getMetamodel() {
		return this.metamodel;
	}

	/**
	 * Handles the exception.
	 * 
	 * @param t
	 *            the exception
	 * @throws BatooException
	 * 
	 * @since 2.0.0
	 */
	protected void handleException(Throwable t) throws BatooException {
		if (t instanceof BatooException) {
			throw (BatooException) t;
		}

		if (t.getCause() != null) {
			throw new BatooException("Unknown error occurred during deployment", t.getCause());
		}

		this.handleException(t.getCause());
	}

	/**
	 * Returns if the type has performed.
	 * 
	 * @param type
	 *            the type
	 * @return true if the type has performed
	 * 
	 * @since 2.0.0
	 */
	public boolean hasPerformed(TypeImpl<?> type) {
		if (type == null) {
			return true;
		}

		if (!this.types.contains(type)) {
			return true;
		}

		return this.performed.contains(type);
	}

	/**
	 * Performs the deployment unit for all the types.
	 * 
	 * @since 2.0.0
	 * @throws BatooException
	 */
	protected final void perform() throws BatooException {
		final long start = System.currentTimeMillis();

		// Submit the tasks
		final List<Future<?>> futures = Lists.newArrayList();

		if (this.context == Context.NAMED_QUERIES) {
			for (final NamedQueryMetadata query : this.namedQueries) {
				futures.add(this.executer.submit(new DeploymentUnitTask(this, query)));
			}
		}
		else {
			for (final ManagedType<?> type : this.types) {
				futures.add(this.executer.submit(new DeploymentUnitTask(this, type)));
			}
		}

		// wait until tasks finish or one bails out with an exception
		try {
			for (final Future<?> future : futures) {
				future.get();
			}
		}
		catch (final Exception t) {
			this.handleException(t);
		}
		finally {
			this.executer.shutdownNow();
		}

		this.log.debug("Deployment pass took {0} msecs", System.currentTimeMillis() - start);
	}

	/**
	 * Performs the actual task on the type.
	 * 
	 * @param type
	 *            the type to perform for
	 * @return always null
	 * @throws BatooException
	 *             thrown in case of underlying error
	 * 
	 * @since 2.0.0
	 */
	public abstract Void perform(X type) throws BatooException;

	/**
	 * Marks the type as performed.
	 * 
	 * @param type
	 *            the type to mark
	 * 
	 * @since 2.0.0
	 */
	public void performed(X type) {
		if (!(type instanceof NamedQueryMetadata)) {
			this.performed.add((TypeImpl<?>) type);
		}
	}
}
