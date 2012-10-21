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

import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.core.impl.manager.CallbackAvailability;
import org.batoo.jpa.core.impl.manager.CallbackManager;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.attribute.AttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.attribute.EmbeddedAttribute;
import org.batoo.jpa.core.impl.model.attribute.SingularAttributeImpl;
import org.batoo.jpa.core.impl.model.attribute.VersionType;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata.EntityListenerType;
import org.batoo.jpa.parser.metadata.attribute.AttributesMetadata;
import org.batoo.jpa.parser.metadata.attribute.EmbeddedIdAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.IdAttributeMetadata;
import org.batoo.jpa.parser.metadata.attribute.VersionAttributeMetadata;
import org.batoo.jpa.parser.metadata.type.IdentifiableTypeMetadata;
import org.batoo.jpa.parser.metadata.type.ManagedTypeMetadata;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link IdentifiableType}.
 * 
 * @param <X>
 *            The represented entity or mapped superclass type.
 * 
 * @author hceylan
 * @since $version
 */
public abstract class IdentifiableTypeImpl<X> extends ManagedTypeImpl<X> implements IdentifiableType<X> {

	private final IdentifiableTypeImpl<? super X> supertype;
	private final EmbeddableTypeImpl<?> idType;

	private final Map<String, SingularAttributeImpl<X, ?>> declaredIdAttributes = Maps.newHashMap();
	private final Map<String, SingularAttributeImpl<? super X, ?>> idAttributes = Maps.newHashMap();
	private EmbeddedAttribute<X, ?> declaredEmbeddedId;
	private EmbeddedAttribute<? super X, ?> embeddedId;

	private BasicAttribute<X, ?> declaredVersionAttribute;
	private BasicAttribute<? super X, ?> versionAttribute;
	private VersionType versionType;
	private final CallbackManager callbackManager;
	private CallbackAvailability callbackAvailability;

