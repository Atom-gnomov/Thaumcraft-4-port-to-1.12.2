package thaumcraft.common.tiles.tinkerer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;

/**
 * Transvector Interface — reimplemented from Thaumic Tinkerer
 * (pixlepix/nekosune, originally Vazkii) for 1.12.2.
 *
 * <p>Acts as a stand-in for another block up to {@value #MAX_DISTANCE} blocks
 * away: anything asking this block for its inventory, tanks, energy or
 * essentia is answered by the linked block instead. Link it with the
 * Transvector Connector.</p>
 *
 * <p>The original hand-wrote a delegate for every interface it knew about
 * (ISidedInventory, IFluidHandler, BuildCraft and RF power, …). On 1.12 those
 * all travel through the capability system, so forwarding {@code getCapability}
 * covers items, fluids and every mod's energy in one place — including mods the
 * original never knew about. Thaumcraft's own essentia interfaces are not
 * capabilities, so they are delegated explicitly below.</p>
 */
public class TileTransvectorInterface extends TileTransvector implements IAspectContainer, IEssentiaTransport {

    /** Per-axis reach, as in the original (LibFeatures.INTERFACE_DISTANCE). */
    public static final int MAX_DISTANCE = 4;

    @Override
    public int getMaxDistance() {
        return MAX_DISTANCE;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        TileEntity linked = getLinkedTile();
        if (linked != null) {
            linked.markDirty();
        }
    }

    // ---- Capabilities: items, fluids, energy — whatever the far block offers ----

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        TileEntity linked = getLinkedTile();
        return (linked != null && linked.hasCapability(capability, facing))
                || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        TileEntity linked = getLinkedTile();
        if (linked != null && linked.hasCapability(capability, facing)) {
            return linked.getCapability(capability, facing);
        }
        return super.getCapability(capability, facing);
    }

    // ---- Thaumcraft essentia: plain interfaces, delegated by hand ----

    @Nullable
    private IAspectContainer container() {
        TileEntity linked = getLinkedTile();
        return linked instanceof IAspectContainer ? (IAspectContainer) linked : null;
    }

    @Nullable
    private IEssentiaTransport transport() {
        TileEntity linked = getLinkedTile();
        return linked instanceof IEssentiaTransport ? (IEssentiaTransport) linked : null;
    }

    @Override
    public AspectList getAspects() {
        IAspectContainer c = container();
        return c == null ? null : c.getAspects();
    }

    @Override
    public void setAspects(AspectList aspects) {
        IAspectContainer c = container();
        if (c != null) {
            c.setAspects(aspects);
        }
    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        IAspectContainer c = container();
        return c != null && c.doesContainerAccept(aspect);
    }

    @Override
    public int addToContainer(Aspect aspect, int amount) {
        IAspectContainer c = container();
        return c == null ? amount : c.addToContainer(aspect, amount);
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int amount) {
        IAspectContainer c = container();
        return c != null && c.takeFromContainer(aspect, amount);
    }

    @Override
    public boolean takeFromContainer(AspectList aspects) {
        IAspectContainer c = container();
        return c != null && c.takeFromContainer(aspects);
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int amount) {
        IAspectContainer c = container();
        return c != null && c.doesContainerContainAmount(aspect, amount);
    }

    @Override
    public boolean doesContainerContain(AspectList aspects) {
        IAspectContainer c = container();
        return c != null && c.doesContainerContain(aspects);
    }

    @Override
    public int containerContains(Aspect aspect) {
        IAspectContainer c = container();
        return c == null ? 0 : c.containerContains(aspect);
    }

    @Override
    public boolean isConnectable(EnumFacing face) {
        IEssentiaTransport t = transport();
        return t != null && t.isConnectable(face);
    }

    @Override
    public boolean canInputFrom(EnumFacing face) {
        IEssentiaTransport t = transport();
        return t != null && t.canInputFrom(face);
    }

    @Override
    public boolean canOutputTo(EnumFacing face) {
        IEssentiaTransport t = transport();
        return t != null && t.canOutputTo(face);
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        IEssentiaTransport t = transport();
        if (t != null) {
            t.setSuction(aspect, amount);
        }
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    @Override
    public int getMinimumSuction() {
        IEssentiaTransport t = transport();
        return t == null ? 0 : t.getMinimumSuction();
    }

    @Override
    public Aspect getSuctionType(EnumFacing face) {
        IEssentiaTransport t = transport();
        return t == null ? null : t.getSuctionType(face);
    }

    @Override
    public int getSuctionAmount(EnumFacing face) {
        IEssentiaTransport t = transport();
        return t == null ? 0 : t.getSuctionAmount(face);
    }

    @Override
    public Aspect getEssentiaType(EnumFacing face) {
        IEssentiaTransport t = transport();
        return t == null ? null : t.getEssentiaType(face);
    }

    @Override
    public int getEssentiaAmount(EnumFacing face) {
        IEssentiaTransport t = transport();
        return t == null ? 0 : t.getEssentiaAmount(face);
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, EnumFacing face) {
        IEssentiaTransport t = transport();
        return t == null ? 0 : t.takeEssentia(aspect, amount, face);
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, EnumFacing face) {
        IEssentiaTransport t = transport();
        return t == null ? 0 : t.addEssentia(aspect, amount, face);
    }
}
