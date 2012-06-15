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

import java.util.concurrent.Callable;

import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.IdentifiableTypeImpl;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.core.impl.model.type.MappedSuperclassTypeImpl;

/**
 * Implementation for deployment unit tasks.
 * <p>
 * Implements common behavior like ordering and waiting for the super type to perform first.
 * 
 * @author hceylan
 * @since $version
 */
public final class DeploymentUnitTask implements Callable<Void>, Comparable<DeploymentUnitTask> {

	private final ManagedTypeImpl<?> type;

	@SuppressWarnings("rawtypes")
	private final DeploymentManager manager;

	/**
	 * @param manager
	 *            the deployment unit manager
	 * @param type
	 *            the type of this task
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public DeploymentUnitTask(@SuppressWarnings("rawtypes") DeploymentManager manager, ManagedTypeImpl<?> type) {
		super();

		this.manager = manager;
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final Void call() throws Exception {
		final IdentifiableTypeImpl<?> parent = this.type instanceof IdentifiableTypeImpl ? ((IdentifiableTypeImpl) this.type).getSupertype() : null;

		// if the super type hasn't performed yet, then wait for to finish
		while (!this.manager.hasPerformed(parent)) {
			try {
				Thread.sleep(10);
			}
			catch (final InterruptedException e) {
				return null;
			}
		}

		try {
			return this.manager.perform(this.type);
		}
		finally {
			this.manager.performed(this.type);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int compareTo(DeploymentUnitTask o) {
		// if this type is Embeddable and the other type is not then perform this earlier
		if ((this.type instanceof EmbeddableTypeImpl) && (!(o.type instanceof EmbeddableTypeImpl))) {
			return -1;
		}

		// if the other type is Embeddable and this type is not then perform the other earlier
		if ((o.type instanceof EmbeddableTypeImpl) && (!(this.type instanceof EmbeddableTypeImpl))) {
			return 1;
		}

		// if this type is a MappedSuperClass and the other type is not then perform this earlier
		if ((this.type instanceof MappedSuperclassTypeImpl) && (!(o.type instanceof MappedSuperclassTypeImpl))) {
			return -1;
		}

		// if the other type is a MappedSuperClass and this type is not then perform this later
		if ((o.type instanceof MappedSuperclassTypeImpl) && (!(this.type instanceof MappedSuperclassTypeImpl))) {
			return 1;
		}

		// if the other type is super type of this type then perform other earlier
		Class<?> javaType = this.type.getJavaType();
		while (javaType.getSuperclass() != null) {
			javaType = javaType.getSuperclass();
			if (javaType == o.type.getJavaType()) {
				return 1;
			}
		}

		// it is safe to perform this earlier.
		return -1;
	}
}
