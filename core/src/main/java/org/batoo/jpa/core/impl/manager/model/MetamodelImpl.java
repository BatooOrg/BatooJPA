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
package org.batoo.jpa.core.impl.manager.model;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MappedSuperclassType;
import javax.persistence.metamodel.Metamodel;

import org.batoo.jpa.common.BatooException;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.manager.jdbc.AbstractTable;
import org.batoo.jpa.core.impl.manager.jdbc.DataSourceImpl;
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

	private static final BLogger LOG = BLoggerFactory.getLogger(MetamodelImpl.class);

	private final JdbcAdaptor jdbcAdaptor;

	private final Map<Class<?>, BasicTypeImpl<?>> basics = Maps.newHashMap();
	private final Map<Class<?>, MappedSuperclassType<?>> mappedSuperclasses = Maps.newHashMap();
	private final Map<Class<?>, EmbeddableType<?>> embeddables = Maps.newHashMap();
	private final Map<Class<?>, EntityType<?>> entities = Maps.newHashMap();

	private final Map<String, SequenceGenerator> sequenceGenerators = Maps.newHashMap();
	private final Map<String, TableGenerator> tableGenerators = Maps.newHashMap();

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
		this.sequenceGenerators.put(metadata.getName(), new SequenceGenerator(metadata));
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
	public synchronized void addSequenceGenerator(TableGeneratorMetadata metadata) {
		this.tableGenerators.put(metadata.getName(), new TableGenerator(metadata));
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
	public <X> EntityType<X> entity(Class<X> clazz) {
		return (EntityType<X>) this.entities.get(clazz);
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
		return Sets.newHashSet(this.entities.values());
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
	 * Performs the DDL operations.
	 * 
	 * @param datasource
	 *            the datasource
	 * @param schemas
	 *            the schema registry
	 * @param ddlMode
	 *            the DDL Mode
	 * @param firstPass
	 *            if the first or second pass
	 * @param type
	 *            the type to perform DDL against
	 * @throws BatooException
	 *             thrown in case of an underlying exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performTables(DataSourceImpl datasource, Set<String> schemas, DDLMode ddlMode, boolean firstPass, EntityTypeImpl<?> type) {
		MetamodelImpl.LOG.info("Performing DDL operations for {0}, mode {2}", this, ddlMode);

		final Collection<AbstractTable> tables = type.getTables();

		for (final AbstractTable table : tables) {
			try {
				this.jdbcAdaptor.createTable(table, datasource, schemas);
			}
			catch (final SQLException e) {
				throw new MappingException("DDL operation failed on " + table.getName(), e);
			}

		}
	}
}
