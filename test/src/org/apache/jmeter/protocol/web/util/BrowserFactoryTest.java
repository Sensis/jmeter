package org.apache.jmeter.protocol.web.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;
import java.util.Vector;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BrowserFactory.class)
public class BrowserFactoryTest {
    /**
     * Used to store the browsers created by {#BrowserCreator} threads.
     */
    private final List<WebDriver> browsers = new Vector<WebDriver>();

    /**
     * The run method will access a browser from the factory and add it to the {#browsers} list.
     */
    private class BrowserCreator implements Runnable {

        @Override
        public void run() {
            browsers.add(BrowserFactoryTest.this.factory.getBrowser());
        }
    };

    private BrowserFactory factory;

    @Before
    public void initFactory() {
        factory = BrowserFactory.getInstance();
        factory.setProxy(null);
    }

    @After
    public void clearBrowsers() {
        factory.clearBrowser();
        for(WebDriver browser: browsers) {
            browser.quit();
        }
        browsers.clear();
    }

    @Test
    public void shouldReturnTheSameBrowserWhenSubsequentGetBrowserIsInvoked() throws Exception {
        FirefoxDriver mockBrowser = mock(FirefoxDriver.class);
        whenNew(FirefoxDriver.class).withNoArguments().thenReturn(mockBrowser);

        WebDriver firstBrowser = factory.getBrowser();
        WebDriver secondBrowser = factory.getBrowser();
        assertThat(firstBrowser, is(sameInstance(secondBrowser)));

        verifyNew(FirefoxDriver.class, Mockito.times(1)).withNoArguments();
    }

    @Test
    public void shouldClearCookiesOnCurrentBrowserWhenClearCookiesIsInvoked() throws Exception {
        FirefoxDriver mockBrowser = mock(FirefoxDriver.class);
        whenNew(FirefoxDriver.class).withNoArguments().thenReturn(mockBrowser);
		WebDriver.Options mockOptions = mock(WebDriver.Options.class);
		when(mockBrowser.manage()).thenReturn(mockOptions);

        factory.clearBrowserCookies();

        verifyNew(FirefoxDriver.class, Mockito.times(1)).withNoArguments();
        verify(mockOptions).deleteAllCookies();
    }

    @Test
    public void shouldBrowserShouldContainProxySettingsWhenSpecified() throws Exception {
        FirefoxDriver mockBrowser = mock(FirefoxDriver.class);
        whenNew(FirefoxDriver.class).withParameterTypes(Capabilities.class).withArguments(isA(DesiredCapabilities.class)).thenReturn(mockBrowser);

        factory.setProxy(new Proxy());
        WebDriver browser = factory.getBrowser();
        assertThat(browser, is(not(nullValue())));

        verifyNew(FirefoxDriver.class, Mockito.times(1)).withArguments(isA(DesiredCapabilities.class));
    }

    @Test
    public void shouldReturnNewBrowserWhenClearBrowserIsInvoked() throws Exception {
        FirefoxDriver firstBrowser = mock(FirefoxDriver.class);
        FirefoxDriver secondBrowser = mock(FirefoxDriver.class);
        whenNew(FirefoxDriver.class).withNoArguments().thenReturn(firstBrowser, secondBrowser);

        WebDriver beforeReset = factory.getBrowser();
        factory.clearBrowser();
        WebDriver afterReset = factory.getBrowser();

        assertThat(afterReset, is(not(sameInstance(beforeReset))));

        verifyNew(FirefoxDriver.class, Mockito.times(2)).withNoArguments();
    }

    @Test
    public void shouldReturnDifferentBrowserWhenCalledFromSeparateThreads() throws Exception {
        FirefoxDriver firstBrowser = mock(FirefoxDriver.class);
        FirefoxDriver secondBrowser = mock(FirefoxDriver.class);
        whenNew(FirefoxDriver.class).withNoArguments().thenReturn(firstBrowser, secondBrowser);

        Thread firstThread = new Thread(this.new BrowserCreator());
        Thread secondThread = new Thread(this.new BrowserCreator());

        firstThread.start();
        secondThread.start();

        firstThread.join();
        secondThread.join();

        assertThat(browsers.size(), is(2));
        assertThat(browsers.get(0), is(not(sameInstance(browsers.get(1)))));

        verifyNew(FirefoxDriver.class, Mockito.times(2)).withNoArguments();
    }

}
