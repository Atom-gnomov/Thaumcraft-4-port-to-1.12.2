package thaumcraft.common.items.tinkerer.kami.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Boots of the Horizontal Shield — ported from Thaumic Tinkerer's
 * {@code ItemGemBoots} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Haste while worn, a full block of step height (halved when sneaking), a
 * push forward as you walk or fly, a higher jump, no fall damage, and grass
 * springing up from the plain dirt underfoot.</p>
 */
public class ItemGemBoots extends ItemIchorclothArmorAdv {

    public ItemGemBoots() {
        super(EntityEquipmentSlot.FEET);
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
        player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 2, 1, true, false));
        if (player.world.isRemote) {
            player.stepHeight = player.isSneaking() ? 0.5F : 1.0F;
        }
        if ((player.onGround || player.capabilities.isFlying) && player.moveForward > 0.0F) {
            player.moveRelative(0.0F, 0.0F, 1.0F,
                    player.capabilities.isFlying ? 0.075F : 0.15F);
        }
        player.jumpMovementFactor = player.isSprinting() ? 0.05F : 0.04F;
        player.fallDistance = 0.0F;

        // Plain dirt underfoot turns to grass.
        BlockPos below = new BlockPos((int) player.posX, (int) player.posY - 1, (int) player.posZ);
        if (player.world.getBlockState(below).getBlock() == Blocks.DIRT
                && player.world.getBlockState(below).getBlock()
                .getMetaFromState(player.world.getBlockState(below)) == 0) {
            player.world.setBlockState(below, Blocks.GRASS.getDefaultState(), 2);
        }
    }

    @SubscribeEvent
    public void onPlayerJump(LivingJumpEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer
                && isActive((EntityPlayer) event.getEntityLiving())) {
            event.getEntityLiving().motionY += 0.3D;
        }
    }

    /**
     * Upstream added this on top of zeroing fallDistance every tick, calling it
     * redundant in theory and keeping it because falls still hurt on servers.
     */
    @SubscribeEvent
    public void onFall(LivingFallEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer
                && isActive((EntityPlayer) event.getEntityLiving())) {
            event.setCanceled(true);
        }
    }
}
