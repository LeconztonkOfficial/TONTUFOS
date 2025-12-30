package tontufosmp2.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class AlientoDelAlbaEnchantment extends Enchantment {

    public AlientoDelAlbaEnchantment() {
        super(
                Rarity.RARE,
                EnchantmentTarget.ARMOR_CHEST,
                new EquipmentSlot[]{EquipmentSlot.CHEST}
        );
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
