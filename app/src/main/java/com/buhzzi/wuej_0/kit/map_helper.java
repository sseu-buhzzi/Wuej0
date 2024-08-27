package com.buhzzi.wuej_0.kit;

import java.util.HashMap;
import java.util.Map;

public class map_helper {
	public static <key_t, val_t> Map<key_t, val_t> of(Object... entries) {
		if ((entries.length & 1) != 0) {
			throw new InternalError("Length is odd");
		}
		final Map<key_t, val_t> ret = new HashMap<>(entries.length >>> 1);
		for (int i = 0; i < entries.length; i += 2) {
			ret.put((key_t) entries[i], (val_t) entries[i + 1]);
		}
		return ret;
	}
}
