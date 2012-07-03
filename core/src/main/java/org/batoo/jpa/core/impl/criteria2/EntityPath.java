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
package org.batoo.jpa.core.impl.criteria2;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.apache.commons.lang.mutable.MutableInt;
import org.batoo.jpa.core.impl.criteria.FetchImpl;
import org.batoo.jpa.core.impl.instance.EnhancedInstance;
import org.batoo.jpa.core.impl.instance.ManagedInstance;
import org.batoo.jpa.core.impl.jdbc.AbstractColumn;
import org.batoo.jpa.core.impl.jdbc.BasicColumn;
import org.batoo.jpa.core.impl.jdbc.DiscriminatorColumn;
import org.batoo.jpa.core.impl.jdbc.EntityTable;
import org.batoo.jpa.core.impl.jdbc.JoinColumn;
import org.batoo.jpa.core.impl.jdbc.PkColumn;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.mapping.AssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.BasicMapping;
import org.batoo.jpa.core.impl.model.mapping.EmbeddedMapping;
import org.batoo.jpa.core.impl.model.mapping.Mapping;
import org.batoo.jpa.core.impl.model.mapping.RootMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularAssociationMapping;
import org.batoo.jpa.core.impl.model.mapping.SingularMapping;
import org.batoo.jpa.core.impl.model.type.EmbeddableTypeImpl;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.util.Pair;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

/**
 * The path for entity types.
 * 
 * @param <X>
 *            the type referenced by the path
 * 
 * @author hceylan
 * @since $version
 */
public abstract class EntityPath<X> extends AbstractPath<X> {

	private final HashBiMap<String, AbstractColumn> idFields = HashBiMap.create();
	private final HashBiMap<String, AbstractColumn> fields = HashBiMap.create();
	private final HashBiMap<String, AbstractColumn> joinFields = HashBiMap.create();
	private final List<SingularAssociationMapping<?, ?>> joins = Lists.newArrayList();

	private String discriminatorAlias;
	private final EntityTypeImpl<X> entity;

