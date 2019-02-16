/*
 *      Copyright (c) 2018-2028, DreamLu All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: DreamLu 卢春梦 (596392912@qq.com)
 */

package org.springblade.core.tool.beans;


import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ClassUtil;
import org.springblade.core.tool.utils.ReflectUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.cglib.core.*;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * spring cglib 魔改
 *
 * <p>
 *     1. 支持链式 bean，支持 map
 *     2. ClassLoader 跟 target 保持一致
 * </p>
 *
 * @author L.cm
 */
public abstract class BladeBeanCopier {
	private static final Type CONVERTER = TypeUtils.parseType("org.springframework.cglib.core.Converter");
	private static final Type CLASS_UTIL = TypeUtils.parseType("org.springblade.core.tool.utils");
	private static final Type BEAN_COPIER = TypeUtils.parseType(BladeBeanCopier.class.getName());
	private static final Type BEAN_MAP = TypeUtils.parseType(Map.class.getName());
	private static final Signature COPY = new Signature("copy", Type.VOID_TYPE, new Type[]{Constants.TYPE_OBJECT, Constants.TYPE_OBJECT, CONVERTER});
	private static final Signature CONVERT = TypeUtils.parseSignature("Object convert(Object, Class, Object)");
	private static final Signature BEAN_MAP_GET = TypeUtils.parseSignature("Object get(Object)");
	private static final Signature IS_ASSIGNABLE_VALUE = TypeUtils.parseSignature("boolean isAssignableValue(Class, Object)");
	/**
	 * The map to store {@link BladeBeanCopier} of source type and class type for copy.
	 */
	private static final ConcurrentMap<BladeBeanCopierKey, BladeBeanCopier> BEAN_COPIER_MAP = new ConcurrentHashMap<>();

	public static BladeBeanCopier create(Class source, Class target, boolean useConverter) {
		return BladeBeanCopier.create(source, target, useConverter, false);
	}

	public static BladeBeanCopier create(Class source, Class target, boolean useConverter, boolean nonNull) {
		BladeBeanCopierKey copierKey = new BladeBeanCopierKey(source, target, useConverter, nonNull);
		// 利用 ConcurrentMap 缓存 提高性能，接近 直接 get set
		return BEAN_COPIER_MAP.computeIfAbsent(copierKey, key -> {
			Generator gen = new Generator();
			gen.setSource(key.getSource());
			gen.setTarget(key.getTarget());
			gen.setUseConverter(key.isUseConverter());
			gen.setNonNull(key.isNonNull());
			return gen.create(key);
		});
	}

	/**
	 * 拷贝
	 * @param from
	 * @param to
	 * @param converter
	 */
	abstract public void copy(Object from, Object to, Converter converter);

	public static class Generator extends AbstractClassGenerator {
		private static final Source SOURCE = new Source(BladeBeanCopier.class.getName());
		private Class source;
		private Class target;
		private boolean useConverter;
		private boolean nonNull;

		Generator() {
			super(SOURCE);
		}

		public void setSource(Class source) {
			if (!Modifier.isPublic(source.getModifiers())) {
				setNamePrefix(source.getName());
			}
			this.source = source;
		}

		public void setTarget(Class target) {
			if (!Modifier.isPublic(target.getModifiers())) {
				setNamePrefix(target.getName());
			}
			this.target = target;
		}

		public void setUseConverter(boolean useConverter) {
			this.useConverter = useConverter;
		}

		public void setNonNull(boolean nonNull) {
			this.nonNull = nonNull;
		}

		@Override
		protected ClassLoader getDefaultClassLoader() {
			// L.cm 保证 和 返回使用同一个 ClassLoader
			return target.getClassLoader();
		}

		@Override
		protected ProtectionDomain getProtectionDomain() {
			return ReflectUtils.getProtectionDomain(source);
		}

		@Override
		public BladeBeanCopier create(Object key) {
			return (BladeBeanCopier) super.create(key);
		}

		@Override
		public void generateClass(ClassVisitor v) {
			Type sourceType = Type.getType(source);
			Type targetType = Type.getType(target);
			ClassEmitter ce = new ClassEmitter(v);
			ce.begin_class(Constants.V1_2,
				Constants.ACC_PUBLIC,
				getClassName(),
				BEAN_COPIER,
				null,
				Constants.SOURCE_FILE);

			EmitUtils.null_constructor(ce);
			CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, COPY, null);

