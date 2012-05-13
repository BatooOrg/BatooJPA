package org.batoo.jpa.core.test.enhance;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.batoo.jpa.core.impl.EntityManagerImpl;
import org.batoo.jpa.core.impl.SessionImpl;
import org.batoo.jpa.core.impl.metamodel.EntityTypeImpl;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.google.common.collect.Maps;

public final class Enhancer implements Opcodes {

	private static final String THIS = "this";
	private static final String SUFFIX_ENHANCED = "$Enhanced";

	private static final String CLASS_ENTITY_MANAGER_IMPL = "org/batoo/jpa/core/impl/EntityManagerImpl";
	private static final String CLASS_SESSION_IMPL = "org/batoo/jpa/core/impl/SessionImpl";
	private static final String CLASS_ENHANCED_INSTANCE_NAME = "org/batoo/jpa/core/impl/instance/EnhancedInstance";
	private static final String CLASS_ENHANCED_SUFFIX = SUFFIX_ENHANCED;

	private static final String CONSTRUCTOR_INIT = "<init>";

	private static final String FIELD_SERIAL_VERSION_UID = "serialVersionUID";
	private static final String FIELD_ENHANCED_INITIALIZED = "__enhanced__$$__initialized";
	private static final String FIELD_ENHANCED_SESSION = "__enhanced_$$__session";
	private static final String FIELD_ENHANCED_TYPE = "__enhanced_$$__type";
	private static final String FIELD_ENHANCED_ID = "__enhanced__$$__id";

	private static final String METHOD_ENHANCED_IS_INITIALIZED = "__enhanced__$$__isInitialized";
	private static final String METHOD_ENHANCED_CHECK = "__enhanced_$$__check";
	private static final String METHOD_ENHANCED_GET_ID = "get__enhanced__$$__id";
	private static final String METHOD_GET_ENTITY_MANAGER = "getEntityManager";
	private static final String METHOD_FIND = "find";

	private static final String DESC_CLASS_OBJECT = "Ljava/lang/Object;";
	private static final String DESC_JAVA_LANG_CLASS = "Ljava/lang/Class;";
	private static final String DESC_SESSION_IMPL = "Lorg/batoo/jpa/core/impl/SessionImpl;";

	private static final String PRIM_BOOLEAN = "Z";
	private static final String DESC_PRIM_BOOLEAN = "()Z";

	public static byte[] create(EntityTypeImpl<?> type, Class<?> clazz) throws Exception {

		final String enhancingClassName = Enhancer.makeClassName(clazz);
		final String enhancedClassName = enhancingClassName + SUFFIX_ENHANCED;
		final String descEnhancer = Enhancer.makeClassDesc(enhancedClassName);

		final ClassWriter cw = new ClassWriter(0);
		cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, enhancedClassName, null, enhancingClassName, new String[] { CLASS_ENHANCED_INSTANCE_NAME });

