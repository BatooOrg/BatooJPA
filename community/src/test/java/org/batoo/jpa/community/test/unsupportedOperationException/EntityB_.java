package org.batoo.jpa.community.test.unsupportedOperationException;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel(value = EntityB.class)
public class EntityB_  {
	public static volatile SingularAttribute<EntityB, Long> id;
	public static volatile SingularAttribute<EntityB, String> code;
	public static volatile SetAttribute<EntityB, EntityA> entitiesA;
}
