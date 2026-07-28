package thaumcraft.common.items.tinkerer;

/**
 * Marks an item whose enchantments the Spellbinding Cloth must not strip —
 * ported from Thaumic Tinkerer's {@code INoRemoveEnchant} (pixlepix /
 * nekosune, originally Vazkii).
 *
 * <p>Nothing implements it upstream either: it is the hook a mod uses to keep
 * its own item out of the cloth's reach, and it is carried over so that hook
 * still exists here.</p>
 */
public interface INoRemoveEnchant {
}
