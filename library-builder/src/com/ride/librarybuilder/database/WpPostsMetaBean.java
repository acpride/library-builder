package com.ride.librarybuilder.database;

public class WpPostsMetaBean {
	
	private Integer metaId;
	private Integer postId;
	private String meta_key;
	private String meta_value;
	
	public Integer getMetaId() {
		return metaId;
	}
	public void setMetaId(Integer metaId) {
		this.metaId = metaId;
	}
	public Integer getPostId() {
		return postId;
	}
	public void setPostId(Integer postId) {
		this.postId = postId;
	}
	public String getMeta_key() {
		return meta_key;
	}
	public void setMeta_key(String meta_key) {
		this.meta_key = meta_key;
	}
	public String getMeta_value() {
		return meta_value;
	}
	public void setMeta_value(String meta_value) {
		this.meta_value = meta_value;
	}
	
}
