package com.cyc;

import java.util.ArrayList;
import java.util.List;

public class Searcher {
	
	private int size = 38;

	public void search(String text) {
		if (null == text || "" == text)
			return;
		int length = text.length();
		int arraySize = length % size == 0 ? length/size : length/size + 1;
		List<String> strings = new ArrayList<>(arraySize);
		for(int i = 0;i < length; i+=size) {
			String temp = text;
		}
	}
}
