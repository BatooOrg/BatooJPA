package javax.persistence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines a class whose instances are stored as an intrinsic part of an owning entity and share the identity of the entity. Each of the
 * persistent properties or fields of the embedded object is mapped to the database table for the entity.
 * 
 * <p>
 * Note that the {@link Transient} annotation may be used to designate the non-persistent state of an embeddable class.
 * 
 * <pre>
 * 
 *    Example 1:
 * 
 *    &#064;Embeddable public class EmploymentPeriod {
 *       &#064;Temporal(DATE) java.util.Date startDate;
 *       &#064;Temporal(DATE) java.util.Date endDate;
 *      ...
 *    }
 * 
 *    Example 2:
 * 
 *    &#064;Embeddable public class PhoneNumber {
 *        protected String areaCode;
 *        protected String localNumber;
 *        &#064;ManyToOne PhoneServiceProvider provider;
 *        ...
 *     }
 * 
 *    &#064;Entity public class PhoneServiceProvider {
 *        &#064;Id protected String name;
 *         ...
 *     }
 * 
 *    Example 3:
 * 
 *    &#064;Embeddable public class Address {
 *       protected String street;
 *       protected String city;
 *       protected String state;
 *       &#064;Embedded protected Zipcode zipcode;
 *    }
 * 
 *    &#064;Embeddable public class Zipcode {
 *       protected String zip;
 *       protected String plusFour;
 *     }
 * 
 * 
 * </pre>
 * 
 * @since Java Persistence 1.0
 */
@Documented
@Target({ TYPE })
@Retention(RUNTIME)
public @interface Embeddable {}
