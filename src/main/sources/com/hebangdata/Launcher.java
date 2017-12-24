package com.hebangdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Launcher {
	private static final Logger log = LoggerFactory.getLogger("Launcher");

	public static void main(String[] args) throws IOException {
		FirstSplit.process("assets/百度买房语料.txt", "assets/百度买房语料.sentence.txt", "assets/百度买房语料.grouped.txt");
		FirstSplit.process("assets/网易新闻语料.txt", "assets/网易新闻语料.sentence.txt", "assets/网易新闻语料.grouped.txt");
		FirstSplit.process("assets/网易新闻语料20171122.txt", "assets/网易新闻语料20171122.sentence.txt", "assets/网易新闻语料20171122.grouped.txt");
		FirstSplit.process("assets/一般词全集语料.txt", "assets/一般词全集语料.sentence.txt", "assets/一般词全集语料.grouped.txt");
		FirstSplit.process("assets/一般词全集语料1.txt", "assets/一般词全集语料1.sentence.txt", "assets/一般词全集语料1.grouped.txt");
		FirstSplit.process("assets/一般词全集语料2.txt", "assets/一般词全集语料2.sentence.txt", "assets/一般词全集语料2.grouped.txt");
		FirstSplit.process("assets/一般词全集语料3.txt", "assets/一般词全集语料3.sentence.txt", "assets/一般词全集语料3.grouped.txt");
		FirstSplit.process("assets/一般词全集语料4.txt", "assets/一般词全集语料4.sentence.txt", "assets/一般词全集语料4.grouped.txt");
		FirstSplit.process("assets/一般词全集语料5.txt", "assets/一般词全集语料5.sentence.txt", "assets/一般词全集语料5.grouped.txt");
	}
}
