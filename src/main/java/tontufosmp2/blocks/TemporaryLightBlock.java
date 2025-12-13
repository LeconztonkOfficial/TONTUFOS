package tontufosmp2.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class TemporaryLightBlock extends Block {

    public TemporaryLightBlock(Settings settings) {
        super(settings);
    }




    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE; //Se pone que el bloque sea "INVISIBLE" para que no renderice la textura
    }

    /**
     * Se llama cuando el bloque se coloca en el mundo.
     * Se le agregan cosas para que se ejecuten  por cada tick que pase
     * @param state El estado actual del bloque.
     * @param world El mundo en el que se colocó el bloque.
     * @param pos La posición donde se colocó el bloque.
     * @param oldState El estado que había antes.
     * @param isMoving Si el bloque se está moviendo.
     */
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onBlockAdded(state, world, pos, oldState, isMoving);
        // Programa una actualización para este bloque dentro de 200 ticks (10 segundos)
        world.scheduleBlockTick(pos, this, 200); //el tiempo que tarda en desaparecer el efecto de luz
                                                                // cosa para agregar al archivo de config del mod quizas?
    }

    /**
     * Se ejecuta cuando el tick programado ocurre.
     * Elimina el bloque y lo cambia por uno de aire
     * siento que esto puede dar problemas si es que se tira en un objeto que no ocupe todo el bloque
     * @param state El estado actual del bloque.
     * @param world El mundo (debe ser un ServerWorld para los ticks).
     * @param pos La posición del bloque.
     * @param random Una instancia de Random.
     */
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
    }
}
