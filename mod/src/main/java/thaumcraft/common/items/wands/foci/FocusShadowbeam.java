package thaumcraft.common.items.wands.foci;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.projectile.EntityShadowbeam;
import thaumcraft.common.items.wands.ItemWandCasting;

/**
 * Wand Focus: Shadowbeam — the port of Thaumic Tinkerer's
 * {@code ItemFocusShadowbeam}.
 *
 * <p>Channelled: every tick it pays its vis and looses one beam, which runs
 * its entire flight before the tick ends, bouncing off surfaces and hurting
 * what it strikes. Potency adds to the damage.</p>
 */
public class FocusShadowbeam extends ItemFocusBasic {

    private static final AspectList COST = new AspectList()
            .add(Aspect.ORDER, 25).add(Aspect.ENTROPY, 25).add(Aspect.AIR, 15);

    public FocusShadowbeam() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public boolean isVisCostPerTick(ItemStack focusstack) {
        return true;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return COST;
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0x4B0053;
    }

    @Override
    public void onUsingFocusTick(ItemStack wandStack, EntityPlayer player, int count) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) {
            return;
        }
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        if (!player.world.isRemote
                && wand.consumeAllVis(wandStack, player, getVisCost(wandStack), true, false)) {
            int potency = Config.enchPotency == null ? 0
                    : EnchantmentHelper.getEnchantmentLevel(Config.enchPotency,
                            wand.getFocusItem(wandStack));
            if (player.world.rand.nextInt(10) == 0) {
                player.world.playSound(null, player.posX, player.posY, player.posZ,
                        net.minecraft.util.SoundEvent.REGISTRY.getObject(
                                new net.minecraft.util.ResourceLocation("thaumcraft", "brain")),
                        SoundCategory.PLAYERS, 0.5F, 1.0F);
            }
            EntityShadowbeam beam = new EntityShadowbeam(player.world, player, potency);
            beam.updateUntilDead();
        }
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "SHADOWBEAM" + super.getSortingHelper(stack);
    }

    // Carried over from the original's ItemModKamiFocus, which this port has
    // no counterpart for — see FocusXpDrain for the same note.

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public int getItemEnchantability() {
        return 5;
    }

    @Override
    public ItemFocusBasic.WandFocusAnimation getAnimation(ItemStack focusstack) {
        return ItemFocusBasic.WandFocusAnimation.WAVE;
    }
}
