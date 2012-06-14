/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.batoo.jpa.core.impl.jdbc;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.AbstractMapping;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.util.Pair;
import org.batoo.jpa.parser.MappingException;
import org.batoo.jpa.parser.metadata.JoinColumnMetadata;
import org.batoo.jpa.parser.metadata.PrimaryKeyJoinColumnMetadata;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Foreign key definition.
 * 
 * @author hceylan
 * @since $version
 */
public class ForeignKey {

	private final List<JoinColumn> joinColumns = Lists.newArrayList();

	private AbstractTable table;
	private String tableName;

	/**
	 * Constructor to create a join foreign key
	 * 
	 * @param metadata
	 *            the metadata for join column
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ForeignKey(List<JoinColumnMetadata> metadata) {
		super();

		for (final JoinColumnMetadata columnMetadata : metadata) {
			this.joinColumns.add(new JoinColumn(columnMetadata));
		}
	}

	/**
	 * @param table
	 *            the secondary table
	 * @param entity
	 *            the entity
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ForeignKey(SecondaryTable table, EntityTypeImpl<?> entity, List<PrimaryKeyJoinColumnMetadata> metadata) {
		super();

		/**
		 * Here's how this works:
		 * 
		 * If there is single id mapping then it is either a single basic id or embedded id. <br />
		 * if it is basic id. If there are multiple id mappings they all are basic mappings.
		 * 
		 * For basic mappings, if there is metatada, then join columns are created with metadata, otherwise join columns are created only
		 * with the basic mapping.
		 * 
		 * In the case of embedded id's the child mappings of the embedded mappings are iterated recursively and the join columns are
		 * created the same way for simple basic mappings.
		 */

