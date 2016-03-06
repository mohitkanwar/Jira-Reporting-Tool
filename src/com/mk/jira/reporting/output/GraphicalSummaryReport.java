package com.mk.jira.reporting.output;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mk.jira.reporting.model.JiraTicket;

public class GraphicalSummaryReport extends AbstractReport {

	public String getReportBody(List<JiraTicket> tickets) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div id=\"chartContainer\" style=\"height: 350px; width: 100%;\"></div>");
		sb.append("<script type=\"text/javascript\">");
		
		Map<String,Integer> statucCountMap = new HashMap<String, Integer>();
		
		int count = 0;
		for (JiraTicket tkt : tickets) {
			
			if(statucCountMap.containsKey(tkt.getStatus().getName())){
				count = statucCountMap.get(tkt.getStatus().getName());
			}
			else{
				count = 0;
			}
			statucCountMap.put(tkt.getStatus().getName(), count+1);
		}
		sb.append("var totalTickets = " + tickets.size() + ";");
		for(String s:statucCountMap.keySet()){
			sb.append("var "+s.replaceAll(" ", "").toLowerCase()+" = " + statucCountMap.get(s) + ";");
		}
		
		for(String s:statucCountMap.keySet()){
			sb.append("var "+s.replaceAll(" ", "").toLowerCase()+"percent = Math.round("+s.replaceAll(" ", "").toLowerCase()+"*100/totalTickets);");
		}

		for(String s:statucCountMap.keySet()){
			sb.append("var "+s.replaceAll(" ", "").toLowerCase()+"Text = \""+s+"\"+"+s.replaceAll(" ", "").toLowerCase()+"percent+\"%\";");
		}
		for(String s:statucCountMap.keySet()){
			sb.append("var "+s.replaceAll(" ", "").toLowerCase()+"legendText = \""+s+"\";");
		}

		
		sb.append(" var datapoints =[");
		Iterator<String> it = statucCountMap.keySet().iterator();
		while(it.hasNext()){
			String s = it.next();
			sb.append("{y:"+s.replaceAll(" ", "").toLowerCase()+"percent, legendText:"+s.replaceAll(" ", "").toLowerCase()+"legendText}");
			if(it.hasNext()){
				sb.append(",");
			}
		}
		
		sb.append("];");

		sb.append(" var chartTitle= \"Tickets Status on " + new Date() + "\";");
		sb.append("var chart = new CanvasJS.Chart(\"chartContainer\",");
		sb.append("{");
		sb.append("title:{");
		sb.append("text: chartTitle,");
		sb.append("fontFamily: \"Impact\",");
		sb.append("fontWeight: \"normal\"");
		sb.append("},");
		sb.append("exportFileName: chartTitle,");
		sb.append("exportEnabled: true,");
		sb.append("animationEnabled: true,");
		sb.append("legend:{");
		sb.append("verticalAlign: \"center\",");
		sb.append("horizontalAlign: \"right\",");
		sb.append("fontSize: 20,");
		sb.append("fontFamily: \"Helvetica\"");
		sb.append("},");
		sb.append(" data: [");
		sb.append("{");
		sb.append("indexLabelFontSize: 18,");
		sb.append("indexLabelFontFamily: \"Garamond\",");
		sb.append("indexLabelFontColor: \"darkgrey\",");
		sb.append("indexLabelLineColor: \"darkgrey\",");
		sb.append("indexLabelPlacement: \"outside\",");
		sb.append("type: \"pie\",");
		sb.append("showInLegend: true,");
		sb.append("dataPoints: datapoints");
		sb.append("}");
		sb.append("]");
		sb.append("});");

		sb.append("chart.render();");
		sb.append("</script>");

		return sb.toString();
	}

	@Override
	public String getReportName() {
		return "Summary";
	}

	@Override
	public String getReportPageName() {
		return "graph.html";
	}
}
