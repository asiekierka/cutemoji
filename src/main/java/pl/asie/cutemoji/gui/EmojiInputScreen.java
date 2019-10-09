package pl.asie.cutemoji.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import pl.asie.cutemoji.CutemojiClient;
import pl.asie.cutemoji.CutemojiElement;
import pl.asie.cutemoji.CutemojiEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EmojiInputScreen extends Screen {
	private final Screen last;
	private final Predicate<CutemojiEntry> filter;
	private final Consumer<String> finishedEmojiConsumer;
	private final List<CutemojiEntry> entries = new ArrayList<>();
	private int scrollOffset = 0;

	private final int borderSize = 1;
	private final int marginSize = 1;
	private final int emojiWindowSize = 8;
	private final int cellSize = (emojiWindowSize + borderSize * 2 + marginSize * 2);
	private final int borderCellSize = (emojiWindowSize + borderSize * 2);
	private final int scrollWidth = 2;

	private int xOffset, yOffset;

	public EmojiInputScreen(Screen last, Consumer<String> finishedEmojiConsumer, Predicate<CutemojiEntry> filter) {
		super(new StringTextComponent("Cutemoji input screen"));
		this.last = last;
		this.finishedEmojiConsumer = finishedEmojiConsumer;
		this.filter = filter;
	}

	@Override
	protected void init() {
		super.init();

		entries.clear();
		CutemojiClient.emojiMap.values().stream().filter(filter).forEach(entries::add);

		xOffset = 8;
		yOffset = this.height - 20 - 4 * cellSize;
		scrollOffset = 0;
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double delta) {
		if (mx >= xOffset && my >= yOffset && mx < xOffset + 4*cellSize && my < yOffset + 4*cellSize) {
			int change = -(int)delta;
			scrollOffset += change*4;
			if (scrollOffset < 0) scrollOffset = 0;
			if (scrollOffset >= ((entries.size() - 13) & (~3))) scrollOffset = (entries.size() - 13) & (~3);
			return true;
		}

		return super.mouseScrolled(mx, my, delta);
	}

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		int pos = scrollOffset;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++, pos++) {
				if (pos >= entries.size()) break;

				int renderX = xOffset + cellSize * x + marginSize;
				int renderY = yOffset + cellSize * y + marginSize;

				if (mx >= renderX && my >= renderY && mx < renderX + borderCellSize && my < renderY + borderCellSize) {
					CutemojiEntry entry = entries.get(pos);
					finishedEmojiConsumer.accept(CutemojiElement.PREFIX + "{e," + entry.getId() + "}");
					onClose();
					return true;
				}
			}
		}

		return super.mouseClicked(mx, my, button);
	}

	@Override
	public void render(int mx, int my, float delta) {
		last.render(mx, my, delta);

		if (CutemojiClient.emojiAtlas == null) {
			return;
		}

		GlStateManager.enableTexture();
		MinecraftClient.getInstance().getTextureManager().bindTexture(CutemojiClient.ATLAS_ID);

		Tessellator tess = Tessellator.getInstance();
		BufferBuilder builder = tess.getBufferBuilder();

		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		builder.begin(7, VertexFormats.POSITION_COLOR);
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				int renderX = xOffset + cellSize * x + marginSize;
				int renderY = yOffset + cellSize * y + marginSize;

				if (mx >= renderX && my >= renderY && mx < renderX + borderCellSize && my < renderY + borderCellSize) {
					builder.vertex(renderX, renderY, 0).color(1f, 1f, 1f, 0.25f).next();
					builder.vertex(renderX, renderY + borderCellSize, 0).color(1f, 1f, 1f, 0.25f).next();
					builder.vertex(renderX + borderCellSize, renderY + borderCellSize, 0).color(1f, 1f, 1f, 0.25f).next();
					builder.vertex(renderX + borderCellSize, renderY, 0).color(1f, 1f, 1f, 0.25f).next();
				} else {
					builder.vertex(renderX, renderY, 0).color(0f, 0f, 0f, 0.2f).next();
					builder.vertex(renderX, renderY + borderCellSize, 0).color(0f, 0f, 0f, 0.2f).next();
					builder.vertex(renderX + borderCellSize, renderY + borderCellSize, 0).color(0f, 0f, 0f, 0.2f).next();
					builder.vertex(renderX + borderCellSize, renderY, 0).color(0f, 0f, 0f, 0.2f).next();
				}
			}
		}

		if (entries.size() > 4 * 4) {
			int scrollHeight = Math.max(2, (4 * cellSize) / ((((entries.size() - (4 * 4)) + 3) / 4) + 1));
			int scrollPos = scrollOffset * (4 * cellSize - scrollHeight) / entries.size();

			builder.vertex(xOffset + 4 * cellSize, yOffset, 0).color(0f, 0f, 0f, 0.2f).next();
			builder.vertex(xOffset + 4 * cellSize, yOffset + 4 * cellSize, 0).color(0f, 0f, 0f, 0.2f).next();
			builder.vertex(xOffset + 4 * cellSize + scrollWidth, yOffset + 4 * cellSize, 0).color(0f, 0f, 0f, 0.2f).next();
			builder.vertex(xOffset + 4 * cellSize + scrollWidth, yOffset, 0).color(0f, 0f, 0f, 0.2f).next();

			builder.vertex(xOffset + 4 * cellSize, yOffset + scrollPos, 0).color(0.85f, 0.85f, 0.85f, 1.0f).next();
			builder.vertex(xOffset + 4 * cellSize, yOffset + scrollPos + scrollHeight, 0).color(0.85f, 0.85f, 0.85f, 1.0f).next();
			builder.vertex(xOffset + 4 * cellSize + scrollWidth, yOffset + scrollPos + scrollHeight, 0).color(0.85f, 0.85f, 0.85f, 1.0f).next();
			builder.vertex(xOffset + 4 * cellSize + scrollWidth, yOffset + scrollPos, 0).color(0.85f, 0.85f, 0.85f, 1.0f).next();
		}

		tess.draw();
		GlStateManager.enableTexture();

		builder.begin(7, VertexFormats.POSITION_UV_COLOR);
		int pos = scrollOffset;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++, pos++) {
				if (pos >= entries.size()) break;

				int renderX = xOffset + cellSize * x + marginSize + borderSize;
				int renderY = yOffset + cellSize * y + marginSize + borderSize;

				Sprite sprite = CutemojiClient.emojiAtlas.getSprite(entries.get(pos).getTexture());

				builder.vertex(renderX, renderY, 0).texture(sprite.getMinU(), sprite.getMinV()).color(1f, 1, 1, 1).next();
				builder.vertex(renderX, renderY + sprite.getHeight()/2f, 0).texture(sprite.getMinU(), sprite.getMaxV()).color(1f, 1, 1, 1).next();
				builder.vertex(renderX + sprite.getWidth()/2f, renderY + sprite.getHeight()/2f, 0).texture(sprite.getMaxU(), sprite.getMaxV()).color(1f, 1, 1, 1).next();
				builder.vertex(renderX + sprite.getWidth()/2f, renderY, 0).texture(sprite.getMaxU(), sprite.getMinV()).color(1f, 1, 1, 1).next();
			}
		}
		tess.draw();
	}

	@Override
	public void onClose() {
		super.onClose();
		this.minecraft.currentScreen = last;
	}
}
