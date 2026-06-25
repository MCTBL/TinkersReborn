package mctbl.tinkersreborn.library.entity;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class TinkersEntityProperties implements IExtendedEntityProperties {

    public static final String IDENTIFIER = "tinkersentityproperties";

    private Map<String, Effect> typeTicksMap;

    public TinkersEntityProperties() {
        this.typeTicksMap = new HashMap<>();
    }

    public void apply(String identifier, int ticks, int level) {
        this.typeTicksMap.computeIfAbsent(identifier, k -> new Effect(0, 0))
            .apply(ticks, level);
    }

    public void remove(String identifier) {
        this.typeTicksMap.remove(identifier);
    }

    public void tick() {
        this.typeTicksMap.values()
            .forEach(e -> e.tick());
    }

    public boolean isActive(String identifier) {
        return this.typeTicksMap.computeIfAbsent(identifier, k -> new Effect(0, 0))
            .isActive();
    }

    public int getRemainingTicks(String identifier) {
        return this.typeTicksMap.computeIfAbsent(identifier, k -> new Effect(0, 0))
            .getRemainingTicks();
    }

    public int getLevel(String identifier) {
        return this.typeTicksMap.computeIfAbsent(identifier, k -> new Effect(0, 0))
            .getLevel();
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {}

    @Override
    public void loadNBTData(NBTTagCompound compound) {}

    public static TinkersEntityProperties getProps(EntityLivingBase entity) {
        return (TinkersEntityProperties) entity.getExtendedProperties(TinkersEntityProperties.IDENTIFIER);
    }

    @Override
    public void init(Entity entity, World world) {}

    public static class Effect {

        private int remainingTicks;
        private int level;

        /**
         * @param remainingTicks
         * @param level
         */
        public Effect(int remainingTicks, int level) {
            this.remainingTicks = remainingTicks;
            this.level = level;
        }

        public int getRemainingTicks() {
            return remainingTicks;
        }

        public int getLevel() {
            return level;
        }

        public void tick() {
            if (this.remainingTicks > 0) {
                this.remainingTicks--;
            }
            if (this.remainingTicks <= 0) {
                this.level = 0;
            }
        }

        public boolean isActive() {
            return this.remainingTicks > 0;
        }

        public void apply(int ticks, int level) {
            if (level < this.level) {
                // level II won't cover by level I
                return;
            }
            this.remainingTicks = Math.max(this.remainingTicks, ticks);
            this.level = level;
        }
    }
}
