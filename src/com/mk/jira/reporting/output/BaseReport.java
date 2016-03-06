package com.mk.jira.reporting.output;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mk.jira.reporting.model.Comment;
import com.mk.jira.reporting.model.JiraTicket;

public class BaseReport extends AbstractReport {


	@Override
	public String getReportBody(List<JiraTicket> tickets) {
		DateFormat df = new SimpleDateFormat("dd MMM yyyy");
		StringBuilder pageContents = new StringBuilder("<h3>");
		pageContents.append("Status as of ");
		pageContents.append(df.format(new Date()));
		pageContents.append("</h3>");
		//populateTableStart(pageContents);
		Collections.sort(tickets);
		Iterator<JiraTicket> it = tickets.iterator();
		Map<String,StringBuilder> statusTicketsRowMap = new LinkedHashMap<String,StringBuilder>();
		int i = 0;
		while (it.hasNext()) {
			JiraTicket tkt = it.next();
			StringBuilder builder;
			if(statusTicketsRowMap.containsKey(tkt.getStatus().getName())){
				builder=statusTicketsRowMap.get(tkt.getStatus().getName());
			}
			else{
				builder= new StringBuilder();
				i=0;
			}
			i = populateTicketData(df, builder, i, tkt);
			statusTicketsRowMap.put(tkt.getStatus().getName(),builder);
		}
		pageContents.append("<ul class=\"nav nav-tabs\">");
		boolean first = true;
		for(String status:statusTicketsRowMap.keySet()){
			pageContents.append("<li");
			if(first){
				pageContents.append(" class=\"active\"");
				first = false;
			}
			pageContents.append(">");
			pageContents.append("<a data-toggle=\"tab\" href=\"#");
			pageContents.append(status.replaceAll(" ", "").toLowerCase());
			pageContents.append("\" >");
			pageContents.append(status);
			pageContents.append("</a></li>");
		}
		pageContents.append("</ul>");
		pageContents.append("<div class=\"tab-content\">");
		first=true;
		for(String status:statusTicketsRowMap.keySet()){
			
					pageContents.append("<div id=\"");
					pageContents.append(status.replaceAll(" ", "").toLowerCase());
					pageContents.append("\" class=\"tab-pane fade ");
					if(first){
						pageContents.append("in active");
						first = false;
					}
					pageContents.append("\">");
					pageContents.append("<h3>");
					pageContents.append(status);
					pageContents.append("</h3>");
					pageContents.append("<p>");
					populateTableStart(pageContents);
					pageContents.append(statusTicketsRowMap.get(status));
					populateTableEnd(pageContents);
					pageContents.append("</p>");
					pageContents.append("</div>");
				
		}
		pageContents.append("</div>");
		
		pageContents.append("<script>");
		pageContents.append("function toggleTab(tabId){alert(tabId);}");
		pageContents.append("</script>");
		//populateTableEnd(pageContents);
		return pageContents.toString();

	}

	private int populateTicketData(DateFormat df, StringBuilder pageContents,
			int i, JiraTicket tkt) {
		pageContents.append("<tr>");
		pageContents.append("<td>" + ++i + "</td>");
		pageContents.append("<td><a href=\"" + tkt.getJiraLink()
				+ "\" target=\"_blank\">" + tkt.getKey() + "</a></td>");
		pageContents.append("<td title=\"" + tkt.getSummary() + "\">"
				+ tkt.getSummary() + "</td>");
		pageContents.append("<td title=\"" + tkt.getStatus().getName()
				+ " since " + df.format(tkt.getDateForCurrentStatus())
				+ "\">" + tkt.getStatus().getName() + " since "
				+ df.format(tkt.getDateForCurrentStatus()) + "</td>");
		pageContents.append("<td><a href=\"mailto:"
				+ tkt.getAssignee().getEmailAddress() + "\">"
				+ tkt.getAssignee().getName() + "</a></td>");
		pageContents.append("<td><div class=\"scrollable\">");
		List<Comment> comments = tkt.getComments();
		Collections.sort(comments);
		Iterator<Comment> ci = comments.iterator();
		while (ci.hasNext()) {
			Comment c = ci.next();
			pageContents.append("<div class=\"panel panel-primary\" >");
			pageContents.append("<div class=\"panel-heading\">");
			pageContents.append("<h3 class=\"panel-title\">"
					+ c.getAuthor().getName() + " on "
					+ df.format(c.getCreateDate()) + "</h3>");
			pageContents.append("</div>");
			pageContents.append("<div class=\"panel-body\">");
			pageContents.append(c.getBody());
			pageContents.append(" </div></div>");
		}
		pageContents.append("</div></td>");
		pageContents.append("</tr>");
		return i;
	}

	private void populateTableEnd(StringBuilder pageContents) {
		pageContents.append("</table>");
	}

	private void populateTableStart(StringBuilder pageContents) {
		pageContents
				.append("<table class=\"table table-hover table-bordered\" id=\"ticketsTable\">");
		pageContents
				.append("<th><td><b>Ticket Id</b></td><td><b>Title</b></td><td><b>Status</b></td><td><b>Assignee</b></td><td><b>Comments</b></td></th>");
	}

	@Override
	public String getReportName() {
		return "Base Report";
	}

	@Override
	public String getReportPageName() {
		return "report.html";
	}
}
