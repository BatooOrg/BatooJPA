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
package org.batoo.jpa.core.test.lob;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.google.common.collect.Sets;

/**
 * 
 * @author hceylan
 * @since $version
 */
@Entity
public class Foo {

	@Id
	@GeneratedValue
	private Integer key;

	@Lob
	private final HashSet<String> values = Sets.newHashSet();

	@Lob
	private String clob;

	private byte[] blob;

	/**
	 * Returns the blob.
	 * 
	 * @return the blob
	 * @since $version
	 */
	public byte[] getBlob() {
		return this.blob;
	}

	public String getClob() {
		return this.clob;
	}

	/**
	 * Returns the key.
	 * 
	 * @return the key
	 * @since $version
	 */
	public Integer getKey() {
		return this.key;
	}

	/**
	 * Returns the values.
	 * 
	 * @return the values
	 * @since $version
	 */
	public Set<String> getValues() {
		return this.values;
	}

	/**
	 * Sets the blob.
	 * 
	 * @param blob
	 *            the blob to set
	 * @since $version
	 */
	public void setBlob(byte[] blob) {
		this.blob = blob;
	}

	public void setClob(String clob) {
		this.clob = clob;
	}

}
