package tontufosmp2.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import tontufosmp2.Tontufosmp2;


public class ModItems {
    public static final Item GARRADIMENCIONAL = registrerItem("garradimencional", new Item(new Item.Settings()));

    private static Item registrerItem (String itemId, Item item){
       return Registry.register(Registries.ITEM, new Identifier(Tontufosmp2.MOD_ID, itemId), item);
    }
    public static void registerItems(){
    Tontufosmp2.LOGGER.info("Registrando items...");
    }
}
