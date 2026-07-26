package thaumcraft.common.items.wands.foci;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.TCSounds;

/**
 * Focus of Dislocation — ported 1:1 from Thaumic Tinkerer's
 * ItemFocusDislocation (pixlepix / nekosune / Vazkii). The first cast lifts the
 * targeted block out of the world, tile entity and all, and stores it on the
 * wand; the next places it against the face you click. Blocks with a tile cost
 * five times as much, and mob spawners twenty times.
 */
public class FocusDislocation extends ItemFocusBasic {

    /** The original's three cost tiers. */
    private static final AspectList COST_PLAIN =
            new AspectList().add(Aspect.ENTROPY, 500).add(Aspect.ORDER, 500).add(Aspect.EARTH, 100);
    private static final AspectList COST_TILE =
            new AspectList().add(Aspect.ENTROPY, 2500).add(Aspect.ORDER, 2500).add(Aspect.EARTH, 500);
    private static final AspectList COST_SPAWNER =
            new AspectList().add(Aspect.ENTROPY, 10000).add(Aspect.ORDER, 10000).add(Aspect.EARTH, 5000);

    private static final String TAG_BLOCK = "DislocationBlock";
    private static final String TAG_META = "DislocationMeta";
    private static final String TAG_TILE = "DislocationTile";

    public FocusDislocation() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0x9933CC;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return COST_PLAIN;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "DL" + super.getSortingHelper(stack);
    }

    private static AspectList getCost(TileEntity tile) {
        if (tile == null) return COST_PLAIN;
        return tile instanceof TileEntityMobSpawner ? COST_SPAWNER : COST_TILE;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult mop) {
        if (!(wandStack.getItem() instanceof ItemWandCasting) || mop == null
                || mop.typeOfHit != RayTraceResult.Type.BLOCK) {
            return wandStack;
        }
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        BlockPos pos = mop.getBlockPos();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        TileEntity tile = world.getTileEntity(pos);

        if (!player.canPlayerEdit(pos, mop.sideHit, wandStack)) {
            return wandStack;
        }

        ItemStack carried = getPickedBlock(wandStack);
        if (!carried.isEmpty()) {
            // Second cast: place what is being carried against the clicked face.
            BlockPos target = pos.offset(mop.sideHit);
            Block carriedBlock = Block.getBlockFromName(wandStack.getTagCompound().getString(TAG_BLOCK));
            if (carriedBlock == null) {
                clearPickedBlock(wandStack);
                return wandStack;
            }
            int meta = wandStack.getTagCompound().getInteger(TAG_META);
            if (world.mayPlace(carriedBlock, target, false, mop.sideHit, player)) {
                if (!world.isRemote) {
                    world.setBlockState(target, carriedBlock.getStateFromMeta(meta), 3);
                    carriedBlock.onBlockPlacedBy(world, target, carriedBlock.getStateFromMeta(meta), player, wandStack);
                    NBTTagCompound tileTag = getStackTileEntity(wandStack);
                    if (tileTag != null && !tileTag.isEmpty()) {
                        tileTag.setInteger("x", target.getX());
                        tileTag.setInteger("y", target.getY());
                        tileTag.setInteger("z", target.getZ());
                        TileEntity restored = TileEntity.create(world, tileTag);
                        if (restored != null) {
                            world.setTileEntity(target, restored);
                        }
                    }
                    clearPickedBlock(wandStack);
                } else {
                    player.swingArm(ItemWandCasting.getHandHoldingWand(player, wandStack));
                }
                for (int i = 0; i < 8; i++) {
                    Thaumcraft.proxy.burst(world, target.getX() + Math.random(),
                            target.getY() + Math.random() + 0.65D, target.getZ() + Math.random(), 0.2F);
                }
                world.playSound(null, player.posX, player.posY, player.posZ,
                        TCSounds.WAND, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }
            return wandStack;
        }

        // First cast: lift the block, tile entity included.
        if (block != Blocks.AIR
                && !ThaumcraftApi.portableHoleBlackList.contains(block)
                && state.getBlockHardness(world, pos) != -1.0F
                && wand.consumeAllVis(wandStack, player, getCost(tile), true, false)) {
            if (!world.isRemote) {
                storePickedBlock(wandStack, block, block.getMetaFromState(state), tile);
                world.removeTileEntity(pos);
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            }
            for (int i = 0; i < 8; i++) {
                Thaumcraft.proxy.burst(world, pos.getX() + Math.random(),
                        pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.2F);
            }
            world.playSound(null, player.posX, player.posY, player.posZ,
                    block.getSoundType(state, world, pos, player).getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    TCSounds.WAND, SoundCategory.PLAYERS, 0.5F, 1.0F);
            if (world.isRemote) {
                player.swingArm(ItemWandCasting.getHandHoldingWand(player, wandStack));
            }
        }
        return wandStack;
    }

    private static ItemStack getPickedBlock(ItemStack wandStack) {
        if (!wandStack.hasTagCompound() || !wandStack.getTagCompound().hasKey(TAG_BLOCK)) {
            return ItemStack.EMPTY;
        }
        Block block = Block.getBlockFromName(wandStack.getTagCompound().getString(TAG_BLOCK));
        if (block == null) return ItemStack.EMPTY;
        return new ItemStack(block, 1, wandStack.getTagCompound().getInteger(TAG_META));
    }

    private static void storePickedBlock(ItemStack wandStack, Block block, int meta, TileEntity tile) {
        ResourceLocation name = Block.REGISTRY.getNameForObject(block);
        if (name == null) return;
        NBTTagCompound tag = wandStack.hasTagCompound() ? wandStack.getTagCompound() : new NBTTagCompound();
        tag.setString(TAG_BLOCK, name.toString());
        tag.setInteger(TAG_META, meta);
        if (tile != null) {
            NBTTagCompound tileTag = new NBTTagCompound();
            tile.writeToNBT(tileTag);
            tag.setTag(TAG_TILE, tileTag);
        } else {
            tag.removeTag(TAG_TILE);
        }
        wandStack.setTagCompound(tag);
    }

    private static NBTTagCompound getStackTileEntity(ItemStack wandStack) {
        if (!wandStack.hasTagCompound() || !wandStack.getTagCompound().hasKey(TAG_TILE)) return null;
        return wandStack.getTagCompound().getCompoundTag(TAG_TILE);
    }

    private static void clearPickedBlock(ItemStack wandStack) {
        if (!wandStack.hasTagCompound()) return;
        NBTTagCompound tag = wandStack.getTagCompound();
        tag.removeTag(TAG_BLOCK);
        tag.removeTag(TAG_META);
        tag.removeTag(TAG_TILE);
    }
}
