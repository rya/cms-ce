package com.enonic.cms.launcher.tomcat;

import com.enonic.cms.launcher.logging.LogListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TomcatService
    implements LogListener
{
    private final static Pattern PORT_NUMBER_PATTERN =
        Pattern.compile("Starting Coyote HTTP/1.1 on http\\-([0-9]+)");
    
    private final static Logger LOG = Logger.getLogger(TomcatService.class.getName());

    private final TomcatClassLoader classLoader;
    private final ExecutorService executorService;
    private final TomcatListener listener;
    private final Object bootstrap;
    private boolean started;
    private int portNumber = 8080;

    public TomcatService(final File tomcatDir, final TomcatListener listener)
        throws Exception
    {
        this.listener = listener;
        this.executorService = Executors.newSingleThreadExecutor();
        this.classLoader = new TomcatClassLoader();

        final File binDir = new File(tomcatDir, "bin");
        for (final File f : binDir.listFiles()) {
            if (f.getName().endsWith(".jar")) {
                this.classLoader.addURL(f.toURI().toURL());
            }
        }

        final File libDir = new File(tomcatDir, "lib");
        for (final File f : libDir.listFiles()) {
            if (f.getName().endsWith(".jar")) {
                this.classLoader.addURL(f.toURI().toURL());
            }
        }

        this.bootstrap = Class.forName("org.apache.catalina.startup.Catalina", true, this.classLoader).newInstance();
    }

    public void start()
    {
        if (this.started) {
            return;
        }

        this.executorService.execute(new Runnable() {
            public void run() {
                doStart();
            }
        });
    }

    public void stop()
    {
        if (!this.started) {
            return;
        }

        this.executorService.execute(new Runnable() {
            public void run() {
                doStop();
            }
        });
    }

    private void invokeMethod(final String name)
        throws Exception
    {
        final Method method = this.bootstrap.getClass().getMethod(name);

        final ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.classLoader);

        try {
            method.invoke(this.bootstrap);
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }

    private void doStart()
    {
        try {
            invokeMethod("start");
            this.started = true;
        } catch (final Exception e) {
            LOG.log(Level.SEVERE, "Failed to start tomcat", e);
            this.started = false;
        } finally {
            fireEvent();
        }
    }

    private void doStop()
    {
        try {
            invokeMethod("stop");
        } catch (final Exception e) {
            LOG.log(Level.WARNING, "Error while stopping tomcat", e);
        } finally {
            this.started = false;
            fireEvent();
        }
    }

    private void fireEvent()
    {
        if (this.started) {
            this.listener.serverStarted();
        } else {
            this.listener.serverStopped();
        }
    }

    public void log(final LogRecord record)
    {
        if (record.getLevel() != Level.INFO) {
            return;
        }

        if (!record.getLoggerName().equals("org.apache.coyote.http11.Http11Protocol")) {
            return;
        }

        final Matcher matcher = PORT_NUMBER_PATTERN.matcher(record.getMessage());
        if (matcher.matches()) {
            this.portNumber = Integer.parseInt(matcher.group(1));
        }
    }

    public int getPortNumber()
    {
        return this.portNumber;
    }
}

