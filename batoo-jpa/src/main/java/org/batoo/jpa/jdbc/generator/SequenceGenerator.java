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
package org.batoo.jpa.jdbc.generator;

import org.apache.commons.lang.StringUtils;
import org.batoo.jpa.jdbc.AbstractGenerator;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;

import com.google.common.base.Joiner;

/**
 * Sequence based generator.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class SequenceGenerator extends AbstractGenerator {

	private static final String DEFAULT_SEQUENCE_NAME = "BATOO_SEQ";

	private final String sequenceName;

	/**
	 * @param metadata
	 *            the metadata
	 * 
	 * @since 2.0.0
	 */
	public SequenceGenerator(SequenceGeneratorMetadata metadata) {
		super(metadata);

		this.sequenceName = (metadata != null) && StringUtils.isNotBlank(metadata.getName()) ? metadata.getName() : SequenceGenerator.DEFAULT_SEQUENCE_NAME;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String getQName() {
		return Joiner.on(".").skipNulls().join(this.getSchema(), this.sequenceName);
	}

	/**
	 * Returns the sequenceName of the sequence generator.
	 * 
	 * @return the sequenceName of the sequence generator
	 * 
	 * @since 2.0.0
	 */
	public String getSequenceName() {
		return this.sequenceName;
	}
}
