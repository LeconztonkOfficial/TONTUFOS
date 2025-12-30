package tontufosmp2.event;


import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import tontufosmp2.enchantment.ModEnchantments;

public class ModEnchantmentsTickHandler {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ModEnchantmentsTickHandler::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {

            checkFuegoSolar(player);
            checkAlientoDelAlba(player);
            checkAlientoDeVida(player);
        }
    }

    //  FUEGO SOLAR
    private static void checkFuegoSolar(PlayerEntity player) {

        int piezas = 0;

        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        }) {
            if (EnchantmentHelper.getLevel(
                    ModEnchantments.FUEGO_SOLAR,
                    player.getEquippedStack(slot)
            ) > 0) {
                piezas++;
            }
        }

        if (piezas == 0) return;

        World world = player.getWorld();

        boolean bajoSol =
                world.isDay()
                        && !world.isRaining()
                        && world.isSkyVisible(player.getBlockPos());

        boolean fueAtacado = player.hurtTime > 0;

        //  Castigo por acumulación
        if (piezas > 1) {
            player.setOnFireFor(1);
            return;
        }

        // ☀ Curación
        if (bajoSol && !fueAtacado) {
            if (player.getHealth() < player.getMaxHealth()) {
                player.heal(0.01F);
            }
        }
    }

    // ALIENTO DEL ALBA
    private static void checkAlientoDelAlba(PlayerEntity player) {

        // Contamos cuántas piezas tienen el encantamiento
        int piezas = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        }) {
            if (EnchantmentHelper.getLevel(
                    ModEnchantments.ALIENTO_DEL_ALBA,
                    player.getEquippedStack(slot)
            ) > 0) {
                piezas++;
            }
        }

        if (piezas == 0) return;

        World world = player.getWorld();
        long time = world.getTimeOfDay() % 24000;

        // Amanecer hasta mediodía (tick 0 a 6000)
        boolean amanecerAMediodia = time >= 0 && time <= 6000;

        if (amanecerAMediodia && player.hurtTime == 0) {
            // Curación gradual, escalable según número de piezas
            if (player.getHealth() < player.getMaxHealth()) {
                player.heal(0.03F * piezas);
            }

            // Velocidad de movimiento escalable
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.SPEED,
                    40,        // duración en ticks (2 segundos, se renueva cada tick)
                    piezas - 1, // nivel del efecto
                    false,
                    false,
                    true
            ));
        }
    }
    private static void checkAlientoDeVida(PlayerEntity player) {

        // Contamos cuántas piezas tienen el encantamiento (solo pechera)
        int piezas = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        }) {
            if (EnchantmentHelper.getLevel(
                    ModEnchantments.ALIENTO_DE_VIDA,
                    player.getEquippedStack(slot)
            ) > 0) {
                piezas++;
            }
        }

        if (piezas == 0) {
            // Restauramos vida máxima base si se quitó la pieza
            if (player.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH)
                    .getBaseValue() != 20.0F) {
                player.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH)
                        .setBaseValue(20.0F);
            }
            return;
        }

        // Aumentamos vida máxima según nivel del encantamiento
        float extraHealth = piezas * 4.0F; // 2 corazones por pieza
        float baseHealth = 20.0F;

        if (player.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH)
                .getBaseValue() < baseHealth + extraHealth) {
            player.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH)
                    .setBaseValue(baseHealth + extraHealth);
        }
    }


}

