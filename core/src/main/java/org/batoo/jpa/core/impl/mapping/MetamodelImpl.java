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
package org.batoo.jpa.core.impl.mapping;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceException;
import javax.persistence.SequenceGenerator;
import javax.persistence.metamodel.BasicType;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MappedSuperclassType;
import javax.persistence.metamodel.Metamodel;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.jdbc.PhysicalTableGenerator;
import org.batoo.jpa.core.impl.jdbc.SequenceQueue;
import org.batoo.jpa.core.impl.jdbc.TableIdQueue;
import org.batoo.jpa.core.impl.metamodel.BasicTypeImpl;
import org.batoo.jpa.core.impl.metamodel.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;
import org.batoo.jpa.core.impl.metamodel.IdentifiableTypeImpl;
import org.batoo.jpa.core.impl.metamodel.ManagedTypeImpl;
import org.batoo.jpa.core.impl.metamodel.MappedSuperclassTypeImpl;
import org.batoo.jpa.core.impl.metamodel.TypeImpl;
import org.batoo.jpa.core.jdbc.DDLMode;
import org.batoo.jpa.core.jdbc.adapter.JDBCAdapter;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link Metamodel}.
 * 
 * @since $version
 * @author hceylan
 */
public class MetamodelImpl implements Metamodel {

	private class SequenceThreadFactory implements ThreadFactory {

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public Thread newThread(Runnable r) {
			final Thread thread = new Thread(r, "Batoo Sequence Generator - " + nextThreadNo.incrementAndGet());

			thread.setPriority(Thread.MAX_PRIORITY);

			return thread;
		}

	}

	protected static final AtomicInteger nextThreadNo = new AtomicInteger();

	private static final SequenceGenerator BATOO_SEQUENCE = new SequenceGenerator() {

		@Override
		public int allocationSize() {
			return 50;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return SequenceGenerator.class;
		}

		@Override
		public String catalog() {
			return "";
		}

		@Override
		public int initialValue() {
			return 1;
		}

		@Override
		public String name() {
			return "";
		}

		@Override
		public String schema() {
			return null;
		}

		@Override
		public String sequenceName() {
			return "batoo_sequence";
		}
	};

	// TODO Consider making this configurable
	private static final long POLL_TIMEOUT = 60;

	private JDBCAdapter jdbcAdapter;

	private final Map<BasicType<?>, Class<?>> _basics = Maps.newHashMap();
	private final Map<EmbeddableType<?>, Class<?>> _embeddables = Maps.newHashMap();
	private final Map<EntityType<?>, Class<?>> _entities = Maps.newHashMap();

	private final Map<MappedSuperclassType<?>, Class<?>> _mappedSuperclasses = Maps.newHashMap();
	private final Map<Class<?>, BasicType<?>> basics = Maps.newHashMap();
	private final Map<Class<?>, EmbeddableType<?>> embeddables = Maps.newHashMap();
	private final Map<Class<?>, EntityType<?>> entities = Maps.newHashMap();

	private final Set<Association<?, ?>> associations = Sets.newHashSet();

	private final Map<Class<?>, MappedSuperclassType<?>> mappedSuperclasses = Maps.newHashMap();

	private final Map<String, SequenceGenerator> sequenceGenerators = Maps.newHashMap();
	private final Map<String, SequenceQueue> sequenceQueues = Maps.newHashMap();

	private final Map<String, PhysicalTableGenerator> tableGenerators = Maps.newHashMap();
	private final Map<String, TableIdQueue> tableIdQueues = Maps.newHashMap();

	private final Map<String, TableTemplate> tables = Maps.newConcurrentMap();

	private ExecutorService idGeneratorExecuter;

	private boolean deployed;

	/**
	 * @since $version
	 * @author hceylan
	 */
	public MetamodelImpl() {
		super();
	}

