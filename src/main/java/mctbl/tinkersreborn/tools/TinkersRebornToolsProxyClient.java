package mctbl.tinkersreborn.tools;

import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;
import mctbl.tinkersreborn.tools.model.ChestRender;
import mctbl.tinkersreborn.tools.model.TableRender;
import mctbl.tinkersreborn.tools.model.ToolRender;

public class TinkersRebornToolsProxyClient extends TinkersRebornToolsProxyCommon {

    @Override
    public void initialize() {
        this.registerRenderer();
    }

    protected void registerRenderer() {
        TableRender tableRender = new TableRender();
        RenderingRegistry.registerBlockHandler(tableRender);
        ClientRegistry.bindTileEntitySpecialRenderer(TinkersRebornInventoryLogic.class, tableRender);
        RenderingRegistry.registerBlockHandler(new ChestRender());

        ToolRender render = new ToolRender();
        TinkersRebornRegistry.getAllTools()
            .forEach(t -> MinecraftForgeClient.registerItemRenderer(t, render));
    }
}
