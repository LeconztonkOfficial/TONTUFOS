package tontufosmp2.items;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.util.UseAction;
import tontufosmp2.entities.ThrowableLightEntity;

public class PocionLuzItem extends Item {

    public PocionLuzItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            ThrowableLightEntity light = new ThrowableLightEntity(
                    tontufosmp2.entities.ModEntities.THROWABLE_LIGHT, world
            );

            light.setOwner(user);
            light.setPosition(user.getX(), user.getEyeY(), user.getZ());
            light.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);

            world.spawnEntity(light);
        }

        user.swingHand(hand, true);

        if (!user.isCreative()) {
            stack.decrement(1);
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }
}
