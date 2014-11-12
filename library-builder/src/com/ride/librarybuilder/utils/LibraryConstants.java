package com.ride.librarybuilder.utils;

public interface LibraryConstants {
	
	//public static final String CONTEXT_ROOT = "http://localhost/wordpress/";
	public static final String CONTEXT_ROOT = "http://getbooks4free.com/";
	
	public static final String TERM_GENRE = "genre";
	public static final String BOOK_AUTHOR = "book-author";
	
	public static final String TAXONOMY_SDM_CATEGORIES = "sdm_categories";
	
	public static final String POST_STATUS_PUBLISHED = "publish";
	public static final String POST_STATUS_INHERIT = "inherit";
	public static final String POST_STATUS_OPEN = "open";
	public static final String POST_STATUS_CLOSED = "closed";
	public static final String POST_TYPE_BOOK_REVIEW = "book-review";
	public static final String POST_TYPE_ATTACHMENT = "attachment";
	public static final String POST_TYPE_SDM_DOWNLOADS = "sdm_downloads";
	public static final String POST_TYPE_MIMETYPE_IMAGE = "image/jpeg";
	
	public static final String POST_META_KEY_ISBN = "isbn";
	public static final String POST_META_KEY_EPUB = "epub";
	public static final String POST_META_KEY_MOBI = "mobi";
	public static final String POST_META_KEY_PDF = "pdf";
	public static final String POST_META_KEY_AZW3 = "azw3";
	public static final String POST_META_THUMBNAIL = "_thumbnail_id";
	public static final String POST_META_KEY_SDM_DESCRIPTION = "sdm_description";
	public static final String POST_META_KEY_SDM_UPLOAD = "sdm_upload";
	public static final String POST_META_KEY_SDM_UPLOAD_THUMBNAIL = "sdm_upload_thumbnail";
	public static final String POST_META_KEY_SDM_COUNT_OFFSET = "sdm_count_offset";
	
	public static final String FILE_EPUB = "epub";
	public static final String FILE_MOBI = "mobi";
	public static final String FILE_PDF = "pdf";
	public static final String FILE_AZW3 = "azw3";
	public static final String FILE_EPUB_SUFFIX = "_epub";
	public static final String FILE_MOBI_SUFFIX = "_mobi";
	public static final String FILE_PDF_SUFFIX = "_pdf";
	public static final String FILE_AZW3_SUFFIX = "_azw3";
	public static final String FILE_DOWNLOADABLE_SUFFIX = "_file";
	
	//rating
	public static final String RATING_ZERO_STAR = "zero-stars";
	public static final String RATING_ONE_STAR = "one-star";
	public static final String RATING_TWO_STAR = "two-stars";
	public static final String RATING_THREE_STAR = "three-stars";
	public static final String RATING_FOUR_STAR = "four-stars";
	public static final String RATING_FIVE_STAR = "five-stars";
	public static final Integer RATING_ZERO_STAR_NAME = 0;
	public static final Integer RATING_ONE_STAR_NAME = 1;
	public static final Integer RATING_TWO_STAR_NAME = 2;
	public static final Integer RATING_THREE_STAR_NAME = 3;
	public static final Integer RATING_FOUR_STAR_NAME = 4;
	public static final Integer RATING_FIVE_STAR_NAME = 5;
}
