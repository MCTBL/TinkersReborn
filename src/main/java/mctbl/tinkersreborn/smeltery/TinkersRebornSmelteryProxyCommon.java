package mctbl.tinkersreborn.smeltery;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import mctbl.tinkersreborn.CommonProxy;
import mctbl.tinkersreborn.library.entity.TinkersRebornInventoryLogic;

public class TinkersRebornSmelteryProxyCommon implements IGuiHandler {

    public static final int smelteryGuiID = 6;

    public void initialize() {
        registerGuiHandler();
    }

    protected void registerGuiHandler() {
        CommonProxy.registerServerGuiHandler(smelteryGuiID, this);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TinkersRebornInventoryLogic l) {
            return l.getGuiContainer(player.inventory, world, x, y, z);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

}
