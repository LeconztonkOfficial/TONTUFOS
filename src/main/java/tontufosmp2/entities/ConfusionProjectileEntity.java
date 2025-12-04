package tontufosmp2.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import tontufosmp2.Posion.ModEffects;
import tontufosmp2.items.ModItems;

import java.util.List;

public class ConfusionProjectileEntity extends ThrownItemEntity {

    // Constructor requerido por Fabric
    public ConfusionProjectileEntity(EntityType<? extends ConfusionProjectileEntity> type, World world) {
        super(type, world);
    }

    // Constructor para cuando el jugador lo lanza
    public ConfusionProjectileEntity(World world, LivingEntity owner) {
        super(ModEntities.CONFUSION_PROJECTILE, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.POLVODECONFUCION;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (!this.getWorld().isClient) {

            double radius = 3.0;

            // 1) Aplicación instantánea del efecto a entidades dentro del radio
            List<LivingEntity> entities = this.getWorld().getEntitiesByClass(
                    LivingEntity.class,
                    new net.minecraft.util.math.Box(
                            this.getX() - radius,
                            this.getY() - radius,
                            this.getZ() - radius,
                            this.getX() + radius,
                            this.getY() + radius,
                            this.getZ() + radius
                    ),
                    e -> true
            );

            for (LivingEntity living : entities) {
                living.addStatusEffect(new StatusEffectInstance(
                        ModEffects.CONFUSION,
                        2400,   // Duración en ticks (10 segundos)
                        0     // Nivel del efecto
                ));
            }

            // 2) Crear nube instantánea solo visual para efecto splash
            AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(
                    this.getWorld(),
                    this.getX(),
                    this.getY(),
                    this.getZ()
            );

            cloud.setRadius((float) radius);
            cloud.setDuration(1); // dura 1 tick (solo animación)
            cloud.setWaitTime(0);
            cloud.setParticleType(ParticleTypes.EFFECT);

            this.getWorld().spawnEntity(cloud);

            // 3) Eliminar el proyectil
            this.discard();
        }
    }
}