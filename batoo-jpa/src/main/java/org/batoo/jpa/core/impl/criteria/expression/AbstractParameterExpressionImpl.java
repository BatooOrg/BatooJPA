/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
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

package org.batoo.jpa.core.impl.criteria.expression;

import java.sql.Connection;

import javax.persistence.metamodel.Type.PersistenceType;

import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.criteria.AbstractCriteriaQueryImpl;
import org.batoo.jpa.core.impl.criteria.BaseQueryImpl;
import org.batoo.jpa.core.impl.model.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.TypeImpl;
import org.batoo.jpa.core.impl.model.attribute.SingularAttributeImpl;
import org.batoo.jpa.jdbc.AbstractColumn;

/**
 * Type of criteria query parameter expressions.
 * 
 * @param <T>
 *            the type of the parameter expression
 * @author hceylan
 * @since 2.0.0
 */
public abstract class AbstractParameterExpressionImpl<T> extends AbstractExpression<T> {

	private TypeImpl<?> type;

	/**
	 * @param type
	 *            the persistent type of the parameter
	 * @param paramClass
	 *            the class of the parameter
	 * 
	 * @since 2.0.0
	 */
	public AbstractParameterExpressionImpl(TypeImpl<T> type, Class<T> paramClass) {
		super(paramClass);

		this.type = type;
	}

	/**
	 * Ensures the alias is generated.
	 * 
	 * @param query
	 *            the query
	 * 
	 * @since 2.0.0
	 */
	protected abstract void ensureAlias(BaseQueryImpl<?> query);

	/**
	 * Ensures the type has been resolved.
	 * 
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @since 2.0.0
	 */
	protected void ensureTypeResolved(MetamodelImpl metamodel) {
		if (this.type == null) {
			this.type = metamodel.createBasicType(this.getJavaType());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateJpqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		return this.generateJpqlRestriction(query);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSqlSelect(AbstractCriteriaQueryImpl<?> query, boolean selected) {
		this.ensureAlias(query);

		return null;
	}

	/**
	 * Returns the number of SQL parameters when expanded.
	 * 
	 * @param metamodelImpl
	 *            the metamodel
	 * @return the number of SQL parameters when expanded
	 * 
	 * @since 2.0.0
	 */
	public int getExpandedCount(MetamodelImpl metamodelImpl) {
		if (this.getJavaType() == Class.class) {
			return 1;
		}

		this.ensureTypeResolved(metamodelImpl);

		if ((this.type == null) || (this.type.getPersistenceType() == PersistenceType.BASIC)) {
			return 1;
		}
		else if (this.type.getPersistenceType() == PersistenceType.EMBEDDABLE) {
			return ((EmbeddableTypeImpl<?>) this.type).getAttributeCount();
		}

		return ((EntityTypeImpl<?>) this.type).getPrimaryTable().getPkColumns().size();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String[] getSqlRestrictionFragments(BaseQueryImpl<?> query) {
		this.ensureAlias(query);

		query.setNextSqlParam(this);

		final String[] restrictions = new String[this.getExpandedCount(query.getMetamodel())];

		for (int i = 0; i < restrictions.length; i++) {
			restrictions[i] = "?";
		}

		return restrictions;
	}

	private void setParameter(Connection connection, Object[] parameters, MutableInt sqlIndex, Object value, final EmbeddableTypeImpl<?> type) {
		final SingularAttributeImpl<?, ?>[] attributes = type.getSingularMappings();

		for (final SingularAttributeImpl<?, ?> attribute : attributes) {
			switch (attribute.getPersistentAttributeType()) {
				case BASIC:
					parameters[sqlIndex.intValue()] = attribute.get(value);
					sqlIndex.increment();
					break;
				case MANY_TO_ONE:
				case ONE_TO_ONE:
					this.setParameter(connection, parameters, sqlIndex, attribute.get(value), (EntityTypeImpl<?>) attribute.getType());
					break;
				case EMBEDDED:
					this.setParameter(connection, parameters, sqlIndex, attribute.get(value), (EmbeddableTypeImpl<?>) this.type);
				case ELEMENT_COLLECTION:
				case MANY_TO_MANY:
				case ONE_TO_MANY:
					// N/A
			}
		}
	}

	private void setParameter(Connection connection, Object[] parameters, MutableInt sqlIndex, Object value, final EntityTypeImpl<?> type) {
		for (final AbstractColumn column : type.getPrimaryTable().getPkColumns()) {
			if (value != null) {
				parameters[sqlIndex.intValue()] = column.getValue(connection, value);
			}
			else {
				parameters[sqlIndex.intValue()] = null;
			}

			sqlIndex.increment();
		}
	}

	/**
	 * Sets the parameters expanding if necessary.
	 * 
	 * @param metamodel
	 *            the metamodel
	 * @param connection
	 *            the connection
	 * @param parameters
	 *            the SQL parameters
	 * @param sqlIndex
	 *            the index corresponding to expanded SQL parameter
	 * @param value
	 *            the value to set to the parameter
	 * 
	 * @since 2.0.0
	 */
	protected void setParameter(MetamodelImpl metamodel, Connection connection, Object[] parameters, MutableInt sqlIndex, Object value) {
		// type parameter
		if (this.getJavaType() == Class.class) {
			final EntityTypeImpl<?> entity = metamodel.entity((Class<?>) value);

			if (entity == null) {
				throw new IllegalArgumentException("Type is not managed: " + value);
			}

			if (entity.getRootType().getInheritanceType() == null) {
				throw new IllegalArgumentException("Entity does not have inheritence: " + entity.getName());
			}

			parameters[sqlIndex.intValue()] = entity.getDiscriminatorValue();
			sqlIndex.increment();
		}
		else {
			this.ensureTypeResolved(metamodel);

			if ((this.type == null) || (this.type.getPersistenceType() == PersistenceType.BASIC)) {
				parameters[sqlIndex.intValue()] = value;

				sqlIndex.increment();
			}
			else {
				final TypeImpl<? extends Object> valueType = value == null ? this.type : metamodel.type(value.getClass());
				if ((valueType != null) && (valueType.getPersistenceType() == PersistenceType.BASIC)) {
					parameters[sqlIndex.intValue()] = value;

					sqlIndex.increment();
				}
				else if (this.type.getPersistenceType() == PersistenceType.ENTITY) {
					final EntityTypeImpl<?> type = (EntityTypeImpl<?>) this.type;

					this.setParameter(connection, parameters, sqlIndex, value, type);
				}
				else {
					final EmbeddableTypeImpl<?> type = (EmbeddableTypeImpl<?>) this.type;

					this.setParameter(connection, parameters, sqlIndex, value, type);
				}
			}
		}
	}
}
