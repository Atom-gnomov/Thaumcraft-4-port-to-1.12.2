package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.tinkerer.TileEnchanter;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Osmotic Enchanter — reimplemented from Thaumic Tinkerer (pixlepix/nekosune,
 * originally Vazkii) for 1.12.2.
 *
 * <p>Right-clicking opens its screen, where the tool and wand go in and the
 * enchantments are chosen — as in the original.</p>
 *
 * @see TileEnchanter
 */
public class BlockEnchanter extends BlockContainer {

    /** The original's {@code setBlockBounds(0, 0, 0, 1, 0.75F, 1)}. */
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);

    public BlockEnchanter() {
        super(Material.ROCK);
        this.setHardness(5.0F);
        this.setResistance(2000.0F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEnchanter();
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
        if (!(world.getTileEntity(pos) instanceof TileEnchanter)) {
            return false;
        }
        player.openGui(Thaumcraft.instance, CommonProxy.GUI_ENCHANTER,
                world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEnchanter) {
            ItemStackHandler inv = ((TileEnchanter) te).getInventory();
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                ItemStack stored = inv.getStackInSlot(slot);
                if (!stored.isEmpty()) {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stored);
                }
            }
        }
        super.breakBlock(world, pos, state);
    }
}
