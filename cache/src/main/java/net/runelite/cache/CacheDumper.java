package net.runelite.cache;

import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.runelite.cache.definitions.EnumDefinition;
import net.runelite.cache.definitions.HitSplatDefinition;
import net.runelite.cache.definitions.StructDefinition;
import net.runelite.cache.definitions.VarbitDefinition;
import net.runelite.cache.definitions.loaders.EnumLoader;
import net.runelite.cache.definitions.loaders.HitSplatLoader;
import net.runelite.cache.definitions.loaders.StructLoader;
import net.runelite.cache.definitions.loaders.VarbitLoader;
import net.runelite.cache.definitions.loaders.sound.SoundEffectTrackLoader;
import net.runelite.cache.definitions.sound.SoundEffectTrackDefinition;
import net.runelite.cache.fs.Archive;
import net.runelite.cache.fs.ArchiveFiles;
import net.runelite.cache.fs.FSFile;
import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Store;
import net.runelite.cache.fs.Storage;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class CacheDumper
{
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private static final String DEFAULT_OUT = "dump" + File.separator;
	private static String outDir = DEFAULT_OUT;

	public static boolean DUMP_SFX = true;

	public static void main(String[] args) throws IOException
	{
		String cacheDir = "";

		if (args.length == 1)
		{
			cacheDir = args[0] + File.separator;
			outDir = cacheDir + DEFAULT_OUT;
		}
		else
		{
			cacheDir = System.getProperty("user.home") + File.separator + "jagexcache" + File.separator;
			outDir = cacheDir + DEFAULT_OUT;
			cacheDir += "oldschool" + File.separator + "LIVE" + File.separator;
		}

		File outputDirs = new File(outDir);
		if (!outputDirs.exists()) outputDirs.mkdirs();
		System.out.printf("dumping cache @ [%s] to [%s]\n", cacheDir, outDir);

		Store store = Cache.loadStore(cacheDir);

		dumpTitle("title.jpg", store);
		dumpTitle("titlewide.jpg", store);

		dumpVarbits(store);
		dumpEnums(store);
		dumpStructs(store);

		dumpSplats(store);

		Cache.dumpItems(store, dumpDir("items"));
		Cache.dumpNpcs(store, dumpDir("npcs"));
		Cache.dumpObjects(store, dumpDir("objs"));
		Cache.dumpSprites(store, dumpDir("sprites"));

		dumpSounds(store);
	}

	public static File dumpDir(String out)
	{
		File outputFolder = new File(outDir + File.separator + out + File.separator);
		if (!outputFolder.exists()) outputFolder.mkdirs();
		return outputFolder;
	}

	public static File dumpFile(String out)
	{
		return new File(outDir + File.separator + out);
	}

	private static void dumpTitle(String title, Store store) throws IOException
	{
		File out = dumpFile(title);
		if (out.exists()) out.delete();
		out.createNewFile();

		store.load();

		Storage storage = store.getStorage();
		Index index = store.getIndex(IndexType.BINARY);
		Archive archive = index.findArchiveByName(title);
		byte[] contents = archive.decompress(storage.loadArchive(archive));

		Files.write(out.toPath(), contents);
	}

	private static void dumpVarbits(Store store) throws IOException
	{
		File out = dumpFile("varbits.json");
		if (out.exists()) out.delete();

		int count = 0;

		store.load();

		Storage storage = store.getStorage();
		Index index = store.getIndex(IndexType.CONFIGS);
		Archive archive = index.getArchive(ConfigType.VARBIT.getId());

		byte[] archiveData = storage.loadArchive(archive);
		ArchiveFiles files = archive.getFiles(archiveData);

		VarbitLoader loader = new VarbitLoader();
		CharSink sink = com.google.common.io.Files.asCharSink(out, Charset.defaultCharset(), FileWriteMode.APPEND);
		for (FSFile file : files.getFiles())
		{
			VarbitDefinition varbit = loader.load(file.getFileId(), file.getContents());

			sink.write(gson.toJson(varbit) + "\n");
			++count;
		}

		System.out.printf("Dumped %d varbits to %s\n", count, out);
	}

	private static void dumpEnums(Store store) throws IOException
	{
		File enumDir = dumpDir("enums");
		int count = 0;

		Storage storage = store.getStorage();
		Index index = store.getIndex(IndexType.CONFIGS);
		Archive archive = index.getArchive(ConfigType.ENUM.getId());

		byte[] archiveData = storage.loadArchive(archive);
		ArchiveFiles files = archive.getFiles(archiveData);

		EnumLoader loader = new EnumLoader();

		for (FSFile file : files.getFiles())
		{
			EnumDefinition def = loader.load(file.getFileId(), file.getContents());

			if (def != null)
			{
				com.google.common.io.Files.asCharSink(new File(enumDir, file.getFileId() + ".json"), Charset.defaultCharset()).write(gson.toJson(def));
				++count;
			}
		}

		System.out.printf("Dumped %d enums to %s\n", count, enumDir);
	}

	private static void dumpStructs(Store store) throws IOException
	{
		File out = dumpFile("structs.json");
		if (out.exists()) out.delete();

		int count = 0;

		store.load();

		Storage storage = store.getStorage();
		Index index = store.getIndex(IndexType.CONFIGS);
		Archive archive = index.getArchive(ConfigType.STRUCT.getId());

		byte[] archiveData = storage.loadArchive(archive);
		ArchiveFiles files = archive.getFiles(archiveData);

		StructLoader loader = new StructLoader();
		CharSink sink = com.google.common.io.Files.asCharSink(out, Charset.defaultCharset(), FileWriteMode.APPEND);
		for (FSFile file : files.getFiles())
		{
			StructDefinition struct = loader.load(file.getFileId(), file.getContents());

			sink.write(gson.toJson(struct) + "\n");
			++count;
		}

		System.out.printf("Dumped %d structs to %s\n", count, out);
	}

	private static void dumpSplats(Store store) throws IOException
	{
		File splatDir = dumpDir("splats");
		int count = 0;

		Storage storage = store.getStorage();
		Index index = store.getIndex(IndexType.CONFIGS);
		Archive archive = index.getArchive(ConfigType.HITSPLAT.getId());

		HitSplatLoader loader = new HitSplatLoader();

		byte[] archiveData = storage.loadArchive(archive);
		ArchiveFiles files = archive.getFiles(archiveData);

		for (FSFile file : files.getFiles())
		{
			byte[] b = file.getContents();

			HitSplatDefinition def = loader.load(b);

			com.google.common.io.Files.asCharSink(new File(splatDir, file.getFileId() + ".json"), Charset.defaultCharset()).write(gson.toJson(def));
			++count;
		}

		System.out.printf("Dumped %d hitsplats to %s\n", count, splatDir);
	}

	private static void dumpSounds(Store store) throws IOException
	{
		File out = dumpFile("sfx.json");
		if (out.exists()) out.delete();

		File sfxDir = dumpDir("sfx");
		int count = 0;
		int countDumped = 0;

		store.load();

		Storage storage = store.getStorage();
		Index index = store.getIndex(IndexType.SOUNDEFFECTS);

		CharSink sink = com.google.common.io.Files.asCharSink(out, Charset.defaultCharset(), FileWriteMode.APPEND);
		for (Archive archive : index.getArchives())
		{
			byte[] contents = archive.decompress(storage.loadArchive(archive));

			SoundEffectTrackLoader setLoader = new SoundEffectTrackLoader();
			SoundEffectTrackDefinition soundEffect = setLoader.load(contents);

			sink.write(gson.toJson(soundEffect));

			if (DUMP_SFX)
			{
				try
				{
					Object audioStream;
					byte[] data = soundEffect.mix();

					AudioFormat audioFormat = new AudioFormat(22050.0f, 8, 1, true, false);
					audioStream = new AudioInputStream(new ByteArrayInputStream(data), audioFormat, data.length);

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					AudioSystem.write((AudioInputStream) audioStream, AudioFileFormat.Type.WAVE, bos);
					data = bos.toByteArray();

					try (FileOutputStream fos = new FileOutputStream(new File(sfxDir, archive.getArchiveId() + ".wav")))
					{
						fos.write(data);
						++countDumped;
					}
				}
				catch (Exception e)
				{
					System.err.printf("Failed to dump wav track %s\n", archive.getArchiveId() + ".wav");
				}
			}

			++count;
		}

		if (DUMP_SFX)
			System.out.printf("Dumped %d sound effects in %s to %d wav files in %s\n", count, out, countDumped, sfxDir);
		else
			System.out.printf("Dumped %d sound effects in %s\n", count, out);
	}
}
