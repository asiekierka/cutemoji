package pl.asie.cutemoji.mixin;

import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ingame.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.TextComponent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.asie.cutemoji.gui.EmojiInputScreen;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen {
	protected ChatScreenMixin(TextComponent textComponent_1) {
		super(textComponent_1);
	}

	@Shadow
	protected TextFieldWidget chatField;

	@Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
	private void keyPressedHookPre(int int_1, int int_2, int int_3, CallbackInfoReturnable<Boolean> info) {
		if (int_1 == GLFW.GLFW_KEY_E && hasShiftDown()) {
			minecraft.openScreen(new EmojiInputScreen(
				this,
					(s) -> chatField.addText(s),
					(emoji) -> true
			));
			info.setReturnValue(true);
		}
	}
}