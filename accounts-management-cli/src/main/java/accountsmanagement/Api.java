package accountsmanagement;

import com.smec.cc.accountsmanagement.ApiClient;
import com.smec.cc.accountsmanagement.api.AccountApi;
import com.smec.cc.accountsmanagement.api.EventApi;
import com.smec.cc.accountsmanagement.api.StatisticsApi;

public class Api {

    private static Api api;

    public static Api getInstance() {
        if (api == null) {
            synchronized (Api.class) {
                if (api == null) {
                    api = new Api();
                }
            }
        }
        return api;
    }

    private Api() {}

    public final ApiClient apiClient = new ApiClient();
    public final AccountApi account = new AccountApi(apiClient);
    public final EventApi event = new EventApi(apiClient);
    public final StatisticsApi statistics = new StatisticsApi(apiClient);

    public void setBasePath(String path) {
        apiClient.setBasePath(path);
    }

}
