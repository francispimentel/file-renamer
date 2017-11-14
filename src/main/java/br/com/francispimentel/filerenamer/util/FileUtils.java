package br.com.francispimentel.filerenamer.util;

import java.io.File;

public class FileUtils {

	public static final File TEMP_DIRECTORY = new File("temp");

	public static boolean purgeDirectory(File dir) {
		File files[] = dir.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					purgeDirectory(f);
				} else {
					f.delete();
				}
			}
		}
		return dir.delete();
	}

	public static File findFileInFolder(String fileName, File parentFolder, String... fileExtensions) {
		File f = new File(parentFolder, fileName);
		if (f.exists()) {
			return f;
		}
		
		f = new File(parentFolder, fileName + " ");
		if (f.exists()) {
			return f;
		}

		if (fileExtensions != null) {
			for (String extension : fileExtensions) {

				f = new File(parentFolder, fileName + "." + extension.toLowerCase());
				if (f.exists()) {
					return f;
				}

				f = new File(parentFolder, fileName + "." + extension.toUpperCase());
				if (f.exists()) {
					return f;
				}
				
				f = new File(parentFolder, fileName + " ." + extension.toLowerCase());
				if (f.exists()) {
					return f;
				}

				f = new File(parentFolder, fileName + " ." + extension.toUpperCase());
				if (f.exists()) {
					return f;
				}
			}
		}

		return null;
	}
}
