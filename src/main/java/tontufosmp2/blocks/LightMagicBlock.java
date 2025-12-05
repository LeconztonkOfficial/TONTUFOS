package tontufosmp2.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LightMagicBlock extends Block {

    public LightMagicBlock(Settings settings) {
        super(settings.luminance(state -> 15).noCollision().nonOpaque());
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);

        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, 40); // 2 segundos
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        world.removeBlock(pos, false);
    }
}