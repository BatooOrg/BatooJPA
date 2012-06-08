package javax.persistence.spi;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * A persistence provider supplies an instance of this interface to the PersistenceUnitInfo.addTransformer method. The supplied transformer
 * instance will get called to transform entity class files when they are loaded or redefined. The transformation occurs before the class is
 * defined by the JVM.
 */
public interface ClassTransformer {
	/**
	 * Invoked when a class is being loaded or redefined. The implementation of this method may transform the supplied class file and return
	 * a new replacement class file.
	 * 
	 * @param loader
	 *            the defining loader of the class to be transformed, may be null if the bootstrap loader
	 * @param className
	 *            the name of the class in the internal form of fully qualified class and interface names
	 * @param classBeingRedefined
	 *            if this is a redefine, the class being redefined, otherwise null
	 * @param protectionDomain
	 *            the protection domain of the class being defined or redefined
	 * @param classfileBuffer
	 *            the input byte buffer in class file format - must not be modified
	 * @return a well-formed class file buffer (the result of the transform), or null if no transform is performed
	 * @throws IllegalClassFormatException
	 *             if the input does not represent a well-formed class file
	 */
	byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
		byte[] classfileBuffer) throws IllegalClassFormatException;
}
