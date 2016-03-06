package com.mk.jira.reporting.model;

import java.util.Date;
import java.util.List;

public class History implements Comparable<History> {
	private Contact author;
	private Date created;
	private List<HistoryItem> items;

	public Contact getAuthor() {
		return author;
	}

	public void setAuthor(Contact author) {
		this.author = author;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public List<HistoryItem> getItems() {
		return items;
	}

	public void setItems(List<HistoryItem> items) {
		this.items = items;
	}

	@Override
	public int compareTo(History that) {
		return that.created.compareTo(this.created);
	}
}
