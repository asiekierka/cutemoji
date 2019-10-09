package pl.asie.cutemoji.mixin;

import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.asie.cutemoji.CutemojiElement;
import pl.asie.cutemoji.CutemojiTextRenderer;

import java.util.List;

@Mixin(TextRenderer.class)
public abstract class TextRendererMixin {
	@Shadow
	protected abstract int draw(String text, float x, float y, int color, boolean shadow);
	@Shadow
	protected abstract int getStringWidth(String text);

	@Inject(at = @At("HEAD"), method = "draw(Ljava/lang/String;FFIZ)I", cancellable = true)
	private void drawPreHook(String text, float x, float y, int color, boolean shadow, CallbackInfoReturnable<Integer> info) {
		List<CutemojiElement> list = CutemojiTextRenderer.split(text, true);
		if (list != null) {
			int xPos = 0;
			for (CutemojiElement element : list) {
				switch (element.getType()) {
					case STRING:
						xPos = draw(element.getText(), x + xPos, y, color, shadow);
						break;
					case ELEMENT:
						element.draw(x + xPos, y, color, shadow);
						xPos += element.getWidth();
						break;
				}
			}

			info.setReturnValue(xPos);
		}
	}

	@Inject(at = @At("HEAD"), method = "getStringWidth(Ljava/lang/String;)I", cancellable = true)
	private void getStringWidthPreHook(String text, CallbackInfoReturnable<Integer> info) {
		List<CutemojiElement> list = CutemojiTextRenderer.split(text, true);
		if (list != null) {
			int xPos = 0;
			for (CutemojiElement element : list) {
				switch (element.getType()) {
					case STRING:
						xPos += getStringWidth(element.getText());
						break;
					case ELEMENT:
						xPos += element.getWidth();
						break;
				}
			}

			info.setReturnValue(xPos);
		}
	}
}
