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
package org.batoo.jpa.core.impl.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.criteria.CriteriaBuilderImpl;
import org.batoo.jpa.core.impl.criteria.CriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.ParameterExpressionImpl;
import org.batoo.jpa.core.impl.criteria.PredicateImpl;
import org.batoo.jpa.core.impl.criteria.RootImpl;
import org.batoo.jpa.core.impl.criteria.TypedQueryImpl;
import org.batoo.jpa.core.impl.instance.Enhancer;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.jdbc.SecondaryTable;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.EntityTransactionImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.metamodel.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AssociatedAttribute;
import org.batoo.jpa.core.impl.model.attribute.PhysicalAttributeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.SecondaryTableMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Implementation of {@link EntityType}.
 * 
 * @param <X>
 *            The represented entity type
 * 
 * @author hceylan
 * @since $version
 */
public class EntityTypeImpl<X> extends IdentifiableTypeImpl<X> implements EntityType<X> {

	private final String name;
	private EntityTable primaryTable;
	private final Map<String, EntityTable> declaredTables = Maps.newHashMap();
	private AssociatedAttribute<? super X, ?>[] associatedAttributes;
	private AssociatedAttribute<? super X, ?>[] persistableAssociations;
	private EntityTable[] tables;
	@SuppressWarnings("restriction")
	private sun.reflect.ConstructorAccessor enhancer;
	private CriteriaQueryImpl<X> selectCriteria;

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param parent
	 *            the parent type
	 * @param javaType
	 *            the java type of the managed type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTypeImpl(MetamodelImpl metamodel, IdentifiableTypeImpl<? super X> parent, Class<X> javaType, EntityMetadata metadata) {
		super(metamodel, parent, javaType, metadata);

		this.name = metadata.getName();

		this.initTables(metadata);
	}

	@SuppressWarnings("restriction")
	private void enhanceIfNeccessary() {
		if (this.enhancer == null) {
			synchronized (this) {
				if (this.enhancer == null) {
					try {
						final Class<X> enhancedClass = Enhancer.enhance(this);
						final Constructor<X> enhancedConstructor = enhancedClass.getConstructor(Class.class, SessionImpl.class,
							Object.class, Boolean.TYPE);

						final Class<?> magClass = Class.forName("sun.reflect.MethodAccessorGenerator");
						final Constructor<?> c = magClass.getDeclaredConstructors()[0];
						final Method generateMethod = magClass.getMethod("generateConstructor", Class.class, Class[].class, Class[].class,
							Integer.TYPE);

						ReflectHelper.setAccessible(c, true);
						ReflectHelper.setAccessible(generateMethod, true);

						try {
							final Object mag = c.newInstance();
							this.enhancer = (sun.reflect.ConstructorAccessor) generateMethod.invoke(mag,
								enhancedConstructor.getDeclaringClass(), enhancedConstructor.getParameterTypes(),
								enhancedConstructor.getExceptionTypes(), enhancedConstructor.getModifiers());
						}
						finally {
							ReflectHelper.setAccessible(c, false);
							ReflectHelper.setAccessible(generateMethod, false);
						}
					}
					catch (final Exception e) {
						throw new RuntimeException("Cannot enhance class: " + this.getJavaType(), e);
					}
				}
			}
		}
	}

