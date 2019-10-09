package pl.asie.cutemoji;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CutemojiClient implements ClientModInitializer {
	public static final Identifier ATLAS_ID = new Identifier("cutemoji:textures/emoji");
	public static final Map<String, CutemojiEntry> emojiMap = new HashMap<>();
	public static SpriteAtlasTexture emojiAtlas;

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.ASSETS).registerReloadListener(new CutemojiLoader());
	}
}
