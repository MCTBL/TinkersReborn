package mctbl.tinkersreborn.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.input.Keyboard;

import com.google.gson.JsonParser;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.materials.TinkersRebornMaterial;
import mctbl.tinkersreborn.library.utils.BlockPos;

public class TinkersRebornUtils {

    private TinkersRebornUtils() {
        /* This utility class should not be instantiated */
    }

    public static final DecimalFormat df = new DecimalFormat(
        "#,###,###.##",
        DecimalFormatSymbols.getInstance(Locale.US));
    public static final DecimalFormat dfPercent = new DecimalFormat("#%");
    public static final JsonParser jsonParser = new JsonParser();
    public static final Pattern regex = Pattern.compile(" ");

    /**
     * Removes all whitespaces from the given string and makes it lowerspace.
     */
    public static String sanitizeLocalizationString(String string) {
        return regex.matcher(string.toLowerCase(Locale.US))
            .replaceAll("");
    }

    public static String translate(String string) {
        return StatCollector.translateToLocal(string);
    }

    public static String translate(String string, Object... obj) {
        return String.format(StatCollector.translateToLocal(string), obj);
    }

    public static boolean canTranslate(String string) {
        return StatCollector.canTranslate(string);
    }

    public static void ensureOreIsRegistered(String oreDict, ItemStack stack) {
        if (OreDictionary.getOreIDs(stack).length == 0) OreDictionary.registerOre(oreDict, stack);
    }

