package accountsmanagement;

import picocli.CommandLine;

import java.util.Arrays;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "event", mixinStandardHelpOptions = true, version = "0.1",
        description = "Send events for an account")
public class RaiseEvents implements Callable<Integer> {

   @CommandLine.Option(names = {"-account"}, required = true, description = "Account name")
    private String accountName;

    @CommandLine.Parameters(index = "0", arity="1..*", description = "List of account names, e.g., acc-1 acc-2 acc-3")
    private String[] eventNames;

    //@CommandLine.Option(names = {"-e"}, description = "Event names, e.g., ev1 ev2 ev3,timestamp ev4")
    //private String[] eventNames;

    //@CommandLine.Option(names = {"-e"}, description = "Event timestamp, e.g., \"xxxx\", default is current time")
    //private String timestamp = "";

    @Override
    public Integer call() throws Exception {
        System.out.println(accountName + ": events: " + Arrays.asList(eventNames));
        return 0;
    }
}
