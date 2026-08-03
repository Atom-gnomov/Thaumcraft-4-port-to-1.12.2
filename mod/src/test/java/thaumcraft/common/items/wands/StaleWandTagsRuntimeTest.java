package thaumcraft.common.items.wands;

import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * A wand whose NBT names a cap or rod that no longer exists must degrade, not
 * crash.
 *
 * <p>The owner's world-join crash of 2026-08-03, reproduced exactly: the
 * dragonbreath <em>cap</em> shipped in 1.2.4.0, was replaced by the focus in
 * 1.2.5.0, and every wand crafted in between kept {@code cap="dragonbreath"}
 * in its NBT. {@code getCap} returned null for the unknown tag and
 * {@code ModelWand.render} died on {@code getTexture()} the moment the wand
 * appeared in hand. The registry lookup now falls back — iron for caps, wood
 * for rods — because a wand the player can still hold beats a crash report,
 * and this is the general shape of every content removal to come.</p>
 */
public class StaleWandTagsRuntimeTest {

    private static boolean registeredIron;
    private static boolean registeredWood;

    @BeforeClass
    public static void bootstrap() {
        Bootstrap.register();
        // The registries the fallbacks reach for, as Thaumcraft registers them.
        if (WandCap.caps.get("iron") == null) {
            new WandCap("iron", 1.1f, new ItemStack(net.minecraft.init.Items.IRON_NUGGET), 1);
            registeredIron = true;
        }
        if (WandRod.rods.get("wood") == null) {
            new WandRod("wood", 25, new ItemStack(net.minecraft.init.Items.STICK), 1);
            registeredWood = true;
        }
    }

    /**
     * The cap and rod maps are global statics shared across every suite in the
     * worker JVM. Leaving the fixtures in them changed the enchanter test's
     * wand-discount arithmetic two packages away — clean up what was added.
     */
    @AfterClass
    public static void unregisterFixtures() {
        if (registeredIron) {
            WandCap.caps.remove("iron");
        }
        if (registeredWood) {
            WandRod.rods.remove("wood");
        }
    }

    private static ItemStack wandWith(String capTag, String rodTag) {
        ItemStack wand = new ItemStack(Item.getItemFromBlock(net.minecraft.init.Blocks.STONE));
        NBTTagCompound tag = new NBTTagCompound();
        if (capTag != null) {
            tag.setString("cap", capTag);
        }
        if (rodTag != null) {
            tag.setString("rod", rodTag);
        }
        wand.setTagCompound(tag);
        return wand;
    }

    /** The crash itself: cap tag from a removed registration. */
    @Test
    public void aWandCappedWithRemovedContentFallsBackToIron() {
        WandCap cap = ItemWandCasting.getCap(wandWith("dragonbreath", null));
        assertNotNull("the renderer calls getTexture() on this — null is the 12.04.15 crash", cap);
        assertEquals("iron", cap.getTag());
    }

    /** The same failure one field over. */
    @Test
    public void aWandOnAnUnknownRodFallsBackToWood() {
        WandRod rod = ItemWandCasting.getRod(wandWith(null, "someRemovedExperiment"));
        assertNotNull(rod);
        assertEquals("wood", rod.getTag());
    }

    /** Known tags still resolve to themselves — the fallback must not flatten everything. */
    @Test
    public void knownTagsStillResolve() {
        assertEquals("iron", ItemWandCasting.getCap(wandWith("iron", null)).getTag());
        assertEquals("wood", ItemWandCasting.getRod(wandWith(null, "wood")).getTag());
    }
}
