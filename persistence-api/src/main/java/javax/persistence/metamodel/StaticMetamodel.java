package javax.persistence.metamodel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The StaticMetamodel annotation specifies that the class is a metamodel class that represents the entity, mapped superclass, or embeddable
 * class designated by the value element.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StaticMetamodel {

	/**
	 * Class being modeled by the annotated class.
	 */
	Class<?> value();
}
