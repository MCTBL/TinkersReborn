package mctbl.tinkersreborn.tools.modifiers;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierNBT;
import mctbl.tinkersreborn.library.tools.modifiers.ModifierTrait;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class ModMendingMoss extends ModifierTrait {

    public static final int MENDING_MOSS_LEVELS = 10;

    private static final String TAG_STORED_XP = "stored_xp";
    private static final String TAG_LAST_HEAL = "heal_timestamp";

    private static final int DELAY = 20 * 7 + 10; // every 7.5s

    public ModMendingMoss() {
        super("mending_moss", 0x43AB32, 3, 0);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
        // only in the hotbar of a player
        if (!world.isRemote && entity instanceof EntityLivingBase) {
            // must be in hotbar or offhand for players
            if (entity instanceof EntityPlayer && !(itemSlot >= 0 && itemSlot < 9)) { // isHotbar
                return;
            }

            // needs ot be repaired and is in hotbar or offhand
            if (needsRepair(tool)) {
                if (useXp(tool, world)) {
                    ToolTagsHelper.healTool(tool, getDurabilityPerXP(tool), (EntityLivingBase) entity);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPickupXp(PlayerPickupXpEvent event) {
        // try mainhand first, then offhand

        ItemStack tool = event.entityPlayer.getHeldItem();
        EntityXPOrb entityXPOrb = event.orb;

        if (!TinkersRebornUtils.isStackEmpty(tool) && isMendingMossModified(tool)) {
            int stored = storeXp(entityXPOrb.xpValue, tool);
            entityXPOrb.xpValue -= stored;
        }

    }

    private boolean isMendingMossModified(ItemStack itemStack) {
        return ToolTagsHelper.hasModifier(itemStack, getIdentifier());
    }

    private boolean needsRepair(ItemStack itemStack) {
        return !TinkersRebornUtils.isStackEmpty(itemStack) && itemStack.getItemDamage() > 0
            && !ToolTagsHelper.isBroken(itemStack);
    }

    private int getDurabilityPerXP(ItemStack itemStack) {
        return 2 + ModifierNBT.readTag(ToolTagsHelper.getModifierTag(itemStack, getIdentifier()), Data.class).level;
    }

    // 100 * 3^(level-1)
    private int getMaxXp(int level) {
        if (level <= 1) {
            return 100;
        }

        return getMaxXp(level - 1) * 3;
    }

    private boolean canStoreXp(Data data) {
        return data.storedXp < getMaxXp(data.level);
    }

    private int storeXp(int amount, ItemStack itemStack) {
        NBTTagCompound modifierTag = ToolTagsHelper.getModifierTag(itemStack, getIdentifier());
        Data data = ModifierNBT.readTag(modifierTag, Data.class);

        int change = 0;
        if (canStoreXp(data)) {
            int max = getMaxXp(data.level);
            change = Math.min(amount, max - data.storedXp);
            data.storedXp += change;
            data.write(modifierTag);
        }
        return change;
    }

    private boolean useXp(ItemStack itemStack, World world) {
        NBTTagCompound modifierTag = ToolTagsHelper.getModifierTag(itemStack, getIdentifier());
        Data data = ModifierNBT.readTag(modifierTag, Data.class);

        if (data.storedXp > 0 && world.getTotalWorldTime() - data.lastHeal > DELAY) {
            data.storedXp--;
            data.lastHeal = world.getTotalWorldTime();
            data.write(modifierTag);
            return true;
        }
        return false;
    }

    @Override
    public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
        Data data = ModifierNBT.readTag(modifierTag, Data.class);
        assert data != null;
        String loc = TinkersRebornUtils.translate(String.format(LOC_Extra, getIdentifier()));
        return ImmutableList.of(String.format(loc, data.storedXp));
    }

    @Override
    public void convertTConstractNBT(ItemStack tool, NBTTagCompound tcToolNBTCompound) {
        if (tcToolNBTCompound.hasKey("Moss")) {
            apply(tool);
        }
    }

    public static class Data extends ModifierNBT {

        public int storedXp;
        public long lastHeal;

        @Override
        public void read(NBTTagCompound tag) {
            super.read(tag);
            storedXp = tag.getInteger(TAG_STORED_XP);
            lastHeal = tag.getLong(TAG_LAST_HEAL);
        }

        @Override
        public void write(NBTTagCompound tag) {
            super.write(tag);
            tag.setInteger(TAG_STORED_XP, storedXp);
            tag.setLong(TAG_LAST_HEAL, lastHeal);
        }
    }
}
