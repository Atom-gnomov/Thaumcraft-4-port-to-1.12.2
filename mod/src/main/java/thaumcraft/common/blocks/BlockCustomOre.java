package thaumcraft.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.block.BlockTC;

public class BlockCustomOre extends BlockTC {
    public BlockCustomOre() {
        super(Material.ROCK);
        this.setResistance(5.0f);
        this.setHardness(1.5f);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int m = 0; m <= 7; m++) {
            items.add(new ItemStack(this, 1, m));
        }
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int md = getMetaFromState(state);
        if (md == 0) {
            drops.add(new ItemStack(this, 1, 0));
        } else if (md == 7) {
            drops.add(new ItemStack(ConfigItems.itemResource, 1 + RANDOM.nextInt(fortune + 1), 6));
        } else {
            int q = 1 + RANDOM.nextInt(2 + fortune);
            for (int a = 0; a < q; a++) {
                drops.add(new ItemStack(ConfigItems.itemShard, 1, md - 1));
            }
        }
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        int md = getMetaFromState(state);
        if (md != 0 && md != 7) {
            return MathHelper.getInt(RANDOM, 0, 3);
        }
        if (md == 7) {
            return MathHelper.getInt(RANDOM, 1, 4);
        }
        return super.getExpDrop(state, world, pos, fortune);
    }
}
