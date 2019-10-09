package pl.asie.cutemoji;

public class CutemojiElement {
	public static final String PREFIX = "uz";

	private final Type type;
	private final String text;

	protected CutemojiElement(Type type, String text) {
		this.type = type;
		this.text = text;
	}

	public static CutemojiElement create(Type type, String text) {
		if (type == Type.STRING) {
			return new CutemojiElement(type, text);
		}

		if (text.startsWith("e,")) {
			return new EmojiCutemojiElement(text.substring(2));
		}

		return new CutemojiElement(Type.STRING, "?");
	}

	public void draw(float x, float y, int color, boolean shadow) {

	}

	public int getWidth() {
		return 16;
	}

	public Type getType() {
		return type;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return getText();
	}

	public enum Type {
		STRING,
		ELEMENT
	}
}
