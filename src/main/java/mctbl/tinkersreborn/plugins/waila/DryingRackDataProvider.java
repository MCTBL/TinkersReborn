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
import mctbl.tinkersreborn.common.entity.DryingRackLogic;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.TinkersStr;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class DryingRackDataProvider implements IWailaDataProvider {

    public static final String CONFIG_KEY = "tinkersreborn.dryingrack";

    private static final String NBT_DATA = "TinkersRebornDryingRack";

    private static final String NBT_CURRENT_TIME = "CurrentTime";

    private static final String NBT_MAX_TIME = "MaxTime";

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
        if (accessor.getTileEntity() instanceof DryingRackLogic te && config.getConfig(CONFIG_KEY, true)) {
            ItemStack stack = te.getStackInSlot(0);
            stack = TinkersRebornUtils.isStackEmpty(stack) ? te.getStackInSlot(1) : stack;
            if (!TinkersRebornUtils.isStackEmpty(stack)) {
                currenttip.add(TinkersStr.wailaDrying.toString() + stack.getDisplayName());

                NBTTagCompound root = ToolTagsHelper.getTagSafe(accessor.getNBTData(), NBT_DATA);
                if (root.getInteger(NBT_MAX_TIME) > 0) {
                    final float progress = root.getInteger(NBT_CURRENT_TIME) * 100F / root.getInteger(NBT_MAX_TIME);
                    currenttip
                        .add(TinkersStr.wailaDryingProgress.toString() + TinkersRebornUtils.df.format(progress) + "%");
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
        if (te instanceof DryingRackLogic rack) {
            NBTTagCompound dryingData = new NBTTagCompound();
            dryingData.setInteger(NBT_CURRENT_TIME, rack.getCurrentTime());
            dryingData.setInteger(NBT_MAX_TIME, rack.getMaxTime());
            tag.setTag(NBT_DATA, dryingData);
        }
        return tag;
    }

}
