package thaumcraft.common.items.tinkerer;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.CreativeTabThaumcraft;

/**
 * Experience Talisman — reimplemented from Thaumic Tinkerer (pixlepix/nekosune)
 * for 1.12.2. Right-click to deposit all of your experience into the talisman;
 * sneak-right-click to withdraw it back.
 */
public class ItemXpTalisman extends Item {

    private static final String KEY_XP = "StoredXP";

    public ItemXpTalisman() {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            int stored = getStored(stack);
            if (player.isSneaking()) {
                if (stored > 0) {
                    player.addExperience(stored);
                    setStored(stack, 0);
                    play(world, player, 0.8F);
                }
            } else if (player.experienceTotal > 0) {
                setStored(stack, stored + player.experienceTotal);
                player.addExperience(-player.experienceTotal);
                play(world, player, 1.2F);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private static void play(World world, EntityPlayer player, float pitch) {
        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5F, pitch);
    }

    private static int getStored(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound().getInteger(KEY_XP) : 0;
    }

    private static void setStored(ItemStack stack, int xp) {
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tag.setInteger(KEY_XP, xp);
        stack.setTagCompound(tag);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(new TextComponentTranslation("tc.xptalisman.stored", getStored(stack)).getFormattedText());
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getStored(stack) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        int stored = getStored(stack);
        return stored <= 0 ? 0.0D : 1.0D - Math.min(1.0D, stored / 1000.0D);
    }
}
