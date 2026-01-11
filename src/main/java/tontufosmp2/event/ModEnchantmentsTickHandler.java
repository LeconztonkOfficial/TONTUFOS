package tontufosmp2.event;


import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import tontufosmp2.enchantment.ModEnchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.Vec3d;
import java.util.List;


public class ModEnchantmentsTickHandler {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ModEnchantmentsTickHandler::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {

            checkFuegoSolar(player);
            checkAlientoDelAlba(player);
            checkAlientoDeVida(player);
            checkGranForja(player);
            checkMedianoche(player);
            checkOjoDeCazador(player);
            checkPulsoVenenoso(player);
            checkSaltoVital(player);
            checkPulsoVital(player);
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

    // GRAN FORJA ENCANTAMIENTO


    private static void checkGranForja(PlayerEntity player) {
        ItemStack shield = player.getOffHandStack(); // Escudo en la mano secundaria
        int nivel = EnchantmentHelper.getLevel(ModEnchantments.GRAN_FORJA, shield);

        if (nivel <= 0) return;

        // 1️ Aplicar resistencia temporal física
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.RESISTANCE,
                80,
                nivel - 1,
                false,
                false,
                true
        ));

        // 2 Aumento de daño de armas (para simplificar, aplicamos un boost de Strength)
        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.STRENGTH,
                80,
                nivel - 1,
                false,
                false,
                true
        ));
    }

    // ------------------------
// MEDIANOCHE (Invisibilidad fuerte balanceada)
// ------------------------
    private static void checkMedianoche(PlayerEntity player) {

        // Medianoche SOLO en botas
        if (EnchantmentHelper.getLevel(
                ModEnchantments.MEDIA_NOCHE,
                player.getEquippedStack(EquipmentSlot.FEET)
        ) <= 0) return;

        World world = player.getWorld();
        long time = world.getTimeOfDay() % 24000;

        boolean esDeNoche = time >= 13000 && time <= 23000;
        boolean pocaLuz = world.getLightLevel(player.getBlockPos()) <= 4;

        // Si falla una condición, no se aplican efectos
        if (!esDeNoche || !pocaLuz || player.hurtTime > 0) {
            return;
        }

        // Invisibilidad fuerte
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.INVISIBILITY,
                40,
                0,
                false,
                false,
                true
        ));

        // Velocidad de sigilo
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SPEED,
                40,
                0,
                false,
                false,
                true
        ));

        // Debilita reacción enemiga (opcional pero interesante)
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.WEAKNESS,
                40,
                0,
                false,
                false,
                true
        ));
    }

    // ------------------------
// OJO DE CAZADOR
// ------------------------
    private static void checkOjoDeCazador(PlayerEntity player) {

        // Solo si está en el casco
        if (EnchantmentHelper.getLevel(
                ModEnchantments.OJO_CAZADOR,
                player.getEquippedStack(EquipmentSlot.HEAD)
        ) <= 0) return;

        // Requiere estar agachado (activar conscientemente)
        if (!player.isSneaking()) return;

        World world = player.getWorld();

        // Cooldown simple usando edad del jugador
        if (player.age % 40 != 0) return; // cada 2 segundos

        double radio = 16.0;

        world.getEntitiesByClass(
                net.minecraft.entity.mob.HostileEntity.class,
                player.getBoundingBox().expand(radio),
                mob -> mob.isAlive()
        ).forEach(mob -> {

            // Efecto de marcado
            mob.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.GLOWING,
                    60, // 3 segundos
                    0,
                    false,
                    false,
                    true
            ));
        });
    }

    ////PULSO VENENOSO

    private static void checkPulsoVenenoso(PlayerEntity player) {

        World world = player.getWorld();
        if (world.isClient) return;

        int level = EnchantmentHelper.getLevel(
                ModEnchantments.PULSO_VENENOSO,
                player.getEquippedStack(EquipmentSlot.CHEST)
        );

        if (level <= 0) return;

        // Solo cuando el jugador ataca
        if (!player.handSwinging) return;

        // Cooldown simple
        if (player.getItemCooldownManager().isCoolingDown(
                player.getMainHandStack().getItem()
        )) return;

        List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                player.getBoundingBox().expand(2.5),
                entity -> entity instanceof HostileEntity && entity.isAlive()
        );

        for (LivingEntity target : targets) {

            Vec3d look = player.getRotationVec(1.0F);
            Vec3d direction = target.getPos().subtract(player.getPos()).normalize();

            // Debe estar frente al jugador (cono de ataque)
            if (look.dotProduct(direction) < 0.8) continue;

            // Aplicar veneno
            target.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.POISON,
                    60 + (level * 20), // duración
                    0                  // nivel del veneno
            ));

            // Cooldown del arma
            player.getItemCooldownManager().set(
                    player.getMainHandStack().getItem(),
                    40
            );

            break; // solo un objetivo por golpe
        }
    }


//SALTO VITAL


    private static void checkSaltoVital(PlayerEntity player) {

        int level = EnchantmentHelper.getLevel(
                ModEnchantments.SALTO_VITAL,
                player.getEquippedStack(EquipmentSlot.FEET)
        );

        if (level <= 0) return;

        // Jump Boost constante
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.JUMP_BOOST,
                20,    // 1 segundo (se renueva)
                0,
                false,
                false,
                true
        ));

        // Reducir daño por caída
        if (player.fallDistance > 3.0F) {
            player.fallDistance *= 0.6F;
        }

        // Consumir hambre SOLO cuando salta (server-safe)
        if (!player.isOnGround() && player.getVelocity().y > 0.0D) {
            if (player.getHungerManager().getFoodLevel() > 0) {
                player.getHungerManager().add(-1, 0.0F);
            }
        }
    }


    private static void checkPulsoVital(PlayerEntity player) {

        int level = EnchantmentHelper.getLevel(
                ModEnchantments.PULSO_VITAL,
                player.getEquippedStack(EquipmentSlot.CHEST)
        );

        if (level <= 0) return;

        float vidaActual = player.getHealth();
        float vidaMax = player.getMaxHealth();

        // 30% de vida o menos
        if (vidaActual / vidaMax > 0.3F) return;

        // Cooldown usando el encantamiento como referencia
        if (player.getItemCooldownManager().isCoolingDown(
                player.getEquippedStack(EquipmentSlot.CHEST).getItem()
        )) return;

        // Regeneración fuerte de emergencia
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.REGENERATION,
                100,   // 5 segundos
                2,     // Regeneración III
                false,
                false,
                true
        ));

        // Cooldown largo (30 segundos)
        player.getItemCooldownManager().set(
                player.getEquippedStack(EquipmentSlot.CHEST).getItem(),
                600
        );
    }


}

