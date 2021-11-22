package com.basis.ui;

import com.kit.utils.Logger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ObjUtil {

    /**
     * 判断一个是JAVA内置类型还是用户定义类型
     *
     * @param clz
     * @return
     */
    public static boolean isJavaClass(Class<?> clz) {
        return clz != null && clz.getClassLoader() == null;
    }

    /**
     * 判断对象是JAVA内置类型还是用户定义类型
     *
     * @param obj
     * @return
     */
    public static boolean isJavaClass(Object obj) {
        return isJavaClass(obj.getClass());
    }

    /**
     * 判断两个对象是否是相同类型
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean sameClass(Object obj1, Object obj2) {
        return obj1.getClass() == obj2.getClass();
    }

    /**
     * 判断对象是否相同或继承关系
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean sameType(Object obj1, Object obj2) {
        return isInstance(obj1, obj2.getClass()) || isInstance(obj2, obj1.getClass());
    }

    /**
     * @param obj   对象
     * @param clazz 判断类型Class字节码
     * @return
     */
    private static boolean isInstance(Object obj, Class clazz) {
        // String a = "abc"
        return clazz.isInstance(obj);//a instanceof String
    }

    public static <T> Class[] getTType(T obj) {
        return getTType(obj.getClass());
    }

    public static <T> Class[] getTType(Class clazz) {
        if (clazz == null) {
            return null;
        }
        Class[] classes = null;
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            //获得泛型类型的泛型参数（实际类型参数)
            Type[] tArr = pt.getActualTypeArguments();
            int len = null == tArr ? 0 : tArr.length;
            Logger.e("ObjUtil", "len :" + len);
            classes = new Class[len];
            for (int i = 0; i < len; i++) {
                Type t = tArr[i];
                if (t instanceof Class) {
                    classes[i] = (Class) tArr[i];
                }
                Logger.e("ObjUtil", "t TypeName:" + t.toString());
            }
        }
        if (classes != null) {
            return classes;
        } else {
            return getTType(clazz.getSuperclass());
        }
    }

}
