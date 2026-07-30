package thaumcraft.common.items.wands.foci;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Focus of Smelting — ported 1:1 from Thaumic Tinkerer's ItemFocusSmelt
 * (pixlepix / nekosune / Vazkii). Held to channel: while the beam stays on one
 * block its progress counts down, and on reaching zero the block is replaced by
 * its smelted form. Only smelts block into block, as the original did.
 */
public class FocusSmelt extends ItemFocusBasic {

    /** Upstream's {@code hasOrnament()} returns true for this focus; ModelWand draws the ornament on the wand. */
    private static final String ORNAMENT_SPRITE = "thaumcraft:items/focus_smelt_orn";

    /** The original's visUsage: charged every tick while channelling. */
    private static final AspectList COST = new AspectList().add(Aspect.FIRE, 45).add(Aspect.ENTROPY, 12);

    /** Per-player target and countdown, as the original's playerData map. */
    private static final Map<UUID, SmeltData> PLAYER_DATA = new HashMap<>();

    private static final class SmeltData {
        private final BlockPos pos;
        private int progress;

        private SmeltData(BlockPos pos, int progress) {
            this.pos = pos;
            this.progress = progress;
        }

        private boolean equalPos(BlockPos other) {
            return other != null && this.pos.equals(other);
        }
    }

    public FocusSmelt() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0xFF6600;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getOrnament(ItemStack stack) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(ORNAMENT_SPRITE);
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        return COST;
    }

    @Override
    public boolean isVisCostPerTick(ItemStack focusstack) {
        return true;
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "SM" + super.getSortingHelper(stack);
    }

    @Override
    public ItemFocusBasic.WandFocusAnimation getAnimation(ItemStack focusstack) {
        return ItemFocusBasic.WandFocusAnimation.CHARGE;
    }

    /** The original is a use-item focus: right-click starts the channel. */
    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult mop) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return wandStack;
        player.setActiveHand(ItemWandCasting.getHandHoldingWand(player, wandStack));
        WandManager.setCooldown(player, -1);
        return wandStack;
    }

    @Override
    public void onUsingFocusTick(ItemStack wandStack, EntityPlayer player, int count) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        World world = player.world;

        // Simulated drain first: no vis, no work — as the original checked.
        if (!wand.consumeAllVis(wandStack, player, COST, false, false)) {
            return;
        }

        RayTraceResult mop = this.rayTrace(world, player, false);
        if (mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }
        BlockPos pos = mop.getBlockPos();
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        ItemStack blockStack = new ItemStack(block, 1, block.getMetaFromState(state));
        ItemStack result = FurnaceRecipes.instance().getSmeltingResult(blockStack);

        // The original only ever smelted a block into another block.
        if (result.isEmpty() || !(result.getItem() instanceof ItemBlock)) {
            return;
        }

        boolean decremented = false;
        SmeltData data = PLAYER_DATA.get(player.getUniqueID());
        if (data != null && data.equalPos(pos)) {
            data.progress--;
            decremented = true;
            if (data.progress <= 0) {
                if (!world.isRemote) {
                    Block smelted = ((ItemBlock) result.getItem()).getBlock();
                    world.setBlockState(pos, smelted.getStateFromMeta(result.getMetadata()), 3);
                    world.playSound(null, player.posX, player.posY, player.posZ,
                            SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.6F, 1.0F);
                    world.playSound(null, player.posX, player.posY, player.posZ,
                            SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    wand.consumeAllVis(wandStack, player, COST, true, false);
                    PLAYER_DATA.remove(player.getUniqueID());
                    decremented = false;
                }
                for (int i = 0; i < 25; i++) {
                    Thaumcraft.proxy.wispFX2(world, pos.getX() + Math.random(), pos.getY() + Math.random(),
                            pos.getZ() + Math.random(), (float) Math.random() / 2.0F, 4, true, false,
                            (float) -Math.random() / 10.0F);
                }
            }
        }

        if (!decremented) {
            int potency = this.getUpgradeLevel(wand.getFocusItem(wandStack), FocusUpgradeType.potency);
            PLAYER_DATA.put(player.getUniqueID(), new SmeltData(pos, 20 - Math.min(3, potency) * 5));
        } else {
            for (int i = 0; i < 2; i++) {
                world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS,
                        (float) Math.random() / 2.0F + 0.5F, 1.0F);
                Thaumcraft.proxy.wispFX2(world, pos.getX() + Math.random(), pos.getY() + Math.random(),
                        pos.getZ() + Math.random(), (float) Math.random() / 2.0F, 4, true, false,
                        (float) -Math.random() / 10.0F);
            }
        }

        if (world.isRemote) {
            Thaumcraft.proxy.beamCont(world, player, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    2, 0xFF0000, true, 0.0F, null, 1);
        }
    }

    @Override
    public void onPlayerStoppedUsingFocus(ItemStack wandstack, World world, EntityPlayer player, int count) {
        PLAYER_DATA.remove(player.getUniqueID());
    }
}
