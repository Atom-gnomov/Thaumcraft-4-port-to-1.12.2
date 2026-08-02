package thaumcraft.common.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.TileNode;
import thaumcraft.common.tiles.TileWardingStone;

public class BlockCosmeticSolid extends Block {

    /** Index into {@link #types}. The Obsidian Totem — the Osmotic Enchanter's pillars. */
    public static final int TYPE_OBSIDIAN_TOTEM = 0;
    public static final int TYPE_TRAVEL = 2;
    public static final int TYPE_WARDING = 3;
    /** Index into {@link #types}. The Charged Obsidian Totem — drawn exactly like the plain one. */
    public static final int TYPE_CHARGED_OBSIDIAN_TOTEM = 8;

    /**
     * The totem's side textures, in the original's own order — its
     * {@code icon[1]} through {@code icon[6]}. Kept as one list so the index
     * carried in the unlisted properties means the same thing on both sides of
     * the model.
     */
    public static final String[] TOTEM_TEXTURES = {
            "obsidiantotembase",
            "obsidiantotem1", "obsidiantotem2", "obsidiantotem3", "obsidiantotem4",
            "obsidiantotembaseshaded",
    };
    /** What the top, the bottom and the inventory icon use — the original's {@code icon[0]}. */
    public static final String TOTEM_END_TEXTURE = "obsidiantile";

    public static final int TOTEM_TEXTURE_COUNT = TOTEM_TEXTURES.length;
    public static final int TOTEM_BASE = 0;
    public static final int TOTEM_BODY_FIRST = 1;
    public static final int TOTEM_SHADED = 5;

