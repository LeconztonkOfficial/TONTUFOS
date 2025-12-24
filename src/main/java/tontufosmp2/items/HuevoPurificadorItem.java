package tontufosmp2.items;


import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Iterator;


public class HuevoPurificadorItem extends Item {

    public HuevoPurificadorItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (!world.isClient) {

            Iterator<StatusEffectInstance> iterator =
                    user.getStatusEffects().iterator();

            while (iterator.hasNext()) {
                StatusEffectInstance effect = iterator.next();

                // ❌ Quitar SOLO efectos negativos
                if (!effect.getEffectType().isBeneficial()) {
                    user.removeStatusEffect(effect.getEffectType());
                }
            }
        }

        // 🥚 Consumir el Huevo Purificador
        user.getStackInHand(hand).decrement(1);

        return TypedActionResult.success(user.getStackInHand(hand), world.isClient);
    }
}
