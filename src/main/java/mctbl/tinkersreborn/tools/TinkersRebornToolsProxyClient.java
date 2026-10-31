package mctbl.tinkersreborn.tools;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mctbl.tinkersreborn.common.manuals.TinkersRebornManualDataBase;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;
import mctbl.tinkersreborn.library.tools.BowCore;
import mctbl.tinkersreborn.library.tools.ShieldCore;
import mctbl.tinkersreborn.tools.entity.EntityArrow;
import mctbl.tinkersreborn.tools.entity.EntityBolt;
import mctbl.tinkersreborn.tools.events.TinkersRebornProjectileAndToolsRenderEvents;
import mctbl.tinkersreborn.tools.items.tools.CrossBow;
import mctbl.tinkersreborn.tools.model.BowRenderer;
import mctbl.tinkersreborn.tools.model.ChestRender;
import mctbl.tinkersreborn.tools.model.CrossBowRender;
import mctbl.tinkersreborn.tools.model.EntityArrowRenderer;
import mctbl.tinkersreborn.tools.model.ShieldRender;
import mctbl.tinkersreborn.tools.model.TableRender;
import mctbl.tinkersreborn.tools.model.ToolRender;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

public class TinkersRebornToolsProxyClient extends TinkersRebornToolsProxyCommon {

    @Override
    public void init() {
        this.registerRenderer();
    }

    @Override
    public void postInit() {
        TinkersRebornManualDataBase.processManuals();
    }

    protected void registerRenderer() {
        TableRender tableRender = new TableRender();
        RenderingRegistry.registerBlockHandler(tableRender);
        ClientRegistry.bindTileEntitySpecialRenderer(TinkersRebornInventoryLogic.class, tableRender);
        RenderingRegistry.registerBlockHandler(new ChestRender());

        ToolRender render = new ToolRender();
        TinkersRebornRegistry.getAllTools().forEach(t -> {
            if (t instanceof CrossBow) {
                MinecraftForgeClient.registerItemRenderer(t, new CrossBowRender());
            } else if (t instanceof BowCore) {
                MinecraftForgeClient.registerItemRenderer(t, new BowRenderer());
            } else if (t instanceof ShieldCore) {
                MinecraftForgeClient.registerItemRenderer(t, new ShieldRender());
            }  else {
                MinecraftForgeClient.registerItemRenderer(t, render);
            }
        });

        // EntityRegistry.registerModEntity(EntityArrow.class, "arrow", 10, TinkersReborn.instance, 64, 1, false);
        // EntityRegistry.registerModEntity(EntityBolt.class, "bolt", 11, TinkersReborn.instance, 64, 1, false);
        // EntityRegistry.registerModEntity(EntityShuriken.class, "shuriken", 12, TinkersReborn.instance, 64, 1, false);
        EntityArrowRenderer arrowRenderer = new EntityArrowRenderer();
        RenderingRegistry.registerEntityRenderingHandler(EntityArrow.class, arrowRenderer);
        RenderingRegistry.registerEntityRenderingHandler(EntityBolt.class, arrowRenderer);

        MinecraftForge.EVENT_BUS.register(new TinkersRebornProjectileAndToolsRenderEvents());
    }

    @Override
    public int getTableRenderId() {
        return TableRender.model;
    }

    @Override
    public int getChestRenderId() {
        return ChestRender.model;
    }
}
