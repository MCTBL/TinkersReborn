package mctbl.tinkersreborn.tools.items.tools;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import mctbl.tinkersreborn.library.entity.EntityProjectileBase;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.AmmoCore;
import mctbl.tinkersreborn.library.tools.ProjectileNBT;
import mctbl.tinkersreborn.tools.Category;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.entity.EntityArrow;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.tools.materials.FletchingMaterialStats;
import mctbl.tinkersreborn.tools.materials.HeadMaterialStats;
import mctbl.tinkersreborn.tools.materials.ShaftMaterialStats;

public class Arrow extends AmmoCore {

    public Arrow() {
        super("Arrow", 3);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.arrowShaft, MaterialStatusType.SHAFT, "_arrow_shaft"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.arrowhead, MaterialStatusType.HEAD, "_arrow_head"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.fletching, MaterialStatusType.FLETCHING, "_arrow_fletching"));

        addCategory(Category.NO_MELEE, Category.PROJECTILE);
    }

    @Override
    public EntityProjectileBase getProjectile(ItemStack stack, ItemStack launcher, World world, EntityPlayer player,
        float speed, float inaccuracy, float power, boolean usedAmmo) {
        inaccuracy -= (1f - 1f / ProjectileNBT.from(stack).accuracy) * speed / 2f;
        return new EntityArrow(
            world,
            player,
            speed,
            inaccuracy,
            power,
            getProjectileStack(stack, world, player, usedAmmo),
            launcher);
    }

    @Override
    public float damagePotential() {
        return 1f;
    }

    @Override
    public ProjectileNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ProjectileNBT data = new ProjectileNBT();

        ShaftMaterialStats shaft = materials.get(0)
            .getStats(MaterialStatusType.SHAFT);
        HeadMaterialStats head = materials.get(1)
            .getStats(MaterialStatusType.HEAD);
        FletchingMaterialStats fletching = materials.get(2)
            .getStats(MaterialStatusType.FLETCHING);

        data.head(head);
        data.fletchings(fletching);
        data.shafts(this, shaft);

        data.attack += 2;

        return data;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(32, 41) // shaft
                .addSlotPosition(32 + 18, 41 - 18) // head
                .addSlotPosition(32 - 18, 41 + 18); // fletching
        }
        return this.toolBuildGuiInfo;
    }

}
