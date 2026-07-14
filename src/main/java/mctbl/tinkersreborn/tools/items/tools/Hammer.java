package mctbl.tinkersreborn.tools.items.tools;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.particle.Particles;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.AoeHarvestTool;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class Hammer extends AoeHarvestTool {

    public static final float DURABILITY_MODIFIER = 2.5f;

    public Hammer() {
        super("Hammer", 4);

        // set the toolclass, actual harvestlevel is done by the overridden callback
        this.setHarvestLevel("pickaxe", 0);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.hammerHead, MaterialStatusType.HEAD, "_hammer_head"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.largeplate, MaterialStatusType.HEAD, "_hammer_front"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.largeplate, MaterialStatusType.HEAD, "_hammer_back"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.toughrod, MaterialStatusType.HANDLE, "_hammer_handle"));
    }

    @Override
    public float miningSpeedModifier() {
        return 0.4f;
    }

    @Override
    public float damagePotential() {
        return 1.2f;
    }

    @Override
    public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
        // bonus damage vs. undead!
        if (entity instanceof EntityLivingBase living
            && living.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
            damage += 3 + TinkersReborn.random.nextInt(4);
        }
        boolean hit = super.dealDamage(stack, player, entity, damage);

        if (hit) {
            TinkersReborn.proxy.spawnAttackParticle(Particles.HAMMER_ATTACK, player, 0.8d);
        }
        return hit;
    }

    @Override
    public List<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
        return ToolTagsHelper.calcAOEBlocks(stack, world, player, origin, 3, 3, 1, -1);
    }

    @Override
    public float getRepairModifierForPart(int index) {
        // head is 2.5x / plate is 1.5x
        return index == 0 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.6f;
    }

    @Override
    public ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ToolNBT toolTag = super.buildToolTag(materials);
        toolTag.durability *= DURABILITY_MODIFIER;
        return toolTag;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(33 + 13 - 2, 42 - 13) // head
                .addSlotPosition(33 + 10 + 16 - 2, 42 - 10 + 16) // plate 1
                .addSlotPosition(33 + 10 - 16 - 2, 42 - 10 - 16) // plate 2
                .addSlotPosition(33 - 10 - 2, 42 + 10); // handle
        }
        return this.toolBuildGuiInfo;
    }
}