	/**
	 * @param mapping
	 *            the mapping
	 * @param generatedAlias
	 *            the generated alias
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public EntityPath(RootMapping<X> mapping, String generatedAlias) {
		super(mapping, generatedAlias);

		this.entity = mapping.getType();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String generateSelectSql(CriteriaQueryImpl<?> query) {
		final List<String> selects = Lists.newArrayList();

		for (final EntityTable table : this.getModel().getAllTables()) {
			int fieldNo = 0;

			final List<String> fields = Lists.newArrayList();

			final String tableAlias = this.getAlias();

			final Collection<AbstractColumn> columns = table.getColumns();
			for (final AbstractColumn column : columns) {

				final String fieldAlias = tableAlias + "_F" + fieldNo++;

				final String field = Joiner.on(".").skipNulls().join(tableAlias, column.getName());
				if (column instanceof DiscriminatorColumn) {
					this.discriminatorAlias = fieldAlias;
				}
				else if (column instanceof PkColumn) {
					this.idFields.put(fieldAlias, column);
				}
				else if (column.getMapping() instanceof SingularAssociationMapping) {
					final SingularAssociationMapping<?, ?> mapping = (SingularAssociationMapping<?, ?>) column.getMapping();

					// if we are on a join on that path then ignore
					if (this.ignoreJoin(mapping)) {
						continue;
					}

					this.joins.add(mapping);
					this.joinFields.put(fieldAlias, column);
				}
				else {
					this.fields.put(fieldAlias, column);
				}

				fields.add(field + " AS " + fieldAlias);
			}

			selects.add(Joiner.on(", ").join(fields));
		}

		return Joiner.on(",\n\t\t").join(selects);
	}

	private Object getId(Map<String, Object> row) {
		if (this.entity.hasSingleIdAttribute()) {
			final SingularMapping<? super X, ?> idMapping = this.entity.getIdMapping();
			if (idMapping instanceof BasicMapping) {
				final BasicColumn column = ((BasicMapping<? super X, ?>) idMapping).getColumn();
				final String field = this.idFields.inverse().get(column);

				return row.get(field);
			}

			final MutableBoolean allNull = new MutableBoolean(true);
			final Object id = this.populateEmbeddedId(row, (EmbeddedMapping<? super X, ?>) idMapping, null, allNull);

			return allNull.booleanValue() ? null : id;
		}

		final Object id = ((EmbeddableTypeImpl<?>) this.entity.getIdType()).newInstance();
		for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> pair : this.entity.getIdMappings()) {
			final BasicColumn column = pair.getFirst().getColumn();
			final String field = this.idFields.inverse().get(column);

			pair.getSecond().set(id, row.get(field));
		}

		return id;
	}

	private Object getId(SingularAssociationMapping<?, ?> mapping, Map<String, Object> row) {
		final EntityTypeImpl<?> entity = mapping.getType();
		if (entity.hasSingleIdAttribute()) {
			final SingularMapping<?, ?> idMapping = entity.getIdMapping();
			if (idMapping instanceof BasicMapping) {
				final JoinColumn joinColumn = mapping.getForeignKey().getJoinColumns().get(0);
				final String field = this.joinFields.inverse().get(joinColumn);
				return row.get(field);
			}

			final MutableBoolean allNull = new MutableBoolean(true);
			final Object id = this.populateEmbeddedId(row, (EmbeddedMapping<?, ?>) idMapping, null, allNull);

			return allNull.booleanValue() ? null : id;
		}

		final Object id = ((EmbeddableTypeImpl<?>) entity.getIdType()).newInstance();
		for (final Pair<BasicMapping<? super X, ?>, BasicAttribute<?, ?>> pair : this.entity.getIdMappings()) {
			final BasicColumn column = pair.getFirst().getColumn();

			for (final JoinColumn joinColumn : mapping.getForeignKey().getJoinColumns()) {
				if (joinColumn.getReferencedColumnName().equals(column.getName())) {
					final String field = this.joinFields.inverse().get(joinColumn);
					pair.getSecond().set(id, row.get(field));

					break;
				}
			}
		}

		return id;
	}

	/**
	 * Returns the managed instance based on the id.
	 * 
	 * @param session
	 *            the session
	 * @param row
	 *            the data row
	 * @return the managed instance or null if the id is null
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedInstance<? extends X> getInstance(SessionImpl session, Map<String, Object> row) {
		// get the id of for the instance
		final Object id = this.getId(row);

		if (id == null) {
			return null;
		}

		// if inheritance is in place then locate the correct child type
		if (this.entity.getRootType().getInheritanceType() != null) {
			final Object discriminatorValue = row.get(this.discriminatorAlias);

			final EntityTypeImpl<? extends X> effectiveType = this.entity.getChildType(discriminatorValue);
			if (effectiveType != null) {
				return effectiveType.getManagedInstanceById(session, id);
			}
		}

		return this.entity.getManagedInstanceById(session, id);
	}

	/**
	 * Returns the associate either from the session or by creating a lazy instance.
	 * 
	 * @param session
	 *            the session
	 * @param mapping
	 *            the mapping
	 * @param row
	 *            the row data
	 * @return the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private Object getInstance(SessionImpl session, SingularAssociationMapping<?, ?> mapping, Map<String, Object> row) {
		final Object id = this.getId(mapping, row);
		if (id == null) {
			return null;
		}

		ManagedInstance<?> instance = mapping.getType().getManagedInstanceById(session, id);
		final ManagedInstance<?> managedInstance = session.get(instance);
		if (managedInstance != null) {
			return managedInstance.getInstance();
		}

		instance = mapping.getType().getManagedInstanceById(session, null, null, id, true);
		session.put(instance);

		return instance.getInstance();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public EntityTypeImpl<X> getModel() {
		return this.entity;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Collection<? extends X> handle(SessionImpl session, LinkedList<Map<String, Object>> data, MutableInt rowNo) {
		final List<ManagedInstance<? extends X>> instances = this.handleMulti(session, data, null, rowNo, 1);

		return Lists.transform(instances, new Function<ManagedInstance<? extends X>, X>() {

			@Override
			public X apply(ManagedInstance<? extends X> input) {
				return input.getInstance();
			}
		});
	}

	private ManagedInstance<? extends X> handleInstance(List<ManagedInstance<? extends X>> instances, SessionImpl session, List<Map<String, Object>> data,
		MutableInt rowNo, int leap, final Map<String, Object> row, ManagedInstance<? extends X> instance) {
		for (final Entry<String, AbstractColumn> entry : this.fields.entrySet()) {
			final String field = entry.getKey();
			final AbstractColumn column = entry.getValue();
			column.setValue(instance, row.get(field));
		}

		for (final SingularAssociationMapping<?, ?> mapping : this.joins) {
			final Object child = this.getInstance(session, mapping, row);
			mapping.set(instance, child);
		}

		if (instances != null) {
			instances.add(instance);
		}
		session.put(instance);

		for (int i = this.fetches.size() - 1; i >= 0; i--) {
			final FetchImpl<X, ?> fetch = this.fetches.get(i);
			final MutableInt subRowNo = new MutableInt(rowNo);
			final AssociationMapping<? super X, ?> mapping = fetch.getMapping();

			// if it is a plural association then get a list of instances
			if (mapping.getAttribute().isCollection()) {
				// resolve the children
				final List<?> children = fetch.handleMulti(session, data, instance, subRowNo, leap);

				// set the children
				mapping.set(instance, Lists.transform(children, new Function<Object, Object>() {

					@Override
					public Object apply(Object input) {
						return ((ManagedInstance<?>) input).getInstance();
					}
				}));

				// if it is a one-to-many mapping and has an inverse then set the inverses
				if ((mapping.getInverse() != null) && //
					(mapping.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_MANY)) {
					for (final Object child : children) {
						mapping.getInverse().set((ManagedInstance<?>) child, instance.getInstance());
					}
				}

				leap = subRowNo.intValue() - rowNo.intValue();
			}
			// if it is singular association then get a single instance
			else {
				final ManagedInstance<?> value = fetch.handleSingle(null, session, data, instance, subRowNo, leap);
				if (value == null) {
					mapping.set(instance, null);
				}
				else {
					mapping.set(instance, value.getInstance());

					// if this is a one-to-one mapping and has an inverse then set it
					if ((mapping.getInverse() != null) && (mapping.getAttribute().getPersistentAttributeType() == PersistentAttributeType.ONE_TO_ONE)) {
						mapping.getInverse().set(value, instance.getInstance());
					}

					leap = subRowNo.intValue() - rowNo.intValue();
				}
			}

		}

		rowNo.add(leap);

		return instance;
	}

	/**
	 * Handles the row for multiple results.
	 * 
	 * @param session
	 *            the session
	 * @param query
	 *            the query
	 * @param data
	 *            the resultset data
	 * @param parent
	 *            the parent instance
	 * @param rowNo
	 *            the current row no
	 * @param jumpSize
	 *            the jump size
	 * @return the list of instances
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected List<ManagedInstance<? extends X>> handleMulti(SessionImpl session, List<Map<String, Object>> data, ManagedInstance<?> parent, MutableInt rowNo,
		int jumpSize) {
		final List<ManagedInstance<? extends X>> instances = Lists.newArrayList();

		while ((rowNo.intValue() < data.size())) {
			final ManagedInstance<? extends X> instance = this.handleSingle(instances, session, data, parent, rowNo, jumpSize);
			if (instance == null) {
				break;
			}
		}

		return instances;
	}

	/**
	 * Handles the row for a single result.
	 * 
	 * @param instances
	 *            the instances already loaded
	 * @param session
	 *            the session
	 * @param query
	 *            the query
	 * @param data
	 *            the resultset data
	 * @param parent
	 *            the parent instance
	 * @param rowNo
	 *            the current row no
	 * @param jumpSize
	 *            the jump size
	 * @return the managed instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected ManagedInstance<? extends X> handleSingle(List<ManagedInstance<? extends X>> instances, SessionImpl session, List<Map<String, Object>> data,
		ManagedInstance<?> parent, MutableInt rowNo, int jumpSize) {
		final int leap = jumpSize;

		final Map<String, Object> row = data.get(rowNo.intValue());

		// if id is null then break
		ManagedInstance<? extends X> instance = this.getInstance(session, row);
		if (instance == null) {
			return null;
		}

		// if different parent then break
		if (!this.shouldContinue(session, parent, row)) {
			return null;
		}

		// if we processed the same instance then break
		if ((instances != null) && instances.contains(instance)) {
			return null;
		}

		final ManagedInstance<? extends X> managedInstance = session.get(instance);
		if (managedInstance != null) {
			if ((managedInstance.getInstance() instanceof EnhancedInstance)
				&& !((EnhancedInstance) managedInstance.getInstance()).__enhanced__$$__isInitialized()) {
				instance = managedInstance;
			}
			else {
				instances.add(managedInstance);
				rowNo.add(leap);

				return managedInstance;
			}
		}

		return this.handleInstance(instances, session, data, rowNo, leap, row, instance);
	}

	/**
	 * Returns if the join should be ignored
	 * 
	 * @param mapping
	 *            the mapping
	 * @return true if the join should be ignored, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private boolean ignoreJoin(SingularAssociationMapping<?, ?> mapping) {
		// if we are joined on this mapping then ignore
		if ((mapping.getInverse() != null) && (mapping.getInverse() == this.getMapping())) {
			return true;
		}

		// if we will join on that mapping then ignore
		for (final FetchImpl<X, ?> fetch : this.fetches) {
			if (fetch.getMapping() == mapping) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Populates the embedded id fields from the result set
	 * 
	 * @param row
	 *            the row
	 * @param idMapping
	 *            the embedded mapping
	 * @return the generated embedded id
	 * 
	 * @since $version
	 * @author hceylan
	 * @param mapping2
	 */
	private Object
		populateEmbeddedId(Map<String, Object> row, EmbeddedMapping<?, ?> idMapping, SingularAssociationMapping<?, ?> mapping, MutableBoolean allNull) {
		final Object id = idMapping.getAttribute().newInstance();

		for (final Mapping<?, ?> child : idMapping.getChildren()) {
			if (child instanceof BasicMapping) {
				final BasicMapping<?, ?> basicMapping = (BasicMapping<?, ?>) child;
				final BasicColumn column = basicMapping.getColumn();

				if (mapping == null) {
					final String field = this.idFields.inverse().get(column);
					final Object value = row.get(field);

					if (value != null) {
						allNull.setValue(false);
					}

					basicMapping.getAttribute().set(id, value);
				}
				else {
					for (final JoinColumn joinColumn : mapping.getForeignKey().getJoinColumns()) {
						if (joinColumn.getReferencedColumnName().equals(column.getName())) {
							final String field = this.joinFields.inverse().get(joinColumn);
							final Object value = row.get(field);

							if (value != null) {
								allNull.setValue(false);
							}

							basicMapping.getAttribute().set(id, value);
						}
					}
				}
			}
			else {
				child.getAttribute().set(id, this.populateEmbeddedId(row, idMapping, mapping, allNull));
			}
		}

		return id;
	}

	/**
	 * Returns whether the handling should continue.
	 * <p>
	 * For Fetch parents this is always true, where as Fetch checks if row still belongs to the parent
	 * 
	 * @param session
	 *            the session
	 * @param parent
	 *            the parent instance
	 * @param row
	 *            the data row
	 * @return true if the handling should continue, false otherwise
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected boolean shouldContinue(SessionImpl session, ManagedInstance<?> parent, Map<String, Object> row) {
		return true;
	}
}
