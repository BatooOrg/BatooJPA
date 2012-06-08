package javax.persistence;

/**
 * A class that implements this interface can be used to convert entity attribute state into database column representation and back again.
 * Note that the X and Y types may be the same Java type.
 * 
 * @param <X>
 *            the type of the entity attribute
 * @param <Y>
 *            the type of the database column
 */
public interface AttributeConverter<X, Y> {
	/**
	 * Converts the value stored in the entity attribute into the data representation to be stored in the database.
	 * 
	 * @param attribute
	 *            the entity attribute value to be converted
	 * @return the converted data to be stored in the database column
	 */
	public Y convertToDatabaseColumn(X attribute);

	/**
	 * Converts the data stored in the database column into the value to be stored in the entity attribute. Note that it is the
	 * responsibility of the converter writer to specify the correct dbData type for the corresponding column for use by the JDBC driver:
	 * i.e., persistence providers are not expected to do such type conversion.
	 * 
	 * @param dbData
	 *            the data from the database column to be converted
	 * @return the converted value to be stored in the entity attribute
	 */
	public X convertToEntityAttribute(Y dbData);
}
