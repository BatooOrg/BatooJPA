/*
 * Copyright 2011, Mysema Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.batoo.jpa.community.test.t5;

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
	@Index(name = "ix_kittens")
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
