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
package org.batoo.jpa.core.impl.model;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;

/**
 * Sequence based generator.
 * 
 * @author hceylan
 * @since $version
 */
public class SequenceGenerator extends AbstractGenerator {

	private static final String DEFAULT_SEQUENCE_NAME = "BATOO_ID";

	private final String sequenceName;

	/**
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public SequenceGenerator(SequenceGeneratorMetadata metadata) {
		super(metadata);

		this.sequenceName = (metadata != null) && StringUtils.isNotBlank(metadata.getName()) ? metadata.getName() : SequenceGenerator.DEFAULT_SEQUENCE_NAME;
	}

	/**
	 * Returns the sequenceName of the sequence generator.
	 * 
	 * @return the sequenceName of the sequence generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getSequenceName() {
		return this.sequenceName;
	}
}
