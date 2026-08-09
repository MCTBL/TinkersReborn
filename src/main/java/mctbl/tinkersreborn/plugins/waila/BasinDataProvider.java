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
import mctbl.tinkersreborn.smeltery.entity.CastingBasinLogic;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

public class BasinDataProvider implements IWailaDataProvider {

    public static final String CONFIG_KEY = "tinkersreborn.basin";

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof CastingBasinLogic te) {
            return te.getStackInSlot(0);
        }
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
        if (accessor.getTileEntity() instanceof CastingBasinLogic te && config.getConfig(CONFIG_KEY, true)) {
            if (te.getFluidAmount() != 0) {
                FluidStack fs = te.getFluid();
                currenttip.add(TinkersStr.wailaLiquidtag.toString() + fs.getLocalizedName());
                currenttip.add(TinkersStr.wailaAmounttag.toString() + fs.amount + "/" + te.getCapacity());
                final float progress = te.getProgress() * 100F;
                currenttip.add(TinkersStr.wailaProgress.toString() + TinkersRebornUtils.df.format(progress) + "%");
            } else {
                if (te.getStackInSlot(0) != null) {
                    currenttip.add(
                        TinkersStr.wailaContains.toString() + te.getStackInSlot(0)
                            .getDisplayName());
                } else {
                    currenttip.add(SpecialChars.ITALIC + TinkersStr.wailaEmpty.toString());
                }
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
