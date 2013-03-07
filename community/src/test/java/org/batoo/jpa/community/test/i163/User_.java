package org.batoo.jpa.community.test.i163;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(value = User.class)
public class User_ {
	public static volatile SingularAttribute<User, Long> id;
	public static volatile SingularAttribute<User, Long> timeStamp;
	public static volatile SingularAttribute<User, String> login;
}
