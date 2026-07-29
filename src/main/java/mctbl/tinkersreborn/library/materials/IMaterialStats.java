package mctbl.tinkersreborn.library.materials;

import java.util.List;

import net.minecraftforge.common.config.Configuration;

/**
 * All material or special material need implement this interface
 * Author MCTBL
 * Time 2026-05-26 05:56:03
 */
public interface IMaterialStats {

    /**
     * Returns a unique String to identify the type of stats the material has.
     */
    MaterialStatusType getIdentifier();

    /**
     * Returns the name of the stat type, to be displayed to the player.
     */
    String getLocalizedName();

    /**
     * Returns a list containing a String for each player-relevant value.</br>
     * Each line should consist of the name of the value followed by the value
     * itself.</br>
     * Example: "Durability: 25"</br>
     * </br>
     * This is used to display properties of materials to the user.
     */
    List<String> getLocalizedInfo();

    /**
     * Returns a list containing a String describing each player-relevant
     * value.</br>
     * The indices of the lines must line up with the lines from
     * getLocalizedInfo()!</br>
     * * This is used to display properties of materials to the user.
     */
    List<String> getLocalizedDesc();

    default IMaterialStats readFromCfg(Configuration cfg, String categotry) {
        return null;
    }

    default void writeToCfg(Configuration cfg, String categotry) {}

    default IMaterialStats getNewStatsFromCfg(Configuration cfg, String categotry) {
        return null;
    }
}
