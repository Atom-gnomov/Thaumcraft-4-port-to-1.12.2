package thaumcraft.common.items.tinkerer;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.tinkerer.TileTransvector;
import thaumcraft.common.tiles.tinkerer.TileTransvectorInterface;

/**
 * Transvector Connector — reimplemented from Thaumic Tinkerer
 * (pixlepix/nekosune, originally Vazkii) for 1.12.2.
 *
 * <p>Two-step linking, as in the original: right-click a transvector device to
 * pick it up, then right-click the block it should stand in for. Sneak-click
 * with a stored device to forget it, and sneak-click the device itself to clear
 * its link.</p>
 */
public class ItemTransvectorConnector extends Item {

    private static final String TAG_POS = "linkPos";

    public ItemTransvectorConnector() {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        ItemStack stack = player.getHeldItem(hand);
        TileEntity clicked = world.getTileEntity(pos);

        // Sneak-clicking a device clears whatever it points at.
        if (player.isSneaking() && clicked instanceof TileTransvector) {
            ((TileTransvector) clicked).unlink();
            say(player, "tc.transvector.cleared");
            playClick(world, pos);
            return EnumActionResult.SUCCESS;
        }

        BlockPos stored = getStoredPos(stack);
        if (stored == null) {
            if (!(clicked instanceof TileTransvector)) {
                say(player, "tc.transvector.notdevice");
                return EnumActionResult.SUCCESS;
            }
            setStoredPos(stack, pos);
            say(player, "tc.transvector.picked");
            playClick(world, pos);
            return EnumActionResult.SUCCESS;
        }

        // Second click: resolve the device we picked up and point it here.
        TileEntity deviceTile = world.getTileEntity(stored);
        if (!(deviceTile instanceof TileTransvector)) {
            clearStoredPos(stack);
            say(player, "tc.transvector.gone");
            return EnumActionResult.SUCCESS;
        }
        TileTransvector device = (TileTransvector) deviceTile;

        if (stored.equals(pos)) {
            clearStoredPos(stack);
            say(player, "tc.transvector.cleared");
            return EnumActionResult.SUCCESS;
        }
        // Chaining two interfaces would make the lookup recurse.
        if (device instanceof TileTransvectorInterface && clicked instanceof TileTransvectorInterface) {
            say(player, "tc.transvector.nochain");
            return EnumActionResult.SUCCESS;
        }
        if (!device.link(pos)) {
            say(player, "tc.transvector.toofar", device.getMaxDistance());
            return EnumActionResult.SUCCESS;
        }
        clearStoredPos(stack);
        say(player, "tc.transvector.linked");
        playClick(world, pos);
        return EnumActionResult.SUCCESS;
    }

    private static void playClick(World world, BlockPos pos) {
        world.playSound(null, pos, TCSounds.CAMERACLACK, SoundCategory.BLOCKS, 0.4F, 1.0F);
    }

    private static void say(EntityPlayer player, String key, Object... args) {
        player.sendStatusMessage(new TextComponentTranslation(key, args), true);
    }

    @Nullable
    private static BlockPos getStoredPos(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey(TAG_POS) ? BlockPos.fromLong(tag.getLong(TAG_POS)) : null;
    }

    private static void setStoredPos(ItemStack stack, BlockPos pos) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setLong(TAG_POS, pos.toLong());
    }

    private static void clearStoredPos(ItemStack stack) {
        if (stack.hasTagCompound()) {
            stack.getTagCompound().removeTag(TAG_POS);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        BlockPos stored = getStoredPos(stack);
        if (stored != null) {
            tooltip.add(I18n.translateToLocal("tc.transvector.holding")
                    .replace("%s", stored.getX() + ", " + stored.getY() + ", " + stored.getZ()));
        }
    }
}
