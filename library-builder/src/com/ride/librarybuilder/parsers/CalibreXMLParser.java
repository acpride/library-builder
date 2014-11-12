package com.ride.librarybuilder.parsers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ride.librarybuilder.beans.Book;
import com.ride.librarybuilder.database.EbookLibraryDAO;
import com.ride.librarybuilder.database.WpPostsBean;
import com.ride.librarybuilder.database.WpPostsMetaBean;
import com.ride.librarybuilder.database.WpTermBean;
import com.ride.librarybuilder.database.WpTermRelationships;
import com.ride.librarybuilder.database.WpTermTaxonomyBean;
import com.ride.librarybuilder.images.CropImage;
import com.ride.librarybuilder.utils.LibraryConstants;
import com.ride.librarybuilder.wordpress.WordpressUtils;

public class CalibreXMLParser implements LibraryConstants {

	private HashMap<String, WpTermTaxonomyBean> hmTermTaxonomyGenres = new HashMap<String, WpTermTaxonomyBean>();
	private HashMap<String, WpTermTaxonomyBean> hmTermTaxonomyBookAuthors = new HashMap<String, WpTermTaxonomyBean>();
	private HashMap<String, Integer> hmSdmCategories = new HashMap<String, Integer>();
	private HashMap<Integer, Integer> hmStarRating = new HashMap<Integer, Integer>();

