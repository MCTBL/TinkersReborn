package mctbl.tinkersreborn.plugins.waila;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import mctbl.tinkersreborn.smeltery.entity.SmelteryLogic;
import mctbl.tinkersreborn.util.TinkersStr;

public class SmelteryDataProvider implements IWailaDataProvider {

    public static final String CONFIG_KEY = "tinkersreborn.smeltery";

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof SmelteryLogic te && config.getConfig(CONFIG_KEY, true)) {
            if (te.validStructure) {
                List<FluidStack> fls = te.moltenMetal;
                if (fls.isEmpty()) {
                    currenttip.add(SpecialChars.ITALIC + TinkersStr.wailaEmpty.toString());
                } else {
                    for (FluidStack st : fls) {
                        currenttip.add(st.getLocalizedName() + " (" + st.amount + "mB)");
                    }
                }
            } else {
                currenttip.add(SpecialChars.ITALIC + TinkersStr.wailaInvalidstructure.toString());
            }
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
        int y, int z) {
        return tag;
    }
}
