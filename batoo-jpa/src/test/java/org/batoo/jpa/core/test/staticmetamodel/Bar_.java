package org.batoo.jpa.core.test.staticmetamodel;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@SuppressWarnings("javadoc")
@StaticMetamodel(value = Bar.class)
public class Bar_ {
	public static volatile SingularAttribute<Bar, Long> id;
	public static volatile SingularAttribute<Bar, String> name;

	public static volatile SetAttribute<Bar, Foo> foos;

}
