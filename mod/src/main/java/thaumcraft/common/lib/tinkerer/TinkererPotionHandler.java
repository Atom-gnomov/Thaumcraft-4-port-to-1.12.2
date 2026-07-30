package thaumcraft.common.lib.tinkerer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;

/**
 * What the four primal potions actually do — ported from Thaumic Tinkerer's
 * {@code PotionEffectHandler} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Nothing happens while you merely hold the effect. Everything fires when
 * you strike something:</p>
 *
 * <ul>
 *   <li><b>Aer</b> — the target is thrown about at random for the next second.</li>
 *   <li><b>Ignis</b> — the target burns, wreathed in wisps, for the next second.</li>
 *   <li><b>Terra</b> — a five-by-five forcefield slams up between you and it,
 *       along whichever axis you are further apart on.</li>
 *   <li><b>Aqua</b> — lava within two blocks of you sets to obsidian. This one
 *       is the exception: it runs on the player's own tick, not on a hit.</li>
 * </ul>
 */
public class TinkererPotionHandler {

    /** How long a struck entity keeps being thrown about or burned: 20 ticks. */
    private static final int HIT_DURATION = 20;
    /** The effects fire every fifth tick, as upstream. */
    private static final int CADENCE = 5;

    private static final Map<Entity, Long> AIR_HITS = new HashMap<>();
    private static final Map<Entity, Long> FIRE_HITS = new HashMap<>();

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        Entity target = event.getEntity();
        if (player.world.isRemote) {
            return;
        }

        if (player.isPotionActive(ModPotionsTinkerer.potionAir)) {
            AIR_HITS.put(target, player.world.getTotalWorldTime());
        }
        if (player.isPotionActive(ModPotionsTinkerer.potionFire)) {
            FIRE_HITS.put(target, player.world.getTotalWorldTime());
        }
        if (player.isPotionActive(ModPotionsTinkerer.potionEarth)) {
            raiseWall(player, target);
        }
    }

    /**
     * The wall goes up midway between the two, five by five, standing on the
     * axis they are <em>closer</em> together on — so it blocks the line
     * between them rather than lying along it.
     */
    private static void raiseWall(EntityPlayer player, Entity target) {
        World world = player.world;
        boolean xAxis = Math.abs(target.posZ - player.posZ) < Math.abs(target.posX - player.posX);
        int centerX = (int) ((target.posX + player.posX) / 2);
        int centerY = (int) (player.posY + 2);
        int centerZ = (int) ((target.posZ + player.posZ) / 2);

        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                BlockPos pos = xAxis
                        ? new BlockPos(centerX, centerY + i, centerZ + j)
                        : new BlockPos(centerX + j, centerY + i, centerZ);
                if (world.isAirBlock(pos)) {
                    world.setBlockState(pos, ConfigBlocks.blockForcefield.getDefaultState());
                    Thaumcraft.proxy.blockSparkle(world, pos.getX(), pos.getY(), pos.getZ(), 100, 100);
                }
            }
        }
    }

    /** Aqua works on its holder rather than on what they hit. */
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (player == null || player.world.isRemote
                || !player.isPotionActive(ModPotionsTinkerer.potionWater)) {
            return;
        }
        World world = player.world;
        for (int x = (int) (player.posX - 2); x < player.posX + 2; x++) {
            for (int y = (int) (player.posY - 2); y < player.posY + 2; y++) {
                for (int z = (int) (player.posZ - 2); z < player.posZ + 2; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    net.minecraft.block.Block block = world.getBlockState(pos).getBlock();
                    if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
                        world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
                        Thaumcraft.proxy.burst(world, x + 0.5D, y + 0.5D, z + 0.5D, 1.2F);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        tickAir();
        tickFire();
    }

    private static void tickAir() {
        Random rand = new Random();
        for (Iterator<Map.Entry<Entity, Long>> it = AIR_HITS.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Entity, Long> entry = it.next();
            Entity target = entry.getKey();
            if (target.isEntityAlive() && target.world.getTotalWorldTime() % CADENCE == 0) {
                target.setVelocity(rand.nextFloat() - 0.5D, rand.nextFloat(), rand.nextFloat() - 0.5D);
                Thaumcraft.proxy.burst(target.world, target.posX, target.posY, target.posZ, 0.5F);
            }
            if (target.world.getTotalWorldTime() > entry.getValue() + HIT_DURATION) {
                it.remove();
            }
        }
    }

    private static void tickFire() {
        Random rand = new Random();
        for (Iterator<Map.Entry<Entity, Long>> it = FIRE_HITS.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Entity, Long> entry = it.next();
            Entity target = entry.getKey();
            if (target.isEntityAlive() && target.world.getTotalWorldTime() % CADENCE == 0) {
                target.setFire(6);
                // Thirty wisps scattered over a sphere of radius 2.5.
                for (int i = 0; i < 30; i++) {
                    double theta = rand.nextFloat() * 2 * Math.PI;
                    double phi = rand.nextFloat() * 2 * Math.PI;
                    double r = 2.5D;
                    double x = r * Math.sin(theta) * Math.cos(phi);
                    double y = r * Math.sin(theta) * Math.sin(phi);
                    double z = r * Math.cos(theta);
                    Thaumcraft.proxy.wispFX2(target.world, target.posX + x, target.posY + y + 1,
                            target.posZ + z, 0.1F, 4, true, true, 1.0F);
                }
            }
            if (target.world.getTotalWorldTime() > entry.getValue() + HIT_DURATION) {
                it.remove();
            }
        }
    }
}
