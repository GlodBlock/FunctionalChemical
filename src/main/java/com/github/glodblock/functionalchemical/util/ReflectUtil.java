package com.github.glodblock.functionalchemical.util;

import mekanism.api.chemical.merged.ChemicalTankWrapper;

import java.lang.reflect.Field;

public class ReflectUtil {

    public static final FieldAccessor INTERNAL_TANK = get(ChemicalTankWrapper.class, "internal");

    private static FieldAccessor get(Class<?> clazz, String fieldName) {
        try {
            var f = clazz.getDeclaredField(fieldName);
            f.setAccessible(true);
            return new FieldAccessor(f);
        } catch (Throwable e) {
            return FieldAccessor.FAIL;
        }
    }

    public record FieldAccessor(Field field) {

        static FieldAccessor FAIL = new FieldAccessor(null);

        @SuppressWarnings("unchecked")
        public <T> T get(Object host) {
            try {
                return (T) this.field.get(host);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @SuppressWarnings("unchecked")
        public <T> T getSafely(Object host) {
            try {
                return (T) this.field.get(host);
            } catch (Throwable e) {
                return null;
            }
        }

    }

}
