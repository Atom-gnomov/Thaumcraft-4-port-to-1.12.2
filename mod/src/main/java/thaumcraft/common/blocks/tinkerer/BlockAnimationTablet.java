package thaumcraft.common.blocks.tinkerer;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.tinkerer.TileAnimationTablet;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Animation Tablet — reimplemented from Thaumic Tinkerer (pixlepix/nekosune,
 * originally Vazkii) for 1.12.2. Works a held tool against the block it faces.
 *
 * <p>Interaction matches the original: a wand rotates the tablet, anything
 * else opens its screen, where the tool goes in and the strike/use and
 * redstone-control toggles live.</p>
 *
 * @see TileAnimationTablet
 */
public class BlockAnimationTablet extends BlockContainer {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    /** Identity the tablet acts under; block-break and attack events are attributed to it. */
    public static final GameProfile TABLET_PROFILE =
            new GameProfile(UUID.fromString("b1c4e0a2-6f3d-4a5b-9c7e-0d2f8a1b3c4d"), "[thaumcraft_tablet]");

    /** The original's {@code setBlockBounds(0, 0, 0, 1, 2F / 16F, 1)}. */
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 2.0D / 16.0D, 1.0D);

    public BlockAnimationTablet() {
        super(Material.IRON);
        this.setHardness(3.0F);
        this.setResistance(50.0F);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileAnimationTablet();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, net.minecraft.world.IBlockAccess source, BlockPos pos) {
        return SHAPE;
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
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileAnimationTablet)) {
            return false;
        }
        ItemStack held = player.getHeldItem(hand);

        // As in the original: a wand rotates the tablet, anything else opens the GUI.
        if (!held.isEmpty() && held.getItem() instanceof ItemWandCasting) {
            world.setBlockState(pos, state.withProperty(FACING, state.getValue(FACING).rotateY()), 3);
            world.playSound(null, pos, TCSounds.TOOL, SoundCategory.BLOCKS, 0.6F, 1.0F);
            return true;
        }
        player.openGui(Thaumcraft.instance, CommonProxy.GUI_ANIMATION_TABLET,
                world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    /** Fires a queued swing on the rising edge when the tablet waits for redstone. */
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (world.isRemote || !world.isBlockPowered(pos)) {
            return;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileAnimationTablet) {
            ((TileAnimationTablet) te).onRedstonePulse();
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileAnimationTablet) {
            ItemStack stored = ((TileAnimationTablet) te).getInventory().getStackInSlot(0);
            if (!stored.isEmpty()) {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stored);
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byIndex(meta & 3);
        if (facing.getAxis() == EnumFacing.Axis.Y) {
            facing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }
}
