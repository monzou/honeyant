package com.usopla.honeyant.support;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * A simple utility class which creates a preference proxy for given interface.<br />
 * The generated preference instance stores its properties to the project preference.
 * 
 * @author monzou
 */
public final class PreferenceModelFactory {

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz, IProject project) {
        if (clazz.isInterface()) {
            PreferenceStoreAdapter adapter = new PreferenceStoreAdapter(project);
            Class<?>[] interfaces = new Class<?>[] { clazz, };
            ClassLoader classLoader = clazz.getClassLoader();
            return (T) Proxy.newProxyInstance(classLoader, interfaces, adapter);
        }
        throw new IllegalArgumentException("clazz must be interface: given-class=" + clazz.getName());
    }

    private static class PreferenceStoreAdapter implements InvocationHandler {

        private final IProject project;

        PreferenceStoreAdapter(IProject project) {
            this.project = project;
        }

        IPreferenceStore getStore() {
            return PluginUtils.getProjectPreference(project);
        }

        /** {@inheritDoc} */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Utils.isBasicMethod(method)) {
                String methodName = method.getName();
                if ("equals".equals(methodName)) {
                    return proxy == args[0];
                } else if ("hashCode".equals(methodName)) {
                    return System.identityHashCode(proxy);
                } else if ("toString".equals(methodName)) {
                    StringBuilder types = new StringBuilder();
                    for (Class<?> i : proxy.getClass().getInterfaces()) {
                        if (types.length() > 0) {
                            types.append(", ");
                        }
                        types.append(i.getSimpleName());
                    }
                    return String.format("%s@%s[%s][%s]", //
                            proxy.getClass().getName(), Integer.toHexString(System.identityHashCode(proxy)), types, toString());
                }
            }
            if (Utils.isGetter(method)) {
                return getValue(method);
            }
            if (Utils.isSetter(method)) {
                return setValue(method, args);
            }
            return method.invoke(proxy, args);
        }

        private Object setValue(Method method, Object[] args) throws IOException {
            IPreferenceStore store = getStore();
            String key = getKey(method);
            Object value = args[0];
            Class<?> clazz = value.getClass();
            if (clazz.isPrimitive()) {
                clazz = Utils.toWrapperType(clazz);
            }
            if (Boolean.class.isAssignableFrom(clazz)) {
                store.setValue(key, (Boolean) value);
            } else if (String.class.isAssignableFrom(clazz)) {
                store.setValue(key, (String) value);
            } else if (Integer.class.isAssignableFrom(clazz)) {
                store.setValue(key, (Integer) value);
            } else if (Double.class.isAssignableFrom(clazz)) {
                store.setValue(key, (Double) value);
            } else if (Float.class.isAssignableFrom(clazz)) {
                store.setValue(key, (Float) value);
            } else if (Long.class.isAssignableFrom(clazz)) {
                store.setValue(key, (Long) value);
            } else {
                throw new IllegalArgumentException("unexpected type: " + value.getClass().getName());
            }
            ((IPersistentPreferenceStore) store).save();
            return null;
        }

        private Object getValue(Method method) {
            IPreferenceStore store = getStore();
            String key = getKey(method);
            Class<?> returnType = method.getReturnType();
            if (returnType.isPrimitive()) {
                returnType = Utils.toWrapperType(returnType);
            }
            if (Boolean.class.isAssignableFrom(returnType)) {
                return store.getBoolean(key);
            } else if (String.class.isAssignableFrom(returnType)) {
                return store.getString(key);
            } else if (Integer.class.isAssignableFrom(returnType)) {
                return store.getInt(key);
            } else if (Double.class.isAssignableFrom(returnType)) {
                return store.getDouble(key);
            } else if (Float.class.isAssignableFrom(returnType)) {
                return store.getFloat(key);
            } else if (Long.class.isAssignableFrom(returnType)) {
                return store.getLong(key);
            } else {
                throw new IllegalArgumentException("unexpected type: " + returnType.getName());
            }
        }

        private String getKey(Method method) {
            return String.format("%s.%s", getPrefix(), Utils.getPropertyName(method));
        }

        private String getPrefix() {
            return String.format("%s.%s", Constants.PLUGIN_ID, project.getName());
        }

    }

    private PreferenceModelFactory() {
    }

}
