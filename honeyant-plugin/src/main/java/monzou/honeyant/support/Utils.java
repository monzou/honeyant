package monzou.honeyant.support;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * tiny Java language utilities.
 * 
 * @author monzou
 */
public final class Utils {

    public static Class<?> toWrapperType(Class<?> primitiveType) {
        Object array = Array.newInstance(primitiveType, 1);
        return Array.get(array, 0).getClass();
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static String getPropertyName(Method method) {
        return toPropertyName(method.getName());
    }

    public static String toPropertyName(String methodName) {
        if (methodName == null || methodName.isEmpty()) {
            return methodName;
        }
        return uncapitalize(methodName.replaceAll("^(set|is|get)", ""));
    }

    public static boolean isBasicMethod(Method method) {
        Class<?> clazz = method.getDeclaringClass();
        if (clazz == Object.class || method.isBridge() || method.isSynthetic()) {
            return true;
        }
        return false;
    }

    public static boolean isGetter(Method method) {
        if (method == null) {
            return false;
        }
        String methodName = method.getName();
        return methodName.startsWith("get") || methodName.startsWith("is");
    }

    public static boolean isSetter(Method method) {
        if (method == null) {
            return false;
        }
        String methodName = method.getName();
        return methodName.startsWith("set");
    }

    public static String uncapitalize(String s) {
        if (s == null) {
            return s;
        }
        int size = s.length();
        if (size == 0) {
            return s;
        }
        StringBuilder builder = new StringBuilder(size);
        builder.append(Character.toLowerCase(s.charAt(0)));
        return builder.append(s.substring(1)).toString();
    }

    private Utils() {
    }

}
