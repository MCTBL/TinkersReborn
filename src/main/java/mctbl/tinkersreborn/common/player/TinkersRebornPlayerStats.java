package mctbl.tinkersreborn.common.player;

import java.lang.ref.WeakReference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class TinkersRebornPlayerStats implements IExtendedEntityProperties {

    public static final String PROP_NAME = "TinkersReborn";

    public WeakReference<EntityPlayer> player;

    public int bonusHealth;

    public PlayerHeartCanisterExtended heartCanister;

    public TinkersRebornPlayerStats() {
        this.heartCanister = new PlayerHeartCanisterExtended();
    }

    public TinkersRebornPlayerStats(EntityPlayer entityplayer) {
        this.player = new WeakReference<>(entityplayer);
        this.heartCanister = new PlayerHeartCanisterExtended().init(entityplayer);;
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tTag = new NBTTagCompound();
        heartCanister.saveToNBT(tTag);
        compound.setTag(PROP_NAME, tTag);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound properties = (NBTTagCompound) compound.getTag(PROP_NAME);
        if (properties != null) {
            this.heartCanister.readFromNBT(properties);
        }
    }

    @Override
    public void init(Entity entity, World world) {
        this.player = new WeakReference<>((EntityPlayer) entity);
        this.heartCanister.init((EntityPlayer) entity);
    }

    public static void register(EntityPlayer player) {
        player.registerExtendedProperties(PROP_NAME, new TinkersRebornPlayerStats(player));
    }

    public static TinkersRebornPlayerStats get(EntityPlayer player) {
        return (TinkersRebornPlayerStats) player.getExtendedProperties(PROP_NAME);
    }
}
