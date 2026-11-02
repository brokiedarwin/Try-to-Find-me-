package org.brokie.trytofindme.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Anchors cloud rendering to the player while leaving vanilla cloud animation intact. */
@Mixin(CloudRenderer.class)
public abstract class CloudRendererMixin {
    @ModifyVariable(
            method = "prepare(ILnet/minecraft/client/CloudStatus;FILnet/minecraft/world/phys/Vec3;JF)V",
            at = @At("HEAD"),
            argsOnly = true,
            index = 5
    )
    private Vec3 trytofindme$anchorCloudsToPlayer(Vec3 cameraPosition) {
        return new Vec3(0,cameraPosition.y,0);
    }
}
