package mctbl.tinkersreborn.tools;

import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;
import mctbl.tinkersreborn.library.tools.BowCore;
import mctbl.tinkersreborn.tools.entity.EntityArrow;
import mctbl.tinkersreborn.tools.model.BowRenderer;
import mctbl.tinkersreborn.tools.model.ChestRender;
import mctbl.tinkersreborn.tools.model.EntityArrowRenderer;
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
        BowRenderer bowRender = new BowRenderer();
        TinkersRebornRegistry.getAllTools()
            .forEach(t -> MinecraftForgeClient.registerItemRenderer(t, (t instanceof BowCore) ? bowRender : render));

        EntityRegistry.registerModEntity(EntityArrow.class, "arrow", 10, TinkersReborn.instance, 64, 1, false);
        // EntityRegistry.registerModEntity(EntityBolt.class, "bolt", 11, TinkersReborn.instance, 64, 1, false);
        // EntityRegistry.registerModEntity(EntityShuriken.class, "shuriken", 12, TinkersReborn.instance, 64, 1, false);
        RenderingRegistry.registerEntityRenderingHandler(EntityArrow.class, new EntityArrowRenderer());
    }
}
