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
import mctbl.tinkersreborn.smeltery.entity.LavaTankLogic;
import mctbl.tinkersreborn.util.TinkersStr;

public class SearedTankDataProvider implements IWailaDataProvider {

    public static final String CONFIG_KEY = "tinkersreborn.lavatank";

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
        if (accessor.getTileEntity() instanceof LavaTankLogic te && config.getConfig(CONFIG_KEY, true)) {
            if (te.containsFluid()) {
                FluidStack fs = te.tank.getFluid();
                currenttip.add(TinkersStr.wailaLiquidtag.toString() + fs.getLocalizedName());

                currenttip.add(TinkersStr.wailaAmounttag.toString() + fs.amount + "/" + te.tank.getCapacity());
            } else {
                currenttip.add(SpecialChars.ITALIC + TinkersStr.wailaEmpty.toString());
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
