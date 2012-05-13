package org.batoo.jpa.core.impl.instance;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.persistence.PersistenceException;

import org.batoo.jpa.core.util.IdentityHashSet;

public class PriorityList {

	private final IdentityHashMap<ManagedInstance<?>, IdentityHashSet<ManagedInstance<?>>> dependencies;
	private final ManagedInstance<?>[] sortedinstances;
	private int itemNo;

	public PriorityList(int size) {
		super();

		this.dependencies = new IdentityHashMap<ManagedInstance<?>, IdentityHashSet<ManagedInstance<?>>>(size);
		this.sortedinstances = new ManagedInstance[size];
		this.itemNo = size - 1;
	}

	public void addDependency(ManagedInstance<?> dependent, ManagedInstance<?> dependency) {
		this.dependencies.get(dependency).add(dependent);
	}

	public void addInstance(ManagedInstance<?> instance) {
		this.dependencies.put(instance, new IdentityHashSet<ManagedInstance<?>>());
	}

	/**
	 * Deletes the instance from the dependencies
	 * 
	 * @param instance
	 *            the instance to delete
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void deleteInstance(ManagedInstance<?> instance) {
		for (final IdentityHashSet<ManagedInstance<?>> dependencies : this.dependencies.values()) {
			dependencies.remove(instance);
		}
	}

	/**
	 * Returns an instance with no dependencies
	 * 
	 * @return an instance with no dependencies
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private ManagedInstance<?> noSuccessors() {
		for (final Iterator<Entry<ManagedInstance<?>, IdentityHashSet<ManagedInstance<?>>>> i = this.dependencies.entrySet().iterator(); i.hasNext();) {
			final Entry<ManagedInstance<?>, IdentityHashSet<ManagedInstance<?>>> entry = i.next();

			if (entry.getValue().size() == 0) {
				final ManagedInstance<?> instance = entry.getKey();

				i.remove();

				return instance;
			}
		}

		throw new PersistenceException("Circular dependencies not yet supported");
	}

	/**
	 * Sorts the instances based on their dependencies
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedInstance<?>[] sort() {
		// while there is remaining instances continue the operation
		while (this.dependencies.size() > 0) {
			// get an instance with no successors, or -1
			final ManagedInstance<?> instance = this.noSuccessors();

			// move instance into sorted array
			this.sortedinstances[this.itemNo--] = instance;

			this.deleteInstance(instance); // delete instance
		}

		return this.sortedinstances;
	}
}
