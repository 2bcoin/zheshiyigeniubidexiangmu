package com.github.misterchangray.libs.zbapi;

import java.util.*;
import java.util.Map.Entry;

public class MapSort {

	public static void main(String[] args) {

		Map<String, String> map = new HashMap<String, String>();

		map.put("Kb", "K");
		map.put("Wc", "W");
		map.put("Ab", "A");
		map.put("ad", "a");
		map.put("Fe", "N");
		map.put("Bf", "B");
		map.put("Cg", "C");
		map.put("Zh", "Z");

		Map<String, String> resultMap = sortMapByKey(map); // 按Key进行排序，根据首字母hashcode

		for (Entry<String, String> entry : resultMap.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}

	}

	/**
	 * 使用 Map按key首字母hashcode进行排序
	 * 
	 * @param map
	 * @return
	 */
	public static Map<String, String> sortMapByKey(Map<String, String> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());

		sortMap.putAll(map);

		return sortMap;
	}

	public static String toStringMap(Map m) {
		// 按map键首字母顺序进行排序
		m = MapSort.sortMapByKey(m);

		StringBuilder sbl = new StringBuilder();
		for (Iterator<Entry> i = m.entrySet().iterator(); i.hasNext();) {
			Entry e = i.next();
			Object o = e.getValue();
			String v = "";
			if (o == null) {
				v = "";
			} else if (o instanceof String[]) {
				String[] s = (String[]) o;
				if (s.length > 0) {
					v = s[0];
				}
			} else {
				v = o.toString();
			}
			if (!e.getKey().equals("sign") && !e.getKey().equals("reqTime") && !e.getKey().equals("tx")) {
				// try {
				// sbl.append("&").append(e.getKey()).append("=").append(URLEncoder.encode(v,
				// "utf-8"));
				// } catch (UnsupportedEncodingException e1) {
				// e1.printStackTrace();
				sbl.append("&").append(e.getKey()).append("=").append(v);
				// }
			}
		}
		String s = sbl.toString();
		if (s.length() > 0) {
			return s.substring(1);
		}
		return "";
	}
}

class MapKeyComparator implements Comparator<String> {

	public int compare(String str1, String str2) {

		return str1.compareTo(str2);
	}
}