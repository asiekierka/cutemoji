package pl.asie.cutemoji;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

public class EmojiCutemojiElement extends CutemojiElement {
	protected EmojiCutemojiElement(String text) {
		super(Type.ELEMENT, text);
	}

	@Override
	public int getWidth() {
		CutemojiEntry entry = CutemojiClient.emojiMap.get(getText());
		if (entry == null || CutemojiClient.emojiAtlas == null) {
			return 0;
		}

		Sprite sprite = CutemojiClient.emojiAtlas.getSprite(entry.getTexture());
		if (sprite == null) {
			return 0;
		}

		return sprite.getWidth() / 2 + 2;
	}

	@Override
	public String toString() {
		return getText();
	}

	@Override
	public void draw(float x, float y, int color, boolean shadow) {
		CutemojiEntry entry = CutemojiClient.emojiMap.get(getText());
		if (entry == null || CutemojiClient.emojiAtlas == null) {
			return;
		}

		Sprite sprite = CutemojiClient.emojiAtlas.getSprite(entry.getTexture());
		if (sprite == null) {
			return;
		}

		GlStateManager.enableTexture();
		MinecraftClient.getInstance().getTextureManager().bindTexture(CutemojiClient.ATLAS_ID);

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder builder = tess.getBufferBuilder();
		builder.begin(7, VertexFormats.POSITION_UV_COLOR);
		builder.vertex(x, y, 0).texture(sprite.getMinU(), sprite.getMinV()).color(1f, 1, 1, 1).next();
		builder.vertex(x, y + sprite.getHeight()/2f, 0).texture(sprite.getMinU(), sprite.getMaxV()).color(1f, 1, 1, 1).next();
		builder.vertex(x + sprite.getWidth()/2f, y + sprite.getHeight()/2f, 0).texture(sprite.getMaxU(), sprite.getMaxV()).color(1f, 1, 1, 1).next();
		builder.vertex(x + sprite.getWidth()/2f, y, 0).texture(sprite.getMaxU(), sprite.getMinV()).color(1f, 1, 1, 1).next();
		tess.draw();
	}
}
