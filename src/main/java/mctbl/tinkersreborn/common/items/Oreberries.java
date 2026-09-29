package mctbl.tinkersreborn.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mctbl.tinkersreborn.TinkersReborn;
import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.TinkersRebornRegistry;
import mctbl.tinkersreborn.library.items.CraftingItem;
import mctbl.tinkersreborn.util.TinkersRebornUtils;

public class Oreberries extends CraftingItem {

    public Oreberries() {
        super(
            TinkersRebornConfig.oreberryBushTypes,
            TinkersRebornConfig.oreberryBushTypes,
            "oreberries/",
            TinkersRebornRegistry.miscTab);
        this.setUnlocalizedName("tinkersreborn.oreberry");
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (this.unlocalizedNames != null) {
            int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, this.unlocalizedNames.length - 1);
            return "tinkersreborn.oreberry." + this.unlocalizedNames[arr];
        } else {
            return this.getUnlocalizedName();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i) {
            if (!(textureNames[i].equals("")))
                this.icons[i] = iconRegister.registerIcon("tinkersreborn:" + folder + "oreberry_" + textureNames[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean detail) {
        String key = "tinkersreborn.oreberry." + this.unlocalizedNames[stack.getItemDamage()] + ".tooltip";
        if (TinkersRebornUtils.canTranslate(key)) {
            list.add(TinkersRebornUtils.translate(key));
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World worldIn, EntityPlayer player) {
        if (stack.getItemDamage() == 5) {
            int consumeCount = player.isSneaking() ? stack.stackSize : 1;
            int expSize = 0;
            for (int i = 0; i < consumeCount; i++) {
                expSize += itemRand.nextInt(14) + 6;
            }

            EntityXPOrb xpOrb = new EntityXPOrb(worldIn, player.posX, player.posY + 1, player.posZ, expSize);
            TinkersRebornUtils.spawnEntity(worldIn, xpOrb);
            if (!player.capabilities.isCreativeMode) stack.stackSize -= consumeCount;
            if (TinkersRebornConfig.disgustingXPBerries && applyBerryEffects(player, player.isSneaking())
                && !worldIn.isRemote) {
                player.worldObj.playSoundAtEntity(
                    player,
                    "game.player.hurt",
                    0.5F,
                    player.worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }
        }

        return stack;
    }

    /**
     * parameters ID | DURATION | initialAmplifier | maxDuration | maxAmplifier | stackExclusive
     * 
     * @param player     the player that's eating the berries
     * @param isShifting is the player shifting?
     * @return Was one of the effects DAMAGE (used properly play the damage sound in account to the player shifting)
     */
    private static boolean applyBerryEffects(EntityPlayer player, boolean isShifting) {
        boolean output = false;
        if (player.capabilities.isCreativeMode || player.worldObj.isRemote) return false;

        for (String effect : TinkersRebornConfig.disgustingXPBerryEffects) {
            try {
                String[] parameters = effect.replace(" ", "")
                    .split(",");
                boolean isDamageEffect = parameters[0].equals("DAMAGE");
                if (!(isDamageEffect && parameters.length == 3) && parameters.length != 6) {
                    throw new RuntimeException("Too many or few parameters");
                } else {
                    if (isShifting == Boolean.parseBoolean(parameters[isDamageEffect ? 2 : 5])) {
                        if (isDamageEffect) {
                            player.setHealth(player.getHealth() - Float.parseFloat(parameters[1]));
                            output = true;
                        } else {
                            player.addPotionEffect(getBerryEffect(player, parameters));
                        }
                    }
                }
            } catch (Exception e) {
                TinkersReborn.LOG.error("Too many or few parameters for applyBerryEffects in Oreberries");
            }
        }
        return output;
    }

    /**
     * Don't ask how the amplifier increase system works, I'm not fully sure myself @GamingB3ast
     * 
     * @param player
     * @param parameters ID | DURATION | initialAmplifier | maxDuration | maxAmplifier | stackExclusive
     * @return The potion effect which is applied onto the player
     */
    private static PotionEffect getBerryEffect(EntityPlayer player, String[] parameters) {
        int duration = Integer.parseInt(parameters[1]);
        int amplifier = Integer.parseInt(parameters[2]);
        for (PotionEffect currentEffect : player.getActivePotionEffects()) {
            if (currentEffect.getPotionID() == Integer.parseInt(parameters[0])) {
                duration = Math.min(Integer.parseInt(parameters[3]), currentEffect.getDuration() + 40);
                amplifier = currentEffect.getAmplifier() + 1;
                int step = Integer.parseInt(parameters[3]) / (Integer.parseInt(parameters[4]) + 1 - amplifier) - 80;
                amplifier += Math.min(Integer.parseInt(parameters[3]) - 1, (duration >= step * amplifier ? 1 : 0));
                break;
            }
        }
        return new PotionEffect(Integer.parseInt(parameters[0]), duration, amplifier - 1);
    }
}
