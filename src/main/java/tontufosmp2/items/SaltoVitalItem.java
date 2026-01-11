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

public class SaltoVitalItem extends Item {

    public SaltoVitalItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (!world.isClient) {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);

            EnchantedBookItem.addEnchantment(
                    book,
                    new EnchantmentLevelEntry(
                            ModEnchantments.SALTO_VITAL, 1
                    )
            );

            if (!user.getInventory().insertStack(book)) {
                user.dropItem(book, false);
            }

            user.getStackInHand(hand).decrement(1);
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}