	/**
	 * Returns the associatedAttributes of the type.
	 * 
	 * @return the associatedAttributes of the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AssociatedAttribute<? super X, ?>[] getAssociations() {
		if (this.associatedAttributes != null) {
			return this.associatedAttributes;
		}

		synchronized (this) {
			final List<AssociatedAttribute<? super X, ?>> associations = Lists.newArrayList();

			for (final Attribute<? super X, ?> attribute : this.getAttributes()) {
				if (attribute instanceof AssociatedAttribute) {
					associations.add((AssociatedAttribute<? super X, ?>) attribute);
				}
			}

			this.associatedAttributes = new AssociatedAttribute[associations.size()];
			associations.toArray(this.associatedAttributes);
		}

		return this.associatedAttributes;
	}

	/**
	 * Returns the associatedAttributes that are persistable by the type.
	 * 
	 * @return the associatedAttributes that are persistable by the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AssociatedAttribute<? super X, ?>[] getAssociationsPersistable() {
		if (this.persistableAssociations != null) {
			return this.persistableAssociations;
		}

		synchronized (this) {
			final List<AssociatedAttribute<? super X, ?>> persistableAssociations = Lists.newArrayList();

			for (final AssociatedAttribute<? super X, ?> association : this.getAssociations()) {
				if (association.cascadesPersist()) {
					persistableAssociations.add(association);
				}
			}

			this.persistableAssociations = new AssociatedAttribute[persistableAssociations.size()];
			persistableAssociations.toArray(this.persistableAssociations);
		}

		return this.persistableAssociations;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<X> getBindableJavaType() {
		return this.getJavaType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BindableType getBindableType() {
		return BindableType.ENTITY_TYPE;
	}

	/**
	 * Returns the tables of the entity.
	 * 
	 * @return the tables of the entity
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Collection<EntityTable> getDeclaredTables() {
		return this.declaredTables.values();
	}

	/**
	 * Returns the managed instance for the instance.
	 * 
	 * @param instance
	 *            the instance to create managed instance for
	 * @param session
	 *            the session
	 * @return managed id for the instance
	 * @throws NullPointerException
	 *             thrown if the instance is null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public ManagedInstance<X> getManagedInstance(SessionImpl session, Object instance) {
		if (instance == null) {
			throw new NullPointerException();
		}

		return new ManagedInstance<X>(this, session, (X) instance);
	}

	/**
	 * Creates a new managed instance with the id.
	 * 
	 * @param session
	 *            the session
	 * @param id
	 *            the primary key
	 * @return the managed instance created
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedInstance<X> getManagedInstanceById(SessionImpl session, Object id) {
		return this.getManagedInstanceById(session, id, false);
	}

	/**
	 * Creates a new managed instance with the id.
	 * 
	 * @param session
	 *            the session
	 * @param id
	 *            the primary key
	 * @param lazy
	 *            if the instance is lazy
	 * @return the managed instance created
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public ManagedInstance<X> getManagedInstanceById(SessionImpl session, Object id, boolean lazy) {
		this.enhanceIfNeccessary();

		X instance = null;
		try {
			instance = (X) this.enhancer.newInstance(new Object[] { this.getJavaType(), session, id, !lazy });
		}
		catch (final InvocationTargetException e) {} // not possible
		catch (final IllegalArgumentException e) {} // not possible
		catch (final InstantiationException e) {} // not possible

		return new ManagedInstance<X>(this, session, instance, id);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the primary table of the type
	 * 
	 * @return the primary table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTable getPrimaryTable() {
		return this.primaryTable;
	}

	/**
	 * Returns the tables of the type, starting from the top of the hierarchy.
	 * 
	 * @return the tables of the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityTable[] getTables() {
		if (this.tables != null) {
			return this.tables;

		}

		synchronized (this) {
			int size = this.getDeclaredTables().size();
			if (this.getParent() instanceof EntityTypeImpl) {
				size += ((EntityTypeImpl<? super X>) this.getParent()).getTables().length;
			}

			final EntityTable[] tables = new EntityTable[size];

			int i = 0;
			for (final EntityTable entityTable : this.getDeclaredTables()) {
				tables[i] = entityTable;
				i++;
			}

			if (this.getParent() instanceof EntityTypeImpl) {
				for (final EntityTable table : ((EntityTypeImpl<? super X>) this.getParent()).getDeclaredTables()) {
					tables[i] = table;
				}
			}

			return this.tables = tables;
		}
	}

	/**
	 * Initializes the tables.
	 * 
	 * @since $version
	 * @author hceylan
	 * @param metadata
	 */
	private void initTables(EntityMetadata metadata) {
		this.primaryTable = new EntityTable(this, metadata.getTable());

		this.declaredTables.put(this.primaryTable.getName(), this.primaryTable);

		for (final SecondaryTableMetadata secondaryTableMetadata : metadata.getSecondaryTables()) {
			this.declaredTables.put(secondaryTableMetadata.getName(), new SecondaryTable(this, secondaryTableMetadata));
		}

		for (final SingularAttribute<X, ?> attribute : this.getDeclaredSingularAttributes()) {
			if (attribute instanceof PhysicalAttributeImpl) {
				final BasicColumn basicColumn = ((PhysicalAttributeImpl<X, ?>) attribute).getColumn();
				final String tableName = basicColumn.getTableName();

				// if table name is blank, it means the column should belong to the primary table
				if (StringUtils.isBlank(tableName)) {
					basicColumn.setTable(this.primaryTable);
				}
				// otherwise locate the table
				else {
					final AbstractTable table = this.declaredTables.get(tableName);
					if (table == null) {
						throw new MappingException("Table " + tableName + " could not be found", basicColumn.getLocator());
					}

					basicColumn.setTable(table);
				}
			}
		}
	}

