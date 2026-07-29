package thaumcraft.common.items.tinkerer.kami;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.CreativeTabThaumcraft;

/**
 * The sky pearl — the port of Thaumic Tinkerer's {@code ItemSkyPearl}.
 *
 * <p>A pearl remembers one warp gate. Right-clicking a gate with an unattuned
 * pearl writes that gate's position and dimension into it; sneak-right-click
 * anywhere clears it again. There is no other way to set one, which is why the
 * pearl is useless without the gate.</p>
 *
 * <p>"Unattuned" is stored as Y of -1 rather than as a flag, exactly as
 * upstream does it — clearing a pearl only rewrites Y.</p>
 */
public class ItemSkyPearl extends Item {

    public static final String TAG_X = "x";
    public static final String TAG_Y = "y";
    public static final String TAG_Z = "z";
    public static final String TAG_DIM = "dim";

    public ItemSkyPearl() {
        super();
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.getBlockState(pos).getBlock() == ConfigBlocks.blockWarpGate && !isAttuned(stack)) {
            setValues(stack, pos.getX(), pos.getY(), pos.getZ(), player.dimension);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.3F, 0.1F);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking() && isAttuned(stack)) {
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.3F, 0.1F);
            setInt(stack, TAG_Y, -1);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
        EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().player;
        if (player == null) {
            return;
        }
        addInfo(stack, player.dimension, player.posX, player.posY, player.posZ, list, false);
    }

    /** Shared with the gate's destination list, which shows the same lines. */
    public static void addInfo(ItemStack stack, int dim, double px, double py, double pz,
                               List<String> list, boolean simpleMode) {
        if (!isAttuned(stack)) {
            return;
        }
        int x = getX(stack);
        int y = getY(stack);
        int z = getZ(stack);
        list.add("X: " + x);
        if (!simpleMode) {
            list.add("Y: " + y);
        }
        list.add("Z: " + z);
        if (getDim(stack) != dim) {
            if (!simpleMode) {
                list.add(TextFormatting.RED + I18n.translateToLocal("ttmisc.differentDim"));
            }
        } else {
            double dx = x - px;
            double dy = simpleMode ? 0.0D : y - py;
            double dz = z - pz;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            list.add(TextFormatting.BLUE + I18n.translateToLocal("ttmisc.distance")
                    + ": " + new java.math.BigDecimal(distance)
                    .setScale(2, java.math.RoundingMode.UP).toString() + "m");
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return isAttuned(stack);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    public static void setValues(ItemStack stack, int x, int y, int z, int dim) {
        setInt(stack, TAG_X, x);
        setInt(stack, TAG_Y, y);
        setInt(stack, TAG_Z, z);
        setInt(stack, TAG_DIM, dim);
    }

    /** A pearl with no Y is a pearl with no destination. */
    public static boolean isAttuned(ItemStack stack) {
        return stack.hasTagCompound() && getInt(stack, TAG_Y, -1) != -1;
    }

    public static int getX(ItemStack stack) {
        return isAttuned(stack) ? getInt(stack, TAG_X, 0) : 0;
    }

    public static int getY(ItemStack stack) {
        return isAttuned(stack) ? getInt(stack, TAG_Y, 0) : 0;
    }

    public static int getZ(ItemStack stack) {
        return isAttuned(stack) ? getInt(stack, TAG_Z, 0) : 0;
    }

    public static int getDim(ItemStack stack) {
        return isAttuned(stack) ? getInt(stack, TAG_DIM, 0) : 0;
    }

    private static void setInt(ItemStack stack, String tag, int value) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger(tag, value);
    }

    private static int getInt(ItemStack stack, String tag, int fallback) {
        NBTTagCompound cmp = stack.getTagCompound();
        return cmp != null && cmp.hasKey(tag) ? cmp.getInteger(tag) : fallback;
    }
}
