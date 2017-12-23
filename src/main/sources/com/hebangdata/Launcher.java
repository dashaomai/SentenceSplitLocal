package com.hebangdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Launcher {
	private static final Logger log = LoggerFactory.getLogger("Launcher");

	public static void main(String[] args) throws IOException {
		final String inputUrl = "assets/百度买房语料.txt";
		final String outputUrl = "assets/百度买房语料.sentence.txt";
		final String groupedUrl = "assets/百度买房语料.grouped.txt";

		FirstSplit.process(inputUrl, outputUrl, groupedUrl);
	}
}
