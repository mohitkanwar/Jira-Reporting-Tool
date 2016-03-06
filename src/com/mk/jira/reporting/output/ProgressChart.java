package com.mk.jira.reporting.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.mk.jira.reporting.model.History;
import com.mk.jira.reporting.model.HistoryItem;
import com.mk.jira.reporting.model.JiraTicket;
import com.mk.jira.sort.JiraTicketStatusSortUtil;

public class ProgressChart extends AbstractReport{


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
			List<History> filteredLog = changelog.stream()
					.filter(onlyStatusChanges).collect(Collectors.toList());
			Date startDate = tkt.getCreated();
			Date endDate;
			if (filteredLog.size() == 0) {
				endDate = new Date();
			} else {
				endDate = filteredLog.get(0).getCreated();
			}

			Map<String, Integer> statusDaysMap = new HashMap<String, Integer>();

			statusDaysMap.put("Open", getDateDifference(startDate, endDate));

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
						int count = 0;
						if (statusDaysMap.containsKey(item.getToString())) {
							count = statusDaysMap.get(item.getToString());
						}
						statusDaysMap.put(item.getToString(), count
								+ getDateDifference(startDate, endDate));
					}
				}

			}
			StringBuilder panelRow = new StringBuilder("<div class=\"row\">");
			panelRow.append("<div class=\"panel panel-primary\"><div class=\"panel-heading\">");
			panelRow.append(tkt.getKey() + " is in status ["
					+ tkt.getStatus().getName() + "] since last"
					+ getDateDifference(startDate, endDate) + "days.</div>");
			panelRow.append("<div class=\"panel-body\">");
			panelRow.append("<div id=\"chartContainer" + tkt.getJiraIssueId()
					+ "\" style=\"height: 300px; width: 100%;\"></div>");

			panelRow.append("<script type=\"text/javascript\">");

			panelRow.append("	var chart" + tkt.getJiraIssueId()
					+ " = new CanvasJS.Chart(\"chartContainer"
					+ tkt.getJiraIssueId() + "\",");
			panelRow.append("	{");
			panelRow.append("		animationEnabled: true,");
			panelRow.append("		title:{");
			panelRow.append("			text: \"" + tkt.getKey() + "\"");
			panelRow.append("		},");
			panelRow.append("		data: [");
			panelRow.append("		{");
			panelRow.append("			type: \"column\","); // change type to bar,
														// line, area, pie, etc
			panelRow.append("			dataPoints: [");
			 List<String> list = new ArrayList<String>();
			 list.addAll(statusDaysMap.keySet());
			 Comparator<String> c = new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					
					Integer o1Order= getSortOrder(o1);
					Integer o2Order= getSortOrder(o2);
					return o1Order.compareTo(o2Order);
				}
			 private int getSortOrder(String status){
				 
				 JiraTicketStatusSortUtil util = new JiraTicketStatusSortUtil();
				 return util.getLifeSortOrder(status);
				}
			 
			};
			Collections.sort(list, c );
			Iterator<String> itsdm = list.iterator();
			while (itsdm.hasNext()) {
				String key = itsdm.next();
				panelRow.append("{ label: \"" + key + "\", y: "
						+ statusDaysMap.get(key) + ",color: \"" + getColour(key)
						+ "\"},");
			}

			panelRow.append("]");
			panelRow.append("}");
			panelRow.append("]");
			panelRow.append("});");

			panelRow.append("chart" + tkt.getJiraIssueId() + ".render();");
			panelRow.append("chart" + tkt.getJiraIssueId() + " = {};");

			panelRow.append("</script>");

			panelRow.append("</div>");
			panelRow.append("</div>");
			panelRow.append("</div>");
			pageContents.append(panelRow);

		}

		return pageContents.toString();
	}

	private String getColour(String status) {
		JiraTicketStatusSortUtil util = new JiraTicketStatusSortUtil();
		return util.getStatusColour(status);
	}

	private int getDateDifference(Date startDate, Date endDate) {
		return (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
	}

	@Override
	public String getReportName() {
		return "Progress Chart";
	}

	@Override
	public String getReportPageName() {
		return "pchart.html";
	}

}
