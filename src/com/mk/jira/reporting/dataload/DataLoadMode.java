package com.mk.jira.reporting.dataload;

import com.mk.jira.reporting.model.JiraTicket;

public interface DataLoadMode {

	public String getJiraBoard();

	public String getTicketJson(JiraTicket ticket);

}
