package thaumcraft.common.blocks.tinkerer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Dark Quartz — reimplemented from Thaumic Tinkerer (pixlepix/nekosune) for
 * 1.12.2. It behaves exactly like vanilla quartz, because the original was
 * written to: five metas, where {@code 0} is plain, {@code 1} chiseled, and
 * {@code 2}/{@code 3}/{@code 4} are the pillar standing on the Y, X and Z axis.
 *
 * <p>Only the first three appear in creative; the two rotated pillars are
 * reached by placing the pillar against a wall or ceiling, and both drop and
 * pick as the upright one.</p>
 */
public class BlockDarkQuartz extends Block {

    /** Upright pillar — the meta you get from the creative menu and from drops. */
    public static final int PILLAR_Y = 2;
    /** Pillar lying on the X axis; the original's {@code onBlockPlaced} picks it for the west/east faces. */
    public static final int PILLAR_X = 3;
    /** Pillar lying on the Z axis; picked for the north/south faces. */
    public static final int PILLAR_Z = 4;

    public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 4);

    /** The variants offered in creative — the rotated pillars are placement-only, as upstream. */
    public static final String[] VARIANTS = {"plain", "chiseled", "pillar"};

    public BlockDarkQuartz() {
        super(Material.ROCK);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, 0));
        this.setHardness(0.8F);
        this.setResistance(10.0F);
        this.setSoundType(net.minecraft.block.SoundType.STONE);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i < VARIANTS.length; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    /**
     * The original's {@code onBlockPlaced}: placing the pillar turns it to face
     * the way you placed it — down/up keeps it upright, west/east lays it on X,
     * north/south on Z. Every other variant is placed unchanged.
     */
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ, int meta,
                                            EntityLivingBase placer) {
        if (meta != PILLAR_Y) {
            return this.getStateFromMeta(meta);
        }
        switch (facing) {
            case WEST:
            case EAST:
                return this.getDefaultState().withProperty(VARIANT, PILLAR_X);
            case NORTH:
            case SOUTH:
                return this.getDefaultState().withProperty(VARIANT, PILLAR_Z);
            default:
                return this.getDefaultState().withProperty(VARIANT, PILLAR_Y);
        }
    }

    /** A pillar on any axis drops the upright one — the original's {@code damageDropped}. */
    @Override
    public int damageDropped(IBlockState state) {
        return uprightIfPillar(this.getMetaFromState(state));
    }

    /** Pick-block on a lying pillar gives the upright one — the original's {@code createStackedBlock}. */
    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(this, 1, uprightIfPillar(this.getMetaFromState(state)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, MathHelper.clamp(meta, 0, 4));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT);
    }

    private static int uprightIfPillar(int meta) {
        return meta != PILLAR_X && meta != PILLAR_Z ? meta : PILLAR_Y;
    }
}
