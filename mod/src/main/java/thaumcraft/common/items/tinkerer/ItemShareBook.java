package thaumcraft.common.items.tinkerer;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.research.ResearchManager;

/**
 * Tome of Knowledge Sharing — ported from Thaumic Tinkerer's
 * {@code ItemShareBook} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Right-click it once and it binds to you, copying your completed research
 * into itself. Hand it to someone else and their right-click grants them
 * everything it holds.</p>
 *
 * <p>The copy inside is what makes it work offline: if the player it is bound
 * to is not on the server, the tome falls back to the list it wrote down when
 * it was bound.</p>
 */
public class ItemShareBook extends Item {

    private static final String TAG_PLAYER = "player";
    private static final String TAG_RESEARCH = "research";
    private static final String UNASSIGNED = "[none]";

    public ItemShareBook() {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        String bound = getPlayerName(stack);

        if (UNASSIGNED.equals(bound)) {
            String name = player.getGameProfile().getName();
            setPlayerName(stack, name);
            writeResearch(stack, name);
            if (!world.isRemote) {
                player.sendMessage(new TextComponentTranslation("ttmisc.shareTome.write"));
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        List<String> research = liveResearchOf(bound);
        if (research == null) {
            // Bound player is away: fall back to what the tome recorded.
            if (world.isRemote) {
                research = storedResearch(stack);
            } else {
                player.sendMessage(new TextComponentTranslation("ttmisc.shareTome.notOnline"));
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }
        for (String key : research) {
            ResearchManager.addResearch(player, key);
        }
        if (!world.isRemote) {
            player.sendMessage(new TextComponentTranslation("ttmisc.shareTome.sync"));
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Nullable
    private static List<String> liveResearchOf(String name) {
        IPlayerKnowledge knowledge = ResearchManager.getResearchData(name);
        return knowledge == null ? null : new ArrayList<>(knowledge.getResearchComplete());
    }

    private static void writeResearch(ItemStack stack, String name) {
        List<String> research = liveResearchOf(name);
        if (research == null) {
            return;
        }
        NBTTagList list = new NBTTagList();
        for (String key : research) {
            list.appendTag(new NBTTagString(key));
        }
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tag.setTag(TAG_RESEARCH, list);
        stack.setTagCompound(tag);
    }

    private static List<String> storedResearch(ItemStack stack) {
        List<String> out = new ArrayList<>();
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(TAG_RESEARCH)) {
            return out;
        }
        NBTTagList list = stack.getTagCompound().getTagList(TAG_RESEARCH, 8);
        for (int i = 0; i < list.tagCount(); i++) {
            out.add(list.getStringTagAt(i));
        }
        return out;
    }

    private static String getPlayerName(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_PLAYER)
                ? stack.getTagCompound().getString(TAG_PLAYER) : UNASSIGNED;
    }

    private static void setPlayerName(ItemStack stack, String name) {
        NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tag.setString(TAG_PLAYER, name);
        stack.setTagCompound(tag);
    }

    /** The tome's contents have to reach the client for its tooltip. */
    @Override
    public boolean getShareTag() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        String name = getPlayerName(stack);
        tooltip.add(UNASSIGNED.equals(name)
                ? I18n.translateToLocal("ttmisc.shareTome.noAssign")
                : String.format(I18n.translateToLocal("ttmisc.shareTome.playerName"), name));
    }
}
