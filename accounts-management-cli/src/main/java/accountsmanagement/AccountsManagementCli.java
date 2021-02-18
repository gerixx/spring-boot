package accountsmanagement;

import com.smec.cc.accountsmanagement.ApiClient;
import com.smec.cc.accountsmanagement.ApiException;
import com.smec.cc.accountsmanagement.api.AccountApi;
import com.smec.cc.accountsmanagement.api.EventApi;
import com.smec.cc.accountsmanagement.api.StatisticsApi;
import com.smec.cc.accountsmanagement.model.Account;
import com.smec.cc.accountsmanagement.model.Event;

import java.util.Arrays;

public class AccountsManagementCli {

    public static void main(String[] args) throws ApiException {
        ApiClient apiClient = new ApiClient().setBasePath("http://localhost:8080/v1");
        AccountApi accountApi = new AccountApi(apiClient);
        EventApi eventApi = new EventApi(apiClient);
        StatisticsApi statisticsApi = new StatisticsApi(apiClient);

        Account account = accountApi.addAccount(new Account().name("Account-1"));
        eventApi.createEventsForAccountId(account.getId(),
                Arrays.asList(
                        new Event().type("Event-1")
                                   .timestamp(System.currentTimeMillis()),
                        new Event().type("Event-2")
                                   .timestamp(System.currentTimeMillis()),
                        new Event().type("Event-3")
                                   .timestamp(System.currentTimeMillis())
                ));
        String statistics = statisticsApi.getStatistics(account.getName());
        System.out.println(statistics);
    }

}