		// Field: serialVersionUID
		cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC, FIELD_SERIAL_VERSION_UID, "J", null, new Long(1L)).visitEnd();

		// Container fields
		cw.visitField(ACC_PRIVATE, FIELD_ENHANCED_INITIALIZED, PRIM_BOOLEAN, null, null).visitEnd();
		cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_TRANSIENT, FIELD_ENHANCED_ID, DESC_CLASS_OBJECT, null, null).visitEnd();
		cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_TRANSIENT, FIELD_ENHANCED_TYPE, DESC_JAVA_LANG_CLASS, null, null).visitEnd();
		cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_TRANSIENT, FIELD_ENHANCED_SESSION, DESC_SESSION_IMPL, null, null).visitEnd();

		// Constructors
		Enhancer.createNoArgConstructor(enhancingClassName, enhancedClassName, descEnhancer, cw);
		Enhancer.createContainerConstructor(enhancingClassName, enhancedClassName, descEnhancer, cw);
		Enhancer.createMethodIsInitialized(enhancedClassName, descEnhancer, cw);
		Enhancer.createMethodCheck(enhancedClassName, descEnhancer, cw);
		Enhancer.createMethodGetId(enhancedClassName, descEnhancer, cw);

		final Map<String, Method> methods = Maps.newHashMap();

		final Class<?> currentClass = clazz;
		while (currentClass != Object.class) { // we are not interested in Object.class
			for (final Method method : currentClass.getDeclaredMethods()) {
				int modifiers = method.getModifiers();

				// Filter out uninterested details
				modifiers &= Modifier.ABSTRACT;
				modifiers &= Modifier.FINAL;
				modifiers &= Modifier.NATIVE;
				modifiers &= Modifier.PRIVATE;
				modifiers &= Modifier.PROTECTED;
				modifiers &= Modifier.STATIC;
				modifiers &= Modifier.STRICT;

				if ((modifiers == Modifier.PUBLIC) || (modifiers == 0)) {
					// we are not interested in getters for id methods
					if (!type.isIdMethod(method)) {

						// we are not interested in the return type to omit the overridden methods
						final String desc = method.getName() + Enhancer.makeDescription(Void.class, method.getParameterTypes());

						if (methods.get(desc) == null) {
							methods.put(desc, method);
						}
					}
				}
			}
		}

		for (final Method method : methods.values()) {
			Enhancer.createOverrriddenMethod(enhancingClassName, enhancedClassName, descEnhancer, cw, method);
		}

		cw.visitEnd();

		return cw.toByteArray();
	}

	private static void createContainerConstructor(final String enhancingClassName, final String enhancedClassName,
		final String descEnhancer, final ClassWriter cw) {
		MethodVisitor mv;
		mv = cw.visitMethod(ACC_PUBLIC, CONSTRUCTOR_INIT,
			Enhancer.makeDescription(Void.class, Class.class, SessionImpl.class, Object.class, boolean.class), null, null);
		mv.visitCode();

		final Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(45, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, enhancingClassName, CONSTRUCTOR_INIT, "()V");

		final Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLineNumber(47, l1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitFieldInsn(PUTFIELD, enhancedClassName, FIELD_ENHANCED_TYPE, DESC_JAVA_LANG_CLASS);

		final Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLineNumber(48, l2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitFieldInsn(PUTFIELD, enhancedClassName, FIELD_ENHANCED_SESSION, DESC_SESSION_IMPL);

		final Label l3 = new Label();
		mv.visitLabel(l3);
		mv.visitLineNumber(49, l3);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitFieldInsn(PUTFIELD, enhancedClassName, FIELD_ENHANCED_ID, DESC_CLASS_OBJECT);

		final Label l4 = new Label();
		mv.visitLabel(l4);
		mv.visitLineNumber(50, l4);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ILOAD, 4);
		mv.visitFieldInsn(PUTFIELD, enhancedClassName, FIELD_ENHANCED_INITIALIZED, PRIM_BOOLEAN);

		final Label l5 = new Label();
		mv.visitLabel(l5);
		mv.visitLineNumber(51, l5);
		mv.visitInsn(RETURN);

		final Label l6 = new Label();
		mv.visitLabel(l6);
		mv.visitLocalVariable(THIS, descEnhancer, null, l0, l6, 0);
		mv.visitLocalVariable("type", DESC_JAVA_LANG_CLASS, null, l0, l6, 1);
		mv.visitLocalVariable("session", DESC_SESSION_IMPL, null, l0, l6, 2);
		mv.visitLocalVariable("id", DESC_CLASS_OBJECT, null, l0, l6, 3);
		mv.visitLocalVariable("initialized", PRIM_BOOLEAN, null, l0, l6, 4);
		mv.visitMaxs(2, 5);

		mv.visitEnd();
	}

	private static void createMethodCheck(final String enhancedClassName, final String descEnhancer, final ClassWriter cw) {
		MethodVisitor mv;
		mv = cw.visitMethod(ACC_PRIVATE, METHOD_ENHANCED_CHECK, Enhancer.makeDescription(Void.class), null, null);
		mv.visitCode();

		final Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(58, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, enhancedClassName, FIELD_ENHANCED_SESSION, DESC_SESSION_IMPL);
		mv.visitMethodInsn(INVOKEVIRTUAL, CLASS_SESSION_IMPL, METHOD_GET_ENTITY_MANAGER, Enhancer.makeDescription(EntityManagerImpl.class));
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, enhancedClassName, FIELD_ENHANCED_TYPE, DESC_JAVA_LANG_CLASS);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, enhancedClassName, FIELD_ENHANCED_ID, DESC_CLASS_OBJECT);
		mv.visitMethodInsn(INVOKEVIRTUAL, CLASS_ENTITY_MANAGER_IMPL, METHOD_FIND,
			Enhancer.makeDescription(Object.class, Class.class, Object.class));
		mv.visitInsn(POP);

		final Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLineNumber(59, l1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ICONST_1);
		mv.visitFieldInsn(PUTFIELD, enhancedClassName, METHOD_ENHANCED_IS_INITIALIZED, PRIM_BOOLEAN);

		final Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLineNumber(60, l2);
		mv.visitInsn(RETURN);

		final Label l3 = new Label();
		mv.visitLabel(l3);
		mv.visitLocalVariable(THIS, descEnhancer, null, l0, l3, 0);
		mv.visitMaxs(3, 1);

		mv.visitEnd();
	}

	private static void createMethodGetId(final String enhancedClassName, final String descEnhancer, final ClassWriter cw) {
		MethodVisitor mv;
		mv = cw.visitMethod(ACC_PUBLIC, METHOD_ENHANCED_GET_ID, Enhancer.makeDescription(Object.class), null, null);
		mv.visitCode();
		final Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(64, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, enhancedClassName, FIELD_ENHANCED_ID, DESC_CLASS_OBJECT);
		mv.visitInsn(ARETURN);
		final Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable(THIS, descEnhancer, null, l0, l1, 0);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	private static void createMethodIsInitialized(final String enhancedClassName, final String descEnhancer, final ClassWriter cw) {
		final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, METHOD_ENHANCED_IS_INITIALIZED, DESC_PRIM_BOOLEAN, null, null);
		mv.visitCode();

		final Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(54, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, enhancedClassName, FIELD_ENHANCED_INITIALIZED, PRIM_BOOLEAN);
		mv.visitInsn(IRETURN);

		final Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable(THIS, descEnhancer, null, l0, l1, 0);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	private static void createNoArgConstructor(final String enhancingClassName, final String enhancedClassName, final String descEnhancer,
		final ClassWriter cw) {
		final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, CONSTRUCTOR_INIT, Enhancer.makeDescription(Void.class), null, null);

		mv.visitCode();

		final Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(37, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, enhancingClassName, CONSTRUCTOR_INIT, Enhancer.makeDescription(Void.class));

		final Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLineNumber(39, l1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ACONST_NULL);
		mv.visitFieldInsn(PUTFIELD, enhancedClassName, FIELD_ENHANCED_ID, DESC_CLASS_OBJECT);

		final Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLineNumber(40, l2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ACONST_NULL);
		mv.visitFieldInsn(PUTFIELD, enhancedClassName, FIELD_ENHANCED_TYPE, DESC_JAVA_LANG_CLASS);

		final Label l3 = new Label();
		mv.visitLabel(l3);
		mv.visitLineNumber(41, l3);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ACONST_NULL);
		mv.visitFieldInsn(PUTFIELD, enhancedClassName, FIELD_ENHANCED_SESSION, DESC_SESSION_IMPL);

		final Label l4 = new Label();
		mv.visitLabel(l4);
		mv.visitLineNumber(42, l4);
		mv.visitInsn(RETURN);

		final Label l5 = new Label();
		mv.visitLabel(l5);
		mv.visitLocalVariable(THIS, descEnhancer, null, l0, l5, 0);
		mv.visitMaxs(2, 1);
		mv.visitEnd();
	}

	private static void createOverrriddenMethod(final String enhancingClassName, final String enhancedClassName, final String descEnhancer,
		final ClassWriter cw, Method method) {
		MethodVisitor mv;
		mv = cw.visitMethod(ACC_PUBLIC, "getBytePropertyArray", "()[B", null, null);
		mv.visitCode();
		final Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(82, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, enhancedClassName, METHOD_ENHANCED_CHECK, "()V");
		final Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLineNumber(83, l1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, enhancingClassName, "getBytePropertyArray", "()[B");
		mv.visitInsn(ARETURN);
		final Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLocalVariable(THIS, descEnhancer, null, l0, l2, 0);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	/**
	 * Enhances a type
	 * 
	 * @param type
	 *            the type for the class
	 * @return the enhanced class
	 * @throws Exception
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public static <T> Class<T> enhance(EntityTypeImpl<T> type) throws Exception {
		final byte[] byteCode = Enhancer.create(type, type.getJavaType());
		return Enhancer.loadClass(byteCode, type.getJavaType().getCanonicalName() + CLASS_ENHANCED_SUFFIX);
	}

	private static Object getDescription(Class<?> clazz) {
		/**
		 * This piece of code has been shamelessly copied from asm sources
		 */
		if (Void.class == clazz) {
			return "V";
		}

		if (boolean.class == clazz) {
			return "Z";
		}
		if (char.class == clazz) {
			return "C";
		}
		if (byte.class == clazz) {
			return "B";
		}
		if (short.class == clazz) {
			return "S";
		}
		if (int.class == clazz) {
			return "I";
		}
		if (float.class == clazz) {
			return "F";
		}
		if (long.class == clazz) {
			return "J";
		}
		if (double.class == clazz) {
			return "D";
		}
		if (boolean.class == clazz) {
			return "Z";
		}

		return "L" + Enhancer.makeClassName(clazz) + ";";
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> loadClass(byte[] byteCode, String className) throws Exception {
		// override classDefine (as it is protected) and define the class.
		Class<T> clazz = null;

		final ClassLoader loader = ClassLoader.getSystemClassLoader();
		final Class<?> cls = Class.forName("java.lang.ClassLoader");
		final java.lang.reflect.Method method = cls.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, int.class,
			int.class });

		// protected method invocation
		method.setAccessible(true);
		try {
			final Object[] args = new Object[] { className, byteCode, new Integer(0), new Integer(byteCode.length) };
			clazz = (Class<T>) method.invoke(loader, args);
		}
		finally {
			method.setAccessible(false);
		}
		return clazz;
	}

	private static String makeClassDesc(final String className) {
		return "L" + className + ";";
	}

	private static String makeClassName(Class<?> clazz) {
		return clazz.getCanonicalName().replace('.', '/');
	}

	private static String makeDescription(Class<?> returnType, Class<?>... params) {
		/**
		 * This piece of code has been shamelessly copied from asm sources
		 */

		final StringBuffer sb = new StringBuffer("(");
		for (final Class<?> clazz : params) {
			sb.append(Enhancer.getDescription(clazz));
		}

		sb.append(")");

		sb.append(Enhancer.getDescription(returnType));

		return sb.toString();
	}

}
