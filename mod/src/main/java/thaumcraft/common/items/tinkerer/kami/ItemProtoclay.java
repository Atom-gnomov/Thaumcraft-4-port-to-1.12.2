package thaumcraft.common.items.tinkerer.kami;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.common.items.tinkerer.kami.tool.IAdvancedTool;
import thaumcraft.common.items.tinkerer.kami.tool.KamiToolHandler;
import thaumcraft.common.lib.CreativeTabThaumcraft;

/**
 * Protoclay — ported from Thaumic Tinkerer's {@code ItemProtoclay}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Carried anywhere in the inventory it watches what the awakened tool in
 * hand is about to strike, and if another awakened tool in the inventory suits
 * that block better it swaps the two. Stone puts the pickaxe in your hand, dirt
 * the shovel, wood the axe. The sword is left alone, and so is a hand that
 * already holds the right tool.</p>
 */
public class ItemProtoclay extends Item {

    public ItemProtoclay() {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty() || !(held.getItem() instanceof IAdvancedTool)) {
            return;
        }
        IAdvancedTool tool = (IAdvancedTool) held.getItem();
        if ("sword".equals(tool.getType())) {
            return;
        }

        String typeToFind = "";
        RayTraceResult hit = rayTrace(world, player, false);
        if (player.isSwingInProgress && hit != null && hit.getBlockPos() != null) {
            IBlockState state = world.getBlockState(hit.getBlockPos());
            Material mat = state.getMaterial();
            if (KamiToolHandler.isRightMaterial(mat, KamiToolHandler.MATERIALS_PICK)) {
                typeToFind = "pick";
            } else if (KamiToolHandler.isRightMaterial(mat, KamiToolHandler.MATERIALS_SHOVEL)) {
                typeToFind = "shovel";
            } else if (KamiToolHandler.isRightMaterial(mat, KamiToolHandler.MATERIALS_AXE)) {
                typeToFind = "axe";
            }
        }

        if (typeToFind.isEmpty() || tool.getType().equals(typeToFind)) {
            return;
        }

        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack candidate = player.inventory.getStackInSlot(i);
            if (candidate.isEmpty() || candidate == held
                    || !(candidate.getItem() instanceof IAdvancedTool)) {
                continue;
            }
            if (((IAdvancedTool) candidate.getItem()).getType().equals(typeToFind)) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, candidate);
                player.inventory.setInventorySlotContents(i, held);
                break;
            }
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }
}