	private void getAuthorNodes(Document document) {

		SortedMap<String, String> hmAuthors = new TreeMap<String, String>();

		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			// String expression = "/calibredb/record/authors";
			XPathExpression expr = xPath.compile("/calibredb/record/authors");

			NodeList authors = (NodeList) expr.evaluate(document,
					XPathConstants.NODESET);
			System.out.println("Authors: " + authors.getLength());

			for (int i = 0; i < authors.getLength(); i++) {
				Node author = authors.item(i);
				if (author.getNodeType() == Node.ELEMENT_NODE) {
					Element elmnt = (Element) author;
					String aut = elmnt.getAttribute("sort");
					System.out.println("sort : " + aut);
					System.out.println("slug : " + WordpressUtils.getSlug(aut));

					String slug = WordpressUtils.getSlug(aut);
					aut = aut.replace("'", " ");

					// stored only once
					if (hmAuthors.containsKey(slug) == false) {
						hmAuthors.put(slug, aut);

						// insertamos en wp_term y wp_term_taxonomy
						EbookLibraryDAO dao = new EbookLibraryDAO();
						WpTermBean term = new WpTermBean();
						term.setName(aut);
						term.setSlug(slug);
						term = dao.insert_wp_term(term);
						WpTermTaxonomyBean termTaxonomy = new WpTermTaxonomyBean();
						termTaxonomy.setTermId(term.getTermId());
						termTaxonomy.setTaxonomy(BOOK_AUTHOR);
						termTaxonomy = dao
								.insert_wp_term_taxonomy(termTaxonomy);

						hmTermTaxonomyBookAuthors.put(
								elmnt.getAttribute("sort"), termTaxonomy);
					}
				}
			}
			System.out.println("hmAuthors: " + hmAuthors.size());
			System.out.println("hmTermTaxonomyBookAuthors: "
					+ hmTermTaxonomyBookAuthors.size());
			// displayMap(hmAuthors);

			// exportMap(hmAuthors, "c://temp//outputAuthors.txt");
			createInsertAuthors(hmAuthors, "c://temp//outputAuthors.txt");

		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void getCategoryNodes(Document document) {
		SortedMap<String, String> hmTags = new TreeMap<String, String>();

		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			// String expression = "/calibredb/record/tags/tag";
			XPathExpression expr = xPath.compile("/calibredb/record/tags/tag");

			NodeList tags = (NodeList) expr.evaluate(document,
					XPathConstants.NODESET);
			System.out.println("Tags: " + tags.getLength());

			for (int i = 0; i < tags.getLength(); i++) {
				Node tag = tags.item(i);

				String category = tag.getFirstChild().getNodeValue();
				String category_slug = WordpressUtils.getSlug(category);
				System.out.println("tag: " + category);
				System.out.println("slug: " + category_slug);

				// stored only once
				if (hmTags.containsKey(tag.getFirstChild().getNodeValue()) == false) {
					hmTags.put(category, category + " - " + category_slug);

					// insertamos en wp_term y wp_term_taxonomy
					EbookLibraryDAO dao = new EbookLibraryDAO();
					WpTermBean term = new WpTermBean();
					term.setName(category);
					term.setSlug(category_slug);
					term = dao.insert_wp_term(term);
					WpTermTaxonomyBean termTaxonomy = new WpTermTaxonomyBean();
					termTaxonomy.setTermId(term.getTermId());
					termTaxonomy.setTaxonomy(TERM_GENRE);
					termTaxonomy = dao.insert_wp_term_taxonomy(termTaxonomy);

					hmTermTaxonomyGenres.put(category, termTaxonomy);
				}

			}
			System.out.println("hmTags: " + hmTags.size());
			System.out.println("hmTermTaxonomyGenres: "
					+ hmTermTaxonomyGenres.size());
			// displayMap(hmAuthors);

			exportMap(hmTags, "c://temp//outputTags.txt");

		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void populateRatings() {
		try {
			EbookLibraryDAO dao = new EbookLibraryDAO();
			int zeroStarId = dao.get_term_id(RATING_ZERO_STAR);
			int oneStarId = dao.get_term_id(RATING_ONE_STAR);
			int twoStarId = dao.get_term_id(RATING_TWO_STAR);
			int threeStarId = dao.get_term_id(RATING_THREE_STAR);
			int fourStarId = dao.get_term_id(RATING_FOUR_STAR);
			int fiveStarId = dao.get_term_id(RATING_FIVE_STAR);

			hmStarRating.put(RATING_ZERO_STAR_NAME, zeroStarId);
			hmStarRating.put(RATING_ONE_STAR_NAME, oneStarId);
			hmStarRating.put(RATING_TWO_STAR_NAME, twoStarId);
			hmStarRating.put(RATING_THREE_STAR_NAME, threeStarId);
			hmStarRating.put(RATING_FOUR_STAR_NAME, fourStarId);
			hmStarRating.put(RATING_FIVE_STAR_NAME, fiveStarId);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createSdmCategories() {
		try {
			// crear la categoria epub de taxonomia sdm_categories
			// epub
			EbookLibraryDAO dao = new EbookLibraryDAO();
			WpTermBean term = new WpTermBean();
			term.setName(FILE_EPUB);
			term.setSlug(FILE_EPUB);
			term = dao.insert_wp_term(term);
			WpTermTaxonomyBean termTaxonomy = new WpTermTaxonomyBean();
			termTaxonomy.setTermId(term.getTermId());
			termTaxonomy.setTaxonomy(TAXONOMY_SDM_CATEGORIES);
			termTaxonomy = dao.insert_wp_term_taxonomy(termTaxonomy);
			hmSdmCategories.put(FILE_EPUB, termTaxonomy.getTermId());

			// mobi
			term = new WpTermBean();
			term.setName(FILE_MOBI);
			term.setSlug(FILE_MOBI);
			term = dao.insert_wp_term(term);
			termTaxonomy = new WpTermTaxonomyBean();
			termTaxonomy.setTermId(term.getTermId());
			termTaxonomy.setTaxonomy(TAXONOMY_SDM_CATEGORIES);
			termTaxonomy = dao.insert_wp_term_taxonomy(termTaxonomy);
			hmSdmCategories.put(FILE_MOBI, termTaxonomy.getTermId());

			// pdf
			term = new WpTermBean();
			term.setName(FILE_PDF);
			term.setSlug(FILE_PDF);
			term = dao.insert_wp_term(term);
			termTaxonomy = new WpTermTaxonomyBean();
			termTaxonomy.setTermId(term.getTermId());
			termTaxonomy.setTaxonomy(TAXONOMY_SDM_CATEGORIES);
			termTaxonomy = dao.insert_wp_term_taxonomy(termTaxonomy);
			hmSdmCategories.put(FILE_PDF, termTaxonomy.getTermId());

			// azw3
			term = new WpTermBean();
			term.setName(FILE_AZW3);
			term.setSlug(FILE_AZW3);
			term = dao.insert_wp_term(term);
			termTaxonomy = new WpTermTaxonomyBean();
			termTaxonomy.setTermId(term.getTermId());
			termTaxonomy.setTaxonomy(TAXONOMY_SDM_CATEGORIES);
			termTaxonomy = dao.insert_wp_term_taxonomy(termTaxonomy);
			hmSdmCategories.put(FILE_AZW3, termTaxonomy.getTermId());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void getBooks(Document document) {
		SortedMap<String, Book> hmBooks = new TreeMap<String, Book>();

		try {

			XPath xPath = XPathFactory.newInstance().newXPath();
			// String expression = "/calibredb/record";
			XPathExpression expr = xPath.compile("/calibredb/record");

			NodeList books = (NodeList) expr.evaluate(document,
					XPathConstants.NODESET);
			System.out.println("books: " + books.getLength());

			for (int i = 0; i < books.getLength(); i++) {
				Node bookNode = books.item(i);

				// UUID
				XPathExpression expr2 = xPath.compile("uuid");
				String uuid = (String) expr2.evaluate(bookNode,
						XPathConstants.STRING);
				System.out.println("uuid: " + uuid);

				// ID
				expr2 = xPath.compile("id");
				String sId = (String) expr2.evaluate(bookNode,
						XPathConstants.STRING);
				Integer id = Integer.parseInt(sId);
				System.out.println("id: " + id);

				// TITLE
				expr2 = xPath.compile("title");
				String title = (String) expr2.evaluate(bookNode,
						XPathConstants.STRING);
				System.out.println("title: " + title);

				// AUTHOR
				expr2 = xPath.compile("authors/@sort");
				String author = (String) expr2.evaluate(bookNode,
						XPathConstants.STRING);
				System.out.println("author: " + author);
				Integer authorId = ((WpTermTaxonomyBean) hmTermTaxonomyBookAuthors
						.get(author)).getTermTaxonomyId();
				System.out.println("authorId: " + authorId);

				// COMMENTS
				expr2 = xPath.compile("comments");
				String comments = (String) expr2.evaluate(bookNode,
						XPathConstants.STRING);
				System.out.println("comments: " + comments);

				// SIZE
				expr2 = xPath.compile("size");
				String sSize = (String) expr2.evaluate(bookNode,
						XPathConstants.STRING);
				Integer size = Integer.parseInt(sSize);
				System.out.println("size: " + size);

				// PUBLISH DATE
				expr2 = xPath.compile("pubdate");
				String publishDate = (String) expr2.evaluate(bookNode,
						XPathConstants.STRING);
				System.out.println("publishDate: " + publishDate);

				// COVER IMG
				expr2 = xPath.compile("cover");
				String coverImg = (String) expr2.evaluate(bookNode,
						XPathConstants.STRING);
				System.out.println("coverImg: " + coverImg);

				// TAGS
				expr2 = xPath.compile("tags/tag");
				NodeList tagsNodes = (NodeList) expr2.evaluate(bookNode,
						XPathConstants.NODESET);
				System.out.println("tagsNodes: " + tagsNodes.getLength());
				ArrayList<String> tags = new ArrayList<String>();
				ArrayList<Integer> tagsId = new ArrayList<Integer>();
				for (int j = 0; j < tagsNodes.getLength(); j++) {
					Node tag = tagsNodes.item(j);
					tags.add(tag.getFirstChild().getNodeValue());
					Integer tagId = ((WpTermTaxonomyBean) hmTermTaxonomyGenres
							.get(tag.getFirstChild().getNodeValue()))
							.getTermTaxonomyId();
					tagsId.add(tagId);
				}

				// FORMATS
				expr2 = xPath.compile("formats/format");
				NodeList formatsNodes = (NodeList) expr2.evaluate(bookNode,
						XPathConstants.NODESET);
				System.out.println("formatsNodes: " + formatsNodes.getLength());
				ArrayList<String> formats = new ArrayList<String>();
				for (int j = 0; j < formatsNodes.getLength(); j++) {
					Node format = formatsNodes.item(j);
					formats.add(format.getFirstChild().getNodeValue());
				}

				Book book = new Book(id, uuid, title, author, comments, size,
						publishDate, coverImg, tags, formats);

				System.out.println("Book uuid: " + book.getUuid());
				System.out.println("Book id: " + book.getId());
				System.out.println("Book size: " + book.getSize());
				System.out.println("Book comments: " + book.getComments());
				System.out.println("Book tags: " + book.getTags());

				// stored only once
				if (hmBooks.containsKey(book.getUuid()) == false) {
					hmBooks.put(book.getUuid(), book);

					// insertamos en wp_post y wp_post_meta
					EbookLibraryDAO dao = new EbookLibraryDAO();
					WpPostsBean post = new WpPostsBean();
					post.setPost_author(1);
					post.setPost_content(comments);
					post.setPost_title(title);
					post.setPost_excerpt("");
					post.setPost_status(POST_STATUS_PUBLISHED);
					post.setComment_status(POST_STATUS_OPEN);
					post.setPing_status(POST_STATUS_OPEN);
					post.setPost_password("");
					post.setPost_name(WordpressUtils.getSlug(title) + "_"
							+ WordpressUtils.getSlug(author));
					post.setTo_ping("");
					post.setPinged("");
					post.setPost_content_filtered("");
					post.setPost_parent(0);
					post.setGuid("");
					post.setMenu_order(0);
					post.setPost_type(POST_TYPE_BOOK_REVIEW);
					post.setPost_type_mime("");
					post.setComment_count(0);
					post = dao.insert_wp_posts(post);

					WpPostsMetaBean postMeta = new WpPostsMetaBean();
					postMeta.setPostId(post.getId());
					postMeta.setMeta_key(POST_META_KEY_ISBN);
					postMeta.setMeta_value("");
					dao.insert_wp_posts_meta(postMeta);

					// insertamos el autor y los generos en
					// wp_term_relationships, y updateamos contador en
					// wp_term_taxonomy
					WpTermRelationships postAuthorRelationship = new WpTermRelationships();
					postAuthorRelationship.setObjectId(post.getId());
					postAuthorRelationship.setTermTaxonomyId(authorId);
					postAuthorRelationship.setTermOrder(0);
					dao.insert_wp_term_relationships(postAuthorRelationship);
					dao.update_wp_term_taxonomy_count(authorId);

					for (int j = 0; j < tagsId.size(); j++) {
						WpTermRelationships postGenreRelationship = new WpTermRelationships();
						postGenreRelationship.setObjectId(post.getId());
						postGenreRelationship.setTermTaxonomyId(tagsId.get(j));
						postGenreRelationship.setTermOrder(0);
						dao.insert_wp_term_relationships(postGenreRelationship);
						dao.update_wp_term_taxonomy_count(tagsId.get(j));
					}

					// añadimos la imagen destacada del post
					// la buscamos por el titulo
					// int imgId = dao.get_post_image(title);
					String imgKey = title + "_"
							+ WordpressUtils.getSlug(author);
					int imgId = dao.get_post_attachment_id(imgKey,
							POST_TYPE_ATTACHMENT);

					// insertamos en wp_postmeta la relación post -> img
					postMeta.setPostId(post.getId());
					postMeta.setMeta_key(POST_META_THUMBNAIL);
					postMeta.setMeta_value(String.valueOf(imgId));
					dao.insert_wp_posts_meta(postMeta);

					// calculamos el nombre de los ficheros (=nombre
					// post_formato)

					// insertamos los links a los libros como metadatos
					// buscamos el id del archivo
					String epubKey = WordpressUtils.getSlug(title) + "_"
							+ WordpressUtils.getSlug(author)
							+ FILE_DOWNLOADABLE_SUFFIX + FILE_EPUB_SUFFIX;
					// int epubId = dao.get_post_attachment_id(epubKey,
					// POST_TYPE_ATTACHMENT);

					String mobiKey = WordpressUtils.getSlug(title) + "_"
							+ WordpressUtils.getSlug(author)
							+ FILE_DOWNLOADABLE_SUFFIX + FILE_MOBI_SUFFIX;
					// int mobiId = dao.get_post_attachment_id(mobiKey,
					// POST_TYPE_ATTACHMENT);

					String pdfKey = WordpressUtils.getSlug(title) + "_"
							+ WordpressUtils.getSlug(author)
							+ FILE_DOWNLOADABLE_SUFFIX + FILE_PDF_SUFFIX;
					// int pdfId = dao.get_post_attachment_id(pdfKey,
					// POST_TYPE_ATTACHMENT);

					String azw3Key = WordpressUtils.getSlug(title) + "_"
							+ WordpressUtils.getSlug(author)
							+ FILE_DOWNLOADABLE_SUFFIX + FILE_AZW3_SUFFIX;

					// Insertamos el adjunto como tipo sdm_downloads para
					// gestionar las descargas
					int sdmLinkEpub = createDownloadableContent(epubKey);
					// Lo guardamos en el custom field correspondiente
					postMeta.setMeta_key(POST_META_KEY_EPUB);
					// postMeta.setMeta_value(fileNameBase + ".epub");
					postMeta.setMeta_value(String.valueOf(sdmLinkEpub));
					dao.insert_wp_posts_meta(postMeta);

					// Insertamos el adjunto como tipo sdm_downloads para
					// gestionar las descargas
					int sdmLinkMobi = createDownloadableContent(mobiKey);
					// Lo guardamos en el custom field correspondiente
					postMeta.setMeta_key(POST_META_KEY_MOBI);
					// postMeta.setMeta_value(fileNameBase + ".mobi");
					postMeta.setMeta_value(String.valueOf(sdmLinkMobi));
					dao.insert_wp_posts_meta(postMeta);

					// Insertamos el adjunto como tipo sdm_downloads para
					// gestionar las descargas
					int sdmLinkPdf = createDownloadableContent(pdfKey);
					// Lo guardamos en el custom field correspondiente
					postMeta.setMeta_key(POST_META_KEY_PDF);
					// postMeta.setMeta_value(fileNameBase + ".pdf");
					postMeta.setMeta_value(String.valueOf(sdmLinkPdf));
					dao.insert_wp_posts_meta(postMeta);

					int sdmLinkAzw3 = createDownloadableContent(azw3Key);
					// Lo guardamos en el custom field correspondiente
					postMeta.setMeta_key(POST_META_KEY_AZW3);
					// postMeta.setMeta_value(fileNameBase + ".epub");
					postMeta.setMeta_value(String.valueOf(sdmLinkAzw3));
					dao.insert_wp_posts_meta(postMeta);

					// Le asignamos la categoria al contenido descargable
					// (ATENCION:PREVIAMENTE CREAR CONTENIDOS CON LAS CATEGORIAS
					// QUE TOQUE)
					/*
					 * int sdm_category_epub = dao.get_sdm_category(
					 * TAXONOMY_SDM_CATEGORIES, POST_META_KEY_EPUB);
					 */
					int sdm_category_epub = hmSdmCategories.get(FILE_EPUB);
					WpTermRelationships postLinksRelationship = new WpTermRelationships();
					postLinksRelationship.setObjectId(sdmLinkEpub);
					postLinksRelationship.setTermTaxonomyId(sdm_category_epub);
					postLinksRelationship.setTermOrder(0);
					dao.insert_wp_term_relationships(postLinksRelationship);
					dao.update_wp_term_taxonomy_count(sdm_category_epub);

					/*
					 * int sdm_category_mobi = dao.get_sdm_category(
					 * TAXONOMY_SDM_CATEGORIES, POST_META_KEY_MOBI);
					 */
					int sdm_category_mobi = hmSdmCategories.get(FILE_MOBI);
					postLinksRelationship = new WpTermRelationships();
					postLinksRelationship.setObjectId(sdmLinkMobi);
					postLinksRelationship.setTermTaxonomyId(sdm_category_mobi);
					postLinksRelationship.setTermOrder(0);
					dao.insert_wp_term_relationships(postLinksRelationship);
					dao.update_wp_term_taxonomy_count(sdm_category_mobi);

					/*
					 * int sdm_category_pdf = dao.get_sdm_category(
					 * TAXONOMY_SDM_CATEGORIES, POST_META_KEY_PDF);
					 */
					int sdm_category_pdf = hmSdmCategories.get(FILE_PDF);
					postLinksRelationship = new WpTermRelationships();
					postLinksRelationship.setObjectId(sdmLinkPdf);
					postLinksRelationship.setTermTaxonomyId(sdm_category_pdf);
					postLinksRelationship.setTermOrder(0);
					dao.insert_wp_term_relationships(postLinksRelationship);
					dao.update_wp_term_taxonomy_count(sdm_category_pdf);

					int sdm_category_azw3 = hmSdmCategories.get(FILE_AZW3);
					postLinksRelationship = new WpTermRelationships();
					postLinksRelationship.setObjectId(sdmLinkAzw3);
					postLinksRelationship.setTermTaxonomyId(sdm_category_azw3);
					postLinksRelationship.setTermOrder(0);
					dao.insert_wp_term_relationships(postLinksRelationship);
					dao.update_wp_term_taxonomy_count(sdm_category_azw3);

					// insertamos el rating
					int stars = WordpressUtils
							.getRandomNumberBetweenRange(3, 5);
					int starId = hmStarRating.get(stars);

					WpTermRelationships postStarsRelationship = new WpTermRelationships();
					postStarsRelationship.setObjectId(post.getId());
					postStarsRelationship.setTermTaxonomyId(starId);
					postStarsRelationship.setTermOrder(0);
					dao.insert_wp_term_relationships(postStarsRelationship);
					dao.update_wp_term_taxonomy_count(starId);

				}
			}
			System.out.println("hmBooks: " + hmBooks.size());
			// displayMap(hmAuthors);

			exportBookCollection(hmBooks, "c://temp//outputBooks.txt");

		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void displayMap(Map<String, String> map) {
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			System.out.println("key : " + key + " value :" + map.get(key));
		}
	}

	private void createInsertAuthors(Map<String, String> map, String outputPath)
			throws IOException {

		File output = new File(outputPath);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(output), "UTF-8"));

		String sql = "INSERT INTO wp_terms (name,slug) VALUES ";
		bw.write(sql);
		bw.newLine();

		boolean first = true;

		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			if (first) {
				first = false;
				sql = "('" + (String) map.get(key) + "','" + key + "')";
			} else {
				sql = ",('" + (String) map.get(key) + "','" + key + "')";
			}
			bw.write(sql);
			bw.newLine();
		}
		bw.write(";");
		bw.close();
	}

	private void exportMap(Map<String, String> map, String outputPath)
			throws IOException {
		File output = new File(outputPath);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(output), "UTF-8"));
		// BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			bw.write((String) map.get(key));
			bw.newLine();
		}
		bw.close();
	}

	private void exportBookCollection(Map<String, Book> map, String outputPath)
			throws IOException {
		File output = new File(outputPath);
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			Book book = (Book) map.get(key);
			bw.write((String) key + " - " + book.getTitle() + " - "
					+ book.getTags());
			bw.newLine();
		}
		bw.close();
	}

