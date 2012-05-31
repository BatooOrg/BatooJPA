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
package org.batoo.jpa.core.impl.metamodel;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceException;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MappedSuperclassType;
import javax.persistence.metamodel.Metamodel;

import org.batoo.jpa.common.BatooException;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.jdbc.DataSourceImpl;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.model.BasicTypeImpl;
import org.batoo.jpa.core.impl.model.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.IdentifiableTypeImpl;
import org.batoo.jpa.core.impl.model.ManagedTypeImpl;
import org.batoo.jpa.core.jdbc.DDLMode;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.metadata.MetadataImpl;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The root of the persistence model.
 * 
 * @author hceylan
 * @since $version
 */
public class MetamodelImpl implements Metamodel {

	private static class GeneratorThreadFactory implements ThreadFactory {

		private static volatile int nextThreadNo = 1;

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public Thread newThread(Runnable r) {
			final Thread thread = new Thread(r, "Id Generator - " + GeneratorThreadFactory.nextThreadNo++);

			thread.setPriority(Thread.MAX_PRIORITY);

			return thread;
		}
	}

	private static final BLogger LOG = BLoggerFactory.getLogger(MetamodelImpl.class);

	// TODO Consider making this configurable
	private static final long POLL_TIMEOUT = 60;

	private final JdbcAdaptor jdbcAdaptor;

	private final Map<Class<?>, BasicTypeImpl<?>> basics = Maps.newHashMap();
	private final Map<Class<?>, MappedSuperclassType<?>> mappedSuperclasses = Maps.newHashMap();
	private final Map<Class<?>, EmbeddableType<?>> embeddables = Maps.newHashMap();
	private final Map<Class<?>, EntityTypeImpl<?>> entities = Maps.newHashMap();

	private final Map<String, SequenceGenerator> sequenceGenerators = Maps.newHashMap();
	private final Map<String, TableGenerator> tableGenerators = Maps.newHashMap();

	private final Map<String, SequenceQueue> sequenceQueues = Maps.newHashMap();
	private final Map<String, TableIdQueue> tableIdQueues = Maps.newHashMap();

	private ThreadPoolExecutor idGeneratorExecuter;

