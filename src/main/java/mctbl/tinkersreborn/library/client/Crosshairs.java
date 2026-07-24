package mctbl.tinkersreborn.library.client;

import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;

@SideOnly(Side.CLIENT)
public interface Crosshairs {

    Crosshair SQUARE = new Crosshair(new ResourceLocation(TinkersReborn.MODID, "textures/gui/crosshair/square.png"));
    // Crosshair X = new CrosshairTriangle(new ResourceLocation("", "textures/gui/crosshair/x.png"));
    // Crosshair INVERSE = new CrosshairTriangle(new ResourceLocation("", "textures/gui/crosshair/inverse.png"));
    // Crosshair PLUS = new Crosshair(new ResourceLocation("", "textures/gui/crosshair/plus.png"));
    Crosshair T = new CrosshairT(new ResourceLocation(TinkersReborn.MODID, "textures/gui/crosshair/t.png"), 15);
}
