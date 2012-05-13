package org.batoo.jpa.core.impl.instance;

import javax.persistence.PersistenceException;

public class PriorityList {

	private final ManagedInstance<?>[] instances;
	private final int matrix[][]; // adjacency matrix

	private final ManagedInstance<?>[] sortedinstances;

	private int noInstances;

	public PriorityList(int size) {
		this.instances = new ManagedInstance[size];
		this.matrix = new int[size][size];

		this.noInstances = 0;

		for (int i = 0; i < size; i++) {
			for (int k = 0; k < size; k++) {
				this.matrix[i][k] = 0;
			}
		}

		this.sortedinstances = new ManagedInstance[size]; // sorted vert labels
	}

	public void addDependency(int dependent, int dependency) {
		this.matrix[dependency][dependent] = 1;
	}

	public void addInstance(ManagedInstance<?> instance) {
		this.instances[this.noInstances++] = instance;
	}

	public void deleteInstance(int removeInstance) {
		// if not last instance, delete from the instance list
		if (removeInstance != (this.noInstances - 1)) {
			for (int j = removeInstance; j < (this.noInstances - 1); j++) {
				this.instances[j] = this.instances[j + 1];
			}

			for (int row = removeInstance; row < (this.noInstances - 1); row++) {
				this.moveRowUp(row, this.noInstances);
			}

			for (int col = removeInstance; col < (this.noInstances - 1); col++) {
				this.moveColLeft(col, this.noInstances - 1);
			}
		}
		this.noInstances--; // one less instance
	}

	private void moveColLeft(int col, int length) {
		for (int row = 0; row < length; row++) {
			this.matrix[row][col] = this.matrix[row][col + 1];
		}
	}

	private void moveRowUp(int row, int length) {
		for (int col = 0; col < length; col++) {
			this.matrix[row][col] = this.matrix[row + 1][col];
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
	private int noSuccessors() {
		// dependency from row to column in adjMat
		boolean isDependency;

		for (int row = 0; row < this.noInstances; row++) {
			isDependency = false; // check dependencies

			for (int col = 0; col < this.noInstances; col++) {
				if (this.matrix[row][col] > 0) // if dependency to another,
				{
					isDependency = true;
					break; // this instance has a successor try another
				}
			}
			if (!isDependency) {
				return row;
			}
		}

		throw new PersistenceException("Dependencies not yet supported");
	}

	/**
	 * Sorts the instances based on their dependencies
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedInstance<?>[] sort() {
		// while there is remaining instances continue the operation
		while (this.noInstances > 0) {
			// get an instance with no successors, or -1
			final int currentInstance = this.noSuccessors();

			// move instance into sorted array (start at end)
			this.sortedinstances[this.noInstances - 1] = this.instances[currentInstance];

			this.deleteInstance(currentInstance); // delete instance
		}

		return this.sortedinstances;
	}
}
