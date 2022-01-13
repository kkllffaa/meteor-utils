package com.kkllffaa.meteorutils.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
	
	
	public static void addfiletozip(File file, ZipOutputStream stream, ZipEntry entry) {
		if (!file.exists() || !file.isFile() || !file.canRead()) return;
		try {
			FileInputStream fis = new FileInputStream(file);
			ZipEntry zipEntry = entry != null ? new ZipEntry(entry + file.getName()) : new ZipEntry(file.getName());
			stream.putNextEntry(zipEntry);
			stream.write(fis.readAllBytes());
			stream.closeEntry();
			fis.close();
		}catch (IOException ignored) {}
	}
	
	/**
	 * @param recursive -1 for infinite
	 */
	public static void adddirectorytozip(File directury, ZipOutputStream stream, ZipEntry entry, int recursive) {
		if (!directury.exists() || !directury.isDirectory() || !directury.canRead()) return;
		
		try {
			
			ZipEntry zipEntry = entry != null ?
					new ZipEntry(entry + directury.getName() + "/") : new ZipEntry(directury.getName() + "/");
			stream.putNextEntry(zipEntry);
			
			File[] files = directury.listFiles();
			if (files != null) {
				for (File fileentry : files) {
					if (fileentry.isDirectory()) {
						if (recursive > 0) {
							adddirectorytozip(fileentry, stream, zipEntry, recursive-1);
						}else if (recursive == -1) {
							adddirectorytozip(fileentry, stream, zipEntry, -1);
						}
					} else {
						addfiletozip(fileentry, stream, zipEntry);
					}
				}
			}
			stream.closeEntry();
		}catch (IOException ignored) {}
		
	}
	
	public static void addfiletozip(File file, ZipOutputStream stream) {addfiletozip(file, stream, null);}
	
	
	/**
	 * @param recursive -1 for infinite
	 */
	public static void adddirectorytozip(File directury, ZipOutputStream stream, int recursive) {adddirectorytozip(directury, stream, null, recursive);}
	
	
}
