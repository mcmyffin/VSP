package YellowPage;

import java.util.List;
import java.util.Objects;

/**
 * Created by dima on 28.04.16.
 */
public class YellowPageListExpandedDTO {

    private List<YellowPageExpandedDTO> services;

    public YellowPageListExpandedDTO(List<YellowPageExpandedDTO> services) {
        this.services = services;
    }

    public List<YellowPageExpandedDTO> getServices() {
        return services;
    }

    public void setServices(List<YellowPageExpandedDTO> services) {
        this.services = services;
    }
}
