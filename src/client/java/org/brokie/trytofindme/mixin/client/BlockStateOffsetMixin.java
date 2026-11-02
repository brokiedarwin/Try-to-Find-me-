package org.brokie.trytofindme.mixin.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Zeroes the position-based offset for every block state that uses one. */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateOffsetMixin {
    @Inject(method = "getOffset", at = @At("RETURN"), cancellable = true)
    private void trytofindme$zeroBlockOffset(BlockPos pos, CallbackInfoReturnable<Vec3> cir) {
        Vec3 offset = cir.getReturnValue();
        cir.setReturnValue(new Vec3(offset.x * 0.0, offset.y * 0.0, offset.z * 0.0));
    }
}
