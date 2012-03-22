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

import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Type;

import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;
import org.batoo.jpa.core.impl.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.types.ManagedTypeImpl;

import com.google.common.collect.Lists;

/**
 * A Manager that creates meta data for the persistent classes
 * 
 * @author hceylan
 * @since $version
 */
public class ParserManager extends DeploymentManager {

	private class ParserThreadFactory implements ThreadFactory {

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "Batoo Parser [" + ParserManager.this.nextThreadNo.incrementAndGet() + "]");
		}

	}

	private class ParseTask implements Runnable {

		private final ManagedTypeImpl<?> type;

		private ParseTask(ManagedTypeImpl<?> type) {
			this.type = type;
		}

		@Override
		public void run() {
			try {
				ReflectHelper.checkAnnotations(this.type.getJavaType(), this.type.parse());
			}
			catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static final BLogger LOG = BLogger.getLogger(ParserManager.class);

	public static void parse(MetamodelImpl metamodel) throws BatooException {
		new ParserManager(metamodel).parse();
	}

	private final AtomicInteger nextThreadNo = new AtomicInteger(0);

	private final ThreadPoolExecutor executer;

	private final MetamodelImpl metamodel;

	private ParserManager(MetamodelImpl metamodel) {
		super();

		this.metamodel = metamodel;

		final int nThreads = Runtime.getRuntime().availableProcessors() * 2;
		this.executer = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads, new ParserThreadFactory());

		LOG.debug("Number of parser threads is {0}", nThreads);
	}

	private void parse() throws BatooException {
		final long start = System.currentTimeMillis();

		final List<Future<?>> futures = this.parseTypes(this.metamodel.getManagedTypes());

		try {
			this.parse(futures);
		}
		finally {
			this.executer.shutdownNow();
		}

		LOG.debug("Parsing took {0} msecs", System.currentTimeMillis() - start);
	}

	private void parse(final List<Future<?>> futures) throws BatooException {
		for (final Future<?> future : futures) {
			try {
				future.get();
			}
			catch (final Throwable e) {
				this.handleException(e);
			}
		}
	}

	private List<Future<?>> parseTypes(Set<? extends ManagedType<?>> types) {
		final List<Future<?>> futures = Lists.newArrayList();

		for (final Type<?> type : types) {
			final Future<?> future = this.executer.submit(new ParseTask((ManagedTypeImpl<?>) type));
			futures.add(future);
		}

		return futures;
	}

}
