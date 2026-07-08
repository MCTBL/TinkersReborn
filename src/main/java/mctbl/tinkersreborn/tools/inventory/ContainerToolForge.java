package mctbl.tinkersreborn.tools.inventory;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.event.Sounds;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.tools.entity.ToolStationLogic;

public class ContainerToolForge extends ContainerToolStation {

    public ContainerToolForge(InventoryPlayer playerInventory, ToolStationLogic tile) {
        super(playerInventory, tile);
    }

    @Override
    public List<ToolCore> getBuildableTools() {
        return TinkersRebornRegistry.getToolForgeCraftingList();
    }

    @Override
    protected void playCraftSound(EntityPlayer player) {
        Sounds.playSoundForAll(player, "block.anvil.use", 0.9f, 0.95f + 0.2f * TinkersReborn.random.nextFloat());
    }
}
