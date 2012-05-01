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
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Types;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.MapKeyJoinColumns;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.types.AttributeImpl;
import org.batoo.jpa.core.impl.types.CollectionAttributeImpl;
import org.batoo.jpa.core.impl.types.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.types.EntityTypeImpl;
import org.batoo.jpa.core.impl.types.ListAttributeImpl;
import org.batoo.jpa.core.impl.types.ManagedTypeImpl;
import org.batoo.jpa.core.impl.types.MapAttributeImpl;
import org.batoo.jpa.core.impl.types.MappedSuperclassTypeImpl;
import org.batoo.jpa.core.impl.types.SetAttributeImpl;
import org.batoo.jpa.core.impl.types.SingularAttributeImpl;
import org.batoo.jpa.core.jdbc.IdType;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class TypeFactory {

	public static final BLogger LOG = BLogger.getLogger(TypeFactory.class);

	private static Map<Class<? extends Annotation>, Set<Class<? extends Annotation>>> CONFLICTS = TypeFactory.createConflicts();

	public static void collect(MetamodelImpl metaModel, Reflections r, final Class<? extends Annotation> annotation)
		throws MappingException {
		final List<Class<?>> types = Lists.newArrayList(r.getTypesAnnotatedWith(annotation));

		TypeFactory.sortClasses(types);

		for (final Class<?> type : types) {
			TypeFactory.forType(metaModel, annotation, type);
		}

	}

	/**
	 * Returns set of annotations that the annotation conflicts.
	 * 
	 * @param annotation
	 * @return set of annotations that the annotation conflicts
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static Set<Class<? extends Annotation>> conflictsFor(Class<? extends Annotation> annotation) {
		final Set<Class<? extends Annotation>> elements = CONFLICTS.get(annotation);

		if (elements == null) {
			return Collections.emptySet();
		}

		return Sets.newHashSet(elements);
	}

	private static Map<Class<? extends Annotation>, Set<Class<? extends Annotation>>> createConflicts() {
		final Map<Class<? extends Annotation>, Set<Class<? extends Annotation>>> conflicts = Maps.newHashMap();

		Set<Class<? extends Annotation>> items;

		// @Id
		conflicts.put(Id.class, items = Sets.newHashSet());
		items.add(OneToMany.class);
		items.add(ManyToMany.class);
		items.add(ElementCollection.class);
		items.add(CollectionTable.class);
		items.add(Version.class);
		items.add(Enumerated.class);
		items.add(AssociationOverride.class);
		items.add(AttributeOverrides.class);
		items.add(AttributeOverride.class);
		items.add(MapKeyColumn.class);
		items.add(MapKeyJoinColumns.class);
		items.add(MapKeyJoinColumn.class);

		// @Basic
		conflicts.put(Basic.class, items = Sets.newHashSet());
		items.add(OneToMany.class);
		items.add(ManyToOne.class);
		items.add(ManyToMany.class);
		items.add(JoinColumn.class);
		items.add(Embedded.class);
		items.add(AttributeOverrides.class);
		items.add(AttributeOverride.class);
		items.add(AssociationOverrides.class);
		items.add(AssociationOverride.class);
		items.add(MapKeyColumn.class);
		items.add(MapKeyJoinColumns.class);
		items.add(MapKeyJoinColumn.class);

		// @OneToOne
		conflicts.put(OneToOne.class, items = Sets.newHashSet());
		items.add(OneToMany.class);
		items.add(ManyToOne.class);
		items.add(ManyToMany.class);
		items.add(ElementCollection.class);
		items.add(CollectionTable.class);
		items.add(Basic.class);
		items.add(Embedded.class);
		items.add(Column.class);
		items.add(Enumerated.class);
		items.add(Temporal.class);
		items.add(Version.class);
		items.add(AttributeOverrides.class);
		items.add(AttributeOverride.class);
		items.add(MapKeyClass.class);
		items.add(MapKeyColumn.class);
		items.add(MapKeyJoinColumns.class);
		items.add(MapKeyJoinColumn.class);

		// @OneToMany
		conflicts.put(OneToMany.class, items = Sets.newHashSet());
		items.add(OneToOne.class);
		items.add(ManyToOne.class);
		items.add(ManyToMany.class);
		items.add(ElementCollection.class);
		items.add(CollectionTable.class);
		items.add(Basic.class);
		items.add(Embedded.class);
		items.add(Enumerated.class);
		items.add(Column.class);
		items.add(Temporal.class);
		items.add(Version.class);
		items.add(AttributeOverrides.class);
		items.add(AttributeOverride.class);

		// @ManyToOne
		conflicts.put(ManyToOne.class, items = Sets.newHashSet());
		items.add(OneToOne.class);
		items.add(OneToMany.class);
		items.add(ManyToMany.class);
		items.add(ElementCollection.class);
		items.add(CollectionTable.class);
		items.add(Basic.class);
		items.add(Embedded.class);
		items.add(Enumerated.class);
		items.add(Column.class);
		items.add(Temporal.class);
		items.add(Version.class);
		items.add(AttributeOverrides.class);
		items.add(AttributeOverride.class);
		items.add(MapKeyColumn.class);
		items.add(MapKeyJoinColumns.class);
		items.add(MapKeyJoinColumn.class);

		// @ManyToOne
		conflicts.put(ManyToMany.class, items = Sets.newHashSet());
		items.add(OneToOne.class);
		items.add(OneToMany.class);
		items.add(ManyToOne.class);
		items.add(ElementCollection.class);
		items.add(CollectionTable.class);
		items.add(Basic.class);
		items.add(Embedded.class);
		items.add(Enumerated.class);
		items.add(Column.class);
		items.add(Temporal.class);
		items.add(Version.class);
		items.add(AttributeOverrides.class);
		items.add(AttributeOverride.class);

		// @Version
		conflicts.put(Version.class, items = Sets.newHashSet());
		items.add(Id.class);
		items.add(OneToOne.class);
		items.add(ManyToOne.class);
		items.add(ManyToMany.class);
		items.add(Embedded.class);
		items.add(Enumerated.class);
		items.add(Column.class);
		items.add(Version.class);
		items.add(AttributeOverrides.class);
		items.add(AttributeOverride.class);
		items.add(AssociationOverrides.class);
		items.add(AssociationOverride.class);
		items.add(ElementCollection.class);
		items.add(CollectionTable.class);

		// @Column
		conflicts.put(Column.class, items = Sets.newHashSet());
		items.add(Embedded.class);
		items.add(OneToOne.class);
		items.add(OneToMany.class);
		items.add(ManyToOne.class);
		items.add(ManyToMany.class);
		items.add(JoinColumns.class);
		items.add(JoinColumn.class);
		items.add(AttributeOverrides.class);
		items.add(AttributeOverride.class);
		items.add(AssociationOverrides.class);
		items.add(AssociationOverride.class);

		// @JoinColumn
		conflicts.put(JoinColumn.class, items = Sets.newHashSet());
		items.add(JoinColumns.class);
		items.add(Column.class);
		items.add(AttributeOverrides.class);
		items.add(AttributeOverride.class);
		items.add(ElementCollection.class);
		items.add(CollectionTable.class);

		// @JoinColumns
		conflicts.put(JoinColumns.class, items = Sets.newHashSet());
		items.add(JoinColumn.class);
		items.add(Column.class);
		items.add(AttributeOverrides.class);
		items.add(AttributeOverride.class);
		items.add(ElementCollection.class);
		items.add(CollectionTable.class);

		// @AttributeOverride
		conflicts.put(AttributeOverride.class, items = Sets.newHashSet());
		items.add(AssociationOverrides.class);

		// @AttributeOverrides
		conflicts.put(AttributeOverrides.class, items = Sets.newHashSet());
		items.add(AttributeOverride.class);

		// @AssociationOverrides
		conflicts.put(AssociationOverrides.class, items = Sets.newHashSet());
		items.add(AssociationOverride.class);

		// @AssociationOverride
		conflicts.put(AssociationOverride.class, items = Sets.newHashSet());
		items.add(AssociationOverrides.class);

		// @MapKeyJoinColumn
		conflicts.put(MapKeyJoinColumn.class, items = Sets.newHashSet());
		items.add(MapKeyJoinColumns.class);

		// @MapKeyJoinColumns
		conflicts.put(MapKeyJoinColumns.class, items = Sets.newHashSet());
		items.add(MapKeyJoinColumn.class);

		// @Lob
		conflicts.put(Lob.class, items = Sets.newHashSet());
		items.add(Temporal.class);
		items.add(Enumerated.class);
		items.add(OneToOne.class);
		items.add(OneToMany.class);
		items.add(ManyToOne.class);
		items.add(ManyToMany.class);
		items.add(JoinColumn.class);
		items.add(JoinColumns.class);
		items.add(ElementCollection.class);
		items.add(Embedded.class);
		items.add(AttributeOverride.class);
		items.add(AttributeOverrides.class);
		items.add(AssociationOverride.class);
		items.add(AssociationOverrides.class);
		items.add(MapKeyColumn.class);
		items.add(MapKeyJoinColumns.class);
		items.add(MapKeyJoinColumn.class);

		// @SequenceGenerator
		conflicts.put(SequenceGenerator.class, items = Sets.newHashSet());
		items.add(TableGenerator.class);

		// @TableGenerator
		conflicts.put(TableGenerator.class, items = Sets.newHashSet());
		items.add(SequenceGenerator.class);

		// @SecondaryTables
		conflicts.put(SecondaryTables.class, items = Sets.newHashSet());
		items.add(SecondaryTable.class);

		// @Entity
		conflicts.put(Entity.class, items = Sets.newHashSet());
		items.add(MappedSuperclass.class);

		// @Entity
		conflicts.put(Embeddable.class, items = Sets.newHashSet());
		items.add(Entity.class);
		items.add(MappedSuperclass.class);
		items.add(SecondaryTable.class);
		items.add(Table.class);

		return conflicts;
	}

	/**
	 * Returns an {@link Attribute} for the field
	 * 
	 * @param field
	 *            the field
	 * @param clazz
	 *            the type of the field
	 * 
	 * @since $version
	 * @author hceylan
	 * @throws BatooException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <X> Attribute<X, ?> forType(ManagedTypeImpl<X> owner, Field field, Class<?> clazz) throws BatooException {
		// If array type, persistent type must be a basic type
		if (clazz.isArray() && TypeFactory.isBasicType(clazz.getComponentType(), true)) {
			return new SingularAttributeImpl(owner, field, clazz);
		}

		if (Map.class.isAssignableFrom(clazz)) {
			return new MapAttributeImpl(owner, field, clazz);
		}

		if (List.class.isAssignableFrom(clazz)) {
			return new ListAttributeImpl(owner, field, clazz);
		}

		if (Set.class.isAssignableFrom(clazz)) {
			return new SetAttributeImpl(owner, field, clazz);
		}

		if (Collection.class.isAssignableFrom(clazz)) {
			return new CollectionAttributeImpl(owner, field, clazz);
		}

		return new SingularAttributeImpl(owner, field, clazz);
	}

	/**
	 * Creates a managed type.
	 * 
	 * @param annotation
	 *            the annotation found on the class
	 * @param type
	 *            the type to manage
	 * @return the {@link ManagedType}
	 * 
	 * @throws MappingException
	 *             throw in case of a mapping error
	 * 
	 * @since $version
	 * @author hceylan
	 * @param metaModel
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void forType(MetamodelImpl metaModel, Class<? extends Annotation> annotation, Class<?> type) throws MappingException {
		if (annotation == Entity.class) {
			LOG.debug("Found Entity: {0}", type.getCanonicalName());

			// is the type extending a mapped super class
			try {
				final MappedSuperclassTypeImpl<?> superclass = metaModel.mappedSuperclass(type);
				new EntityTypeImpl(metaModel, superclass, type);
				return;
			}
			catch (final Exception e) {}

			// is the class extending another entity
			try {
				final EntityTypeImpl<?> superclass = metaModel.entity(type);
				new EntityTypeImpl(metaModel, superclass, type);
				return;
			}
			catch (final Exception e) {}

			// top type class
			new EntityTypeImpl(metaModel, null, type);
		}

		if (annotation == MappedSuperclass.class) {
			LOG.debug("Found MappedSuperType: {0}", type.getCanonicalName());

			// is the type extending a mapped super class
			try {
				final MappedSuperclassTypeImpl<?> superclass = metaModel.mappedSuperclass(type);
				new MappedSuperclassTypeImpl(metaModel, superclass, type);
				return;
			}
			catch (final Exception e) {}

			new MappedSuperclassTypeImpl(metaModel, null, type);
		}

		if (annotation == Embeddable.class) {
			LOG.debug("Found EmbeddableType: {0}", type.getCanonicalName());

			new EmbeddableTypeImpl(metaModel, type);
		}
	}

	/**
	 * @param strategy
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static IdType getIdType(GenerationType strategy) {
		switch (strategy) {
			case IDENTITY:
				return IdType.IDENTITY;
			case SEQUENCE:
				return IdType.SEQUENCE;
			case TABLE:
				return IdType.TABLE;
			default:
				return null;
		}
	}

	/**
	 * Returns the SQL type of the java type
	 * 
	 * @param javaType
	 *            the java type
	 * @param temporal
	 *            temporal type of the type
	 * @param enumType
	 *            enum type of the type
	 * @param isLob
	 *            if is a lob type
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static int getSqlType(Class<?> javaType, TemporalType temporal, EnumType enumType, boolean isLob) {
		if (isLob) {
			if (Character.class.isAssignableFrom(javaType) || char.class.isAssignableFrom(javaType.getComponentType())
				|| String.class.isAssignableFrom(javaType)) {
				return Types.CLOB;
			}

			return Types.BLOB;
		}

		if (javaType.isArray()) {
			if (Character.class.isAssignableFrom(javaType.getComponentType()) || char.class.isAssignableFrom(javaType.getComponentType())) {
				return Types.CLOB;
			}

			return Types.BLOB;
		}

		if (Boolean.class.isAssignableFrom(javaType) || boolean.class.isAssignableFrom(javaType)) {
			return Types.BOOLEAN;
		}

		if (String.class.isAssignableFrom(javaType)) {
			return Types.VARCHAR;
		}

		if ((Calendar.class.isAssignableFrom(javaType)) || (Date.class.isAssignableFrom(javaType))) {
			if (temporal == null) {
				return Types.TIMESTAMP;
			}

			switch (temporal) {
				case DATE:
					return Types.DATE;
				case TIME:
					return Types.TIME;
				case TIMESTAMP:
					return Types.TIMESTAMP;
			}
		}

		if (Enum.class.isAssignableFrom(javaType)) {
			if ((enumType == null) || (enumType == EnumType.ORDINAL)) {
				return Types.SMALLINT;
			}

			return Types.VARCHAR;
		}

		if (Integer.class.isAssignableFrom(javaType) || int.class.isAssignableFrom(javaType)) {
			return Types.INTEGER;
		}

		if (Byte.class.isAssignableFrom(javaType) || byte.class.isAssignableFrom(javaType)) {
			return Types.TINYINT;
		}

		if (Character.class.isAssignableFrom(javaType) || char.class.isAssignableFrom(javaType)) {
			return Types.CHAR;
		}

		if (Short.class.isAssignableFrom(javaType) || short.class.isAssignableFrom(javaType)) {
			return Types.SMALLINT;
		}

		if (Long.class.isAssignableFrom(javaType) || long.class.isAssignableFrom(javaType)) {
			return Types.BIGINT;
		}

		if (Float.class.isAssignableFrom(javaType) || float.class.isAssignableFrom(javaType)) {
			return Types.FLOAT;
		}

		if (Double.class.isAssignableFrom(javaType) || double.class.isAssignableFrom(javaType)) {
			return Types.DOUBLE;
		}

		if (java.sql.Date.class.isAssignableFrom(javaType)) {
			return Types.DATE;
		}

		if (java.sql.Time.class.isAssignableFrom(javaType)) {
			return Types.TIME;
		}

		if (java.sql.Timestamp.class.isAssignableFrom(javaType)) {
			return Types.TIMESTAMP;
		}

		if (BigDecimal.class.isAssignableFrom(javaType) || BigInteger.class.isAssignableFrom(javaType)) {
			return Types.DECIMAL;
		}

		throw new IllegalArgumentException("Cannot determine sql type: " + javaType);
	}

	/**
	 * Returns the sql type for the inheritence
	 * 
	 * @param inheritance
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static int getSqlType(EntityInheritence inheritance) {
		switch (inheritance.getType()) {
			case CHAR:
				return Types.CHAR;
			case INTEGER:
				return Types.INTEGER;
			default:
				return Types.VARCHAR;
		}
	}

	private static boolean isBasicType(Class<?> cls, boolean array) {
		// basic array types
		if (Byte.class.isAssignableFrom(cls) //
			|| byte.class.isAssignableFrom(cls) //
			|| Character.class.isAssignableFrom(cls) //
			|| char.class.isAssignableFrom(cls)) {
			return true;
		}

		if (array) {
			return false;
		}

		return Short.class.isAssignableFrom(cls) //
			|| Integer.class.isAssignableFrom(cls) //
			|| Long.class.isAssignableFrom(cls) //
			|| Float.class.isAssignableFrom(cls) //
			|| Double.class.isAssignableFrom(cls) //
			|| Boolean.class.isAssignableFrom(cls) //
			|| short.class.isAssignableFrom(cls) //
			|| int.class.isAssignableFrom(cls) //
			|| long.class.isAssignableFrom(cls) //
			|| float.class.isAssignableFrom(cls) //
			|| double.class.isAssignableFrom(cls) //
			|| boolean.class.isAssignableFrom(cls) //
			|| Enum.class.isAssignableFrom(cls) //
			|| String.class.isAssignableFrom(cls) //
			|| BigInteger.class.isAssignableFrom(cls) //
			|| BigDecimal.class.isAssignableFrom(cls) //
			|| Calendar.class.isAssignableFrom(cls) //
			|| Date.class.isAssignableFrom(cls) //
			|| java.sql.Date.class.isAssignableFrom(cls) //
			|| java.sql.Time.class.isAssignableFrom(cls) //
			|| java.sql.Timestamp.class.isAssignableFrom(cls) //
			|| Serializable.class.isAssignableFrom(cls);
	}

	/**
	 * Returns path as string seperated with dots
	 * 
	 * @param path
	 *            the path
	 * @return the path as string
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static String pathAsString(Deque<AttributeImpl<?, ?>> path) {
		final Collection<String> parts = Collections2.transform(path, new Function<AttributeImpl<?, ?>, String>() {

			@Override
			public String apply(AttributeImpl<?, ?> input) {
				return input.getName();
			}
		});

		return Joiner.on(".").join(parts);
	}

	/**
	 * Scans for list of classes.
	 * 
	 * @param metaModel
	 *            the meta model
	 * @param managedClassNames
	 *            the list of managed class names
	 * @throws MappingException
	 *             thrown in case of a mapping error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static void scanForArtifacts(MetamodelImpl metaModel, List<String> managedClassNames) throws MappingException {
		final List<Class<?>> embeddables = Lists.newArrayList();
		final List<Class<?>> mappedSuperclasses = Lists.newArrayList();
		final List<Class<?>> entities = Lists.newArrayList();

		for (final String className : managedClassNames) {
			try {
				final Class<?> clazz = Class.forName(className);
				if (clazz.getAnnotation(Embeddable.class) != null) {
					embeddables.add(clazz);
				}
				else if (clazz.getAnnotation(MappedSuperclass.class) != null) {
					mappedSuperclasses.add(clazz);
				}
				else if (clazz.getAnnotation(Entity.class) != null) {
					entities.add(clazz);
				}
				else {
					throw new MappingException(
						"Class doe not have neither of the @Embeddable, @MappedSuperClass, @Entity annotations declared");
				}
			}
			catch (final ClassNotFoundException e) {
				throw new MappingException("Managed persistent class " + className + " cannot be loaded");
			}
		}

		TypeFactory.sortClasses(embeddables);
		TypeFactory.sortClasses(mappedSuperclasses);
		TypeFactory.sortClasses(entities);

		for (final Class<?> clazz : embeddables) {
			TypeFactory.forType(metaModel, Embeddable.class, clazz);
		}

		for (final Class<?> clazz : mappedSuperclasses) {
			TypeFactory.forType(metaModel, MappedSuperclass.class, clazz);
		}

		for (final Class<?> clazz : entities) {
			TypeFactory.forType(metaModel, Entity.class, clazz);
		}

	}

	/**
	 * Scans for the entities.
	 * <p>
	 * if url is not null then only classes in the resources exist in the resource url pointing is scanned. if url is null then the full
	 * classpath will be scanned for the entities.
	 * <p>
	 * if package is specified then only classes that are within the package and its sub packages is scanned.
	 * 
	 * @param url
	 * @param packageRoot
	 * @throws MappingException
	 *             thrown in case of a mapping exception
	 * 
	 * @since $version
	 * @author hceylan
	 * @param metaModel
	 */
	public static void scanForArtifacts(MetamodelImpl metaModel, URL url, String packageRoot) throws MappingException {
		final ConfigurationBuilder configuration = new ConfigurationBuilder();
		configuration.useParallelExecutor();

		if ((url == null) && (packageRoot == null)) {
			configuration.setUrls(ReflectHelper.getClasspathUrls());
		}
		else {
			if (url != null) {
				configuration.addUrls(ClasspathHelper.forPackage(packageRoot));
			}

			if (StringUtils.isNotBlank(packageRoot)) {
				if (packageRoot != null) {
					configuration.filterInputsBy(new FilterBuilder.Include(FilterBuilder.prefix(packageRoot)));
				}

				if (url == null) {
					configuration.addUrls(ClasspathHelper.forPackage(packageRoot));
				}
			}

		}

		final Reflections r = new Reflections(configuration);

		TypeFactory.collect(metaModel, r, Embeddable.class);
		TypeFactory.collect(metaModel, r, MappedSuperclass.class);
		TypeFactory.collect(metaModel, r, Entity.class);
	}

	private static void sortClasses(final List<Class<?>> types) {
		// sort the classes based on hierarchy
		Collections.sort(types, new Comparator<Class<?>>() {

			@Override
			public int compare(Class<?> o1, Class<?> o2) {
				if (o1.isAssignableFrom(o2)) {
					return -1;
				}

				return 0;
			}
		});
	}

	private TypeFactory() {
		// no instantiation
	}
}
