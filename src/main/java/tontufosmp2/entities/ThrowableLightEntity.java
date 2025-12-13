package tontufosmp2.entities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tontufosmp2.blocks.ModBlocks;
import tontufosmp2.items.ModItems;

public class ThrowableLightEntity extends ThrownItemEntity {

    public ThrowableLightEntity(EntityType<? extends ThrowableLightEntity> type, World world) {
        super(type, world);
    }

    public ThrowableLightEntity(World world, PlayerEntity owner) {
        super(ModEntities.THROWABLE_LIGHT, owner, world);
    }

    /*TODO : ARREGLAR EL COMO REACCIONA SI ES QUE GOLPEA EL AGUA (CUANDO LO TIRAS AL AGUA)
             AGREGARLE ALGUN EFECTO SI ES QUE GOLPEA A ALGUN ENEMIGO O JUGADOR????¿ COSA A DISCUTIR
    * */

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (!this.getWorld().isClient) {
            BlockPos placementPos;

            // Comprueba si es que impacto contra un bloque
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                BlockPos hitBlockPos = blockHitResult.getBlockPos();
                BlockState hitBlockState = this.getWorld().getBlockState(hitBlockPos);

                // Coloca el bloque de luz en el espacio de aire de al lado del bloque que golpea
                // Esto hace que no remplacemos el bloque , pero no lo probe mucho
                placementPos = hitBlockPos.offset(blockHitResult.getSide());

            } else {
                // Si golpea una entidad o se rompe en el aire, se usa la posición de la pocion al romperse.
                placementPos = this.getBlockPos();
            }

            // Comprueba si el lugar donde se va a colocar el bloque de luz es reemplazable
            if (this.getWorld().getBlockState(placementPos).isReplaceable()) {
                // Coloca el bloque de luz temporal.
                this.getWorld().setBlockState(placementPos, ModBlocks.TEMPORARY_LIGHT_BLOCK.getDefaultState());
            }

            // Elimina la entidad del proyectil.
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.POCIONLUZ;
    }
}
