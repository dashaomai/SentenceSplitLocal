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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * 进行第二次合并的功能类，读取指定的几个去重文件，去重后的结果，到指定的文件名当中
 */
public class SecondCombine {
	private static final Logger log = LoggerFactory.getLogger("SecondCombine");

	public static void process(final String url, final String... paths) throws IOException {
		/*// 读出指定的文件列表，并在内存中去重
		final Collection<String> it = read(paths);

		// 将去重结果写回文件中
		write(url, it);*/

		// 改为小内存占用的直接读写
		readTheWrite(url, paths);
	}

	private static Collection<String> read(final String... paths) throws IOException {
		final Set<String> groupedSentences = Collections.synchronizedSet(new HashSet<>());

		final long begin = System.currentTimeMillis();

		for (final String path : paths) {
			final Stream<String> inputStream = Files.lines(Paths.get(path + Utils.GROUPED_EXT), Charset.forName("UTF-8")).parallel();

			inputStream.forEach(groupedSentences::add);

			inputStream.close();
		}

		final long end = System.currentTimeMillis();

		log.info("读取所有文件：{}，耗时：{} 秒", paths, (end - begin) / 1000L);

		return groupedSentences;
	}

	private static void write(final String url, final Collection<String> sentences) throws IOException {
		final long begin = System.currentTimeMillis();

		Files.write(Paths.get(url + Utils.GROUPED_EXT), sentences);

		final long end = System.currentTimeMillis();

		log.info("向文件：{} 写入 {} 行，耗时：{} 秒", url, sentences.size(), (end - begin) / 1000L);

		sentences.clear();
	}


	/**
	 * 读和写同时进行的操作，目的是不在内存中保持大量的文本内容，而是改为使用文本的 hashCode 作为文本是否重复的依据，以减少内存的占用
	 * @param url
	 * @param paths
	 * @throws IOException
	 */
	private static void readTheWrite(final String url, final String... paths) throws IOException {
		final Set<Integer> groupedSentenceHashcodes = Collections.synchronizedSet(new HashSet<>());

		final long begin = System.currentTimeMillis();
		final AtomicInteger counter = new AtomicInteger(0);

		final FileOutputStream fo = new FileOutputStream(url + Utils.GROUPED_EXT);
		final FileChannel channel = fo.getChannel();
		final ByteBuffer buffer = ByteBuffer.allocate(1 << 12);

		for (final String path : paths) {
			final Stream<String> inputStream = Files.lines(Paths.get(path + Utils.GROUPED_EXT), Charset.forName("UTF-8")).parallel();

			inputStream.forEach(line -> {
				counter.incrementAndGet();

				final int hashCode = line.hashCode();

				// 如果该行字符串的 hashCode 不存在，则直接写入文件
				if (groupedSentenceHashcodes.add(hashCode)) {
					buffer.put(line.getBytes());
					buffer.flip();

					try {
						channel.write(buffer);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						buffer.clear();
					}
				}
			});
		}

		channel.close();
		fo.close();

		final long end = System.currentTimeMillis();

		log.info("读取同时写回所有文件：｛｝，读取 {} 行、写回 {} 行，耗时：{} 秒",
				paths, counter.get(), groupedSentenceHashcodes.size(), (end - begin) / 1000L);
	}
}
