package tontufosmp2.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
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
    }
}
