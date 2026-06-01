package mctbl.tinkersreborn.smeltery;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.RenderingRegistry;
import mctbl.tinkersreborn.CommonProxy;
import mctbl.tinkersreborn.smeltery.model.SmelteryRender;
import mctbl.tinkersreborn.smeltery.model.TankItemRenderer;
import mctbl.tinkersreborn.smeltery.model.TankRender;

public class TinkersRebornSmelteryProxyClient extends TinkersRebornSmelteryProxyCommon {

    @Override
    public void initialize() {
        registerRenderer();
        registerGuiHandler();
    }

    void registerRenderer() {
        RenderingRegistry.registerBlockHandler(new TankRender());
        RenderingRegistry.registerBlockHandler(new SmelteryRender());

        // RenderingRegistry.registerBlockHandler(new CastingBlockRender());
        // RenderingRegistry.registerBlockHandler(new DryingRackRender());
        // RenderingRegistry.registerBlockHandler(new PaneRender());
        // RenderingRegistry.registerBlockHandler(new PaneConnectedRender());
        // RenderingRegistry.registerBlockHandler(new RenderBlockFluid());
        // RenderingRegistry.registerBlockHandler(new BlockRenderCastingChannel());

        // ClientRegistry.bindTileEntitySpecialRenderer(CastingTableLogic.class, new CastingTableSpecialRenderer());
        // ClientRegistry.bindTileEntitySpecialRenderer(CastingBasinLogic.class, new CastingBasinSpecialRender());
        // ClientRegistry.bindTileEntitySpecialRenderer(DryingRackLogic.class, new DryingRackSpecialRender());

        IItemRenderer tankItemRenderer = new TankItemRenderer();
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(TinkersRebornSmeltery.lavaTank), tankItemRenderer);
    }

    @Override
    protected void registerGuiHandler() {
        super.registerGuiHandler();
        CommonProxy.registerClientGuiHandler(smelteryGuiID, this);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        // if (ID == SmelteryProxyCommon.smelteryGuiID) {
        // return new SmelteryGui(player.inventory, (SmelteryLogic) world.getTileEntity(x, y, z), world, x, y, z);
        // }
        return null;
    }
}
