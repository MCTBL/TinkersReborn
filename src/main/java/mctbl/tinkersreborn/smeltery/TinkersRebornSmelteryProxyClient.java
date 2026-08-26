package mctbl.tinkersreborn.smeltery;

import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mctbl.tinkersreborn.smeltery.entity.CastingBasinLogic;
import mctbl.tinkersreborn.smeltery.entity.CastingTableLogic;
import mctbl.tinkersreborn.smeltery.model.BlockRenderCastingChannel;
import mctbl.tinkersreborn.smeltery.model.CastingBasinSpecialRender;
import mctbl.tinkersreborn.smeltery.model.CastingBlockRender;
import mctbl.tinkersreborn.smeltery.model.CastingTableSpecialRenderer;
import mctbl.tinkersreborn.smeltery.model.SmelteryRender;
import mctbl.tinkersreborn.smeltery.model.TankItemRenderer;
import mctbl.tinkersreborn.smeltery.model.TankRender;

public class TinkersRebornSmelteryProxyClient extends TinkersRebornSmelteryProxyCommon {

    @Override
    public void initialize() {
        registerRenderer();
    }

    void registerRenderer() {
        RenderingRegistry.registerBlockHandler(new TankRender());
        RenderingRegistry.registerBlockHandler(new SmelteryRender());
        RenderingRegistry.registerBlockHandler(new CastingBlockRender());

        // RenderingRegistry.registerBlockHandler(new PaneRender());
        // RenderingRegistry.registerBlockHandler(new PaneConnectedRender());
        // RenderingRegistry.registerBlockHandler(new RenderBlockFluid());
        RenderingRegistry.registerBlockHandler(new BlockRenderCastingChannel());

        ClientRegistry.bindTileEntitySpecialRenderer(CastingTableLogic.class, new CastingTableSpecialRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(CastingBasinLogic.class, new CastingBasinSpecialRender());

        IItemRenderer tankItemRenderer = new TankItemRenderer();
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(TinkersRebornSmeltery.lavaTank), tankItemRenderer);
    }

    @Override
    public int getSmelteryRender() {
        return SmelteryRender.smelteryModel;
    }

    @Override
    public int getTankRender() {
        return TankRender.tankModelID;
    }

    @Override
    public int getCastingBlockRender() {
        return CastingBlockRender.searedModel;
    }

    @Override
    public int getCastingChannelRender() {
        return BlockRenderCastingChannel.renderID;
    }
}
