package accountsmanagement.cli.commands;

import accountsmanagement.Api;
import com.smec.cc.accountsmanagement.ApiClient;
import com.smec.cc.accountsmanagement.ApiException;
import com.smec.cc.accountsmanagement.api.AccountApi;
import com.smec.cc.accountsmanagement.api.EventApi;
import com.smec.cc.accountsmanagement.api.StatisticsApi;
import com.smec.cc.accountsmanagement.model.Account;
import com.smec.cc.accountsmanagement.model.Event;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "add", mixinStandardHelpOptions = true, version = "0.1",
        description = "add accounts")
public class AddAccounts implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", arity = "1..*", description = "List of account names, e.g., acc-1 acc-2 acc-3")
    private String[] accountNames;

    @Override
    public Integer call() throws Exception {
        Api api = Api.getInstance();

        int returnCode = 0;
        for (String accountName : accountNames) {
            try {
                Account account = api.account.addAccount(new Account().name(accountName));
                System.out.printf("Account '%s' created.%n", accountName);
            } catch (ApiException e) {
                if (e.getCode() == 404) {
                    System.err.printf("Account '%s' already exists.%n", accountName);
                }
                else {
                    System.err.printf("HTTP ERROR %d%n", e.getCode());

                }
                returnCode++;
            }
        }
        return returnCode;
    }
}
