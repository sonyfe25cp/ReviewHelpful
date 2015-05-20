package labeltools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by OmarTech on 15-5-14.
 */
public class LabelButtonListener implements ActionListener {

    static Logger logger = LoggerFactory.getLogger(LabelButtonListener.class);

    JTextArea jTextArea;
    String label;

    public LabelButtonListener(JTextArea jTextArea, String label) {
        this.jTextArea = jTextArea;
        this.label = label;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String text = jTextArea.getText();
        String selected = jTextArea.getSelectedText();
        int selectionStart = jTextArea.getSelectionStart();
        int selectionEnd = jTextArea.getSelectionEnd();
        text = text.substring(0, selectionStart) + "<" + label + ">" + selected + "</" + label + ">" + text.substring(selectionEnd);
        jTextArea.setText(text);
    }
}
