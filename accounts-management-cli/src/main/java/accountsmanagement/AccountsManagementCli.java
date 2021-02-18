package accountsmanagement;

import com.smec.cc.accountsmanagement.ApiException;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "account", mixinStandardHelpOptions = true, version = "0.1",
        description = "Account Management CLI.")
public class AccountsManagementCli implements Callable<Integer> {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Override
    public Integer call() throws Exception {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand");    }

    public static void main(String[] args) throws ApiException {
        CommandLine commandLine = new CommandLine(new AccountsManagementCli());
        commandLine.addSubcommand(new Add());
        commandLine.addSubcommand(new RaiseEvents());
        commandLine.addSubcommand(new Statistics());
        commandLine.addSubcommand(new UpdateAccount());
        System.exit(commandLine.execute(args));

//        ApiClient apiClient = new ApiClient().setBasePath("http://localhost:8080/v1");
//        AccountApi accountApi = new AccountApi(apiClient);
//        EventApi eventApi = new EventApi(apiClient);
//        StatisticsApi statisticsApi = new StatisticsApi(apiClient);
//
//        try {
//            Account account = accountApi.addAccount(new Account().name("Account-1"));
//            eventApi.createEventsForAccountId(account.getId(),
//                    Arrays.asList(
//                            new Event().type("Event-1")
//                                       .timestamp(System.currentTimeMillis()),
//                            new Event().type("Event-2")
//                                       .timestamp(System.currentTimeMillis()),
//                            new Event().type("Event-3")
//                                       .timestamp(System.currentTimeMillis())
//                    ));
//            String statistics = statisticsApi.getStatistics(account.getName());
//            System.out.println(statistics);
//        } catch (ApiException e) {
//            e.printStackTrace();
//        }
    }

}
