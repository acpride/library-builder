package com.ride.librarybuilder;

import java.io.File;
import java.io.IOException;

import com.ride.librarybuilder.images.CropImage;
import com.ride.librarybuilder.parsers.CalibreXMLParser;

public class LibraryBuilder {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out
					.println("USAGE: java -jar LibraryBuilder.jar your-library-database.xml OPTION");
			System.exit(8);
		}

		File xml = new File(args[0]);
		if (xml.exists() == false) {
			System.out.println("File " + args[0] + " not found");
			System.exit(8);
		}

		String option = args[1];
		if ("BUILDER".equals(option) == false
				&& "IMGRENAMER".equals(option) == false
				&& "BOOKRENAMER".equals(option) == false
				&& "TEST".equals(option) == false) {
			System.out
					.println("OPTION PARAMETER VALID VALUES ARE BUILDER OR IMGRENAMER OR BOOKRENAMER");
			System.exit(8);
		}
		CalibreXMLParser parser = new CalibreXMLParser();

		if ("BUILDER".equals(option)) {
			parser.parseXML(xml);
		} else if ("BOOKRENAMER".equals(option)) {
			//parser.bookFileRenamer(xml, "d:/Alberto/books/");
			parser.bookFileRenamer(xml, "d:/Alberto/books/", true);
			
		} else if ("TEST".equals(option)) {

			// CropImage.cropImage(new
			// File("d:/Alberto/covers/test/at-the-villa-rose_mason-a-e-w.jpg"),"d:/Alberto/Dropbox/Web Libros/watermark.png");
			parser.bookCoverCopyBySize(xml, "d:/Alberto/covers/", 
					"c:/temp/test/", 22528);

		} else {
			//parser.imageRenamer(xml, "d:/Alberto/covers/");
			parser.imageRenamer(xml, "d:/Alberto/covers/",false);
		}
	}
}