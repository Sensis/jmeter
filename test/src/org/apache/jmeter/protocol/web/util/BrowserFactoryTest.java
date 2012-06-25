package org.apache.jmeter.protocol.web.util;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;
import java.util.Vector;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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

        private BrowserType type;
        private Proxy proxy;

        public BrowserCreator(BrowserType type) {
            this(type, null);
        }

        public BrowserCreator(Proxy proxy) {
            this(BrowserType.FIREFOX, proxy);
        }

        public BrowserCreator(BrowserType type, Proxy proxy) {
            this.type = type;
            this.proxy = proxy;
        }

        @Override
        public void run() {
            BrowserFactoryTest.this.factory.setBrowserType(type);
            BrowserFactoryTest.this.factory.setProxy(proxy);
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
        whenNew(FirefoxDriver.class).withParameterTypes(Capabilities.class).withArguments(isA(Capabilities.class)).thenReturn(mockBrowser);

        WebDriver firstBrowser = factory.getBrowser();
        WebDriver secondBrowser = factory.getBrowser();
        assertThat(firstBrowser, is(sameInstance(secondBrowser)));

        verifyNew(FirefoxDriver.class, Mockito.times(1)).withArguments(isA(DesiredCapabilities.class));
    }

    @Test
    public void shouldClearCookiesOnCurrentBrowserWhenClearCookiesIsInvoked() throws Exception {
        FirefoxDriver mockBrowser = mock(FirefoxDriver.class);
        whenNew(FirefoxDriver.class).withParameterTypes(Capabilities.class).withArguments(isA(Capabilities.class)).thenReturn(mockBrowser);
		WebDriver.Options mockOptions = mock(WebDriver.Options.class);
		when(mockBrowser.manage()).thenReturn(mockOptions);

        factory.clearBrowserCookies();

        verifyNew(FirefoxDriver.class, Mockito.times(1)).withArguments(isA(DesiredCapabilities.class));
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
        whenNew(FirefoxDriver.class).withParameterTypes(Capabilities.class).withArguments(isA(DesiredCapabilities.class)).thenReturn(firstBrowser, secondBrowser);

        WebDriver beforeReset = factory.getBrowser();
        factory.clearBrowser();
        WebDriver afterReset = factory.getBrowser();

        assertThat(afterReset, is(not(sameInstance(beforeReset))));

        verifyNew(FirefoxDriver.class, Mockito.times(2)).withArguments(isA(DesiredCapabilities.class));
    }

    @Test
    public void shouldReturnDifferentBrowserWhenCalledFromSeparateThreads() throws Exception {
        FirefoxDriver firstBrowser = mock(FirefoxDriver.class);
        FirefoxDriver secondBrowser = mock(FirefoxDriver.class);
        whenNew(FirefoxDriver.class).withParameterTypes(Capabilities.class).withArguments(isA(DesiredCapabilities.class)).thenReturn(firstBrowser, secondBrowser);

        Thread firstThread = new Thread(this.new BrowserCreator(BrowserType.FIREFOX));
        Thread secondThread = new Thread(this.new BrowserCreator(BrowserType.FIREFOX));

        firstThread.start();
        secondThread.start();

        firstThread.join();
        secondThread.join();

        assertThat(browsers.size(), is(2));
        assertThat(browsers.get(0), is(not(sameInstance(browsers.get(1)))));

        verifyNew(FirefoxDriver.class, Mockito.times(2)).withArguments(isA(DesiredCapabilities.class));
    }

    @Test
    public void shouldBeAbleToSetBrowserTypeFromSeparateThread() throws Exception {
        FirefoxDriver firstBrowser = mock(FirefoxDriver.class);
        ChromeDriver secondBrowser = mock(ChromeDriver.class);
        whenNew(FirefoxDriver.class).withParameterTypes(Capabilities.class).withArguments(isA(DesiredCapabilities.class)).thenReturn(firstBrowser);
        whenNew(ChromeDriver.class).withParameterTypes(Capabilities.class).withArguments(isA(DesiredCapabilities.class)).thenReturn(secondBrowser);

        Thread firstThread = new Thread(this.new BrowserCreator(BrowserType.FIREFOX));
        Thread secondThread = new Thread(this.new BrowserCreator(BrowserType.CHROME));

        firstThread.start();
        secondThread.start();

        firstThread.join();
        secondThread.join();

        assertThat(browsers.size(), is(2));
        assertThat(browsers.get(0), is(not(sameInstance(browsers.get(1)))));

        verifyNew(FirefoxDriver.class, Mockito.times(1)).withArguments(isA(DesiredCapabilities.class));
        verifyNew(ChromeDriver.class, Mockito.times(1)).withArguments(isA(DesiredCapabilities.class));
    }

    @Test
    @PrepareForTest(value = {DesiredCapabilities.class, BrowserFactory.class})
    public void shouldBeAbleToSetProxyFromSeparateThread() throws Exception {
        FirefoxDriver firstBrowser = mock(FirefoxDriver.class);
        FirefoxDriver secondBrowser = mock(FirefoxDriver.class);
        ArgumentCaptor<Proxy> proxyArgumentCaptor = ArgumentCaptor.forClass(Proxy.class);
        PowerMockito.mockStatic(DesiredCapabilities.class);
        DesiredCapabilities desiredCapabilities = mock(DesiredCapabilities.class);
        when(DesiredCapabilities.firefox()).thenReturn(desiredCapabilities);
        whenNew(FirefoxDriver.class).withParameterTypes(Capabilities.class).withArguments(desiredCapabilities).thenReturn(firstBrowser);
        whenNew(FirefoxDriver.class).withParameterTypes(Capabilities.class).withArguments(desiredCapabilities).thenReturn(secondBrowser);

        Proxy firstProxy = new Proxy();
        Proxy secondProxy = new Proxy();
        Thread firstThread = new Thread(this.new BrowserCreator(firstProxy));
        Thread secondThread = new Thread(this.new BrowserCreator(secondProxy));

        firstThread.start();
        secondThread.start();

        firstThread.join();
        secondThread.join();

        assertThat(browsers.size(), is(2));

        verifyNew(FirefoxDriver.class, Mockito.times(2)).withArguments(desiredCapabilities);
        verify(desiredCapabilities, Mockito.atLeastOnce()).setCapability(eq(CapabilityType.PROXY), proxyArgumentCaptor.capture());

        assertThat(proxyArgumentCaptor.getAllValues(), hasItem(firstProxy));
        assertThat(proxyArgumentCaptor.getAllValues(), hasItem(secondProxy));
    }

    @Test
    public void shouldReturnFirefoxBrowserWhenNoneSpecified() throws Exception {
        FirefoxDriver mockBrowser = mock(FirefoxDriver.class);
        whenNew(FirefoxDriver.class).withParameterTypes(Capabilities.class).withArguments(isA(DesiredCapabilities.class)).thenReturn(mockBrowser);

        assertThat(factory.getBrowser(), is(sameInstance((WebDriver) mockBrowser)));

        verifyNew(FirefoxDriver.class, Mockito.times(1)).withArguments(isA(DesiredCapabilities.class));
    }

    @Test
    public void shouldReturnChromeBrowserWhenSpecified() throws Exception {
        ChromeDriver mockBrowser = mock(ChromeDriver.class);
        whenNew(ChromeDriver.class).withParameterTypes(Capabilities.class).withArguments(isA(DesiredCapabilities.class)).thenReturn(mockBrowser);

        factory.setBrowserType(BrowserType.CHROME);
        assertThat(factory.getBrowser(), is(sameInstance((WebDriver)mockBrowser)));

        verifyNew(ChromeDriver.class, Mockito.times(1)).withArguments(isA(DesiredCapabilities.class));
    }
}
