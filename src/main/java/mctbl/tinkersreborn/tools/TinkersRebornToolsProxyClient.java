package mctbl.tinkersreborn.tools;

import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.RenderingRegistry;
import mctbl.tinkersreborn.tools.model.ChestRender;
import mctbl.tinkersreborn.tools.model.TableRender;
import mctbl.tinkersreborn.tools.model.ToolRender;

public class TinkersRebornToolsProxyClient extends TinkersRebornToolsProxyCommon {

    @Override
    public void initialize() {
        this.registerRenderer();
    }

    protected void registerRenderer() {
        RenderingRegistry.registerBlockHandler(new TableRender());
        RenderingRegistry.registerBlockHandler(new ChestRender());

        ToolRender render = new ToolRender();
        MinecraftForgeClient.registerItemRenderer(TinkersRebornTools.pickaxe, render);
    }
}
