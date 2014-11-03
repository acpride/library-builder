package com.ride.librarybuilder.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import com.ride.librarybuilder.utils.LibraryConstants;
import com.ride.librarybuilder.wordpress.WordpressUtils;

public class EbookLibraryDAO implements LibraryConstants {

	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public EbookLibraryDAO(){

		try {
			// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// setup the connection with the DB.
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/ebook_library?"
							+ "user=admin&password=admin");

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public WpTermBean insert_wp_term(WpTermBean term) throws SQLException {
       
		String sql = "INSERT INTO wp_terms (name, slug) values(?, ?)";
		preparedStatement = connect.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		preparedStatement.setString(1, term.getName());
		preparedStatement.setString(2, term.getSlug());

		preparedStatement.executeUpdate();
		ResultSet rs = preparedStatement.getGeneratedKeys();
		rs.next();
		Integer auto_id = rs.getInt(1);

		term.setTermId(auto_id);

		rs.close();
		preparedStatement.close();

		return term;
	}

	public WpTermTaxonomyBean insert_wp_term_taxonomy(
			WpTermTaxonomyBean termTaxonomy) throws SQLException {

		String sql = "INSERT INTO wp_term_taxonomy (term_id, taxonomy, description) values(?, ?, ?)";
		preparedStatement = connect.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		preparedStatement.setInt(1, termTaxonomy.getTermId());
		preparedStatement.setString(2, termTaxonomy.getTaxonomy());
		preparedStatement.setString(3, "");

		preparedStatement.executeUpdate();
		ResultSet rs = preparedStatement.getGeneratedKeys();
		rs.next();
		Integer auto_id = rs.getInt(1);
		termTaxonomy.setTermTaxonomyId(auto_id);

		rs.close();
		preparedStatement.close();
		
		return termTaxonomy;
	}

	public WpPostsBean insert_wp_posts(WpPostsBean post) throws SQLException {

		String sql = "INSERT INTO wp_posts (post_author, post_date, post_date_gmt, post_content, post_title, " +
				"post_excerpt, post_status, comment_status, ping_status, post_password, post_name, to_ping, pinged, " + 
				"post_modified, post_modified_gmt, post_content_filtered, post_parent, guid, menu_order, post_type, " +
				"post_mime_type, comment_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		preparedStatement = connect.prepareStatement(sql,
				Statement.RETURN_GENERATED_KEYS);
		preparedStatement.setInt(1, post.getPost_author());
		
		//randomize publish date
		long pubDate = WordpressUtils.getRandomTimeBetweenTwoDates();
		
		preparedStatement.setTimestamp(2,
				new Timestamp(pubDate));
		preparedStatement.setTimestamp(3,
				new Timestamp(pubDate));
		preparedStatement.setString(4, post.getPost_content());
		preparedStatement.setString(5, post.getPost_title());
		preparedStatement.setString(6, post.getPost_excerpt());
		preparedStatement.setString(7, post.getPost_status());
		preparedStatement.setString(8, post.getComment_status());
		preparedStatement.setString(9, post.getPing_status());
		preparedStatement.setString(10, post.getPost_password());
		preparedStatement.setString(11, post.getPost_name());
		preparedStatement.setString(12, post.getTo_ping());
		preparedStatement.setString(13, post.getPinged());
		preparedStatement.setTimestamp(14,
				new Timestamp(pubDate));
		preparedStatement.setTimestamp(15,
				new Timestamp(pubDate));
		preparedStatement.setString(16, post.getPost_content_filtered());
		preparedStatement.setInt(17, post.getPost_parent());
		preparedStatement.setString(18, post.getGuid());
		preparedStatement.setInt(19, post.getMenu_order());
		preparedStatement.setString(20, post.getPost_type());
		preparedStatement.setString(21, post.getPost_type_mime());
		preparedStatement.setInt(22, post.getComment_count());

		preparedStatement.executeUpdate();
		ResultSet rs = preparedStatement.getGeneratedKeys();
		rs.next();
		Integer auto_id = rs.getInt(1);
		post.setId(auto_id);
		
		rs.close();
		preparedStatement.close();
		
		return post;
	}

