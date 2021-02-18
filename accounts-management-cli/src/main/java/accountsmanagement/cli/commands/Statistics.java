package accountsmanagement.cli.commands;

import accountsmanagement.Api;
import com.smec.cc.accountsmanagement.ApiException;
import com.smec.cc.accountsmanagement.model.Account;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "stats", mixinStandardHelpOptions = true, version = "0.1",
        description = "Retrieve statistics for accounts")
public class Statistics implements Callable<Integer> {

    @CommandLine.Option(names = {"-raw"}, description = "Prints raw list of events for every account")
    private boolean raw;

    @CommandLine.Parameters(index = "0", arity="1..*", description = "List of account names, e.g., acc-1 acc-2 acc-3")
    private String[] accountNames;

    @Override
    public Integer call() throws Exception {
        Api api = Api.getInstance();

        int returnCode = 0;
        for (String accountName : accountNames) {
            try {
                if (raw) {
                    String events = api.event.getEventsForAccountName(accountName);
                    System.out.printf("Account '%s': raw events:%n", accountName);
                    System.out.println(events);
                } else {
                    String statistics = api.statistics.getStatistics(accountName);
                    System.out.printf("Account '%s': statistics:%n", accountName);
                    System.out.println(statistics);
                }
            } catch (ApiException e) {
                if (e.getCode() == 404) {
                    System.err.printf("Account '%s' not found.%n", accountName);
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