			// map 单独处理
			if (Map.class.isAssignableFrom(source)) {
				generateClassFormMap(ce, e, sourceType, targetType);
				return;
			}

			// 2018.12.27 by L.cm 支持链式 bean
			// 注意：此处需兼容链式bean 使用了 spring 的方法，比较耗时
			PropertyDescriptor[] getters = ReflectUtil.getBeanGetters(source);
			PropertyDescriptor[] setters = ReflectUtil.getBeanSetters(target);
			Map<String, PropertyDescriptor> names = new HashMap<>(16);
			for (PropertyDescriptor getter : getters) {
				names.put(getter.getName(), getter);
			}

			Local targetLocal = e.make_local();
			Local sourceLocal = e.make_local();
			e.load_arg(1);
			e.checkcast(targetType);
			e.store_local(targetLocal);
			e.load_arg(0);
			e.checkcast(sourceType);
			e.store_local(sourceLocal);

			for (PropertyDescriptor setter : setters) {
				String propName = setter.getName();

				CopyProperty targetIgnoreCopy = ReflectUtil.getAnnotation(target, propName, CopyProperty.class);
				// set 上有忽略的 注解
				if (targetIgnoreCopy != null) {
					if (targetIgnoreCopy.ignore()) {
						continue;
					}
					// 注解上的别名，如果别名不为空，使用别名
					String aliasTargetPropName = targetIgnoreCopy.value();
					if (StringUtil.isNotBlank(aliasTargetPropName)) {
						propName = aliasTargetPropName;
					}
				}
				// 找到对应的 get
				PropertyDescriptor getter = names.get(propName);
				// 没有 get 跳出
				if (getter == null) {
					continue;
				}

				MethodInfo read = ReflectUtils.getMethodInfo(getter.getReadMethod());
				Method writeMethod = setter.getWriteMethod();
				MethodInfo write = ReflectUtils.getMethodInfo(writeMethod);
				Type returnType = read.getSignature().getReturnType();
				Type setterType = write.getSignature().getArgumentTypes()[0];
				Class<?> getterPropertyType = getter.getPropertyType();
				Class<?> setterPropertyType = setter.getPropertyType();

				// L.cm 2019.01.12 优化逻辑，先判断类型，类型一致直接 set，不同再判断 是否 类型转换
				// nonNull Label
				Label l0 = e.make_label();
				// 判断类型是否一致，包括 包装类型
				if (ClassUtil.isAssignable(getterPropertyType, setterPropertyType)) {
					// 2018.12.27 by L.cm 支持链式 bean
					e.load_local(targetLocal);
					e.load_local(sourceLocal);
					e.invoke(read);
					boolean getterIsPrimitive = getterPropertyType.isPrimitive();
					boolean setterIsPrimitive = setterPropertyType.isPrimitive();

					if (nonNull) {
						// 需要落栈，强制装箱
						e.box(returnType);
						Local var = e.make_local();
						e.store_local(var);
						e.load_local(var);
						// nonNull Label
						e.ifnull(l0);
						e.load_local(targetLocal);
						e.load_local(var);
						// 需要落栈，强制拆箱
						e.unbox_or_zero(setterType);
					} else {
						// 如果 get 为原始类型，需要装箱
						if (getterIsPrimitive && !setterIsPrimitive) {
							e.box(returnType);
						}
						// 如果 set 为原始类型，需要拆箱
						if (!getterIsPrimitive && setterIsPrimitive) {
							e.unbox_or_zero(setterType);
						}
					}

					// 构造 set 方法
					invokeWrite(e, write, writeMethod, nonNull, l0);
				} else if (useConverter) {
					e.load_local(targetLocal);
					e.load_arg(2);
					e.load_local(sourceLocal);
					e.invoke(read);
					e.box(returnType);

					if (nonNull) {
						Local var = e.make_local();
						e.store_local(var);
						e.load_local(var);
						e.ifnull(l0);
						e.load_local(targetLocal);
						e.load_arg(2);
						e.load_local(var);
					}

					EmitUtils.load_class(e, setterType);
					// 更改成了属性名，之前是 set 方法名
					e.push(propName);
					e.invoke_interface(CONVERTER, CONVERT);
					e.unbox_or_zero(setterType);

					// 构造 set 方法
					invokeWrite(e, write, writeMethod, nonNull, l0);
				}
			}
			e.return_value();
			e.end_method();
			ce.end_class();
		}

