package thaumcraft.client.renderers.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import thaumcraft.common.blocks.tinkerer.BlockCamo;

/**
 * Draws a camouflaged device as whatever block it is disguised as, falling back
 * to its own model when it is not disguised.
 *
 * <p>Thaumic Tinkerer did this in 1.7.10 by overriding {@code getIcon} to hand
 * back the disguised block's icon per face. There are no per-face icons any
 * more, so the disguise is read off the unlisted {@link BlockCamo#CAMO}
 * property and the disguised block's own baked model supplies the quads.</p>
 */
public final class CamoBakedModel implements IBakedModel {

    private final IBakedModel delegate;

    public CamoBakedModel(IBakedModel delegate) {
        this.delegate = delegate;
    }

    /** The model of the disguise, or our own when there is none. */
    private IBakedModel modelFor(@Nullable IBlockState state) {
        if (!(state instanceof IExtendedBlockState)) {
            return this.delegate;
        }
        IBlockState camo = ((IExtendedBlockState) state).getValue(BlockCamo.CAMO);
        if (camo == null) {
            return this.delegate;
        }
        return Minecraft.getMinecraft().getBlockRendererDispatcher()
                .getModelForState(camo);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        IBakedModel model = modelFor(state);
        // The disguise's model wants the disguise's state, not ours.
        IBlockState pass = model == this.delegate || !(state instanceof IExtendedBlockState)
                ? state
                : ((IExtendedBlockState) state).getValue(BlockCamo.CAMO);
        return model.getQuads(pass, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.delegate.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.delegate.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.delegate.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.delegate.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.delegate.getOverrides();
    }
}
