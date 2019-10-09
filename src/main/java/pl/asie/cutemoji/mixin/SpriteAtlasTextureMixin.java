package pl.asie.cutemoji.mixin;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpriteAtlasTexture.class)
public class SpriteAtlasTextureMixin {
	@Shadow
	private String pathPrefix;

	@Inject(at = @At("HEAD"), method = "getTexturePath", cancellable = true)
	private void getTexturePath(Identifier id, CallbackInfoReturnable<Identifier> info) {
		if ("cutemoji:textures/emoji".equals(pathPrefix)) {
			info.setReturnValue(id);
		}
	}
}