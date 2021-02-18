package accountsmanagement;

import accountsmanagement.cli.commands.AddAccounts;
import accountsmanagement.cli.commands.RaiseEvents;
import accountsmanagement.cli.commands.Statistics;
import accountsmanagement.cli.commands.UpdateAccount;
import com.smec.cc.accountsmanagement.ApiException;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "account", mixinStandardHelpOptions = true, version = "0.1",
        description = "Account Management CLI.")
public class AccountsManagementCli implements Callable<Integer> {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(names = {"-endpoint"}, required = true, description = "Account API endpoint")
    private String apiEndpointUrl = "";

    @Override
    public Integer call() throws Exception {
        if (! apiEndpointUrl.isEmpty()) {
            Api.getInstance().setBasePath(apiEndpointUrl); // http://localhost:8080/v1
            return 0;
        } else {
            throw new CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand");
        }
    }

    public static void main(String[] args) throws ApiException {
        CommandLine commandLine = new CommandLine(new AccountsManagementCli());
        commandLine.addSubcommand(new AddAccounts());
        commandLine.addSubcommand(new RaiseEvents());
        commandLine.addSubcommand(new Statistics());
        commandLine.addSubcommand(new UpdateAccount());
        System.exit(commandLine.execute(args));
    }

}
