package accountsmanagement;

import picocli.CommandLine;

import java.util.Arrays;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "add", mixinStandardHelpOptions = true, version = "0.1",
        description = "add accounts")
public class Add implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", arity="1..*", description = "List of account names, e.g., acc-1 acc-2 acc-3")
    private String[] accountNames;

    @Override
    public Integer call() throws Exception {
        System.out.println("add: " + Arrays.asList(accountNames));
        return 0;
    }
}
