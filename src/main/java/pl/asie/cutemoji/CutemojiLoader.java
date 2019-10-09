package pl.asie.cutemoji;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CutemojiLoader implements SimpleResourceReloadListener<CutemojiLoader.Data> {
	private static final Gson GSON = new GsonBuilder().create();

	static class Data {
		Map<String, CutemojiEntry> map;
		SpriteAtlasTexture.Data atlasData;
	}

	@Override
	public CompletableFuture<Data> load(ResourceManager manager, Profiler profiler, Executor executor) {
		if (CutemojiClient.emojiAtlas == null) {
			CutemojiClient.emojiAtlas = new SpriteAtlasTexture(CutemojiClient.ATLAS_ID.toString());
			MinecraftClient.getInstance().getTextureManager().registerTextureUpdateable(CutemojiClient.ATLAS_ID, CutemojiClient.emojiAtlas);
		}

		return CompletableFuture.supplyAsync(() -> {
			Map<String, CutemojiEntry> map = new HashMap<>();

			for (Identifier id : manager.findResources("cutemoji", (s) -> s.endsWith(".json"))) {
				System.out.println(id);

				try (InputStream stream = manager.getResource(id).getInputStream();
				     InputStreamReader reader = new InputStreamReader(stream)) {
					Map<String, CutemojiEntry> newMap = GSON.fromJson(reader, TypeToken.getParameterized(Map.class, String.class, CutemojiEntry.class).getType());
					for (String s : newMap.keySet()) {
						if (map.containsKey(s)) {
							System.out.println("Warning: duplicate key " + s);
						} else {
							CutemojiEntry entry = newMap.get(s);
							entry.setId(s);
							map.put(s, entry);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			System.out.println("Found " + map.size() + " emoji.");

			List<Identifier> spriteIds = new ArrayList<>();
			for (CutemojiEntry e : map.values()) {
				spriteIds.add(new Identifier(e.getTexture()));
			}

			Data data = new Data();
			data.map = map;
			data.atlasData = CutemojiClient.emojiAtlas.stitch(manager, spriteIds, profiler);

			return data;
		}, executor);
	}

	@Override
	public CompletableFuture<Void> apply(Data data, ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			CutemojiClient.emojiMap.clear();
			CutemojiClient.emojiMap.putAll(data.map);
			CutemojiClient.emojiAtlas.upload(data.atlasData);
		}, executor);
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier("cutemoji:emoji");
	}
}
