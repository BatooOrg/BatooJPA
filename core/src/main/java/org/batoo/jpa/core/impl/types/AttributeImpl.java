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
package org.batoo.jpa.core.impl.types;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Deque;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;

import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.mapping.ColumnTemplate;
import org.batoo.jpa.core.impl.mapping.JoinColumnTemplate;
import org.batoo.jpa.core.impl.reflect.Accessor;
import org.batoo.jpa.core.impl.reflect.FieldAccessor;
import org.batoo.jpa.core.impl.reflect.ReflectHelper;
import org.batoo.jpa.core.jdbc.adapter.JDBCAdapter;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The impementation of {@link Attribute}
 * 
 * @author hceylan
 * 
 * @since $version
 */
public abstract class AttributeImpl<X, Y> implements Attribute<X, Y>, Comparable<AttributeImpl<?, ?>> {

	public enum AtrributeType {

		/**
		 * Attribute is basic type, may be plural with {@link ElementCollection} annotation.
		 */
		BASIC,

		/**
		 * Attribute is binary type, may not be plural
		 */
		LOB,

		/**
		 * Attribute is embedded, may be plural with {@link ElementCollection} annotation.
		 */
		EMBEDDED,

		/**
		 * Attribute is relational, singular with {@link ManyToOne} and {@link OneToOne} annotations, plural with {@link OneToMany} and
		 * {@link ManyToMany} annotations.
		 */
		ASSOCIATION
	}

	// Base properties
	protected final ManagedTypeImpl<X> declaringType;
	protected final Class<Y> javaType; // the java type of the attribute
	protected final Member javaMember; // the java member of the attribute
	private Accessor<Y> javaMemberAccessor; // the accessor to to use for fast get / set operations
	protected final JDBCAdapter jdbcAdapter;

	// Mapping supplied properties
	protected AtrributeType attributeType; // type of the attribute
	protected boolean many; // if this a many to one field, relevant only with RELATIONAL AttributeType

	// Column definitions
	protected final Set<ColumnTemplate<X, Y>> columns = Sets.newHashSet(); // column templates
	protected final Map<String, Column> attributeOverrides = Maps.newHashMap(); // overrides to attributes

	protected CascadeType[] cascadeType; // the cascade type

	protected FetchType fetchType; // the fetch type
	protected boolean orphanRemoval;
	protected String mappedBy;
	// extra attribute information for temporal and enum attributes
	private TemporalType temporalType; // the temporal type of the attribute, applicable to data / time type attributes

	private EnumType enumType; // the enum type of the attribute, applicable to
	protected AbstractMapping<?, ?> mapping; // the mapping of the attribute

	private boolean charType;

	/**
	 * @param declaringType
	 *            the type declaring this attribute
	 * @param javaMember
	 *            the {@link Member} this attribute is associated with
	 * @param javaType
	 *            the java type of the member
	 * 
	 * @throws MappingException
	 *             thrown in case of a parsing error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AttributeImpl(ManagedType<X> declaringType, Member javaMember, Class<Y> javaType) throws MappingException {
		super();

		this.declaringType = (ManagedTypeImpl<X>) declaringType;
		this.javaMember = javaMember;
		this.javaType = javaType;

		this.jdbcAdapter = this.declaringType.getMetaModel().getJdbcAdapter();
		this.attributeType = AtrributeType.BASIC;

		final Set<Class<? extends Annotation>> annotations = this.parse();

		ReflectHelper.checkAnnotations(javaMember, annotations);
		ReflectHelper.makeAccessible(this.javaMember);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final int compareTo(AttributeImpl<?, ?> o) {
		// is this id and other is not?
		if ((this instanceof SingularAttributeImpl) && ((SingularAttributeImpl<X, Y>) this).isId()) {
			if (!(o instanceof SingularAttributeImpl) || !((SingularAttributeImpl<?, ?>) o).isId()) {
				return -1;
			}
		}

		// is other id and this is not?
		if ((o instanceof SingularAttributeImpl) && ((SingularAttributeImpl<?, ?>) o).isId()) {
			if (!(this instanceof SingularAttributeImpl) || !((SingularAttributeImpl<X, Y>) this).isId()) {
				return 1;
			}
		}

		return this.getName().compareTo(o.getName());
	}

	private synchronized void createAccessor() {
		if (this.javaMemberAccessor != null) {
			return;
		}

		if (this.javaMember instanceof Field) {
			final Field field = (Field) this.javaMember;
			if (field.getType() == char.class) {
				this.charType = true;
			}
			this.javaMemberAccessor = new FieldAccessor<Y>(field);
		}
	}

	/**
	 * Returns if this attribute has type of <code>type</code> or transitively embeds the type
	 * 
	 * @param type
	 *            the managed type
	 * @return true if it embeds
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final boolean embeds(EmbeddableTypeImpl<?> type) {
		if (this.getType() == type) {
			return true;
		}

		if (this.getType() instanceof EmbeddableTypeImpl) {
			((EmbeddableTypeImpl<?>) this.getType()).embeds(type);
		}

		return false;
	}

	/**
	 * Returns the attribute value of instance.
	 * 
	 * @param instance
	 *            the instance of which the value to be returned
	 * @return the attribute value of instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public final Y get(Object instance) {
		final Y value = this.getAccessor().get(instance);
		if (this.charType && (((Character) value).hashCode() == 0)) {
			return null;
		}

		return value;
	}

	protected final Accessor<Y> getAccessor() {
		if (this.javaMemberAccessor == null) {
			this.createAccessor();
		}

		return this.javaMemberAccessor;
	}

	/**
	 * Returns the cascade type.
	 * 
	 * @return the cascade type
	 * @since $version
	 */
	public final CascadeType[] getCascadeType() {
		return this.cascadeType;
	}