		// there is no metadata definition
		if ((metadata == null) || (metadata.size() == 0)) {

			// has single id
			if (entity.hasSingleIdAttribute()) {
				final SingularMapping<?, ?> idMapping = entity.getIdMapping();

				// single basic id attribute
				if (idMapping instanceof BasicMapping) {
					this.joinColumns.add(new JoinColumn(table, (BasicMapping<?, ?>) idMapping));
				}
				// embedded id
				else {
					this.createEmbeddedJoins(table, (EmbeddedMapping<?, ?>) idMapping, null);
				}
			}
			// has multiple id
			else {
				for (final Pair<?, BasicAttribute<?, ?>> pair : entity.getIdMappings()) {
					this.joinColumns.add(new JoinColumn(table, (BasicMapping<?, ?>) pair.getFirst()));
				}
			}
		}
		// there is metadata definition
		else {
			// has single id
			if (entity.hasSingleIdAttribute()) {
				final SingularMapping<?, ?> idMapping = entity.getIdMapping();

				// single basic id
				if (idMapping instanceof BasicMapping) {
					final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) idMapping;
					final PrimaryKeyJoinColumnMetadata columnMetadata = this.getColumnMetadata(metadata, basicMapping);

					this.joinColumns.add(new JoinColumn(columnMetadata, table, basicMapping));
				}
				// embedded id
				else {
					this.createEmbeddedJoins(table, (EmbeddedMapping<?, ?>) idMapping, metadata);
				}
			}
			// has multiple id
			else {
				for (final Pair<?, BasicAttribute<?, ?>> pair : entity.getIdMappings()) {
					final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) pair.getFirst();
					final PrimaryKeyJoinColumnMetadata columnMetadata = this.getColumnMetadata(metadata, basicMapping);

					this.joinColumns.add(new JoinColumn(columnMetadata, table, basicMapping));
				}
			}
		}

		this.table = table;
		this.table.addForeignKey(this);
	}

	/**
	 * Creates the join for destination foreign keys.
	 * 
	 * @param joinType
	 *            the type of the join
	 * @param parentAlias
	 *            the alias of the parent table
	 * @param alias
	 *            the alias of the table
	 * @return the join SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String createDestinationJoin(JoinType joinType, String parentAlias, String alias) {
		return this.createJoin(joinType, parentAlias, alias, this.joinColumns.get(0).getReferencedTable().getName(), false);
	}

	private void createEmbeddedJoins(SecondaryTable table, EmbeddedMapping<?, ?> mapping, List<PrimaryKeyJoinColumnMetadata> metadata) {
		for (final AbstractMapping<?, ?> child : mapping.getMappings()) {
			if (child instanceof BasicMapping) {
				final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) child;

				// if metadata present, we match it
				if ((metadata != null) && (metadata.size() > 0)) {
					final PrimaryKeyJoinColumnMetadata columnMetadata = this.getColumnMetadata(metadata, basicMapping);
					this.joinColumns.add(new JoinColumn(columnMetadata, table, basicMapping));
				}
				// otherwise we create with the defaults
				else {
					this.joinColumns.add(new JoinColumn(table, basicMapping));
				}
			}
			// recursively process embedded child
			else {
				this.createEmbeddedJoins(table, (EmbeddedMapping<?, ?>) child, metadata);
			}
		}
	}

	private String createJoin(JoinType joinType, String parentAlias, String alias, final String tableName, final boolean source) {
		final List<String> parts = Lists.newArrayList();

		for (final JoinColumn joinColumn : this.joinColumns) {
			final String leftColumnName = source ? joinColumn.getReferencedColumnName() : joinColumn.getName();
			final String rightColumnName = source ? joinColumn.getName() : joinColumn.getReferencedColumnName();

			final String part = parentAlias + "." + leftColumnName + " = " + alias + "." + rightColumnName;
			parts.add(part);
		}

		final String join = Joiner.on(" AND ").join(parts);

		// append the join part
		switch (joinType) {
			case INNER:
				return "\tINNER JOIN " + tableName + " AS " + alias + " ON " + join;
			case LEFT:
				return "\tLEFT JOIN " + tableName + " AS " + alias + " ON " + join;
			default:
				return "\tRIGHT JOIN " + tableName + " AS " + alias + " ON " + join;
		}
	}

	/**
	 * Creates the join for source foreign keys.
	 * 
	 * @param joinType
	 *            the type of the join
	 * @param parentAlias
	 *            the alias of the parent table
	 * @param alias
	 *            the alias of the table
	 * @return the join SQL fragment
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String createSourceJoin(JoinType joinType, String parentAlias, String alias) {
		return this.createJoin(joinType, parentAlias, alias, this.joinColumns.get(0).getTable().getName(), true);
	}

	private PrimaryKeyJoinColumnMetadata getColumnMetadata(List<PrimaryKeyJoinColumnMetadata> metadata, BasicMapping<?, ?> basicMapping) {
		final String columnName = basicMapping.getColumn().getMappingName();
		for (final PrimaryKeyJoinColumnMetadata columnMetadata : metadata) {
			if (columnName.equals(columnMetadata.getReferencedColumnName())) {
				return columnMetadata;
			}
		}

		throw new MappingException("Primary key field cannot be found for " + basicMapping.getAttribute().getJavaMember() + ".",
			basicMapping.getAttribute().getLocator());
	}

	/**
	 * Returns the list of join columns of the foreign key.
	 * 
	 * @return the list of join columns of the foreign key.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public List<JoinColumn> getJoinColumns() {
		return this.joinColumns;
	}

	/**
	 * Returns the referenced table of the foreign key.
	 * 
	 * @return the referenced table of the foreign key
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getReferencedTableName() {
		return this.joinColumns.get(0).getReferencedTable().getName();
	}

	/**
	 * Returns the table of the ForeignKey.
	 * 
	 * @return the table of the ForeignKey
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractTable getTable() {
		return this.table;
	}

	/**
	 * Links the foreign key.
	 * 
	 * @param mapping
	 *            the owner attribute
	 * @param targetEntity
	 *            the target entity
	 * 
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void link(AssociationMapping<?, ?, ?> mapping, EntityTypeImpl<?> targetEntity) {
		// single primary key
		if (targetEntity.hasSingleIdAttribute()) {
			final SingularMapping<?, ?> idMapping = targetEntity.getIdMapping();

			// simple basic id
			if (idMapping instanceof BasicMapping) {
				this.linkJoinColumn(mapping, (BasicMapping<?, ?>) idMapping);
			}
			// embedded id
			else {
				this.linkEmbeddedJoins(mapping, (EmbeddedMapping<?, ?>) idMapping);
			}
		}
		// multiple id
		else {
			for (final Pair<?, BasicAttribute<?, ?>> pair : targetEntity.getIdMappings()) {
				this.linkJoinColumn(mapping, (BasicMapping<?, ?>) pair.getFirst());
			}
		}

		if (mapping != null) {
			final AbstractTable table = ((EntityTypeImpl<?>) mapping.getEntity()).getTable(this.tableName);
			if (table == null) {
				throw new MappingException("Table " + this.tableName + " could not be found");
			}

			this.setTable(table);
		}
	}

	private void linkEmbeddedJoins(AssociationMapping<?, ?, ?> mapping, EmbeddedMapping<?, ?> embeddedMapping) {
		for (final AbstractMapping<?, ?> child : embeddedMapping.getMappings()) {
			if (child instanceof BasicMapping) {
				this.linkJoinColumn(mapping, (BasicMapping<?, ?>) child);
			}
			// recursively process embedded child
			else {
				this.linkEmbeddedJoins(mapping, (EmbeddedMapping<?, ?>) child);
			}
		}
	}

	private void linkJoinColumn(AssociationMapping<?, ?, ?> mapping, final BasicMapping<?, ?> idMapping) {
		// no definition for the join columns
		if (this.joinColumns.size() == 0) {
			// create the join column
			this.joinColumns.add(new JoinColumn(mapping, idMapping));
		}
		// existing definition for the join column
		else {
			// locate the corresponding join column
			JoinColumn joinColumn = null;
			for (final JoinColumn column : this.joinColumns) {
				if (idMapping.getColumn().getName().equals(column.getReferencedColumnName())) {
					joinColumn = column;
					break;
				}
			}

			// if cannot be found then throw
			if (joinColumn == null) {
				throw new MappingException("Join column cannot be located in a composite key target entity");
			}

			// link the join column
			joinColumn.setColumnProperties(mapping, idMapping);
		}
	}

	/**
	 * Sets the table of the foreign key.
	 * 
	 * @param table
	 *            the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void setTable(AbstractTable table) {
		this.table = table;

		for (final JoinColumn joinColumn : this.joinColumns) {
			joinColumn.setTable(table);
		}

		this.table.addForeignKey(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "ForeignKey [tableName=" + this.table.getName() + ", joinColumns=" + this.joinColumns + "]";
	}
}
