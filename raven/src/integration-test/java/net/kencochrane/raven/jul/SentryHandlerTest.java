package net.kencochrane.raven.jul;

import net.kencochrane.raven.stub.SentryStub;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SentryHandlerTest {
    private static final Logger logger = Logger.getLogger(SentryHandlerTest.class.getName());
    private SentryStub sentryStub;

    @BeforeMethod
    public void setUp() throws Exception {
        // Workaround for GRADLE-2524
        LogManager.getLogManager().readConfiguration();
        sentryStub = new SentryStub();
        sentryStub.removeEvents();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        sentryStub.removeEvents();
    }

    @Test
    public void testInfoLog() throws Exception {
        assertThat(sentryStub.getEventCount(), is(0));
        logger.info("This is a test");
        assertThat(sentryStub.getEventCount(), is(1));
    }

    @Test
    public void testChainedExceptions() throws Exception {
        assertThat(sentryStub.getEventCount(), is(0));
        logger.log(Level.SEVERE, "This is an exception",
                new UnsupportedOperationException("Test", new UnsupportedOperationException()));
        assertThat(sentryStub.getEventCount(), is(1));
    }
}