	/**
	 * Returns the columns.
	 * 
	 * @return the columns
	 * @since $version
	 */
	public Set<ColumnTemplate<X, Y>> getColumns() {
		return this.columns;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final ManagedTypeImpl<X> getDeclaringType() {
		return this.declaringType;
	}

	/**
	 * Returns the enumType.
	 * 
	 * @return the enumType
	 * @since $version
	 */
	public final EnumType getEnumType() {
		return this.enumType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Member getJavaMember() {
		return this.javaMember;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Class<Y> getJavaType() {
		return this.javaType;
	}

	/**
	 * Returns the mappedBy.
	 * 
	 * @return the mappedBy
	 * @since $version
	 */
	public final String getMappedBy() {
		return this.mappedBy;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final String getName() {
		return this.javaMember.getName();
	}

	/**
	 * Returns the temporalType.
	 * 
	 * @return the temporalType
	 * @since $version
	 */
	public final TemporalType getTemporalType() {
		return this.temporalType;
	}

	@SuppressWarnings("unchecked")
	private TypeImpl<Y> getType() {
		if (this instanceof PluralAttribute) {
			return ((PluralAttributeImpl<X, ?, Y>) this).getElementType();
		}
		else {
			return (TypeImpl<Y>) ((SingularAttributeImpl<X, Y>) this).getType();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean isAssociation() {
		return this.attributeType == AtrributeType.ASSOCIATION;
	}

	/**
	 * Returns if this is a binary storage attribute.
	 * 
	 * @return true if this is a binary storage attribute
	 * @since $version
	 */
	public final boolean isLob() {
		return this.attributeType == AtrributeType.LOB;
	}

	/**
	 * Links the attribute to persistent types. Called at horizontal linking phase.
	 * <p>
	 * Subclasses must re-implement to provide their own SQL meatada.
	 * 
	 * @param path
	 *            the path to this attribute
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void link(Deque<AttributeImpl<?, ?>> path) throws MappingException;

	/**
	 * Subclasses should re-implement to parse their own persistence annotations. They must call the super() before processing.
	 * 
	 * @return the annotations parsed
	 * @throws MappingException
	 *             thrown in case of a mapping error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected Set<Class<? extends Annotation>> parse() throws MappingException {
		final Set<Class<? extends Annotation>> parsed = Sets.newHashSet();
		final Temporal temporal = ReflectHelper.getAnnotation(this.javaMember, Temporal.class);
		if (temporal != null) {
			this.temporalType = temporal.value();

			parsed.add(Temporal.class);
		}

		final Enumerated enumerated = ReflectHelper.getAnnotation(this.javaMember, Enumerated.class);
		if (enumerated != null) {
			this.enumType = enumerated.value();

			parsed.add(Enumerated.class);
		}

		final Lob lob = ReflectHelper.getAnnotation(this.javaMember, Lob.class);
		if (lob != null) {
			this.attributeType = AtrributeType.LOB;

			parsed.add(Lob.class);
		}

		this.parseJoinColumns(parsed);

		this.parseAttributeOverrides(parsed);

		return parsed;
	}

	private void parseAttributeOverrides(final Set<Class<? extends Annotation>> parsed) {
		final AttributeOverrides attributeOverrides = ReflectHelper.getAnnotation(this.javaMember, AttributeOverrides.class);
		if (attributeOverrides != null) {
			for (final AttributeOverride attributeOverride : attributeOverrides.value()) {
				this.attributeOverrides.put(attributeOverride.name(), attributeOverride.column());
			}

			parsed.add(AttributeOverrides.class);
		}

		final AttributeOverride attributeOverride = ReflectHelper.getAnnotation(this.javaMember, AttributeOverride.class);
		if (attributeOverride != null) {
			this.attributeOverrides.put(attributeOverride.name(), attributeOverride.column());

			parsed.add(AttributeOverride.class);
		}
	}

	private void parseJoinColumns(final Set<Class<? extends Annotation>> parsed) {
		final JoinColumns joinColumns = ReflectHelper.getAnnotation(this.javaMember, JoinColumns.class);
		if (joinColumns != null) {
			for (final JoinColumn joinColumn : joinColumns.value()) {
				this.columns.add(new JoinColumnTemplate<X, Y>(this, joinColumn));
			}

			parsed.add(JoinColumns.class);
		}

		final JoinColumn joinColumn = ReflectHelper.getAnnotation(this.javaMember, JoinColumn.class);
		if (joinColumn != null) {
			this.columns.add(new JoinColumnTemplate<X, Y>(this, joinColumn));

			parsed.add(JoinColumn.class);
		}
	}

	/**
	 * Sets the attribute value of instance.
	 * 
	 * @param instance
	 *            the instance of which the value to be returned
	 * @param value
	 *            the value to set
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract void set(Object instance, Object value);
}
