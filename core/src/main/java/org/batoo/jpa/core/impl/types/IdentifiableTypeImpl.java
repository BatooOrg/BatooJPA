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
import java.util.Map;
import java.util.Set;

import javax.persistence.IdClass;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.BLogger;
import org.batoo.jpa.core.BatooException;
import org.batoo.jpa.core.MappingException;
import org.batoo.jpa.core.impl.jdbc.PhysicalTableGenerator;
import org.batoo.jpa.core.impl.mapping.MetamodelImpl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link IdentifiableType}.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class IdentifiableTypeImpl<X> extends ManagedTypeImpl<X> implements IdentifiableType<X> {

	private static final BLogger LOG = BLogger.getLogger(IdentifiableTypeImpl.class);

	private final IdentifiableTypeImpl<? super X> supertype;
	protected final Map<String, SingularAttributeImpl<? super X, ?>> versionAttributes = Maps.newHashMap();

	protected Class<?> idJavaType;
	protected TypeImpl<?> idType;

	protected SingularAttributeImpl<?, ?>[] idAttributes;
	private SequenceGenerator sequenceGenerator;
	private TableGenerator tableGenerator;

	private String tableName;

	/**
	 * @param metaModel
	 *            the meta model
	 * @param supertype
	 *            type that this type extends, may be null
	 * @param javaType
	 *            the java type this type corresponds to
	 * @throws MappingException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdentifiableTypeImpl(MetamodelImpl metamodel, IdentifiableTypeImpl<? super X> supertype, Class<X> javaType)
		throws MappingException {
		super(metamodel, javaType);

		this.supertype = supertype;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> type) {
		for (final SingularAttribute<? super X, ?> attribute : this.getDeclaredSingularAttributes()) {
			if ((attribute.getJavaType() == type)) {
				return (SingularAttribute<X, Y>) attribute;
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
		for (final SingularAttributeImpl<?, ?> singularAttribute : this.idAttributes) {
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
	public SingularAttributeImpl<?, ?>[] getIdAttributes() {
		return this.idAttributes;
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

		final Set<SingularAttribute<? super X, ?>> attributes = Sets.newHashSet();
		for (final SingularAttribute<? super X, ?> attribute : this.getSingularAttributes()) {
			if (attribute.isId()) {
				attributes.add(attribute);
			}
		}

		return attributes;
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
	public IdentifiableTypeImpl<? super X> getSupertype() {
		return this.supertype;
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
		return this.idAttributes.length == 1;
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
		final Set<SingularAttribute<? super X, ?>> idAttributes = Sets.newHashSet();
		for (final SingularAttribute<? super X, ?> attribute : this.getSingularAttributes()) {
			if (attribute.isId()) {
				idAttributes.add(attribute);
			}
		}
		this.idAttributes = new SingularAttributeImpl[idAttributes.size()];
		idAttributes.toArray(this.idAttributes);
	}
}
