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

    //AGRUPACION DE ITEMS MAGICOS
public static final ItemGroup MAGIA_ITEM_GROUP = registrerItemGroup("magic_group",
        FabricItemGroup.builder().displayName(Text.translatable("itemGroup.tontufosmp2.magic_group"))
                .icon(() -> new ItemStack(ModItems.ICONO_MAGIA)).entries((displayContext, entries) -> {
      entries.add(ModItems.GARRADIMENCIONAL);
      entries.add(ModItems.COJOINVERSO);
      entries.add(ModItems.CRISTALDELRECUERDO);
      entries.add(ModItems.HUEVOPURIFICADOR);
      entries.add(ModItems.MASCARADELSILENCIOSEPULCRAL);
      entries.add(ModItems.MAZJGORNET);
      entries.add(ModItems.ORBEPOTENCIADOR);
      entries.add(ModItems.PICOINTERDIMENCIONAL);
      entries.add(ModItems.POCIONLUZ);
      entries.add(ModItems.POLVODECONFUCION);
      entries.add(ModItems.SANDIWCHDELAMUERTE);
      entries.add(ModItems.TALADROEXCAVADOR);
      entries.add(ModItems.TALISMANPROTECTOR);
      entries.add(ModItems.TIEMPOCOMPRIMIDO);
      }).build()
);

        //AGRUPACION DE MALDICIONES
public static final ItemGroup MALDICIONES = registrerItemGroup("maldiciones",
                FabricItemGroup.builder().displayName(Text.translatable("itemGroup.tontufosmp2.maldiciones"))
                .icon(() -> new ItemStack(ModItems.ICONO_MALDICIONES)).entries((displayContext, entries) -> {
                    entries.add(ModItems.AGONIAVETERANO);
                    entries.add(ModItems.CEREBROFRAGMENTO);
                    entries.add(ModItems.CORAZONFRAGMENTADO);
                    entries.add(ModItems.DECADENCIADELCICLO);
                    entries.add(ModItems.ECODELDOLOR);
                    entries.add(ModItems.ECODELPASADO);
                    entries.add(ModItems.FATIGAANCESTRAL);
                    entries.add(ModItems.HAMBRESOMBRA);
                    entries.add(ModItems.LENGUAPLOMO);
                    entries.add(ModItems.LIGAMENTODELALMA);
                    entries.add(ModItems.MANTODELOLVIDO);
                    entries.add(ModItems.OJOMALDITO);
                    entries.add(ModItems.PIEPESADO);
                    entries.add(ModItems.SEDETERNA);
                    entries.add(ModItems.TORMENTONOCTURNO);
                }).build()
);

    private static ItemGroup registrerItemGroup (String itemId, ItemGroup itemGroup){
        return Registry.register(Registries.ITEM_GROUP, new Identifier(Tontufosmp2.MOD_ID, itemId), itemGroup);
    }
    public static void registerItemGroups(){
        Tontufosmp2.LOGGER.info("Registrando grupo de items...");
    }
}
