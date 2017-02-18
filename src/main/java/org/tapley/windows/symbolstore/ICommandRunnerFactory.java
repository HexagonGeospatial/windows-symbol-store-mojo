package org.tapley.windows.symbolstore;

import java.util.List;

public interface ICommandRunnerFactory {
    CommandRunner getCommandRunner(List<String> command);
}
