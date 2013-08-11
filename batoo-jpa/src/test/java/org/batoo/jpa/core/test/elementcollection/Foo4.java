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

package org.batoo.jpa.core.test.elementcollection;

import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;

import com.google.common.collect.Maps;

/**
 * 
 * @author asimarslan
 * @since 2.0.1
 */
@Entity
public class Foo4 {

	@Id
	@GeneratedValue
	private Integer key;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "LOCALEE", nullable = false)
	@MapKeyEnumerated(EnumType.STRING)
	@Column(name = "TRANSLATIONN", nullable = false)
	@CollectionTable(name = "FOO4_NAME", joinColumns = @JoinColumn(name = "ID"))
	private final Map<FieldLocale, String> textMap = Maps.newHashMap();

	/**
	 * Returns the key of the Foo2.
	 * 
	 * @return the key of the Foo2
	 * 
	 * @since 2.0.0
	 */
	protected Integer getKey() {
		return this.key;
	}

	/**
	 * 
	 * @return the textMap
	 * 
	 * @since 2.0.1
	 */
	public Map<FieldLocale, String> getTextMap() {
		return this.textMap;
	}
}
