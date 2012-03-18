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
 * Long version of {@link ObjectConstructor}.
 * 
 * @author hceylan
 * @since $version
 */
public class LongObjectConstructor extends AbstractObjectConstructor<Long> {

	static final ObjectConstructor<?> INSTANCE = new LongObjectConstructor();

	private LongObjectConstructor() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Long from(BigDecimal _bigdec) {
		return new Long(_bigdec.longValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Long from(BigInteger _bigInt) {
		return new Long(_bigInt.longValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Long from(Byte _byte) {
		return new Long(_byte.longValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Long from(Integer _int) {
		return new Long(_int.longValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Long from(Long _long) {
		return _long;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Long from(Short _short) {
		return new Long(_short.longValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Long from(String _string) {
		return new Long(_string);
	}

}
