package tontufosmp2.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class GranForjaEnchantment extends Enchantment {

    public GranForjaEnchantment() {
        super(
                Rarity.UNCOMMON,
                EnchantmentTarget.BREAKABLE, // Se aplica a cualquier ítem “rompible”
                new EquipmentSlot[]{EquipmentSlot.OFFHAND} // Solo escudo
        );
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}


