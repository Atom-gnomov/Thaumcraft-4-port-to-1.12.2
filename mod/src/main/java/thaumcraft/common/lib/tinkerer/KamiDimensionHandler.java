package thaumcraft.common.lib.tinkerer;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.items.tinkerer.kami.tool.ItemIchorPickAdv;
import thaumcraft.common.lib.world.dim.bedrock.WorldProviderBedrock;

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

    /** Below this the player is falling through the void and will never land. */
    private static final double VOID_FLOOR = -8.0D;

    /**
     * Catches a player who has fallen out of the Bedrock dimension and puts them
     * back in the overworld.
     *
     * <p><b>Owner's request, 2026-07-30</b> — not upstream behaviour. The
     * dimension's floor is bedrock the pick can dig straight through, and below
     * it there is nothing: a player who broke through kept falling with no way
     * back and no death to release them, because the void does not damage a
     * player here the way it does in the overworld.</p>
     *
     * <p>They arrive at the overworld's spawn, at the surface, with fall
     * distance cleared so the trip does not kill them on landing.</p>
     */
    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        if (player.world.isRemote
                || !(player.world.provider instanceof WorldProviderBedrock)
                || player.posY > VOID_FLOOR) {
            return;
        }

        net.minecraft.server.MinecraftServer server =
                FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) {
            return;
        }
        server.getPlayerList().transferPlayerToDimension(player, 0,
                new net.minecraftforge.common.util.ITeleporter() {
                    @Override
                    public void placeEntity(World world, net.minecraft.entity.Entity entity, float yaw) {
                        BlockPos spawn = world.getTopSolidOrLiquidBlock(world.getSpawnPoint());
                        entity.setPositionAndUpdate(spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D);
                    }
                });
        player.fallDistance = 0.0F;
        player.motionY = 0.0D;
    }
}
