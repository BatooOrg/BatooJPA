package org.batoo.jpa.community.test.unsupportedOperationException;

import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel(value = EntityA.class)
public class EntityA_ {
	public static volatile SingularAttribute<EntityA, Long> id;
	public static volatile SingularAttribute<EntityA, EntityB> entityB;
	public static volatile MapAttribute<EntityA, FieldLocale, String> name;
}
