package konvi.utils.tbc.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class WorklogOverviewPageWebworkAction extends JiraWebActionSupport {
	private static final Logger log = LoggerFactory.getLogger(WorklogOverviewPageWebworkAction.class);

	@Inject
	private PageBuilderService pageBuilderService;

	@Override
	public String execute() throws Exception {
		pageBuilderService.assembler().resources()
				.requireWebResource("konvi.utils.tbc:tbc-plugin-resources--overview-page");
//				.requireWebResource("com.comsysto.kitchen-duty-plugin:kitchen-duty-plugin-resources--overview-page");

//		return "kitchen-duty-overview-page-success";
		return "tbc-page-success";
	}

	public void setPageBuilderService(PageBuilderService pageBuilderService) {
		this.pageBuilderService = pageBuilderService;
	}
}
