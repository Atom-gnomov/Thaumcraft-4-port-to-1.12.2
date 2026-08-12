package thaumcraft.common.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.Optional;
import thaumcraft.common.Thaumcraft;

/**
 * Интеграция Unbound Technology: нитор — вечный пассивный источник тепла IC2
 * (20 HU/t со всех сторон; Стирлинг из этого извлекает ~10 EU/t). Величина —
 * вердикт аудита интеграции (docs/integration/ic2_v3_audit.md П-4, v5 §2
 * «без изменений»). Интерфейс {@code IHeatSource} навешивается только при
 * загруженном IC2 ({@link Optional.Interface} снимает его в рантайме без IC2,
 * сборка использует compile-only зависимость deobfProvided).
 */
@Optional.Interface(iface = "ic2.api.energy.tile.IHeatSource", modid = "ic2")
public class TileNitor extends TileEntity implements ITickable, ic2.api.energy.tile.IHeatSource {

    /** Вечное тепло нитора для машин IC2: 20 HU/t, любая сторона. */
    public static final int HEAT_PER_TICK = 20;

    @Override
    public void update() {
        if (this.world == null || !this.world.isRemote) {
            return;
        }
        if (this.world.rand.nextInt(9 - Thaumcraft.proxy.particleCount(2)) == 0) {
            Thaumcraft.proxy.wispFX3(
                    this.world,
                    this.pos.getX() + 0.5f,
                    this.pos.getY() + 0.5f,
                    this.pos.getZ() + 0.5f,
                    this.pos.getX() + 0.3f + this.world.rand.nextFloat() * 0.4f,
                    this.pos.getY() + 0.5f,
                    this.pos.getZ() + 0.3f + this.world.rand.nextFloat() * 0.4f,
                    0.5f,
                    4,
                    true,
                    -0.025f);
        }
        if (this.world.rand.nextInt(15 - Thaumcraft.proxy.particleCount(4)) == 0) {
            Thaumcraft.proxy.wispFX3(
                    this.world,
                    this.pos.getX() + 0.5f,
                    this.pos.getY() + 0.5f,
                    this.pos.getZ() + 0.5f,
                    this.pos.getX() + 0.4f + this.world.rand.nextFloat() * 0.2f,
                    this.pos.getY() + 0.5f,
                    this.pos.getZ() + 0.4f + this.world.rand.nextFloat() * 0.2f,
                    0.25f,
                    1,
                    true,
                    -0.02f);
        }
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    @Optional.Method(modid = "ic2")
    public int maxrequestHeatTick(EnumFacing side) {
        return HEAT_PER_TICK;
    }

    @Override
    @Optional.Method(modid = "ic2")
    public int requestHeat(EnumFacing side, int request) {
        return Math.min(request, HEAT_PER_TICK);
    }
}