	/**
	 * @param metamodel
	 *            the metamodel
	 * @param supertype
	 *            the super type
	 * @param javaType
	 *            the java type of the managed type
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public IdentifiableTypeImpl(MetamodelImpl metamodel, IdentifiableTypeImpl<? super X> supertype, Class<X> javaType, IdentifiableTypeMetadata metadata) {
		super(metamodel, javaType, metadata);

		this.supertype = supertype;
		this.idType = this.getIdClass(metadata);

		this.addIdAttributes(metadata);

		this.callbackManager = new CallbackManager(metadata, this.getJavaType());
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings({ "unchecked" })
	protected void addAttribute(AttributeImpl<? super X, ?> attribute) {
		if (attribute instanceof BasicAttribute) {
			final BasicAttribute<? super X, ?> basicAttribute = (BasicAttribute<? super X, ?>) attribute;

			if (basicAttribute.isId()) {
				if (attribute.getDeclaringType() == this) {
					this.declaredIdAttributes.put(attribute.getName(), (BasicAttribute<X, ?>) basicAttribute);
				}

				if (this.embeddedId != null) {
					throw new MappingException("Embbeded id attributes cannot be combined with other id attributes.", //
						attribute.getLocator(), this.embeddedId.getLocator());
				}

				if ((this.idType == null) && (this.idAttributes.size() > 0)) {
					throw new MappingException("Multiple id attributes are only allowed with id class declaration.", //
						attribute.getLocator(), this.idAttributes.values().iterator().next().getLocator());
				}

				this.idAttributes.put(attribute.getName(), basicAttribute);
			}

			if (basicAttribute.isVersion()) {
				if (this.versionAttribute != null) {
					throw new MappingException("Multiple version attributes not supported.", this.versionAttribute.getLocator(), attribute.getLocator());
				}

				if (attribute.getDeclaringType() == this) {
					this.declaredVersionAttribute = (BasicAttribute<X, ?>) basicAttribute;
				}

				this.versionAttribute = basicAttribute;
				this.versionType = VersionType.versionType(this.versionAttribute.getJavaType());
			}

		}
		else if (attribute instanceof EmbeddedAttribute) {
			final EmbeddedAttribute<? super X, ?> embeddedAttribute = (EmbeddedAttribute<? super X, ?>) attribute;
			if (embeddedAttribute.isId()) {
				if (this.idType != null) {
					throw new MappingException("When IdClass defined, it is illegal to use embedded id attributes.", this.getLocator());
				}

				if (this.idAttributes.size() > 0) {
					throw new MappingException("Embbeded id attributes cannot be combined with other id attributes.", //
						attribute.getLocator(), this.idAttributes.values().iterator().next().getLocator());
				}

				if (embeddedAttribute.getDeclaringType() == this) {
					this.declaredEmbeddedId = (EmbeddedAttribute<X, ?>) embeddedAttribute;
				}

				this.idAttributes.put(attribute.getName(), (SingularAttributeImpl<? super X, ?>) attribute);
				this.embeddedId = embeddedAttribute;
			}
		}

		super.addAttribute(attribute);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void addAttributes(ManagedTypeMetadata entityMetadata) {
		if (this.supertype != null) {
			for (final Attribute<?, ?> attribute : this.supertype.getAttributes()) {
				this.addAttribute((AttributeImpl<? super X, ?>) attribute);
			}

			if (this.supertype.versionAttribute != null) {
				this.addAttribute(this.supertype.versionAttribute);
			}
		}

		super.addAttributes(entityMetadata);
	}

	/**
	 * Creates and adds the attributes of the managed type from the metadata.
	 * 
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addIdAttributes(ManagedTypeMetadata metadata) {
		final AttributesMetadata attributes = metadata.getAttributes();

		// embedded-id attributes
		for (final EmbeddedIdAttributeMetadata attribute : attributes.getEmbeddedIds()) {
			this.addAttribute(new EmbeddedAttribute(this, attribute));
		}

		// add id attributes
		for (final IdAttributeMetadata attribute : attributes.getIds()) {
			this.addAttribute(new BasicAttribute(this, attribute));
		}

		// add version attributes
		for (final VersionAttributeMetadata attribute : attributes.getVersions()) {
			this.declaredVersionAttribute = new BasicAttribute(this, attribute);
			this.addAttribute(this.declaredVersionAttribute);
		}
	}

	/**
	 * Fires the callbacks.
	 * 
	 * @param instance
	 *            the instance
	 * @param type
	 *            the listener type
	 * @param self
	 *            if the object belongs to this type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void fireCallbacks(boolean self, Object instance, EntityListenerType type) {
		if (!this.callbackManager.excludeDefaultListeners() && self) {
			this.getMetamodel().fireCallbacks(instance, type);
		}

		if ((this.getSupertype() != null) && !this.callbackManager.excludeSuperclassListeners()) {
			this.getSupertype().fireCallbacks(false, instance, type);
		}

		this.callbackManager.fireCallbacks(instance, type);
	}

	/**
	 * Returns the callback availability.
	 * 
	 * @return the callback availability
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public CallbackAvailability getAvailability() {
		if (this.callbackAvailability != null) {
			return this.callbackAvailability;
		}

		synchronized (this) {
			if (this.callbackAvailability != null) {
				return this.callbackAvailability;
			}

			return this.callbackAvailability = this.callbackManager.getAvailibility(this.getMetamodel(), this.getSupertype());
		}
	}

	/**
	 * Returns the callbackManager.
	 * 
	 * @return the callbackManager
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected CallbackManager getCallbackManager() {
		return this.callbackManager;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> SingularAttribute<X, Y> getDeclaredId(Class<Y> type) {
		if ((this.declaredEmbeddedId != null) && (type == this.declaredEmbeddedId.getJavaType())) {
			return (SingularAttribute<X, Y>) this.declaredEmbeddedId;
		}

		if (this.idType != null) {
			throw new IllegalArgumentException("Type defines multiple id attributes");
		}

		if (this.declaredIdAttributes.size() > 1) {
			return (SingularAttribute<X, Y>) this.declaredIdAttributes.values().iterator().next();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> SingularAttribute<X, Y> getDeclaredVersion(Class<Y> type) {
		return (SingularAttribute<X, Y>) this.versionAttribute;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> SingularAttribute<? super X, Y> getId(Class<Y> type) {
		if ((this.embeddedId != null) && (type == this.embeddedId.getJavaType())) {
			return (SingularAttribute<? super X, Y>) this.embeddedId;
		}

		if (this.idType != null) {
			throw new IllegalArgumentException("Type defines multiple id attributes");
		}

		if (this.idAttributes.size() > 1) {
			return (SingularAttribute<? super X, Y>) this.idAttributes.values().iterator().next();
		}

		final SingularAttributeImpl<? super X, Y> attribute = (SingularAttributeImpl<? super X, Y>) this.idAttributes.values().iterator().next();

		return attribute.getJavaType() == type ? attribute : null;
	}

	/**
	 * @param metadata
	 * @return
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private EmbeddableTypeImpl<?> getIdClass(IdentifiableTypeMetadata metadata) {
		if (StringUtils.isNotBlank(metadata.getIdClass())) {
			try {
				final Class<?> clazz = Class.forName(metadata.getIdClass());
				final EmbeddableTypeImpl<?> embeddable = this.getMetamodel().embeddable(clazz);

				if (embeddable == null) {
					throw new MappingException("IdClass " + clazz + " does not have a corresponding embeddable type.", metadata.getLocator());
				}

				return embeddable;
			}
			catch (final ClassNotFoundException e) {}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Set<SingularAttribute<? super X, ?>> getIdClassAttributes() {
		if (this.idType != null) {
			final Set<SingularAttribute<? super X, ?>> idAttributes = Sets.newHashSet();
			idAttributes.addAll(this.idAttributes.values());

			return idAttributes;
		}

		throw new IllegalArgumentException("Type does not have an id class definition");
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Type<?> getIdType() {
		if (this.idType != null) {
			return this.idType;
		}

		if (this.embeddedId != null) {
			return this.embeddedId.getType();
		}

		return this.idAttributes.values().iterator().next().getType();
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
	 * {@inheritDoc}
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <Y> BasicAttribute<? super X, Y> getVersion(Class<Y> type) {
		if (this.versionAttribute == null) {
			return null;
		}

		if (this.versionAttribute.getJavaType() != type) {
			throw new IllegalArgumentException("Version does not match specified type : " + type.getName());
		}

		return (BasicAttribute<? super X, Y>) this.versionAttribute;
	}

	/**
	 * Returns the version attribute.
	 * 
	 * @return the version attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public BasicAttribute<? super X, ?> getVersionAttribute() {
		return this.versionAttribute;
	}

	/**
	 * Returns the type of the version attribute.
	 * 
	 * @return the type of the version attribute
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public VersionType getVersionType() {
		return this.versionType;
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
		return this.versionAttribute != null;
	}

	/**
	 * Updates the callback availability.
	 * 
	 * @param availability
	 *            the callback availability
	 * @param forUpdates
	 *            true if for updates or false for removals
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void updateAvailability(CallbackAvailability availability, Boolean forUpdates) {
		availability.updateAvailability(this.getAvailability(), forUpdates);
	}
}
