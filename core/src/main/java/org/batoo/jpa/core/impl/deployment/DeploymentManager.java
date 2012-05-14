/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core.impl.deployment;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.metamodel.ManagedType;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.metamodel.ManagedTypeImpl;
import org.batoo.jpa.core.impl.metamodel.TypeImpl;
import org.batoo.jpa.core.util.IncrementalNamingThreadFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Abstract base class for Deployment Managers.
 * <p>
 * Facilitates a unique exception handling and thread management.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class DeploymentManager<X extends ManagedTypeImpl<?>> {

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
		ENTITIES
	}

	private final BLogger log;
	private final MetamodelImpl metamodel;
	private final List<ManagedType<?>> types = Lists.newArrayList();

	private final Set<TypeImpl<?>> performed = Sets.newHashSet();
	private final ThreadPoolExecutor executer;
	private final Context context;

	/**
	 * @param name
	 *            the name of the deployment manager.
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @param context
	 *            the context for the operation
	 * @since $version
	 * @author hceylan
	 * @param log
	 */
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
				this.types.addAll(this.metamodel.getIdentifiableTypes());
				break;
			default:
				this.types.addAll(this.metamodel.getEntities());
		}

		final int nThreads = Runtime.getRuntime().availableProcessors() * 2;
		this.executer = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(),
			new IncrementalNamingThreadFactory(name));

		this.log.debug("Number of threads is {0}", nThreads);
	}

	/**
	 * Returns the metamodel.
	 * 
	 * @return the metamodel
	 * @since $version
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
	 * @since $version
	 * @author hceylan
	 */
	protected void handleException(Throwable t) throws BatooException {
		if (t instanceof BatooException) {
			throw (BatooException) t;
		}

		if (t.getCause() == null) {
			throw new BatooException("Unknown error occurred during deployment", t);
		}

		this.handleException(t.getCause());
	}

	/**
	 * Returns if the type has performed.
	 * 
	 * @return true if the type has performed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean hasPerformed(TypeImpl<?> type) {
		return this.performed.contains(type);
	}

	/**
	 * Performs the deployment unit for all the types.
	 * 
	 * @since $version
	 * @author hceylan
	 * @throws BatooException
	 */
	protected final void perform() throws BatooException {
		final long start = System.currentTimeMillis();

		// Submit the tasks
		final List<Future<?>> futures = Lists.newArrayList();
		for (final ManagedType<?> type : this.types) {
			futures.add(this.executer.submit(new DeploymentUnitTask(this, (ManagedTypeImpl<?>) type, this.context != Context.ENTITIES)));
		}

		// wait until tasks finish or one bails out with an exception
		try {
			for (final Future<?> future : futures) {
				future.get();
			}
		}
		catch (final Throwable t) {
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
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract Void perform(X type) throws BatooException;

	/**
	 * Marks the type as performed.
	 * 
	 * @param type
	 *            the type to mark
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performed(TypeImpl<?> type) {
		this.performed.add(type);
	}

}
