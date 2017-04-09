package com.cyc;

import java.util.*;

public class Search {

	private int size = 38;

	public List<List<ResultData>> search(String text) {
		List<List<ResultData>> pages = null;
		try {
			if (null == text || "" == text)
				return null;
			int length = text.length();
			int arraySize = length % size == 0 ? length / size : length / size + 1;
			List<String> strings = new ArrayList<>(arraySize);
			for (int i = 0; i < length; i += size) {
				int end = (i + 38) > length ? length : (i + 38);
				strings.add(text.substring(i, end));
			}
			pages = new ArrayList<>();
			for (int i = 0;i < 10;i++) {
				Random random = new Random();
				int rand = random.nextInt(arraySize);
				Searcher searcher = new JSoupBaiduSearcher();
				SearchResult searchResult = searcher.search(strings.get(rand), 1);
				List<ResultData> webpages = searchResult.getDatas();
				if (null != webpages && webpages.size() > 0) {
					pages.add(webpages);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return pages;
	}
}
