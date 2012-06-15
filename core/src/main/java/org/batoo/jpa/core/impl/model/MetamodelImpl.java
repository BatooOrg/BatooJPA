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

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
import org.batoo.jpa.core.impl.jdbc.ForeignKey;
import org.batoo.jpa.core.impl.jdbc.JoinTable;
import org.batoo.jpa.core.impl.manager.EntityManagerFactoryImpl;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.type.BasicTypeImpl;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.type.IdentifiableTypeImpl;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.core.impl.model.type.MappedSuperclassTypeImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;
import org.batoo.jpa.core.jdbc.DDLMode;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.impl.metadata.MetadataImpl;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.type.EmbeddableMetadata;
import org.batoo.jpa.parser.metadata.type.EntityMetadata;
import org.batoo.jpa.parser.metadata.type.ManagedTypeMetadata;
import org.batoo.jpa.parser.metadata.type.MappedSuperclassMetadata;

import com.google.common.collect.Lists;
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

	private EntityManagerFactoryImpl emf;
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
	 * @param entityManagerFactory
	 *            the entity manager factory
	 * @param jdbcAdaptor
	 *            the JDBC Adaptor
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MetamodelImpl(EntityManagerFactoryImpl entityManagerFactory, JdbcAdaptor jdbcAdaptor, MetadataImpl metadata) {
		super();

		this.emf = entityManagerFactory;
		this.jdbcAdaptor = jdbcAdaptor;

		final List<ManagedTypeMetadata> entities = Lists.newArrayList(metadata.getEntityMappings());
		final List<ManagedTypeMetadata> sortedEntities = Lists.newArrayList();

		// sort so that the embeddables are first
		Collections.sort(entities, new Comparator<ManagedTypeMetadata>() {

			@Override
			public int compare(ManagedTypeMetadata o1, ManagedTypeMetadata o2) {
				if (o1 instanceof EmbeddableMetadata) {
					return -1;
				}

				if (o2 instanceof EmbeddableMetadata) {
					return 1;
				}

				return 0;
			}
		});

		// sort by inheritance
		try {
			while (entities.size() > 0) {
				for (final Iterator<ManagedTypeMetadata> i = entities.iterator(); i.hasNext();) {
					final ManagedTypeMetadata entity = i.next();
					final Class<?> c1 = Class.forName(entity.getClassName());

					boolean independent = true;
					for (final ManagedTypeMetadata entity2 : entities) {
						if (entity == entity2) {
							continue;
						}

						final Class<?> c2 = Class.forName(entity2.getClassName());

						if (c2.isAssignableFrom(c1)) {
							independent = false;
							break;
						}
					}

					if (independent) {
						i.remove();
						sortedEntities.add(entity);
					}
				}
			}
		}
		catch (final Exception e) {
			e.printStackTrace();
		} // not possible at this stage

		for (final ManagedTypeMetadata type : sortedEntities) {
			try {
				final Class<?> clazz = Class.forName(type.getClassName());

				ManagedTypeImpl<?> parent = null;

				// locate the parent
				Class<?> currentClass = clazz.getSuperclass();
				while ((currentClass != Object.class) && (parent == null)) {
					parent = this.managedType(currentClass);
					currentClass = currentClass.getSuperclass();
				}

				if (type instanceof EntityMetadata) {
					// make sure it extends an identifiable type
					if ((parent != null) && !(parent instanceof IdentifiableTypeImpl)) {
						throw new MappingException("Entities can only extend MappedSuperclasses or other Entities.", type.getLocator(), parent.getLocator());
					}

					final EntityTypeImpl entity = new EntityTypeImpl(this, (IdentifiableTypeImpl) parent, clazz, (EntityMetadata) type);

					this.entities.put(entity.getJavaType(), entity);
				}
				else if (type instanceof MappedSuperclassMetadata) {
					// make sure it extends a mapped superclass type
					if ((parent != null) && !(parent instanceof MappedSuperclassTypeImpl)) {
						throw new MappingException("MappedSuperclasses can only extend other MappedSuperclasses.", type.getLocator(), parent.getLocator());
					}

					final MappedSuperclassTypeImpl mappedSuperclass = new MappedSuperclassTypeImpl(this, (MappedSuperclassTypeImpl) parent, clazz,
						(MappedSuperclassMetadata) type);
					this.mappedSuperclasses.put(mappedSuperclass.getJavaType(), mappedSuperclass);
				}
				if (type instanceof EmbeddableMetadata) {
					// make sure it extends a embeddable type
					if ((parent != null) && !(parent instanceof EmbeddableTypeImpl)) {
						throw new MappingException("Embeddables can only extend a other Embeddables.", type.getLocator(), parent.getLocator());
					}

					final EmbeddableTypeImpl embeddable = new EmbeddableTypeImpl(this, clazz, (EmbeddableMetadata) type);
					this.embeddables.put(embeddable.getJavaType(), embeddable);
				}

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
		this.tableGenerators.put(tableGenerator.getName(), tableGenerator);
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
	public <X> EmbeddableTypeImpl<X> embeddable(Class<X> clazz) {
		return (EmbeddableTypeImpl<X>) this.embeddables.get(clazz);
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
	 * Returns the entity manager factory.
	 * 
	 * @return the entity manager factory
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityManagerFactoryImpl getEntityManagerFactory() {
		return this.emf;
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
			throw new PersistenceException("Unable to retrieve next sequence " + generator + " in allowed " + MetamodelImpl.POLL_TIMEOUT + " seconds");
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
			throw new PersistenceException("Unable to retrieve next sequence " + generator + " in allowed " + MetamodelImpl.POLL_TIMEOUT + " seconds");
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
	 * Performs the foreign key DDL operations.
	 * 
	 * @param datasource
	 *            the datasource
	 * @param ddlMode
	 *            the DDL Mode
	 * @param entity
	 *            the entity to perform DDL against
	 * @throws BatooException
	 *             thrown in case of an underlying exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performForeignKeysDdl(DataSourceImpl datasource, DDLMode ddlMode, EntityTypeImpl<?> entity) {
		MetamodelImpl.LOG.info("Performing foreign key DDL operations for entiy {0}, mode {1}", entity.getName(), ddlMode);

		for (final EntityTable table : entity.getTables()) {
			// skip parent tables
			if (table.getEntity() != entity) {
				continue;
			}
			try {
				MetamodelImpl.LOG.info("Performing foreign key DDL operations for table {0}, mode {1}", table.getName(), ddlMode);

				for (final ForeignKey foreignKey : table.getForeignKeys()) {
					this.jdbcAdaptor.createForeignKey(datasource, foreignKey);
				}
			}
			catch (final SQLException e) {
				throw new MappingException("DDL operation failed on table " + table.getName(), e);
			}
		}

		for (final AssociationMapping<?, ?, ?> attribute : entity.getAssociations()) {
			final JoinTable table = attribute.getJoinTable();
			// skip not applicable join tables
			if ((table == null) || (table.getEntity() != entity)) {
				continue;
			}

			MetamodelImpl.LOG.info("Performing foreign key DDL operations for join table {0}, mode {1}", table.getName(), ddlMode);

			for (final ForeignKey foreignKey : table.getForeignKeys()) {
				try {
					this.jdbcAdaptor.createForeignKey(datasource, foreignKey);
				}
				catch (final SQLException e) {
					throw new MappingException("DDL operation failed on table " + table.getName(), e);
				}
			}
		}
	}

	/**
	 * Performs the sequence generators DDL operations.
	 * 
	 * @param datasource
	 *            the datasource
	 * @param ddlMode
	 *            the DDL Mode
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performSequencesDdl(DataSourceImpl datasource, DDLMode ddlMode) {
		for (final SequenceGenerator sequenceGenerator : this.sequenceGenerators.values()) {
			try {
				MetamodelImpl.LOG.info("Performing DDL operations for sequence generators for {0}, mode {1}", sequenceGenerator.getName(), ddlMode);

				this.jdbcAdaptor.createSequenceIfNecessary(datasource, sequenceGenerator);
			}
			catch (final SQLException e) {
				throw new MappingException("DDL operation failed on table generator" + sequenceGenerator.getName(), e);
			}
		}
	}

	/**
	 * Performs the table generator DDL operations.
	 * 
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
				MetamodelImpl.LOG.info("Performing DDL operations for sequence generators for mode table {1}, mode {0}", tableGenerator.getName(), ddlMode);

				this.jdbcAdaptor.createTableGeneratorIfNecessary(datasource, tableGenerator);
			}
			catch (final SQLException e) {
				throw new MappingException("DDL operation failed on table generator" + tableGenerator.getName(), e);
			}
		}
	}

	/**
	 * Performs the table DDL operations.
	 * 
	 * @param datasource
	 *            the datasource
	 * @param ddlMode
	 *            the DDL Mode
	 * @param entity
	 *            the entity to perform DDL against
	 * @throws BatooException
	 *             thrown in case of an underlying exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performTablesDdl(DataSourceImpl datasource, DDLMode ddlMode, EntityTypeImpl<?> entity) {
		MetamodelImpl.LOG.info("Performing DDL operations for entity {0}, mode {1}", entity.getName(), ddlMode);

		// create the entity tables
		for (final EntityTable table : entity.getTables()) {
			// if table belongs to parent then skip
			if (table.getEntity() != entity) {
				continue;
			}

			try {
				MetamodelImpl.LOG.info("Performing DDL operations for {0}, mode {1}", table.getName(), ddlMode);

				this.jdbcAdaptor.createTable(table, datasource);
			}
			catch (final SQLException e) {
				throw new MappingException("DDL operation failed on table " + table.getName(), e);
			}
		}

		// create the join tables
		for (final AssociationMapping<?, ?, ?> attribute : entity.getAssociations()) {
			final JoinTable table = attribute.getJoinTable();

			// skip not applicable tables
			if ((table == null) || (table.getEntity() != entity)) {
				continue;
			}

			try {
				this.jdbcAdaptor.createTable(attribute.getJoinTable(), datasource);
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
			this.sequenceQueues.put(generator.getName(), new SequenceQueue(this.jdbcAdaptor, datasource, this.idGeneratorExecuter, generator.getSequenceName(),
				generator.getAllocationSize()));
		}

		for (final TableGenerator generator : this.tableGenerators.values()) {
			this.tableIdQueues.put(generator.getName(), new TableIdQueue(this.jdbcAdaptor, datasource, this.idGeneratorExecuter, generator));
		}
	}

	/**
	 * Stops the id generators.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void stopIdGenerators() {
		this.idGeneratorExecuter.shutdownNow();
	}

	/**
	 * Returns the type corresponding to the <code>clazz</code>.
	 * 
	 * @param clazz
	 *            the class of the type
	 * @param <X>
	 *            the expected type of the type
	 * @return the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public <X> TypeImpl<X> type(Class<X> clazz) {
		final BasicTypeImpl<?> basic = this.basics.get(clazz);
		if (basic != null) {
			return (TypeImpl<X>) basic;
		}

		return this.managedType(clazz);
	}
}
