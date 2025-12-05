package tontufosmp2;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import tontufosmp2.entities.ModEntities;


public class Tontufosmp2Client implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Registrar renderer del proyectil de confusión
        EntityRendererRegistry.register(
                ModEntities.CONFUSION_PROJECTILE,
                FlyingItemEntityRenderer::new
        );

        EntityRendererRegistry.register(
                ModEntities.THROWABLE_LIGHT, // Asegúrate de que este sea el nombre de tu entidad en ModEntities
                FlyingItemEntityRenderer::new
        );
    }}