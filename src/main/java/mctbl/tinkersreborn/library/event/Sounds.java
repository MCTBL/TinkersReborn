package mctbl.tinkersreborn.library.event;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.world.World;

import mctbl.tinkersreborn.TinkersReborn;

public class Sounds {

    private Sounds() {}

    private static final List<String> sounds = new ArrayList<>();

    public static final String saw = sound("little_saw");
    public static final String sizzle = sound("sizzle");
    public static final String sweep = sound("sweep");

    public static final String stone_hit = sound("stone_hit");
    public static final String wood_hit = sound("wood_hit");

    private static String sound(String name) {
        String soundName = TinkersReborn.MODID + ":" + name;
        sounds.add(soundName);
        return soundName;
    }

    public static void playSoundForAll(Entity entity, String sound, float volume, float pitch) {
        entity.worldObj.playSoundAtEntity(entity, sound, volume, pitch);
    }

    public static void playSoundForPlayer(Entity entity, String sound, float volume, float pitch) {
        if (entity instanceof EntityPlayerMP player) {
            player.playerNetServerHandler
                .sendPacket(new S29PacketSoundEffect(sound, entity.posX, entity.posY, entity.posZ, volume, pitch));
        }
    }

    public static void playSoundAtPos(World world, double x, double y, double z, String sound, float volume,
        float pitch) {
        world.playSoundEffect(x, y, z, sound, volume, pitch);
    }
}
