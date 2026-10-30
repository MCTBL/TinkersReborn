package mctbl.tinkersreborn.library.entity;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import mctbl.tinkersreborn.common.network.TinkerEntityPropertiesSyncPacket;
import mctbl.tinkersreborn.common.network.TinkerNetwork;

public class TinkersEntityProperties implements IExtendedEntityProperties {

    public static final String IDENTIFIER = "tinkersentityproperties";

    private Map<String, Effect> typeTicksMap;
    private EntityLivingBase owner;

    public TinkersEntityProperties(EntityLivingBase living) {
        this.typeTicksMap = new HashMap<>();
        this.owner = living;
    }

    public void apply(String identifier, int ticks, int level) {
        Effect effect = this.typeTicksMap.computeIfAbsent(identifier, k -> new Effect(0, 0));
        int oldTicks = effect.getRemainingTicks();
        int oldLevel = effect.getLevel();
        effect.apply(ticks, level);
        // Synchronize only when an effect starts or its duration/level changes.
        if (oldTicks != effect.getRemainingTicks() || oldLevel != effect.getLevel()) {
            syncOwner();
        }
    }

    public void remove(String identifier) {
        if (this.typeTicksMap.remove(identifier) != null) {
            syncOwner();
        }
    }

    public void tick() {
        boolean expired = false;
        for (Effect effect : this.typeTicksMap.values()) {
            boolean active = effect.isActive();
            effect.tick();
            if (active && !effect.isActive()) expired = true;
        }
        if (expired) {
            typeTicksMap.entrySet()
                .removeIf(
                    entry -> !entry.getValue()
                        .isActive());
            syncOwner();
        }
    }

    public boolean isActive(String identifier) {
        return this.typeTicksMap.getOrDefault(identifier, Effect.getEmptyEffect())
            .isActive();
    }

    public int getRemainingTicks(String identifier) {
        return this.typeTicksMap.getOrDefault(identifier, Effect.getEmptyEffect())
            .getRemainingTicks();
    }

    public int getLevel(String identifier) {
        return this.typeTicksMap.getOrDefault(identifier, Effect.getEmptyEffect())
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

    public Map<String, Effect> getEffects() {
        return typeTicksMap;
    }

    public void replaceEffects(Map<String, Effect> effects) {
        typeTicksMap.clear();
        typeTicksMap.putAll(effects);
    }

    private void syncOwner() {
        if (owner instanceof EntityPlayerMP player && !owner.worldObj.isRemote) {
            TinkerNetwork.sendTo(new TinkerEntityPropertiesSyncPacket(player, this), player);
        }
    }

    public static class Effect {

        private static final Effect clearInstance = new Effect(0, 0);

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

        public static Effect getEmptyEffect() {
            return clearInstance;
        }
    }
}
