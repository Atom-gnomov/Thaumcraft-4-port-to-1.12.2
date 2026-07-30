package thaumcraft.common.blocks.tinkerer.kami;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.world.dim.bedrock.TeleporterBedrock;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Bedrock portal — ported from Thaumic Tinkerer's {@code BlockBedrockPortal}
 * (pixlepix/nekosune, originally Vazkii).
 *
 * <p>Appears where an advanced ichor tool breaks bedrock at the bottom of the
 * world. Walking into it moves the player to the Bedrock dimension and clears
 * the three bedrock layers under the ceiling there, so they arrive in a pocket
 * rather than inside solid rock — the original did exactly that at y 251-253.</p>
 */
public class BlockBedrockPortal extends Block {

    private static final AxisAlignedBB NO_COLLISION = null;

    public BlockBedrockPortal() {
        super(Material.PORTAL);
        this.setHardness(-1.0F);
        this.setResistance(6000000.0F);
        this.setSoundType(SoundType.GLASS);
        this.setLightLevel(0.75F);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos) {
        return NO_COLLISION;
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
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        enter(world, pos, entity);
    }

    /**
     * The portal has no collision box, so walking players fall through it —
     * this is the hook that actually fires, and the one the original used.
     */
    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        enter(world, pos, entity);
    }

    private void enter(World world, BlockPos pos, Entity entity) {
        if (world.isRemote || !(entity instanceof EntityPlayerMP) || !world.provider.isSurfaceWorld()) {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) entity;
        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
                .transferPlayerToDimension(player, Config.dimensionBedrockId,
                        new TeleporterBedrock((WorldServer) player.world));

        // Hollow out the arrival pocket, as the original did.
        World target = player.world;
        for (int y = 251; y <= 253; y++) {
            BlockPos at = new BlockPos(pos.getX(), y, pos.getZ());
            if (target.getBlockState(at).getBlock() == Blocks.BEDROCK) {
                target.setBlockToAir(at);
            }
        }
    }
}
