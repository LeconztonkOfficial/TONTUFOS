package tontufosmp2.entities;


import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import tontufosmp2.Tontufosmp2;

public class ModEntities {

    public static final EntityType<ConfusionProjectileEntity> CONFUSION_PROJECTILE =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier(Tontufosmp2.MOD_ID, "confusion_projectile"),
                    FabricEntityTypeBuilder.<ConfusionProjectileEntity>create(
                                    SpawnGroup.MISC,
                                    (entityType, world) -> new ConfusionProjectileEntity(entityType, world)
                            )
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .trackRangeBlocks(64)
                            .trackedUpdateRate(10)
                            .build()
            );
    public static final EntityType<ThrowableLightEntity> THROWABLE_LIGHT =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier(Tontufosmp2.MOD_ID, "throwable_light"),
                    FabricEntityTypeBuilder.<ThrowableLightEntity>create(
                                    SpawnGroup.MISC,
                                    (type, world) -> new ThrowableLightEntity(type, world)
                            )
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .trackRangeBlocks(64)
                            .trackedUpdateRate(10)
                            .build()
            );
    public static void registerModEntities() {
        Tontufosmp2.LOGGER.info("Registrando entidades de Tontufosmp2");
    }
}