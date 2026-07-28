package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import thaumcraft.common.tiles.tinkerer.TileCamo;

/**
 * Base for the devices that can be disguised as another block — ported from
 * Thaumic Tinkerer's {@code BlockCamo} (pixlepix / nekosune, originally
 * Vazkii).
 *
 * <p>Right-clicking with a block in hand takes on that block's appearance;
 * right-clicking with an empty hand drops the disguise. Only ordinary,
 * full-model blocks qualify — the original tested the render type against a
 * short whitelist, which in 1.12.2 is {@link EnumBlockRenderType#MODEL}.</p>
 *
 * <p>1.7.10 did this by overriding {@code getIcon} per face. Here the disguise
 * travels as an unlisted property and the drawing is done by
 * {@code CamoBakedModel}, which is the same idea in this version's terms.</p>
 */
public abstract class BlockCamo extends BlockContainer {

    /** The block being imitated, or absent when the device wears its own face. */
    public static final IUnlistedProperty<IBlockState> CAMO = new IUnlistedProperty<IBlockState>() {
        @Override
        public String getName() {
            return "camo";
        }

        @Override
        public boolean isValid(IBlockState value) {
            return true;
        }

        @Override
        public Class<IBlockState> getType() {
            return IBlockState.class;
        }

        @Override
        public String valueToString(IBlockState value) {
            return value == null ? "none" : value.toString();
        }
    };

    protected BlockCamo(Material material) {
        super(material);
    }

    /** Whether {@code block} is something this device can be disguised as. */
    public boolean isValidRenderType(IBlockState state) {
        return state.getRenderType() == EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileCamo)) {
            return false;
        }
        TileCamo camo = (TileCamo) tile;
        ItemStack held = player.getHeldItem(hand);

        Block block = null;
        if (!held.isEmpty()) {
            block = Block.getBlockFromItem(held.getItem());
            if (block == null || block == net.minecraft.init.Blocks.AIR || block instanceof BlockCamo
                    || !isValidRenderType(block.getDefaultState())) {
                return false;
            }
        }

        int meta = held.isEmpty() ? 0 : held.getItemDamage();
        if (block != null) {
            meta = faceMetadata(block, meta, side);
        }
        camo.setCamo(block, meta);
        return true;
    }

    /**
     * Turns a directional disguise to face the player, as the original's
     * {@code metadata & 12 | n} switch did for {@link BlockDirectional}.
     */
    private static int faceMetadata(Block block, int meta, EnumFacing side) {
        if (!(block instanceof BlockDirectional) && !(block instanceof BlockHorizontal)) {
            return meta;
        }
        switch (side) {
            case DOWN:
            case UP:
                return meta;
            case NORTH:
                return meta & 12 | 2;
            case SOUTH:
                return meta & 12;
            case WEST:
                return meta & 12 | 1;
            case EAST:
                return meta & 12 | 3;
            default:
                return meta;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCamo();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, listedProperties(),
                new IUnlistedProperty[]{CAMO});
    }

    /** Subclasses with their own state add their properties here. */
    protected net.minecraft.block.properties.IProperty<?>[] listedProperties() {
        return new net.minecraft.block.properties.IProperty<?>[0];
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (!(state instanceof IExtendedBlockState)) {
            return state;
        }
        TileEntity tile = world.getTileEntity(pos);
        IBlockState disguise = tile instanceof TileCamo ? ((TileCamo) tile).getCamoState() : null;
        return ((IExtendedBlockState) state).withProperty(CAMO, disguise);
    }
}
