package tontufosmp2.mixin.client;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tontufosmp2.Posion.ModEffects;

@Mixin(GameRenderer.class)
public class ConfusionCameraMixin {

    private float swayTime = 0f;
    private float currentSwayX = 0f;
    private float currentSwayY = 0f;
    private float currentRoll = 0f;

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V",
                    shift = At.Shift.AFTER
            )
    )
    private void injectConfusionAfterBob(
            float tickDelta,
            long limitTime,
            MatrixStack matrices,
            CallbackInfo ci
    ) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        StatusEffectInstance effect = player.getStatusEffect(ModEffects.CONFUSION);
        if (effect != null) {
            float time = (client.world.getTime() + tickDelta);

            matrices.translate(
                    Math.sin(time * 0.22f) * 0.1f,
                    Math.cos(time * 0.20f) * 0.1f,
                    0.0f
            );

            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(
                    (float) (Math.sin(time * 0.1f) * 4.5f)
            ));
        }
    }
}