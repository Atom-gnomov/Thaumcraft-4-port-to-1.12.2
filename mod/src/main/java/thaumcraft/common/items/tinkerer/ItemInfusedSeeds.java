package thaumcraft.common.items.tinkerer;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Infused Seeds — ported from Thaumic Tinkerer's {@code ItemInfusedSeeds}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Four kinds, one per primal, addressed by item damage. Planting is written
 * out rather than inherited because each damage grows a different block; the
 * checks are upstream's, which are vanilla's for wheat.</p>
 */
public class ItemInfusedSeeds extends Item implements IPlantable {

    public ItemInfusedSeeds() {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    public PrimalCrop getCrop(ItemStack stack) {
        return PrimalCrop.byMeta(stack.getItemDamage());
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (facing != EnumFacing.UP) {
            return EnumActionResult.FAIL;
        }
        ItemStack stack = player.getHeldItem(hand);
        BlockPos above = pos.up();
        if (!player.canPlayerEdit(pos, facing, stack) || !player.canPlayerEdit(above, facing, stack)) {
            return EnumActionResult.FAIL;
        }
        if (!world.getBlockState(pos).getBlock().canSustainPlant(
                world.getBlockState(pos), world, pos, EnumFacing.UP, this)
                || !world.isAirBlock(above)) {
            return EnumActionResult.FAIL;
        }
        world.setBlockState(above, cropBlock(stack).getDefaultState());
        stack.shrink(1);
        return EnumActionResult.SUCCESS;
    }

    private net.minecraft.block.Block cropBlock(ItemStack stack) {
        return ConfigBlocks.blockInfusedGrain[getCrop(stack).ordinal()];
    }

    @Override
    public EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Crop;
    }

    @Override
    public net.minecraft.block.state.IBlockState getPlant(net.minecraft.world.IBlockAccess world, BlockPos pos) {
        return ConfigBlocks.blockInfusedGrain[0].getDefaultState();
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) {
            return;
        }
        for (PrimalCrop crop : PrimalCrop.values()) {
            items.add(new ItemStack(this, 1, crop.ordinal()));
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(getCrop(stack).getAspect().getName());
    }
}
