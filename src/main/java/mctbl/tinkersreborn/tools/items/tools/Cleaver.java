package mctbl.tinkersreborn.tools.items.tools;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.particle.Particles;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.SwordCore;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.tools.modifiers.ModBeheading;

public class Cleaver extends SwordCore {

    public static final float DURABILITY_MODIFIER = 2f;

    public Cleaver() {
        super("Cleaver", 4);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.largeSwordBlade, MaterialStatusType.HEAD, "_cleaver_head"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.toughrod, MaterialStatusType.HANDLE, "_cleaver_handle"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.largeplate, MaterialStatusType.HEAD, "_cleaver_shield"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.toughrod, MaterialStatusType.EXTRA, "_cleaver_guard"));
    }

    @Override
    public float damagePotential() {
        return 1.2f;
    }

    @Override
    public float damageCutoff() {
        return 25f;
    }

    @Override
    public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
        boolean hit = super.dealDamage(stack, player, entity, damage);
        if (hit) {
            TinkersReborn.proxy.spawnAttackParticle(Particles.CLEAVER_ATTACK, player, 0.85d);
        }

        return hit;
    }

    @Override
    public float getRepairModifierForPart(int index) {
        // 2 or 1.5
        return index == 0 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.75f;
    }

    @Override
    public ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ToolNBT data = super.buildToolTag(materials);

        data.attack *= 1.3f;
        data.attack += 3f;

        // triple durability!
        data.durability *= DURABILITY_MODIFIER;

        return data;
    }

    @Override
    public void addMaterialTraits(NBTTagCompound root, List<TinkersRebornMaterial> materials) {
        super.addMaterialTraits(root, materials);

        // beheading "trait", 2 level -> 2 applications
        ModBeheading.CLEAVER_BEHEADING_MOD.apply(root);
        ModBeheading.CLEAVER_BEHEADING_MOD.apply(root);
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(33 - 8, 42 - 10 + 4) // head
                .addSlotPosition(33 - 10 - 14, 42 + 10 + 12) // handle
                .addSlotPosition(33 + 14, 42 - 10 - 2) // plate/shield
                .addSlotPosition(33 + 10 - 10, 42 + 10 + 6); // guard
        }
        return this.toolBuildGuiInfo;
    }
}
