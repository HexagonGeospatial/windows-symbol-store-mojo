package org.tapley.windows.symbolstore;

import java.util.List;

public class CommandRunnerFactory implements ICommandRunnerFactory {

    @Override
    public CommandRunner getCommandRunner(List<String> command) {
        return new CommandRunner(command);
    }   
}
