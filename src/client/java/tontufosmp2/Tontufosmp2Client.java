package tontufosmp2;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import tontufosmp2.entities.ModEntities;
import tontufosmp2.client.renderer.InvisibleEntityRenderer;


public class Tontufosmp2Client implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Registrar renderer del proyectil de confusión
        EntityRendererRegistry.register(
                ModEntities.CONFUSION_PROJECTILE,
                InvisibleEntityRenderer::new
        );


        EntityRendererRegistry.register(
                ModEntities.THROWABLE_LIGHT,
                ctx -> new InvisibleEntityRenderer<>(ctx)
        );


    }
}