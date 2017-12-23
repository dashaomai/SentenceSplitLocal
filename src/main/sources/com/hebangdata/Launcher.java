package com.hebangdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Launcher {
	private static final Logger log = LoggerFactory.getLogger("Launcher");

	public static void main(String[] args) throws IOException {
		// 先读取文件内容，并整理成为行的集合
		final String url = "assets/百度买房语料.txt";
		final List<String> result = new ArrayList<>();

		final long begin = System.currentTimeMillis();

		try (final Stream<String> stream = Files.lines(Paths.get(url), Charset.forName("UTF-8"))) {
			stream.parallel().forEach(line -> result.addAll(split(line)));
		}

		final long end = System.currentTimeMillis();

		log.info("读取：{} 耗时：{} 秒，获得：{} 行文本", url, (end - begin) / 1000L, result.size());

		// 把结果写入文件内
		final String outputUrl = "assets/百度买房语料.sentence.txt";
		final String spliter = "\n";
		final byte[] spliterBytes = spliter.getBytes();

		final long outputBegin = System.currentTimeMillis();

		final FileOutputStream outputStream = new FileOutputStream(outputUrl);
		final FileChannel outputChannel = outputStream.getChannel();

		final ByteBuffer outputByteBuffer = ByteBuffer.allocate(2 << 15);
		for (final String sentence :
				result) {
			if (null == sentence) continue;

			final byte[] finalOutput = sentence.getBytes();
			outputByteBuffer.put(finalOutput);
			outputByteBuffer.put(spliterBytes);
			outputByteBuffer.flip();

			outputChannel.write(outputByteBuffer);
			outputByteBuffer.clear();
		}

		outputChannel.close();
		outputStream.close();

		final long outputEnd = System.currentTimeMillis();

		log.info("写入：{} 耗时：{} 秒", outputUrl, (outputEnd - outputBegin) / 1000L);
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
		final Character.UnicodeScript ub = Character.UnicodeScript.of(chr);

		if (Character.UnicodeScript.HAN == ub || Character.UnicodeScript.LATIN == ub) // 判断是不是汉字
			return false;
		else if (0 <= chr.compareTo('0') && 0 >= chr.compareTo('9')) // 判断是不是数字
			return false;
		else if (chr.equals(' ') || chr.equals('、')) // 判断是不是空格及特别的符号
			return false;
		else
			return true;
	}
}
