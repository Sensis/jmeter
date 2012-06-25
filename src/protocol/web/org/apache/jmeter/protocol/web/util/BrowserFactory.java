package org.apache.jmeter.protocol.web.util;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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

    private static final ThreadLocal<DesiredCapabilities> CAPABILITIES = new ThreadLocal<DesiredCapabilities>() {
        @Override
        public DesiredCapabilities initialValue() {
            return DesiredCapabilities.firefox();
        }
    };

    private static final ThreadLocal<Proxy> PROXIES = new ThreadLocal<Proxy>();

    private static final BrowserFactory INSTANCE = new BrowserFactory();

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
            BROWSERS.set(createBrowser());
        }

        return BROWSERS.get();
    }

    private WebDriver createBrowser() {
        DesiredCapabilities desiredCapabilities = CAPABILITIES.get();
        desiredCapabilities.setCapability(CapabilityType.PROXY, getProxy());
        if("chrome".equalsIgnoreCase(desiredCapabilities.getBrowserName())) {
                return new ChromeDriver(desiredCapabilities);
        }
        else {
                return new FirefoxDriver(desiredCapabilities);
        }
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
        PROXIES.set(proxy);
    }

    /**
     * Access the proxy configured for all browsers accessed from this factory.
     * 
     * @return the configured proxy.
     */
	public Proxy getProxy() {
		return PROXIES.get();
	}

    public void setBrowserType(BrowserType browserType) {
        if (browserType == BrowserType.CHROME) {
            CAPABILITIES.set(DesiredCapabilities.chrome());
        }
        else {
            CAPABILITIES.set(DesiredCapabilities.firefox());
        }
    }
}
