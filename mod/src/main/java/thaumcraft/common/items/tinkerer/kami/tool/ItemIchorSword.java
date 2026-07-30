package thaumcraft.common.items.tinkerer.kami.tool;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import thaumcraft.common.items.tinkerer.kami.KamiMaterials;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Ichor sword — ported from Thaumic Tinkerer's {@code ItemIchorSword}
 * (pixlepix/nekosune, originally Vazkii).
 *
 * <p>Built on the ichor tool material, which the original defined with a use
 * count of {@code -1} — these tools never wear out.</p>
 */
public class ItemIchorSword extends ItemSword {

    public ItemIchorSword() {
        super(KamiMaterials.ICHOR);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    /**
     * No attack-speed penalty, so the blade swings at the player's own base
     * rate.
     *
     * <p><b>Owner's decision, 2026-07-30.</b> 1.7.10 had no attack-speed stat
     * and no swing cooldown at all — the original ichor sword hit as fast as
     * you could click, and its numbers were balanced against that. Carrying the
     * blade over to 1.12 unchanged silently handed it vanilla's {@code -2.4}
     * sword penalty, a restriction the original never had. Dropping the
     * modifier contributes nothing rather than inventing a figure, which is the
     * closest 1.12 gets to a stat that did not exist.</p>
     */
    @Override
    public com.google.common.collect.Multimap<String, net.minecraft.entity.ai.attributes.AttributeModifier>
            getAttributeModifiers(net.minecraft.inventory.EntityEquipmentSlot slot, ItemStack stack) {
        com.google.common.collect.Multimap<String,
                net.minecraft.entity.ai.attributes.AttributeModifier> base =
                super.getAttributeModifiers(slot, stack);
        if (slot != net.minecraft.inventory.EntityEquipmentSlot.MAINHAND) {
            return base;
        }

        String speed = net.minecraft.entity.SharedMonsterAttributes.ATTACK_SPEED.getName();
        com.google.common.collect.Multimap<String,
                net.minecraft.entity.ai.attributes.AttributeModifier> tuned =
                com.google.common.collect.HashMultimap.create();
        for (java.util.Map.Entry<String,
                net.minecraft.entity.ai.attributes.AttributeModifier> entry : base.entries()) {
            if (!speed.equals(entry.getKey())) {
                tuned.put(entry.getKey(), entry.getValue());
            }
        }
        return tuned;
    }
}
