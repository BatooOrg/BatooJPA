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
import java.util.Collection;
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
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;
import javax.persistence.metamodel.BasicType;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MappedSuperclassType;
import javax.persistence.metamodel.Metamodel;
import javax.sql.DataSource;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.jdbc.SequenceQueue;
import org.batoo.jpa.core.impl.jdbc.TableIdQueue;
import org.batoo.jpa.core.impl.types.BasicTypeImpl;
import org.batoo.jpa.core.impl.types.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;
import org.batoo.jpa.core.impl.types.IdentifiableTypeImpl;
import org.batoo.jpa.core.impl.types.ManagedTypeImpl;
import org.batoo.jpa.core.impl.types.MappedSuperclassTypeImpl;
import org.batoo.jpa.core.impl.types.TypeImpl;
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

	private static final TableGenerator BATOO_TABLE = new TableGenerator() {

		@Override
		public int allocationSize() {
			return 50;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return TableGenerator.class;
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
			return "batoo_id";
		}

		@Override
		public String pkColumnName() {
			return "default";
		}

		@Override
		public String pkColumnValue() {
			return "key";
		}

		@Override
		public String schema() {
			return "";
		}

		@Override
		public String table() {
			return "batoo_id";
		}

		@Override
		public UniqueConstraint[] uniqueConstraints() {
			return new UniqueConstraint[] {};
		}

		@Override
		public String valueColumnName() {
			return "id";
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

	private final Map<String, TableIdQueue> tableIdQueues = Maps.newHashMap();
	private final Map<String, TableGenerator> tableGenerators = Maps.newHashMap();

	private final Map<String, TableTemplate> tables = Maps.newConcurrentMap();

	private ExecutorService idGeneratorExecuter;

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
	public boolean addSequenceGenerator(SequenceGenerator sequenceGenerator) {
		if (this.sequenceGenerators.containsKey(sequenceGenerator.name())) {
			return false;
		}

		this.sequenceGenerators.put(sequenceGenerator.name(), sequenceGenerator);

		return true;
	}

	/**
	 * @param tableGenerator
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean addTableGenerator(TableGenerator tableGenerator) {
		if (this.tableGenerators.containsKey(tableGenerator.name())) {
			return false;
		}

		this.tableGenerators.put(tableGenerator.name(), tableGenerator);

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
	 * Creates the Table Id Generators.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void createTableGeneratorTables() {
		for (final TableGenerator generator : this.tableGenerators.values()) {
			throw new NotImplementedException();
		}
	}

	/**
	 * Creates the sequence if not exists.
	 * 
	 * @param schemas
	 *            the list of schemas recreated
	 * @param datasource
	 *            datasource to use
	 * @param ddlMode
	 *            the DDL mode
	 * 
	 * @param sequence
	 *            the generator
	 * @throws MappingException
	 *             thrown if sequence cannot be created
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized void ddl(Set<String> schemas, DataSource datasource, SequenceGenerator sequence, DDLMode ddlMode)
		throws MappingException {
		try {
			JDBCAdapter.dropAndCreateSchemaIfNecessary(this.jdbcAdapter, datasource, schemas, ddlMode, sequence.schema());

			this.jdbcAdapter.createSequenceIfNecessary(datasource, sequence);
		}
		catch (final SQLException e) {
			throw new MappingException("Unable to create sequence " + sequence.name(), e);
		}
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
		final EntityType<?> entityType = this.entities.get(cls);

		if (entityType != null) {
			return (EntityTypeImpl<X>) entityType;
		}

		return this.throwNotFound(cls);
	}

	/**
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SequenceGenerator getDefaultSequenceGenerator() {
		this.sequenceGenerators.put(BATOO_SEQUENCE.name(), BATOO_SEQUENCE);

		return BATOO_SEQUENCE;
	}

	/**
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TableGenerator getDefaultTableIdGenerator() {
		if (!this.tableGenerators.containsKey(BATOO_TABLE.name())) {
			this.tableGenerators.put(BATOO_TABLE.name(), BATOO_TABLE);
		}

		return BATOO_TABLE;
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
			return this.tableIdQueues.get(generator).poll(POLL_TIMEOUT, TimeUnit.SECONDS);
		}
		catch (final InterruptedException e) {
			throw new PersistenceException("Unable to retrieve next sequence " + generator + " in allowed " + POLL_TIMEOUT + " seconds");
		}
	}

	/**
	 * Returns the sequences.
	 * 
	 * @return the sequences
	 * @since $version
	 */
	public Collection<SequenceGenerator> getSequences() {
		return this.sequenceGenerators.values();
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
	@SuppressWarnings("unchecked")
	public <X> IdentifiableTypeImpl<X> identifiableType(Class<X> cls) {
		if (this.entities.containsKey(cls)) {
			return (EntityTypeImpl<X>) this.entities.get(cls);
		}

		if (this.mappedSuperclasses.containsKey(cls)) {
			return (MappedSuperclassTypeImpl<X>) this.mappedSuperclasses.get(cls);
		}

		return this.throwNotFound(cls);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private synchronized <X> BasicTypeImpl<X> lazyCreateBasicType(Class<X> cls, boolean strict) {
		BasicType<?> basicType = this.basics.get(cls);

		if (basicType != null) {
			return (BasicTypeImpl<X>) basicType;
		}

		// skip if annotated with @Entity, @MappedSuperClass or @Embeddable
		if ((cls.getAnnotation(Entity.class) != null //
			)
			|| (cls.getAnnotation(MappedSuperclass.class) != null //
			) || (cls.getAnnotation(Embeddable.class) != null)) {
			return null;
		}

		if (Serializable.class.isAssignableFrom(cls) || cls.isPrimitive()) {
			basicType = new BasicTypeImpl(this, cls);
			this.basics.put(cls, basicType);
			this._basics.put(basicType, cls);

			return (BasicTypeImpl<X>) basicType;
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
				final OwnedAssociation<?, ?> ownedAssociation = (OwnedAssociation<?, ?>) association;
				ownedAssociation.link();
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
		if (this.entities.containsKey(cls)) {
			return (EntityTypeImpl<X>) this.entities.get(cls);
		}

		if (this.embeddables.containsKey(cls)) {
			return (EmbeddableTypeImpl<X>) this.embeddables.get(cls);
		}

		if (this.mappedSuperclasses.containsKey(cls)) {
			return (MappedSuperclassTypeImpl<X>) this.mappedSuperclasses.get(cls);
		}

		return this.throwNotFound(cls);
	}

	/**
	 * Return the meta model mapped super class type representing the class.
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
		final MappedSuperclassType<?> mappedSuperclass = this.mappedSuperclasses.get(cls);

		if (mappedSuperclass != null) {
			return (MappedSuperclassTypeImpl<X>) mappedSuperclass;
		}

		return this.throwNotFound(cls);
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

		for (final TableGenerator generator : this.tableGenerators.values()) {
			this.tableIdQueues.put(
				generator.name(),
				new TableIdQueue(this.jdbcAdapter, datasource, this.idGeneratorExecuter, generator.pkColumnValue(),
					generator.allocationSize()));
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
		throw new PersistenceException("Type is not persistent " + cls);
	}
}
