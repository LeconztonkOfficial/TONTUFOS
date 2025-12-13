package tontufosmp2.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import tontufosmp2.blocks.ModBlocks;
import tontufosmp2.items.ModItems;

public class DatagenModelProvider  extends FabricModelProvider {
    public DatagenModelProvider(FabricDataOutput output){
        super(output);
    }
    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
     itemModelGenerator.register(ModItems.AGONIAVETERANO, Models.GENERATED);
     itemModelGenerator.register(ModItems.ICONO_MALDICIONES, Models.GENERATED);
     itemModelGenerator.register(ModItems.ICONO_MAGIA, Models.GENERATED);
     itemModelGenerator.register(ModItems.CEREBROFRAGMENTO, Models.GENERATED);
     itemModelGenerator.register(ModItems.COJOINVERSO, Models.GENERATED);
     itemModelGenerator.register(ModItems.CORAZONFRAGMENTADO, Models.GENERATED);
     itemModelGenerator.register(ModItems.CRISTALDELRECUERDO, Models.GENERATED);
     itemModelGenerator.register(ModItems.DECADENCIADELCICLO, Models.GENERATED);
     itemModelGenerator.register(ModItems.ECODELDOLOR, Models.GENERATED);
     itemModelGenerator.register(ModItems.ECODELPASADO, Models.GENERATED);
     itemModelGenerator.register(ModItems.FATIGAANCESTRAL, Models.GENERATED);
     itemModelGenerator.register(ModItems.GARRADIMENCIONAL, Models.GENERATED);
     itemModelGenerator.register(ModItems.HAMBREDELANTIGUO, Models.GENERATED);
     itemModelGenerator.register(ModItems.HAMBRESOMBRA, Models.GENERATED);
     itemModelGenerator.register(ModItems.HUEVOPURIFICADOR, Models.GENERATED);
     itemModelGenerator.register(ModItems.LENGUAPLOMO, Models.GENERATED);
     itemModelGenerator.register(ModItems.LIGAMENTODELALMA, Models.GENERATED);
     itemModelGenerator.register(ModItems.MANTODELOLVIDO, Models.GENERATED);
     itemModelGenerator.register(ModItems.MASCARADELSILENCIOSEPULCRAL, Models.GENERATED);
     itemModelGenerator.register(ModItems.MAZJGORNET, Models.GENERATED);
     itemModelGenerator.register(ModItems.OJOMALDITO, Models.GENERATED);
     itemModelGenerator.register(ModItems.ORBEPOTENCIADOR, Models.GENERATED);
     itemModelGenerator.register(ModItems.PICOINTERDIMENCIONAL, Models.GENERATED);
     itemModelGenerator.register(ModItems.PIEPESADO, Models.GENERATED);
     itemModelGenerator.register(ModItems.POCIONLUZ, Models.GENERATED);
     itemModelGenerator.register(ModItems.POLVODECONFUCION, Models.GENERATED);
     itemModelGenerator.register(ModItems.SANDIWCHDELAMUERTE, Models.GENERATED);
     itemModelGenerator.register(ModItems.SEDETERNA, Models.GENERATED);
     itemModelGenerator.register(ModItems.TALADROEXCAVADOR, Models.GENERATED);
     itemModelGenerator.register(ModItems.TALISMANPROTECTOR, Models.GENERATED);
     itemModelGenerator.register(ModItems.TIEMPOCOMPRIMIDO, Models.GENERATED);
     itemModelGenerator.register(ModItems.TORMENTONOCTURNO, Models.GENERATED);
    }
}
