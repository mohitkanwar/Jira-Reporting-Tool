package com.mk.jira.reporting.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Comment implements Comparable<Comment> {
	public final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss");
	private Contact author;
	private String body;
	private Date createDate;
	private Date updateDate;

	public Contact getAuthor() {
		return author;
	}

	public void setAuthor(Contact author) {
		this.author = author;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) throws ParseException {
		this.createDate = DATE_FORMAT.parse(createDate);
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) throws ParseException {
		this.updateDate = DATE_FORMAT.parse(updateDate);
	}

	@Override
	public int compareTo(Comment that) {
		return that.createDate.compareTo(this.createDate);
	}
}
