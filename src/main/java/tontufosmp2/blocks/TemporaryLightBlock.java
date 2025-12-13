package tontufosmp2.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class TemporaryLightBlock extends Block {

    // Define una nueva propiedad de estado para controlar cuando tiene particulas
    public static final BooleanProperty HAS_PARTICLES = BooleanProperty.of("has_particles");

    public TemporaryLightBlock(Settings settings) {
        super(settings);
        // Establece el estado por defecto HAS_PARTICLES 'true'.
        setDefaultState(getStateManager().getDefaultState().with(HAS_PARTICLES, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        // Añade propiedad de HAS_PARTICLES
        builder.add(HAS_PARTICLES);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE; // El bloque sigue siendo invisible.
    }

    /**
     * Se llama cuando el bloque se coloca en el mundo.
     * Programa la primera fase de la vida del bloque (con partículas).
     * @param state El estado actual del bloque.
     * @param world El mundo en el que se colocó el bloque.
     * @param pos La posición donde se colocó el bloque.
     * @param oldState El estado anterior del bloque.
     * @param isMoving Si el bloque se está moviendo.
     */
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onBlockAdded(state, world, pos, oldState, isMoving);
        // 80 ticks (4 segundos).
        // termina las particulas
        world.scheduleBlockTick(pos, this, 80);
        //TODO: quizas agregar el tiempo que tarda en mostrar particulas en el archivo config del server si es posible
    }

    /**
     * Se ejecuta cuando ocurre un tick programado.
     * Controla el ciclo de vida del bloque, pasando de la fase de partículas a la fase sin partículas,
     * y finalmente a su eliminación.
     * @param state El estado actual del bloque.
     * @param world El mundo (debe ser un ServerWorld para los ticks).
     * @param pos La posición del bloque.
     * @param random Una instancia de Random.
     */
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(HAS_PARTICLES)) {
            // Si el bloque está en estado HAS_PARTICLES, cambia su estado a 'sin partículas'

            world.setBlockState(pos, state.with(HAS_PARTICLES, false));
            // 120 ticks (6 segundos)
            world.scheduleBlockTick(pos, this, 120);
        } else {
            // Si el bloque ya no tiene partículas, se elimina del mundo.
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }


     // Genera partículas en el lado del cliente si el bloque está en la fase de partículas.

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(HAS_PARTICLES)) {
            // Genera partículas blancas en una ubicación aleatoria dentro del bloque.
            // Esto solo pone particulas en el bloque que cae , pero quizas estaria bueno hacerlo que aparezcan alrededor
            // aunque esto podria afectar el rendimiento ?¿
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            world.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
        }
    }
}
