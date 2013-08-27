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

package org.batoo.jpa.core.impl.model.mapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import javax.persistence.OrderBy;
import javax.persistence.metamodel.Type.PersistenceType;

import org.apache.commons.lang.ObjectUtils;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.common.reflect.AbstractAccessor;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.common.util.Pair;
import org.batoo.jpa.jdbc.mapping.SingularMapping;
import org.batoo.jpa.parser.MappingException;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * Comparator for the list attributes that are annotated with {@link OrderBy} annotation.
 * 
 * @param <E>
 *            the type of the element
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class ListComparator<E> implements Comparator<E> {

	private class ComparableMapping {

		private final boolean ascending;
		private final AbstractMapping<?, ?, ?> abstractMapping;

		/**
		 * @param ascending
		 *            the order
		 * @param mapping
		 *            the mapping
		 * 
		 * @since 2.0.0
		 */
		public ComparableMapping(boolean ascending, AbstractMapping<?, ?, ?> mapping) {
			super();
			this.ascending = ascending;
			this.abstractMapping = mapping;
		}

		/**
		 * Returns the abstractMapping.
		 * 
		 * @return the abstractMapping
		 * 
		 * @since 2.0.0
		 */
		protected AbstractMapping<?, ?, ?> getMapping() {
			return this.abstractMapping;
		}

		/**
		 * Returns if the order is ascending.
		 * 
		 * @return true if the order is ascending, false otherwise
		 * 
		 * @since 2.0.0
		 */
		public boolean isAscending() {
			return this.ascending;
		}
	}

	private static final BLogger LOG = BLoggerFactory.getLogger(ListComparator.class);

	private final PluralMappingEx<?, ?, E> mapping;
	private final ArrayList<ComparableMapping> comparables = Lists.newArrayList();

	/**
	 * @param mapping
	 *            the owner mapping
	 * 
	 * @since 2.0.0
	 */
	public ListComparator(PluralMappingEx<?, ?, E> mapping) {
		super();

		this.mapping = mapping;
		this.createComparables();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int compare(E o1, E o2) {
		int result = 0;

		try {
			for (int i = 0; i < this.comparables.size(); i++) {
				final ComparableMapping mapping = this.comparables.get(i);
				final Object v1 = mapping.getMapping().get(o1);
				final Object v2 = mapping.getMapping().get(o2);

				result = ObjectUtils.compare((Comparable<?>) v1, (Comparable<?>) v2);
				if (result == 0) {
					continue;
				}

				if (!mapping.isAscending()) {
					result = -result;
				}

				break;
			}

			return result;
		}
		finally {
			if (ListComparator.LOG.isDebugEnabled()) {
				ListComparator.LOG.debug("{0} {1} {2}", result < 0 ? o1 : o2, result == 0 ? "=" : "<", result < 0 ? o2 : o1);
			}
		}
	}

	private void createComparable(SingularMapping<?, ?> idMapping) {
		if (idMapping instanceof BasicMappingImpl) {
			this.comparables.add(new ComparableMapping(true, (AbstractMapping<?, ?, ?>) idMapping));
		}
		else if (idMapping instanceof SingularAssociationMappingImpl) {

		}
	}

	/**
	 * Creates the list of comparables
	 * 
	 * @since 2.0.0
	 */
	private void createComparables() {
		// order on id
		if (this.mapping.getOrderBy().trim().length() == 0) {
			if (this.mapping.isAssociation()) {
				final EntityTypeImpl<E> type = ((PluralAssociationMappingImpl<?, ?, E>) this.mapping).getType();
				if (type.hasSingleIdAttribute()) {
					this.createComparable(type.getIdMapping());
				}
				else {
					for (final Pair<SingularMapping<?, ?>, AbstractAccessor> pair : type.getIdMappings()) {
						this.createComparable(pair.getFirst());
					}
				}
			}
			else if (this.mapping.getType().getPersistenceType() == PersistenceType.EMBEDDABLE) {
				throw new MappingException("Embeddable element collections requires OrderBy value", this.mapping.getAttribute().getLocator());
			}
			else {
				this.comparables.add(new ComparableMapping(true, null));
			}
		}
		else {
			final Iterator<String> i = Splitter.on(",").trimResults().split(this.mapping.getOrderBy()).iterator();
			while (i.hasNext()) {
				final Iterator<String> j = Splitter.on(" ").trimResults().split(i.next()).iterator();

				int index = 0;
				String path = null;
				boolean order = true;
				while (j.hasNext()) {
					if (index == 0) {
						path = j.next();
					}
					else if (index == 1) {
						order = "ASC".equals(j.next().toUpperCase());
					}
					else {
						throw new MappingException("Invalid order by statement: " + this.mapping.getOrderBy() + ".", this.mapping.getAttribute().getLocator());
					}

					index++;
				}

				if (this.mapping.getType().getPersistenceType() == PersistenceType.BASIC) {
					throw new MappingException("Basic element collection must not have OrderBy value", this.mapping.getAttribute().getLocator());
				}

				final AbstractMapping<?, ?, ?> mapping = this.mapping.getMapping(path);
				if (mapping == null) {
					throw new MappingException("Sort property cannot be found: " + path, this.mapping.getAttribute().getLocator());
				}

				this.comparables.add(new ComparableMapping(order, mapping));
			}
		}
	}
}
