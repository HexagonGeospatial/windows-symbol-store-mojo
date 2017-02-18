package org.tapley.windows.symbolstore;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.Assert;

public class TestCommandRunnerFactory {
 
    CommandRunnerFactory factory = new CommandRunnerFactory();
    
    @Test
    public void get() {
        List<String> command = new ArrayList<>();
        command.add("one");
        CommandRunner runner = factory.getCommandRunner(command);
        Assert.assertNotNull(runner);
        Assert.assertTrue(runner instanceof CommandRunner);
    }
}
