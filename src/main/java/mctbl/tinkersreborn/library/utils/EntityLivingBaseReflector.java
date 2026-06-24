package mctbl.tinkersreborn.library.utils;

import java.lang.reflect.Field;

import net.minecraft.entity.EntityLivingBase;

import mctbl.tinkersreborn.TinkersReborn;

public class EntityLivingBaseReflector {

    private static Class<?> clz = EntityLivingBase.class;
    private static Field lastDamage = null;

    public static float getLastDamage(EntityLivingBase obj) {
        try {
            if (lastDamage == null) initField();

            return lastDamage.getFloat(obj);
        } catch (Exception e) {
            TinkersReborn.LOG.error("EntityLivingBaseReflector get error {}", e.getMessage());
            return 0;
        }
    }

    public static void setLastDamage(EntityLivingBase obj, float newDamage) {
        try {
            if (lastDamage == null) initField();

            lastDamage.set(obj, newDamage);
        } catch (Exception e) {
            TinkersReborn.LOG.error("EntityLivingBaseReflector get error {}", e.getMessage());
        }
    }

    private static void initField() throws NoSuchFieldException {
        lastDamage = clz.getDeclaredField("lastDamage");
        lastDamage.setAccessible(true);
    }
}
