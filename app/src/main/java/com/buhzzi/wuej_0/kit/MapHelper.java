package com.buhzzi.wuej_0.kit;

import java.util.HashMap;
import java.util.Map;

public class MapHelper {
	public static <keyT, valT> Map<keyT, valT> of(Object... entries) {
		if ((entries.length & 1) != 0) {
			throw new InternalError("Length is odd");
		}
		final Map<keyT, valT> ret = new HashMap<>(entries.length >>> 1);
		for (int i = 0; i < entries.length; i += 2) {
			ret.put((keyT) entries[i], (valT) entries[i + 1]);
		}
		return ret;
	}
}
