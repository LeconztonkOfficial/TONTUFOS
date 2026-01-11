package tontufosmp2.enchantment;


import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantments {

    public static Enchantment FUEGO_SOLAR;
    public static Enchantment ALIENTO_DEL_ALBA;
    public static Enchantment ALIENTO_DE_VIDA;
    public static Enchantment GRAN_FORJA;
    public static Enchantment MEDIA_NOCHE;
    public static Enchantment OJO_CAZADOR;
    public static Enchantment PULSO_VENENOSO;
    public static Enchantment PULSO_VITAL;
    public static Enchantment SALTO_VITAL;

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

        GRAN_FORJA = Registry.register(
                Registries.ENCHANTMENT,
                new Identifier("tontufosmp2", "gran_forja"),
                new GranForjaEnchantment()
        );

        MEDIA_NOCHE=Registry.register(
                Registries.ENCHANTMENT,
                new Identifier("tontufosmp2", "media_noche"),
                new MediaNocheEnchantment()
        );

        OJO_CAZADOR=Registry.register(
                Registries.ENCHANTMENT,
                new Identifier("tontufosmp2", "ojo_cazador"),
                new MediaNocheEnchantment()
        );

        PULSO_VENENOSO=Registry.register(
                Registries.ENCHANTMENT,
                new Identifier("tontufosmp2", "pulso_venenoso"),
                new MediaNocheEnchantment()
        );

        PULSO_VITAL=Registry.register(
                Registries.ENCHANTMENT,
                new Identifier("tontufosmp2", "pulso_vital"),
                new MediaNocheEnchantment()
        );

        SALTO_VITAL = Registry.register(
                Registries.ENCHANTMENT,
                new Identifier("tontufosmp2", "salto_vital"),
                new MediaNocheEnchantment()
        );



    }
}