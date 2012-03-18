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
package org.batoo.jpa.core.impl.reflect;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Abstract Object constructor.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class AbstractObjectConstructor<T> implements ObjectConstructor<T> {

	/**
	 * {@inheritDoc}
	 * 
	 */
	public T from(Object value) {
		if (value instanceof Long) {
			return this.from((Long) value);
		}

		if (value instanceof Integer) {
			return this.from((Integer) value);
		}

		if (value instanceof Short) {
			return this.from((Short) value);
		}

		if (value instanceof Byte) {
			return this.from((Byte) value);
		}

		if (value instanceof BigInteger) {
			return this.from((BigInteger) value);
		}

		if (value instanceof BigDecimal) {
			return this.from((BigDecimal) value);
		}

		return this.from((String) value);
	}

}
