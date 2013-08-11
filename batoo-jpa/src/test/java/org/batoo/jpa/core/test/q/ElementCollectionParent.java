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

package org.batoo.jpa.core.test.q;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since 2.0.0
 */
@Entity
public class ElementCollectionParent {

	@SuppressWarnings("javadoc")
	public enum FooType {
		TYPE1,
		TYPE2,
		TYPE3
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer key;

	@ElementCollection
	@CollectionTable(name = "CODES1")
	private final List<String> codes1 = Lists.newArrayList();

	@ElementCollection
	@CollectionTable(name = "CODES2")
	private final Set<String> codes2 = Sets.newHashSet();

	@ElementCollection
	@CollectionTable(name = "CODES3")
	private final Set<FooType> codes3 = Sets.newHashSet();

	@ElementCollection
	@CollectionTable(name = "CODES4")
	@Enumerated(EnumType.STRING)
	private final Set<FooType> codes4 = Sets.newHashSet();

	@ElementCollection
	@CollectionTable(name = "MAP")
	private final Map<String, String> codes5 = Maps.newHashMap();

	private String value;

	/**
	 * Returns the codes1.
	 * 
	 * @return the codes1
	 * @since 2.0.0
	 */
	public List<String> getCodes1() {
		return this.codes1;
	}

	/**
	 * Returns the codes2 of the Foo.
	 * 
	 * @return the codes2 of the Foo
	 * 
	 * @since 2.0.0
	 */
	public Set<String> getCodes2() {
		return this.codes2;
	}

	/**
	 * Returns the codes3 of the Foo.
	 * 
	 * @return the codes3 of the Foo
	 * 
	 * @since 2.0.0
	 */
	public Set<FooType> getCodes3() {
		return this.codes3;
	}

	/**
	 * Returns the codes4 of the ElementCollectionParent.
	 * 
	 * @return the codes4 of the ElementCollectionParent
	 * 
	 * @since 2.0.0
	 */
	public Set<FooType> getCodes4() {
		return this.codes4;
	}

	/**
	 * Returns the codes5 of the ElementCollectionParent.
	 * 
	 * @return the codes5 of the ElementCollectionParent
	 * 
	 * @since 2.0.0
	 */
	public Map<String, String> getCodes5() {
		return this.codes5;
	}

	/**
	 * Returns the key.
	 * 
	 * @return the key
	 * @since 2.0.0
	 */
	public Integer getKey() {
		return this.key;
	}

	/**
	 * Returns the value.
	 * 
	 * @return the value
	 * @since 2.0.0
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value to set
	 * @since 2.0.0
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
