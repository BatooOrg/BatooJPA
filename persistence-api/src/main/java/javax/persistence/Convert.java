package javax.persistence;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The <code>Convert</code> annotation is used to specify the conversion of a Basic field or property. It is not necessary to use the Basic
 * annotation or corresponding XML element to specify the basic type.
 * 
 * The <code>Convert</code> annotation should not be used to specify conversion of the following: Id attributes, version attributes,
 * relationship attributes, and attributes explicitly annotated (or designated via XML) as Enumerated or Temporal. Applications that specify
 * such conversions will not be portable.
 * <p>
 * The <code>Convert</code> annotation may be applied to a basic attribute or to an element collection of basic type (in which case the
 * converter is applied to the elements of the collection). In these cases, the attributeName element must not be specified.
 * <p>
 * The <code>Convert</code> annotation may be applied to an embedded attribute or to a map collection attribute whose key or value is of
 * embeddable type (in which case the converter is applied to the specified attribute of the embeddable instances contained in the
 * collection). In these cases, the attributeName element must be specified.
 * <p>
 * To override conversion mappings at multiple levels of embedding, a dot (".") notation form must be used in the attributeName element to
 * indicate an attribute within an embedded attribute. The value of each identifier used with the dot notation is the name of the respective
 * embedded field or property.
 * <p>
 * When the <code>Convert</code> annotation is applied to a map containing instances of embeddable classes, the attributeName element must
 * be specified, and "key." or "value." must be used to prefix the name of the attribute that is to be converted in order to specify it as
 * part of the map key or map value.
 * <p>
 * When the <code>Convert</code> annotation is applied to a map to specify conversion of a map key of basic type, "key" must be used as the
 * value of the attributeName element to specify that it is the map key that is to be converted.
 * <p>
 * The <code>Convert</code> annotation may be applied to an entity class that extends a mapped superclass to specify or override a
 * conversion mapping for an inherited basic or embedded attribute.
 * 
 * <pre>
 * 
 *     Example 1: Convert a basic attribute
 *  
 *     &#064;Converter
 *     public class BooleanToIntegerConverter
 *     implements AttributeConverter<Boolean, Integer> { ... }
 *  
 *     &#064;Entity
 *     public class Employee {
 *         &#064;Id long id;
 *     }
 *  
 *  
 *     Example 2: Auto-apply conversion of a basic attribute
 * 
 *     &#064;Converter(autoApply=true)
 *     public class EmployeeDateConverter
 *     implements AttributeConverter<com.acme.EmployeeDate, java.sql.Date> { ... }
 * 
 *     &#064;Entity
 *     public class Employee {
 *         &#064;Id long id;
 *         ...
 *         // EmployeeDateConverter is applied automatically
 *         EmployeeDate startDate;
 *     }
 *  
 *  
 *     Example 3: Disable conversion in the presence of an auto-apply converter
 * 
 *     &#064;Convert(disableConversion=true)
 *     EmployeeDate lastReview;
 *     
 *     
 *     Example 4: Apply a converter to an element collection of basic type
 *     
 *     &#064;ElementCollection
 *     // applies to each element in the collection
 *     &#064;Convert(NameConverter.class)
 *     List<String> names;
 *    
 *     
 *     Example 5: Apply a converter to an element collection that is a map of basic values. The converter is
 *     applied to the map value.
 *     
 *     &#064;ElementCollection
 *     &#064;Convert(EmployeeNameConverter.class)
 *     Map<String, String> responsibilities;
 *     
 *     
 *     Example 6: Apply a converter to a map key of basic type
 *     
 *     &#064;OneToMany
 *     &#064;Convert(converter=ResponsibilityCodeConverter.class, attributeName="key")
 *     Map<String, Employee> responsibilities;
 *     
 *     
 *     Example 7: Apply a converter to an embeddable attribute
 *     &#064;Embedded
 *     &#064;Convert(converter=CountryConverter.class, attributeName="country")
 *     Address address;
 *     
 *     
 *     Example 8: Apply a converter to a nested embeddable attribute
 *     &#064;Embedded
 *     &#064;Convert(converter=CityConverter.class, attributeName="region.city")
 *     Address address;
 *     
 *     
 *     Example 9: Apply a converter to a nested attribute of an embeddable that is a map key of an element
 *     collection
 *     &#064;Entity public class PropertyRecord {
 *         ...
 *         &#064;Convert(name="key.region.city", converter=CityConverter.class)
 *         &#064;ElementCollection
 *         Map<Address, PropertyInfo> parcels;
 *     }
 *     
 *     
 *     Example 10: Apply a converter to an embeddable that is a map key for a relationship
 *     
 *     &#064;OneToMany
 *     &#064;Convert(attributeName="key.jobType", converter=ResponsibilityTypeConverter.class)
 *     Map<Responsibility, Employee> responsibilities;
 *     
 *     
 *     Example 11: Override conversion mappings for attributes inherited from a mapped superclass
 *     
 *     &#064;Entity
 *     &#064;Converts({
 *     &#064;Convert(attributeName="startDate", converter=DateConverter.class),
 *     &#064;Convert(attributeName="endDate", converter=DateConverter.class)})
 *     public class FullTimeEmployee extends GenericEmployee { ... }
 * 
 * </pre>
 * 
 * @see Converts
 * 
 * @since Java Persistence 2.1
 */
@Target({ METHOD, FIELD, TYPE })
@Retention(RUNTIME)
public @interface Convert {

	/**
	 * (Optional) The name of the attribute to convert. Must be specified unless the <code>Convert</code> annotation is applied to an
	 * attribute of basic type or to an element collection of basic type. Must not be specified otherwise.
	 * 
	 */
	String attributeName() default "";

	/**
	 * (Optional) The converter to be applied.
	 * 
	 */
	Class<?> converter() default void.class;

	/**
	 * (Optional) Whether conversion of the attribute is to be disabled.
	 */
	boolean disableConversion() default false;
}
