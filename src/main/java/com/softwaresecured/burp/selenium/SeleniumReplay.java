package com.softwaresecured.burp.selenium;

import com.softwaresecured.burp.util.ExceptionUtil;
import com.softwaresecured.burp.util.Logger;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class SeleniumReplay {
    private String stdout = "";
    private String stderr = "";
    private String script;
    private Context cx = null;
    private SeleniumDriver seleniumDriver;
    private long startTime = System.currentTimeMillis();
    private boolean completed = false;
    private int maxRuntime;
    boolean headless;
    public SeleniumReplay( String script, int maxRuntime, boolean headless ) {
        this.script = script;
        this.maxRuntime = maxRuntime;
        this.headless = headless;
        seleniumDriver = new SeleniumDriver(headless);
    }

    public void execute() {
        startTime = System.currentTimeMillis();
        ScriptWatchDog scriptWatchDog = new ScriptWatchDog(this, maxRuntime);
        scriptWatchDog.start();
        StringBuilder scriptSb = new StringBuilder();
        scriptSb.append("var SeleniumDriver = Packages.com.softwaresecured.burp.selenium.SeleniumDriver;\n");
        scriptSb.append(script);
        String script = scriptSb.toString();

        ByteArrayOutputStream errBuff = new ByteArrayOutputStream();
        ByteArrayOutputStream outBuff = new ByteArrayOutputStream();
        PrintStream stderrStream = new PrintStream(errBuff);
        PrintStream stdoutStream = new PrintStream(outBuff);



        try {
            Thread.currentThread().setContextClassLoader(Context.class.getClassLoader());
            cx = Context.newBuilder("js")
                    .out(stdoutStream)
                    .err(stderrStream)
                    .allowAllAccess(true)
                    .allowHostClassLookup(className -> true)
                    .build();
            validateSyntax(script);
            cx.getBindings("js").putMember("seleniumDriver",seleniumDriver);
            cx.eval("js",script);
            cx.close();
        } catch ( Exception e ) {
            try {
                stderrStream.write(ExceptionUtil.stackTraceToString(e).getBytes(StandardCharsets.UTF_8));
            } catch (IOException ex) {
                ;
            }
            Logger.log("ERROR", ExceptionUtil.stackTraceToString(e));
        }
        finally {
            seleniumDriver.cleanup();
            stdout = outBuff.toString();
            stderr = errBuff.toString();
            Logger.log("ERROR", String.format("Script STDERR: %s", stderr));
            Logger.log("INFO", String.format("Script STDOUT: %s", stdout));
            try {
                errBuff.close();
                outBuff.close();
                stdoutStream.close();
                stderrStream.close();

            } catch (IOException ignored) {
                ;
            }
        }
        completed = true;
    }

    public void terminate() {
        if ( seleniumDriver != null ) {
            seleniumDriver.cleanup();
        }
        if ( cx != null ) {
            try {
                cx.interrupt(Duration.ZERO);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void validateSyntax ( String script ) throws IOException {
        Source src = Source.newBuilder("js",script,"script.test").build();
        cx.parse(src);
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public SeleniumDriver getSeleniumDriver() {
        return seleniumDriver;
    }
}
