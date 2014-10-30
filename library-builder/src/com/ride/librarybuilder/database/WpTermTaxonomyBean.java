package com.ride.librarybuilder.database;

public class WpTermTaxonomyBean {
	private Integer termTaxonomyId;
	private Integer termId;
	private String taxonomy;
	private String description;
	private Integer parent;
	private Integer count;
	
	public Integer getTermTaxonomyId() {
		return termTaxonomyId;
	}
	public void setTermTaxonomyId(Integer termTaxonomyId) {
		this.termTaxonomyId = termTaxonomyId;
	}
	public Integer getTermId() {
		return termId;
	}
	public void setTermId(Integer termId) {
		this.termId = termId;
	}
	public String getTaxonomy() {
		return taxonomy;
	}
	public void setTaxonomy(String taxonomy) {
		this.taxonomy = taxonomy;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getParent() {
		return parent;
	}
	public void setParent(Integer parent) {
		this.parent = parent;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
	
}
