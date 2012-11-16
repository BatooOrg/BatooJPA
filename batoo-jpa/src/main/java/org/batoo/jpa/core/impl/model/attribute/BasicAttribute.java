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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.PersistenceException;
import javax.persistence.TemporalType;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;
import org.batoo.common.reflect.ReflectHelper;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.TypeFactory;
import org.batoo.jpa.core.impl.model.AbstractGenerator;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.BasicTypeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.type.IdentifiableTypeImpl;
import org.batoo.jpa.core.impl.model.type.ManagedTypeImpl;
import org.batoo.jpa.core.jdbc.IdType;
import org.batoo.jpa.core.jdbc.adapter.JdbcAdaptor;
import org.batoo.jpa.parser.metadata.ColumnTransformerMetadata;
import org.batoo.jpa.parser.metadata.GeneratedValueMetadata;
import org.batoo.jpa.parser.metadata.IndexMetadata;
import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.IdAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.PhysicalAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.VersionAttributeMetadata;

/**
 * Implementation of {@link SingularAttribute} for basic, version and id attributes.
 * 
 * @param <X>
 *            The type containing the represented attribute
 * @param <T>
 *            The type of the represented attribute
 * 
 * @author hceylan
 * @since $version
 */
public class BasicAttribute<X, T> extends SingularAttributeImpl<X, T> {

	private final BasicTypeImpl<T> type;
	private final boolean optional;
	private final boolean version;
	private final IdType idType;
	private final String generator;
	private final boolean lob;
	private final TemporalType temporalType;
	private final EnumType enumType;
	private final IndexMetadata index;
	private final ColumnTransformerMetadata columnTransformer;

	/**
	 * Constructor for version attributes.
	 * 
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 */
	public BasicAttribute(IdentifiableTypeImpl<X> declaringType, VersionAttributeMetadata metadata) {
		super(declaringType, metadata);

		this.version = true;
		this.optional = false;
		this.idType = null;
		this.generator = null;
		this.lob = false;
		this.enumType = null;
		this.index = null;
		this.columnTransformer = null;

		this.type = this.getDeclaringType().getMetamodel().createBasicType(this.getJavaType());

		if (this.getJavaType() == Timestamp.class) {
			this.temporalType = TemporalType.TIMESTAMP;
		}
		else if (this.getJavaType() == java.sql.Date.class) {
			this.temporalType = TemporalType.DATE;
		}
		else if (this.getJavaType() == java.sql.Time.class) {
			this.temporalType = TemporalType.TIME;
		}
		else if ((this.getJavaType() == Date.class) || (this.getJavaType() == Calendar.class)) {
			this.temporalType = metadata.getTemporalType() != null ? metadata.getTemporalType() : TemporalType.TIMESTAMP;
		}
		else {
			this.temporalType = null;
		}
	}

	/**
	 * Constructor for basic attributes.
	 * 
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 */
	public BasicAttribute(ManagedTypeImpl<X> declaringType, BasicAttributeMetadata metadata) {
		super(declaringType, metadata);

		this.version = false;
		this.idType = null;
		this.generator = null;

		this.type = this.getDeclaringType().getMetamodel().createBasicType(this.getJavaType());
		this.optional = metadata.isOptional();
		this.index = metadata.getIndex();
		this.columnTransformer = metadata.getColumnTransformer();

		if (Date.class.isAssignableFrom(this.getJavaType()) || Calendar.class.isAssignableFrom(this.getJavaType())) {
			if (metadata.getTemporalType() == null) {
				this.temporalType = TemporalType.TIMESTAMP;
			}
			else {
				this.temporalType = metadata.getTemporalType();
			}
		}
		else {
			this.temporalType = null;
		}

		if (this.getJavaType().getSuperclass() == Enum.class) {
			if (metadata.getEnumType() != null) {
				this.enumType = metadata.getEnumType();
			}
			else {
				this.enumType = EnumType.ORDINAL;
			}
		}
		else {
			this.enumType = null;
		}

		this.lob = this.inferLobType(metadata.isLob());
	}

