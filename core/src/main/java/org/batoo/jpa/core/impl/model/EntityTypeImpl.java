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
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
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
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.Enhancer;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.SecondaryTable;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.EntityTransactionImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.metamodel.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AssociatedAttribute;
import org.batoo.jpa.core.impl.model.attribute.AssociatedPluralAttribute;
import org.batoo.jpa.core.impl.model.attribute.AssociatedSingularAttribute;
import org.batoo.jpa.core.impl.model.attribute.IdAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.PhysicalAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.SecondaryTableMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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

	private static final int MAX_DEPTH = 5;
	private final String name;
	private EntityTable primaryTable;
	private final Map<String, EntityTable> declaredTables = Maps.newHashMap();

	@SuppressWarnings("restriction")
	private sun.reflect.ConstructorAccessor enhancer;

	private EntityTable[] tables;
	private CriteriaQueryImpl<X> selectCriteria;

	private int dependencyCount;
	private final HashMap<EntityTypeImpl<?>, AssociatedAttribute<?, ?, ?>[]> dependencyMap = Maps.newHashMap();

	private AssociatedAttribute<? super X, ?, ?>[] associatedAttributes;
	private AssociatedAttribute<? super X, ?, ?>[] persistableAssociations;
	private AssociatedAttribute<? super X, ?, ?>[] eagerAssociations;
	private AssociatedAttribute<? super X, ?, ?>[] joinedAssociations;

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

		this.addAttributes(metadata);

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
	public AssociatedAttribute<? super X, ?, ?>[] getAssociations() {
		if (this.associatedAttributes != null) {
			return this.associatedAttributes;
		}

		synchronized (this) {
			if (this.associatedAttributes != null) {
				return this.associatedAttributes;
			}

			final List<AssociatedAttribute<? super X, ?, ?>> associations = Lists.newArrayList();

			for (final Attribute<? super X, ?> attribute : this.getAttributes()) {
				if (attribute instanceof AssociatedAttribute) {
					associations.add((AssociatedAttribute<? super X, ?, ?>) attribute);
				}
			}

			final AssociatedAttribute<? super X, ?, ?>[] associatedAttributes0 = new AssociatedAttribute[associations.size()];
			associations.toArray(associatedAttributes0);

			this.associatedAttributes = associatedAttributes0;
		}

		return this.associatedAttributes;
	}

	/**
	 * Returns the associated attributes that are eager.
	 * 
	 * @return the associated attributes that are eager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AssociatedAttribute<? super X, ?, ?>[] getAssociationsEager() {
		if (this.eagerAssociations != null) {
			return this.eagerAssociations;
		}

		synchronized (this) {
			if (this.eagerAssociations != null) {
				return this.eagerAssociations;
			}

			final List<AssociatedAttribute<? super X, ?, ?>> eagerAssociations = Lists.newArrayList();

			for (final AssociatedAttribute<? super X, ?, ?> association : this.getAssociations()) {
				if (association.isEager()) {
					eagerAssociations.add(association);
				}
			}

			final AssociatedAttribute<? super X, ?, ?>[] eagerAssociations0 = new AssociatedAttribute[eagerAssociations.size()];
			eagerAssociations.toArray(eagerAssociations0);

			this.eagerAssociations = eagerAssociations0;
		}

		return this.eagerAssociations;
	}

	/**
	 * Returns the associated attributes that are joined.
	 * 
	 * @return the associated attributes that are joined
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AssociatedAttribute<? super X, ?, ?>[] getAssociationsJoined() {
		if (this.joinedAssociations != null) {
			return this.joinedAssociations;
		}

		synchronized (this) {
			if (this.joinedAssociations != null) {
				return this.joinedAssociations;
			}

			final List<AssociatedAttribute<? super X, ?, ?>> joinedAssociations = Lists.newArrayList();

			for (final AssociatedAttribute<? super X, ?, ?> association : this.getAssociations()) {
				if (association.getJoinTable() != null) {
					joinedAssociations.add(association);
				}
			}

			final AssociatedAttribute<? super X, ?, ?>[] joinedAssociations0 = new AssociatedAttribute[joinedAssociations.size()];
			joinedAssociations.toArray(joinedAssociations0);

			this.joinedAssociations = joinedAssociations0;
		}

		return this.joinedAssociations;
	}

	/**
	 * Returns the associated attributes that are persistable by the type.
	 * 
	 * @return the associated attributes that are persistable by the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public AssociatedAttribute<? super X, ?, ?>[] getAssociationsPersistable() {
		if (this.persistableAssociations != null) {
			return this.persistableAssociations;
		}

		synchronized (this) {
			if (this.persistableAssociations != null) {
				return this.persistableAssociations;
			}

			final List<AssociatedAttribute<? super X, ?, ?>> persistableAssociations = Lists.newArrayList();

			for (final AssociatedAttribute<? super X, ?, ?> association : this.getAssociations()) {
				if (association.cascadesPersist()) {
					persistableAssociations.add(association);
				}
			}

			final AssociatedAttribute<? super X, ?, ?>[] persistableAssociations0 = new AssociatedAttribute[persistableAssociations.size()];
			persistableAssociations.toArray(persistableAssociations0);

			this.persistableAssociations = persistableAssociations0;
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
	 * Returns the dependencies for the associate type
	 * 
	 * @param associate
	 *            the associate type
	 * @return the array of associations for the associate
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AssociatedAttribute<?, ?, ?>[] getDependenciesFor(EntityTypeImpl<?> associate) {
		return this.dependencyMap.get(associate);
	}

	/**
	 * Returns the dependencyCount.
	 * 
	 * @return the dependencyCount
	 * @since $version
	 */
	public int getDependencyCount() {
		return this.dependencyCount;
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
	@SuppressWarnings({ "unchecked", "restriction" })
	public ManagedInstance<X> getManagedInstanceById(SessionImpl session, Object id, boolean lazy) {
		this.enhanceIfNeccessary();

		X instance = null;
		try {
			instance = (X) this.enhancer.newInstance(new Object[] { this.getJavaType(), session, id, !lazy });
		}
		catch (final Exception e) {
			e.printStackTrace();
		} // not possible

		final ManagedInstance<X> managedInstance = new ManagedInstance<X>(this, session, instance, id);

		((EnhancedInstance) instance).__enhanced__$$__setManagedInstance(managedInstance);

		return managedInstance;
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistenceType getPersistenceType() {
		return PersistenceType.ENTITY;
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
	 * Returns the table with the name.
	 * <p>
	 * If the <code>tableName</code> is blank then the primary table is returned
	 * 
	 * @param tableName
	 *            the name of the table, may be blank
	 * @return the table or null if not found
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractTable getTable(String tableName) {
		if (StringUtils.isBlank(tableName)) {
			return this.primaryTable;
		}

		return this.declaredTables.get(tableName);
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
			if (this.tables != null) {
				return this.tables;
			}

			int size = this.getDeclaredTables().size();
			if (this.getSupertype() instanceof EntityTypeImpl) {
				size += ((EntityTypeImpl<? super X>) this.getSupertype()).getTables().length;
			}

			final EntityTable[] tables = new EntityTable[size];

			int i = 0;
			for (final EntityTable entityTable : this.getDeclaredTables()) {
				tables[i] = entityTable;
				i++;
			}

			if (this.getSupertype() instanceof EntityTypeImpl) {
				for (final EntityTable table : ((EntityTypeImpl<? super X>) this.getSupertype()).getDeclaredTables()) {
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

		// link the singular attributes
		for (final SingularAttribute<X, ?> attribute : this.getDeclaredSingularAttributes0().values()) {
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
	 * @param managedInstance
	 *            the managed instance to perform insert for
	 * @throws SQLException
	 *             thrown in case of an SQL Error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performInsert(ConnectionImpl connection, EntityTransactionImpl transaction, ManagedInstance<X> managedInstance)
		throws SQLException {
		for (final EntityTable table : this.getTables()) {
			table.performInsert(connection, managedInstance);
		}

		for (final PluralAttributeImpl<? super X, ?, ?> attribute : this.getPluralAttributes0().values()) {
			attribute.set(managedInstance, attribute.get(managedInstance.getInstance()));
		}

		managedInstance.setTransaction(transaction);
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
		q.setParameter(1, instance.getId());

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

	/**
	 * Prepares the dependencies for the associate.
	 * 
	 * @param associate
	 *            the associate
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void prepareDependenciesFor(EntityTypeImpl<?> associate) {
		// prepare the related associations
		final Set<AssociatedAttribute<?, ?, ?>> attributes = Sets.newHashSet();

		for (final AssociatedAttribute<?, ?, ?> association : this.getAssociations()) {

			// only owner associations impose priority
			if (!association.isOwner()) {
				continue;
			}

			// only relations kept in the row impose priority
			if ((association.getPersistentAttributeType() != PersistentAttributeType.ONE_TO_ONE) && //
				(association.getPersistentAttributeType() != PersistentAttributeType.MANY_TO_ONE)) {
				continue;
			}

			final Class<?> javaType = association.getJavaType();

			if (javaType.isAssignableFrom(associate.getBindableJavaType())) {
				attributes.add(association);
			}
		}

		final AssociatedAttribute<?, ?, ?>[] dependencies = new AssociatedAttribute[attributes.size()];
		attributes.toArray(dependencies);

		this.dependencyCount += dependencies.length;

		this.dependencyMap.put(associate, dependencies);
	}

	/**
	 * 
	 * @since $version
	 * @author hceylan
	 * @param entityTypeImpl
	 */
	private void prepareEagerAssociations(FetchParent<?, ?> r, int depth, AssociatedAttribute<?, ?, ?> parent) {
		if (depth < EntityTypeImpl.MAX_DEPTH) {

			for (final AssociatedAttribute<?, ?, ?> attribute : this.getAssociationsEager()) {

				// if we are coming from the inverse side and inverse side is not many-to-one then skip
				if ((parent != null) && //
					(attribute.getInverse() == parent) && //
					(parent.getPersistentAttributeType() != PersistentAttributeType.MANY_TO_ONE)) {
					continue;
				}

				final Fetch<Object, Object> r2 = r.fetch(attribute.getName(), JoinType.LEFT);

				EntityTypeImpl<?> type;

				if (attribute instanceof AssociatedSingularAttribute) {
					final AssociatedSingularAttribute<?, ?> singularAttribute = (AssociatedSingularAttribute<?, ?>) attribute;
					type = singularAttribute.getType();
				}
				else {
					final AssociatedPluralAttribute<?, ?, ?> pluralAttribute = (AssociatedPluralAttribute<?, ?, ?>) attribute;
					type = pluralAttribute.getElementType();
				}

				type.prepareEagerAssociations(r2, depth + 1, attribute);
			}
		}
	}

	private synchronized CriteriaQueryImpl<X> prepareSelectCriteria(EntityManagerImpl entityManager) {
		// other thread prepared before this one
		if (this.selectCriteria != null) {
			return this.selectCriteria;
		}

		final CriteriaBuilderImpl cb = entityManager.getCriteriaBuilder();
		CriteriaQueryImpl<X> q = cb.createQuery(this.getJavaType());
		final RootImpl<X> r = q.from(this);
		q = q.select(r);

		this.prepareEagerAssociations(r, 0, null);

		final List<PredicateImpl> predicates = Lists.newArrayList();
		for (final IdAttributeImpl<? super X, ?> idAttribute : this.getIdAttributes()) {
			final ParameterExpressionImpl<?> pe = cb.parameter(idAttribute.getJavaType());
			final Path<?> path = r.get(idAttribute);
			final PredicateImpl predicate = cb.equal(path, pe);

			predicates.add(predicate);
		}

		return this.selectCriteria = q.where(predicates.toArray(new PredicateImpl[predicates.size()]));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "EntityTypeImpl [name=" + this.name + "]";
	}
}
