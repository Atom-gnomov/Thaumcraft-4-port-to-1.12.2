package thaumcraft.common.items.tinkerer;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.blocks.tinkerer.gas.BlockGas;
import thaumcraft.common.lib.CreativeTabThaumcraft;

/**
 * A bottled gas — ported from Thaumic Tinkerer's {@code ItemGas}
 * (pixlepix / nekosune, originally Vazkii). One class serves both the
 * Illuminae and the Tenebrae; each instance carries the block it releases.
 *
 * <p>Right-click and it pops the gas out one block above your head at spread 4,
 * so it blooms four blocks outward from there. The bottle is spent whether or
 * not there was room, which is upstream's behaviour and not an oversight in the
 * port — the count drops before the air check.</p>
 */
public class ItemGas extends Item {

    /** The original's placement: metadata 4, so it spreads four blocks. */
    private static final int RELEASE_SPREAD = 4;

    private final Block released;

    public ItemGas(Block released) {
        this.released = released;
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    public Block getReleased() {
        return this.released;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        BlockPos pos = new BlockPos((int) player.posX, (int) player.posY + 1, (int) player.posZ);
        boolean air = world.isAirBlock(pos);

        if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS,
                0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (air) {
            if (!world.isRemote) {
                world.setBlockState(pos, this.released.getDefaultState()
                        .withProperty(BlockGas.SPREAD, RELEASE_SPREAD), 2);
            } else {
                player.swingArm(hand);
            }
            world.scheduleUpdate(pos, this.released, 10);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
