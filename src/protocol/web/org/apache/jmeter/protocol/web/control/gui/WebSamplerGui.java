package org.apache.jmeter.protocol.web.control.gui;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.jmeter.protocol.web.sampler.WebSampler;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;

public class WebSamplerGui extends AbstractSamplerGui {

	private static final long serialVersionUID = -3484685528176139410L;

	private JTextField parameters;// parameters to pass to script file (or script)

	private JTextArea scriptField;// script area

	public WebSamplerGui() {
		createGui();
	}

	@Override
	public String getLabelResource() {
		return "web";
	}

    @Override
    public void configure(TestElement element) {
        scriptField.setText(element.getPropertyAsString(WebSampler.SCRIPT));
        parameters.setText(element.getPropertyAsString(WebSampler.PARAMETERS));
        super.configure(element);
    }

	@Override
	public TestElement createTestElement() {
		WebSampler sampler = new WebSampler();
		modifyTestElement(sampler);
		return sampler;
	}

	@Override
	public void modifyTestElement(TestElement element) {
		element.clear();
		this.configureTestElement(element);
		element.setProperty(WebSampler.SCRIPT, scriptField.getText());
		element.setProperty(WebSampler.PARAMETERS, parameters.getText());
	}

    @Override
    public void clearGui() {
        super.clearGui();

        parameters.setText(""); //$NON-NLS-1$
        scriptField.setText(""); //$NON-NLS-1$
    }

	private void createGui() {
		setLayout(new BorderLayout(0, 5));
		setBorder(makeBorder());

		Box box = Box.createVerticalBox();
		box.add(makeTitlePanel());
		box.add(createParameterPanel());
		add(box, BorderLayout.NORTH);

		JPanel panel = createScriptPanel();
		add(panel, BorderLayout.CENTER);
		// Don't let the input field shrink too much
		add(Box.createVerticalStrut(panel.getPreferredSize().height),
				BorderLayout.WEST);
	}

	private JPanel createParameterPanel() {
		JLabel label = new JLabel(
				JMeterUtils.getResString("web_script_parameters")); // $NON-NLS-1$

		parameters = new JTextField(10);
		parameters.setName(WebSampler.PARAMETERS);
		label.setLabelFor(parameters);

		JPanel parameterPanel = new JPanel(new BorderLayout(5, 0));
		parameterPanel.add(label, BorderLayout.WEST);
		parameterPanel.add(parameters, BorderLayout.CENTER);
		return parameterPanel;
	}

	private JPanel createScriptPanel() {
		scriptField = new JTextArea();
		scriptField.setRows(4);
		scriptField.setLineWrap(true);
		scriptField.setWrapStyleWord(true);

		JLabel label = new JLabel(JMeterUtils.getResString("web_script")); // $NON-NLS-1$
		label.setLabelFor(scriptField);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(label, BorderLayout.NORTH);
		panel.add(new JScrollPane(scriptField), BorderLayout.CENTER);

		JTextArea explain = new JTextArea(
				JMeterUtils.getResString("web_script_variables")); //$NON-NLS-1$
		explain.setLineWrap(true);
		explain.setEditable(false);
		explain.setBackground(this.getBackground());
		panel.add(explain, BorderLayout.SOUTH);

		return panel;
	}
}
