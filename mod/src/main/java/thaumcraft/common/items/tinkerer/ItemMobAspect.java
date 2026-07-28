package thaumcraft.common.items.tinkerer;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.CreativeTabThaumcraft;

/**
 * Soul Aspect — ported from Thaumic Tinkerer's {@code ItemMobAspect}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Torn out of a creature by the Cursed Spirit's Blade. Three tiers live in
 * the item damage, {@link SoulAspects#TIER_STRIDE} apart: the plain aspect, the
 * condensed one nine of them press into, and the infused one an infusion of
 * nine condensed makes. Only the infused kind survives being used, and it
 * remembers which tablet used it last.</p>
 */
public class ItemMobAspect extends Item {

    private static final String TAG_LAST_X = "LastX";
    private static final String TAG_LAST_Y = "LastY";
    private static final String TAG_LAST_Z = "LastZ";

    public ItemMobAspect() {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(16);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Nullable
    public static Aspect getAspect(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return SoulAspects.byNumber(stack.getItemDamage() % SoulAspects.TIER_STRIDE);
    }

    public static boolean isCondensed(ItemStack stack) {
        int damage = stack.getItemDamage();
        return damage >= SoulAspects.TIER_STRIDE && damage < SoulAspects.TIER_STRIDE * 2;
    }

    public static boolean isInfused(ItemStack stack) {
        return stack.getItemDamage() >= SoulAspects.TIER_STRIDE * 2;
    }

    /** The plain soul of {@code aspect}, or empty if it is not a numbered one. */
    public static ItemStack stackFor(Aspect aspect) {
        int number = SoulAspects.numberOf(aspect);
        return number < 0 ? ItemStack.EMPTY : new ItemStack(ConfigItems.itemMobAspect, 1, number);
    }

    public static ItemStack infusedStackFor(Aspect aspect) {
        int number = SoulAspects.numberOf(aspect);
        return number < 0 ? ItemStack.EMPTY
                : new ItemStack(ConfigItems.itemMobAspect, 1, SoulAspects.TIER_STRIDE * 2 + number);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if (isCondensed(stack)) {
            return super.getTranslationKey() + ".condensed";
        }
        if (isInfused(stack)) {
            return super.getTranslationKey() + ".infused";
        }
        return super.getTranslationKey();
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return isInfused(stack);
    }

    /** Only the plain and infused tiers are shown; the condensed one is a step. */
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) {
            return;
        }
        for (Aspect aspect : SoulAspects.all()) {
            items.add(stackFor(aspect));
            items.add(infusedStackFor(aspect));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        Aspect aspect = getAspect(stack);
        if (aspect != null) {
            tooltip.add(aspect.getName());
        }
    }

    /**
     * An infused soul is not consumed, so a tablet must not be able to spin it
     * forever: it records where it was last used and refuses a second run at
     * the same place until it has been somewhere else.
     */
    public static void markLastUsedTablet(ItemStack stack, BlockPos tablet) {
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tag.setInteger(TAG_LAST_X, tablet.getX());
        tag.setInteger(TAG_LAST_Y, tablet.getY());
        tag.setInteger(TAG_LAST_Z, tablet.getZ());
        stack.setTagCompound(tag);
    }

    public static boolean lastUsedTabletMatches(ItemStack stack, BlockPos tablet) {
        if (!stack.hasTagCompound()) {
            return true;
        }
        NBTTagCompound tag = stack.getTagCompound();
        return tag.getInteger(TAG_LAST_X) == tablet.getX()
                && tag.getInteger(TAG_LAST_Y) == tablet.getY()
                && tag.getInteger(TAG_LAST_Z) == tablet.getZ();
    }
}
