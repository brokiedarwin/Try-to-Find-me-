package org.brokie.trytofindme.mixin.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.FormattedCharSequence;
import org.brokie.trytofindme.client.CoordinateTextFilter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Hides coordinate-like numbers from rendered GUI text. */
@Mixin(GuiGraphicsExtractor.class)
public abstract class CoordinateTextMixin {
    @ModifyVariable(
            method = "text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;IIIZ)V",
            at = @At("HEAD"),
            argsOnly = true,
            index = 2
    )
    private FormattedCharSequence trytofindme$hidePlayerCoordinates(FormattedCharSequence text) {
        return CoordinateTextFilter.hideCoordinates(text);
    }
}
