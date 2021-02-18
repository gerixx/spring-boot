package accountsmanagement.cli.commands;

import accountsmanagement.Api;
import com.smec.cc.accountsmanagement.ApiException;
import com.smec.cc.accountsmanagement.model.Account;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "update", mixinStandardHelpOptions = true, version = "0.1",
        description = "Updates the name of an account")
public class UpdateAccount implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", arity="2", description = "needed are the account name to change and the new account name")
    private String[] oldNewAccountName;

    @Override
    public Integer call() throws Exception {
        Api api = Api.getInstance();

        String currentName = oldNewAccountName[0];
        String newName = oldNewAccountName[1];

        try {
            Account account = api.account.findAccountByName(currentName);
            account.setName(newName);
            api.account.updateAccount(account);
            System.out.printf("Changed account name '%s' to '%s'%n", currentName, newName);
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                System.err.printf("Account '%s' not found.%n", currentName);
            } else {
                System.err.printf("HTTP ERROR %d%n", e.getCode());
                e.printStackTrace(System.err);
            }
            return 1;
        }
        return 0;
    }
}
