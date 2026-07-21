package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.visnet.TileVisNode;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.TCSounds;

import java.awt.Color;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class TileVisRelay extends TileVisNode implements IWandable {
    public static final HashMap<Integer, WeakReference<TileVisRelay>> nearbyPlayers = new HashMap<>();
    public static final int[] colors = {0xFFFF7E, 0xFF3E01, 0x00907F, 0x00A000, 0xEECCFF, 0x555577};

    public byte orientation = 1;
    public byte color = -1;

    // Client-side beam-to-parent rendering state.
    protected Object beam1 = null;
    protected int pulse;
    public float pRed = 0.5f;
    public float pGreen = 0.5f;
    public float pBlue = 0.5f;
    // Offset from this conduit to its parent node, synced via NBT so the client can
    // reconstruct the parent and draw the connecting beam.
    protected int px;
    protected int py;
    protected int pz;
    protected boolean parentLoaded = false;

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos, this.pos.add(1, 1, 1));
    }

    @Override
    public int getRange() {
        return 8;
    }

    @Override
    public boolean isSource() {
        return false;
    }

    @Override
    public byte getAttunement() {
        return color;
    }

    @Override
    public void parentChanged() {
        // Relight so the "energized/valid" light level (getLightValue) refreshes when the
        // network reconnects.
        if (this.world != null && this.world.isRemote) {
            this.world.checkLightFor(EnumSkyBlock.BLOCK, this.pos);
        }
    }

    @Override
    public void invalidate() {
        this.beam1 = null;
        super.invalidate();
    }

    @Override
    public void update() {
        drawEffect();
        super.update();
        if (this.world == null || this.world.isRemote || this.ticksExisted() % 20 != 0) return;
        List<EntityPlayer> players = this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.pos).grow(5.0D));
        for (EntityPlayer player : players) {
            WeakReference<TileVisRelay> currentRef = nearbyPlayers.get(player.getEntityId());
            TileVisRelay current = currentRef == null ? null : currentRef.get();
            if (current == null || current.isInvalid() || player.getDistanceSq(this.pos) < player.getDistanceSq(current.getPos())) {
                nearbyPlayers.put(player.getEntityId(), new WeakReference<>(this));
            }
        }
    }

    /** Client-side: reconstruct the parent from px/py/pz and draw the vis beam toward it. */
    @SideOnly(Side.CLIENT)
    protected void drawEffect() {
        if (this.world == null || !this.world.isRemote) {
            if (this.pulse > 0) this.pulse--;
            return;
        }
        if (this.parentLoaded) {
            if (this.px != 0 || this.py != 0 || this.pz != 0) {
                TileEntity tile = this.world.getTileEntity(this.pos.add(-this.px, -this.py, -this.pz));
                this.setParent(tile instanceof TileVisNode ? new WeakReference<>((TileVisNode) tile) : null);
            } else {
                this.setParent(null);
            }
            this.parentLoaded = false;
            this.parentChanged();
        }
        if (VisNetHandler.isNodeValid(this.getParent())) {
            TileVisNode parent = this.getParent().get();
            double xx = parent.getPos().getX() + 0.5;
            double yy = parent.getPos().getY() + 0.5;
            double zz = parent.getPos().getZ() + 0.5;
            int d1x = 0, d1y = 0, d1z = 0;
            if (parent instanceof TileVisRelay) {
                net.minecraft.util.EnumFacing pf = net.minecraft.util.EnumFacing.byIndex(((TileVisRelay) parent).orientation);
                d1x = pf.getXOffset();
                d1y = pf.getYOffset();
                d1z = pf.getZOffset();
            }
            net.minecraft.util.EnumFacing d2 = net.minecraft.util.EnumFacing.byIndex(this.orientation);
            this.beam1 = Thaumcraft.proxy.beamPower(this.world,
                    xx - d1x * 0.05, yy - d1y * 0.05, zz - d1z * 0.05,
                    this.pos.getX() + 0.5 - d2.getXOffset() * 0.05,
                    this.pos.getY() + 0.5 - d2.getYOffset() * 0.05,
                    this.pos.getZ() + 0.5 - d2.getZOffset() * 0.05,
                    this.pRed, this.pGreen, this.pBlue, this.pulse > 0, this.beam1);
        }
        if (this.pRed < 1.0f) this.pRed = Math.min(1.0f, this.pRed + 0.025f);
        if (this.pGreen < 1.0f) this.pGreen = Math.min(1.0f, this.pGreen + 0.025f);
        if (this.pBlue < 1.0f) this.pBlue = Math.min(1.0f, this.pBlue + 0.025f);
        if (this.pulse > 0) this.pulse--;
    }

    private int ticksExisted() {
        return this.world == null ? 0 : (int)(this.world.getTotalWorldTime() & Integer.MAX_VALUE);
    }

    @Override
    public void triggerConsumeEffect(Aspect aspect) {
        addPulse(aspect);
    }

    /** Server-side: an aspect flowed through — flash a colored pulse down the beam. */
    protected void addPulse(Aspect aspect) {
        int c = -1;
        if (aspect == Aspect.AIR) c = 0;
        else if (aspect == Aspect.FIRE) c = 1;
        else if (aspect == Aspect.WATER) c = 2;
        else if (aspect == Aspect.EARTH) c = 3;
        else if (aspect == Aspect.ORDER) c = 4;
        else if (aspect == Aspect.ENTROPY) c = 5;
        if (c >= 0 && this.pulse == 0 && this.world != null) {
            this.pulse = 5;
            this.world.addBlockEvent(this.pos, ConfigBlocks.blockMetalDevice, 0, c);
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 0) {
            if (this.world != null && this.world.isRemote) {
                Color c = new Color(colors[type]);
                this.pulse = 5;
                this.pRed = c.getRed() / 255.0f;
                this.pGreen = c.getGreen() / 255.0f;
                this.pBlue = c.getBlue() / 255.0f;
                // Propagate the pulse color up the chain toward the source.
                WeakReference<TileVisNode> vr = this.getParent();
                while (VisNetHandler.isNodeValid(vr) && vr.get() instanceof TileVisRelay && ((TileVisRelay) vr.get()).pulse == 0) {
                    TileVisRelay relay = (TileVisRelay) vr.get();
                    relay.pRed = this.pRed;
                    relay.pGreen = this.pGreen;
                    relay.pBlue = this.pBlue;
                    relay.pulse = 5;
                    vr = relay.getParent();
                }
            }
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.orientation = nbt.getByte("orientation");
        this.color = nbt.hasKey("color") ? nbt.getByte("color") : -1;
        this.attunement = this.color;
        this.px = nbt.getByte("px");
        this.py = nbt.getByte("py");
        this.pz = nbt.getByte("pz");
        this.parentLoaded = true;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setByte("orientation", this.orientation);
        nbt.setByte("color", this.color);
        if (VisNetHandler.isNodeValid(this.getParent())) {
            TileVisNode parent = this.getParent().get();
            nbt.setByte("px", (byte) (this.pos.getX() - parent.getPos().getX()));
            nbt.setByte("py", (byte) (this.pos.getY() - parent.getPos().getY()));
            nbt.setByte("pz", (byte) (this.pos.getZ() - parent.getPos().getZ()));
        } else {
            nbt.setByte("px", (byte) 0);
            nbt.setByte("py", (byte) 0);
            nbt.setByte("pz", (byte) 0);
        }
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int event) {
        if (world != null && !world.isRemote) {
            cycleColor();
        }
        return 0;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
        return null;
    }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
    }

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {
    }

    private void cycleColor() {
        if (this.world == null) return;
        this.color++;
        if (this.color > 5) this.color = -1;
        this.removeThisNode();
        this.attunement = this.color;
        this.nodeRefresh = true;
        this.markDirty();
        this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        this.world.playSound(null, this.pos, TCSounds.CRYSTAL, SoundCategory.BLOCKS, 0.2F, 1.0F);
    }
}
