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
package org.batoo.jpa.core.impl.model.mapping;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import javax.persistence.EnumType;
import javax.persistence.TemporalType;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.PluralAttribute.CollectionType;
import javax.persistence.metamodel.Type.PersistenceType;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.collections.ManagedCollection;
import org.batoo.jpa.core.impl.collections.ManagedList;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.CollectionTable;
import org.batoo.jpa.core.impl.jdbc.ConnectionImpl;
import org.batoo.jpa.core.impl.jdbc.JoinableTable;
import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.model.attribute.MapAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.PluralAttributeImpl;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.ColumnMetadata;
import org.batoo.jpa.parser.metadata.attribute.ElementCollectionAttributeMetadata;

/**
 * Mapping for element collections.
 * 
 * @param <Z>
 *            the source type
 * @param <E>
 *            the element type
 * @param <C>
 *            the collection type
 * 
 * @author hceylan
 * @since $version
 */
public class ElementCollectionMapping<Z, C, E> extends Mapping<Z, C, E> implements PluralMapping<Z, C, E> {

	private final PluralAttributeImpl<? super Z, C, E> attribute;
	private final CollectionTable collectionTable;
	private final ColumnMetadata column;
	private final EnumType enumType;
	private final boolean lob;
	private final TemporalType temporalType;
	private final String orderBy;
	private final ColumnMetadata orderColumn;

	private TypeImpl<E> type;
	private SingularMapping<? super E, ?> keyMapping;
	private ElementMapping<E> rootMapping;
	private Comparator<E> comparator;

	/**
	 * @param parent
	 *            the parent mapping
	 * @param attribute
	 *            the attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ElementCollectionMapping(ParentMapping<?, Z> parent, PluralAttributeImpl<? super Z, C, E> attribute) {
		super(parent, parent.getRoot().getType(), attribute, attribute.getJavaType(), attribute.getName());

		this.attribute = attribute;
		final ElementCollectionAttributeMetadata metadata = (ElementCollectionAttributeMetadata) this.attribute.getMetadata();
		this.collectionTable = new CollectionTable(this.getRoot().getType(), metadata.getCollectionTable());
		this.column = metadata.getColumn();
		this.enumType = metadata.getEnumType();
		this.lob = metadata.isLob();
		this.temporalType = metadata.getTemporalType();

		if (this.attribute.getCollectionType() == CollectionType.LIST) {
			this.orderColumn = metadata.getOrderColumn();
			this.orderBy = metadata.getOrderBy();
		}
		else {
			this.orderBy = null;
			this.orderColumn = null;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean cascadesMerge() {
		// TODO Auto-generated method
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void enhance(ManagedInstance<?> instance) {
		final C c = this.get(instance.getInstance());
		if (c == null) {
			this.set(instance, this.attribute.newCollection(this, instance, false));
		}
		else {
			this.set(instance, this.attribute.newCollection(this, instance, c));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void flush(ConnectionImpl connection, ManagedInstance<?> managedInstance, boolean removals, boolean force) throws SQLException {
		final Object collection = this.get(managedInstance.getInstance());

		((ManagedCollection<E>) collection).flush(connection, removals, force);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PluralAttributeImpl<? super Z, C, E> getAttribute() {
		return this.attribute;
	}

	/**
	 * Returns the collection table.
	 * 
	 * @return the collection table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CollectionTable getCollectionTable() {
		return this.collectionTable;
	}

	private Comparator<E> getComparator() {
		if (this.comparator != null) {
			return this.comparator;
		}
		synchronized (this) {
			if (this.comparator != null) {
				return this.comparator;
			}

			return this.comparator = new ListComparator<E>(this);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TypeImpl<?> getMapKeyClass() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Mapping<?, ?, ?> getMapping(String path) {
		return this.rootMapping.getMapping(path);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public MappingType getMappingType() {
		return MappingType.ELEMENT_COLLECTION;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getOrderBy() {
		return this.orderBy;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public JoinableTable getTable() {
		return this.collectionTable;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TypeImpl<E> getType() {
		return this.type;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isAssociation() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String join(String parentAlias, String alias, JoinType joinType) {
		return this.collectionTable.getKey().createSourceJoin(joinType, parentAlias, alias);
	}

	/**
	 * Links the attribute to its collection table
	 * 
	 * @throws MappingException
	 *             thrown in case of a linkage error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public void link() {
		this.getRoot().getType();

		this.type = this.attribute.getElementType();

		if (this.type.getPersistenceType() == PersistenceType.EMBEDDABLE) {
			this.rootMapping = new ElementMapping<E>((EmbeddableTypeImpl<E>) this.type);
		}

		if (this.attribute.getCollectionType() == CollectionType.MAP) {
			final MapAttributeImpl<? super Z, Map<?, E>, E> mapAttribute = (MapAttributeImpl<? super Z, Map<?, E>, E>) this.attribute;
			final String mapKey = mapAttribute.getMapKey();
			if (mapKey != null) {
				if (this.type.getPersistenceType() == PersistenceType.EMBEDDABLE) {
					this.keyMapping = (SingularMapping<? super E, ?>) this.rootMapping.getMapping(mapKey);
				}

				if (this.keyMapping == null) {
					throw new MappingException("Cannot locate the MapKey: " + mapKey, this.attribute.getLocator());
				}
			}
		}

		final String defaultName = this.getAttribute().getName();
		if (this.type.getPersistenceType() == PersistenceType.EMBEDDABLE) {
			this.collectionTable.link((EmbeddableTypeImpl<E>) this.type, defaultName, this.rootMapping);
		}
		else {
			this.collectionTable.link(this.type, defaultName, this.column, this.enumType, this.temporalType, this.lob);
		}

		if (this.attribute.getCollectionType() == CollectionType.LIST) {
			if (this.orderColumn != null) {
				final String name = StringUtils.isNotBlank(this.orderColumn.getName()) ? this.orderColumn.getName() : this.attribute.getName() + "_ORDER";
				this.collectionTable.setOrderColumn(this.orderColumn, name);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<? extends E> loadCollection(ManagedInstance<?> managedInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void persistAdditions(EntityManagerImpl entityManager, ManagedInstance<?> instance) {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void setLazy(ManagedInstance<?> instance) {
		this.set(instance, this.attribute.newCollection(this, instance, true));
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void sortList(Object instance) {
		final ManagedList<Z, E> list = (ManagedList<Z, E>) this.get(instance);
		final ArrayList<E> delegate = list.getDelegate();
		if ((list != null) && list.isInitialized()) {
			Collections.sort(delegate, this.getComparator());
		}
	}
}
