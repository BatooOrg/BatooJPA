package org.batoo.jpa.core.impl.instance;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.metamodel.EntityType;

import org.batoo.jpa.core.impl.manager.EntityManagerImpl;
import org.batoo.jpa.core.impl.manager.SessionImpl;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * The helper class to enhance a persistent class.
 * 
 * @author hceylan
 * @since $version
 */
public final class Enhancer {

	private static final Set<String> IGNORED_METHODS = Sets.newHashSet();

	static {
		for (final Method method : Object.class.getMethods()) {
			Enhancer.IGNORED_METHODS.add(method.getName());
		}
	}

	/**
	 * The enhanced suffix for the class
	 */
	public static final String SUFFIX_ENHANCED = "$Enhanced";

	private static final String THIS = "this";

	private static final String CLASS_ENHANCED_SUFFIX = Enhancer.SUFFIX_ENHANCED;

	private static final String CONSTRUCTOR_INIT = "<init>";

	private static final String FIELD_SERIAL_VERSION_UID = "serialVersionUID";
	private static final String FIELD_ENHANCED_INITIALIZED = "__enhanced_$$__initialized";
	private static final String FIELD_ENHANCED_SESSION = "__enhanced_$$__session";
	private static final String FIELD_ENHANCED_TYPE = "__enhanced_$$__type";
	private static final String FIELD_ENHANCED_ID = "__enhanced_$$__id";
	private static final String FIELD_ENHANCED_MANAGED_INSTANCE = "__enhanced_$$__managedInstance";

	private static final String METHOD_ENHANCED_IS_INITIALIZED = "__enhanced__$$__isInitialized";
	private static final String METHOD_ENHANCED_SET_INITIALIZED = "__enhanced__$$__setInitialized";
	private static final String METHOD_ENHANCED_CHECK = "__enhanced_$$__check";
	private static final String METHOD_GET_ENTITY_MANAGER = "getEntityManager";
	private static final String METHOD_ENHANCED_GET_MANAGED_INSTANCE = "__enhanced__$$__getManagedInstance";
	private static final String METHOD_ENHANCED_SET_MANAGED_INSTANCE = "__enhanced__$$__setManagedInstance";
	private static final String METHOD_FIND = "find";
	private static final String METHOD_CHANGED = "changed";

	private static final String DESCRIPTOR_BOOLEAN = Type.getDescriptor(Boolean.TYPE);
	private static final String DESCRIPTOR_MANAGED_INSTANCE = Type.getDescriptor(ManagedInstance.class);
	private static final String DESCRIPTOR_OBJECT = Type.getDescriptor(Object.class);
	private static final String DESCRIPTOR_SESSION = Type.getDescriptor(SessionImpl.class);
	private static final String DESCRIPTOR_CLASS = Type.getDescriptor(Class.class);

	private static final String INTERNAL_PERSISTENCE_EXCEPTION = Type.getInternalName(PersistenceException.class);
	private static final String INTERNAL_SESSION = Type.getInternalName(SessionImpl.class);
	private static final String INTERNAL_ENTITY_MANAGER = Type.getInternalName(EntityManagerImpl.class);
	private static final String INTERNAL_MANAGED_INSTANCE = Type.getInternalName(ManagedInstance.class);

