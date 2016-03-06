package com.mk.jira.reporting.output;

import java.util.List;

import com.mk.jira.reporting.model.JiraTicket;

public abstract class AbstractReport {

	public abstract String getReportBody(List<JiraTicket> tickets);
	public abstract String getReportName();
	public abstract String getReportPageName();
	public String getReport(HTMLPageTemplate template,List<JiraTicket> tickets){
		return template.getPage(getReportBody(tickets),getReportName());
	}
}
