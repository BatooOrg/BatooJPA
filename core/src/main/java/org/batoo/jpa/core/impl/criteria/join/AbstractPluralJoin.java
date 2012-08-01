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
package org.batoo.jpa.core.impl.criteria.join;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.PluralJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.Type.PersistenceType;

import org.batoo.jpa.core.impl.model.mapping.ElementCollectionMapping;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping;
import org.batoo.jpa.core.impl.model.mapping.JoinedMapping.MappingType;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.PluralAssociationMapping;

/**
 * The implementation of {@link PluralJoin}.
 * 
 * @param <Z>
 *            the source type
 * @param <C>
 *            the collection type
 * @param <E>
 *            the element type of the collection
 * 
 * @author hceylan
 * @since $version
 */
public class AbstractPluralJoin<Z, C, E> extends AbstractJoin<Z, E> implements PluralJoin<Z, C, E> {

	/**
	 * @param parent
	 *            the parent
	 * @param mapping
	 *            the mapping
	 * @param jointType
	 *            the join type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractPluralJoin(AbstractFrom<?, Z> parent, JoinedMapping<? super Z, ?, E> mapping, JoinType jointType) {
		super(parent, mapping, jointType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected <CC, Y> Mapping<? super E, CC, Y> getMapping(String name) {
		Mapping<? super E, ?, ?> child = null;

		if (this.getMapping().getMappingType() == MappingType.ELEMENT_COLLECTION) {
			final ElementCollectionMapping<? super Z, C, E> elementCollectionMapping = (ElementCollectionMapping<? super Z, C, E>) this.getMapping();
			if (elementCollectionMapping.getType().getPersistenceType() == PersistenceType.EMBEDDABLE) {
				child = (Mapping<? super E, ?, ?>) elementCollectionMapping.getMapping(name);
			}
		}
		else {
			child = ((PluralAssociationMapping<? super Z, C, E>) this.getMapping()).getType().getRootMapping().getChild(name);
		}

		if (child == null) {
			throw this.cannotDereference(name);
		}

		return (Mapping<? super E, CC, Y>) child;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PluralAttribute<? super Z, C, E> getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Join<Z, E> on(Expression<Boolean> restriction) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Join<Z, E> on(Predicate... restrictions) {
		// TODO Auto-generated method stub
		return null;
	}

}
