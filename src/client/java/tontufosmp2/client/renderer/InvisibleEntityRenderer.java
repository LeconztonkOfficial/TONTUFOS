package tontufosmp2.client.renderer;


import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.entity.Entity;

public class InvisibleEntityRenderer<T extends Entity> extends EntityRenderer<T> {

    public InvisibleEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {}

    @Override
    public Identifier getTexture(T entity) {
        return null;
    }
}