		private static void invokeWrite(CodeEmitter e, MethodInfo write, Method writeMethod, boolean nonNull, Label l0) {
			// 返回值，判断 链式 bean
			Class<?> returnType = writeMethod.getReturnType();
			e.invoke(write);
			// 链式 bean，有返回值需要 pop
			if (!returnType.equals(Void.TYPE)) {
				e.pop();
			}
			if (nonNull) {
				e.visitLabel(l0);
			}
		}

		@Override
		protected Object firstInstance(Class type) {
			return BeanUtil.newInstance(type);
		}

		@Override
		protected Object nextInstance(Object instance) {
			return instance;
		}

		/**
		 * 处理 map 的 copy
		 * @param ce ClassEmitter
		 * @param e CodeEmitter
		 * @param sourceType sourceType
		 * @param targetType targetType
		 */
		public void generateClassFormMap(ClassEmitter ce, CodeEmitter e, Type sourceType, Type targetType) {
			// 2018.12.27 by L.cm 支持链式 bean
			PropertyDescriptor[] setters = ReflectUtil.getBeanSetters(target);

			// 入口变量
			Local targetLocal = e.make_local();
			Local sourceLocal = e.make_local();
			e.load_arg(1);
			e.checkcast(targetType);
			e.store_local(targetLocal);
			e.load_arg(0);
			e.checkcast(sourceType);
			e.store_local(sourceLocal);
			Type mapBox = Type.getType(Object.class);

			for (PropertyDescriptor setter : setters) {
				String propName = setter.getName();

				// set 上有忽略的 注解
				CopyProperty targetIgnoreCopy = ReflectUtil.getAnnotation(target, propName, CopyProperty.class);
				if (targetIgnoreCopy != null) {
					if (targetIgnoreCopy.ignore()) {
						continue;
					}
					// 注解上的别名
					String aliasTargetPropName = targetIgnoreCopy.value();
					if (StringUtil.isNotBlank(aliasTargetPropName)) {
						propName = aliasTargetPropName;
					}
				}

				Method writeMethod = setter.getWriteMethod();
				MethodInfo write = ReflectUtils.getMethodInfo(writeMethod);
				Type setterType = write.getSignature().getArgumentTypes()[0];

				e.load_local(targetLocal);
				e.load_local(sourceLocal);

				e.push(propName);
				// 执行 map get
				e.invoke_interface(BEAN_MAP, BEAN_MAP_GET);
				// box 装箱，避免 array[] 数组问题
				e.box(mapBox);

				// 生成变量
				Local var = e.make_local();
				e.store_local(var);
				e.load_local(var);

				// 先判断 不为null，然后做类型判断
				Label l0 = e.make_label();
				e.ifnull(l0);
				EmitUtils.load_class(e, setterType);
				e.load_local(var);
				// ClassUtil.isAssignableValue(Integer.class, id)
				e.invoke_static(CLASS_UTIL, IS_ASSIGNABLE_VALUE);
				Label l1 = new Label();
				// 返回值，判断 链式 bean
				Class<?> returnType = writeMethod.getReturnType();
				if (useConverter) {
					e.if_jump(Opcodes.IFEQ, l1);
					e.load_local(targetLocal);
					e.load_local(var);
					e.unbox_or_zero(setterType);
					e.invoke(write);
					if (!returnType.equals(Void.TYPE)) {
						e.pop();
					}
					e.goTo(l0);
					e.visitLabel(l1);
					e.load_local(targetLocal);
					e.load_arg(2);
					e.load_local(var);
					EmitUtils.load_class(e, setterType);
					e.push(propName);
					e.invoke_interface(CONVERTER, CONVERT);
					e.unbox_or_zero(setterType);
					e.invoke(write);
				} else {
					e.if_jump(Opcodes.IFEQ, l0);
					e.load_local(targetLocal);
					e.load_local(var);
					e.unbox_or_zero(setterType);
					e.invoke(write);
				}
				// 返回值，判断 链式 bean
				if (!returnType.equals(Void.TYPE)) {
					e.pop();
				}
				e.visitLabel(l0);
			}
			e.return_value();
			e.end_method();
			ce.end_class();
		}
	}
}
