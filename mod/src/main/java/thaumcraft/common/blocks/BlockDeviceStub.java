package thaumcraft.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.block.BlockTC;

/**
 * Phase 1 inert stub for TC4 "device" blocks (metal/wooden/stone device, table, jar, tube,
 * mirror, furnaces, hungry chest, lifter, magic box, essentia reservoir, airy, warded).
 *
 * <p>Ports only the passive block identity so each device is registered, placeable and looks
 * plausible: material, sound, hardness/resistance, light, the creative-tab metas and a
 * representative cube texture per meta. Every device MECHANIC — TileEntity logic, the custom
 * TESR shapes, GUIs, essentia/vis handling — is deliberately left as <b>TODO Phase 3</b>.
 */
public class BlockDeviceStub extends BlockTC {
    private final int[] creativeMetas;
    private final boolean transparent;
    private final boolean noCollision;

    public BlockDeviceStub(Material material, SoundType sound, float hardness, float resistance,
                           float light, int[] creativeMetas, boolean hasTab,
                           boolean transparent, boolean noCollision) {
        super(material);
        if (hardness < 0.0f) {
            this.setBlockUnbreakable();
        } else {
            this.setHardness(hardness);
        }
        if (resistance > 0.0f) {
            this.setResistance(resistance);
        }
        this.setSoundType(sound);
        if (light > 0.0f) {
            this.setLightLevel(light);
        }
        if (hasTab) {
            this.setCreativeTab(Thaumcraft.tabTC);
        }
        this.creativeMetas = creativeMetas;
        this.transparent = transparent;
        this.noCollision = noCollision;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int m : creativeMetas) {
            items.add(new ItemStack(this, 1, m));
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return !transparent;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return !transparent;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return transparent ? BlockRenderLayer.CUTOUT : BlockRenderLayer.SOLID;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return noCollision ? NULL_AABB : super.getCollisionBoundingBox(state, world, pos);
    }
}
