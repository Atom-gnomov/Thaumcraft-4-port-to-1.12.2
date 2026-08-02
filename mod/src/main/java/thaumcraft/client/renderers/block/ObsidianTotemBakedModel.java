package thaumcraft.client.renderers.block;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;
import thaumcraft.common.blocks.BlockCosmeticSolid;

/**
 * Draws the Obsidian Totem's varied sides.
 *
 * <p>In 1.7.10 this was a one-line override — {@code getIcon} took the world
 * and the coordinates, so the block could answer differently per position and
 * per face. In 1.12 a model is baked once and reused everywhere, and knows
 * neither. The way across is Forge's unlisted properties: the block works out
 * the four side textures in {@code getExtendedState}, and this reads them back
 * out. The choice itself lives in
 * {@link BlockCosmeticSolid#getExtendedState}, next to the original it copies.
 *
 * <p>Top and bottom are not part of that: they take the plain obsidian tile,
 * which is what the original's flat {@code getIcon(side, meta)} returns for
 * these metadata values — and therefore also what the block looks like held in
 * the hand. A totem only becomes a totem once it is placed.</p>
 *
 * <p>Modelled on {@code WardedGlassBakedModel}, which solves the same problem
 * for connected glass.</p>
 */
public final class ObsidianTotemBakedModel implements IBakedModel {

    private static final FaceBakery FACE_BAKERY = new FaceBakery();
    private static final BlockFaceUV FULL_UV = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
    /** One past the side textures: the index this model uses for the flat ends. */
    private static final int END_TEXTURE = BlockCosmeticSolid.TOTEM_TEXTURE_COUNT;
    private static final int TEXTURE_COUNT = END_TEXTURE + 1;

    private final IBakedModel delegate;
    private final Map<Integer, List<BakedQuad>> cache = new ConcurrentHashMap<>();

    public ObsidianTotemBakedModel(IBakedModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (side == null) {
            return ImmutableList.of();
        }
        int textureIndex = textureFor(state, side);
        int key = side.getIndex() * TEXTURE_COUNT + textureIndex;
        return this.cache.computeIfAbsent(key, ignored -> ImmutableList.of(makeFace(side, textureIndex)));
    }

    /**
     * The end faces are fixed; the sides come from the block. A missing or
     * out-of-range property means this is being drawn without a world behind it
     * — as an item, or before {@code getExtendedState} has run — and the
     * original draws the plain tile in exactly that case.
     */
    private static int textureFor(@Nullable IBlockState state, EnumFacing side) {
        if (side == EnumFacing.DOWN || side == EnumFacing.UP) {
            return END_TEXTURE;
        }
        if (!(state instanceof IExtendedBlockState)) {
            return END_TEXTURE;
        }
        Integer value = ((IExtendedBlockState) state).getValue(propertyFor(side));
        return value != null && value >= 0 && value < BlockCosmeticSolid.TOTEM_TEXTURE_COUNT
                ? value
                : END_TEXTURE;
    }

    private static IUnlistedProperty<Integer> propertyFor(EnumFacing side) {
        switch (side) {
            case NORTH:
                return BlockCosmeticSolid.TOTEM_NORTH;
            case SOUTH:
                return BlockCosmeticSolid.TOTEM_SOUTH;
            case WEST:
                return BlockCosmeticSolid.TOTEM_WEST;
            case EAST:
            default:
                return BlockCosmeticSolid.TOTEM_EAST;
        }
    }

    private static BakedQuad makeFace(EnumFacing face, int textureIndex) {
        BlockPartFace partFace = new BlockPartFace(null, -1, "", FULL_UV);
        return FACE_BAKERY.makeBakedQuad(
                new Vector3f(0.0F, 0.0F, 0.0F),
                new Vector3f(16.0F, 16.0F, 16.0F),
                partFace,
                sprite(textureIndex),
                face,
                ModelRotation.X0_Y0,
                null,
                false,
                true);
    }

    private static TextureAtlasSprite sprite(int index) {
        String name = index == END_TEXTURE
                ? BlockCosmeticSolid.TOTEM_END_TEXTURE
                : BlockCosmeticSolid.TOTEM_TEXTURES[index];
        return Minecraft.getMinecraft().getTextureMapBlocks()
                .getAtlasSprite("thaumcraft:blocks/" + name);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return sprite(END_TEXTURE);
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.delegate.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.delegate.getOverrides();
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType type) {
        Pair<? extends IBakedModel, Matrix4f> delegatePerspective = this.delegate.handlePerspective(type);
        return Pair.of(this, delegatePerspective.getRight());
    }
}
