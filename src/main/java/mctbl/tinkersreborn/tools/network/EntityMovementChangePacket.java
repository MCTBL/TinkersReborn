package mctbl.tinkersreborn.tools.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mctbl.tinkersreborn.common.network.AbstractPacketThreadsafe;
import mctbl.tinkersreborn.common.network.TinkerNetwork.AbstactPacketHandler;

public class EntityMovementChangePacket extends AbstractPacketThreadsafe {

    public static class Handler extends AbstactPacketHandler {
    }

    public int entityID;
    public double posX, posY, posZ;
    public double motionX, motionY, motionZ;
    public float yaw;
    public float pitch;
    public int ticksInAir;
    public boolean inGround;
    public int ticksInGround;
    public int arrowShake;

    public EntityMovementChangePacket() {}

    public EntityMovementChangePacket(Entity entity) {
        this.entityID = entity.getEntityId();
        this.motionX = entity.motionX;
        this.motionY = entity.motionY;
        this.motionZ = entity.motionZ;
        this.yaw = entity.rotationYaw;
        this.pitch = entity.rotationPitch;
        this.posX = entity.posX;
        this.posY = entity.posY;
        this.posZ = entity.posZ;
        if (entity instanceof EntityArrow arrow) {
            this.ticksInAir = arrow.ticksInAir;
            this.inGround = arrow.inGround;
            this.ticksInGround = arrow.ticksInGround;
            this.arrowShake = arrow.arrowShake;
        }
    }

    @Override
    public void handleClientSafe(MessageContext netHandler) {
        Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(entityID);
        if (entity != null) {
            entity.posX = this.posX;
            entity.posY = this.posY;
            entity.posZ = this.posZ;
            entity.motionX = this.motionX;
            entity.motionY = this.motionY;
            entity.motionZ = this.motionZ;
            entity.rotationYaw = yaw;
            entity.rotationPitch = pitch;
            if (entity instanceof EntityArrow arrow) {
                arrow.ticksInAir = this.ticksInAir;
                arrow.inGround = this.inGround;
                arrow.ticksInGround = this.ticksInGround;
                arrow.arrowShake = this.arrowShake;
            }
        }
    }

    @Override
    public void handleServerSafe(MessageContext ctx) {
        // only ever sent to clients
        throw new UnsupportedOperationException("Serverside only");
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityID = buf.readInt();
        this.motionX = buf.readDouble();
        this.motionY = buf.readDouble();
        this.motionZ = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
        this.posX = buf.readDouble();
        this.posY = buf.readDouble();
        this.posZ = buf.readDouble();
        this.ticksInAir = buf.readInt();
        this.inGround = buf.readBoolean();
        this.ticksInGround = buf.readInt();
        this.arrowShake = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeDouble(motionX);
        buf.writeDouble(motionY);
        buf.writeDouble(motionZ);
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);
        buf.writeInt(ticksInAir);
        buf.writeBoolean(inGround);
        buf.writeInt(ticksInGround);
        buf.writeInt(arrowShake);
    }

}
