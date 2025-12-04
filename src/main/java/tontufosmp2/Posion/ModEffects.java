package tontufosmp2.Posion;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import tontufosmp2.Tontufosmp2;

public class ModEffects {

    public static StatusEffect CONFUSION;

    public static void registerEffects() {
        CONFUSION = Registry.register(
                Registries.STATUS_EFFECT,
                new Identifier(Tontufosmp2.MOD_ID, "confusion"),
                new ConfusionEffect()
        );

        System.out.println("Registering Mod Effects for " + Tontufosmp2.MOD_ID);
    }
}
