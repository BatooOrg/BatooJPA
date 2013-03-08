package org.batoo.jpa.core.test.staticmetamodel;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@SuppressWarnings("javadoc")
@StaticMetamodel(value = Foo.class)
public class Foo_ {
	public static volatile SingularAttribute<Foo, Long> id;
	public static volatile SingularAttribute<Foo, Long> timeStamp;
	public static volatile SingularAttribute<Foo, String> fooName;

	public static volatile SingularAttribute<Foo, Bar> bar;

}
