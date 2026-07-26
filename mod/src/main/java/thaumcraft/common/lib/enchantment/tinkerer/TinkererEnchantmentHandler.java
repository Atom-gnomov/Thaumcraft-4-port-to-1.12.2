package thaumcraft.common.lib.enchantment.tinkerer;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.lib.TCSounds;

/**
 * Effects for Thaumic Tinkerer's own enchantments — ported 1:1 from the
 * original's {@code ModEnchantmentHandler} (pixlepix / nekosune / Vazkii),
 * event for event and formula for formula.
 */
public class TinkererEnchantmentHandler {

    public static final String NBT_LAST_TARGET = "TTEnchantLastTarget";
    public static final String NBT_SUCCESSIVE_STRIKE = "TTEnchantSuccessiveStrike";
    public static final String NBT_TUNNEL_DIRECTION = "TTEnchantTunnelDir";

    @SubscribeEvent
    public void onEntityDamaged(LivingHurtEvent event) {
        if (!(event.getSource().getTrueSource() instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
        ItemStack heldItem = attacker.getHeldItemMainhand();
        if (heldItem.isEmpty()) {
            return;
        }
        if (!heldItem.hasTagCompound()) {
            heldItem.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tag = heldItem.getTagCompound();

        // Pounce: stronger while airborne, checked by the block underfoot.
        if (attacker instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attacker;
            ItemStack legs = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
            int pounce = EnchantmentHelper.getEnchantmentLevel(ModEnchantmentsTinkerer.pounce, legs);
            if (pounce > 0) {
                BlockPos below = new BlockPos(MathHelper.floor(player.posX),
                        MathHelper.floor(player.posY) - 1, MathHelper.floor(player.posZ));
                if (player.world.getBlockState(below).getBlock() == Blocks.AIR) {
                    event.setAmount((float) (event.getAmount() * (1 + 0.25 * pounce)));
                }
            }
        }

        int finalStrike = EnchantmentHelper.getEnchantmentLevel(ModEnchantmentsTinkerer.finalStrike, heldItem);
        if (finalStrike > 0) {
            Random rand = new Random();
            if (rand.nextInt(20 - finalStrike) == 0) {
                event.setAmount(event.getAmount() * 3);
            }
        }

        int valiance = EnchantmentHelper.getEnchantmentLevel(ModEnchantmentsTinkerer.valiance, heldItem);
        if (valiance > 0 && attacker.getHealth() / attacker.getMaxHealth() < 0.5F) {
            event.setAmount((float) (event.getAmount() * (1 + 0.1 * valiance)));
        }

        int focusedStrikes = EnchantmentHelper.getEnchantmentLevel(ModEnchantmentsTinkerer.focusedStrike, heldItem);
        int dispersedStrikes = EnchantmentHelper.getEnchantmentLevel(ModEnchantmentsTinkerer.dispersedStrikes, heldItem);
        if (focusedStrikes > 0 || dispersedStrikes > 0) {
            int lastTarget = tag.getInteger(NBT_LAST_TARGET);
            int successiveStrikes = tag.getInteger(NBT_SUCCESSIVE_STRIKE);
            int entityId = event.getEntityLiving().getEntityId();
            if (lastTarget != entityId) {
                tag.setInteger(NBT_SUCCESSIVE_STRIKE, 0);
                successiveStrikes = 0;
            } else {
                tag.setInteger(NBT_SUCCESSIVE_STRIKE, successiveStrikes + 1);
                successiveStrikes = 1;
            }
            if (focusedStrikes > 0) {
                float amount = event.getAmount() / 2.0F;
                amount += (float) (0.5 * successiveStrikes * amount * focusedStrikes);
                event.setAmount(amount);
            }
            if (dispersedStrikes > 0) {
                float amount = event.getAmount() * (1 + successiveStrikes / 5);
                amount /= (1 + successiveStrikes * 2);
                event.setAmount(amount);
            }
            tag.setInteger(NBT_LAST_TARGET, entityId);
        }

        int vampirism = EnchantmentHelper.getEnchantmentLevel(ModEnchantmentsTinkerer.vampirism, heldItem);
        if (vampirism > 0) {
            attacker.heal(vampirism);
            event.getEntityLiving().world.playSound(null, event.getEntityLiving().posX,
                    event.getEntityLiving().posY, event.getEntityLiving().posZ,
                    TCSounds.ZAP, SoundCategory.PLAYERS, 0.6F, 1.0F);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEntityUpdate(LivingUpdateEvent event) {
        final double min = -0.0784000015258789D;
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();

        // Slow Fall: eases the descent once the fall is already dangerous.
        int slowfall = EnchantmentHelper.getMaxEnchantmentLevel(ModEnchantmentsTinkerer.slowFall, player);
        if (slowfall > 0 && !player.isSneaking() && player.motionY < min && player.fallDistance >= 2.9F) {
            player.motionY /= 1 + slowfall * 0.33F;
            player.fallDistance = Math.max(2.9F, player.fallDistance - slowfall / 3.0F);
            player.world.spawnParticle(EnumParticleTypes.CLOUD,
                    player.posX + 0.25D, player.posY - 1.0D, player.posZ + 0.25D,
                    -player.motionX, player.motionY, -player.motionZ);
        }

        // Quick Draw: pulls the bow's use timer forward.
        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.isEmpty()) {
            return;
        }
        int quickDraw = EnchantmentHelper.getEnchantmentLevel(ModEnchantmentsTinkerer.quickDraw, heldItem);
        ItemStack usingItem = player.getActiveItemStack();
        int time = player.getItemInUseCount();
        if (quickDraw > 0 && !usingItem.isEmpty() && usingItem.getItem() instanceof ItemBow
                && (usingItem.getItem().getMaxItemUseDuration(usingItem) - time) % (6 - quickDraw) == 0) {
            thaumcraft.common.lib.utils.Utils.setItemInUseCount(player, time - 1);
        }
    }

    @SubscribeEvent
    public void onPlayerJump(LivingJumpEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        int boost = EnchantmentHelper.getMaxEnchantmentLevel(ModEnchantmentsTinkerer.ascentBoost, player);
        if (boost >= 1 && !player.isSneaking()) {
            player.motionY *= (boost + 2) / 2.0D;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onFall(LivingFallEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        ItemStack boots = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        int shockwave = EnchantmentHelper.getEnchantmentLevel(ModEnchantmentsTinkerer.shockwave, boots);
        if (shockwave <= 0) {
            return;
        }
        AxisAlignedBB box = new AxisAlignedBB(
                player.posX - 10.0D, player.posY - 10.0D, player.posZ - 10.0D,
                player.posX + 10.0D, player.posY + 10.0D, player.posZ + 10.0D);
        List<EntityLivingBase> targets = player.world.getEntitiesWithinAABB(EntityLivingBase.class, box);
        for (EntityLivingBase target : targets) {
            if (target != player && event.getDistance() > 3.0F) {
                target.attackEntityFrom(DamageSource.FALL, 0.1F * shockwave * event.getDistance());
            }
        }
    }

    /** Tunnel remembers the heading each break, to reward digging straight. */
    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        ItemStack item = event.getPlayer().getHeldItemMainhand();
        if (item.isEmpty()) {
            return;
        }
        int tunnel = EnchantmentHelper.getEnchantmentLevel(ModEnchantmentsTinkerer.tunnel, item);
        if (tunnel > 0) {
            if (!item.hasTagCompound()) {
                item.setTagCompound(new NBTTagCompound());
            }
            item.getTagCompound().setFloat(NBT_TUNNEL_DIRECTION, event.getPlayer().rotationYaw);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGetHarvestSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack heldItem = event.getEntityPlayer().getHeldItemMainhand();
        if (heldItem.isEmpty()) {
            return;
        }

        // Shatter: made for the very hard blocks, slightly worse on everything else.
        int shatter = EnchantmentHelper.getEnchantmentLevel(ModEnchantmentsTinkerer.shatter, heldItem);
        if (shatter > 0) {
            if (event.getState().getBlock().getBlockHardness(event.getState(),
                    event.getEntityPlayer().world, event.getPos()) > 20.0F) {
                event.setNewSpeed(event.getNewSpeed() * (3 * shatter));
            } else {
                event.setNewSpeed(event.getNewSpeed() * 0.8F);
            }
        }

        // Tunnel: faster while still facing the way you were, far slower if you turn.
        int tunnel = EnchantmentHelper.getEnchantmentLevel(ModEnchantmentsTinkerer.tunnel, heldItem);
        if (tunnel > 0 && heldItem.hasTagCompound()
                && heldItem.getTagCompound().hasKey(NBT_TUNNEL_DIRECTION)) {
            float oldDir = heldItem.getTagCompound().getFloat(NBT_TUNNEL_DIRECTION);
            float dif = Math.abs(oldDir - event.getEntityPlayer().rotationYaw);
            if (dif < 50.0F) {
                event.setNewSpeed((float) (event.getNewSpeed() * (1 + 0.2 * tunnel)));
            } else {
                event.setNewSpeed(event.getNewSpeed() * 0.3F);
            }
        }

        // Desintegrate and Auto Smelt each shatter their own kind of block
        // instantly for one durability, and refuse to mine anything else.
        int desintegrate = EnchantmentHelper.getEnchantmentLevel(ModEnchantmentsTinkerer.desintegrate, heldItem);
        int autoSmelt = EnchantmentHelper.getEnchantmentLevel(ModEnchantmentsTinkerer.autoSmelt, heldItem);
        float hardness = event.getState().getBlock().getBlockHardness(event.getState(),
                event.getEntityPlayer().world, event.getPos());
        boolean desintegrateApplies = desintegrate > 0 && hardness <= 1.5F;
        boolean autoSmeltApplies = autoSmelt > 0
                && event.getState().getMaterial() == net.minecraft.block.material.Material.WOOD;
        if (desintegrateApplies || autoSmeltApplies) {
            heldItem.damageItem(1, event.getEntityPlayer());
            event.setNewSpeed(Float.MAX_VALUE);
        } else if (desintegrate > 0 || autoSmelt > 0) {
            event.setCanceled(true);
        }
    }
}
