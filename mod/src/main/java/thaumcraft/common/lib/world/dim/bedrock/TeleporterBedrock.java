package thaumcraft.common.lib.world.dim.bedrock;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * Teleporter into the Bedrock dimension — ported from Thaumic Tinkerer's
 * {@code TeleporterBedrock} (pixlepix/nekosune, originally Vazkii).
 *
 * <p>The dimension is solid rock, so there is nothing to build a portal frame
 * into and nowhere to search for one: every hook is a no-op and the entity is
 * simply left where the transfer put it, as in the original.</p>
 */
public class TeleporterBedrock extends Teleporter {

    public TeleporterBedrock(WorldServer world) {
        super(world);
    }

    @Override
    public boolean makePortal(Entity entity) {
        return true;
    }

    @Override
    public boolean placeInExistingPortal(Entity entity, float rotationYaw) {
        return true;
    }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw) {
    }

    @Override
    public void removeStalePortalLocations(long worldTime) {
    }
}