	/**
	 * Returns the enhanced class bytecode.
	 * 
	 * @param clazz
	 *            the class to enhance
	 * @return the enhanced class
	 * @throws Exception
	 *             thrown in case of an error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	//@formatter:off
	public static byte[] create(Class<?> clazz) throws Exception {

		final String enhancingClassName = Type.getInternalName(clazz);
		final String enhancedClassName = enhancingClassName + Enhancer.SUFFIX_ENHANCED;
		final String descEnhancer = Enhancer.makeClassDesc(enhancedClassName);

		final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, enhancedClassName, null, enhancingClassName,
			new String[] { Type.getInternalName(EnhancedInstance.class) });

		// Field: serialVersionUID
		cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, Enhancer.FIELD_SERIAL_VERSION_UID,
			Type.getDescriptor(Long.TYPE), null, Long.valueOf(1L)).visitEnd();

		// Container fields
		cw.visitField(Opcodes.ACC_PRIVATE, Enhancer.FIELD_ENHANCED_INITIALIZED, Enhancer.DESCRIPTOR_BOOLEAN, null, null).visitEnd();
		cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_TRANSIENT, Enhancer.FIELD_ENHANCED_ID, Enhancer.DESCRIPTOR_OBJECT, null, null).visitEnd();
		cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_TRANSIENT, Enhancer.FIELD_ENHANCED_TYPE, Enhancer.DESCRIPTOR_CLASS, null, null).visitEnd();
		cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_TRANSIENT, Enhancer.FIELD_ENHANCED_SESSION, Enhancer.DESCRIPTOR_SESSION, null, null).visitEnd();
		cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_TRANSIENT, Enhancer.FIELD_ENHANCED_MANAGED_INSTANCE, Enhancer.DESCRIPTOR_MANAGED_INSTANCE, null, null).visitEnd();

		// Constructors
		Enhancer.createNoArgConstructor(enhancingClassName, enhancedClassName, descEnhancer, cw);
		Enhancer.createContainerConstructor(enhancingClassName, enhancedClassName, descEnhancer, cw);
		Enhancer.createMethodIsInitialized(enhancedClassName, descEnhancer, cw);
		Enhancer.createMethodSetInitialized(enhancedClassName, descEnhancer, cw);
		Enhancer.createMethodCheck(enhancedClassName, descEnhancer, cw);
		Enhancer.createMethodGetManagedInstance(enhancedClassName, descEnhancer, cw);
		Enhancer.createMethodSetManagedInstance(enhancedClassName, descEnhancer, cw);

		final Map<String, Method> methods = Maps.newHashMap();

		Class<?> currentClass = clazz;
		while (currentClass != Object.class) { // we are not interested in Object.class
			for (final Method method : currentClass.getDeclaredMethods()) {
				int modifiers = method.getModifiers();

				// Filter out the details that we are not interested
				modifiers &= Modifier.ABSTRACT;
				modifiers &= Modifier.FINAL;
				modifiers &= Modifier.NATIVE;
				modifiers &= Modifier.PRIVATE;
				modifiers &= Modifier.PROTECTED;
				modifiers &= Modifier.STATIC;
				modifiers &= Modifier.STRICT;

				if ((modifiers == Modifier.PUBLIC) || (modifiers == 0)) {
					// we are not interested in the return type to omit the overridden methods
					final String desc = method.getName() + Enhancer.makeDescription(Void.TYPE, method.getParameterTypes());

					if (methods.get(desc) == null) {
						methods.put(desc, method);
					}
				}
			}

			currentClass = currentClass.getSuperclass();
		}

		for (final Method method : methods.values()) {
			if (!Enhancer.IGNORED_METHODS.contains(method.getName())) {
				Enhancer.createOverrriddenMethod(enhancingClassName, enhancedClassName, descEnhancer, cw, method);
			}
		}

		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void createContainerConstructor(final String enhancingClassName, final String enhancedClassName,
		final String descEnhancer, final ClassWriter cw) {
		final MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, Enhancer.CONSTRUCTOR_INIT, Enhancer.makeDescription(Void.TYPE, Class.class, SessionImpl.class, Object.class, Boolean.TYPE), null, null);
		mv.visitCode();

		final Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, enhancingClassName, Enhancer.CONSTRUCTOR_INIT, Enhancer.makeDescription(Void.TYPE));

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_TYPE, Enhancer.DESCRIPTOR_CLASS);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitFieldInsn(Opcodes.PUTFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_SESSION, Enhancer.DESCRIPTOR_SESSION);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 3);
		mv.visitFieldInsn(Opcodes.PUTFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_ID, Enhancer.DESCRIPTOR_OBJECT);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ILOAD, 4);
		mv.visitFieldInsn(Opcodes.PUTFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_INITIALIZED, Enhancer.DESCRIPTOR_BOOLEAN);

		mv.visitInsn(Opcodes.RETURN);

		final Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable(Enhancer.THIS, descEnhancer, null, l0, l1, 0);
		mv.visitLocalVariable("type", Enhancer.DESCRIPTOR_CLASS, null, l0, l1, 1);
		mv.visitLocalVariable("session", Enhancer.DESCRIPTOR_SESSION, null, l0, l1, 2);
		mv.visitLocalVariable("id", Enhancer.DESCRIPTOR_OBJECT, null, l0, l1, 3);
		mv.visitLocalVariable("initialized", Enhancer.DESCRIPTOR_BOOLEAN, null, l0, l1, 4);
		mv.visitMaxs(0, 0);

		mv.visitEnd();
	}


	private static void createMethodCheck(final String enhancedClassName, final String descEnhancer, final ClassWriter cw) {
		final MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PRIVATE, Enhancer.METHOD_ENHANCED_CHECK, Enhancer.makeDescription(Void.TYPE), null, null);
		mv.visitCode();

		final Label lCheckInitialized = new Label();
		final Label lReturn = new Label();
		new Label();
		final Label lFind = new Label();
		final Label lInitialized = new Label();
		final Label lChanged = new Label();
		final Label lOut = new Label();

		// if (!this.__enhanced__$$__initialized) {
		mv.visitLabel(lCheckInitialized);
		mv.visitFrame(Opcodes.F_NEW, 1, new Object[] { enhancedClassName }, 0, new Object[] {});
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_INITIALIZED, Enhancer.DESCRIPTOR_BOOLEAN);
		mv.visitJumpInsn(Opcodes.IFNE, lChanged);

		//     if (this.__enhanced_$$__session == null)
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_SESSION, Enhancer.DESCRIPTOR_SESSION);
		mv.visitJumpInsn(Opcodes.IFNONNULL, lFind);

		//         throw new PersistenceException("No session to initialize the instance");
		mv.visitTypeInsn(Opcodes.NEW, Enhancer.INTERNAL_PERSISTENCE_EXCEPTION);
		mv.visitInsn(Opcodes.DUP);
		mv.visitLdcInsn("No session to initialize the instance");
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, Enhancer.INTERNAL_PERSISTENCE_EXCEPTION, Enhancer.CONSTRUCTOR_INIT, Enhancer.makeDescription(Void.TYPE, String.class));
		mv.visitInsn(Opcodes.ATHROW);

		//	    this.__enhanced_$$__session.getEntityManager().find(this.__enhanced_$$__type, this.__enhanced__$$__id);
		mv.visitLabel(lFind);
		mv.visitFrame(Opcodes.F_NEW, 1, new Object[] { enhancedClassName }, 0, new Object[] {});
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_SESSION, Enhancer.DESCRIPTOR_SESSION);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Enhancer.INTERNAL_SESSION, Enhancer.METHOD_GET_ENTITY_MANAGER, Enhancer.makeDescription(EntityManagerImpl.class));
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_TYPE, Enhancer.DESCRIPTOR_CLASS);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_ID, Enhancer.DESCRIPTOR_OBJECT);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Enhancer.INTERNAL_ENTITY_MANAGER, Enhancer.METHOD_FIND, Enhancer.makeDescription(Object.class, Class.class, Object.class));
		mv.visitInsn(Opcodes.POP);

		//	this.__enhanced__$$__initialized = true;
		mv.visitLabel(lInitialized);
		mv.visitFrame(Opcodes.F_NEW, 1, new Object[] { enhancedClassName }, 0, new Object[] {});
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitInsn(Opcodes.ICONST_1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_INITIALIZED, Enhancer.DESCRIPTOR_BOOLEAN);

		// if (this.__enhanced_$$__session != null)
		mv.visitLabel(lChanged);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_SESSION, Enhancer.DESCRIPTOR_SESSION);
		mv.visitJumpInsn(Opcodes.IFNULL, lReturn);

		//     this.__enhanced__$$__managedInstance.changed();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_MANAGED_INSTANCE, Enhancer.DESCRIPTOR_MANAGED_INSTANCE);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Enhancer.INTERNAL_MANAGED_INSTANCE, Enhancer.METHOD_CHANGED, Enhancer.makeDescription(Void.TYPE));

		// return;
		mv.visitLabel(lReturn);
		mv.visitFrame(Opcodes.F_NEW, 1, new Object[] { enhancedClassName }, 0, new Object[] {});
		mv.visitInsn(Opcodes.RETURN);

		mv.visitLabel(lOut);
		mv.visitLocalVariable(Enhancer.THIS, descEnhancer, null, lCheckInitialized, lOut, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private static void createMethodGetManagedInstance(final String enhancedClassName, final String descEnhancer, final ClassWriter cw) {
		final MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, Enhancer.METHOD_ENHANCED_GET_MANAGED_INSTANCE, Enhancer.makeDescription(ManagedInstance.class), null, null);
		mv.visitCode();

		final Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_MANAGED_INSTANCE, Enhancer.DESCRIPTOR_MANAGED_INSTANCE);
		mv.visitInsn(Opcodes.ARETURN);

		final Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable(Enhancer.THIS, descEnhancer, null, l0, l1, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private static void createMethodIsInitialized(final String enhancedClassName, final String descEnhancer, final ClassWriter cw) {
		final MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, Enhancer.METHOD_ENHANCED_IS_INITIALIZED, Enhancer.makeDescription(Boolean.TYPE), null, null);
		mv.visitCode();

		final Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_INITIALIZED, Enhancer.DESCRIPTOR_BOOLEAN);
		mv.visitInsn(Opcodes.IRETURN);

		final Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable(Enhancer.THIS, descEnhancer, null, l0, l1, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private static void createMethodSetInitialized(final String enhancedClassName, final String descEnhancer, final ClassWriter cw) {
		final MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, Enhancer.METHOD_ENHANCED_SET_INITIALIZED, Enhancer.makeDescription(Void.TYPE), null, null);
		mv.visitCode();

		final Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitInsn(Opcodes.ICONST_1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_INITIALIZED, Enhancer.DESCRIPTOR_BOOLEAN);

		mv.visitInsn(Opcodes.RETURN);

		final Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable(Enhancer.THIS, descEnhancer, null, l0, l1, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private static void createMethodSetManagedInstance(final String enhancedClassName, final String descEnhancer, final ClassWriter cw) {
		final MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, Enhancer.METHOD_ENHANCED_SET_MANAGED_INSTANCE, Enhancer.makeDescription(Void.TYPE, ManagedInstance.class), null, null);
		mv.visitCode();

		final Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_MANAGED_INSTANCE, Enhancer.DESCRIPTOR_MANAGED_INSTANCE);

		mv.visitInsn(Opcodes.RETURN);

		final Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable(Enhancer.THIS, descEnhancer, null, l0, l1, 0);
		mv.visitLocalVariable("instance", Enhancer.DESCRIPTOR_MANAGED_INSTANCE, null, l0, l1, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	private static void createNoArgConstructor(final String enhancingClassName, final String enhancedClassName, final String descEnhancer, final ClassWriter cw) {
		final MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, Enhancer.CONSTRUCTOR_INIT, Enhancer.makeDescription(Void.TYPE), null, null);
		mv.visitCode();

		final Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, enhancingClassName, Enhancer.CONSTRUCTOR_INIT, Enhancer.makeDescription(Void.TYPE));

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitInsn(Opcodes.ACONST_NULL);
		mv.visitFieldInsn(Opcodes.PUTFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_ID, Enhancer.DESCRIPTOR_OBJECT);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitInsn(Opcodes.ACONST_NULL);
		mv.visitFieldInsn(Opcodes.PUTFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_TYPE, Enhancer.DESCRIPTOR_CLASS);

		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitInsn(Opcodes.ACONST_NULL);
		mv.visitFieldInsn(Opcodes.PUTFIELD, enhancedClassName, Enhancer.FIELD_ENHANCED_SESSION, Enhancer.DESCRIPTOR_SESSION);

		mv.visitInsn(Opcodes.RETURN);

		final Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable(Enhancer.THIS, descEnhancer, null, l0, l1, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	//@formatter:on

	private static void createOverrriddenMethod(final String enhancingClassName, final String enhancedClassName, final String descEnhancer,
		final ClassWriter cw, Method method) {
		final String methodDescription = Enhancer.makeDescription(method.getReturnType(), method.getParameterTypes());

		// TODO Exception types
		for (int i = 0; i < method.getExceptionTypes().length; i++) {}

		final MethodVisitor mv = cw.visitMethod(method.getModifiers(), method.getName(), methodDescription, null, null);
		mv.visitCode();

		// this.__enhanced_$$__check();
		final Label lCheck = new Label();
		mv.visitLabel(lCheck);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, enhancedClassName, Enhancer.METHOD_ENHANCED_CHECK, Enhancer.makeDescription(Void.TYPE));

		mv.visitVarInsn(Opcodes.ALOAD, 0); // load this

		// infer the method parameters
		for (int i = 0, r = 1; i < method.getParameterTypes().length; i++, r++) {
			final Class<?> paramClass = method.getParameterTypes()[i];
			mv.visitVarInsn(Enhancer.getLoadType(paramClass), r); // load parameter

			if ((paramClass == Double.TYPE) || (paramClass == Long.TYPE)) {
				r++;
			}
		}

		// super.method(...);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, enhancingClassName, method.getName(), methodDescription);

		if (method.getReturnType() != Void.TYPE) {
			// non void return
			mv.visitInsn(Enhancer.getReturnType(method.getReturnType()));
		}
		else {
			// void return
			mv.visitInsn(Opcodes.RETURN);
		}

		final Label lOut = new Label();
		mv.visitLabel(lOut);
		Enhancer.registerLocals(descEnhancer, method, mv, lCheck, lOut);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	/**
	 * Enhances a type.
	 * 
	 * @param <T>
	 *            the type of the entity
	 * @param type
	 *            the type for the class
	 * @return the enhanced class
	 * @throws Exception
	 *             thrown if the enhancement fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> enhance(EntityType<T> type) throws Exception {
		final Class<T> javaType = type.getJavaType();
		final ClassLoader classLoader = javaType.getClassLoader();
		final String className = type.getJavaType().getName() + Enhancer.CLASS_ENHANCED_SUFFIX;

		// try if loaded
		final Class<T> enhancedClass = (Class<T>) Enhancer.tryLoadClass(classLoader, className);
		if (enhancedClass != null) {
			return enhancedClass;
		}

		return Enhancer.enhance0(javaType, classLoader, className);
	}

	private synchronized static <T> Class<T> enhance0(final Class<T> javaType, final ClassLoader classLoader, final String className) throws Exception {
		final byte[] byteCode = Enhancer.create(javaType);

		final Class<T> enhancedClass = Enhancer.loadClass(classLoader, byteCode, className);

		return enhancedClass;
	}

	private static void getDescriptor(final StringBuffer buf, final Class<?> c) {
		Class<?> d = c;
		while (true) {
			if (d.isPrimitive()) {
				char car;
				if (d == Integer.TYPE) {
					car = 'I';
				}
				else if (d == Void.TYPE) {
					car = 'V';
				}
				else if (d == Boolean.TYPE) {
					car = 'Z';
				}
				else if (d == Byte.TYPE) {
					car = 'B';
				}
				else if (d == Character.TYPE) {
					car = 'C';
				}
				else if (d == Short.TYPE) {
					car = 'S';
				}
				else if (d == Double.TYPE) {
					car = 'D';
				}
				else if (d == Float.TYPE) {
					car = 'F';
				}
				else /* if (d == Long.TYPE) */{
					car = 'J';
				}
				buf.append(car);
				return;
			}
			else if (d.isArray()) {
				buf.append('[');
				d = d.getComponentType();
			}
			else {
				buf.append('L');
				final String name = d.getName();
				final int len = name.length();
				for (int i = 0; i < len; ++i) {
					final char car = name.charAt(i);
					buf.append(car == '.' ? '/' : car);
				}
				buf.append(';');
				return;
			}
		}
	}

	private static int getLoadType(Class<?> paramClass) {
		if (!paramClass.isPrimitive() || paramClass.isArray()) {
			return Opcodes.ALOAD;
		}

		if (Long.TYPE == paramClass) {
			return Opcodes.LLOAD;
		}

		if (Float.TYPE == paramClass) {
			return Opcodes.FLOAD;
		}

		if (Double.TYPE == paramClass) {
			return Opcodes.DLOAD;
		}

		return Opcodes.ILOAD;
	}

	private static int getReturnType(Class<?> paramClass) {
		if (!paramClass.isPrimitive() || paramClass.isArray()) {
			return Opcodes.ARETURN;
		}

		if (Long.TYPE == paramClass) {
			return Opcodes.LRETURN;
		}

		if (Float.TYPE == paramClass) {
			return Opcodes.FRETURN;
		}

		if (Double.TYPE == paramClass) {
			return Opcodes.DRETURN;
		}

		return Opcodes.IRETURN;
	}

	/**
	 * Loads the class.
	 * 
	 * @param classLoader
	 *            the class loader
	 * @param byteCode
	 *            the bytecode
	 * @param className
	 *            the name of the class
	 * @param <T>
	 *            the type of the class
	 * @return the loaded class
	 * @throws Exception
	 *             if class cannot be loaded
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> loadClass(ClassLoader classLoader, byte[] byteCode, String className) throws Exception {
		final Class<?> cls = Class.forName("java.lang.ClassLoader");
		final java.lang.reflect.Method method = cls.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, Integer.TYPE, Integer.TYPE });

		// protected method invocation
		method.setAccessible(true);
		try {
			final Object[] args = new Object[] { className, byteCode, Integer.valueOf(0), Integer.valueOf(byteCode.length) };
			return (Class<T>) method.invoke(classLoader, args);
		}
		finally {
			method.setAccessible(false);
		}
	}

	private static String makeClassDesc(String className) {
		return "L" + className + ";";
	}

	private static String makeDescription(Class<?> returnType, Class<?>... parameters) {
		final StringBuffer buf = new StringBuffer();
		buf.append('(');
		for (int i = 0; i < parameters.length; ++i) {
			Enhancer.getDescriptor(buf, parameters[i]);
		}
		buf.append(')');
		Enhancer.getDescriptor(buf, returnType);
		return buf.toString();
	}

	private static void registerLocals(final String descEnhancer, Method method, final MethodVisitor mv, final Label l0, final Label l) {
		mv.visitLocalVariable(Enhancer.THIS, descEnhancer, null, l0, l, 0);
		for (int i = 0, r = 1; i < method.getParameterTypes().length; i++, r++) {
			final Class<?> paramClass = method.getParameterTypes()[i];
			mv.visitLocalVariable("arg" + i, Type.getDescriptor(paramClass), null, l0, l, r);

			if ((paramClass == Double.TYPE) || (paramClass == Long.TYPE)) {
				r++;
			}
		}
	}

	private static Class<?> tryLoadClass(final ClassLoader classLoader, final String className) {
		final ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(classLoader);
			return Class.forName(className);
		}
		catch (final ClassNotFoundException e) {
			return null;
		}
		finally {
			Thread.currentThread().setContextClassLoader(oldClassLoader);
		}
	}
}
