package accountsmanagement;

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
        System.out.println(Arrays.asList(accountNames));
        System.out.println("raw: " + raw);
        return 0;
    }
}
