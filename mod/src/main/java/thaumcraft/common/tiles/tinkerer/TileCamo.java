package thaumcraft.common.tiles.tinkerer;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.TileThaumcraft;

/**
 * Holds the block a camouflaged device is disguised as — ported from Thaumic
 * Tinkerer's {@code TileCamo} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>The original stored the block's registry name and its metadata, and so
 * does this. Nothing is written until a disguise is actually set, so an
 * undisguised device carries no extra NBT.</p>
 *
 * <p>Upstream this is the base of the transvector tiles as well, and it is here
 * too — see {@link TileTransvector}.</p>
 */
public class TileCamo extends TileThaumcraft {

    private static final String TAG_CAMO = "camo";
    private static final String TAG_CAMO_META = "camoMeta";

    private Block camo;
    private int camoMeta;

    @Nullable
    public Block getCamo() {
        return this.camo;
    }

    public int getCamoMeta() {
        return this.camoMeta;
    }

    /** The disguise as a state, or null when the device wears its own face. */
    @Nullable
    public IBlockState getCamoState() {
        if (this.camo == null) {
            return null;
        }
        try {
            return this.camo.getStateFromMeta(this.camoMeta);
        } catch (RuntimeException malformedMeta) {
            return this.camo.getDefaultState();
        }
    }

    public void setCamo(@Nullable Block block, int meta) {
        this.camo = block;
        this.camoMeta = meta;
        this.markDirty();
        if (this.world != null) {
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, 3);
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound tag) {
        super.writeCustomNBT(tag);
        if (this.camo != null) {
            ResourceLocation name = Block.REGISTRY.getNameForObject(this.camo);
            if (name != null) {
                tag.setString(TAG_CAMO, name.toString());
                tag.setInteger(TAG_CAMO_META, this.camoMeta);
            }
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound tag) {
        super.readCustomNBT(tag);
        String name = tag.getString(TAG_CAMO);
        this.camo = name.isEmpty() ? null : Block.REGISTRY.getObject(new ResourceLocation(name));
        this.camoMeta = tag.getInteger(TAG_CAMO_META);
    }

    /** A new disguise has to redraw the chunk, not just update the tile. */
    @Override
    public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {
        super.onDataPacket(manager, packet);
        if (this.world != null && this.world.isRemote) {
            this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
        }
    }

    /** Keep the tile across a state change so the disguise survives it. */
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
