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
 * Byte version of {@link ObjectConstructor}.
 * 
 * @author hceylan
 * @since $version
 */
public class ByteObjectConstructor extends AbstractObjectConstructor<Byte> {

	static final ObjectConstructor<?> INSTANCE = new ByteObjectConstructor();

	private ByteObjectConstructor() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Byte from(BigDecimal _bigdec) {
		return new Byte(_bigdec.byteValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Byte from(BigInteger _bigInt) {
		return new Byte(_bigInt.byteValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Byte from(Byte _byte) {
		return _byte;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Byte from(Integer _int) {
		return new Byte(_int.byteValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Byte from(Long _long) {
		return _long.byteValue();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Byte from(Short _short) {
		return _short.byteValue();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	public Byte from(String _string) {
		return new Byte(_string);
	}

}
