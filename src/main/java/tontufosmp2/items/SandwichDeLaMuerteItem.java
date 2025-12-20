package tontufosmp2.items;



import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class SandwichDeLaMuerteItem extends Item {

    private static final Random RANDOM = new Random();

    public SandwichDeLaMuerteItem(Settings settings) {
        super(settings.food(new FoodComponent.Builder()
                .hunger(20)
                .saturationModifier(20.0f)
                .alwaysEdible()
                .build()));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {

        if (!world.isClient && user instanceof PlayerEntity player) {

            ServerWorld serverWorld = (ServerWorld) world;

            // Curar completamente
            player.setHealth(player.getMaxHealth());

            // Ajuste de probabilidad
            if (RANDOM.nextInt(100) < 50) {

                Vec3d pos = player.getPos();

                //  Explosión tipo TNT
                serverWorld.createExplosion(
                        player,
                        pos.x, pos.y, pos.z,
                        4.0f,
                        World.ExplosionSourceType.TNT
                );

                //  Muerte instantánea
                player.damage(
                        serverWorld.getDamageSources().generic(),
                        Float.MAX_VALUE
                );
            }
        }

        return super.finishUsing(stack, world, user);
    }
}