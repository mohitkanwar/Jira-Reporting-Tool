package com.mk.jira.reporting.output;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.mk.jira.reporting.model.History;
import com.mk.jira.reporting.model.HistoryItem;
import com.mk.jira.reporting.model.JiraTicket;

public class ProgressReport extends AbstractReport{



	private DateFormat df = new SimpleDateFormat("dd MM yyyy");

	public String getReportBody(List<JiraTicket> tickets) {
		StringBuilder pageContents = new StringBuilder();

		Collections.sort(tickets);

		Iterator<JiraTicket> it = tickets.iterator();
		while (it.hasNext()) {
			JiraTicket tkt = it.next();
			List<History> changelog = tkt.getChangelog();
			Collections.sort(changelog);
			Collections.reverse(changelog);
			
			
			Predicate<History> onlyStatusChanges = new Predicate<History>() {

				@Override
				public boolean test(History t) {
					List<HistoryItem> items = t.getItems();
					Iterator<HistoryItem> itemsIterator = items.iterator();
					while (itemsIterator.hasNext()) {
						HistoryItem item = itemsIterator.next();

						if (item.getField().equals("status")) {
							return true;
						}
					}
					return false;
				}
			};
			List<History> filteredLog = changelog.stream().filter(onlyStatusChanges ).collect(Collectors.toList());
			Date startDate = tkt.getCreated();
			Date endDate;
			if(filteredLog.size()==0){
				endDate=new Date();
			}
			else{
				endDate= filteredLog.get(0).getCreated();
			}
			
			
			
			StringBuilder opentag = new StringBuilder();
			 
			opentag
			.append("<span  title=\"From "
					+ df.format(startDate)
					+ " to "
					+ df.format(endDate) + "\">");
		
			
			opentag.append(getBadge("Open ",startDate,endDate));
	
			opentag.append("</span>");
			StringBuilder pc = new StringBuilder();
			
			for (int iCLog = 0; iCLog < filteredLog.size(); iCLog++) {
				History h = filteredLog.get(iCLog);

				List<HistoryItem> items = h.getItems();
				Iterator<HistoryItem> itemsIterator = items.iterator();
				while (itemsIterator.hasNext()) {
					HistoryItem item = itemsIterator.next();

					if (item.getField().equals("status")) {
						startDate = h.getCreated();
						if (iCLog < filteredLog.size() - 1) {
							endDate = filteredLog.get(iCLog + 1).getCreated();
						} else {
							endDate = new Date();
						}
						opentag.append(getToBadge());
						opentag
								.append("<span  title=\"From "
										+ df.format(startDate)
										+ " to "
										+ df.format(endDate) + "\">");
						opentag.append(getBadge(item.getToString(),startDate,endDate));
						//appendButton(pc1, item,startDate,endDate);
						opentag.append("</span>");
					
					}
				}
				
			}
			pc.append("<div class=\"row\">");
			pc
					.append("<div class=\"panel panel-primary\"><div class=\"panel-heading\">");
			pc.append(tkt.getKey()+" is in status ["+tkt.getStatus().getName()+"] since last"+getBadge("", startDate,  endDate)+"days.");
			pc.append("</div><div class=\"panel-body\">");
			
			
			pc.append(opentag);
			pageContents.append(pc);
			pageContents.append("</div></div></div>");
			
		}

		return pageContents.toString();
	}

	private String getBadge(String status ,Date startDate, Date endDate) {
		String badge = "<span class=\"badge\">"+status+" (";
		badge = badge
				+ (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
		badge = badge + ")</span>";
		return badge;
	}
	private String getToBadge(){
		return "<span><a href=\"#\" aria-label=\"to\"><span aria-hidden=\"true\">&rarr;</span></a></span>";
	}
	

	@Override
	public String getReportName() {
		return "Progress";
	}

	@Override
	public String getReportPageName() {
		return "progress.html";
	}

}
