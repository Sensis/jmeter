package org.apache.jmeter.protocol.web.config;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.apache.jmeter.protocol.web.util.BrowserFactory;
import org.apache.jmeter.protocol.web.util.BrowserType;
import org.apache.jmeter.testbeans.TestBean;

public class WebBrowserTypeConfig extends ConfigTestElement implements TestBean, LoopIterationListener {
    private static final long serialVersionUID = -657902955849089888L;

    private static final String TYPE = "WebBrowserTypeConfig.type";

    public WebBrowserTypeConfig() {
    }

    public String getType() {
        return getPropertyAsString(TYPE);
    }

    public void setType(String type) {
        setProperty(TYPE, type);
    }


    @Override
    public void iterationStart(LoopIterationEvent iterEvent) {
        BrowserFactory.getInstance().setBrowserType(BrowserType.valueOf(getType()));
    }
}