	public void parseXML(File inputXml) {

		Document document = parseDocument(inputXml);

		getAuthorNodes(document);

		getCategoryNodes(document);

		createSdmCategories();

		// cargamos el hashmap con los ratings
		populateRatings();

		getBooks(document);

	}

	private Document parseDocument(File xml) {
		System.setProperty("file.encoding", "UTF-8");
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		Document document = null;
		try {
			InputStream inputStream = new FileInputStream(xml);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			document = builder.parse(is);
			// document = builder.parse(new FileInputStream(inputXml));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return document;
	}

	public void imageRenamer(File xml, String outputDir) {
		imageRenamer(xml, outputDir, true);
	}

	public void imageRenamer(File xml, String outputDir, boolean crop) {

		Document document = parseDocument(xml);

		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xPath.compile("/calibredb/record");
			NodeList books = (NodeList) expr.evaluate(document,
					XPathConstants.NODESET);
			System.out.println("books: " + books.getLength());

			for (int i = 0; i < books.getLength(); i++) {
				Node bookNode = books.item(i);
				// obtenemos cover
				XPathExpression expr2 = xPath.compile("cover");
				String cover = (String) expr2.evaluate(bookNode,
						XPathConstants.STRING);
				System.out.println("cover: " + cover);

				// obtenemos el titulo
				expr2 = xPath.compile("title");
				String title = (String) expr2.evaluate(bookNode,
						XPathConstants.STRING);
				System.out.println("title: " + title);

				// Obtenemos el autor
				expr2 = xPath.compile("authors/@sort");
				String author = (String) expr2.evaluate(bookNode,
						XPathConstants.STRING);
				System.out.println("author: " + author);

				// calculamos el nombre
				String destFileName = outputDir + WordpressUtils.getSlug(title)
						+ "_" + WordpressUtils.getSlug(author) + ".jpg";
				File orig = new File(cover);
				File dest = new File(destFileName);

				FileUtils.copyFile(orig, dest);

				if (crop) {
					CropImage.cropImage(dest,
							"d:/Alberto/Dropbox/Web Libros/watermark.png");
				}

			}

		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void bookFileRenamer(File xml, String outputDir) {
		bookFileRenamer(xml, outputDir, false);
	}
	
	public void bookFileRenamer(File xml, String outputDir, boolean deletePrevious) {

		Document document = parseDocument(xml);

		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xPath.compile("/calibredb/record");
			NodeList books = (NodeList) expr.evaluate(document,
					XPathConstants.NODESET);
			System.out.println("books: " + books.getLength());

			for (int i = 0; i < books.getLength(); i++) {
				Node bookNode = books.item(i);

				// obtenemos el titulo
				XPathExpression expr2 = xPath.compile("title");
				String title = (String) expr2.evaluate(bookNode,
						XPathConstants.STRING);
				System.out.println("title: " + title);

				// Obtenemos el autor
				expr2 = xPath.compile("authors/@sort");
				String author = (String) expr2.evaluate(bookNode,
						XPathConstants.STRING);
				System.out.println("author: " + author);

				// obtenemos archivo
				expr2 = xPath.compile("formats/format");
				NodeList formats = (NodeList) expr2.evaluate(bookNode,
						XPathConstants.NODESET);

				// asumimos que EPUB es el item(1)...
				//Node format = formats.item(0);
				Node format = formats.item(1);
				String file = format.getFirstChild().getNodeValue();
				System.out.println("file: " + file);

				// calculamos el nombre
				String destFileName = outputDir + WordpressUtils.getSlug(title)
						+ "_" + WordpressUtils.getSlug(author) + ".epub";
				File orig = new File(file);
				File dest = new File(destFileName);

				FileUtils.copyFile(orig, dest);

				// zip
				boolean ret = zipFile(dest, FILE_EPUB, deletePrevious);
				if (ret) {
					// borramos archivo original
					dest.delete();
				}

				// buscamos en el directorio un archivo mobi, azw3 y uno pdf, y
				// los
				// renombramos como el epub
				File mobi = lookForOtherFormats(file, FILE_MOBI);
				if (mobi != null) {
					System.out.println("mobi: " + mobi);
					// copiamos con el nombre nuevo
					String destFileNameMobi = outputDir
							+ WordpressUtils.getSlug(title) + "_"
							+ WordpressUtils.getSlug(author) + ".mobi";
					File destMobi = new File(destFileNameMobi);

					FileUtils.copyFile(mobi, destMobi);
					// zip
					ret = zipFile(destMobi, FILE_MOBI, deletePrevious);
					if (ret) {
						// borramos archivo original
						destMobi.delete();
					}

				}
				File pdf = lookForOtherFormats(file, FILE_PDF);
				if (pdf != null) {
					System.out.println("pdf: " + pdf);
					// copiamos con el nombre nuevo
					String destFileNamePdf = outputDir
							+ WordpressUtils.getSlug(title) + "_"
							+ WordpressUtils.getSlug(author) + ".pdf";
					File destPdf = new File(destFileNamePdf);

					FileUtils.copyFile(pdf, destPdf);
					// zip
					ret = zipFile(destPdf, FILE_PDF, deletePrevious);
					if (ret) {
						// borramos archivo original
						destPdf.delete();
					}
				}
				File azw3 = lookForOtherFormats(file, FILE_AZW3);
				if (azw3 != null) {
					System.out.println("azw3: " + azw3);
					// copiamos con el nombre nuevo
					String destFileNameAzw3 = outputDir
							+ WordpressUtils.getSlug(title) + "_"
							+ WordpressUtils.getSlug(author) + ".azw3";
					File destAzw3 = new File(destFileNameAzw3);

					FileUtils.copyFile(pdf, destAzw3);
					// zip
					ret = zipFile(destAzw3, FILE_AZW3, deletePrevious);
					if (ret) {
						// borramos archivo original
						destAzw3.delete();
					}
				}
			}

		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean zipFile(File fileToCompress, String type) {
		return zipFile(fileToCompress, type, false);
	}
	
	private boolean zipFile(File fileToCompress, String type, boolean deletePrevious) {
		String fileName = fileToCompress.getAbsolutePath().substring(0,
				fileToCompress.getAbsolutePath().lastIndexOf("."));

		try {
			File zip = new File(fileName + FILE_DOWNLOADABLE_SUFFIX + "_"
					+ type + ".zip");
			
			if(zip.exists() && deletePrevious){
				boolean del = zip.delete();
				System.out.println("Fichero " + zip + " borrado: " + del);
			}
			ZipFile zipFile = new ZipFile(fileName + FILE_DOWNLOADABLE_SUFFIX
					+ "_" + type + ".zip");
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
			zipFile.addFile(fileToCompress, parameters);
			
			return zip.exists();

		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		/*
		 * OutputStream zip_output = new FileOutputStream(new
		 * File("zip_output.zip"));
		 * 
		 * 
		 * ArchiveOutputStream logical_zip = new
		 * ArchiveStreamFactory().createArchiveOutputStream
		 * (ArchiveStreamFactory.ZIP, zip_output);
		 * logical_zip.putArchiveEntry(new ZipArchiveEntry("test_file_1.xml"));
		 * IOUtils.copy(new FileInputStream(new File("test_file_1.xml")),
		 * logical_zip); logical_zip.closeArchiveEntry();
		 * logical_zip.putArchiveEntry(new ZipArchiveEntry("test_file_2.xml"));
		 * IOUtils.copy(new FileInputStream(new File("test_file_2.xml")),
		 * logical_zip); logical_zip.closeArchiveEntry();
		 * 
		 * logical_zip.finish();
		 * 
		 * zip_output.close();
		 */

	}

	private File lookForOtherFormats(String epubFile, String type) {

		String directory = epubFile.substring(0, epubFile.lastIndexOf("/") + 1);
		System.out.println("directory: " + directory);
		File dir = new File(directory);

		File[] list = dir.listFiles();
		if (list != null)
			for (File fil : list) {
				String extension = FilenameUtils.getExtension(fil
						.getAbsolutePath());
				System.out.println("extension: " + extension);
				if (extension.toLowerCase().equals(type)) {
					return fil;
				}
			}
		return null;
	}

	private int createDownloadableContent(String title) {

		EbookLibraryDAO dao = new EbookLibraryDAO();
		WpPostsBean post = new WpPostsBean();
		post.setPost_author(1);
		post.setPost_content("");
		post.setPost_title(title);
		post.setPost_excerpt("");
		post.setPost_status(POST_STATUS_PUBLISHED);
		// post.setComment_status(POST_STATUS_OPEN);
		// post.setPing_status(POST_STATUS_OPEN);
		post.setComment_status(POST_STATUS_CLOSED);
		post.setPing_status(POST_STATUS_CLOSED);
		post.setPost_password("");
		post.setPost_name(title);
		post.setTo_ping("");
		post.setPinged("");
		post.setPost_content_filtered("");
		post.setPost_parent(0);
		post.setGuid("");
		post.setMenu_order(0);
		post.setPost_type(POST_TYPE_SDM_DOWNLOADS);
		post.setPost_type_mime("");
		post.setComment_count(0);

		try {
			post = dao.insert_wp_posts(post);

			String guid = CONTEXT_ROOT + "?post_type=sdm_downloads&p="
					+ post.getId();
			dao.update_wp_posts_guid(post.getId(), guid);

			// sdm_description
			WpPostsMetaBean postMeta = new WpPostsMetaBean();
			postMeta.setPostId(post.getId());
			postMeta.setMeta_key(POST_META_KEY_SDM_DESCRIPTION);
			postMeta.setMeta_value(title);
			dao.insert_wp_posts_meta(postMeta);

			// sdm_upload
			// OBTENEMOS LA URL DEL ARCHIVO DESCARGABLE (de tipo attachment!!!)
			// String url = dao.get_post_attachment_url(title,
			// POST_TYPE_SDM_DOWNLOADS);
			String url = dao.get_post_attachment_url(title,
					POST_TYPE_ATTACHMENT);
			postMeta = new WpPostsMetaBean();
			postMeta.setPostId(post.getId());
			postMeta.setMeta_key(POST_META_KEY_SDM_UPLOAD);
			postMeta.setMeta_value(url);
			dao.insert_wp_posts_meta(postMeta);

			// sdm_upload_thumbnail
			postMeta = new WpPostsMetaBean();
			postMeta.setPostId(post.getId());
			postMeta.setMeta_key(POST_META_KEY_SDM_UPLOAD_THUMBNAIL);
			postMeta.setMeta_value("");
			dao.insert_wp_posts_meta(postMeta);

			// sdm_count_offset
			postMeta = new WpPostsMetaBean();
			postMeta.setPostId(post.getId());
			postMeta.setMeta_key(POST_META_KEY_SDM_COUNT_OFFSET);
			postMeta.setMeta_value(String.valueOf(WordpressUtils
					.getRandomNumberBetweenRange(50, 300)));
			dao.insert_wp_posts_meta(postMeta);

			return post.getId();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return -1;
	}

	public void bookCoverCopyBySize(File xml, String coverDir,
			String outputDir, long maxSize) {
		Document document = parseDocument(xml);

		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xPath.compile("/calibredb/record");
			NodeList books = (NodeList) expr.evaluate(document,
					XPathConstants.NODESET);
			System.out.println("books: " + books.getLength());

			for (int i = 0; i < books.getLength(); i++) {
				Node bookNode = books.item(i);

				// obtenemos el titulo
				XPathExpression expr2 = xPath.compile("title");
				String title = (String) expr2.evaluate(bookNode,
						XPathConstants.STRING);
				System.out.println("title: " + title);

				// Obtenemos el autor
				expr2 = xPath.compile("authors/@sort");
				String author = (String) expr2.evaluate(bookNode,
						XPathConstants.STRING);
				System.out.println("author: " + author);

				// obtenemos archivo
				expr2 = xPath.compile("formats/format");
				NodeList formats = (NodeList) expr2.evaluate(bookNode,
						XPathConstants.NODESET);

				// asumimos que solo hay un formato...
				// Node format = formats.item(0);
				// epub viene en segunda posición
				Node format = formats.item(1);
				String file = format.getFirstChild().getNodeValue();
				System.out.println("file: " + file);

				// calculamos el nombre
				String coverFileName = coverDir + WordpressUtils.getSlug(title)
						+ "_" + WordpressUtils.getSlug(author) + ".jpg";

				File cover = new File(coverFileName);
				if (cover.length() < maxSize) {
					System.out.println("El archivo " + cover.getAbsolutePath()
							+ " ocupa menos de " + maxSize + " bytes");
					// copiamos el libro al directorio destino
					String outputPath = file.substring(file.indexOf("/"));

					/*
					 * String destFileName = outputDir + outputPath +
					 * WordpressUtils.getSlug(title) + "_" +
					 * WordpressUtils.getSlug(author) + ".epub";
					 */
					String destFileName = outputDir + outputPath;
					File book = new File(file);
					File destFile = new File(destFileName);
					FileUtils.copyFile(book, destFile);
				}

			}

		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
