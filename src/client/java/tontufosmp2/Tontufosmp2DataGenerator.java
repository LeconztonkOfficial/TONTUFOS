package tontufosmp2;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import tontufosmp2.datagen.DatagenModelProvider;
import tontufosmp2.datagen.DatagenRecipeProvider;

public class Tontufosmp2DataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(DatagenModelProvider::new);
        pack.addProvider(DatagenRecipeProvider::new);
	}
}
