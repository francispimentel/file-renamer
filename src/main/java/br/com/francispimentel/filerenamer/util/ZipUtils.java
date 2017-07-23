package br.com.francispimentel.filerenamer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

	public static File extractZip(InputStream inputStream) throws IOException {
		File dir = new File(FileUtils.TEMP_DIRECTORY, UUID.randomUUID().toString());
		dir.mkdirs();

		int len;
		byte[] buffer = new byte[1024];

		ZipInputStream zis = new ZipInputStream(inputStream);
		ZipEntry ze = zis.getNextEntry();

		while (ze != null) {
			FileOutputStream fos = new FileOutputStream(new File(dir, ze.getName()));

			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}

			fos.close();
			ze = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();

		return dir;
	}

	public static File createZipFile(File dir) throws IOException {
		File zip = new File(FileUtils.TEMP_DIRECTORY, dir.getName() + ".zip");
		byte[] buffer = new byte[1024];

		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));

		File files[] = dir.listFiles();
		for (File entrada : files) {
			ZipEntry ze = new ZipEntry(entrada.getName());
			zos.putNextEntry(ze);

			FileInputStream in = new FileInputStream(entrada);
			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
			in.close();

			zos.closeEntry();
		}

		zos.close();

		return zip;
	}
}
