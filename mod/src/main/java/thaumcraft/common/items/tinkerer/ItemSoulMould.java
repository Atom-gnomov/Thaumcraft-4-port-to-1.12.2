package thaumcraft.common.items.tinkerer;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.CreativeTabThaumcraft;

/**
 * Soul Mould — reimplemented from Thaumic Tinkerer (pixlepix/nekosune,
 * originally Vazkii) for 1.12.2.
 *
 * <p>Right-click a creature to record what it is; the mould then names that
 * kind and is used to filter machines that care about a specific mob, such as
 * the Mob Magnet. Sneak-right-click in the air clears it.</p>
 */
public class ItemSoulMould extends Item {

    private static final String TAG_PATTERN = "pattern";

    public ItemSoulMould() {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player,
                                            EntityLivingBase target, EnumHand hand) {
        if (player.world.isRemote || target instanceof EntityPlayer) {
            return false;
        }
        ResourceLocation key = EntityList.getKey(target);
        if (key == null) {
            return false;
        }
        setPattern(stack, key.toString());
        return true;
    }

    @Override
    public net.minecraft.util.ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote && player.isSneaking()) {
            clearPattern(stack);
        }
        return new net.minecraft.util.ActionResult<>(net.minecraft.util.EnumActionResult.SUCCESS, stack);
    }

    /** Entity registry id this mould is keyed to, or {@code null} when blank. */
    @Nullable
    public static String getPattern(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey(TAG_PATTERN) ? tag.getString(TAG_PATTERN) : null;
    }

    public static void setPattern(ItemStack stack, String pattern) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setString(TAG_PATTERN, pattern);
    }

    public static void clearPattern(ItemStack stack) {
        if (stack.hasTagCompound()) {
            stack.getTagCompound().removeTag(TAG_PATTERN);
        }
    }

    /** Whether {@code entity} is the kind this mould was pressed against. */
    public static boolean matches(ItemStack mould, EntityLivingBase entity) {
        String pattern = getPattern(mould);
        if (pattern == null) {
            return true;
        }
        ResourceLocation key = EntityList.getKey(entity);
        return key != null && key.toString().equals(pattern);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        String pattern = getPattern(stack);
        if (pattern == null) {
            tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("tc.soulmould.blank"));
            return;
        }
        String name = EntityList.getTranslationName(new ResourceLocation(pattern));
        tooltip.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("tc.soulmould.bound")
                .replace("%s", name == null ? pattern : I18n.translateToLocal("entity." + name + ".name")));
    }
}
