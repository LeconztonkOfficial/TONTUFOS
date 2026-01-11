package tontufosmp2.items;

import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import tontufosmp2.enchantment.ModEnchantments;

public class MediaNocheItem extends Item {

    public MediaNocheItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (!world.isClient) {
            // Creamos un libro encantado
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);

            // Le agregamos el encantamiento Aliento de Vida nivel 1
            EnchantedBookItem.addEnchantment(
                    book,
                    new EnchantmentLevelEntry(
                            ModEnchantments.MEDIA_NOCHE, 1
                    )
            );

            // Intentamos agregar al inventario del jugador, si no, lo soltamos
            if (!user.getInventory().insertStack(book)) {
                user.dropItem(book, false);
            }

            // Reducimos el item usado en la mano
            user.getStackInHand(hand).decrement(1);
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
