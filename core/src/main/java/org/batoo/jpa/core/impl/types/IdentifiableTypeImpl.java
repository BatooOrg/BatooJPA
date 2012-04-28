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
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.IdClass;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.jdbc.PhysicalTableGenerator;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;

import com.google.common.collect.Maps;

/**
 * Implementation of {@link IdentifiableType}.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class IdentifiableTypeImpl<X> extends ManagedTypeImpl<X> implements IdentifiableType<X> {

	private static final BLogger LOG = BLogger.getLogger(IdentifiableTypeImpl.class);

	protected Class<?> idJavaType;
	protected TypeImpl<?> idType;

	protected final Map<String, SingularAttributeImpl<X, ?>> declaredIdAttributes = Maps.newHashMap();
	protected final Map<String, SingularAttributeImpl<? super X, ?>> idAttributes = Maps.newHashMap();

	protected final Map<String, SingularAttributeImpl<? super X, ?>> versionAttributes = Maps.newHashMap();
	private SequenceGenerator sequenceGenerator;
	private TableGenerator tableGenerator;

	private String tableName;

	/**
	 * @param metaModel
	 *            the meta model
	 * @param javaType
	 *            the java type this type corresponds to
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdentifiableTypeImpl(MetamodelImpl metamodelImpl, Class<X> javaType) throws MappingException {
		super(metamodelImpl, javaType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> type) {
		for (final SingularAttribute<X, ?> singularAttribute : this.declaredIdAttributes.values()) {
			if (singularAttribute.getJavaType() == type) {
				return (SingularAttribute<X, Y>) singularAttribute;
			}
		}

		return this.throwNotFound();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> type) {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> SingularAttribute<? super X, Y> getId(Class<Y> type) {
		for (final SingularAttribute<? super X, ?> singularAttribute : this.idAttributes.values()) {
			if (singularAttribute.getJavaType() == type) {
				return (SingularAttribute<? super X, Y>) singularAttribute;
			}
		}

		return this.throwNotFound();
	}

	/**
	 * Returns the idAttributes.
	 * 
	 * @return the idAttributes
	 * @since $version
	 */
	public Collection<SingularAttributeImpl<? super X, ?>> getIdAttributes() {
		return this.idAttributes.values();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<SingularAttribute<? super X, ?>> getIdClassAttributes() {
		if (this.idJavaType == null) {
			return this.throwNotFound();
		}

		return new HashSet<SingularAttribute<? super X, ?>>(this.idAttributes.values());
	}

	/**
	 * Returns the idJavaType.
	 * 
	 * @return the idJavaType
	 * @since $version
	 */
	public Class<?> getIdJavaType() {
		return this.idJavaType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TypeImpl<?> getIdType() {
		return this.idType;
	}

	/**
	 * Returns the sequence generator for the type or null if no sequence generator is specified.
	 * 
	 * @return the sequence generator or null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SequenceGenerator getSequenceGenerator() {
		return this.sequenceGenerator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public synchronized IdentifiableTypeImpl<? super X> getSupertype() {
		IdentifiableTypeImpl<? super X> type = null;

		// seed
		Class<? super X> clazz = this.getJavaType().getSuperclass();
		do {
			// get the type for the class
			type = this.metaModel.identifiableType0(clazz);
		}
		while (((clazz = clazz.getSuperclass()) != null) && (type == null));

		return type;
	}

	/**
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public TableGenerator getTableGenerator() {
		return this.tableGenerator;
	}

	/**
	 * Returns the table name of the identifiable.
	 * 
	 * @return the table name of the identifiable
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getTableName() {
		return this.tableName;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public <Y> SingularAttribute<? super X, Y> getVersion(final Class<Y> type) {
		this.throwNotImplemented();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasSingleIdAttribute() {
		return this.idAttributes.size() == 1;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasVersionAttribute() {
		return this.versionAttributes.size() > 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * 
	 */
	@Override
	public void parse(Set<Class<? extends Annotation>> parsed) throws BatooException {
		super.parse(parsed);

		final IdClass idClass = this.getJavaType().getAnnotation(IdClass.class);
		if (idClass != null) {
			this.idJavaType = idClass.value();

			parsed.add(IdClass.class);
		}

		this.parseGenerators(parsed);

		for (final Attribute<X, ?> attribute : this.setDeclaredAttributes) {
			if (attribute instanceof SingularAttribute) {
				final SingularAttributeImpl<X, ?> singularAttribute = (SingularAttributeImpl<X, ?>) attribute;

				this.declaredAttributes.put(singularAttribute.getName(), singularAttribute);
				this.setDeclaredAttributes.add(singularAttribute);

				if (singularAttribute.isId()) {
					this.declaredIdAttributes.put(singularAttribute.getName(), singularAttribute);
				}
			}
		}
	}

	private void parseGenerators(Set<Class<? extends Annotation>> parsed) throws MappingException {
		this.sequenceGenerator = this.getJavaType().getAnnotation(SequenceGenerator.class);
		if (this.sequenceGenerator != null) {
			// is the name ok?
			if (StringUtils.isBlank(this.sequenceGenerator.name())) {
				throw new MappingException("SequenceGenerator name must be specified, defined on " + this.getJavaType());
			}

			// is it unique
			if (!this.getMetaModel().addSequenceGenerator(this.sequenceGenerator)) {
				throw new MappingException("An existing SequenceGenerator with name " + this.sequenceGenerator.name()
					+ " already refefined on " + this.getJavaType());
			}

			parsed.add(SequenceGenerator.class);
		}

		this.tableGenerator = this.getJavaType().getAnnotation(TableGenerator.class);
		if (this.tableGenerator != null) {
			// is the name ok?
			if (StringUtils.isBlank(this.tableGenerator.name())) {
				throw new MappingException("TableGenerator name must be specified, defined on " + this.getJavaType());
			}

			// is it unique
			if (!this.metaModel.addTableGenerator(new PhysicalTableGenerator(this.tableGenerator, this.metaModel.getJdbcAdapter()))) {
				throw new MappingException("An existing TequenceGenerator with name " + this.tableGenerator.name()
					+ " already refefined on " + this.getJavaType());
			}

			parsed.add(TableGenerator.class);
		}
	}

	/**
	 * Sets the idJavaType.
	 * 
	 * @param type
	 *            the idJavaType to set
	 * @since $version
	 */
	public void setIdJavaType(EmbeddableTypeImpl<?> type) {
		this.idJavaType = type.getJavaType();
		this.idType = type;
	}

	/**
	 * Vertically links the type.
	 * 
	 * @throws BatooException
	 *             thrown in case of a mapping error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void vlink() throws BatooException {
		LOG.debug("Vertically linking {0}", this);

		this.attributes.putAll(this.declaredAttributes);
		this.setAttributes.addAll(this.setDeclaredAttributes);
		this.idAttributes.putAll(this.declaredIdAttributes);

		final IdentifiableTypeImpl<? super X> supertype = this.getSupertype();
		if (supertype instanceof MappedSuperclassTypeImpl) {

			// inherit attributes
			for (final Attribute<?, ?> superAttribute : supertype.setAttributes) {
				if (this.attributes.containsKey(superAttribute.getName())) {
					continue;
				}

				final AttributeImpl<X, ?> attribute = ((AttributeImpl<?, ?>) superAttribute).clone((EntityTypeImpl<X>) this);

				this.attributes.put(attribute.getName(), attribute);
				this.setAttributes.add(attribute);

				// TODO Check id conflict
				if (attribute instanceof SingularAttributeImpl) {
					final SingularAttributeImpl<X, ?> singularAttribute = (SingularAttributeImpl<X, ?>) attribute;
					if (singularAttribute.isId()) {
						this.idAttributes.put(singularAttribute.getName(), singularAttribute);
					}
				}
			}

			this.idJavaType = supertype.idJavaType;
			this.idType = supertype.idType;
		}

	}
}
