package org.apache.jmeter.protocol.web.sampler;

import org.apache.jmeter.protocol.web.util.BrowserFactory;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BrowserFactory.class)
public class WebSamplerTest {
    @Test
    public void shouldNotBeSuccessfulWhenExecutionOfTheScriptReturnsFalse() {
        PowerMockito.mockStatic(BrowserFactory.class);
        BrowserFactory mockBrowserFactory = mock(BrowserFactory.class);
        WebDriver mockBrowser = mock(WebDriver.class);
        when(BrowserFactory.getInstance()).thenReturn(mockBrowserFactory);
        when(mockBrowserFactory.getBrowser()).thenReturn(mockBrowser);
        when(mockBrowser.getPageSource()).thenReturn("");

        WebSampler sampler = new WebSampler();
        sampler.setScript("false");
        SampleResult sampleResult = sampler.sample(null);
        assertThat(sampleResult.isSuccessful(), is(false));
    }

    @Test
    public void shouldBeSuccessfulWhenExecutionOfTheScriptReturnsTrue() {
        PowerMockito.mockStatic(BrowserFactory.class);
        BrowserFactory mockBrowserFactory = mock(BrowserFactory.class);
        WebDriver mockBrowser = mock(WebDriver.class);
        when(BrowserFactory.getInstance()).thenReturn(mockBrowserFactory);
        when(mockBrowserFactory.getBrowser()).thenReturn(mockBrowser);
        when(mockBrowser.getPageSource()).thenReturn("");

        WebSampler sampler = new WebSampler();
        sampler.setScript("true");
        SampleResult sampleResult = sampler.sample(null);
        assertThat(sampleResult.isSuccessful(), is(true));
    }

    @Test
    public void shouldBeSuccessfulWhenExecutionOfTheScriptDoesNotReturnValue() {
        PowerMockito.mockStatic(BrowserFactory.class);
        BrowserFactory mockBrowserFactory = mock(BrowserFactory.class);
        WebDriver mockBrowser = mock(WebDriver.class);
        when(BrowserFactory.getInstance()).thenReturn(mockBrowserFactory);
        when(mockBrowserFactory.getBrowser()).thenReturn(mockBrowser);
        when(mockBrowser.getPageSource()).thenReturn("");

        WebSampler sampler = new WebSampler();
        sampler.setScript("function() {}");
        SampleResult sampleResult = sampler.sample(null);
        assertThat(sampleResult.isSuccessful(), is(true));
    }
}
