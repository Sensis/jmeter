package org.apache.jmeter.protocol.web.util;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * This is responsible for accessing (and unsetting) a WebDriver browser instance per thread.
 */
public class BrowserFactory {
    /**
     * Each thread will reference their WebDriver (browser) instance via this ThreadLocal instance.  This is
     * initialised in the {@see #threadStarted()} and quit & unset in {@see #threadFinished()}.
     */
    private static final ThreadLocal<WebDriver> BROWSERS = new ThreadLocal<WebDriver>();

    private static final BrowserFactory INSTANCE = new BrowserFactory();

    private Proxy proxy;

    public static BrowserFactory getInstance() {
        return INSTANCE;
    }

    private BrowserFactory() {}

    /**
     * Call this method to get a WebDriver (browser) for the current thread.  The returned browser instance will be
     * stored and returned on subsequent calls until {@see #clearBrowser()} is called.
     *
     * @return a thread specific WebDriver instance.
     */
    public WebDriver getBrowser() {
        if(BROWSERS.get() == null) {
            WebDriver browser = null;
            if(proxy != null) {
                final DesiredCapabilities capabilities = DesiredCapabilities.firefox();
                capabilities.setCapability(CapabilityType.PROXY, proxy);
                browser = new FirefoxDriver(capabilities);
            }
            else {
                browser = new FirefoxDriver();
            }
            BROWSERS.set(browser);
        }

        return BROWSERS.get();
    }
    
    /**
     * Removes all cookies in the current browser used by the running thread.
     */
    public void clearBrowserCookies() {
    	getBrowser().manage().deleteAllCookies();
    }

    /**
     * Removes any WebDriver instance associated with the calling thread and quits the running browser instance.
     */
    public void clearBrowser() {
        if(BROWSERS.get() != null) {
            BROWSERS.get().quit();
            BROWSERS.remove();
        }
    }

    /**
     * Use this to set the proxy to use when getting/creating new WebDriver instances {#getBrowser}.  Unlike the browsers
     * this setting spans across threads, so there is no per thread configured values.
     *
     * @param proxy is the proxy to use when {#getBrowser} is invoked.
     */
    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Access the proxy configured for all browsers accessed from this factory.
     * 
     * @return the configured proxy.
     */
	public Proxy getProxy() {
		return proxy;
	}
}
