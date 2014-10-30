package com.ride.librarybuilder.database;

public class WpTermBean {

	private Integer termId;
	private String name;
	private String slug;
	private Integer termGroup;
	
	public Integer getTermId() {
		return termId;
	}
	public void setTermId(Integer termId) {
		this.termId = termId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	public Integer getTermGroup() {
		return termGroup;
	}
	public void setTermGroup(Integer termGroup) {
		this.termGroup = termGroup;
	}
	
}