    public static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }

    /*
     * Code for ctl and shift down from TicTooltips by squeek502
     * https://github.com/squeek502/TiC-Tooltips/blob/1.7.10/java/squeek/tictooltips
     * /helpers/KeyHelper.java
     */
    public static boolean isCtrlKeyDown() {
        // prioritize CONTROL, but allow OPTION as well on Mac (note: GuiScreen's
        // isCtrlKeyDown only checks for the OPTION key on Mac)
        boolean isCtrlKeyDown = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        if (!isCtrlKeyDown && Minecraft.isRunningOnMac) {
            isCtrlKeyDown = Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA);
        }
        return isCtrlKeyDown;
    }

    // balantly stolen from StackOverflow and then optimized
    public static String getRomanNumeral(int value) {
        if (value < 1 || value > 3999) {
            return "Really big";
        }

        StringBuilder sb = new StringBuilder();
        while (value >= 1000) {
            sb.append("M");
            value -= 1000;
        }
        while (value >= 900) {
            sb.append("CM");
            value -= 900;
        }
        while (value >= 500) {
            sb.append("D");
            value -= 500;
        }
        while (value >= 400) {
            sb.append("CD");
            value -= 400;
        }
        while (value >= 100) {
            sb.append("C");
            value -= 100;
        }
        while (value >= 90) {
            sb.append("XC");
            value -= 90;
        }
        while (value >= 50) {
            sb.append("L");
            value -= 50;
        }
        while (value >= 40) {
            sb.append("XL");
            value -= 40;
        }
        while (value >= 10) {
            sb.append("X");
            value -= 10;
        }
        while (value >= 9) {
            sb.append("IX");
            value -= 9;
        }
        while (value >= 5) {
            sb.append("V");
            value -= 5;
        }
        while (value >= 4) {
            sb.append("IV");
            value -= 4;
        }
        while (value >= 1) {
            sb.append("I");
            value -= 1;
        }
        return sb.toString();
    }

    public static boolean isStackEmpty(@Nullable ItemStack stack) {
        return (stack == null || stack.stackSize == 0);
    }

    public static List<ItemStack> copyItemStackList(List<ItemStack> stackList) {
        return stackList.stream()
            .map(i -> i != null ? i.copy() : null)
            .collect(Collectors.toList());
    }

    public static String convertNewlines(String line) {
        if (line == null) return null;
        int j;
        while ((j = line.indexOf("\\n")) >= 0) {
            line = line.substring(0, j) + '\n' + line.substring(j + 2);
        }

        return line;
    }

    public static void dropItemAtPos(World world, BlockPos pos, ItemStack stack) {
        dropItemAtPos(world, pos.x, pos.y, pos.z, stack);
    }

    public static void dropItemAtPos(World world, int x, int y, int z, ItemStack stack) {
        if (!world.isRemote) {
            float f = 0.7F;
            double d0 = (world.rand.nextFloat() * f) + (1.0F - f) * 0.5D;
            double d1 = (world.rand.nextFloat() * f) + (1.0F - f) * 0.5D;
            double d2 = (world.rand.nextFloat() * f) + (1.0F - f) * 0.5D;

            EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, stack);
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        }
    }

    public static void drropItemAtPlayer(EntityPlayer player, ItemStack stack) {
        dropItemAtPos(player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ, stack);
    }

    public static void spawnEntity(World world, Entity entity) {
        if (!world.isRemote) {
            world.spawnEntityInWorld(entity);
        }
    }

    /**
     * Formats the given temperature as a string using the config preference
     * 
     * @param temperature Temperature in kelvin
     * @return Formatted string
     */
    public static String temperatureString(int temperature) {
        return temperatureString(temperature, TinkersRebornConfig.celsiusPref);
    }

    /**
     * Formats the given temperature as a string
     * 
     * @param temperature Temperature in kelvin
     * @param celsius     If true, displays a celsius, false kelvin
     * @return Formatted string
     */
    public static String temperatureString(int temperature, boolean celsius) {
        if (celsius) {
            return String.format(TinkersStr.guiCelsius.toString(), df.format(transferKelvinToCelsius(temperature)));
        }
        return String.format(TinkersStr.guiKelvin.toString(), temperature);
    }

    public static float transferKelvinToCelsius(float kelvin) {
        return kelvin - 273.15F;
    }

    public static boolean isOreBlock(World world, BlockPos pos) {
        if (world == null) return false;

        Block block = world.getBlock(pos.x, pos.y, pos.z);
        int meta = world.getBlockMetadata(pos.x, pos.y, pos.z);

        return isOreBlock(block, meta);
    }

    // TODO is worth to cache this?
    public static boolean isOreBlock(Block block, int meta) {
        if (block == null) return false;

        ItemStack stack = new ItemStack(block, 1, meta);
        if (stack.getItem() != null) {
            for (int id : OreDictionary.getOreIDs(stack)) {
                String name = OreDictionary.getOreName(id);
                if (name.startsWith("ore")) {
                    return true;
                }
            }
        }

        if (block instanceof BlockOre) return true;

        String tool = block.getHarvestTool(meta);
        int level = block.getHarvestLevel(meta);
        return "pickaxe".equals(tool) && level >= 2;
    }

    public static int ceilDiv(int x, int y) {
        final int q = x / y;
        // if the signs are the same and modulo not zero, round up
        if ((x ^ y) >= 0 && (q * y != x)) {
            return q + 1;
        }
        return q;
    }

    /**
     * Replace one currently held item for a given player.
     *
     * @param player      A player
     * @param replacement An ItemStack that will replace one of the items in the player's currently held ItemStack.
     */
    public static void replaceHeldItem(EntityPlayer player, ItemStack replacement) {
        ItemStack current = player.getHeldItem();
        if (current.stackSize == 1) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, replacement);
        } else {
            player.inventory.decrStackSize(player.inventory.currentItem, 1);

            if (!player.inventory.addItemStackToInventory(replacement)) {
                player.dropPlayerItemWithRandomChoice(replacement, false);
            }
        }
    }

    /**
     * calculate the rendering heights for all the liquids
     *
     * @param liquids  The liquids
     * @param capacity Max capacity of smeltery, to calculate how much height one
     *                 liquid takes up
     * @param height   Maximum height, basically represents how much height full
     *                 capacity is
     * @param min      Minimum amount of height for a fluid. A fluid can never have
     *                 less than this value height returned
     * @return Array with heights corresponding to input-list liquids
     */
    public static int[] calcLiquidHeights(List<FluidStack> liquids, int capacity, int height) {
        int[] fluidHeights = new int[liquids.size()];

        int totalFluidAmount = 0;
        int min = 3;

        if (!liquids.isEmpty()) {

            for (int i = 0; i < liquids.size(); i++) {
                FluidStack liquid = liquids.get(i);

                float h = (float) liquid.amount / (float) capacity;
                totalFluidAmount += liquid.amount;
                fluidHeights[i] = Math.max(min, (int) Math.ceil(h * 1.0F * height));
            }

            // if not completely full, leave a few pixels for the empty tank display
            if (totalFluidAmount < capacity) {
                height -= min;
            }

            // check if we have enough height to render everything, if not remove pixels
            // from the tallest liquid
            int sum;
            do {
                sum = 0;
                int biggest = -1;
                int m = 0;
                for (int i = 0; i < fluidHeights.length; i++) {
                    sum += fluidHeights[i];
                    if (fluidHeights[i] > biggest) {
                        biggest = fluidHeights[i];
                        m = i;
                    }
                }

                // we can't get a result without going negative
                if (fluidHeights[m] == 0) {
                    break;
                }

                // remove a pixel from the biggest one
                if (sum > height) {
                    fluidHeights[m]--;
                }
            } while (sum > height);
        }

        return fluidHeights;
    }

    /**
     * Adds information to the tooltip based on the fluid amount
     *
     * @param amount Fluid amount
     * @param text   Text to add information to.
     */
    public static void amountToString(int amount, List<String> text) {
        amount = calcLiquidText(amount, 1000000, TinkersStr.smtleteryLiquidKB.toString(), text);
        amount = calcLiquidText(amount, 1000, TinkersStr.smtleteryLiquidB.toString(), text);
        calcLiquidText(amount, 1, TinkersStr.smtleteryLiquidmB.toString(), text);
    }

    public static void amountToIngotString(int amount, List<String> text) {
        amount = calcLiquidText(
            amount,
            TinkersRebornMaterial.VALUE_Ingot,
            TinkersStr.smtleteryLiquidIngot.toString(),
            text);
        amountToString(amount, text);
    }

    public static int calcLiquidText(int amount, int divider, String unit, List<String> text) {
        int full = amount / divider;
        if (full > 0) {
            text.add(String.format("%s%d %s", EnumChatFormatting.GRAY, full, unit));
        }

        return amount % divider;
    }

    /**
     * Adds information for the tooltip based on the fluid stacks size.
     *
     * @param fluid Input fluid stack
     * @param text  Text to add information to.
     */
    public static void liquidToString(FluidStack fluid, List<String> text) {
        int amount = fluid.amount;

        if (!TinkersRebornUtils.isShiftKeyDown()) {
            // List<FluidGuiEntry> entries = fluidGui.get(fluid.getFluid());
            // if(entries == null) {
            // entries = calcFluidGuiEntries(fluid.getFluid());
            // fluidGui.put(fluid.getFluid(), entries);
            // }
            //
            // for(FluidGuiEntry entry : entries) {
            // amount = calcLiquidText(amount, entry.amount, entry.getText(), text);
            // }
        }

        // standard display stuff: bucket amounts
        amountToString(amount, text);
    }
}
