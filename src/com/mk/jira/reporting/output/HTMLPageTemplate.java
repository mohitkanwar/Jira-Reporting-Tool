package com.mk.jira.reporting.output;

import java.io.File;
import java.util.List;

public class HTMLPageTemplate {
	private List<AbstractReport> reports;
	public HTMLPageTemplate(List<AbstractReport> reports) {
		this.reports = reports;
	}
	private final String HTML_START = "<html>";

	private final String HEAD = "<head><title> Report </title>"
			+ "<link href=\"../../.."
			+ File.separator
			+ "ui"
			+ File.separator
			+ "css/bootstrap.min.css\" rel=\"stylesheet\">"
			+ "<link href=\"../../.."
			+ File.separator
			+ "ui"
			+ File.separator
			+ "css/styles.css\" rel=\"stylesheet\">"
			+ "<link href=\"../../.."
			+ File.separator
			+ "ui"
			+ File.separator
			+ "css/bootstrap-datepicker3.standalone.min.css\" rel=\"stylesheet\">"
			+ "<script src=\"../../.."
			+ File.separator
			+ "ui"
			+ File.separator
			+ "js/jquery-2.2.1.min.js\"></script><script src=\"../../.."+ File.separator
			+ "ui"
			+ File.separator+"js/bootstrap.min.js\"></script>"
			+ "<script src=\"../../.."
			+ File.separator
			+ "ui"
			+ File.separator
			+ "js/canvasjs.min.js\"></script>"
			+ "<script src=\"../../.."
			+ File.separator
			+ "ui"
			+ File.separator
			+ "js/bootstrap-datepicker.min.js\"></script></head>";
	private final String BODY_START = "<body>";



	private String getNavBar(String pagename) {
		StringBuilder sb = new StringBuilder(
				"<nav class=\"navbar navbar-default\"> <div class=\"container-fluid\">"
						+ "<div class=\"navbar-header\">"
						+ "<button type=\"button\" class=\"navbar-toggle collapsed\" data-toggle=\"collapse\" data-target=\"#bs-example-navbar-collapse-1\" aria-expanded=\"false\">"
						+ "<span class=\"sr-only\">Toggle navigation</span>"
						+ "<span class=\"icon-bar\"></span>"
						+ "<span class=\"icon-bar\"></span>"
						+ "<span class=\"icon-bar\"></span>"
						+ "</button>"
						+ "<a class=\"navbar-brand\" href=\"#/\">Report</a>"
						+ "</div>"
						+ "<div class=\"collapse navbar-collapse\" id=\"bs-example-navbar-collapse-1\">"
						+ "<ul class=\"nav navbar-nav\">" );
		for(AbstractReport report : reports){
			sb.append("<li ");
			if (pagename.equals(report.getReportName())) {
				sb.append("class=\"active\">");
			} else {
				sb.append(">");
			}
			sb.append("<a href=\"");
			sb.append(report.getReportPageName());
			sb.append("\">");
			sb.append(report.getReportName());
			sb.append("</a></li>");
		}


		sb.append("</ul>" + "</div>" + "</div>" + "</nav>");
		return sb.toString();
	}

	private final String BODY_CONTAINER_START = "<div class=\"container\">";
	private final String BODY_CONTAINER_END = "</div>";
	private final String BODY_ENDS = "</body>";
	private final String HTML_ENDS = "</html>";

	public String getPage(String body, String pagename) {
		return HTML_START + HEAD + BODY_START + getNavBar(pagename)
				+ BODY_CONTAINER_START + body + BODY_CONTAINER_END + BODY_ENDS
				+ HTML_ENDS;
	}

}
