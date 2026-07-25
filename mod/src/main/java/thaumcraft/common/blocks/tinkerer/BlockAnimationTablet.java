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
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.tinkerer.TileAnimationTablet;

/**
 * Animation Tablet — reimplemented from Thaumic Tinkerer (pixlepix/nekosune,
 * originally Vazkii) for 1.12.2. Works a held tool against the block it faces.
 *
 * <p>Interaction, in place of the original's GUI: right-click inserts or
 * retrieves the tool, sneak-click switches strike/use mode, and sneak-click
 * with redstone in hand switches between running freely and waiting for a
 * pulse.</p>
 *
 * @see TileAnimationTablet
 */
public class BlockAnimationTablet extends BlockContainer {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    /** Identity the tablet acts under; block-break and attack events are attributed to it. */
    public static final GameProfile TABLET_PROFILE =
            new GameProfile(UUID.fromString("b1c4e0a2-6f3d-4a5b-9c7e-0d2f8a1b3c4d"), "[thaumcraft_tablet]");

    public BlockAnimationTablet() {
        super(Material.ROCK);
        this.setHardness(3.0F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileAnimationTablet();
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
        TileAnimationTablet tablet = (TileAnimationTablet) te;
        ItemStack held = player.getHeldItem(hand);

        if (player.isSneaking()) {
            if (!held.isEmpty() && held.getItem() == Items.REDSTONE) {
                tablet.toggleRedstoneMode();
                player.sendStatusMessage(new TextComponentTranslation(
                        tablet.isRedstoneMode() ? "tc.tablet.redstone.on" : "tc.tablet.redstone.off"), true);
            } else {
                tablet.toggleMode();
                player.sendStatusMessage(new TextComponentTranslation(
                        tablet.isStrikeMode() ? "tc.tablet.mode.strike" : "tc.tablet.mode.use"), true);
            }
            return true;
        }

        ItemStackHandler inv = tablet.getInventory();
        ItemStack stored = inv.getStackInSlot(0);
        if (!stored.isEmpty()) {
            if (!player.inventory.addItemStackToInventory(stored)) {
                player.dropItem(stored, false);
            }
            inv.setStackInSlot(0, ItemStack.EMPTY);
        }
        if (!held.isEmpty()) {
            ItemStack single = held.copy();
            single.setCount(1);
            inv.setStackInSlot(0, single);
            held.shrink(1);
        }
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
