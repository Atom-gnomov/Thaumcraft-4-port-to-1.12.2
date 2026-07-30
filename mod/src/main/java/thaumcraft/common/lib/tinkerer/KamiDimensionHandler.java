package thaumcraft.common.lib.tinkerer;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.items.tinkerer.kami.tool.ItemIchorPickAdv;

/**
 * Opens the way into the Bedrock dimension — ported from Thaumic Tinkerer's
 * {@code KamiDimensionHandler} (Katrina, for pixlepix / nekosune).
 *
 * <p>The Awakened Ichor Pickaxe turns bedrock into the dimension's portal, and
 * that lives in its {@code onBlockStartBreak}. Nothing ever calls it there:
 * bedrock's hardness is {@code -1}, so {@code ForgeHooks.blockStrength} returns
 * zero, the break never completes, and {@code tryHarvestBlock} — the only thing
 * that would have invoked {@code onBlockStartBreak} — is never reached.</p>
 *
 * <p>Upstream's answer is this handler, and it is the whole reason the feature
 * works there: watch for a left click on bedrock and call the pick's hook by
 * hand. The port carried the hook across and left this behind, so striking
 * bedrock did nothing at all.</p>
 */
public class KamiDimensionHandler {

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getWorld().getBlockState(event.getPos()).getBlock() != Blocks.BEDROCK) {
            return;
        }
        ItemStack held = event.getEntityPlayer().getHeldItemMainhand();
        if (held.isEmpty() || !(held.getItem() instanceof ItemIchorPickAdv)) {
            return;
        }
        held.getItem().onBlockStartBreak(held, event.getPos(), event.getEntityPlayer());
    }
}
