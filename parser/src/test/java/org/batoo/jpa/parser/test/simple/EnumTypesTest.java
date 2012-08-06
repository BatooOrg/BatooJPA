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
package org.batoo.jpa.parser.test.simple;

import javax.persistence.AccessType;
import javax.persistence.TemporalType;

import org.batoo.jpa.parser.metadata.attribute.BasicAttributeMetadata;
import org.batoo.jpa.parser.test.BaseParserTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for enum types.
 * 
 * @author hceylan
 * @since $version
 */
public class EnumTypesTest extends BaseParserTest {

	/**
	 * Tests that the access type is consumed properly.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	@Ignore
	public void testAccessType() {
		Assert.assertEquals(AccessType.PROPERTY, this.e().getAccessType());
	}

	/**
	 * Tests that the temporal type is consumed properly.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	@Ignore
	public void testTemporalType() {
		Assert.assertEquals(TemporalType.TIMESTAMP, ((BasicAttributeMetadata) this.a()).getTemporalType());
	}
}
