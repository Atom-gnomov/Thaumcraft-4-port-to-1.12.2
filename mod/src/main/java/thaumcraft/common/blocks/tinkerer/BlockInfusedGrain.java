package thaumcraft.common.blocks.tinkerer;

import java.util.Random;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.tinkerer.PrimalCrop;

/**
 * An infused crop growing in a field — ported from Thaumic Tinkerer's
 * {@code BlockInfusedGrain} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>There is one block per primal, exactly as upstream: the aspect is fixed at
 * construction rather than stored in metadata, because metadata is already
 * spoken for by the growth stage. It behaves as wheat does, and yields its own
 * seed and its own grain.</p>
 */
public class BlockInfusedGrain extends BlockCrops {

    private final PrimalCrop crop;

    public BlockInfusedGrain(PrimalCrop crop) {
        this.crop = crop;
    }

    public PrimalCrop getPrimal() {
        return this.crop;
    }

    @Override
    protected Item getSeed() {
        return ConfigItems.itemInfusedSeeds;
    }

    @Override
    protected Item getCrop() {
        return ConfigItems.itemInfusedGrain;
    }

    /** Seed and grain both carry this crop's primal in their damage. */
    @Override
    public int damageDropped(IBlockState state) {
        return this.crop.ordinal();
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos,
                        IBlockState state, int fortune) {
        int age = this.getAge(state);
        Random rand = world instanceof net.minecraft.world.World
                ? ((net.minecraft.world.World) world).rand : new Random();

        if (age >= this.getMaxAge()) {
            drops.add(new ItemStack(getCrop(), 1, this.crop.ordinal()));
        }
        // One seed back always, and up to three more once grown — vanilla's rule.
        drops.add(new ItemStack(getSeed(), 1, this.crop.ordinal()));
        if (age >= this.getMaxAge()) {
            for (int i = 0; i < 3 + fortune; i++) {
                if (rand.nextInt(2 * this.getMaxAge()) <= age) {
                    drops.add(new ItemStack(getSeed(), 1, this.crop.ordinal()));
                }
            }
        }
    }

    @Override
    public ItemStack getItem(net.minecraft.world.World world, BlockPos pos, IBlockState state) {
        return new ItemStack(getSeed(), 1, this.crop.ordinal());
    }
}
