package thaumcraft.common.items.tinkerer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.IRepairable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.tinkerer.MobAspects;

/**
 * Cursed Spirit's Blade — ported from Thaumic Tinkerer's
 * {@code ItemBloodSword} (pixlepix / nekosune, originally Vazkii).
 *
 * <p>It hits far harder than anything else and moves you faster, and it takes
 * the price out of you: blocking with it turns the blow into three magic damage
 * you cannot armour against, and striking anything costs you two more.</p>
 *
 * <p>Sneak-right-click toggles essentia harvesting. With it on, whatever you
 * kill drops nothing of its own — only the Soul Aspects that make it up.</p>
 */
public class ItemBloodSword extends ItemSword implements IRepairable {

    private static final String TAG_ACTIVATED = "Activated";
    /** The original's flat attack bonus. */
    private static final int DAMAGE = 10;
    /** Recoil damage skips three events after firing, or it loops. */
    private static int handleNext = 0;

    public ItemBloodSword(ToolMaterial material) {
        super(material);
        MinecraftForge.EVENT_BUS.register(this);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    public static boolean isHarvesting(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().getInteger(TAG_ACTIVATED) == 1;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> modifiers = HashMultimap.create();
        if (slot == EntityEquipmentSlot.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", DAMAGE, 0));
            modifiers.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(),
                    new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", 0.25D, 1));
        }
        return modifiers;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ActionResult<ItemStack> result = super.onItemRightClick(world, player, hand);
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking() && !world.isRemote) {
            NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
            boolean on = tag.getInteger(TAG_ACTIVATED) == 0;
            tag.setInteger(TAG_ACTIVATED, on ? 1 : 0);
            stack.setTagCompound(tag);
            player.sendMessage(new TextComponentTranslation(on
                    ? "ttmisc.bloodSword.activateEssentiaHarvest"
                    : "ttmisc.bloodSword.deactivateEssentiaHarvest"));
        }
        return result;
    }

    /** With harvesting on, the kill drops its souls and nothing else. */
    @SubscribeEvent
    public void onDrops(LivingDropsEvent event) {
        if (!"player".equals(event.getSource().damageType)
                || !(event.getSource().getTrueSource() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty() || held.getItem() != this || !isHarvesting(held)) {
            return;
        }
        Aspect[] aspects = MobAspects.aspectsFor(event.getEntityLiving().getClass());
        if (aspects == null) {
            return;
        }
        event.getDrops().clear();
        for (Aspect aspect : aspects) {
            ItemStack soul = ItemMobAspect.stackFor(aspect);
            if (!soul.isEmpty()) {
                addDrop(event, soul);
            }
        }
    }

    private static void addDrop(LivingDropsEvent event, ItemStack stack) {
        EntityLivingBase dying = event.getEntityLiving();
        EntityItem item = new EntityItem(dying.world, dying.posX, dying.posY, dying.posZ, stack);
        item.setPickupDelay(10);
        event.getDrops().add(item);
    }

    /**
     * The blade's price. Blocking with it converts the blow into three magic
     * damage to the blocker; swinging it costs two. {@code handleNext} keeps
     * the recoil from re-entering its own event.
     */
    @SubscribeEvent
    public void onDamageTaken(LivingAttackEvent event) {
        if (event.getEntity().world.isRemote) {
            return;
        }
        boolean handle = handleNext == 0;
        if (!handle) {
            handleNext--;
        }
        if (handle && event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            ItemStack active = player.getActiveItemStack();
            if (!active.isEmpty() && active.getItem() == this) {
                event.setCanceled(true);
                handleNext = 3;
                player.attackEntityFrom(DamageSource.MAGIC, 3);
            }
        }
        if (handle) {
            Entity source = event.getSource().getImmediateSource();
            if (source instanceof EntityLivingBase) {
                EntityLivingBase attacker = (EntityLivingBase) source;
                ItemStack held = attacker.getHeldItemMainhand();
                if (!held.isEmpty() && held.getItem() == this) {
                    attacker.attackEntityFrom(DamageSource.MAGIC, 2);
                }
            }
        }
    }
}
