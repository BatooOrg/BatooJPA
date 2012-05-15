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
package org.batoo.jpa.spec.test;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.batoo.jpa.spec.impl.converter.OrmReader;
import org.batoo.jpa.spec.orm.EntityMappings;
import org.junit.Before;

/**
 * Base test instrumentation class for ORM Tests.
 * 
 * @author hceylan
 * @since $version
 */
public class BaseOrmTest {

	private EntityMappings entityMappings;

	/**
	 * Returns the entityMappings.
	 * 
	 * @return the entityMappings
	 * @since $version
	 */
	public EntityMappings em() {
		return this.entityMappings;
	}

	/**
	 * Subclasses can override.
	 * <p>
	 * Default implementation returns <code>org.batoo.jpa.spec.test.mytets.TestSomething.xml</code>
	 * 
	 * @return the XML file name relative to current package
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected String getXmlName() {
		return this.getClass().getSimpleName() + ".xml";
	}

	/**
	 * Consumes the XML.
	 * <p>
	 * The XML consumed is available by {@link #em()}.
	 * 
	 * @throws Exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Before
	public void setUp() throws Exception {
		final URL resource = this.getClass().getResource(this.getXmlName());

		final OrmReader ormReader = new OrmReader();
		ormReader.consume(this.getClass().getResourceAsStream(this.getXmlName()));
		final JAXBContext context = JAXBContext.newInstance(EntityMappings.class);
		final Unmarshaller unmarshaller = context.createUnmarshaller();

		this.entityMappings = (EntityMappings) unmarshaller.unmarshal(resource);
	}
}
