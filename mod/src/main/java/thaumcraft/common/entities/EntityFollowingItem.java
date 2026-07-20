package thaumcraft.common.entities;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thaumcraft.common.Thaumcraft;

/**
 * TC4 parity: dropped item that flies toward a target entity/point with a
 * sparkle trail (colored by {@code type}; type 10 uses crucible bubbles).
 */
public class EntityFollowingItem extends EntitySpecialItem implements IEntityAdditionalSpawnData {

    double targetX = 0.0;
    double targetY = 0.0;
    double targetZ = 0.0;
    int type = 3;
    public Entity target = null;
    private int followDelay = 20;
    public double gravity = 0.04F;

    public EntityFollowingItem(World world) {
        super(world);
        this.setSize(0.25F, 0.25F);
    }

    public EntityFollowingItem(World world, double x, double y, double z, ItemStack stack) {
        super(world);
        this.setSize(0.25F, 0.25F);
        this.setPosition(x, y, z);
        this.setItem(stack);
        this.rotationYaw = (float) (Math.random() * 360.0);
    }

    public EntityFollowingItem(World world, double x, double y, double z, ItemStack stack, Entity target, int type) {
        this(world, x, y, z, stack);
        this.target = target;
        this.targetX = target.posX;
        this.targetY = target.getEntityBoundingBox().minY + target.height / 2.0F;
        this.targetZ = target.posZ;
        this.type = type;
        this.noClip = true;
    }

    public EntityFollowingItem(World world, double x, double y, double z, ItemStack stack, double tx, double ty, double tz) {
        this(world, x, y, z, stack);
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
    }

    @Override
    public void onUpdate() {
        if (this.target != null) {
            this.targetX = this.target.posX;
            this.targetY = this.target.getEntityBoundingBox().minY + this.target.height / 2.0F;
            this.targetZ = this.target.posZ;
        }
        if (this.targetX != 0.0 || this.targetY != 0.0 || this.targetZ != 0.0) {
            float xd = (float) (this.targetX - this.posX);
            float yd = (float) (this.targetY - this.posY);
            float zd = (float) (this.targetZ - this.posZ);
            if (this.followDelay > 1) {
                --this.followDelay;
            }
            double distance = MathHelper.sqrt(xd * xd + yd * yd + zd * zd);
            if (distance > 0.5) {
                distance *= this.followDelay;
                this.motionX = xd / distance;
                this.motionY = yd / distance;
                this.motionZ = zd / distance;
            } else {
                this.motionX *= 0.1F;
                this.motionY *= 0.1F;
                this.motionZ *= 0.1F;
                this.targetX = 0.0;
                this.targetY = 0.0;
                this.targetZ = 0.0;
                this.target = null;
                this.noClip = false;
            }
            if (this.world.isRemote) {
                float px = (float) this.prevPosX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.125F;
                float py = (float) this.prevPosY + this.height / 2.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.125F;
                float pz = (float) this.prevPosZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.125F;
                if (this.type != 10) {
                    Thaumcraft.proxy.sparkle(px, py, pz, this.type);
                } else {
                    Thaumcraft.proxy.crucibleBubble(this.world, px, py, pz, 0.33F, 0.33F, 1.0F);
                }
            }
        } else {
            this.motionY -= this.gravity;
        }
        super.onUpdate();
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setShort("type", (short) this.type);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.type = nbt.getShort("type");
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        data.writeInt(this.target == null ? -1 : this.target.getEntityId());
        data.writeDouble(this.targetX);
        data.writeDouble(this.targetY);
        data.writeDouble(this.targetZ);
        data.writeByte(this.type);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        try {
            int ent = data.readInt();
            if (ent > -1) {
                this.target = this.world.getEntityByID(ent);
            }
            this.targetX = data.readDouble();
            this.targetY = data.readDouble();
            this.targetZ = data.readDouble();
            this.type = data.readByte();
        } catch (Exception ignored) {
        }
    }
}