	/**
	 * @param association
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void addAssociation(Association<?, ?> association) {
		this.associations.add(association);
	}

	/**
	 * Adds the newly collected entity to meta model
	 * 
	 * @param basicType
	 *            the new entity type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public <X> void addBasic(BasicType<X> basicType) {
		this.basics.put(basicType.getJavaType(), basicType);
		this._basics.put(basicType, basicType.getJavaType());
	}

	/**
	 * Adds the newly collected embeddable to meta model
	 * 
	 * @param embeddableType
	 *            the new embeddable type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public <X> void addEmbeddable(EmbeddableType<X> embeddableType) {
		this.embeddables.put(embeddableType.getJavaType(), embeddableType);
		this._embeddables.put(embeddableType, embeddableType.getJavaType());
	}

	/**
	 * Adds the newly collected entity to meta model
	 * 
	 * @param entityType
	 *            the new entity type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public <X> void addEntity(EntityType<X> entityType) {
		this.entities.put(entityType.getJavaType(), entityType);
		this._entities.put(entityType, entityType.getJavaType());
	}

	/**
	 * Adds the newly collected mapped super class to meta model
	 * 
	 * @param MappedSuperclassType
	 *            the new mapped super class type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public <X> void addMappedSuperclass(MappedSuperclassType<X> MappedSuperclassType) {
		this.mappedSuperclasses.put(MappedSuperclassType.getJavaType(), MappedSuperclassType);
		this._mappedSuperclasses.put(MappedSuperclassType, MappedSuperclassType.getJavaType());
	}

	/**
	 * Adds a sequence generator to the meta model.
	 * 
	 * @param sequenceGenerator
	 *            the sequence generator
	 * @return false if sequence generator with the same name already exists, true otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized boolean addSequenceGenerator(SequenceGenerator sequenceGenerator) {
		if (this.sequenceGenerators.containsKey(sequenceGenerator.name())) {
			return false;
		}

		this.sequenceGenerators.put(sequenceGenerator.name(), sequenceGenerator);

		return true;
	}

	/**
	 * Adds a table generator to the metamodel
	 * 
	 * @param generator
	 *            the table generator declaration
	 * @return true if the generator already exists.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized boolean addTableGenerator(PhysicalTableGenerator generator) {
		if (this.tableGenerators.containsKey(generator.getName())) {
			return false;
		}

		this.tableGenerators.put(generator.getName(), generator);

		return true;
	}

	/**
	 * Return the meta model basic type representing the class.
	 * 
	 * @param cls
	 *            the type of the represented class
	 * 
	 * @return the meta model basic type
	 * 
	 * @throws IllegalArgumentException
	 *             if not a basic type
	 */
	public <X> BasicTypeImpl<X> basic(Class<X> cls) {
		return this.basic(cls, true);
	}

