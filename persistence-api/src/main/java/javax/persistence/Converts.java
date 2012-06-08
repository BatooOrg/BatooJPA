package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines conversions for embedded attributes.
 * 
 * <pre>
 * 
 *    Example:
 *    &#064;Embedded
 *    &#064;Converts({
 *        &#064;Convert(converter=CountryConverter.class,attributeName="country"),
 *        &#064;Convert(converter=CityConverter.class, attributeName="region.city")})
 *    })
 *    public Address getAddress() { return address; }
 * </pre>
 * 
 * @see Convert
 * 
 * @since Java Persistence 2.1
 */
@Target({ METHOD, FIELD, TYPE })
@Retention(RUNTIME)
public @interface Converts {

	/**
	 * (Required) The Convert mappings that are to be applied to the entity or the field or property.
	 * 
	 */
	Convert[] value();
}
