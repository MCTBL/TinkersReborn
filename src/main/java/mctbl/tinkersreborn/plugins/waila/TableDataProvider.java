package mctbl.tinkersreborn.plugins.waila;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mctbl.tinkersreborn.smeltery.entity.CastingTableLogic;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;

public class TableDataProvider implements IWailaDataProvider {

    public static final String CONFIG_KEY = "tinkersreborn.table";

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof CastingTableLogic te) {
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
        if (accessor.getTileEntity() instanceof CastingTableLogic te && config.getConfig(CONFIG_KEY, true)) {
            if (te.getStackInSlot(1) != null) {
                currenttip.add(
                    TinkersStr.wailaContains.toString() + te.getStackInSlot(1)
                        .getDisplayName());
            }
            if (te.getFluid() != null) {
                currenttip.add(
                    TinkersStr.wailaLiquidtag.toString() + te.getFluid()
                        .getLocalizedName());
                currenttip.add(TinkersStr.wailaAmounttag.toString() + te.getFluidAmount() + "/" + te.getCapacity());
                final float progress = te.getProgress() * 100F;
                currenttip.add(TinkersStr.wailaProgress.toString() + TinkersRebornUtils.df.format(progress) + "%");
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
