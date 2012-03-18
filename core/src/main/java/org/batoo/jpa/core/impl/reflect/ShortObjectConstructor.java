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
 * Short version of {@link ObjectConstructor}.
 * 
 * @author hceylan
 * @since $version
 */
public class ShortObjectConstructor extends AbstractObjectConstructor<Short> {

	static final ObjectConstructor<?> INSTANCE = new ShortObjectConstructor();

	private ShortObjectConstructor() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Short from(BigDecimal _bigdec) {
		return new Short(_bigdec.shortValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Short from(BigInteger _bigInt) {
		return new Short(_bigInt.shortValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Short from(Byte _byte) {
		return new Short(_byte.shortValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Short from(Integer _int) {
		return new Short(_int.shortValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Short from(Long _long) {
		return _long.shortValue();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Short from(Short _short) {
		return _short;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Short from(String _string) {
		return new Short(_string);
	}

}
