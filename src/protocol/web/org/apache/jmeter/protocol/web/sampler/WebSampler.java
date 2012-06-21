package org.apache.jmeter.protocol.web.sampler;

import java.io.PrintStream;
import java.util.Properties;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.jmeter.protocol.web.util.BrowserFactory;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.util.JOrphanUtils;
import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;


/**
 * A Sampler that makes HTTP requests using a real browser (via. Selenium/WebDriver).  It currently 
 * provides a scripting mechanism via. Javascript to control the browser instance.
 */
public class WebSampler extends AbstractSampler implements ThreadListener {
	
	/**
	 * This declares the 'websampler' variable, which is a shorthand for accessing <code>org.openqa.selenium</code> and
	 * <code>org.openqa.selenium.support.ui</code> classes without specifying the full package name.  The shorthand for
	 * accessing these classes is as follows:
	 * <pre>
	 * with(websampler) {
	 *     var element = browser.findElement(By.id('myId'));
	 * }
	 * </pre>
	 */
	private static final String SCRIPT_UTILITY = "var websampler = JavaImporter(org.openqa.selenium, org.openqa.selenium.support.ui)";

    public static final String SCRIPT = "WebSampler.script";

	public static final String PARAMETERS = "WebSampler.parameters";
    
	private static final Logger LOGGER = LoggingManager.getLoggerForClass();
	
	private static final long serialVersionUID = 234L;
	
	@Override
	public SampleResult sample(Entry e) {
        LOGGER.info("sampling web");
        
        // BSF Code copied liberally from BSFSampler
        final BSFEngine bsfEngine;
        final BSFManager mgr = new BSFManager();
        
        final SampleResult res = new SampleResult();
        res.setSampleLabel(getName());
        res.setSamplerData(toString());
        res.setDataType(SampleResult.TEXT);
        res.setContentType("text/plain"); // $NON-NLS-1$
        res.setDataEncoding("UTF-8");

        // Assume we will be successful
        res.setSuccessful(true);
        res.setResponseMessageOK();
        res.setResponseCodeOK();

        try {
            initManager(mgr);
            bsfEngine = mgr.loadScriptingEngine("javascript");
            
            // utility importer
            bsfEngine.exec("script", 0, 0, SCRIPT_UTILITY);
            res.sampleStart();
            Object outcome = bsfEngine.eval("script", 0, 0, getScript());
            res.sampleEnd();

            // setup status and data useful for verification
            res.setResponseData(BrowserFactory.getInstance().getBrowser().getPageSource().getBytes());
            if(outcome instanceof Boolean) { // only set this if the return value is boolean
                res.setSuccessful((Boolean) outcome);
            }
        } catch (Exception ex) {
            res.setResponseMessage(ex.toString());
            res.setResponseCode("000");
            if(ex.getMessage() != null) {
                res.setResponseData(ex.getMessage().getBytes());
            }
            res.setSuccessful(false);
        }

        return res;
	}

	private void initManager(BSFManager mgr) throws BSFException {
		final String scriptParameters = getParameters();
		
        // Use actual class name for log
        mgr.declareBean("log", LOGGER, Logger.class); // $NON-NLS-1$
        mgr.declareBean("Label",getName(), String.class); // $NON-NLS-1$
        mgr.declareBean("Parameters", scriptParameters, String.class); // $NON-NLS-1$
        String [] args=JOrphanUtils.split(scriptParameters, " ");//$NON-NLS-1$
        mgr.declareBean("args",args,args.getClass());//$NON-NLS-1$

        // web specific classes
        mgr.declareBean("browser", BrowserFactory.getInstance().getBrowser(), WebDriver.class);
        // For use in debugging:
        mgr.declareBean("OUT", System.out, PrintStream.class); // $NON-NLS-1$
    }
	
	public String getScript() {
		return getPropertyAsString(SCRIPT);
	}
	
	public void setScript(String script) {
		setProperty(SCRIPT, script);
	}

	public String getParameters() {
		return getPropertyAsString(PARAMETERS);
	}

	public void setParameters(String parameters) {
		setProperty(PARAMETERS, parameters);
	}

	@Override
	public void threadStarted() {
		LOGGER.info(Thread.currentThread().getName()+" threadStarted()");
	}

	@Override
	public void threadFinished() {
		LOGGER.info(Thread.currentThread().getName()+" threadFinished()");
        BrowserFactory.getInstance().clearBrowser();
	}
}
