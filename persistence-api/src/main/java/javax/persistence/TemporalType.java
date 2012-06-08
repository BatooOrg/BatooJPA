package javax.persistence;

/**
 * Type used to indicate a specific mapping of <code>java.util.Date</code> or <code>java.util.Calendar</code>.
 * 
 * @since Java Persistence 1.0
 */
public enum TemporalType {

	/**
	 * Map as <code>java.sql.Date</code>
	 */
	DATE,

	/**
	 * Map as <code>java.sql.Time</code>
	 */
	TIME,

	/**
	 * Map as <code>java.sql.Timestamp</code>
	 */
	TIMESTAMP
}
