package mctbl.tinkersreborn.library.tools.leveling;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import mctbl.tinkersreborn.library.tools.ToolCore;
import mctbl.tinkersreborn.util.ToolTagsHelper;

public class CommandModifierChange extends CommandBase {

    @Override
    public String getCommandName() {
        return "addtoolmodifier";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        EntityPlayerMP entityplayermp = astring.length >= 1 ? getPlayer(icommandsender, astring[0])
            : getCommandSenderAsPlayer(icommandsender);
        ItemStack equipped = entityplayermp.getCurrentEquippedItem();
        if (equipped != null && equipped.getItem() instanceof ToolCore) {
            ToolTagsHelper.setModifierSlots(equipped, ToolTagsHelper.getModifierSlots(equipped) + 1);
        } else throw new WrongUsageException("Player must have a Tinkers Reborn tool in hand", new Object[0]);
    }

    @Override
    public int compareTo(Object arg0) {
        return 0;
    }

    /**
     * Return the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

}
