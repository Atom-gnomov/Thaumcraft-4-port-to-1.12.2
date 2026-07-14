package thaumcraft.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.block.BlockTC;

// Phase 1 port: eldritch structure stone (obsidian-tile family from the outer lands / eldritch dungeons).
// Meta 0..10 map to distinct structure pieces; only meta 4 (Glowing Crusted Stone) is obtainable in creative,
// exactly like the original (func_149666_a adds only meta 4). Per-meta light / hardness / blast-resistance are
// reproduced so future worldgen metas behave 1:1.
// TODO Phase 3: BlockContainer + TileEntities (altar meta0, obelisk meta1, capstone meta3, lock meta8,
//   crab-spawner meta9, trap meta10), eldritch-eye altar interaction, lock unlocking, spark/rune particles,
//   removal chain-collapse+explosion (func_149749_a), ThaumcraftApi.portableHoleBlackList, connected-texture
//   variants (func_149673_e for meta 8/10). Phase 4: dungeon worldgen placement.
public class BlockEldritch extends BlockTC {

    public BlockEldritch() {
        super(Material.ROCK);
        this.setHardness(50.0f);
        // Original func_149752_b(20000) → blockResistance 60000 → default explosion resistance 12000 (metas 0-3).
        this.setResistance(20000.0f);
        this.setSoundType(net.minecraft.block.SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setTickRandomly(true);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        // Only the Glowing Crusted Stone brick is player-obtainable (matches original).
        items.add(new ItemStack(this, 1, 4));
    }

    @Override
    public int getLightValue(IBlockState state) {
        int meta = state.getValue(META);
        if (meta == 4 || meta == 5 || meta == 7) {
            return 12;
        }
        if (meta == 6 || meta == 8) {
            return 5;
        }
        if (meta == 9) {
            return 4;
        }
        if (meta == 10) {
            return 0;
        }
        return 8;
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        int meta = state.getValue(META);
        if (meta == 4 || meta == 5) {
            return 2.0f;
        }
        if (meta == 6) {
            return 4.0f;
        }
        if (meta == 7 || meta == 8) {
            return -1.0f; // unbreakable
        }
        if (meta == 9 || meta == 10) {
            return 15.0f;
        }
        return 50.0f;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        int meta = world.getBlockState(pos).getValue(META);
        if (meta == 4 || meta == 5 || meta == 9 || meta == 10) {
            return 30.0f;
        }
        if (meta == 6) {
            return 100.0f;
        }
        if (meta == 7 || meta == 8) {
            return Float.MAX_VALUE;
        }
        return 12000.0f; // metas 0-3: original super path = blockResistance(60000)/5.
    }
}
