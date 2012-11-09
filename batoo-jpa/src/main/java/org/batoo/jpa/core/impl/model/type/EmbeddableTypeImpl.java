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
package org.batoo.jpa.core.impl.model.type;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.SingularAttribute;

import org.batoo.common.util.FinalWrapper;
import org.batoo.jpa.common.reflect.ConstructorAccessor;
import org.batoo.jpa.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AssociatedSingularAttribute;
import org.batoo.jpa.core.impl.model.attribute.SingularAttributeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.type.EmbeddableMetadata;

import com.google.common.collect.Lists;

/**
 * Implementation of {@link EmbeddableType}.
 * 
 * @param <X>
 *            The represented embeddable type
 * 
 * @author hceylan
 * @since $version
 */
public class EmbeddableTypeImpl<X> extends ManagedTypeImpl<X> implements EmbeddableType<X> {
	private static final Object[] EMPTY_PARAMS = new Object[] {};

	private ConstructorAccessor constructor;
	private FinalWrapper<Integer> attributeCount;
	private FinalWrapper<SingularAttributeImpl<?, ?>[]> singularMappings;

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param javaType
	 *            the java type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EmbeddableTypeImpl(MetamodelImpl metamodel, Class<X> javaType, EmbeddableMetadata metadata) {
		super(metamodel, javaType, metadata);

		this.addAttributes(metadata);

		try {
			this.constructor = ReflectHelper.createConstructor(javaType.getConstructor());
		}
		catch (final Exception e) {
			throw new MappingException("Embeddable type does not have a default constructor", e, this.getLocator());
		}
	}

	/**
	 * Returns the attribute count of the embeddable.
	 * 
	 * @return the attribute count of the embeddable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getAttributeCount() {
		FinalWrapper<Integer> wrapper = this.attributeCount;

		if (wrapper == null) {
			synchronized (this) {
				if (this.attributeCount == null) {

					int attributeCount = 0;
					for (final SingularAttribute<?, ?> attribute : this.getSingularAttributes()) {
						switch (attribute.getPersistentAttributeType()) {
							case BASIC:
								attributeCount++;
								break;
							case EMBEDDED:
								attributeCount += ((EmbeddableTypeImpl<?>) attribute).getAttributeCount();
								break;
							case MANY_TO_ONE:
							case ONE_TO_ONE:
								attributeCount += ((EntityTypeImpl<?>) attribute.getType()).getPrimaryTable().getPkColumns().size();
								break;
							case ELEMENT_COLLECTION:
							case MANY_TO_MANY:
							case ONE_TO_MANY:
								break;
						}
					}

					this.attributeCount = new FinalWrapper<Integer>(attributeCount);
				}

				wrapper = this.attributeCount;
			}
		}

		return this.attributeCount.value;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistenceType getPersistenceType() {
		return PersistenceType.EMBEDDABLE;
	}

	/**
	 * Returns the sorted singular mappings of the embeddable.
	 * 
	 * @return the list of sorted singular attributes
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SingularAttributeImpl<?, ?>[] getSingularMappings() {
		FinalWrapper<SingularAttributeImpl<?, ?>[]> wrapper = this.singularMappings;

		if (this.singularMappings == null) {
			synchronized (this) {
				if (this.singularMappings == null) {

					final List<SingularAttributeImpl<? super X, ?>> singularMappings = Lists.newArrayList();
					for (final SingularAttribute<? super X, ?> attribute : this.getSingularAttributes()) {
						switch (attribute.getPersistentAttributeType()) {
							case BASIC:
							case EMBEDDED:
								singularMappings.add((SingularAttributeImpl<? super X, ?>) attribute);
								break;
							case MANY_TO_ONE:
							case ONE_TO_ONE:
								final AssociatedSingularAttribute<? super X, ?> association = (AssociatedSingularAttribute<? super X, ?>) attribute;
								if (!association.isOwner() || !association.isJoined()) {
									continue;
								}

								singularMappings.add((SingularAttributeImpl<? super X, ?>) attribute);
							case ELEMENT_COLLECTION:
							case MANY_TO_MANY:
							case ONE_TO_MANY:
								// N/A
						}
					}

					Collections.sort(singularMappings, new Comparator<SingularAttributeImpl<? super X, ?>>() {

						@Override
						public int compare(SingularAttributeImpl<? super X, ?> o1, SingularAttributeImpl<? super X, ?> o2) {
							return o1.getAttributeId().compareTo(o2.getAttributeId());
						}
					});

					this.singularMappings = new FinalWrapper<SingularAttributeImpl<?, ?>[]>(
						singularMappings.toArray(new SingularAttributeImpl[singularMappings.size()]));
				}

				wrapper = this.singularMappings;
			}
		}

		return wrapper.value;
	}

	/**
	 * Returns a new instance of the type
	 * 
	 * @return a new instance of the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public X newInstance() {
		try {
			return (X) this.constructor.newInstance(EmbeddableTypeImpl.EMPTY_PARAMS);
		}
		catch (final Exception e) {} // not possible at this stage

		return null;
	}

}
