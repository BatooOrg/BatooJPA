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
package org.batoo.jpa.core.impl;

import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.sql.DataSource;

import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;

/**
 * 
 * @author hceylan
 * @since $version
 */
public class EntityManagerImpl extends SimpleEntityManager {

	/**
	 * @param entityManagerFactoryImpl
	 * 
	 * @since $version
	 * @author hceylan
	 * @param datasource
	 * @param properties
	 */
	public EntityManagerImpl(EntityManagerFactoryImpl emf, @SuppressWarnings("rawtypes") Map properties, DataSourceImpl datasource) {
		super(emf, properties, datasource);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Query createNamedQuery(String name) {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Query createNativeQuery(String sqlString) {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Query createNativeQuery(String sqlString, @SuppressWarnings("rawtypes") Class resultClass) {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Query createNativeQuery(String sqlString, String resultSetMapping) {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Query createQuery(String qlString) {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<String, Object> getProperties() {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setFlushMode(FlushModeType flushMode) {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setProperty(String propertyName, Object value) {
		this.throwNotImplemented();

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> cls) {
		if (cls == DataSource.class) {
			return (T) this.datasource;
		}

		return null;
	}

}
