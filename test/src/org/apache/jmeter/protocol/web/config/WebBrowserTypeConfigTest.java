package org.apache.jmeter.protocol.web.config;

import org.apache.jmeter.protocol.web.util.BrowserFactory;
import org.apache.jmeter.protocol.web.util.BrowserType;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class WebBrowserTypeConfigTest {
    private WebBrowserTypeConfig typeConfig;

    @Before
	public void setUp() {
		typeConfig = new WebBrowserTypeConfig();
	}
	
	@Test
    public void shouldBeAbleToReadSamePropertiesFromConfigAfterDeserialisation() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArray);

        final String type = "cache setting value";
        
        typeConfig.setType(type);

        outputStream.writeObject(typeConfig);
        outputStream.flush();

        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(byteArray.toByteArray()));
        WebBrowserTypeConfig deserialisedtypeConfig = (WebBrowserTypeConfig)inputStream.readObject();

        assertThat(deserialisedtypeConfig.getType(), is(type));
    }
	
	@Test
	public void shouldUseChromeWhenSpecified() throws Exception {
		typeConfig.setType(WebBrowserTypeConfigBeanInfo.CHROME);
		typeConfig.testIterationStart(null);
		
		assertThat(BrowserFactory.getInstance().getBrowserType(), is(BrowserType.CHROME));
	}
	
	@Test
	public void shouldUseFirefoxWhenSpecified() throws Exception {
        typeConfig.setType(WebBrowserTypeConfigBeanInfo.FIREFOX);
        typeConfig.testIterationStart(null);

        assertThat(BrowserFactory.getInstance().getBrowserType(), is(BrowserType.FIREFOX));
    }
}
