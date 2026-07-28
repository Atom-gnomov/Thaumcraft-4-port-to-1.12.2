package thaumcraft.common.tiles.tinkerer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.tinkerer.ItemMobAspect;
import thaumcraft.common.lib.tinkerer.MobAspects;
import thaumcraft.common.tiles.TilePedestal;

/**
 * The Tablet of Necromancy at work — ported from Thaumic Tinkerer's
 * {@code TileSummon} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>Every fifteen seconds it looks over the pedestals around it for three
 * holding Soul Aspects that together name a creature, and puts that creature
 * on top of itself. Plain souls are spent doing it; infused ones survive but
 * only fire once a minute, and only after being used somewhere else in
 * between — otherwise one set would run forever on a single tablet.</p>
 *
 * <p>Redstone stops it.</p>
 */
public class TileSummon extends TileThaumcraft implements ITickable {

    /** The original's cadence: a look round every 300 ticks. */
    private static final int INTERVAL = 300;
    /** Infused souls are held back to one summon per 1200 ticks. */
    private static final int INFUSED_INTERVAL = 1200;
    /** How far out the pedestals may stand. */
    private static final int RADIUS = 5;

    @Override
    public void update() {
        if (this.world.getTotalWorldTime() % INTERVAL != 0) {
            return;
        }
        if (this.world.isBlockPowered(this.pos)) {
            return;
        }

        List<TilePedestal> pedestals = findPedestals();
        // Upstream's triple loop, which considers every ordered triple.
        for (TilePedestal first : pedestals) {
            for (TilePedestal second : pedestals) {
                for (TilePedestal third : pedestals) {
                    if (first == second || second == third || first == third) {
                        continue;
                    }
                    if (trySummon(first, second, third)) {
                        return;
                    }
                }
            }
        }
    }

    private List<TilePedestal> findPedestals() {
        List<TilePedestal> found = new ArrayList<>();
        for (int x = this.pos.getX() - RADIUS; x < this.pos.getX() + RADIUS; x++) {
            for (int z = this.pos.getZ() - RADIUS; z < this.pos.getZ() + RADIUS; z++) {
                TileEntity tile = this.world.getTileEntity(new BlockPos(x, this.pos.getY(), z));
                if (!(tile instanceof TilePedestal)) {
                    continue;
                }
                ItemStack held = ((TilePedestal) tile).getStackInSlot(0);
                if (!held.isEmpty() && held.getItem() instanceof ItemMobAspect) {
                    found.add((TilePedestal) tile);
                }
            }
        }
        return found;
    }

    /** @return true when this triple was a match, whether or not it fired */
    private boolean trySummon(TilePedestal first, TilePedestal second, TilePedestal third) {
        ItemStack a = first.getStackInSlot(0);
        ItemStack b = second.getStackInSlot(0);
        ItemStack c = third.getStackInSlot(0);

        List<Aspect> aspects = Arrays.asList(
                ItemMobAspect.getAspect(a), ItemMobAspect.getAspect(b), ItemMobAspect.getAspect(c));
        MobAspects.Entry match = MobAspects.match(aspects);
        if (match == null) {
            return false;
        }

        boolean infused = ItemMobAspect.isInfused(a)
                && ItemMobAspect.isInfused(b) && ItemMobAspect.isInfused(c);
        if (infused && this.world.getTotalWorldTime() % INFUSED_INTERVAL != 0) {
            return true;
        }
        if (!infused) {
            first.setInventorySlotContents(0, ItemStack.EMPTY);
            second.setInventorySlotContents(0, ItemStack.EMPTY);
            third.setInventorySlotContents(0, ItemStack.EMPTY);
        }

        boolean freshHere = ItemMobAspect.lastUsedTabletMatches(a, this.pos)
                && ItemMobAspect.lastUsedTabletMatches(b, this.pos)
                && ItemMobAspect.lastUsedTabletMatches(c, this.pos);
        if (freshHere) {
            if (!this.world.isRemote) {
                spawn(match);
            } else {
                for (int i = 0; i < 3; i++) {
                    TilePedestal from = i == 0 ? first : i == 1 ? second : third;
                    BlockPos at = from.getPos();
                    Thaumcraft.proxy.essentiaTrailFx(this.world,
                            at.getX(), at.getY(), at.getZ(),
                            this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                            20, aspects.get(i).getColor(), 20);
                }
            }
        }
        if (infused) {
            ItemMobAspect.markLastUsedTablet(a, this.pos);
            ItemMobAspect.markLastUsedTablet(b, this.pos);
            ItemMobAspect.markLastUsedTablet(c, this.pos);
        }
        return true;
    }

    private void spawn(MobAspects.Entry match) {
        Entity spawned = EntityList.createEntityByIDFromName(match.getId(), this.world);
        if (spawned == null) {
            return;
        }
        spawned.setLocationAndAngles(this.pos.getX() + 0.5D, this.pos.getY() + 1, this.pos.getZ() + 0.5D, 0, 0);
        // Upstream turned a skeleton in the Nether into a wither skeleton. That
        // is a separate entity in this version, so the table names it directly
        // instead and nothing special happens here.
        this.world.spawnEntity(spawned);
        if (spawned instanceof EntityLiving) {
            ((EntityLiving) spawned).onInitialSpawn(
                    this.world.getDifficultyForLocation(this.pos), null);
            ((EntityLiving) spawned).playLivingSound();
        }
    }
}
