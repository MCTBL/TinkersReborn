package mctbl.tinkersreborn.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

public class TinkersRebornManualFontRender extends TinkersRebornFontRender {

    public TinkersRebornManualFontRender(GameSettings gameSettingsIn, ResourceLocation location,
        TextureManager textureManagerIn) {
        super(gameSettingsIn, location, textureManagerIn);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        super.onResourceManagerReload(resourceManager);
        setUnicodeFlag(true);
        setBidiFlag(
            Minecraft.getMinecraft()
                .getLanguageManager()
                .isCurrentLanguageBidirectional());
    }

}
