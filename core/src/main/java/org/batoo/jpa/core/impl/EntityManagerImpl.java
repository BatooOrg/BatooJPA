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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;
import javax.sql.DataSource;

import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;

/**
 * Implementation of {@link EntityManager}.
 * 
 * @author hceylan
 * @since $version
 */
public class EntityManagerImpl implements EntityManager {

	private final EntityManagerFactoryImpl emf;
	private final MetamodelImpl metamodel;
	private final DataSourceImpl datasource;

	/**
	 * @param entityManagerFactory
	 *            the entity manager factory
	 * @param metamodel
	 *            the metamodel
	 * @param datasource
	 *            the datasource
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityManagerImpl(EntityManagerFactoryImpl entityManagerFactory, MetamodelImpl metamodel, DataSourceImpl datasource) {
		super();

		this.emf = entityManagerFactory;
		this.metamodel = metamodel;
		this.datasource = datasource;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void clear() {
		// TODO Auto-generated method stub
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean contains(Object entity) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Query createNamedQuery(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Query createNativeQuery(String sqlString) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Query createNativeQuery(String sqlString, Class resultClass) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Query createNativeQuery(String sqlString, String resultSetMapping) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Query createQuery(String qlString) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void detach(Object entity) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object getDelegate() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public FlushModeType getFlushMode() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public LockModeType getLockMode(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Metamodel getMetamodel() {
		return this.metamodel;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<String, Object> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTransaction getTransaction() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void joinTransaction() {
		// TODO Auto-generated method stub
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void lock(Object entity, LockModeType lockMode) {
		// TODO Auto-generated method stub
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <T> T merge(T entity) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void persist(Object entity) {
		// TODO Auto-generated method stub
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refresh(Object entity) {
		// TODO Auto-generated method stub
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refresh(Object entity, LockModeType lockMode) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		// TODO Auto-generated method stub
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void refresh(Object entity, Map<String, Object> properties) {
		// TODO Auto-generated method stub
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void remove(Object entity) {
		// TODO Auto-generated method stub
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setFlushMode(FlushModeType flushMode) {
		// TODO Auto-generated method stub
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setProperty(String propertyName, Object value) {
		// TODO Auto-generated method stub
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> clazz) {
		if (clazz == DataSource.class) {
			return (T) this.datasource;
		}

		return null;
	}

}
