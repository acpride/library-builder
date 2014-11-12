package com.ride.librarybuilder.wordpress;

import java.sql.Timestamp;
import java.util.Random;


public class WordpressUtils {
	
	private static long beginTime = Timestamp.valueOf("2014-08-01 00:00:00").getTime();
	private static long endTime = Timestamp.valueOf("2014-11-01 00:00:00").getTime();
	private static Random rand = new Random();
	

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
	
	public static long getRandomTimeBetweenTwoDates () {
	    long diff = endTime - beginTime + 1;
	    return beginTime + (long) (Math.random() * diff);
	}
	
	public static int getRandomNumberBetweenRange (int min, int max) {
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
}
