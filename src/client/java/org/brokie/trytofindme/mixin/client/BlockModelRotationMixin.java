package org.brokie.trytofindme.mixin.client;

import com.mojang.math.OctahedralGroup;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Removes model rotations except while resolving Glow Lichen. */
@Mixin(BlockModelRotation.class)
public abstract class BlockModelRotationMixin {
    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private static void trytofindme$forceIdentity(OctahedralGroup orientation,
                                                   CallbackInfoReturnable<BlockModelRotation> cir) {
        // Allow the identity entry to initialize itself during BlockModelRotation class initialization.
        if (orientation != OctahedralGroup.IDENTITY) {
            cir.setReturnValue(BlockModelRotation.IDENTITY);
        }
    }
}
