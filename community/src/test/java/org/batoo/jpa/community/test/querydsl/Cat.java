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

package org.batoo.jpa.community.test.querydsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.batoo.jpa.annotations.Index;

/**
 * The Class Cat.
 */
@Entity
@DiscriminatorValue("C")
@SuppressWarnings("javadoc")
public class Cat extends Animal {

	private int breed;

	private Color eyecolor;

	@OneToMany
	@JoinTable(name = "kittens", joinColumns = @JoinColumn(name = "cat_id"), inverseJoinColumns = @JoinColumn(name = "kitten_id"))
	@Index(name = "ind")
	private final List<Cat> kittens = new ArrayList<Cat>();

	@OneToMany
	@JoinTable(name = "kittens_set", joinColumns = @JoinColumn(name = "cat_id"), inverseJoinColumns = @JoinColumn(name = "kitten_id"))
	private Set<Cat> kittensSet;

	@ManyToOne
	private Cat mate;

	public Cat() {
	}

	public Cat(int id) {
		this.setId(id);
	}

	public Cat(String name, int id) {
		this.setId(id);
		this.setName(name);
	}

	public Cat(String name, int id, double bodyWeight) {
		this(name, id);
		this.setBodyWeight(bodyWeight);
		this.setFloatProperty((float) bodyWeight);
	}

	public void addKitten(Cat kitten) {
		this.kittens.add(kitten);
		// kittensArray = new Cat[]{kitten};
	}

	public int getBreed() {
		return this.breed;
	}

	public Color getEyecolor() {
		return this.eyecolor;
	}

	public List<Cat> getKittens() {
		return this.kittens;
	}

	// public Cat[] getKittensArray() {
	// return kittensArray;
	// }

	public Set<Cat> getKittensSet() {
		return this.kittensSet;
	}

	public Cat getMate() {
		return this.mate;
	}

	public void setKittensSet(Set<Cat> kittensSet) {
		this.kittensSet = kittensSet;
	}

}
