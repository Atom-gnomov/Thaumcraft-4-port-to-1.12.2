package thaumcraft.common.items.tinkerer.kami.armor;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Base of the awakened ichorcloth armour — ported from Thaumic Tinkerer's
 * {@code ItemIchorclothArmorAdv} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Each awakened piece can be switched off on its own: sneak-right-click
 * flips item damage between 0 and 1, and a piece at 1 does nothing but protect.
 * Pieces that need a per-tick hook say so by overriding {@link #ticks()}, and
 * only those register themselves on the event bus — as upstream does.</p>
 */
public abstract class ItemIchorclothArmorAdv extends ItemIchorclothArmor {

    protected ItemIchorclothArmorAdv(EntityEquipmentSlot slot) {
        super(slot);
        this.setHasSubtypes(true);
        if (ticks()) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    /** Whether this piece needs {@link #tickPlayer} called while worn. */
    protected boolean ticks() {
        return false;
    }

    /** Runs every tick the piece is worn. Does nothing by default. */
    protected void tickPlayer(EntityPlayer player) {
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        ItemStack worn = player.getItemStackFromSlot(this.armorType);
        if (!worn.isEmpty() && worn.getItem() == this) {
            tickPlayer(player);
        }
    }

    /** True when this piece is worn and switched on. */
    protected boolean isActive(EntityPlayer player) {
        ItemStack worn = player.getItemStackFromSlot(this.armorType);
        return !worn.isEmpty() && worn.getItem() == this && worn.getItemDamage() == 0;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            stack.setItemDamage(~stack.getItemDamage() & 1);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.3F, 0.1F);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return super.onItemRightClick(world, player, hand);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        if (stack.getItemDamage() == 1) {
            tooltip.add(I18n.translateToLocal("ttmisc.awakenedArmor1"));
        }
    }

    /** The awakened set has its own two sheets. */
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return slot == EntityEquipmentSlot.LEGS
                ? "thaumcraft:textures/models/ichor_gem2.png"
                : "thaumcraft:textures/models/ichor_gem1.png";
    }
}
