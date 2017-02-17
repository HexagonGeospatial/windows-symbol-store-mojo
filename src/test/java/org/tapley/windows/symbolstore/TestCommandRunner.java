package org.tapley.windows.symbolstore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class TestCommandRunner {

    @Mock 
    Runtime runtime;
    
    @Mock
    Process process;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    CommandRunner runner;
    
    List<String> commandList;
    
    @Before
    public void setUp() {
        commandList = new ArrayList<>();
        commandList.add("ls");
        commandList.add("-l");
        commandList.add("/tmp");
        runner = new CommandRunner(commandList);
        
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void terminate() {
        ReflectionTestUtils.setField(runner, "process", process);
        
        runner.terminate();
        Mockito.verify(process).destroy();
        Mockito.verifyNoMoreInteractions(process);
    }
    
    @Test
    public void terminateNoProcess() {
        ReflectionTestUtils.setField(runner, "process", null);
        runner.terminate();
    }
    
    @Test
    public void generalUsage() throws IOException {
        ReflectionTestUtils.setField(runner, "runtime", runtime);
        Mockito.when(runtime.exec(Mockito.any(String[].class))).thenAnswer(new Answer<Process>() {
            
            public Process answer(InvocationOnMock invocation) throws Throwable {
                String[] commandArray = invocation.getArgumentAt(0, String[].class);
                Assert.assertEquals(commandList.size(), commandArray.length);
                for(int i = 0; i < commandList.size(); i++) {
                    Assert.assertEquals(commandList.get(i), commandArray[i]);
                }
                return process;
            }
        });
        Mockito.when(process.isAlive()).thenAnswer(new Answer<Boolean>() {
            boolean nextResponse = true;
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                if(nextResponse) {
                    nextResponse = false;
                    return true;
                }
                return nextResponse;
            }
        });
        String outputExpected = "the command output";
        int expectedExitValue = 12345;
        Mockito.when(process.getInputStream()).thenReturn(new ByteArrayInputStream(outputExpected.getBytes("UTF-8")));
        Mockito.when(process.exitValue()).thenReturn(expectedExitValue);
        
        runner.run();
        
        Assert.assertEquals(expectedExitValue, runner.getExitValue());
        Assert.assertEquals(outputExpected, runner.getOutput());
    }
    
    @Test
    public void commandFails() throws IOException {
        thrown.expect(IllegalStateException.class);
        
        ReflectionTestUtils.setField(runner, "runtime", runtime);
        Mockito.when(runtime.exec(Mockito.any(String[].class))).thenThrow(new IOException("bang!"));
        runner.run();
    }
    
    @Test
    public void constructorNullArg() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("commandList cannot be null or empty");
        
        runner = new CommandRunner(null);
    }
  
    @Test
    public void constructorEmptyListArg() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("commandList cannot be null or empty");
        
        commandList.clear();
        runner = new CommandRunner(commandList);
    }
}
