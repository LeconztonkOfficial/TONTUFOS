package tontufosmp2.items;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tontufosmp2.Tontufosmp2;

public class ModItemGroups {
public static final ItemGroup MAGIA_ITEM_GROUP = registrerItemGroup("magic_group",
        FabricItemGroup.builder().displayName(Text.translatable("itemGroup.tontufosmp2.magic_group"))
                .icon(() -> new ItemStack(ModItems.ICONO_MAGIA)).entries((displayContext, entries) -> {
      entries.add(ModItems.GARRADIMENCIONAL);
      }).build()
);
    private static ItemGroup registrerItemGroup (String itemId, ItemGroup itemGroup){
        return Registry.register(Registries.ITEM_GROUP, new Identifier(Tontufosmp2.MOD_ID, itemId), itemGroup);
    }
    public static void registerItemGroups(){
        Tontufosmp2.LOGGER.info("Registrando grupo de items...");
    }
}
