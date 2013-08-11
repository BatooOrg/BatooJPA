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

package org.batoo.jpa.core.impl.deployment;

import javax.persistence.metamodel.EntityType;

import org.batoo.common.BatooException;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.core.impl.model.EntityTypeImpl;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.mapping.AssociationMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.ElementCollectionMappingImpl;
import org.batoo.jpa.core.impl.model.mapping.PluralMappingEx;

/**
 * A Manager that performs the association linking operations.
 * 
 * @author hceylan
 * @since 2.0.0
 */
public class LinkManager extends DeploymentManager<EntityTypeImpl<?>> {

	/**
	 * The linking phase
	 * 
	 * @author hceylan
	 * @since 2.0.0
	 */
	public static enum Phase {

		/**
		 * The association linking phase.
		 */
		LINK_ASSOCIATIONS,

		/**
		 * The element collection phase.
		 */
		LINK_ELEMENT_COLLECTIONS,

		/**
		 * The dependency linking phase.
		 */
		LINK_DEPENDENCIES
	}

	private static final BLogger LOG = BLoggerFactory.getLogger(LinkManager.class);

	/**
	 * Performs the association linking operations.
	 * 
	 * @param metamodel
	 *            the metamodel
	 * @throws BatooException
	 *             thrown in case of an underlying exception
	 * 
	 * @since 2.0.0
	 */
	public static void perform(MetamodelImpl metamodel) throws BatooException {
		new LinkManager(metamodel, Phase.LINK_ASSOCIATIONS).perform();
		new LinkManager(metamodel, Phase.LINK_ELEMENT_COLLECTIONS).perform();
		new LinkManager(metamodel, Phase.LINK_DEPENDENCIES).perform();
	}

	private final Phase phase;

	private LinkManager(MetamodelImpl metamodel, Phase phase) {
		super(LinkManager.LOG, "Link Manager", metamodel, Context.ENTITIES);

		this.phase = phase;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Void perform(EntityTypeImpl<?> entity) throws BatooException {
		switch (this.phase) {
			case LINK_ASSOCIATIONS:
				for (final AssociationMappingImpl<?, ?, ?> mapping : entity.getAssociations()) {
					if (mapping.getRoot().getType() == entity) {
						mapping.link();
					}
				}
				break;
			case LINK_ELEMENT_COLLECTIONS:
				for (final PluralMappingEx<?, ?, ?> mapping : entity.getMappingsPlural()) {
					if (mapping.getRoot().getType() == entity) {
						if (mapping instanceof ElementCollectionMappingImpl) {
							((ElementCollectionMappingImpl<?, ?, ?>) mapping).link();
						}
					}
				}
				break;
			case LINK_DEPENDENCIES:
				for (final EntityType<?> type : this.getMetamodel().getEntities()) {
					entity.prepareDependenciesFor((EntityTypeImpl<?>) type);
				}
				break;
		}

		return null;
	}
}
