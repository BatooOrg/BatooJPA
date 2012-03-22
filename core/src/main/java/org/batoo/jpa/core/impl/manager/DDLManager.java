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
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Type;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;
import org.batoo.jpa.core.jdbc.DDLMode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * A Manager that performs the DDL operations.
 * 
 * @author hceylan
 * @since $version
 */
public class DDLManager extends DeploymentManager {

	private class PerformDDLTask implements Runnable, Comparable<Type<?>> {

		private final EntityTypeImpl<?> type;
		private final Set<String> schemas;
		private final boolean firstPass;

		private PerformDDLTask(Set<String> schemas, EntityTypeImpl<?> type, boolean firstPass) {
			super();

			this.type = type;
			this.schemas = schemas;
			this.firstPass = firstPass;
		}

		@Override
		public int compareTo(Type<?> o) {
			Class<?> javaType = this.type.getJavaType();

			while (javaType.getSuperclass() != null) {
				javaType = javaType.getSuperclass();
				if (javaType == o.getJavaType()) {
					return -1;
				}
			}

			javaType = o.getJavaType();
			while (javaType.getSuperclass() != null) {
				javaType = javaType.getSuperclass();
				if (javaType == this.type.getJavaType()) {
					return 1;
				}
			}

			return 0;
		}

		@Override
		public void run() {
			try {
				this.type.ddl(DDLManager.this.datasource, this.schemas, DDLManager.this.ddlMode, this.firstPass);
			}
			catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private class PerformerThreadFactory implements ThreadFactory {

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "Batoo DDL [" + DDLManager.this.nextThreadNo.incrementAndGet() + "]");
		}

	}

	private static final BLogger LOG = BLogger.getLogger(DDLManager.class);

	public static void perform(DataSourceImpl datasource, MetamodelImpl metamodel, DDLMode ddlMode) throws BatooException {
		new DDLManager(datasource, metamodel, ddlMode, true).perform();
		new DDLManager(datasource, metamodel, ddlMode, false).perform();
	}

	private final MetamodelImpl metamodel;
	private final DataSourceImpl datasource;
	private final DDLMode ddlMode;

	private final AtomicInteger nextThreadNo = new AtomicInteger(0);
	private final ThreadPoolExecutor executer;
	private final boolean firstPass;

	private DDLManager(DataSourceImpl datasource, MetamodelImpl metamodel, DDLMode ddlMode, boolean firstPass) {
		super();

		this.datasource = datasource;
		this.metamodel = metamodel;
		this.ddlMode = ddlMode;
		this.firstPass = firstPass;

		final int nThreads = Runtime.getRuntime().availableProcessors() * 2;
		this.executer = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(),
			new PerformerThreadFactory());

		LOG.debug("Number of Performer threads is {0}", Runtime.getRuntime().availableProcessors() * 2);
	}

	private void perform() throws BatooException {
		final long start = System.currentTimeMillis();

		final Set<String> schemas = Sets.<String> newHashSet();
		try {

			if (this.firstPass) {
				this.metamodel.performSequencesDdl(schemas, this.datasource, this.ddlMode);
				this.metamodel.performGeneratorTablesDdl(schemas, this.datasource, this.ddlMode);
			}

			this.performTypes(schemas);
		}
		finally {
			this.executer.shutdownNow();
		}

		LOG.debug("DDL Operations took {0} msecs", System.currentTimeMillis() - start);
	}

	private void performTypes(Set<String> schemas) throws BatooException {
		this.waitFor(this.performTypes(schemas, this.metamodel.getEntities()));
	}

	private List<Future<?>> performTypes(Set<String> schemas, Set<EntityType<?>> set) {
		final List<Future<?>> futures = Lists.newArrayList();

		for (final EntityType<?> type : set) {
			final Future<?> future = this.executer.submit(new PerformDDLTask(schemas, (EntityTypeImpl<?>) type, this.firstPass));
			futures.add(future);
		}

		return futures;
	}

	private void waitFor(final List<Future<?>> futures) throws BatooException {
		for (final Future<?> future : futures) {
			try {
				future.get();
			}
			catch (final Throwable e) {
				this.handleException(e);
			}
		}
	}

}
