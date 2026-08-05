package mctbl.tinkersreborn.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.client.TinkersRebornFontRender;
import mctbl.tinkersreborn.common.entity.DryingRackLogic;
import mctbl.tinkersreborn.common.model.DryingRackRender;
import mctbl.tinkersreborn.common.model.DryingRackSpecialRender;

public class TinkersRebornGeneralProxyClient extends TinkersRebornGeneralProxyCommon {

    private static final Minecraft mc = Minecraft.getMinecraft();
    public static TinkersRebornFontRender fontRender;

    @Override
    public void init() {
        registerRender();
        RenderingRegistry.registerBlockHandler(new DryingRackRender());
        ClientRegistry.bindTileEntitySpecialRenderer(DryingRackLogic.class, new DryingRackSpecialRender());
    }

    @SideOnly(Side.CLIENT)
    void registerRender() {
        IReloadableResourceManager resourceManager = (IReloadableResourceManager) mc.getResourceManager();

        fontRender = new TinkersRebornFontRender(
            mc.gameSettings,
            new ResourceLocation("textures/font/ascii.png"),
            mc.renderEngine);

        if (mc.gameSettings.language != null) {
            fontRender.setUnicodeFlag(
                mc.getLanguageManager()
                    .isCurrentLocaleUnicode() || mc.gameSettings.forceUnicodeFont);
            fontRender.setBidiFlag(
                mc.getLanguageManager()
                    .isCurrentLanguageBidirectional());
        }
        resourceManager.registerReloadListener(fontRender);
    }
}
