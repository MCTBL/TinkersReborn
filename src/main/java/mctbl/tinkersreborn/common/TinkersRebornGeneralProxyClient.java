package mctbl.tinkersreborn.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.client.TinkersRebornFontRender;
import mctbl.tinkersreborn.client.TinkersRebornManualFontRender;
import mctbl.tinkersreborn.common.entity.BlueSlime;
import mctbl.tinkersreborn.common.entity.DryingRackLogic;
import mctbl.tinkersreborn.common.entity.KingBlueSlime;
import mctbl.tinkersreborn.common.events.HealthBarRenderer;
import mctbl.tinkersreborn.common.manuals.TinkersRebornManualDataBase;
import mctbl.tinkersreborn.common.manuals.pages.CoverPage.CoverPageProcessor;
import mctbl.tinkersreborn.common.manuals.pages.ImagePage.ImagePageProcessor;
import mctbl.tinkersreborn.common.manuals.pages.NavigationPage.NavigationPageProcessor;
import mctbl.tinkersreborn.common.manuals.pages.RecipePage.RecipePageProcessor;
import mctbl.tinkersreborn.common.manuals.pages.TextPage.TextPageProcessor;
import mctbl.tinkersreborn.common.manuals.pages.ToolsNavigationPage.ToolsNavigationPageProcessor;
import mctbl.tinkersreborn.common.model.DryingRackRender;
import mctbl.tinkersreborn.common.model.DryingRackSpecialRender;
import mctbl.tinkersreborn.common.model.SlimeRender;
import mctbl.tinkersreborn.library.gui.GuiManual;
import mctbl.tinkersreborn.library.manuals.ManualBookData;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class TinkersRebornGeneralProxyClient extends TinkersRebornGeneralProxyCommon {

    private static final Minecraft mc = Minecraft.getMinecraft();
    public static FontRenderer fontRender = mc.fontRenderer;
    public static FontRenderer manualFontRender = mc.fontRenderer;

    @Override
    public void preInit() {
        TinkersRebornManualDataBase.loadManuals();
        TinkersRebornManualDataBase.registerPageProcessor("text", new TextPageProcessor());
        TinkersRebornManualDataBase.registerPageProcessor("navigation", new NavigationPageProcessor());
        TinkersRebornManualDataBase.registerPageProcessor("cover", new CoverPageProcessor());
        TinkersRebornManualDataBase.registerPageProcessor("recipe", new RecipePageProcessor());
        TinkersRebornManualDataBase.registerPageProcessor("image", new ImagePageProcessor());
        TinkersRebornManualDataBase.registerPageProcessor("toolsNavigation", new ToolsNavigationPageProcessor());
    }

    @Override
    public void init() {
        registerRender();
        RenderingRegistry.registerBlockHandler(new DryingRackRender());
        ClientRegistry.bindTileEntitySpecialRenderer(DryingRackLogic.class, new DryingRackSpecialRender());

        HealthBarRenderer healthBarRenderer = new HealthBarRenderer();
        MinecraftForge.EVENT_BUS.register(healthBarRenderer);
        FMLCommonHandler.instance()
            .bus()
            .register(healthBarRenderer);
    }

    @Override
    public void postInit() {
        TinkersRebornManualDataBase.processManuals();
    }

    @SideOnly(Side.CLIENT)
    void registerRender() {
        IReloadableResourceManager resourceManager = (IReloadableResourceManager) mc.getResourceManager();
        if (!TinkersRebornConfig.isAngelicaLoaded) {
            fontRender = new TinkersRebornFontRender(
                mc.gameSettings,
                new ResourceLocation("textures/font/ascii.png"),
                mc.renderEngine);
            // if (mc.gameSettings.language != null) {
            // fontRender.setUnicodeFlag(
            // mc.getLanguageManager()
            // .isCurrentLocaleUnicode() || mc.gameSettings.forceUnicodeFont);
            // fontRender.setBidiFlag(
            // mc.getLanguageManager()
            // .isCurrentLanguageBidirectional());
            // }
            resourceManager.registerReloadListener(fontRender);
        }

        manualFontRender = new TinkersRebornManualFontRender(
            mc.gameSettings,
            new ResourceLocation("textures/font/ascii.png"),
            mc.renderEngine);
        resourceManager.registerReloadListener(fontRender);

        SlimeRender slimeRender = new SlimeRender(new ModelSlime(16), new ModelSlime(0), 0.25F);

        RenderingRegistry.registerEntityRenderingHandler(BlueSlime.class, slimeRender);
        RenderingRegistry.registerEntityRenderingHandler(KingBlueSlime.class, slimeRender);
    }

    @Override
    public int getDryingRackRenderId() {
        return DryingRackRender.model;
    }

    @Override
    public void openManual(ItemStack stack) {
        String bookName = ToolTagsHelper.getTagSafe(stack)
            .getString("name");
        ManualBookData bookData = TinkersRebornManualDataBase.getBooks()
            .getOrDefault(bookName, null);
        if (bookData == null) {
            TinkersReborn.LOG.error("There's no book data for {}", bookName);
            return;
        }
        Minecraft.getMinecraft()
            .displayGuiScreen(new GuiManual(bookData));
    }

}
