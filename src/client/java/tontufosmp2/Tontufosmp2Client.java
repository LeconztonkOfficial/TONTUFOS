package tontufosmp2;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
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


        // Registra el renderizador para la entidad de luz temporal
        // Hace que lo que tires se vea mientras va cayendo, como si fuese una bola de nieve en el juego
        EntityRendererRegistry.register(
                ModEntities.THROWABLE_LIGHT,
                FlyingItemEntityRenderer::new
        );


    }
}
