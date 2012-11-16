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
package org.batoo.jpa.core.impl.manager;

import java.util.List;
import java.util.Map;

import org.batoo.jpa.core.impl.manager.Callback.CallbackType;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.IdentifiableTypeImpl;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.CallbackMetadata;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata;
import org.batoo.jpa.parser.metadata.EntityListenerMetadata.EntityListenerType;
import org.batoo.jpa.parser.metadata.type.IdentifiableTypeMetadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Manager for callbacks
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class CallbackManager {

	private final boolean excludeDefaultListeners;
	private final boolean excludeSuperclassListeners;

	private final Callback[] postLoad;
	private final Callback[] postPersist;
	private final Callback[] postRemove;
	private final Callback[] postUpdate;
	private final Callback[] prePersist;
	private final Callback[] preRemove;
	private final Callback[] preUpdate;

	/**
	 * Constructor for identifiable types.
	 * 
	 * @param metadata
	 *            the identifiable metadata
	 * @param javaType
	 *            the java type of the identifiable
	 * 
	 * @since 2.0.0
	 */
	public CallbackManager(IdentifiableTypeMetadata metadata, Class<?> javaType) {
		super();

		this.excludeDefaultListeners = metadata.excludeDefaultListeners();
		this.excludeSuperclassListeners = metadata.excludeSuperclassListeners();

		final Map<EntityListenerType, List<Callback>> callbacks = this.linkCallbacks(javaType, metadata.getCallbacks(), metadata.getListeners());

		this.postLoad = this.getCallbacks(callbacks, EntityListenerType.POST_LOAD);
		this.postPersist = this.getCallbacks(callbacks, EntityListenerType.POST_PERSIST);
		this.postRemove = this.getCallbacks(callbacks, EntityListenerType.POST_REMOVE);
		this.postUpdate = this.getCallbacks(callbacks, EntityListenerType.POST_UPDATE);
		this.prePersist = this.getCallbacks(callbacks, EntityListenerType.PRE_PERSIST);
		this.preRemove = this.getCallbacks(callbacks, EntityListenerType.PRE_REMOVE);
		this.preUpdate = this.getCallbacks(callbacks, EntityListenerType.PRE_UPDATE);
	}

	/**
	 * Constructor for metamodel.
	 * 
	 * @param defaultListeners
	 *            the list of default listeners
	 * 
	 * @since 2.0.0
	 */
	public CallbackManager(List<EntityListenerMetadata> defaultListeners) {
		super();

		this.excludeDefaultListeners = false;
		this.excludeSuperclassListeners = false;

		final Map<EntityListenerType, List<Callback>> callbacks = this.linkCallbacks(null, null, defaultListeners);

		this.postLoad = this.getCallbacks(callbacks, EntityListenerType.POST_LOAD);
		this.postPersist = this.getCallbacks(callbacks, EntityListenerType.POST_PERSIST);
		this.postRemove = this.getCallbacks(callbacks, EntityListenerType.POST_REMOVE);
		this.postUpdate = this.getCallbacks(callbacks, EntityListenerType.POST_REMOVE);
		this.prePersist = this.getCallbacks(callbacks, EntityListenerType.PRE_PERSIST);
		this.preRemove = this.getCallbacks(callbacks, EntityListenerType.PRE_REMOVE);
		this.preUpdate = this.getCallbacks(callbacks, EntityListenerType.PRE_UPDATE);

	}

	/**
	 * Returns if the default listeners are excluded.
	 * 
	 * @return true the default listeners are excluded, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean excludeDefaultListeners() {
		return this.excludeDefaultListeners;
	}

	/**
	 * Returns if the super class listeners are excluded.
	 * 
	 * @return true the super class listeners are excluded, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean excludeSuperclassListeners() {
		return this.excludeSuperclassListeners;
	}

	/**
	 * Fires the callbacks.
	 * 
	 * @param instance
	 *            the instance
	 * @param type
	 *            the type
	 * 
	 * @since 2.0.0
	 */
	public void fireCallbacks(Object instance, EntityListenerType type) {
		Callback[] callbacks = null;

		switch (type) {
			case POST_LOAD:
				callbacks = this.postLoad;
				break;
			case POST_REMOVE:
				callbacks = this.postRemove;
				break;
			case POST_PERSIST:
				callbacks = this.postPersist;
				break;
			case POST_UPDATE:
				callbacks = this.postUpdate;
				break;
			case PRE_PERSIST:
				callbacks = this.prePersist;
				break;
			case PRE_REMOVE:
				callbacks = this.preRemove;
				break;
			case PRE_UPDATE:
				callbacks = this.preUpdate;
				break;
		}

		if (callbacks != null) {
			for (final Callback callback : callbacks) {
				callback.fire(instance);
			}
		}
	}

	/**
	 * Returns the callback availability.
	 * 
	 * @param metamodel
	 *            the metamodel
	 * @param superType
	 *            the super type, may be <code>null</code>
	 * @return the availability
	 * 
	 * @since 2.0.0
	 */
	public CallbackAvailability getAvailibility(MetamodelImpl metamodel, IdentifiableTypeImpl<?> superType) {
		final CallbackAvailability availability = new CallbackAvailability();

		if (!this.excludeDefaultListeners) {
			metamodel.updateAvailability(availability);
		}

		if (!this.excludeSuperclassListeners && (superType != null)) {
			superType.updateAvailability(availability, null);
		}

		return availability.updateAvailability(this);
	}

	private Callback[] getCallbacks(Map<EntityListenerType, List<Callback>> callbacks, EntityListenerType type) {
		final List<Callback> list = callbacks.get(type);

		if (list == null) {
			return null;
		}

		return list.toArray(new Callback[list.size()]);
	}

	/**
	 * Returns if the default listeners are excluded.
	 * 
	 * @return true if the default listeners are excluded, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean isExcludeDefaultListeners() {
		return this.excludeDefaultListeners;
	}

	/**
	 * Returns if the super type listeners are excluded.
	 * 
	 * @return true if the super type listeners are excluded, false otherwise
	 * 
	 * @since 2.0.0
	 */
	public boolean isExcludeSuperclassListeners() {
		return this.excludeSuperclassListeners;
	}

	private void linkCallback(Map<EntityListenerType, List<Callback>> callbacks, final Callback callback) {
		List<Callback> list = callbacks.get(callback.getListenerType());
		if (list == null) {
			list = Lists.newArrayList();
			callbacks.put(callback.getListenerType(), list);
		}

		list.add(callback);
	}

	private Map<EntityListenerType, List<Callback>> linkCallbacks(Class<?> javaType, List<CallbackMetadata> callbacks,
		final List<EntityListenerMetadata> listeners) {

		final Map<EntityListenerType, List<Callback>> callbackMap = Maps.newHashMap();

		if (listeners != null) {
			for (final EntityListenerMetadata listenerMetadata : listeners) {
				Class<?> clazz;
				try {
					clazz = Class.forName(listenerMetadata.getClassName());
				}
				catch (final ClassNotFoundException e) {
					throw new MappingException("Cannot map listener", listenerMetadata.getLocator());
				}

				for (final CallbackMetadata callbackMetadata : listenerMetadata.getCallbacks()) {
					this.linkCallback(callbackMap, new Callback(//
						listenerMetadata.getLocator(), //
						clazz, //
						callbackMetadata.getName(), //
						callbackMetadata.getType(), //
						CallbackType.LISTENER));
				}
			}
		}

		if (callbacks != null) {
			for (final CallbackMetadata callbackMetadata : callbacks) {
				this.linkCallback(callbackMap, new Callback(//
					callbackMetadata.getLocator(), //
					javaType, //
					callbackMetadata.getName(), //
					callbackMetadata.getType(), //
					CallbackType.CALLBACK));
			}
		}

		return callbackMap;
	}

	/**
	 * Returns the PostLoad callbacks.
	 * 
	 * @return the PostLoad callbacks
	 * 
	 * @since 2.0.0
	 */
	public Callback[] postLoad() {
		return this.postLoad;
	}

	/**
	 * Returns the PostPersist callbacks.
	 * 
	 * @return the PostPersist callbacks
	 * 
	 * @since 2.0.0
	 */
	public Callback[] postPersist() {
		return this.postPersist;
	}

	/**
	 * Returns the PostRemove callbacks.
	 * 
	 * @return the PostRemove callbacks
	 * 
	 * @since 2.0.0
	 */
	public Callback[] postRemove() {
		return this.postRemove;
	}

	/**
	 * Returns the PostUpdate callbacks.
	 * 
	 * @return the PostUpdate callbacks
	 * 
	 * @since 2.0.0
	 */
	public Callback[] postUpdate() {
		return this.postUpdate;
	}

	/**
	 * Returns the PrePersist callbacks.
	 * 
	 * @return the PrePersist callbacks
	 * 
	 * @since 2.0.0
	 */
	public Callback[] prePersist() {
		return this.prePersist;
	}

	/**
	 * Returns the PreRemove callbacks.
	 * 
	 * @return the PreRemove callbacks
	 * 
	 * @since 2.0.0
	 */
	public Callback[] preRemove() {
		return this.preRemove;
	}

	/**
	 * Returns the PreUpdate callbacks.
	 * 
	 * @return the PreUpdate callbacks
	 * 
	 * @since 2.0.0
	 */
	public Callback[] preUpdate() {
		return this.preUpdate;
	}
}
