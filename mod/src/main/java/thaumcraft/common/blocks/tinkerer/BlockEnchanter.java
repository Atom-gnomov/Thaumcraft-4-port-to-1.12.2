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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.tinkerer.TileEnchanter;

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

    public BlockEnchanter() {
        super(Material.ROCK);
        this.setHardness(3.0F);
        this.setResistance(12.0F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEnchanter();
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
