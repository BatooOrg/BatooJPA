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
package org.batoo.jpa.spec.impl.converter;

import javax.persistence.AccessType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter for {@link javax.persistence.AccessType}.
 * 
 * @author hceylan
 * @since $version
 */
public class AccessTypeAdapter extends XmlAdapter<String, AccessType> {

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String marshal(AccessType v) throws Exception {
		return v != null ? v.name() : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public AccessType unmarshal(String v) throws Exception {
		return v != null ? AccessType.valueOf(v) : null;
	}
}
