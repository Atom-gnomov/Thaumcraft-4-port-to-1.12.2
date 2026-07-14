package thaumcraft.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thaumcraft.common.lib.block.BlockTC;

/**
 * Invisible, unbreakable worldgen block (render type {@link EnumBlockRenderType#INVISIBLE}).
 * Backs TC4's Eldritch <i>Nothingness</i> and the Eldritch <i>Portal</i> — both are placed by
 * mechanics (never in creative, no item form) and are never meant to be seen.
 *
 * <p>This Phase-1 port faithfully reproduces the three player-visible traits of the originals —
 * <b>shape</b> (collision box), <b>light</b> and <b>step sound</b> — while leaving the active
 * mechanics (entity damage for Nothingness, teleport TileEntity for the Portal, and the
 * self-removal checks) as <b>TODO Phase 3</b>.
 */
public class BlockInvisibleTC extends BlockTC {
    /** Collision box; {@code null} means the block is fully intangible (walk-through). */
    private final AxisAlignedBB collision;

    public BlockInvisibleTC(Material material, SoundType sound, float light, float resistance, AxisAlignedBB collision) {
        super(material);
        this.setBlockUnbreakable();
        this.setResistance(resistance);
        this.setSoundType(sound);
        if (light > 0.0f) {
            this.setLightLevel(light);
        }
        this.collision = collision;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return collision;
    }
}
