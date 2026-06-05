package mctbl.tinkersreborn.tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.client.registry.RenderingRegistry;
import mctbl.tinkersreborn.ClientProxy;
import mctbl.tinkersreborn.tools.entity.CastChestLogic;
import mctbl.tinkersreborn.tools.entity.CraftingStationLogic;
import mctbl.tinkersreborn.tools.entity.PartChestLogic;
import mctbl.tinkersreborn.tools.entity.TinkersRebornPartBuilderLogic;
import mctbl.tinkersreborn.tools.entity.TinkersRebornToolForgeLogic;
import mctbl.tinkersreborn.tools.entity.TinkersRebornToolStationLogic;
import mctbl.tinkersreborn.tools.gui.GuiCastChest;
import mctbl.tinkersreborn.tools.gui.GuiCraftingStation;
import mctbl.tinkersreborn.tools.gui.GuiPartBuilder;
import mctbl.tinkersreborn.tools.gui.GuiPartChest;
import mctbl.tinkersreborn.tools.gui.GuiToolForge;
import mctbl.tinkersreborn.tools.gui.GuiToolStation;
import mctbl.tinkersreborn.tools.model.ChestRender;
import mctbl.tinkersreborn.tools.model.TableRender;
import mctbl.tinkersreborn.tools.model.ToolRender;

public class TinkersRebornToolsProxyClient extends TinkersRebornToolsProxyCommon {

    @Override
    public void initialize() {
        MinecraftForge.EVENT_BUS.register(this);
        this.registerRenderer();
        this.registerGuiHandler();
    }

    protected void registerRenderer() {
        RenderingRegistry.registerBlockHandler(new TableRender());
        RenderingRegistry.registerBlockHandler(new ChestRender());

        ToolRender render = new ToolRender();
        MinecraftForgeClient.registerItemRenderer(TinkersRebornTools.pickaxe, render);
    }

    @Override
    protected void registerGuiHandler() {
        super.registerGuiHandler();
        ClientProxy.registerClientGuiHandler(toolStationID, this);
        ClientProxy.registerClientGuiHandler(partBuilderID, this);
        ClientProxy.registerClientGuiHandler(castChestID, this);
        ClientProxy.registerClientGuiHandler(partChestID, this);
        ClientProxy.registerClientGuiHandler(toolForgeID, this);
        ClientProxy.registerClientGuiHandler(craftingStationID, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (ID == toolStationID && te instanceof TinkersRebornToolStationLogic l) {
            return new GuiToolStation(player.inventory, l, world, x, y, z);
        } else if (ID == partBuilderID && te instanceof TinkersRebornPartBuilderLogic l) {
            return new GuiPartBuilder(player.inventory, l, world, x, y, z);
        } else if (ID == partChestID && te instanceof PartChestLogic l) {
            return new GuiPartChest(player.inventory, l, world, x, y, z);
        } else if (ID == castChestID && te instanceof CastChestLogic l) {
            return new GuiCastChest(player.inventory, l, world, x, y, z);
        } else if (ID == toolForgeID && te instanceof TinkersRebornToolForgeLogic l) {
            return new GuiToolForge(player.inventory, l, world, x, y, z);
        } else if (ID == craftingStationID && te instanceof CraftingStationLogic l) {
            return new GuiCraftingStation(player.inventory, l, world, x, y, z);
        }
        return super.getClientGuiElement(ID, player, world, x, y, z);
    }

}
