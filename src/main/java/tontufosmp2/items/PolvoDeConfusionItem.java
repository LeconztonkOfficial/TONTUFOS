package tontufosmp2.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import tontufosmp2.entities.ConfusionProjectileEntity;

public class PolvoDeConfusionItem extends Item {

    public PolvoDeConfusionItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            ConfusionProjectileEntity projectile =
                    new ConfusionProjectileEntity(world, user);

            projectile.setItem(stack.copy());
            projectile.setVelocity(user, user.getPitch(), user.getYaw(),
                    0.0F, 1.5F, 1.0F);

            world.spawnEntity(projectile);
        }

        if (!user.getAbilities().creativeMode) {
            stack.decrement(1);
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}
