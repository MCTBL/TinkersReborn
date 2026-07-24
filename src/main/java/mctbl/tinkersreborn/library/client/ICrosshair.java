package mctbl.tinkersreborn.library.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ICrosshair {

    ICrosshair DEFAULT = new ICrosshair() {

        @Override
        public void render(float charge, float width, float height, float partialTicks) {
            // do nothing
        }
    };

    void render(float charge, float width, float height, float partialTicks);
}
