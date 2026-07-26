package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.tiles.tinkerer.TileFunnel;

/**
 * Funnel — ported from Thaumic Tinkerer's {@code BlockFunnel}
 * (pixlepix / nekosune / Vazkii). Right-click puts a filled jar in or takes it
 * back out; the tile then drips that jar's essentia into whatever the hopper
 * beneath it points at (see {@link TileFunnel}).
 */
public class BlockFunnel extends BlockContainer {

    public BlockFunnel() {
        super(Material.IRON);
        this.setHardness(3.0F);
        this.setResistance(8.0F);
        this.setSoundType(net.minecraft.block.SoundType.METAL);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileFunnel();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileFunnel)) {
            return false;
        }
        IItemHandler inventory = ((TileFunnel) tile).getInventory();
        ItemStack held = player.getHeldItem(hand);

        if (!held.isEmpty()) {
            ItemStack remainder = inventory.insertItem(0, held.copy(), world.isRemote);
            if (remainder.getCount() != held.getCount()) {
                if (!world.isRemote) {
                    player.setHeldItem(hand, remainder);
                    tile.markDirty();
                }
                return true;
            }
            return false;
        }

        ItemStack stored = inventory.extractItem(0, 1, world.isRemote);
        if (!stored.isEmpty()) {
            if (!world.isRemote) {
                if (!player.inventory.addItemStackToInventory(stored)) {
                    player.dropItem(stored, false);
                }
                tile.markDirty();
            }
            return true;
        }
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileFunnel) {
            IItemHandler inventory = ((TileFunnel) tile).getInventory();
            ItemStack stored = inventory.extractItem(0, inventory.getSlotLimit(0), false);
            if (!stored.isEmpty()) {
                net.minecraft.block.Block.spawnAsEntity(world, pos, stored);
            }
        }
        super.breakBlock(world, pos, state);
    }
}
