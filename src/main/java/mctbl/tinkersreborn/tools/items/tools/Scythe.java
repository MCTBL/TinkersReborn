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
import net.minecraft.util.AxisAlignedBB;
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
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.tools.AoeHarvestTool;
import mctbl.tinkersreborn.library.tools.TinkerToolEvent;
import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.library.tools.ToolNBT;
import mctbl.tinkersreborn.library.utils.BlockPos;
import mctbl.tinkersreborn.tools.Category;
import mctbl.tinkersreborn.tools.TinkersRebornTools;
import mctbl.tinkersreborn.tools.gui.ToolBuildGuiInfo;
import mctbl.tinkersreborn.util.TinkersRebornUtils;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class Scythe extends AoeHarvestTool {

    public static final float DURABILITY_MODIFIER = 2.2f;

    public Scythe() {
        super("Scythe", 4);

        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.scytheHead, MaterialStatusType.HEAD, "_scythe_head"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.toughrod, MaterialStatusType.HANDLE, "_scythe_handle"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.toughrod, MaterialStatusType.HANDLE, "_scythe_accessory"));
        this.componentsParts
            .add(new ToolPartRecord(TinkersRebornTools.toughbind, MaterialStatusType.EXTRA, "_scythe_binding"));

        this.setHarvestLevel("shears", 0);
        this.categoryTags.add(Category.WEAPON);
    }

    @Override
    public boolean isEffective(Block block) {
        return kamaEffectiveMaterials.contains(block.getMaterial());
    }

    private static boolean isSilkTouch(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack) > 0;
    }

    @Override
    public float damagePotential() {
        return 0.75F;
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

            // AOE crop harvest: 3x3x3 area, skips origin on first pass
            boolean harvestedSomething = false;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0) continue;
                    harvestedSomething |= harvestCrop(itemStackIn, worldIn, player, origin.offset(dx, 0, dz), fortune);
                }
            }
            // also harvest the origin block itself
            harvestedSomething |= harvestCrop(itemStackIn, worldIn, player, origin, fortune);

            if (harvestedSomething) {
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

    @Override
    protected boolean breakBlock(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        // only allow shears with silktouch :D
        return isSilkTouch(itemstack) && (!ToolTagsHelper.isBroken(itemstack)
            && ToolTagsHelper.shearBlock(itemstack, player.worldObj, player, BlockPos.of(x, y, z)));
    }

    @Override
    protected void breakExtraBlock(ItemStack tool, World world, EntityPlayer player, BlockPos pos, BlockPos refPos) {
        // only allow shears with silktouch :D
        if (isSilkTouch(tool)) {
            ToolTagsHelper.shearExtraBlock(tool, world, player, pos, refPos);
            return;
        }

        // can't be sheared or no silktouch. break it
        ToolTagsHelper.breakExtraBlock(tool, world, player, pos, refPos);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        if ("shears".equals(toolClass) && !isSilkTouch(stack)) {
            return -1;
        }

        return super.getHarvestLevel(stack, toolClass);
    }

    @Override
    public List<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
        return ToolTagsHelper.calcAOEBlocks(stack, world, player, origin, 3, 3, 3, -1);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity target) {
        // increase the size based on the AOE stuffs
        TinkerToolEvent.ExtraBlockBreak event = TinkerToolEvent.ExtraBlockBreak.fireEvent(
            stack,
            player,
            player.getEntityWorld()
                .getBlock((int) target.posX, (int) target.posY, (int) target.posZ),
            3,
            3,
            3,
            -1);
        if (event.isCanceled()) {
            return false;
        }

        // AOE attack!
        Sounds.playSoundForAll(player, Sounds.sweep, 1.0F, 1.0F);
        if (!player.worldObj.isRemote) {
            TinkersReborn.proxy.spawnAttackParticle(Particles.BROADSWORD_ATTACK, player, 0.7d);
        }

        int distance = event.distance;
        boolean hit = false;
        // we cache the cooldown here since it resets as soon as the first entity is hit
        for (Entity entity : getAoeEntities(player, target, event)) {
            if (distance < 0 || entity.getDistanceToEntity(target) <= distance) {
                hit |= ToolTagsHelper.attackEntity(stack, this, player, entity, null);
            }
        }

        // subtract the default box and then half as this number is the amount to
        // increase the box by
        return hit;
    }

    private List<Entity> getAoeEntities(EntityPlayer player, Entity target, TinkerToolEvent.ExtraBlockBreak event) {
        int width = (event.width - 1) / 2;
        int height = (event.height - 1) / 2;
        AxisAlignedBB box = target.boundingBox.addCoord(width, height, width);

        return player.getEntityWorld()
            .getEntitiesWithinAABBExcludingEntity(player, box);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        // only run AOE on shearable entities
        if (!(target instanceof IShearable)) {
            return false;
        }

        // increase the size based on the AOE stuffs
        TinkerToolEvent.ExtraBlockBreak event = TinkerToolEvent.ExtraBlockBreak.fireEvent(
            stack,
            player,
            player.getEntityWorld()
                .getBlock((int) target.posX, (int) target.posY, (int) target.posZ),
            3,
            3,
            3,
            -1);
        if (event.isCanceled()) {
            return false;
        }

        int distance = event.distance;
        boolean shorn = false;

        int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);
        for (Entity entity : getAoeEntities(player, target, event)) {
            if (distance < 0 || entity.getDistanceToEntity(target) <= distance) {
                shorn |= shearEntity(stack, player.getEntityWorld(), player, entity, fortune);
            }
        }

        if (shorn && !player.worldObj.isRemote) {
            player.swingItem();
            TinkersReborn.proxy.spawnAttackParticle(Particles.BROADSWORD_ATTACK, player, 0.7d);
        }

        return shorn;
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

    @Override
    public float getRepairModifierForPart(int index) {
        return DURABILITY_MODIFIER;
    }

    @Override
    public ToolNBT buildToolTag(List<TinkersRebornMaterial> materials) {
        ToolNBT toolTag = super.buildToolTag(materials);
        toolTag.durability *= DURABILITY_MODIFIER;
        return toolTag;
    }

    @Override
    public ToolBuildGuiInfo getToolBuildGuiInfo() {
        if (this.toolBuildGuiInfo == null) {
            this.toolBuildGuiInfo = new ToolBuildGuiInfo(this).addSlotPosition(33 + 3, 42 - 23) // head
                .addSlotPosition(33 - 16, 42 + 12) // handle
                .addSlotPosition(33 - 12 + 16, 42 + 5) // handle2
                .addSlotPosition(33 + 7 + 16, 42 - 23 + 10); // binding
        }
        return this.toolBuildGuiInfo;
    }

}
