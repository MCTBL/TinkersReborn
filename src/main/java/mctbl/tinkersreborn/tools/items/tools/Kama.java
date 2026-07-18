package mctbl.tinkersreborn.tools.items.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockReed;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;

import cpw.mods.fml.common.eventhandler.Event.Result;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.common.particle.Particles;
import mctbl.tinkersreborn.library.event.Sounds;
import mctbl.tinkersreborn.library.materials.MaterialStatusType;
import mctbl.tinkersreborn.library.tools.HarvestTool;
import mctbl.tinkersreborn.library.tools.TinkerToolEvent;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.Category;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class Kama extends HarvestTool {

    public Kama() {
        super("Kama", 3);

        this.componentsParts.add(new ToolPartRecord(TinkersRebornTools.kamaHead, MaterialStatusType.HEAD, "_head"));
        this.componentsParts.add(new ToolPartRecord(TinkersRebornTools.rod, MaterialStatusType.HANDLE, "_handle"));
        this.componentsParts.add(new ToolPartRecord(TinkersRebornTools.binding, MaterialStatusType.EXTRA, "_binding"));

        this.setHarvestLevel("shears", 0);
        this.categoryTags.add(Category.WEAPON);
    }

    @Override
    public float damagePotential() {
        return 1.0F;
    }

    @Override
    public boolean isEffective(Block block) {
        return kamaEffectiveMaterials.contains(block.getMaterial());
    }

    @Override
    protected boolean breakBlock(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        return !ToolTagsHelper.isBroken(itemstack)
            && ToolTagsHelper.shearBlock(itemstack, player.worldObj, player, BlockPos.of(x, y, z));
    }

    @Override
    public boolean onItemUse(ItemStack itemStackIn, EntityPlayer player, World worldIn, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        MovingObjectPosition mop = ((ToolCore) itemStackIn.getItem())
            .getMovingObjectPositionFromPlayer(worldIn, player, true);
        if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
            int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemStackIn);
            BlockPos origin = BlockPos.of(mop.blockX, mop.blockY, mop.blockZ);
            if (harvestCrop(itemStackIn, worldIn, player, origin, fortune)) {
                Sounds.playSoundForAll(player, Sounds.sweep, 1.0F, 1.0F);
                TinkersReborn.proxy.spawnAttackParticle(Particles.BROADSWORD_ATTACK, player, 0.7d);
                player.swingItem();
            }
            return true;
        }

        return super.onItemUse(itemStackIn, player, worldIn, x, y, z, side, hitX, hitY, hitZ);
    }

    protected boolean canHarvestCrop(Block block, int meta) {
        if ((block instanceof BlockCrops && meta >= 7) || (block instanceof BlockNetherWart && meta >= 3)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean harvestCrop(ItemStack stack, World world, EntityPlayer player, BlockPos pos, int fortune) {
        Block block = world.getBlock(pos.x, pos.y, pos.z);
        int meta = world.getBlockMetadata(pos.x, pos.y, pos.z);

        boolean canHarvest = canHarvestCrop(block, meta);

        // do not harvest bottom row reeds
        BlockPos down = pos.down();
        if (block instanceof BlockReed && !(world.getBlock(down.x, down.y, down.z) instanceof BlockReed)) {
            canHarvest = false;
        }

        TinkerToolEvent.OnScytheHarvest event = TinkerToolEvent.OnScytheHarvest
            .fireEvent(stack, player, world, pos, block, canHarvest);

        // can't harvest
        if (event.isCanceled()) {
            return false;
        }

        // harvest handled by event
        if (event.getResult() == Result.DENY) {
            return true;
        }
        // should harwest block nontheless
        else if (event.getResult() == Result.ALLOW) {
            canHarvest = true;
        }

        if (!canHarvest) {
            return false;
        }

        // can be harvested, always just return true clientside for the animation stuff
        if (!world.isRemote) {
            doHarvestCrop(stack, world, player, pos, fortune, block, meta);
        }

        return true;
    }

    protected void doHarvestCrop(ItemStack stack, World world, EntityPlayer player, BlockPos pos, int fortune,
        Block block, int meta) {
        // first, try getting a seed from the drops, if we don't have one we don't
        // replant
        float chance = 1.0f;
        ArrayList<ItemStack> drops = block.getDrops(world, pos.x, pos.y, pos.z, meta, fortune);
        chance = ForgeEventFactory
            .fireBlockHarvesting(drops, world, block, pos.x, pos.y, pos.z, meta, fortune, chance, false, player);

        IPlantable seed = null;
        for (ItemStack drop : drops) {
            if (!TinkersRebornUtils.isStackEmpty(drop) && drop.getItem() instanceof IPlantable plantAble) {
                seed = plantAble;
                drop.stackSize -= 1;
                if (TinkersRebornUtils.isStackEmpty(drop)) {
                    drops.remove(drop);
                }

                break;
            }
        }

        // if we have a valid seed, try to plant the crop
        boolean replanted = false;
        if (seed != null) {
            // make sure the plant is allowed here. should already be, mainly just covers
            // the case of seeds from grass
            BlockPos blokcDown = pos.down();
            Block down = world.getBlock(blokcDown.x, blokcDown.y, blokcDown.z);

            if (down.canSustainPlant(world, blokcDown.x, blokcDown.y, blokcDown.z, ForgeDirection.UP, seed)) {
                // success! place the plant and drop the rest of the items
                Block crop = seed.getPlant(world, pos.x, pos.y, pos.z);

                // only place the block/damage the tool if its a different state
                if (crop == block) {
                    world.setBlock(pos.x, pos.y, pos.z, seed.getPlant(world, pos.x, pos.y, pos.z), 0, 2);
                    ToolTagsHelper.damageTool(stack, 1, player);
                }

                // drop the remainder of the items
                for (ItemStack drop : drops) {
                    if (world.rand.nextFloat() <= chance) {
                        TinkersRebornUtils.dropItemAtPos(world, pos, drop);
                    }
                }
                replanted = true;
            }
        }

        // can't plant? just break the block directly
        if (!replanted) {
            breakExtraBlock(stack, player.getEntityWorld(), player, pos, pos);
        }
    }

    public boolean shearEntity(ItemStack stack, World world, EntityPlayer player, Entity entity, int fortune) {
        if (!(entity instanceof IShearable)) {
            return false;
        }

        IShearable shearable = (IShearable) entity;
        if (shearable.isShearable(stack, world, (int) entity.posX, (int) entity.posY, (int) entity.posZ)) {
            if (!world.isRemote) {
                List<ItemStack> drops = shearable
                    .onSheared(stack, world, (int) entity.posX, (int) entity.posY, (int) entity.posZ, fortune);
                Random rand = world.rand;
                for (ItemStack drop : drops) {
                    EntityItem entityItem = entity.entityDropItem(drop, 1.0F);
                    if (entityItem != null) {
                        entityItem.motionY += rand.nextFloat() * 0.05F;
                        entityItem.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                        entityItem.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                    }
                }
            }
            ToolTagsHelper.damageTool(stack, 1, player);

            return true;
        }

        return false;
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on
     * sheep.
     */
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        // only run AOE on shearable entities
        if (target instanceof IShearable) {
            int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);
            if (shearEntity(stack, player.getEntityWorld(), player, target, fortune) && !player.worldObj.isRemote) {
                player.swingItem();
                TinkersReborn.proxy.spawnAttackParticle(Particles.BROADSWORD_ATTACK, player, 0.7d);
                return true;
            }
        }

        return false;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(18 + 10, 60 - 35) // kama head
                .addSlotPosition(18, 60) // rod
                .addSlotPosition(18 + 28, 60 - 20); // binding
        }
        return this.toolBuildGuiInfo;
    }

}
