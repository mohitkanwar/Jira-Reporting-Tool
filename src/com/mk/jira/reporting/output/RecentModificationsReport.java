package com.mk.jira.reporting.output;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mk.jira.reporting.model.Comment;
import com.mk.jira.reporting.model.History;
import com.mk.jira.reporting.model.HistoryItem;
import com.mk.jira.reporting.model.JiraTicket;

public class RecentModificationsReport extends AbstractReport{

	@Override
	public String getReportBody(List<JiraTicket> tickets) {
		StringBuilder sb = new StringBuilder();
		for(JiraTicket tk:tickets){
			boolean hasChange = false;
			StringBuilder body = new StringBuilder();
			for(History hs :tk.getChangelog()){
				if(((long)new Date().getTime() -  hs.getCreated().getTime())<1000*60*60*24*2){
					hasChange = true;
				
					for(HistoryItem item:hs.getItems()){
						if(getDetailedDescriptionFieldsList().contains(item.getField())){
							body.append(item.getField() +" has been modified from [" + item.getFromString()+"] to ["+item.getToString()+"].<br>");
						}
						else if(getMinimalChangeNotificationFieldsList().contains(item.getField())){
							body.append(item.getField() +" has been modified.<br>");
						}
						else{
							body.append(item.getField() +" has been modified or added as ["+item.getToString()+"].<br>");
						}
					}
				}
			}
			List<Comment> comments =tk.getComments();
			for(Comment comment:comments){
				
				if(((long)new Date().getTime() -  comment.getCreateDate().getTime())<1000*60*60*24*2){
					hasChange = true;
					body.append("New Comment by <b>"+comment.getAuthor().getName()+ "</b><br>");
					body.append("<hr>");
					body.append(comment.getBody());
					body.append("<hr>");
				}
			}
			
			if(hasChange){
				sb.append("<div class=\"row\">");
					sb.append("<div class=\"panel panel-primary\"><div class=\"panel-heading\">");
						sb.append(tk.getKey()+" : "+tk.getSummary());
						sb.append("</div><div class=\"panel-body\">");
						sb.append(body);
						sb.append("</div>");
					sb.append("</div>");
				sb.append("</div>");
			
			}
		}
		
		return sb.toString();
	}

	@Override
	public String getReportName() {
		return "Recent Changes";
	}

	@Override
	public String getReportPageName() {
		return "today.html";
	}
	
	private List<String> getDetailedDescriptionFieldsList(){
		List<String> list = new ArrayList<String>();
		list.add("status");
		list.add("assignee");
		return list;
	}
	private List<String> getMinimalChangeNotificationFieldsList(){
		List<String> list = new ArrayList<String>();
		list.add("description");
		return list;
	}
	

}
