package org.apache.jmeter.protocol.web.config;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.protocol.web.util.BrowserFactory;
import org.apache.jmeter.protocol.web.util.BrowserType;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.TestListener;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class WebBrowserTypeConfig extends ConfigTestElement implements TestBean, TestListener {
    private static final Logger LOGGER = LoggingManager.getLoggerForClass();

    private static final long serialVersionUID = -1257902955849089888L;

    private static final String TYPE = "WebBrowserTypeConfig.type";

    public WebBrowserTypeConfig() {
    }

    public String getType() {
        return getPropertyAsString(TYPE);
    }

    public void setType(String type) {
        LOGGER.info("setType: "+type);
        setProperty(TYPE, type);
    }

    @Override
    public void testStarted() {
        BrowserFactory.getInstance().setBrowserType(BrowserType.valueOf(getType()));
    }

    @Override
    public void testStarted(String host) {
        testStarted();
    }

    @Override
    public void testEnded() {
    }

    @Override
    public void testEnded(String host) {
    }

    @Override
    public void testIterationStart(LoopIterationEvent event) {
        LOGGER.info("Setting browser to: "+getType());
        BrowserFactory.getInstance().setBrowserType(BrowserType.valueOf(getType()));
    }
}
