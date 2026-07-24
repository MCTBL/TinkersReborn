package mctbl.tinkersreborn.tools.items.tools;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.ProjectileLauncherNBT;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.tools.materials.BowMaterialStats;
import mctbl.tinkersreborn.tools.materials.ExtraMaterialStats;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.tools.materials.StringMaterialStats;

public class LongBow extends ShortBow {

    // little more durability due to the plate
    public static final float DURABILITY_MODIFIER = 1.4f;

    public LongBow() {
        super("LongBow", 4);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.bowString, MaterialStatusType.STRING, "_bowstring"));
        this.componentsParts.add(new ToolPartRecord(TinkersRebornTools.bowLimb, MaterialStatusType.BOW, "_bow_top"));
        this.componentsParts.add(new ToolPartRecord(TinkersRebornTools.bowLimb, MaterialStatusType.BOW, "_bow_bottom"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.largeplate, MaterialStatusType.EXTRA, "_bow_grip"));
    }

    @Override
    public float baseProjectileDamage() {
        return 2.5f;
    }

    @Override
    protected float baseProjectileSpeed() {
        return 5.5f;
    }

    @Override
    protected float baseInaccuracy() {
        return 0.8f;
    }

    @Override
    public float projectileDamageModifier() {
        return 1.25f;
    }

    @Override
    public int getDrawTime() {
        return 30;
    }

    @Override
    public float getZoomLevel() {
        return 0.4F;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        // no speedup on charging
        onUpdateTraits(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public ProjectileLauncherNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ProjectileLauncherNBT data = new ProjectileLauncherNBT();
        HeadMaterialStats head1 = materials.get(1)
            .getStats(MaterialStatusType.HEAD);
        HeadMaterialStats head2 = materials.get(2)
            .getStats(MaterialStatusType.HEAD);
        BowMaterialStats limb1 = materials.get(1)
            .getStats(MaterialStatusType.BOW);
        BowMaterialStats limb2 = materials.get(2)
            .getStats(MaterialStatusType.BOW);
        StringMaterialStats bowstring = materials.get(0)
            .getStats(MaterialStatusType.STRING);
        ExtraMaterialStats grip = materials.get(3)
            .getStats(MaterialStatusType.EXTRA);

        data.head(head1, head2);
        data.limb(limb1, limb2);
        data.extra(grip);
        data.bowstring(bowstring);

        data.durability *= DURABILITY_MODIFIER;

        return data;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(32 + 6, 41 + 6) // center bowstring
                .addSlotPosition(32 + 12, 41 - 22) // top limb
                .addSlotPosition(32 - 22, 41 + 12) // left limb
                .addSlotPosition(32 - 15, 41 - 15); // grip
        }
        return this.toolBuildGuiInfo;
    }
}
