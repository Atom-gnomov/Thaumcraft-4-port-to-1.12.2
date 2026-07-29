package thaumcraft.common.blocks.tinkerer.kami;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabTinkerer;
import thaumcraft.common.tiles.tinkerer.kami.TileWarpGate;

/**
 * The warp gate — the port of Thaumic Tinkerer's {@code BlockWarpGate}.
 *
 * <p>Right-clicking opens its destination list. Breaking it scatters the
 * pearls it held, in the same scattered-stack way the original did.</p>
 */
public class BlockWarpGate extends BlockContainer {

    private final Random random = new Random();

    public BlockWarpGate() {
        super(Material.ROCK);
        setHardness(5.0F);
        setResistance(2000.0F);
        setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                    EntityPlayer player, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null) {
                player.openGui(thaumcraft.common.Thaumcraft.instance,
                        thaumcraft.common.CommonProxy.GUI_WARP_GATE,
                        world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileWarpGate) {
            TileWarpGate gate = (TileWarpGate) tile;
            for (int slot = 0; slot < gate.getSizeInventory(); ++slot) {
                ItemStack stack = gate.getStackInSlot(slot);
                if (stack.isEmpty()) {
                    continue;
                }
                float fx = this.random.nextFloat() * 0.8F + 0.1F;
                float fy = this.random.nextFloat() * 0.8F + 0.1F;
                float fz = this.random.nextFloat() * 0.8F + 0.1F;
                while (stack.getCount() > 0) {
                    int take = Math.min(this.random.nextInt(21) + 10, stack.getCount());
                    stack.shrink(take);
                    ItemStack dropped = new ItemStack(stack.getItem(), take, stack.getItemDamage());
                    if (stack.hasTagCompound()) {
                        dropped.setTagCompound(stack.getTagCompound().copy());
                    }
                    EntityItem item = new EntityItem(world,
                            pos.getX() + fx, pos.getY() + fy, pos.getZ() + fz, dropped);
                    item.motionX = (float) this.random.nextGaussian() * 0.05F;
                    item.motionY = (float) this.random.nextGaussian() * 0.05F + 0.2F;
                    item.motionZ = (float) this.random.nextGaussian() * 0.05F;
                    world.spawnEntity(item);
                }
            }
            world.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(world, pos, state);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileWarpGate();
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
}
