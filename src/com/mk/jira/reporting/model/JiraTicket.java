package com.mk.jira.reporting.model;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.mk.jira.sort.JiraTicketStatusSortUtil;


public class JiraTicket implements Comparable<JiraTicket>{
	
	
	private	String summary;
	private String key ;
	private String lastupdated ;
	private String description;
	private Status status ;
	private String detailsLink;
	private String jiraIssueId;
	private String issuetype;
	private String reporter;
	private Date created;
	private List<Comment>comments;
	private String jiraLink;
	private Contact assignee;
	private List<History> changelog;

	
	public List<History> getChangelog() {
		return changelog;
	}
	public void setChangelog(List<History> changelog) {
		this.changelog = changelog;
	}
	public Contact getAssignee() {
		return assignee;
	}
	public void setAssignee(Contact assignee) {
		this.assignee = assignee;
	}
	public String getJiraIssueId() {
		return jiraIssueId;
	}
	public void setJiraIssueId(String jiraIssueId) {
		this.jiraIssueId = jiraIssueId;
	}
	public String getIssuetype() {
		return issuetype;
	}
	public void setIssuetype(String issuetype) {
		this.issuetype = issuetype;
	}
	public String getReporter() {
		return reporter;
	}
	public void setReporter(String reporter) {
		this.reporter = reporter;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date date) {
		this.created = date;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public String getJiraLink() {
		return jiraLink;
	}
	public void setJiraLink(String jiraLink) {
		this.jiraLink = jiraLink;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getLastupdated() {
		return lastupdated;
	}
	public void setLastupdated(String lastupdated) {
		this.lastupdated = lastupdated;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status statusObj) {
		this.status = statusObj;
	}
	public String getDetailsLink() {
		return detailsLink;
	}
	public void setDetailsLink(String detailsLink) {
		this.detailsLink = detailsLink;
	}
	@Override
	public int compareTo(JiraTicket o) {
		JiraTicketStatusSortUtil sortUtil = new JiraTicketStatusSortUtil();
			return sortUtil.getSortOrder(status.getName()).compareTo(sortUtil.getSortOrder(o.status.getName()));
	}
	
	public Date getDateForCurrentStatus() {
		Collections.sort(changelog);
		 Iterator<History> it =changelog.iterator();
		 Date sinceDate = new Date();
		 while(it.hasNext()){
			 History h = it.next();
			 List<HistoryItem> items = h.getItems();
			 Iterator<HistoryItem> itemsIterator = items.iterator();
			 while(itemsIterator.hasNext()){
				 HistoryItem item = itemsIterator.next();
				 if(status.getName().equals("Open")){
					 return created;
				 }
				 if(item.getField().equals("status")&&item.getToString().equals(status.getName())){
					 sinceDate = h.getCreated();
					 break;
				 }
			 }
		 }
		 return sinceDate;
		
	}
	
}
