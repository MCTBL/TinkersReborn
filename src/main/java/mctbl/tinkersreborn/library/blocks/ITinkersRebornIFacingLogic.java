package mctbl.tinkersreborn.library.blocks;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.util.ForgeDirection;

public interface ITinkersRebornIFacingLogic {

    public ForgeDirection getForgeDirection();

    public void setForgeDirection(ForgeDirection direction);

    public void setFacedDirection(EntityLivingBase player);
}
