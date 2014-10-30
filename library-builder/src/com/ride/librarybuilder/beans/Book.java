package com.ride.librarybuilder.beans;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Book {
	private Integer id;
	private String uuid;
	private String title;
	private String author;
	private String comments;
	private Integer size;
	private Date publishDate;
	private String coverImg;
	private ArrayList<String> tags;
	private ArrayList<String> formats;

	// private ArrayList<File> files;

	public Book() {
		super();
	}

	public Book(Integer id, String uuid, String title, String author,
			String comments, Integer size, String publishDate, String coverImg,
			ArrayList<String> tags, ArrayList<String> formats) {

		super();
		this.id = id;
		this.uuid = uuid;
		this.title = title;
		this.author = author;
		this.comments = comments;
		this.size = size;
		
		try {
			String d = publishDate.substring(0,10);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
			this.publishDate = sdf.parse(publishDate);
			
		} catch (ParseException e) {
		}
		this.coverImg = coverImg;
		this.tags = tags;
		this.formats = formats;
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	public ArrayList<String> getFormats() {
		return formats;
	}

	public void setFormats(ArrayList<String> formats) {
		this.formats = formats;
	}

	public String getCoverImg() {
		return coverImg;
	}

	public void setCoverImg(String coverImg) {
		this.coverImg = coverImg;
	}

}