	public WpPostsMetaBean insert_wp_posts_meta(WpPostsMetaBean postMeta) throws SQLException {

		String sql = "INSERT INTO wp_postmeta (post_id, meta_key, meta_value) VALUES (?, ?, ?)";
		preparedStatement = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		preparedStatement.setInt(1, postMeta.getPostId());
		preparedStatement.setString(2, postMeta.getMeta_key());
		preparedStatement.setString(3, postMeta.getMeta_value());
		
		preparedStatement.executeUpdate();
		ResultSet rs = preparedStatement.getGeneratedKeys();
		rs.next();
		Integer auto_id = rs.getInt(1);
		postMeta.setMetaId(auto_id);
		
		rs.close();
		preparedStatement.close();
		
		return postMeta;
	}
	
	public WpTermRelationships insert_wp_term_relationships(WpTermRelationships termRelationships) throws SQLException{
		
		String sql = "INSERT INTO wp_term_relationships(object_id, term_taxonomy_id, term_order) VALUES (?, ?, ?)";
		preparedStatement = connect.prepareStatement(sql);
		preparedStatement.setInt(1, termRelationships.getObjectId());
		preparedStatement.setInt(2, termRelationships.getTermTaxonomyId());
		preparedStatement.setInt(3, termRelationships.getTermOrder());
		
		preparedStatement.executeUpdate();
		
		preparedStatement.close();
		
		return termRelationships;	
		
	}

	public int update_wp_term_taxonomy_count(Integer termId) throws SQLException{
		String sql = "UPDATE wp_term_taxonomy SET COUNT=COUNT+1 WHERE TERM_ID=?";
		preparedStatement = connect.prepareStatement(sql);
		preparedStatement.setInt(1, termId);
		
		int ret = preparedStatement.executeUpdate();
		
		preparedStatement.close();
		
		return ret;
		
		
	}
		
	public int get_post_attachment_id(String title, String type) throws SQLException{
		int ret = -1;
		String sql = "SELECT ID FROM wp_posts WHERE POST_TYPE=? AND POST_TITLE=?";
		preparedStatement = connect.prepareStatement(sql);
		//preparedStatement.setString(1, POST_TYPE_ATTACHMENT);
		preparedStatement.setString(1, type);
		preparedStatement.setString(2, WordpressUtils.getSlug(title));
		
		ResultSet rs = preparedStatement.executeQuery();
		if(rs.next()){
			int imgId = rs.getInt("ID");
			ret = imgId;
		}
		
		rs.close();
		preparedStatement.close();
		
		return ret;
	}
	
	public String get_post_attachment_url(String title, String type) throws SQLException{
		String ret = "";
		String sql = "SELECT GUID FROM wp_posts WHERE POST_TYPE=? AND POST_TITLE=?";
		preparedStatement = connect.prepareStatement(sql);
		//preparedStatement.setString(1, POST_TYPE_ATTACHMENT);
		preparedStatement.setString(1, type);
		preparedStatement.setString(2, WordpressUtils.getSlug(title));
		
		ResultSet rs = preparedStatement.executeQuery();
		if(rs.next()){
			String guid = rs.getString("GUID");
			ret = guid;
		}
		
		rs.close();
		preparedStatement.close();
		
		return ret;
	}
	
	public int update_wp_posts_parent(Integer postId, Integer parentPost) throws SQLException {
		String sql = "UPDATE wp_posts SET PARENT=? WHERE ID=?";
		preparedStatement = connect.prepareStatement(sql);
		preparedStatement.setInt(1, parentPost);
		preparedStatement.setInt(2, postId);
		
		int ret = preparedStatement.executeUpdate();
		
		preparedStatement.close();
		
		return ret;
		
	}

	public int update_wp_posts_guid(Integer postId, String guid) throws SQLException {
		String sql = "UPDATE wp_posts SET GUID=? WHERE ID=?";
		preparedStatement = connect.prepareStatement(sql);
		preparedStatement.setString(1, guid);
		preparedStatement.setInt(2, postId);
		
		int ret = preparedStatement.executeUpdate();
		
		preparedStatement.close();
		
		return ret;
		
	}
	
	public int get_sdm_category(String taxonomy, String name) throws SQLException{
		int ret = -1;
		String sql = "SELECT A.term_id FROM wp_terms A, wp_term_taxonomy B WHERE A.name=? AND B.taxonomy=? AND A.TERM_ID=B.TERM_ID";
		preparedStatement = connect.prepareStatement(sql);
		preparedStatement.setString(1, name);
		preparedStatement.setString(2, taxonomy);
		
		ResultSet rs = preparedStatement.executeQuery();
		if(rs.next()){
			int smd_category = rs.getInt(1);
			ret = smd_category;
		}
		
		rs.close();
		preparedStatement.close();
		
		return ret;
	}

}
