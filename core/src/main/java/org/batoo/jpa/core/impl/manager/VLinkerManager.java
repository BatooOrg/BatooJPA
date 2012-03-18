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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.types.IdentifiableTypeImpl;

import com.google.common.collect.Lists;

/**
 * A Manager that links persistent classes vertically.
 * 
 * @author hceylan
 * @since $version
 */
public class VLinkerManager extends DeploymentManager {

	private class LinkerThreadFactory implements ThreadFactory {

		public Thread newThread(Runnable r) {
			return new Thread(r, "Batoo Linker [" + VLinkerManager.this.nextThreadNo.incrementAndGet() + "]");
		}

	}

	private class LinkTask implements Runnable {

		private final IdentifiableTypeImpl<?> type;

		private LinkTask(IdentifiableTypeImpl<?> type) {
			this.type = type;
		}

		public void run() {
			try {
				this.type.vlink();

			}
			catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static final BLogger LOG = BLogger.getLogger(VLinkerManager.class);

	public static void link(MetamodelImpl metamodel) throws BatooException {
		new VLinkerManager(metamodel).link();
	}

	private final AtomicInteger nextThreadNo = new AtomicInteger(0);

	private final ThreadPoolExecutor executer;

	private final MetamodelImpl metamodel;

	private VLinkerManager(MetamodelImpl metamodel) {
		super();

		this.metamodel = metamodel;
		final int nThreads = Runtime.getRuntime().availableProcessors() * 2;
		this.executer = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads, new LinkerThreadFactory());

		LOG.debug("Number of linker threads is {0}", nThreads);
	}

	private ArrayList<IdentifiableType<?>> getOrderedTypes() {
		final ArrayList<IdentifiableType<?>> types = Lists.newArrayList(this.metamodel.getIdentifiableTypes());

		Collections.sort(types, new Comparator<IdentifiableType<?>>() {

			public int compare(IdentifiableType<?> o1, IdentifiableType<?> o2) {
				Class<?> javaType = o1.getJavaType();

				while (javaType.getSuperclass() != null) {
					javaType = javaType.getSuperclass();
					if (javaType == o2.getJavaType()) {
						return -1;
					}
				}

				javaType = o2.getJavaType();
				while (javaType.getSuperclass() != null) {
					javaType = javaType.getSuperclass();
					if (javaType == o1.getJavaType()) {
						return 1;
					}
				}

				return 0;
			}
		});

		return types;
	}

	private void link() throws BatooException {
		final long start = System.currentTimeMillis();

		final List<IdentifiableType<?>> types = this.getOrderedTypes();

		final List<Future<?>> futures = this.linkTypes(types);

		try {
			this.link(futures);
		}
		finally {
			this.executer.shutdownNow();
		}

		LOG.debug("Vertical linking took {0} msecs", System.currentTimeMillis() - start);
	}

	private void link(final List<Future<?>> futures) throws BatooException {
		for (final Future<?> future : futures) {
			try {
				future.get();
			}
			catch (final Throwable e) {
				this.handleException(e);
			}
		}
	}

	private List<Future<?>> linkTypes(List<IdentifiableType<?>> types) {
		final List<Future<?>> futures = Lists.newArrayList();

		for (final ManagedType<?> type : types) {
			final Future<?> future = this.executer.submit(new LinkTask((IdentifiableTypeImpl<?>) type));
			futures.add(future);
		}

		return futures;
	}

}
