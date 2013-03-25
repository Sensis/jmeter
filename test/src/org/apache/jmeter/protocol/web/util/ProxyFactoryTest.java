package org.apache.jmeter.protocol.web.util;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Proxy;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class ProxyFactoryTest {
    private ProxyFactory factory;

    @Before
    public void initFactory() {
        factory = ProxyFactory.getInstance();
    }

    @Test
    public void shouldCreateAnAutoDetectProxy() {
        Proxy proxy = factory.getAutodetectProxy();
        assertThat(proxy.getProxyType(), is(Proxy.ProxyType.AUTODETECT));

        assertThat(proxy.isAutodetect(), is(true));
        assertThat(proxy.getHttpProxy(), is(nullValue()));
    }

    @Test
    public void shouldCreateDirectProxy() {
        Proxy proxy = factory.getDirectProxy();
        assertThat(proxy.getProxyType(), is(Proxy.ProxyType.DIRECT));

        assertThat(proxy.isAutodetect(), is(false));
        assertThat(proxy.getHttpProxy(), is(nullValue()));
    }

    @Test
    public void shouldCreateUrlProxy() {
        String pacUrl = "http://example.com/proxy.pac";
        Proxy proxy = factory.getUrlProxy(pacUrl);
        assertThat(proxy.getProxyType(), is(Proxy.ProxyType.PAC));
        assertThat(proxy.getProxyAutoconfigUrl(), is(pacUrl));

        assertThat(proxy.isAutodetect(), is(false));
        assertThat(proxy.getHttpProxy(), is(nullValue()));
    }

    @Test
    public void shouldCreateManualProxy() {
        String http = "http.com:1234";
        String https = "https.com:1234";
        String ftp = "ftp:1234";
        String noProxy = "none";
        Proxy proxy = factory.getManualProxy(http, https, ftp, noProxy);
        assertThat(proxy.getProxyType(), is(Proxy.ProxyType.MANUAL));
        assertThat(proxy.getHttpProxy(), is(http));
        assertThat(proxy.getSslProxy(), is(https));
        assertThat(proxy.getFtpProxy(), is(ftp));
        assertThat(proxy.getNoProxy(), is(noProxy));

        assertThat(proxy.isAutodetect(), is(false));
    }

}
