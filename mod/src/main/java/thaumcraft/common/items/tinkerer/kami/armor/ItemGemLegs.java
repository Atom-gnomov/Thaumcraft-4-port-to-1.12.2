package thaumcraft.common.items.tinkerer.kami.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.blocks.tinkerer.gas.BlockGas;

/**
 * Leggings of the Burning Mantle — ported from Thaumic Tinkerer's
 * {@code ItemGemLegs} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Fire does not hurt you in these; it heals you for what it would have
 * done. They also lay a short trail of nitor light ahead of you — one block
 * overhead and five more along the way you are facing.</p>
 */
public class ItemGemLegs extends ItemIchorclothArmorAdv {

    /** How far ahead the light is laid, in blocks — the original's five. */
    private static final int TRAIL_LENGTH = 5;

    public ItemGemLegs() {
        super(EntityEquipmentSlot.LEGS);
    }

    @Override
    protected boolean ticks() {
        return true;
    }

    @Override
    protected void tickPlayer(EntityPlayer player) {
        if (!isActive(player)) {
            return;
        }
        World world = player.world;
        int x = MathHelper.floor(player.posX);
        int y = (int) player.posY + 1;
        int z = MathHelper.floor(player.posZ);

        // Overhead first, then along the facing — as upstream does.
        light(world, new BlockPos(x, y, z));

        float yaw = MathHelper.wrapDegrees(player.rotationYaw + 90.0F) * (float) Math.PI / 180.0F;
        double dx = Math.cos(yaw);
        double dz = Math.sin(yaw);
        for (int step = 1; step <= TRAIL_LENGTH; step++) {
            light(world, new BlockPos(x + (int) (dx * step), y, z + (int) (dz * step)));
        }
    }

    /**
     * Lays a nitor gas block, at the spread value that marks it as the
     * leggings' work — brighter, and it searches further for its owner.
     */
    private static void light(World world, BlockPos pos) {
        if (world.isRemote) {
            return;
        }
        if (world.isAirBlock(pos) || world.getBlockState(pos).getBlock() == ConfigBlocks.blockNitorGas) {
            world.setBlockState(pos, ConfigBlocks.blockNitorGas.getDefaultState()
                    .withProperty(BlockGas.SPREAD, 1), 2);
        }
    }

    /** Fire heals instead of hurting — at the lowest priority, as upstream. */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDamageTaken(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (event.getSource().isFireDamage() && isActive(player)) {
            event.setCanceled(true);
            player.heal(event.getAmount());
        }
    }
}
