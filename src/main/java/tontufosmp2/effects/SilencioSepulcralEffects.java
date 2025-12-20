package tontufosmp2.effects;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import tontufosmp2.items.SilencioSepulcralItem;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SilencioSepulcralEffects {

    private static final int RADIUS = 28;
    private static final double STEALTH_DETECTION_RANGE = 10.0;
    private static final int DURABILITY_DAMAGE_INTERVAL = 20;

    private static final HashMap<UUID, Boolean> STEALTH_STATE = new HashMap<>();

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> onWorldTick(world));
    }

    private static void onWorldTick(ServerWorld world) {

        if (world.getServer().getTicks() % 20 != 0) return;

        for (PlayerEntity player : world.getPlayers()) {

            ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);

            boolean hasMask = helmet.getItem() instanceof SilencioSepulcralItem;
            boolean stealthActive = hasMask && !breaksStealth(player);

            // Manejo de sonido
            handleStealthSound(player, stealthActive);

            if (!stealthActive) {
                clearGlow(player);
                continue;
            }

            //  Desgaste por uso
            helmet.damage(1, player, p ->
                    p.sendEquipmentBreakStatus(EquipmentSlot.HEAD)
            );

            Box box = player.getBoundingBox().expand(RADIUS);

            List<HostileEntity> hostiles =
                    world.getEntitiesByClass(
                            HostileEntity.class,
                            box,
                            LivingEntity::isAlive
                    );

            for (HostileEntity entity : hostiles) {

                // Aura
                entity.addStatusEffect(
                        new StatusEffectInstance(
                                StatusEffects.GLOWING,
                                60,
                                0,
                                false,
                                false
                        )
                );

                // Reducción de detección
                if (entity.getTarget() == player) {
                    double distance = entity.squaredDistanceTo(player);

                    if (distance > STEALTH_DETECTION_RANGE * STEALTH_DETECTION_RANGE) {
                        entity.setTarget(null);
                    }
                }
            }
        }
    }

    private static boolean breaksStealth(PlayerEntity player) {
        return player.isSprinting() || player.handSwinging;
    }

    private static void clearGlow(PlayerEntity player) {
        Box box = player.getBoundingBox().expand(RADIUS);

        List<HostileEntity> hostiles =
                player.getWorld().getEntitiesByClass(
                        HostileEntity.class,
                        box,
                        LivingEntity::isAlive
                );

        for (HostileEntity entity : hostiles) {
            entity.removeStatusEffect(StatusEffects.GLOWING);
        }
    }

    private static void handleStealthSound(PlayerEntity player, boolean stealthActive) {
        UUID id = player.getUuid();
        boolean wasStealth = STEALTH_STATE.getOrDefault(id, false);

        if (!wasStealth && stealthActive) {
            player.getWorld().playSound(
                    null,
                    player.getBlockPos(),
                    SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
                    SoundCategory.PLAYERS,
                    0.6f,
                    0.8f
            );
        }

        if (wasStealth && !stealthActive) {
            player.getWorld().playSound(
                    null,
                    player.getBlockPos(),
                    SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,
                    SoundCategory.PLAYERS,
                    0.6f,
                    1.2f
            );
        }

        STEALTH_STATE.put(id, stealthActive);
    }
}