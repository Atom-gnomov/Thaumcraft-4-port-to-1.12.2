package thaumcraft.common.items.tinkerer;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.common.lib.CreativeTabTinkerer;

/**
 * Cat's Amulet — ported 1:1 from Thaumic Tinkerer's {@code ItemCatAmulet}
 * (pixlepix / nekosune / Vazkii). Worn in the amulet slot, it makes nearby
 * creatures treat the wearer as they would an ocelot: anything that flees
 * ocelots flees the wearer instead, anything hunting players loses interest,
 * and creepers stop counting down and drop their target.
 */
public class ItemCatAmulet extends Item implements IBauble {

    /** The original's search box: ten blocks out, four up and down. */
    private static final int RANGE = 10;
    private static final int RANGE_Y = 4;

    public ItemCatAmulet() {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabTinkerer.tabTinkerer);
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.AMULET;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        AxisAlignedBB box = new AxisAlignedBB(
                player.posX - RANGE, player.posY - RANGE_Y, player.posZ - RANGE,
                player.posX + RANGE, player.posY + RANGE_Y, player.posZ + RANGE);
        List<EntityLiving> entities = player.world.getEntitiesWithinAABB(EntityLiving.class, box);

        for (EntityLiving entity : entities) {
            List<EntityAITasks.EntityAITaskEntry> entries = new ArrayList<>(entity.tasks.taskEntries);
            entries.addAll(entity.targetTasks.taskEntries);

            for (EntityAITasks.EntityAITaskEntry entry : entries) {
                if (entry.action instanceof EntityAIAvoidEntity) {
                    messWithRunAwayAI((EntityAIAvoidEntity<?>) entry.action);
                }
                if (entry.action instanceof EntityAINearestAttackableTarget) {
                    messWithGetTargetAI((EntityAINearestAttackableTarget<?>) entry.action);
                }
            }

            if (entity instanceof EntityCreeper) {
                setCreeperFuse((EntityCreeper) entity);
                entity.setAttackTarget(null);
            }
        }
    }

    /** Anything set to run from ocelots is pointed at players instead. */
    private static void messWithRunAwayAI(EntityAIAvoidEntity<?> ai) {
        swapClassField(ai, EntityAIAvoidEntity.class, EntityOcelot.class, EntityPlayer.class);
    }

    /** Anything hunting players is pointed at something that will not be around. */
    private static void messWithGetTargetAI(EntityAINearestAttackableTarget<?> ai) {
        swapClassField(ai, EntityAINearestAttackableTarget.class, EntityPlayer.class, EntityEnderCrystal.class);
    }

    /**
     * Both AI tasks keep their subject in a single {@code Class} field, which is
     * private here where the original could assign it directly.
     */
    private static void swapClassField(Object ai, Class<?> owner, Class<?> expected, Class<?> replacement) {
        try {
            for (Field field : owner.getDeclaredFields()) {
                if (!Class.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                field.setAccessible(true);
                if (field.get(ai) == expected) {
                    field.set(ai, replacement);
                }
                return;
            }
        } catch (Exception ignored) {
        }
    }

    /** The original set timeSinceIgnited to 2, which is private in 1.12. */
    private static void setCreeperFuse(EntityCreeper creeper) {
        try {
            net.minecraftforge.fml.common.ObfuscationReflectionHelper.setPrivateValue(
                    EntityCreeper.class, creeper, 2, "timeSinceIgnited", "field_70833_d");
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }
}
