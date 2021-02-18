package accountsmanagement.cli.commands;

import accountsmanagement.Api;
import com.smec.cc.accountsmanagement.ApiException;
import com.smec.cc.accountsmanagement.model.Account;
import com.smec.cc.accountsmanagement.model.Event;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "event", mixinStandardHelpOptions = true, version = "0.1",
        description = "Send events for an account")
public class RaiseEvents implements Callable<Integer> {

    @CommandLine.Option(names = {"-account"}, required = true, description = "Account name")
    private String accountName;

    @CommandLine.Parameters(index = "0", arity = "1..*", description = "List of events, e.g., even-1 event-2 acc-3")
    private String[] eventTypes;

    @Override
    public Integer call() throws Exception {
        Api api = Api.getInstance();

        final Long accountId;
        try {
            accountId = api.account.findAccountByName(accountName)
                                   .getId();
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                System.err.printf("Account '%s' not found.%n", accountName);
            } else {
                System.err.printf("HTTP ERROR %d%n", e.getCode());
            }
            return 1;
        }

        List<Event> events = new ArrayList<>();
        for (String eventType : eventTypes) {
            events.add(new Event().type(eventType).timestamp(System.currentTimeMillis())); // TODO support timestamp arg
        }
        try {
            api.event.createEventsForAccountId(accountId, events);
            System.out.printf("Account '%s': created events %s.%n", accountName, events);
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                System.err.printf("Account ID %d not found.%n", accountId);
            } else {
                e.printStackTrace(System.err);
            }
            return 1;
        }

        return 0;
    }

}
