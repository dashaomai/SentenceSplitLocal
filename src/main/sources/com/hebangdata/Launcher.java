package com.hebangdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Launcher {
	private static final Logger log = LoggerFactory.getLogger("Launcher");

	public static void main(String[] args) throws IOException {
		// 拆开所有的文本
		firstSplit();
	}

	private static void firstSplit() throws IOException {
		final long begin = System.currentTimeMillis();

		FirstSplit.process("assets/百度买房语料");
		FirstSplit.process("assets/网易新闻语料");
		FirstSplit.process("assets/网易新闻语料20171122");
		FirstSplit.process("assets/一般词全集语料");
		FirstSplit.process("assets/一般词全集语料1");
		FirstSplit.process("assets/一般词全集语料2");
		FirstSplit.process("assets/一般词全集语料3");
		FirstSplit.process("assets/一般词全集语料4");
		FirstSplit.process("assets/一般词全集语料5");

		final long end = System.currentTimeMillis();

		log.info("全部文件拆句子处理耗时：{} 秒", (end - begin) / 1000L);
	}

	private static void combine() throws IOException {
		final long begin = System.currentTimeMillis();


		final long end = System.currentTimeMillis();

		log.info("按要求合并句子处理耗时：{} 秒", (end - begin) / 1000L);
	}
}
