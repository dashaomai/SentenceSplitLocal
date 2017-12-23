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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 进行第一次初分的功能类，先读出所有文本，在内存中处理完毕再写入磁盘。
 *
 * 问题：内存占用太大
 */
public class FirstSplit {
	private static final Logger log = LoggerFactory.getLogger("FirstSplit");

	public static void process(final String inputUrl, final String outputUrl, final String groupedUrl) throws IOException {

		// 先读取文件内容，并整理成为行的集合

		final long begin = System.currentTimeMillis();

		final List<String> orderedSentences = new ArrayList<>();
		final Set<String> groupedSentences = new HashSet<>();

		final Stream<String> stream = Files.lines(Paths.get(inputUrl), Charset.forName("UTF-8"));

		stream.parallel().forEach(line -> {
			final List<String> lines = Utils.split(line);

			orderedSentences.addAll(lines);
			groupedSentences.addAll(lines);
		});

		stream.close();

		final long end = System.currentTimeMillis();

		log.info("读取：{} 耗时：{} 秒，获得：{} 行文本", inputUrl, (end - begin) / 1000L, groupedSentences.size());

		final ByteBuffer outputByteBuffer = ByteBuffer.allocate(2 << 15);

		// 把未去重的结果写入文件内
		final long outputBegin = System.currentTimeMillis();

		final FileOutputStream outputStream = new FileOutputStream(groupedUrl);
		final FileChannel outputChannel = outputStream.getChannel();

		outputByteBuffer.clear();

		orderedSentences.parallelStream().forEachOrdered((String sentence) -> {
			if (null == sentence || 0 == sentence.length()) return;

			outputByteBuffer.put(sentence.getBytes());
			outputByteBuffer.put(Utils.SPLITER_BYTES);
			outputByteBuffer.flip();

			try {
				outputChannel.write(outputByteBuffer);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				outputByteBuffer.clear();
			}

		});

		outputChannel.close();
		outputStream.close();

		final long outputEnd = System.currentTimeMillis();

		log.info("写入有序文件：{}，{} 行，耗时：{} 秒", outputUrl, orderedSentences.size(), (outputEnd - outputBegin) / 1000L);

		orderedSentences.clear();

		// 把去重后的结果写入文件内
		final long groupedBegin = System.currentTimeMillis();

		final FileOutputStream groupedStream = new FileOutputStream(groupedUrl);
		final FileChannel groupedChannel = groupedStream.getChannel();

		outputByteBuffer.clear();

		for (final String sentence : groupedSentences) {
			if (null == sentence || 0 == sentence.length()) continue;

			outputByteBuffer.put(sentence.getBytes());
			outputByteBuffer.put(Utils.SPLITER_BYTES);
			outputByteBuffer.flip();

			groupedChannel.write(outputByteBuffer);
			outputByteBuffer.clear();
		}

		groupedChannel.close();
		groupedStream.close();

		final long groupedEnd = System.currentTimeMillis();

		log.info("写入去重文件：{}，{} 行，耗时：{} 秒", groupedUrl, groupedSentences.size(), (groupedEnd - groupedBegin) / 1000L);

		groupedSentences.clear();
	}
}
