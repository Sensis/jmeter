package org.apache.jmeter.protocol.web.util;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.android.AndroidDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;

/**
 * This is responsible for accessing (and unsetting) a WebDriver browser instance per thread.
 */
public class BrowserFactory {
    private static final Logger LOGGER = LoggingManager.getLoggerForClass();

    /**
     * Each thread will reference their WebDriver (browser) instance via this ThreadLocal instance.  This is
     * initialised in the {@see #threadStarted()} and quit & unset in {@see #threadFinished()}.
     */
    private static final ThreadLocal<WebDriver> BROWSERS = new ThreadLocal<WebDriver>();

    private static final ThreadLocal<Proxy> PROXIES = new ThreadLocal<Proxy>();

    private static final ThreadLocal<BrowserType> BROWSER_TYPES = new ThreadLocal<BrowserType>() {
        @Override
        public BrowserType initialValue() {
            return BrowserType.CHROME;
        }
    };

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
        BrowserType browserType = BROWSER_TYPES.get();
        if(BrowserType.CHROME == browserType) {
            ChromeOptions options = new ChromeOptions();
            return new ChromeDriver(options);
        }
        else if(BrowserType.ANDROID == browserType) {
            return new AndroidDriver();
        }
        else {
            DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();
            desiredCapabilities.setCapability(CapabilityType.PROXY, getProxy());
            return new FirefoxDriver(desiredCapabilities);
        }
    }

    private String asChromeOptionsArgument(Proxy proxy) {
        LOGGER.info("proxy: "+proxy.getHttpProxy());
        return "--proxy-server=http://"+proxy.getHttpProxy();
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
        BROWSER_TYPES.set(browserType);
    }

    public BrowserType getBrowserType() {
        return BROWSER_TYPES.get();
    }
}
