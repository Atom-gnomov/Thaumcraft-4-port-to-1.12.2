package thaumcraft.common.blocks;

import java.util.Random;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;

/**
 * Arcane Door — Phase 1 port as a vanilla-style {@link BlockDoor}: working open/close, two halves,
 * facing and hinge all handled by the base class. TC4's original is <i>warded</i> (a {@code TileOwned}
 * lets only the owner open it); that ownership gate is <b>TODO Phase 3</b>.
 *
 * <p>{@code BlockDoor#getItem()} (no-arg) is private and hard-codes the vanilla wood/iron doors, so
 * the two public entry points that call it are overridden to yield our own door item instead.
 */
public class BlockArcaneDoor extends BlockDoor {
    public BlockArcaneDoor() {
        super(Material.IRON);
        // TC4: Config.wardedStone ? -1 : 15 — default config leaves it breakable at hardness 15.
        this.setHardness(15.0f);
        this.setResistance(999.0f);
        this.setSoundType(SoundType.METAL);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(HALF) == EnumDoorHalf.UPPER ? Items.AIR : ConfigBlocks.itemArcaneDoor;
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(ConfigBlocks.itemArcaneDoor);
    }
}
