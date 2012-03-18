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
package org.batoo.jpa.core.impl.manager;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.metamodel.EntityType;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;

import com.google.common.collect.Lists;

/**
 * A Manager that links persistent classes horizontally
 * 
 * @author hceylan
 * @since $version
 */
public class HLinkerManager extends DeploymentManager {

	private class LinkerThreadFactory implements ThreadFactory {

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "Batoo Linker [" + HLinkerManager.this.nextThreadNo.incrementAndGet() + "]");
		}
	}

	private class LinkTask implements Runnable {

		private final EntityTypeImpl<?> entity;
		private final boolean basic;

		private LinkTask(EntityTypeImpl<?> entity, boolean basic) {
			this.entity = entity;
			this.basic = basic;
		}

		@Override
		public void run() {
			try {
				this.entity.link(HLinkerManager.this.datasource, this.basic);
			}
			catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static final BLogger LOG = BLogger.getLogger(HLinkerManager.class);

	public static void link(MetamodelImpl metamodel, DataSourceImpl datasource) throws BatooException {
		new HLinkerManager(metamodel, datasource, true).link();
		new HLinkerManager(metamodel, datasource, false).link();
	}

	private final AtomicInteger nextThreadNo = new AtomicInteger(0);
	private final ThreadPoolExecutor executer;
	private final MetamodelImpl metamodel;
	private final DataSourceImpl datasource;
	private final boolean basic;

	private HLinkerManager(MetamodelImpl metamodel, DataSourceImpl datasource, boolean basic) {
		super();

		this.metamodel = metamodel;
		this.datasource = datasource;
		this.basic = basic;

		final int nThreads = Runtime.getRuntime().availableProcessors() * 2;
		this.executer = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads, new LinkerThreadFactory());

		LOG.debug("Number of linker threads is {0}", nThreads);
	}

	private void link() throws BatooException {
		final long start = System.currentTimeMillis();

		final Set<EntityType<?>> entities = this.metamodel.getEntities();
		final List<Future<?>> futures = this.linkTypes(entities);

		try {
			this.link(futures);
		}
		finally {
			this.executer.shutdownNow();
		}

		LOG.debug("Horizontal linking took {0} msecs", System.currentTimeMillis() - start);
	}

	private void link(final List<Future<?>> futures) throws BatooException {
		for (final Future<?> future : futures) {
			try {
				future.get();
			}
			catch (final Throwable t) {
				this.handleException(t);
			}
		}
	}

	private List<Future<?>> linkTypes(Set<EntityType<?>> entities) {
		final List<Future<?>> futures = Lists.newArrayList();

		for (final EntityType<?> entity : entities) {
			final Future<?> future = this.executer.submit(new LinkTask((EntityTypeImpl<?>) entity, this.basic));
			futures.add(future);
		}

		return futures;
	}

}