	@SuppressWarnings("unchecked")
	private <X> BasicTypeImpl<X> basic(Class<X> cls, boolean strict) {
		final BasicType<?> basicType = this.basics.get(cls);

		if (basicType != null) {
			return (BasicTypeImpl<X>) basicType;
		}

		return this.lazyCreateBasicType(cls, strict);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <X> EmbeddableTypeImpl<X> embeddable(Class<X> cls) {
		final EmbeddableType<?> embeddableType = this.embeddables.get(cls);

		if (embeddableType != null) {
			return (EmbeddableTypeImpl<X>) embeddableType;
		}

		return this.throwNotFound(cls);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <X> EntityTypeImpl<X> entity(Class<X> cls) {
		Class<?> clazz = cls;
		while (clazz != Object.class) {
			final EntityType<?> entityType = this.entities.get(clazz);

			if (entityType != null) {
				return (EntityTypeImpl<X>) entityType;
			}

			clazz = clazz.getSuperclass();
		}

		return this.throwNotFound(clazz);
	}

	/**
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized SequenceGenerator getDefaultSequenceGenerator() {
		this.sequenceGenerators.put(BATOO_SEQUENCE.name(), BATOO_SEQUENCE);

		return BATOO_SEQUENCE;
	}

	/**
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized PhysicalTableGenerator getDefaultTableIdGenerator() {
		if (!this.tableGenerators.containsKey("")) {
			final PhysicalTableGenerator generator = new PhysicalTableGenerator(null, this.jdbcAdapter);
			this.tableGenerators.put(generator.getName(), generator);
		}

		return this.tableGenerators.get(PhysicalTableGenerator.DEFAULT_NAME);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<EmbeddableType<?>> getEmbeddables() {
		return Collections.unmodifiableSet(this._embeddables.keySet());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<EntityType<?>> getEntities() {
		return Collections.unmodifiableSet(this._entities.keySet());
	}

	/**
	 * Return the metamodel managed types.
	 * 
	 * @return the metamodel managed types
	 */
	public Set<IdentifiableType<?>> getIdentifiableTypes() {
		return Sets.union(this._mappedSuperclasses.keySet(), this._entities.keySet());
	}

	/**
	 * Returns the executor service for the id generation operations.
	 * 
	 * @return the executor service for the id generation operations
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ExecutorService getIdGeneratorExecutor() {
		return this.idGeneratorExecuter;
	}

	/**
	 * Returns the jdbcAdapter.
	 * 
	 * @return the jdbcAdapter
	 * @since $version
	 */
	public JDBCAdapter getJdbcAdapter() {
		return this.jdbcAdapter;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<ManagedType<?>> getManagedTypes() {
		return Sets.union(this._embeddables.keySet(), this.getIdentifiableTypes());
	}

	/**
	 * Return the metamodel mappedSuperclass types. Returns empty set
	 * if there are no embeddable types.
	 * 
	 * @return the metamodel mappedSuperclass types
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Set<MappedSuperclassType<?>> getMappedSuperclasses() {
		return Collections.unmodifiableSet(this._mappedSuperclasses.keySet());
	}

	/**
	 * Returns the next sequence for the generator.
	 * 
	 * @param generator
	 *            the generator
	 * @return the next sequence for the generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Integer getNextSequence(String generator) {
		try {
			final SequenceQueue queue = StringUtils.isBlank(generator) ? this.sequenceQueues.get(BATOO_SEQUENCE.name())
				: this.sequenceQueues.get(generator);
			return queue.poll(POLL_TIMEOUT, TimeUnit.SECONDS);
		}
		catch (final InterruptedException e) {
			throw new PersistenceException("Unable to retrieve next sequence " + generator + " in allowed " + POLL_TIMEOUT + " seconds");
		}
	}

	/**
	 * Returns the next table value for the generator.
	 * 
	 * @param generator
	 *            the generator
	 * @return the next table value for the generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Object getNextTableValue(String generator) {
		try {
			if (StringUtils.isBlank(generator)) {
				generator = PhysicalTableGenerator.DEFAULT_NAME;
			}

			return this.tableIdQueues.get(generator).poll(POLL_TIMEOUT, TimeUnit.SECONDS);
		}
		catch (final InterruptedException e) {
			throw new PersistenceException("Unable to retrieve next sequence " + generator + " in allowed " + POLL_TIMEOUT + " seconds");
		}
	}

	/**
	 * Return the metamodel managed type representing the basic,
	 * entity, mapped superclass, or embeddable class.
	 * 
	 * @param cls
	 *            the type of the represented type
	 * 
	 * @return the metamodel type
	 * 
	 * @throws IllegalArgumentException
	 *             if not a managed class
	 */
	public TypeImpl<?> getType(Class<?> cls) {
		final BasicTypeImpl<?> basic = this.basic(cls, false);
		if (basic != null) {
			return basic;
		}

		return this.managedType(cls);
	}

	/**
	 * Return the meta model identifiable type representing the
	 * entity or mapped superclass class.
	 * 
	 * @param cls
	 *            the type of the represented identifiable class
	 * 
	 * @return the meta model identifiable type
	 * 
	 * @throws IllegalArgumentException
	 *             if not an identifiable class
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public <X> IdentifiableTypeImpl<X> identifiableType(Class<X> cls) {
		final IdentifiableTypeImpl<X> type = this.identifiableType0(cls);

		if (type != null) {
			return type;
		}

		return this.throwNotFound(cls);
	}

	@SuppressWarnings("unchecked")
	public <X> IdentifiableTypeImpl<X> identifiableType0(Class<X> cls) {
		final EntityType<?> entityType = this.entities.get(cls);
		if (entityType != null) {
			return (IdentifiableTypeImpl<X>) entityType;
		}

		return (IdentifiableTypeImpl<X>) this.mappedSuperclasses.get(cls);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private synchronized <X> BasicTypeImpl<X> lazyCreateBasicType(Class<X> cls, boolean strict) {
		if (this.deployed) {
			if (!strict) {
				return null;
			}

			return this.throwNotFound(cls);
		}

		// skip if annotated with @Entity, @MappedSuperClass or @Embeddable
		if ((cls.getAnnotation(Entity.class) != null) //
			|| (cls.getAnnotation(MappedSuperclass.class) != null) //
			|| (cls.getAnnotation(Embeddable.class) != null)) {

			return null;
		}

		if (Serializable.class.isAssignableFrom(cls) || cls.isPrimitive()) {
			final BasicTypeImpl basicType = new BasicTypeImpl(this, cls);
			this.basics.put(cls, basicType);
			this._basics.put(basicType, cls);

			return basicType;
		}

		if (!strict) {
			return null;
		}

		return this.throwNotFound(cls);
	}

	/**
	 * Links the associations with the opposites.
	 * 
	 * @throws MappingException
	 *             thrown if the bidirectional relation is not valid
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void linkAssociations() throws MappingException {
		for (final Association<?, ?> association : this.associations) {
			if (association instanceof OwnedAssociation) {
				((OwnedAssociation<?, ?>) association).link(this.jdbcAdapter);
			}
			else if (association instanceof OwnerManyToManyMapping) {
				((OwnerManyToManyMapping<?, ?, ?>) association).link(this.jdbcAdapter);
			}
			else if (association instanceof OwnerOneToManyMapping) {
				((OwnerOneToManyMapping<?, ?, ?>) association).link(this.jdbcAdapter);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <X> ManagedTypeImpl<X> managedType(Class<X> cls) {
		final EntityType<?> entityType = this.entities.get(cls);
		if (entityType != null) {
			return (ManagedTypeImpl<X>) entityType;
		}

		final EmbeddableType<?> embeddableType = this.embeddables.get(cls);
		if (embeddableType != null) {
			return (EmbeddableTypeImpl<X>) embeddableType;
		}

		final MappedSuperclassType<?> mappedSuperclassType = this.mappedSuperclasses.get(cls);
		if (mappedSuperclassType != null) {
			return (MappedSuperclassTypeImpl<X>) mappedSuperclassType;
		}

		return this.throwNotFound(cls);
	}

	/**
	 * Return the meta model mapped super class type representing the class or its super classes.
	 * 
	 * @param cls
	 *            the type of the represented class
	 * 
	 * @return the meta model mapped super class type
	 * 
	 * @throws IllegalArgumentException
	 *             if not a mapped super class type
	 */
	@SuppressWarnings("unchecked")
	public <X> MappedSuperclassTypeImpl<X> mappedSuperclass(Class<X> cls) {
		Class<? super X> currentClass = cls;
		while (currentClass != Object.class) {
			final MappedSuperclassType<?> mappedSuperclass = this.mappedSuperclasses.get(currentClass);

			if (mappedSuperclass != null) {
				return (MappedSuperclassTypeImpl<X>) mappedSuperclass;
			}

			currentClass = currentClass.getSuperclass();
		}

		return this.throwNotFound(cls);
	}

	/**
	 * Seals the metamodel.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void markDeployed() {
		this.deployed = true;
	}

	/**
	 * Creates the generator tables if not exist.
	 * 
	 * @param schemas
	 *            the list of schemas recreated
	 * @param datasource
	 *            datasource to use
	 * @param ddlMode
	 *            the DDL mode
	 * 
	 * @throws MappingException
	 *             thrown if a table generator cannot be created
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized void performGeneratorTablesDdl(Set<String> schemas, DataSource datasource, DDLMode ddlMode) throws MappingException {
		for (final PhysicalTableGenerator table : this.tableGenerators.values()) {
			try {
				this.jdbcAdapter.dropAndCreateSchemaIfNecessary(this.jdbcAdapter, datasource, schemas, ddlMode, table.getSchema());

				this.jdbcAdapter.createTableGeneratorIfNecessary(datasource, table);
			}
			catch (final SQLException e) {
				throw new MappingException("Unable to create table generator " + table.getName(), e);
			}
		}
	}

	/**
	 * Creates the sequences if not exist.
	 * 
	 * @param schemas
	 *            the list of schemas recreated
	 * @param datasource
	 *            datasource to use
	 * @param ddlMode
	 *            the DDL mode
	 * 
	 * @throws MappingException
	 *             thrown if a sequence cannot be created
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized void performSequencesDdl(Set<String> schemas, DataSource datasource, DDLMode ddlMode) throws MappingException {
		for (final SequenceGenerator sequence : this.sequenceGenerators.values()) {
			try {
				this.jdbcAdapter.dropAndCreateSchemaIfNecessary(this.jdbcAdapter, datasource, schemas, ddlMode, sequence.schema());

				this.jdbcAdapter.createSequenceIfNecessary(datasource, sequence);
			}
			catch (final SQLException e) {
				throw new MappingException("Unable to create sequence " + sequence.name(), e);
			}
		}
	}

	/**
	 * Prefills the id generators.
	 * 
	 * @param datasource
	 *            the datasource to use
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void preFillGenerators(DataSourceImpl datasource) {
		final int nThreads = Runtime.getRuntime().availableProcessors();

		this.idGeneratorExecuter = new ThreadPoolExecutor(1, nThreads, //
			30, TimeUnit.SECONDS, //
			new LinkedBlockingQueue<Runnable>(), //
			new SequenceThreadFactory());

		for (final SequenceGenerator generator : this.sequenceGenerators.values()) {
			this.sequenceQueues.put(
				generator.name(),
				new SequenceQueue(this.jdbcAdapter, datasource, this.idGeneratorExecuter, generator.sequenceName(),
					generator.allocationSize()));
		}

		for (final PhysicalTableGenerator generator : this.tableGenerators.values()) {
			this.tableIdQueues.put(generator.getName(), new TableIdQueue(this.jdbcAdapter, datasource, this.idGeneratorExecuter, generator));
		}
	}

	/**
	 * Adds the tables to the registry.
	 * 
	 * @param tables
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void putTables(Map<String, TableTemplate> tables) throws MappingException {
		for (final Entry<String, TableTemplate> entry : tables.entrySet()) {
			final TableTemplate existingDefinition = this.tables.get(entry.getKey());
			if (existingDefinition != null) {
				throw new MappingException("Duplicate definition of same table defined on " + existingDefinition.getOwner().getJavaType()
					+ ", " + entry.getValue().getOwner().getJavaType());
			}

			this.tables.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Sets the jdbcAdapter.
	 * 
	 * @param jdbcAdapter
	 *            the jdbcAdapter to set
	 * @since $version
	 */
	public void setJdbcAdapter(JDBCAdapter jdbcAdapter) {
		this.jdbcAdapter = jdbcAdapter;
	}

	private <T> T throwNotFound(Class<?> cls) {
		throw new PersistenceException("Type is not a persistent " + cls);
	}
}
