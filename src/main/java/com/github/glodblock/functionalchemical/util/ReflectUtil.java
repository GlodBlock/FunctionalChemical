package com.github.glodblock.functionalchemical.util;

import com.glodblock.github.glodium.reflect.ReflectKit;
import mekanism.api.chemical.merged.ChemicalTankWrapper;

import java.lang.reflect.Field;

public class ReflectUtil {

    public static final FieldAccessor INTERNAL_TANK = get(ChemicalTankWrapper.class, "internal");

    private static FieldAccessor get(Class<?> clazz, String fieldName) {
        try {
            return new FieldAccessor(ReflectKit.reflectField(clazz, fieldName));
        } catch (Throwable e) {
            return FieldAccessor.FAIL;
        }
    }

    public record FieldAccessor(Field field) {

        static FieldAccessor FAIL = new FieldAccessor(null);

        public <T> T get(Object host) {
            return ReflectKit.readField(host, this.field);
        }

    }

}
