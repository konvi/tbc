package com.atlassian.tutorial.helloworld;

import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.issue.worklog.WorklogManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Named
public class MyPluginServlet extends HttpServlet {
//	private static final Logger log = LoggerFactory.getLogger(MyPluginServlet.class);

//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		resp.setContentType("text/html");
//		resp.getWriter().write("<html><body>Hello! You did it.</body></html>");
//
//	}


	//private static final String PLUGIN_STORAGE_KEY = "com.atlassian.plugins.tutorial.refapp.adminui";
//	@ComponentImport
	private final WorklogManager worklogManager;
//	@ComponentImport
//	private final LoginUriProvider loginUriProvider;
//	@ComponentImport
//	private final TemplateRenderer templateRenderer;
//	@ComponentImport
//	private final PluginSettingsFactory pluginSettingsFactory;

	@Inject
	public MyPluginServlet(WorklogManager worklogManager) {
		this.worklogManager = worklogManager;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {


		Long since = LocalDate.now().minusDays(3L).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
		List<Worklog> worklogs = worklogManager.getWorklogsUpdatedSince(since);

		response.setContentType("text/html");


		for (Worklog worklog : worklogs) {
			response.getWriter().write(" " + worklog.getAuthorObject().getDisplayName() + " " +
					worklog.getIssue() + "  " + worklog.getTimeSpent() + "\r\n");
		}

	}

//	@Override
//	protected void doPost(HttpServletRequest req, HttpServletResponse response)
//			throws ServletException, IOException {
//		PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
//		pluginSettings.put(PLUGIN_STORAGE_KEY + ".name", req.getParameter("name"));
//		pluginSettings.put(PLUGIN_STORAGE_KEY + ".age", req.getParameter("age"));
//		response.sendRedirect("test");
//	}
}
