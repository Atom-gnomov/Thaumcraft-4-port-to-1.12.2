package thaumcraft.common.config;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/**
 * Port of TC4 {@code MaterialAiry}: the material used by {@code blockAiry} (an aura node held
 * inside an otherwise-empty block). Non-solid, does not block light and is not opaque, so the
 * block renders effectively invisible — matching the original {@code blankIcon} behaviour.
 */
public class MaterialAiry extends Material {
    public MaterialAiry(MapColor color) {
        super(color);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean blocksLight() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }
}