	/**
	 * Returns if the method is an id method.
	 * 
	 * @param method
	 *            the method
	 * @return if the method is an id method
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean isIdMethod(Method method) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Performs inserts to each table for the managed instance.
	 * 
	 * @param connection
	 *            the connection to use
	 * @param transaction
	 *            the transaction for which the insert will be performed
	 * @param instance
	 *            the managed instance to perform insert for
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performInsert(ConnectionImpl connection, EntityTransactionImpl transaction, ManagedInstance<X> instance)
		throws SQLException {
		for (final EntityTable table : this.getTables()) {
			table.performInsert(connection, instance);
		}

		instance.setTransaction(transaction);
	}

	/**
	 * @param connection
	 *            the connection to use
	 * @param transaction
	 *            the transaction for which the remove will be performed
	 * @param instance
	 *            the managed instance to perform remove for
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performRemove(ConnectionImpl connection, EntityTransactionImpl transaction, ManagedInstance<X> instance)
		throws SQLException {
	}

	/**
	 * Performs select to find the instance.
	 * 
	 * @param entityManager
	 *            the entity manager to use
	 * @param instance
	 *            the managed instance to perform insert for
	 * @return the instance found or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public X performSelect(EntityManagerImpl entityManager, ManagedInstance<X> instance) {
		if (this.selectCriteria == null) {
			this.prepareSelectCriteria(entityManager);
		}

		final TypedQueryImpl<X> q = entityManager.createQuery(this.selectCriteria);
		q.setParameter(1, instance);

		return q.getSingleResult();
	}

	/**
	 * @param connection
	 *            the connection to use
	 * @param transaction
	 *            the transaction for which the update will be performed
	 * @param instance
	 *            the managed instance to perform update for
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performUpdate(ConnectionImpl connection, EntityTransactionImpl transaction, ManagedInstance<X> instance)
		throws SQLException {
	}

	private synchronized CriteriaQueryImpl<X> prepareSelectCriteria(EntityManagerImpl entityManager) {
		// other thread prepared before this one
		if (this.selectCriteria != null) {
			return this.selectCriteria;
		}

		final CriteriaBuilderImpl cb = entityManager.getCriteriaBuilder();
		CriteriaQueryImpl<X> q = cb.createQuery(this.getJavaType());
		final RootImpl<X> r = q.from(this.getJavaType());
		q = q.select(r);
		q = q.distinct(true);

		final ParameterExpressionImpl<X> pe = cb.parameter(this.getJavaType());
		final PredicateImpl w = cb.equal(r, pe);
		q = q.where(w);
		this.selectCriteria = q;

		return q;
	}
}
