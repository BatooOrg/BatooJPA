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
package org.batoo.jpa.core.impl.model.mapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import javax.persistence.OrderBy;
import javax.persistence.metamodel.Type.PersistenceType;

import org.apache.commons.lang.ObjectUtils;
import org.batoo.jpa.core.impl.model.attribute.BasicAttribute;
import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;
import org.batoo.jpa.core.util.Pair;
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
 * @since $version
 */
public class ListComparator<E> implements Comparator<E> {

	private class ComparableMapping {

		private final boolean ascending;
		private final Mapping<?, ?, ?> mapping;

		/**
		 * @param ascending
		 *            the order
		 * @param mapping
		 *            the mapping
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public ComparableMapping(boolean ascending, Mapping<?, ?, ?> mapping) {
			super();
			this.ascending = ascending;
			this.mapping = mapping;
		}

		/**
		 * Returns the mapping.
		 * 
		 * @return the mapping
		 * 
		 * @since $version
		 * @author hceylan
		 */
		protected Mapping<?, ?, ?> getMapping() {
			return this.mapping;
		}

		/**
		 * Returns if the order is ascending.
		 * 
		 * @return true if the order is ascending, false otherwise
		 * 
		 * @since $version
		 * @author hceylan
		 */
		public boolean isAscending() {
			return this.ascending;
		}
	}

	private final PluralMapping<?, ?, E> mapping;
	private final ArrayList<ComparableMapping> comparables = Lists.newArrayList();

	/**
	 * @param mapping
	 *            the owner mapping
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ListComparator(PluralMapping<?, ?, E> mapping) {
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
		for (final ComparableMapping mapping : this.comparables) {
			final Object v1 = mapping.getMapping().get(o1);
			final Object v2 = mapping.getMapping().get(o2);

			final int result = ObjectUtils.compare((Comparable<?>) v1, (Comparable<?>) v2);
			if (result == 0) {
				continue;
			}
			if (result < 0) {
				if (mapping.isAscending()) {
					return -1;
				}
				else {
					return 1;
				}
			}
		}

		return 0;
	}

	/**
	 * Creates the list of comparables
	 * 
	 * @since $version
	 * @author hceylan
	 */
	private void createComparables() {
		// order on id
		if (this.mapping.getOrderBy().trim().length() == 0) {
			if (this.mapping.isAssociation()) {
				final EntityTypeImpl<E> type = ((PluralAssociationMapping<?, ?, E>) this.mapping).getType();
				if (type.hasSingleIdAttribute()) {
					this.comparables.add(new ComparableMapping(true, (Mapping<?, ?, ?>) type.getIdMapping()));
				}
				else {
					for (final Pair<BasicMapping<? super E, ?>, BasicAttribute<?, ?>> pair : type.getIdMappings()) {
						this.comparables.add(new ComparableMapping(true, pair.getFirst()));
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

				final Mapping<?, ?, ?> mapping = this.mapping.getMapping(path);
				if (mapping == null) {
					throw new MappingException("Sort property cannot be found: " + path, this.mapping.getAttribute().getLocator());
				}

				this.comparables.add(new ComparableMapping(order, mapping));
			}
		}
	}
}
