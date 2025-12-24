package tontufosmp2.items;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CristalDeRecuerdo extends Item {

    public CristalDeRecuerdo(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack cristal = player.getStackInHand(hand);
        ItemStack target = player.getOffHandStack(); // Item a desencantar

        if (target.isEmpty() || !target.hasEnchantments()) {
            // No hay item encantado
            return TypedActionResult.pass(cristal);
        }

        int xpCost = 10;

        if (!world.isClient) {
            if (player.experienceLevel < xpCost) {
                return TypedActionResult.fail(cristal);
            }

            // Quitar encantamientos
            target.removeSubNbt("Enchantments");
            target.removeSubNbt("StoredEnchantments");

            // Restar niveles de experiencia
            player.addExperienceLevels(-xpCost);

            // Consumir el cristal
            cristal.decrement(1);

            // Sonido de yunque
            world.syncWorldEvent(1030, player.getBlockPos(), 0);
        }

        return TypedActionResult.success(cristal);
    }
}