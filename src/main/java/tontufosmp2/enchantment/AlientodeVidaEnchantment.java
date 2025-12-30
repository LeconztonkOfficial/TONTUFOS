package tontufosmp2.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class AlientodeVidaEnchantment extends Enchantment {

    public AlientodeVidaEnchantment() {
        super(
                Rarity.RARE,
                EnchantmentTarget.ARMOR_CHEST,
                new EquipmentSlot[]{EquipmentSlot.CHEST} // Solo pechera
        );
    }

    @Override
    public int getMaxLevel() {
        return 3; // Nivel máximo configurable
    }
}

