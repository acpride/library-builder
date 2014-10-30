package com.ride.librarybuilder.wordpress;

public class WordpressUtils {

	public static String getSlug(String text) {

		// to lowerCase, no special chars, no blank spaces
		String fullASCII = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝßàáâãäåæçèéêëìíîïðñòóôõöōøùúûüūýÿ";
		String targetASCII = "AAAAAAACEEEEIIIIDNOOOOOOUUUUYBaaaaaaaceeeeiiiionooooooouuuuuyy";
		
		String output = text.toLowerCase();
		
		for (int i=0; i<fullASCII.length(); i++) {
	        output = output.replace(fullASCII.charAt(i), targetASCII.charAt(i));
	    }
		
		output = output.replace("&", "and");
		output = output.replace(" ", "-");
		output = output.replace(",", "");
		output = output.replace(".", "");
		output = output.replace("'", "-");
		output = output.replace(":", " ");
		output = output.replace("\"", "");
		output = output.replace("?", "");
		output = output.replace("’", "-");
		output = output.replace("–", "-");
		
		return output;
	}
}
