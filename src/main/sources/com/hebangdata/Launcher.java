package com.hebangdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Launcher {
	private static final Logger log = LoggerFactory.getLogger("Launcher");

	public static void main(String[] args) throws IOException {
		final String url = "assets/百度买房语料.txt";
		final List<String> result = new ArrayList<>();

		final double begin = System.nanoTime();

		try (final Stream<String> stream = Files.lines(Paths.get(url), Charset.forName("UTF-8"))) {
			stream.parallel().forEach(line -> result.addAll(split(line)));
		}

		final double end = System.nanoTime();
		final double passed = end - begin;

		log.info("读取：{} 耗时：{} 纳秒，获得：{} 行文本", url, passed, result.size());
	}

	private static List<String> split(final String line) {
		if (null == line) return null;

		// 语句拆分
		final List<String> lines = new ArrayList<>();

		// 循环每一个字符，拆出每一个完整的句子
		final StringBuilder builder = new StringBuilder();

		for (final Character chr : line.toCharArray()) {
			if (isSpliter(chr)) {
				if (0 < builder.length()) {
					lines.add(builder.toString());
					builder.delete(0, builder.length());
				}
			} else {
				builder.append(chr);
			}
		}

		return lines;
	}
	/**
	 * 判断一个字符是不是非句内字符
	 * @param chr
	 * @return
	 */
	private static boolean isSpliter(final Character chr) {
		// 判断是不是汉字
		final Character.UnicodeScript ub = Character.UnicodeScript.of(chr);

		if (Character.UnicodeScript.HAN == ub || Character.UnicodeScript.LATIN == ub)
			return false;
		else
			return 0 < chr.compareTo('0') || 0 > chr.compareTo('9');
	}
}
