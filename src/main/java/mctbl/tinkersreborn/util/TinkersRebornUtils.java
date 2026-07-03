package mctbl.tinkersreborn.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.input.Keyboard;

import mctbl.tinkersreborn.TinkersRebornConfig;
import mctbl.tinkersreborn.library.utils.BlockPos;

public class TinkersRebornUtils {

    public static final DecimalFormat df = new DecimalFormat(
        "#,###,###.##",
        DecimalFormatSymbols.getInstance(Locale.US));
    public static final DecimalFormat dfPercent = new DecimalFormat("#%");

    /**
     * Removes all whitespaces from the given string and makes it lowerspace.
     */
    public static String sanitizeLocalizationString(String string) {
        return string.toLowerCase(Locale.US)
            .replaceAll(" ", "");
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
        if (stack == null || stack.stackSize == 0) return true;

        return false;
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
        float f = 0.7F;
        double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
        double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
        double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;

        EntityItem entityitem = new EntityItem(world, (double) x + d0, (double) y + d1, (double) z + d2, stack);
        entityitem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityitem);
    }

    public static void drropItemAtPlayer(EntityPlayer player, ItemStack stack) {
        dropItemAtPos(player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ, stack);
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
            return String.format(
                translate("tinkersreborn.gui.general.temperature.celsius"),
                df.format(transferFahrenheitToCelsius(temperature)));
        }
        return String.format(translate("tinkersreborn.gui.general.temperature.fahrenheit"), temperature);
    }

    public static float transferFahrenheitToCelsius(int fahrenheit) {
        return (fahrenheit - 32) * 1.0F / 1.8F;
    }
}
