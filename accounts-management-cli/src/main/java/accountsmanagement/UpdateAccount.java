package accountsmanagement;

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
        System.out.println(Arrays.asList(oldNewAccountName));
        return 0;
    }
}
