package com.rwtema.denseores.modintegration;

import cpw.mods.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflections {

    // Let's try to be sparing with the reflection
    @SuppressWarnings("unchecked")
    public static Method getMethodFromClass(String clazzName, String methodName, Class<?>... params) throws ReflectionHelper.UnableToFindMethodException {
        try {
            Class<? super Object> clazz = (Class<? super Object>) Class.forName(clazzName);
            return ReflectionHelper.findMethod(clazz, null, new String[]{methodName}, params);
        } catch (ClassNotFoundException e) {
            throw new ReflectionHelper.UnableToFindMethodException(new String[]{methodName}, e);
        }
    }


    @SuppressWarnings("unchecked")
    public static Field getFieldFromClass(String clazzName, String fieldName) throws ReflectionHelper.UnableToFindFieldException {
        try {
            Class<? super Object> clazz = (Class<? super Object>) Class.forName(clazzName);
            return ReflectionHelper.findField(clazz, fieldName);
        } catch (ClassNotFoundException e) {
            throw new ReflectionHelper.UnableToFindFieldException(new String[]{fieldName}, e);
        }
    }
}
