package konvi.utils.tbc.rest;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorklogOverviewPageResourceModel {

    @XmlElement(name = "item")
    private String item;
    @XmlElement
    private Long estimation;
    @XmlElement
    private List<String> devs;
    @XmlElement
    private Map<String, Long> loggedByDev;

    public WorklogOverviewPageResourceModel() {
    }

    public WorklogOverviewPageResourceModel(String item,
            Long estimation, Map<String, Long> loggedByDev, List<String> devs) {
        this.item = item;
        this.estimation = estimation;
        this.loggedByDev = loggedByDev;
        this.devs = devs;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Long getEstimation() {
        return estimation;
    }

    public void setEstimation(Long estimation) {
        this.estimation = estimation;
    }

    public List<String> getDevs() {
        return devs;
    }

    public void setDevs(List<String> devs) {
        this.devs = devs;
    }

    public Map<String, Long> getLoggedByDev() {
        return loggedByDev;
    }

    public void setLoggedByDev(Map<String, Long> loggedByDev) {
        this.loggedByDev = loggedByDev;
    }
}