	/**
	 * Constructor for basic id attributes.
	 * 
	 * @param declaringType
	 *            the declaring type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 */
	public BasicAttribute(ManagedTypeImpl<X> declaringType, IdAttributeMetadata metadata) {
		super(declaringType, metadata);

		this.version = false;
		this.optional = false;
		this.lob = false;
		this.enumType = null;
		this.index = null;
		this.columnTransformer = null;

		this.type = this.getDeclaringType().getMetamodel().createBasicType(this.getJavaType());
		this.temporalType = metadata.getTemporalType();

		final JdbcAdaptor jdbcAdaptor = declaringType.getMetamodel().getJdbcAdaptor();
		final MetamodelImpl metamodel = declaringType.getMetamodel();

		final GeneratedValueMetadata generatedValue = metadata.getGeneratedValue();
		if (generatedValue != null) {
			this.idType = jdbcAdaptor.supports(generatedValue.getStrategy());

			// if generator is not specified then assign the default name
			if (StringUtils.isNotBlank(generatedValue.getGenerator())) {
				this.generator = generatedValue.getGenerator();
			}
			else {
				this.generator = AbstractGenerator.DEFAULT_NAME;

				if (this.idType == IdType.SEQUENCE) {
					metamodel.addSequenceGenerator(null);
				}
				else if (this.idType == IdType.TABLE) {
					metamodel.addTableGenerator(null);
				}
			}

			// add sequence generator if defined
			if (metadata.getSequenceGenerator() != null) {
				metamodel.addSequenceGenerator(metadata.getSequenceGenerator());
			}

			// add table generator if defined
			if (metadata.getTableGenerator() != null) {
				metamodel.addTableGenerator(metadata.getTableGenerator());
			}
		}
		else {
			this.generator = null;
			this.idType = IdType.MANUAL;
		}
	}

	/**
	 * Fills the sequence / table generated value.
	 * <p>
	 * The operation returns false if at least one entity needs to obtain identity from the database.
	 * 
	 * @param type
	 *            the entity type
	 * @param managedInstance
	 *            the managed instance
	 * @param instance
	 *            the instance to fill ids.
	 * @return false if all OK, true if if at least one entity needs to obtain identity from the database
	 * 
	 * @since $version
	 */
	@SuppressWarnings("unchecked")
	public boolean fillValue(EntityTypeImpl<?> type, ManagedInstance<?> managedInstance, Object instance) {
		T value = this.get(instance);

		// if the attribute already has value, bail out
		if (value != null) {
			return true;
		}

		if (this.idType == null) {
			if (value == null) {
				value = (T) type.getMappedId(this.getName(), managedInstance.getInstance());
				if (value != null) {
					this.set(instance, value);

					return true;
				}
			}

			throw new PersistenceException("Ids should be manually assigned");
		}

		Long id;

		// fill the id
		switch (this.idType) {
			case IDENTITY:
				// indicate a requirement for an implicit flush
				return false;
			case SEQUENCE:
				// fill with the sequence
				id = this.getMetamodel().getNextSequence(this.generator);
				this.set(instance, ReflectHelper.convertNumber(id, this.getJavaType()));
				break;
			case TABLE:
				// fill with the next table generator id
				id = this.getMetamodel().getNextTableValue(this.generator);
				this.set(instance, ReflectHelper.convertNumber(id, this.getJavaType()));
				break;
			case MANUAL:
				// not possible, manual already handled
				break;
		}

		return true;
	}

	/**
	 * Returns columnTransformer definition
	 * 
	 * @return the columnTransformer
	 * 
	 * @since $version
	 */
	public ColumnTransformerMetadata getColumnTransformer() {
		return this.columnTransformer;
	}

	/**
	 * Returns the enum type of the attribute.
	 * 
	 * @return the enum type of the attribute
	 * 
	 * @since $version
	 */
	public EnumType getEnumType() {
		return this.enumType;
	}

	/**
	 * Returns the id type of the attribute.
	 * 
	 * @return the id type of the attribute
	 * 
	 * @since $version
	 */
	public IdType getIdType() {
		return this.idType;
	}

	/**
	 * Returns the index of the basic attribute.
	 * 
	 * @return the index of the basic attribute
	 * 
	 * @since $version
	 */
	public IndexMetadata getIndex() {
		return this.index;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PhysicalAttributeMetadata getMetadata() {
		return (PhysicalAttributeMetadata) super.getMetadata();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public PersistentAttributeType getPersistentAttributeType() {
		return PersistentAttributeType.BASIC;
	}

	/**
	 * Returns the temporal type of the attribute.
	 * 
	 * @return the temporal type of the attribute
	 * 
	 * @since $version
	 */
	public TemporalType getTemporalType() {
		return this.temporalType;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public BasicTypeImpl<T> getType() {
		return this.type;
	}

	private boolean inferLobType(boolean lob) {
		if (lob) {
			return true;
		}

		try {
			TypeFactory.getSqlType(this.getJavaType(), this.temporalType, this.enumType, false);
		}
		catch (final IllegalArgumentException e) {
			if (Serializable.class.isAssignableFrom(this.getJavaType())) {
				return true;
			}

			throw e;
		}

		return false;
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
	public boolean isId() {
		return this.idType != null;
	}

	/**
	 * Returns if the attribute is lob.
	 * 
	 * @return true if the attribute is lob, false otherwise
	 * 
	 * @since $version
	 */
	public boolean isLob() {
		return this.lob;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isOptional() {
		return this.optional;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean isVersion() {
		return this.version;
	}
}
