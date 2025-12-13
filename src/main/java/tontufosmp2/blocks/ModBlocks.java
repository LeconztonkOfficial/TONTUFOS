package tontufosmp2.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import tontufosmp2.Tontufosmp2;

public class ModBlocks {

    /**
     * Bloque de luz temporal e invisible.
     * - Emite un nivel de luz de 15 (el máximo).
     * - No tiene colisiones, por lo que los jugadores y entidades pueden atravesarlo.
     * - Es completamente invisible y no obstruye la visión.
     * - Se destruye a sí mismo después de un tiempo (ver TemporaryLightBlock.java).
     */
    public static final Block TEMPORARY_LIGHT_BLOCK = register(
            "temporary_light_block",
            new TemporaryLightBlock(
                    FabricBlockSettings.copyOf(Blocks.AIR) // Copia propiedades del aire (invisible, sin colisión)
                            .luminance(s -> 15) // Pero le añade luz
                            .nonOpaque() // Asegura que no renderice caras
            )
    );

    private static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(Tontufosmp2.MOD_ID, id), block);
    }

    public static void registerModBlocks() {
        // Este método se llama durante la inicialización del mod para asegurar que los bloques se registren.
        Tontufosmp2.LOGGER.info("Registering ModBlocks for " + Tontufosmp2.MOD_ID);
    }
}
