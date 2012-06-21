package org.apache.jmeter.protocol.web.config;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.apache.jmeter.protocol.web.util.BrowserFactory;
import org.apache.jmeter.testbeans.TestBean;

public class WebBrowserCacheConfig extends ConfigTestElement implements TestBean, LoopIterationListener {
	private static final long serialVersionUID = -6579029558490898888L;
	
	private static final String CACHE_SETTINGS = "WebBrowserCacheConfig.cacheSettings";
	
    public WebBrowserCacheConfig() {
    }

    public String getCacheSettings() {
        return getPropertyAsString(CACHE_SETTINGS);
    }

    public void setCacheSettings(String cacheSettings) {
        setProperty(CACHE_SETTINGS, cacheSettings);
    }

    @Override
    public void iterationStart(LoopIterationEvent iterEvent) {
        if(WebBrowserCacheConfigBeanInfo.CLEAR_ALL.equals(getCacheSettings())) {
            BrowserFactory.getInstance().clearBrowser();
        } else if(WebBrowserCacheConfigBeanInfo.CLEAR_COOKIES.equals(getCacheSettings())) {
            BrowserFactory.getInstance().clearBrowserCookies();
        }
    }
}
