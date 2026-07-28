package mctbl.tinkersreborn.library.tools.leveling;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.tools.ToolCore;

public class CommandLevelUpTool extends CommandBase {

    @Override
    public String getCommandName() {
        return "leveluptool";
    }

    /**
     * Return the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        EntityPlayerMP entityplayermp = astring.length >= 1 ? getPlayer(icommandsender, astring[0])
            : getCommandSenderAsPlayer(icommandsender);
        ItemStack equipped = entityplayermp.getCurrentEquippedItem();
        if (equipped != null && equipped.getItem() instanceof ToolCore) {
            ToolLevelingHelper.levelUpTool(equipped, entityplayermp);
            ToolLevelingHelper.levelUpMiningLevel(equipped, entityplayermp, false);
        } else throw new WrongUsageException("Player must have a Tinkers Reborn tool in hand", new Object[0]);
    }

    /**
     * Parses an int from the given sring with a specified minimum.
     */
    public static int parseIntWithMinMax(ICommandSender par0ICommandSender, String par1Str, int min, int max) {
        return parseIntBounded(par0ICommandSender, par1Str, min, max);
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return null;
    }

    @Override
    public int compareTo(Object arg0) {
        return 0;
    }

}
