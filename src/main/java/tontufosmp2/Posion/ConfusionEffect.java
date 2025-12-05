package tontufosmp2.Posion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;


public class ConfusionEffect extends StatusEffect {

    public ConfusionEffect() {
        super(StatusEffectCategory.HARMFUL, 0xAA00FF); // Color del efecto
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient) return;

        // Intensidad del movimiento (ajusta esto)
        float intensity = 0.2f + amplifier * 1.5f;

        // Cambios bruscos en yaw y pitch
        entity.setYaw(entity.getYaw() + (entity.getRandom().nextFloat() - 0.5f) * intensity);
        entity.setPitch(entity.getPitch() + (entity.getRandom().nextFloat() - 0.5f) * intensity);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true; // Se ejecuta cada tick
    }
}

