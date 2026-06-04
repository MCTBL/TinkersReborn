package mctbl.tinkersreborn.common.entity;

import mctbl.tinkersreborn.library.entity.SlimeBase;
import mctbl.tinkersreborn.library.tools.ToolCore;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.world.World;


public class KingBlueSlime extends SlimeBase implements IBossDisplayData {

    public KingBlueSlime(World world) {
        super(world);
        this.experienceValue = 500;

        // persistance required. this is used by named entities to not despawn, for example.
        this.func_110163_bv();
    }
	

    @Override
    protected String getSlimeParticle() {
        return "tinkers.blueslime";
    }

    @Override
    protected SlimeBase createInstance(World world) {
        return new KingBlueSlime(world);
    }

    @Override
    protected void initializeSlime() {
        this.yOffset = 0.0F;
        this.slimeJumpDelay = this.rand.nextInt(120) + 40;
        this.setSlimeSize(8);
    }

    @Override
    protected float getMaxHealthForSize() {
        return 100;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void setDead() {
        if (!this.worldObj.isRemote && this.getHealth() <= 0) {
            // doesn't break into the next smaller one. let's spawn many tiny slimes instead! :D
            int c = 15 + this.rand.nextInt(6);
            for (; c > 0; c--) {
                BlueSlime entityslime = new BlueSlime(this.worldObj);
                entityslime.setSlimeSize(1);
                double r = rand.nextDouble() * Math.PI;
                double x = Math.cos(r);
                double z = Math.sin(r);
                entityslime.setLocationAndAngles(
                        this.posX - 1d + x,
                        this.posY + 0.5D,
                        this.posZ - 1d + z,
                        this.rand.nextFloat() * 360.0F,
                        0.0F);
                entityslime.motionX = x;
                entityslime.motionY = -0.5d - rand.nextDouble();
                entityslime.motionZ = z;
                this.worldObj.spawnEntityInWorld(entityslime);
            }
        }

        this.isDead = true;
    }

    @Override
    protected void dropFewItems(boolean par1, int par2) {
        super.dropFewItems(par1, par2);
    }

    ToolCore getValidTool() {
    		return null;
    }

    @Override
    public void setSlimeSize(int size) {
        super.setSlimeSize(size);
        this.experienceValue = 500;
    }

}
