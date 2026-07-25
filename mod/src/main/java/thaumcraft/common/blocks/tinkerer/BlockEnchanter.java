package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.tinkerer.TileEnchanter;

/**
 * Osmotic Enchanter — reimplemented from Thaumic Tinkerer (pixlepix/nekosune,
 * originally Vazkii) for 1.12.2.
 *
 * <p>Right-click with a tool to place it, with a wand to supply vis, and with
 * an enchanted book to start enchanting. An empty hand retrieves the tool, and
 * sneak-clicking cancels a run in progress.</p>
 *
 * @see TileEnchanter
 */
public class BlockEnchanter extends BlockContainer {

    public BlockEnchanter() {
        super(Material.ROCK);
        this.setHardness(3.0F);
        this.setResistance(12.0F);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEnchanter();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEnchanter)) {
            return false;
        }
        TileEnchanter enchanter = (TileEnchanter) te;
        ItemStackHandler inv = enchanter.getInventory();
        ItemStack held = player.getHeldItem(hand);

        if (player.isSneaking()) {
            enchanter.cancel();
            player.sendStatusMessage(new TextComponentTranslation("tc.enchanter.cancelled"), true);
            return true;
        }

        // An enchanted book starts a run.
        if (held.getItem() == Items.ENCHANTED_BOOK) {
            if (enchanter.isWorking()) {
                player.sendStatusMessage(new TextComponentTranslation("tc.enchanter.busy"), true);
                return true;
            }
            if (enchanter.countPillars() < 6) {
                player.sendStatusMessage(new TextComponentTranslation("tc.enchanter.nopillars"), true);
                return true;
            }
            if (!enchanter.beginFromBook(held)) {
                player.sendStatusMessage(new TextComponentTranslation("tc.enchanter.cannot"), true);
                return true;
            }
            held.shrink(1);
            player.sendStatusMessage(new TextComponentTranslation("tc.enchanter.started"), true);
            return true;
        }

        int slot = held.getItem() instanceof ItemWandCasting ? TileEnchanter.SLOT_WAND : TileEnchanter.SLOT_TOOL;
        if (held.isEmpty()) {
            // Empty hand: take the tool back, else the wand.
            slot = inv.getStackInSlot(TileEnchanter.SLOT_TOOL).isEmpty()
                    ? TileEnchanter.SLOT_WAND : TileEnchanter.SLOT_TOOL;
        }
        ItemStack stored = inv.getStackInSlot(slot);
        if (!stored.isEmpty()) {
            if (!player.inventory.addItemStackToInventory(stored)) {
                player.dropItem(stored, false);
            }
            inv.setStackInSlot(slot, ItemStack.EMPTY);
        }
        if (!held.isEmpty()) {
            ItemStack single = held.copy();
            single.setCount(1);
            inv.setStackInSlot(slot, single);
            held.shrink(1);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEnchanter) {
            ItemStackHandler inv = ((TileEnchanter) te).getInventory();
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                ItemStack stored = inv.getStackInSlot(slot);
                if (!stored.isEmpty()) {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stored);
                }
            }
        }
        super.breakBlock(world, pos, state);
    }
}
