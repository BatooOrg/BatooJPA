/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.core.impl.model.attribute;

import java.util.Map;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Type;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.collections.ManagedMap;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralMapping;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.attribute.AttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.PluralAttributeMetadata;

/**
 * Implementation of {@link ListAttribute}.
 * 
 * @param <X>
 *            The type the represented collection belongs to
 * @param <K>
 *            The key element type of the represented collection
 * @param <V>
 *            The value element type of the represented collection
 * 
 * @author hceylan
 * @since $version
 */
public class MapAttributeImpl<X, K, V> extends PluralAttributeImpl<X, Map<K, V>, V> implements MapAttribute<X, K, V> {

	private Type<K> keyType;
	private Class<K> keyJavaType;

	/**
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * @param attributeType
	 *            attribute type
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public MapAttributeImpl(ManagedTypeImpl<X> declaringType, AttributeMetadata metadata, PersistentAttributeType attributeType) {
		super(declaringType, metadata, attributeType, 1);

		final PluralAttributeMetadata pluralAttributeMetadata = (PluralAttributeMetadata) metadata;
		if (StringUtils.isNotBlank(pluralAttributeMetadata.getMapKeyClassName())) {
			try {
				this.keyJavaType = (Class<K>) Class.forName(pluralAttributeMetadata.getMapKeyClassName());
			}
			catch (final ClassNotFoundException e) {
				throw new MappingException("Target enttity class not found", metadata.getLocator());
			}
		}
		else {
			this.keyJavaType = ReflectHelper.getGenericType(this.getJavaMember(), 0);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public CollectionType getCollectionType() {
		return CollectionType.MAP;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Class<K> getKeyJavaType() {
		return this.keyJavaType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Type<K> getKeyType() {
		if (this.keyType != null) {
			return this.keyType;
		}

		final MetamodelImpl metamodel = this.getDeclaringType().getMetamodel();

		return this.keyType = metamodel.embeddable(this.keyJavaType) != null ? metamodel.embeddable(this.keyJavaType)
			: metamodel.createBasicType(this.keyJavaType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Map<K, V> newCollection(PluralMapping<?, Map<K, V>, V> mapping, ManagedInstance<?> managedInstance, boolean lazy) {
		return new ManagedMap<X, K, V>(mapping, managedInstance, lazy);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<K, V> newCollection(PluralMapping<?, Map<K, V>, V> mapping, ManagedInstance<?> managedInstance, Object values) {
		return new ManagedMap<X, K, V>(mapping, managedInstance, (Map<? extends K, ? extends V>) values);
	}
}
