package mctbl.tinkersreborn.tools.gui;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.entity.ToolForgeLogic;

@SideOnly(Side.CLIENT)
public class GuiToolForge extends GuiToolStation {

    public GuiToolForge(InventoryPlayer playerInv, World world, BlockPos pos, ToolForgeLogic tile) {
        super(playerInv, world, pos, tile);

        metal();
    }

    @Override
    public List<ToolCore> getBuildableItems() {
        return TinkersRebornRegistry.getToolForgeCraftingList();
    }
}
