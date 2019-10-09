package pl.asie.cutemoji;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class CutemojiTextRenderer {
	private CutemojiTextRenderer() {

	}

	public static List<CutemojiElement> split(String s, boolean returnNullIfNormal) {
		List<CutemojiElement> list = new ArrayList<>();
		String origString = s;

		int i;
		while ((i = s.indexOf(CutemojiElement.PREFIX + "{")) >= 0) {
			if (i > 0) {
				list.add(CutemojiElement.create(CutemojiElement.Type.STRING, s.substring(0, i)));
				s = s.substring(i);
			}

			int len = s.indexOf("}");
			if (len < 0) {
				// bail completely
				return returnNullIfNormal ? null : Collections.singletonList(CutemojiElement.create(CutemojiElement.Type.STRING, origString));
			}

			list.add(CutemojiElement.create(CutemojiElement.Type.ELEMENT, s.substring(3, len)));
			s = s.substring(len + 1);
		}

		if (s.length() > 0) {
			if (returnNullIfNormal && list.isEmpty()) {
				return null;
			}

			list.add(CutemojiElement.create(CutemojiElement.Type.STRING, s));
		}

		return list;
	}
}
