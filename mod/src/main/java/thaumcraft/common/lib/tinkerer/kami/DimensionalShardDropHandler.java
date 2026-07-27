package thaumcraft.common.lib.tinkerer.kami;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DimensionType;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.tinkerer.kami.ItemKamiResource;

/**
 * Dimensional shard drops — ported from Thaumic Tinkerer's
 * {@code DimensionalShardDropHandler} (pixlepix/nekosune, originally Vazkii).
 *
 * <p>The two shards that start the KAMI chain only come from mobs killed by a
 * player in their own dimension, at the original's rates: an Enderman in the
 * End drops an ender shard one time in thirty-two, a Zombie Pigman in the
 * Nether drops a nether shard one time in sixteen.</p>
 */
public class DimensionalShardDropHandler {

    private static final double ENDER_SHARD_CHANCE = 1.0D / 32.0D;
    private static final double NETHER_SHARD_CHANCE = 1.0D / 16.0D;

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        EntityLivingBase victim = event.getEntityLiving();
        if (victim == null || victim.world == null
                || !(event.getSource().getTrueSource() instanceof EntityPlayer)) {
            return;
        }
        int dimension = victim.world.provider.getDimension();

        if (victim instanceof EntityEnderman
                && dimension == DimensionType.THE_END.getId()
                && Math.random() <= ENDER_SHARD_CHANCE) {
            drop(event, victim, ItemKamiResource.ENDER_SHARD);
        }
        if (victim instanceof EntityPigZombie
                && dimension == DimensionType.NETHER.getId()
                && Math.random() <= NETHER_SHARD_CHANCE) {
            drop(event, victim, ItemKamiResource.NETHER_SHARD);
        }
    }

    private static void drop(LivingDropsEvent event, EntityLivingBase victim, int meta) {
        event.getDrops().add(new EntityItem(victim.world, victim.posX, victim.posY, victim.posZ,
                new ItemStack(ConfigItems.itemKamiResource, 1, meta)));
    }
}
