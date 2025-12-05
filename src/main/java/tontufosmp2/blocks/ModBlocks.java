package tontufosmp2.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import tontufosmp2.Tontufosmp2;

public class ModBlocks {

    public static final Block LIGHT_MAGIC_BLOCK = register(
            "light_magic_block",
            new LightMagicBlock(
                    FabricBlockSettings.create()
                            .luminance(s -> 15)
                            .noCollision()
                            .nonOpaque()
            )
    );

    private static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(Tontufosmp2.MOD_ID, id), block);
    }

    public static void registerModBlocks() {
        System.out.println("Registering ModBlocks for " + Tontufosmp2.MOD_ID);
    }
}