    public static final IUnlistedProperty<Integer> TOTEM_NORTH = new IntUnlistedProperty("totem_north");
    public static final IUnlistedProperty<Integer> TOTEM_SOUTH = new IntUnlistedProperty("totem_south");
    public static final IUnlistedProperty<Integer> TOTEM_WEST = new IntUnlistedProperty("totem_west");
    public static final IUnlistedProperty<Integer> TOTEM_EAST = new IntUnlistedProperty("totem_east");
    /**
     * Internal names per metadata. Entries 0 and 1 used to be the wrong way
     * round — the array said {@code obsidianTile} at 0 and {@code obsidianTotem}
     * at 1, while everything that matters says the opposite: the original's
     * {@code tile.blockCosmeticSolid.0.name=Obsidian Totem}, this port's own
     * {@code cosmetic_solid.0.name}, the model files, and the original's
     * side-icon routine, which gives the totem column treatment to metas 0 and 8.
     * Nothing reads these strings, so nothing broke — they simply lied to
     * whoever read them next.
     */
    public static final String[] types = {"obsidianTotem", "obsidianTile", "pavingStone", "wardingStone", "thaumiumBlock", "tallowBlock", "pedestalTop", "arcaneStone", "chargedObsidianTotem", "golemStone", "golemStoneActive", "eldritchStone", "eldritchPattern", "eldritchStone2", "crust", "eldritchPedestal"};
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 15);

    public BlockCosmeticSolid() {
        super(Material.ROCK);
        this.setHardness(2.0f);
        this.setResistance(10.0f);
        this.setSoundType(SoundType.STONE);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0));
        this.setHarvestLevel("pickaxe", 0);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i < 16; i++) {
            if (types[i] != null) {
                list.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        if (this.getMetaFromState(world.getBlockState(pos)) == 8) {
            Thaumcraft.proxy.burst(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 1.0F);
            world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    TCSounds.CRAFTFAIL, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
        }
        return super.addDestroyEffects(world, pos, manager);
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        int meta = this.getMetaFromState(state);
        if (meta <= 1) return 30.0f;
        if (meta == 4 || meta == 6 || meta == 7) return 4.0f;
        return 2.0f;
    }

    @Override
    public int getLightValue(IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (meta == TYPE_TRAVEL) return 9;
        if (meta == 14) return 4;
        return 0;
    }

    public float getExplosionResistance(World world, BlockPos pos, net.minecraft.entity.Entity exploder) {
        int meta = this.getMetaFromState(world.getBlockState(pos));
        if (meta == 0 || meta == 1 || meta == 8) return 999.0f;
        return super.getExplosionResistance(exploder);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        int meta = this.getMetaFromState(state);
        return meta == TYPE_WARDING || meta == 8;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        int meta = this.getMetaFromState(state);
        if (meta == TYPE_WARDING) return new TileWardingStone();
        if (meta == 8) return new TileNode();
        return null;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        int meta = this.getMetaFromState(state);
        if (meta == TYPE_TRAVEL || meta == TYPE_WARDING || meta == 13) return false;
        return super.canCreatureSpawn(state, world, pos, type);
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == this && state.getValue(TYPE) == TYPE_TRAVEL && entity instanceof EntityLivingBase) {
            if (world.isRemote) {
                Thaumcraft.proxy.blockSparkle(world, pos.getX(), pos.getY(), pos.getZ(), 32768, 5);
            } else {
                EntityLivingBase living = (EntityLivingBase) entity;
                living.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 1, false, false));
                living.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 40, 0, false, false));
            }
        }
        super.onEntityWalk(world, pos, entity);
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (state.getValue(TYPE) != TYPE_WARDING) return;

        if (world.isBlockPowered(pos)) {
            for (int i = 0; i < Thaumcraft.proxy.particleCount(2); i++) {
                Thaumcraft.proxy.blockRunes(world, pos.getX(), pos.getY() + 0.7D, pos.getZ(),
                        0.2F + rand.nextFloat() * 0.4F, rand.nextFloat() * 0.3F,
                        0.8F + rand.nextFloat() * 0.2F, 20, -0.02F);
            }
            return;
        }

        if (!hasWardingAuraSpace(world, pos.up()) || !hasWardingAuraSpace(world, pos.up(2))) {
            for (int i = 0; i < Thaumcraft.proxy.particleCount(3); i++) {
                Thaumcraft.proxy.blockRunes(world, pos.getX(), pos.getY() + 0.7D, pos.getZ(),
                        0.9F + rand.nextFloat() * 0.1F, rand.nextFloat() * 0.3F,
                        rand.nextFloat() * 0.3F, 24, -0.02F);
            }
            return;
        }

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class,
                new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(1.0D, 1.0D, 1.0D));
        for (EntityLivingBase entity : entities) {
            if (entity instanceof EntityPlayer) continue;
            Thaumcraft.proxy.blockRunes(world, pos.getX(),
                    pos.getY() + 0.6D + rand.nextFloat() * Math.max(0.8F, entity.getEyeHeight()),
                    pos.getZ(), 0.6F + rand.nextFloat() * 0.4F, 0.0F,
                    0.3F + rand.nextFloat() * 0.7F, 20, 0.0F);
            break;
        }
    }

    private static boolean hasWardingAuraSpace(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == ConfigBlocks.blockAiry || state.getBlock().isReplaceable(world, pos);
    }

    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new net.minecraftforge.common.property.ExtendedBlockState(this,
                new net.minecraft.block.properties.IProperty[]{TYPE},
                new IUnlistedProperty[]{TOTEM_NORTH, TOTEM_SOUTH, TOTEM_WEST, TOTEM_EAST});
    }

    /**
     * Picks the side texture of an Obsidian Totem, reproducing the original's
     * {@code getIcon(IBlockAccess, x, y, z, side)} — see
     * {@code decompiled/thaumcraft/common/blocks/BlockCosmeticSolid.java},
     * {@code func_149673_e}.
     *
     * <p>Three cases, in the original's order: a totem underneath the next one
     * is drawn shaded; a totem standing on something that is not a totem is a
     * base; anything else picks one of four body textures from its own
     * coordinates and the face being drawn. Top and bottom faces are not
     * touched — they fall through to the plain tile, which is also what the
     * block looks like in the inventory.</p>
     *
     * <p>The arithmetic is copied rather than tidied. {@code %} keeps its sign
     * in Java, so {@code x % 4} is negative west of the origin and the
     * {@code Math.abs} sits <em>outside</em> the second remainder — moving it
     * inward, or swapping in a floor-modulus, would change which texture lands
     * on which block across a third of the world.</p>
     */
    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (!isTotem(state)) {
            return state;
        }
        IExtendedBlockState extended = (IExtendedBlockState) state;
        if (isTotem(world.getBlockState(pos.up()))) {
            return allSides(extended, TOTEM_SHADED);
        }
        if (!isTotem(world.getBlockState(pos.down()))) {
            return allSides(extended, TOTEM_BASE);
        }
        int coords = pos.getX() % 4 + pos.getZ() % 4 + pos.getY() % 4;
        return extended
                .withProperty(TOTEM_NORTH, bodyTexture(EnumFacing.NORTH, coords))
                .withProperty(TOTEM_SOUTH, bodyTexture(EnumFacing.SOUTH, coords))
                .withProperty(TOTEM_WEST, bodyTexture(EnumFacing.WEST, coords))
                .withProperty(TOTEM_EAST, bodyTexture(EnumFacing.EAST, coords));
    }

    private static int bodyTexture(EnumFacing side, int coords) {
        return TOTEM_BODY_FIRST + Math.abs((side.getIndex() + coords) % 4);
    }

    private static IExtendedBlockState allSides(IExtendedBlockState state, int texture) {
        return state
                .withProperty(TOTEM_NORTH, texture)
                .withProperty(TOTEM_SOUTH, texture)
                .withProperty(TOTEM_WEST, texture)
                .withProperty(TOTEM_EAST, texture);
    }

    /**
     * Whether this is a totem for drawing purposes. The original tests both
     * metadata values everywhere it tests one — a charged totem stacks with a
     * plain one and the column reads as continuous.
     */
    public boolean isTotem(IBlockState state) {
        if (state.getBlock() != this) {
            return false;
        }
        int meta = state.getValue(TYPE);
        return meta == TYPE_OBSIDIAN_TOTEM || meta == TYPE_CHARGED_OBSIDIAN_TOTEM;
    }

    /** Minimal {@code Integer} unlisted property; mirrors the one in {@code BlockCosmeticOpaque}. */
    private static final class IntUnlistedProperty implements IUnlistedProperty<Integer> {

        private final String name;

        IntUnlistedProperty(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean isValid(Integer value) {
            return value != null && value >= 0 && value < TOTEM_TEXTURE_COUNT;
        }

        @Override
        public Class<Integer> getType() {
            return Integer.class;
        }

        @Override
        public String valueToString(Integer value) {
            return String.valueOf(value);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 15));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(TYPE, MathHelper.clamp(meta, 0, 15));
    }
}
