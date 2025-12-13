package tontufosmp2.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.UseAction;
import tontufosmp2.blocks.ModBlocks;
import tontufosmp2.blocks.TemporaryLightBlock;
import tontufosmp2.entities.ThrowableLightEntity;

public class PocionLuzItem extends Item {

    public PocionLuzItem(Settings settings) {
        super(settings);
    }

    /**
    si el jugador esta agachado se crean bloques de luz alrededor
     si se tira normal solo genera 1
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            if (user.isSneaking()) {
                // Si el jugador está agachado, crea un área de luz más grande a su alrededor.
                BlockPos centerPos = user.getBlockPos();
                // Define las posiciones relativas para formar una cruz alrededor del jugador.
                BlockPos[] positions = {
                        centerPos,
                        centerPos.north(),
                        centerPos.south(),
                        centerPos.east(),
                        centerPos.west()
                };

                // Itera sobre las posiciones y coloca un bloque de luz en cada una si es reemplazable.
                for (BlockPos pos : positions) {
                    if (world.getBlockState(pos).isReplaceable()) {
                        world.setBlockState(pos, ModBlocks.TEMPORARY_LIGHT_BLOCK.getDefaultState()
                                .with(TemporaryLightBlock.BOOSTED, true));
                    }
                }
            } else {
                // Si no está agachado, lanza la entidad de luz.
                ThrowableLightEntity light = new ThrowableLightEntity(
                        tontufosmp2.entities.ModEntities.THROWABLE_LIGHT, world
                );

                light.setOwner(user);
                light.setPosition(user.getX(), user.getEyeY(), user.getZ());
                light.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);

                world.spawnEntity(light);
            }
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
