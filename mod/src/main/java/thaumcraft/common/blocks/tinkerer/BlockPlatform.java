package thaumcraft.common.blocks.tinkerer;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;

/**
 * Ethereal Platform — ported from Thaumic Tinkerer's {@code BlockPlatform}
 * (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Solid from above and open from below, so you can walk out over a drop and
 * still jump up through it. Sneaking drops you through. Like the other
 * camouflaged devices it can be disguised as any ordinary block — see
 * {@link BlockCamo}.</p>
 */
public class BlockPlatform extends BlockCamo {

    public BlockPlatform() {
        super(Material.WOOD);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(Thaumcraft.tabTC);
    }

    /**
     * The original's rule, verbatim: collide only when the entity is above the
     * block — more than two blocks above for a player — and never for a player
     * who is sneaking.
     */
    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> boxes, @Nullable Entity entity, boolean isActualState) {
        boolean player = entity instanceof EntityPlayer;
        if (entity != null
                && entity.posY > pos.getY() + (player ? 2 : 0)
                && (!player || !entity.isSneaking())) {
            super.addCollisionBoxToList(state, world, pos, entityBox, boxes, entity, isActualState);
        }
    }

    /** Never counts as an obstacle, so pathfinding walks straight through. */
    @Override
    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        return true;
    }
}
