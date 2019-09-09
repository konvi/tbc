package konvi.utils.tbc.webwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class WorklogOverviewWebworkAction extends JiraWebActionSupport {
	private static final Logger log = LoggerFactory.getLogger(WorklogOverviewWebworkAction.class);

	@Override
	public String execute() throws Exception {

//        pageBuilderService.assembler().resources()
//                .requireWebResource("com.comsysto.kitchen-duty-plugin:kitchen-duty-plugin-resources")
//                .requireWebResource("com.comsysto.kitchen-duty-plugin:kitchen-duty-plugin-resources--overview-page");

		return "tbc-page-success";
	}
}
