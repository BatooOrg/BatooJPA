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
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.mapping.ColumnTemplate;
import org.batoo.jpa.core.impl.mapping.JoinColumnTemplate;
import org.batoo.jpa.core.impl.mapping.OwnedManyToManyMapping;
import org.batoo.jpa.core.impl.mapping.OwnedOneToManyMapping;
import org.batoo.jpa.core.impl.mapping.OwnerManyToManyMapping;
import org.batoo.jpa.core.impl.reflect.ReflectHelper;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * The implementation of {@link PluralAttribute}
 * 
 * @author hceylan
 * 
 * @since $version
 */
public abstract class PluralAttributeImpl<X, C, E> extends AttributeImpl<X, C> implements PluralAttribute<X, C, E> {

	private String collectionTableName;
	private String collectionTableSchema;
	private final Set<UniqueConstraint> collectionTableUniqueConstraints = Sets.newHashSet();

	// Link Phase properties
	private final Class<E> elementJavaType;

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
	public PluralAttributeImpl(ManagedType<X> owner, Member member, Class<C> javaMember, Class<E> elementJavaType) throws MappingException {
		super(owner, member, javaMember);

		this.elementJavaType = elementJavaType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final Class<E> getBindableJavaType() {
		return this.elementJavaType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final BindableType getBindableType() {
		return BindableType.PLURAL_ATTRIBUTE;
	}

	/**
	 * Returns the collectionTableName.
	 * 
	 * @return the collectionTableName
	 * @since $version
	 */
	public final String getCollectionTableName() {
		return this.collectionTableName;
	}

	/**
	 * Returns the collectionTableSchema.
	 * 
	 * @return the collectionTableSchema
	 * @since $version
	 */
	public final String getCollectionTableSchema() {
		return this.collectionTableSchema;
	}

	/**
	 * Returns the collectionTableUniqueConstraints.
	 * 
	 * @return the collectionTableUniqueConstraints
	 * @since $version
	 */
	public final Collection<UniqueConstraint> getCollectionTableUniqueConstraints() {
		return Collections.unmodifiableCollection(this.collectionTableUniqueConstraints);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final TypeImpl<E> getElementType() {
		return (TypeImpl<E>) this.getDeclaringType().getMetaModel().getType(this.elementJavaType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final javax.persistence.metamodel.Attribute.PersistentAttributeType getPersistentAttributeType() {
		switch (this.attributeType) {
			case ASSOCIATION:
				return this.many ? PersistentAttributeType.ONE_TO_MANY : PersistentAttributeType.MANY_TO_MANY;
			case BASIC:
			default:
				return PersistentAttributeType.ELEMENT_COLLECTION;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public final boolean isCollection() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void link(Deque<AttributeImpl<?, ?>> path, Map<String, Column> attributeOverrides) throws MappingException {
		path = Lists.newLinkedList(path);
		path.addLast(this);

		switch (this.attributeType) {
			case EMBEDDED:
				throw new NotImplementedException();
			case ASSOCIATION:
				final boolean eager = this.fetchType == FetchType.EAGER;
				if (!this.many) {
					this.mapping = new OwnedOneToManyMapping<X, C, E>(this, path, this.orphanRemoval, eager);
				}
				else {
					if (StringUtils.isNotBlank(this.mappedBy)) {
						this.mapping = new OwnedManyToManyMapping<X, C, E>(this, path, this.orphanRemoval, eager);
					}
					else {
						this.mapping = new OwnerManyToManyMapping<X, C, E>(this, path, this.overrideColumns(attributeOverrides), eager);
					}
				}
		}
	}

	/**
	 * Applies the overrides to the column templates.
	 * 
	 * @param attributeOverrides
	 *            the attribute overrides
	 * @return the overridden column templates
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private Collection<ColumnTemplate<X, C>> overrideColumns(final Map<String, Column> attributeOverrides) {
		if ((attributeOverrides == null) || (attributeOverrides.size() == 0)) {
			return this.columns;
		}

		return Collections2.transform(this.columns, new Function<ColumnTemplate<X, C>, ColumnTemplate<X, C>>() {

			@Override
			public ColumnTemplate<X, C> apply(ColumnTemplate<X, C> input) {
				final Column column = attributeOverrides.get(input.getName());
				if (column != null) {}

				return input;
			}
		});
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	protected Set<Class<? extends Annotation>> parse() throws MappingException {
		final Set<Class<? extends Annotation>> parsed = super.parse();

		final Member member = this.getJavaMember();

		final ElementCollection elementCollection = ReflectHelper.getAnnotation(member, ElementCollection.class);
		if (elementCollection != null) {
			this.attributeType = AtrributeType.BASIC;

			this.fetchType = elementCollection.fetch();

			parsed.add(ElementCollection.class);

			final CollectionTable collectionTable = ReflectHelper.getAnnotation(member, CollectionTable.class);
			if (collectionTable != null) {
				this.collectionTableName = collectionTable.name();
				this.collectionTableSchema = collectionTable.schema();
				for (final UniqueConstraint uniqueConstraint : collectionTable.uniqueConstraints()) {
					this.collectionTableUniqueConstraints.add(uniqueConstraint);
				}

				for (final JoinColumn joinColumn : collectionTable.joinColumns()) {
					this.columns.add(new JoinColumnTemplate<X, C>(this, joinColumn));
				}

				parsed.add(CollectionTable.class);
			}
		}

		final OneToMany oneToMany = ReflectHelper.getAnnotation(member, OneToMany.class);
		if (oneToMany != null) {
			this.attributeType = AtrributeType.ASSOCIATION;

			this.mappedBy = oneToMany.mappedBy();
			this.fetchType = oneToMany.fetch();
			this.cascadeType = oneToMany.cascade();
			this.orphanRemoval = oneToMany.orphanRemoval();

			parsed.add(OneToMany.class);
		}

		final ManyToMany manyToMany = ReflectHelper.getAnnotation(member, ManyToMany.class);
		if (manyToMany != null) {
			this.attributeType = AtrributeType.ASSOCIATION;
			this.many = true;

			this.mappedBy = manyToMany.mappedBy();
			this.fetchType = manyToMany.fetch();
			this.cascadeType = manyToMany.cascade();

			parsed.add(ManyToMany.class);
		}

		return parsed;
	}

}