	/**
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MetamodelImpl(JdbcAdaptor jdbcAdaptor, MetadataImpl metadata) {
		super();

		this.jdbcAdaptor = jdbcAdaptor;

		final List<EntityMetadata> entities = metadata.getEntities();

		Collections.sort(entities, new Comparator<EntityMetadata>() {

			@Override
			public int compare(EntityMetadata o1, EntityMetadata o2) {
				try {
					final Class<?> c1 = Class.forName(o1.getClassName());
					final Class<?> c2 = Class.forName(o2.getClassName());

					if (c1.isAssignableFrom(c2)) {
						return -1;
					}
					if (c2.isAssignableFrom(c1)) {
						return 1;
					}
				}
				catch (final Exception e) {} // not possible at this stage

				return 0;
			}

		});

		for (final EntityMetadata entity : entities) {
			try {
				EntityTypeImpl<?> entityType;
				final Class<?> clazz = Class.forName(entity.getClassName());

				ManagedTypeImpl<?> parent = null;

				// locate the parent
				Class<?> currentClass = clazz.getSuperclass();
				while ((currentClass != Object.class) && (parent == null)) {
					parent = this.managedType(currentClass);
					currentClass = currentClass.getSuperclass();
				}

				// make sure it extends an identifiable type
				if ((parent != null) && (parent instanceof EmbeddableTypeImpl)) {
					throw new MappingException("An Entity can only extend a MappedSuperclass or another Entity.", entity.getLocator(),
						parent.getLocator());
				}

				entityType = new EntityTypeImpl(this, (IdentifiableTypeImpl) parent, clazz, entity);

				this.entities.put(entityType.getJavaType(), entityType);
			}
			catch (final ClassNotFoundException e) {} // not possible at this time
		}
	}

	/**
	 * Adds the sequence generator to the metamodel
	 * 
	 * @param metadata
	 *            the generator metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized void addSequenceGenerator(SequenceGeneratorMetadata metadata) {
		final SequenceGenerator sequenceGenerator = new SequenceGenerator(metadata);
		this.sequenceGenerators.put(sequenceGenerator.getName(), sequenceGenerator);
	}

	/**
	 * Adds the sequence generator to the metamodel
	 * 
	 * @param metadata
	 *            the generator metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public synchronized void addTableGenerator(TableGeneratorMetadata metadata) {
		final TableGenerator tableGenerator = new TableGenerator(metadata);
		this.tableGenerators.put(metadata.getName(), tableGenerator);
	}

	/**
	 * Creates of returns an existing {@link BasicTypeImpl} of <code>clazz</code>
	 * 
	 * @param clazz
	 *            the class
	 * @return the basic type
	 * 
	 * @param <T>
	 *            the java type of the basic type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public <T> BasicTypeImpl<T> createBasicType(Class<T> clazz) {
		final BasicTypeImpl<T> basicType = (BasicTypeImpl<T>) this.basics.get(clazz);
		if (basicType != null) {
			return basicType;
		}

		return this.lazyCreateBasicType(clazz);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <X> EmbeddableType<X> embeddable(Class<X> clazz) {
		return (EmbeddableType<X>) this.embeddables.get(clazz);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <X> EntityTypeImpl<X> entity(Class<X> clazz) {
		return (EntityTypeImpl<X>) this.entities.get(clazz);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<EmbeddableType<?>> getEmbeddables() {
		return Sets.newHashSet(this.embeddables.values());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<EntityType<?>> getEntities() {
		final Set<EntityType<?>> entities = Sets.newHashSet();

		for (final EntityType<?> entity : this.entities.values()) {
			entities.add(entity);
		}

		return entities;
	}

	/**
	 * Returns the identifiable types.
	 * 
	 * @return the identifiable types
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Set<IdentifiableType<?>> getIdentifiables() {
		final Set<IdentifiableType<?>> identifiables = Sets.newHashSet();

		identifiables.addAll(this.mappedSuperclasses.values());
		identifiables.addAll(this.entities.values());

		return identifiables;
	}

	/**
	 * Returns the JDBC Adaptor.
	 * 
	 * @return the JDBC Adaptor
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public JdbcAdaptor getJdbcAdaptor() {
		return this.jdbcAdaptor;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<ManagedType<?>> getManagedTypes() {
		final Set<ManagedType<?>> managedTypes = Sets.newHashSet();

		managedTypes.addAll(this.embeddables.values());
		managedTypes.addAll(this.mappedSuperclasses.values());
		managedTypes.addAll(this.entities.values());

		return managedTypes;
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
			return this.sequenceQueues.get(generator).poll(MetamodelImpl.POLL_TIMEOUT, TimeUnit.SECONDS);
		}
		catch (final InterruptedException e) {
			throw new PersistenceException("Unable to retrieve next sequence " + generator + " in allowed " + MetamodelImpl.POLL_TIMEOUT
				+ " seconds");
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
			return this.tableIdQueues.get(generator).poll(MetamodelImpl.POLL_TIMEOUT, TimeUnit.SECONDS);
		}
		catch (final InterruptedException e) {
			throw new PersistenceException("Unable to retrieve next sequence " + generator + " in allowed " + MetamodelImpl.POLL_TIMEOUT
				+ " seconds");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private synchronized <X> BasicTypeImpl<X> lazyCreateBasicType(Class<X> clazz) {
		// skip if annotated with @Entity, @MappedSuperClass or @Embeddable
		if ((clazz.getAnnotation(Entity.class) != null) //
			|| (clazz.getAnnotation(MappedSuperclass.class) != null) //
			|| (clazz.getAnnotation(Embeddable.class) != null)) {

			return null;
		}

		if (Serializable.class.isAssignableFrom(clazz) || clazz.isPrimitive()) {
			final BasicTypeImpl basicType = new BasicTypeImpl(this, clazz);
			this.basics.put(clazz, basicType);

			return basicType;
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <X> ManagedTypeImpl<X> managedType(Class<X> clazz) {
		ManagedTypeImpl<X> managedType = (ManagedTypeImpl<X>) this.embeddables.get(clazz);
		if (managedType != null) {
			return managedType;
		}

		managedType = (ManagedTypeImpl<X>) this.mappedSuperclasses.get(clazz);
		if (managedType != null) {
			return managedType;
		}

		return (ManagedTypeImpl<X>) this.entities.get(clazz);
	}

	/**
	 * @param datasource
	 * @param schemas
	 * @param ddlMode
	 * @param type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performForeignKeysDdl(DataSourceImpl datasource, DDLMode ddlMode, EntityTypeImpl<?> type) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param datasource
	 *            the datasource
	 * @param drop
	 *            the DDL Mode
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performSequencesDdl(DataSourceImpl datasource, DDLMode drop) {
		for (final SequenceGenerator sequenceGenerator : this.sequenceGenerators.values()) {
			try {
				this.jdbcAdaptor.createSequenceIfNecessary(datasource, sequenceGenerator);
			}
			catch (final SQLException e) {
				throw new MappingException("DDL operation failed on table generator" + sequenceGenerator.getName(), e);
			}
		}
	}

	/**
	 * @param datasource
	 *            the datasource
	 * @param ddlMode
	 *            the DDL Mode
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performTableGeneratorsDdl(DataSourceImpl datasource, DDLMode ddlMode) {
		for (final TableGenerator tableGenerator : this.tableGenerators.values()) {
			try {
				this.jdbcAdaptor.createTableGeneratorIfNecessary(datasource, tableGenerator);
			}
			catch (final SQLException e) {
				throw new MappingException("DDL operation failed on table generator" + tableGenerator.getName(), e);
			}
		}
	}

	/**
	 * Performs the DDL operations.
	 * 
	 * @param datasource
	 *            the datasource
	 * @param ddlMode
	 *            the DDL Mode
	 * @param type
	 *            the type to perform DDL against
	 * @throws BatooException
	 *             thrown in case of an underlying exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performTablesDddl(DataSourceImpl datasource, DDLMode ddlMode, EntityTypeImpl<?> type) {
		MetamodelImpl.LOG.info("Performing DDL operations for {0}, mode {2}", this, ddlMode);

		for (final EntityTable table : type.getDeclaredTables()) {
			try {
				this.jdbcAdaptor.createTable(table, datasource);
			}
			catch (final SQLException e) {
				throw new MappingException("DDL operation failed on table " + table.getName(), e);
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
			new GeneratorThreadFactory());

		for (final SequenceGenerator generator : this.sequenceGenerators.values()) {
			this.sequenceQueues.put(generator.getName(), new SequenceQueue(this.jdbcAdaptor, datasource, this.idGeneratorExecuter,
				generator.getSequenceName(), generator.getAllocationSize()));
		}

		for (final TableGenerator generator : this.tableGenerators.values()) {
			this.tableIdQueues.put(generator.getName(), new TableIdQueue(this.jdbcAdaptor, datasource, this.idGeneratorExecuter, generator));
		}
	}
}
