package tontufosmp2.enchantment;


import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantments {

    public static Enchantment FUEGO_SOLAR;
    public static Enchantment ALIENTO_DEL_ALBA;
    public static Enchantment ALIENTO_DE_VIDA;

    public static void register() {
        FUEGO_SOLAR = Registry.register(
                Registries.ENCHANTMENT,
                new Identifier("tontufosmp2", "fuego_solar"),
                new FuegoSolarEnchantment()
        );

        ALIENTO_DEL_ALBA = Registry.register(
                Registries.ENCHANTMENT,
                new Identifier("tontufosmp2", "aliento_del_alba"),
                new AlientoDelAlbaEnchantment()
        );

        ALIENTO_DE_VIDA = Registry.register(
                Registries.ENCHANTMENT,
                new Identifier("tontufosmp2", "aliento_de_vida"),
                new AlientodeVidaEnchantment()
        );

    }
}