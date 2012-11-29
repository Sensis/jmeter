package org.apache.jmeter.protocol.web.config;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.protocol.web.util.BrowserFactory;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.TestListener;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class WebBrowserCacheConfig extends ConfigTestElement implements TestBean, TestListener {
    private static final Logger LOGGER = LoggingManager.getLoggerForClass();

    private static final long serialVersionUID = -6579029558490898888L;
	
	private static final String CACHE_SETTINGS = "WebBrowserCacheConfig.cacheSettings";
	
    public WebBrowserCacheConfig() {
    }

    public String getCacheSettings() {
        return getPropertyAsString(CACHE_SETTINGS);
    }

    public void setCacheSettings(String cacheSettings) {
        LOGGER.info("Setting to: "+cacheSettings);
        setProperty(CACHE_SETTINGS, cacheSettings);
    }

    @Override
    public void testStarted() {
        LOGGER.info("Cache settings: "+getCacheSettings());
        if(WebBrowserCacheConfigBeanInfo.CLEAR_ALL.equals(getCacheSettings())) {
            BrowserFactory.getInstance().clearBrowser();
        } else if(WebBrowserCacheConfigBeanInfo.CLEAR_COOKIES.equals(getCacheSettings())) {
            BrowserFactory.getInstance().clearBrowserCookies();
        }
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
        testStarted();
    }
}
