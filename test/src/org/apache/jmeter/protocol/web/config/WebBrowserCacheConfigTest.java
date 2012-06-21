package org.apache.jmeter.protocol.web.config;

import org.apache.jmeter.protocol.web.util.BrowserFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BrowserFactory.class)
public class WebBrowserCacheConfigTest {
    private WebBrowserCacheConfig cacheConfig;

    @Before
	public void setUp() {
		cacheConfig = new WebBrowserCacheConfig();
	}
	
	@Test
    public void shouldBeAbleToReadSamePropertiesFromConfigAfterDeserialisation() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArray);

        final String cacheSettings = "cache setting value";
        
        cacheConfig.setCacheSettings(cacheSettings);

        outputStream.writeObject(cacheConfig);
        outputStream.flush();

        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(byteArray.toByteArray()));
        WebBrowserCacheConfig deserialisedCacheConfig = (WebBrowserCacheConfig)inputStream.readObject();

        assertThat(deserialisedCacheConfig.getCacheSettings(), is(cacheSettings));
    }
	
	@Test
	public void shouldClearBrowserWhenCacheSettingIsSetToClearAll() throws Exception {
		PowerMockito.mockStatic(BrowserFactory.class);
		BrowserFactory mockBrowserFactory = mock(BrowserFactory.class);
		when(BrowserFactory.getInstance()).thenReturn(mockBrowserFactory);
		
		cacheConfig.setCacheSettings(WebBrowserCacheConfigBeanInfo.CLEAR_ALL);
		cacheConfig.iterationStart(null);
		
		verify(mockBrowserFactory).clearBrowser();
	}
	
	@Test
	public void shouldClearCookiesWhenCacheSettingIsSetToClearCookies() throws Exception {
		PowerMockito.mockStatic(BrowserFactory.class);
		BrowserFactory mockBrowserFactory = mock(BrowserFactory.class);
		when(BrowserFactory.getInstance()).thenReturn(mockBrowserFactory);
		
		cacheConfig.setCacheSettings(WebBrowserCacheConfigBeanInfo.CLEAR_COOKIES);
		cacheConfig.iterationStart(null);
		
		verify(mockBrowserFactory).clearBrowserCookies();
	}
}
