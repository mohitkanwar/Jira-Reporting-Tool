package com.mk.jira.reporting.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mk.jira.reporting.FileHandler;
import com.mk.jira.reporting.model.JiraTicket;

public class HTMLReport {
	public void paint(List<JiraTicket> tickets) throws IOException{
		List<AbstractReport> reports = getReportsList();
		HTMLPageTemplate template = new HTMLPageTemplate(reports);
		FileHandler fh = new FileHandler();
		for(AbstractReport report:reports){
			fh.createFile(report.getReport(template, tickets), report.getReportPageName());
		}
	}

	private List<AbstractReport> getReportsList() {
		List<AbstractReport> reports = new ArrayList<AbstractReport>();
		reports.add(new BaseReport());
		reports.add(new ProgressReport());
		reports.add(new ProgressChart());
		reports.add(new GraphicalSummaryReport());
		reports.add(new RecentModificationsReport());
		return reports;
	}
}
