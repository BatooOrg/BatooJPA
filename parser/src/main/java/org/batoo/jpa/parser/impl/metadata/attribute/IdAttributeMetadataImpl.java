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
package org.batoo.jpa.parser.impl.metadata.attribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;

import org.batoo.common.reflect.ReflectHelper;
import org.batoo.jpa.parser.impl.metadata.GeneratedValueMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.SequenceGeneratorMetadataImpl;
import org.batoo.jpa.parser.impl.metadata.TableGeneratorMetadataImpl;
import org.batoo.jpa.parser.metadata.GeneratedValueMetadata;
import org.batoo.jpa.parser.metadata.SequenceGeneratorMetadata;
import org.batoo.jpa.parser.metadata.TableGeneratorMetadata;
import org.batoo.jpa.parser.metadata.attribute.IdAttributeMetadata;

/**
 * The implementation of the {@link IdAttributeMetadata}.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class IdAttributeMetadataImpl extends PhysicalAttributeMetadataImpl implements IdAttributeMetadata {

	private final GeneratedValueMetadata generatedValue;
	private final SequenceGeneratorMetadata sequenceGenerator;
	private final TableGeneratorMetadata tableGenerator;

	/**
	 * @param member
	 *            the java member of basic attribute
	 * @param metadata
	 *            the metadata definition of the basic attribute
	 * 
	 * @since 2.0.0
	 */
	public IdAttributeMetadataImpl(Member member, IdAttributeMetadata metadata) {
		super(member, metadata);

		this.generatedValue = metadata.getGeneratedValue();
		this.sequenceGenerator = metadata.getSequenceGenerator();
		this.tableGenerator = metadata.getTableGenerator();
	}

	/**
	 * @param member
	 *            the java member of basic attribute
	 * @param name
	 *            the name of the basic attribute
	 * @param parsed
	 *            set of annotations parsed
	 * 
	 * @since 2.0.0
	 */
	public IdAttributeMetadataImpl(Member member, String name, Set<Class<? extends Annotation>> parsed) {
		super(member, name, parsed);

		final TableGenerator tableGenerator = ReflectHelper.getAnnotation(member, TableGenerator.class);
		final SequenceGenerator sequenceGenerator = ReflectHelper.getAnnotation(member, SequenceGenerator.class);
		final GeneratedValue generatedValue = ReflectHelper.getAnnotation(member, GeneratedValue.class);

		parsed.add(Id.class);
		parsed.add(TableGenerator.class);
		parsed.add(SequenceGenerator.class);
		parsed.add(GeneratedValue.class);

		this.generatedValue = generatedValue != null ? new GeneratedValueMetadataImpl(this.getLocator(), generatedValue) : null;
		this.tableGenerator = tableGenerator != null ? new TableGeneratorMetadataImpl(this.getLocator(), tableGenerator) : null;
		this.sequenceGenerator = sequenceGenerator != null ? new SequenceGeneratorMetadataImpl(this.getLocator(), sequenceGenerator) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public GeneratedValueMetadata getGeneratedValue() {
		return this.generatedValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public SequenceGeneratorMetadata getSequenceGenerator() {
		return this.sequenceGenerator;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public TableGeneratorMetadata getTableGenerator() {
		return this.tableGenerator;
	}
}
