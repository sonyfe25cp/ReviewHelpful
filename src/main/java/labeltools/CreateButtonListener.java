package labeltools;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by OmarTech on 15-5-14.
 */
public class CreateButtonListener implements ActionListener {

    static Logger logger = LoggerFactory.getLogger(CreateButtonListener.class);

    Set<String> labels;
    JPanel labelPanel;
    JTextArea jTextArea;
    JButton createNewLabel;

    public CreateButtonListener(Set<String> labels, JPanel labelPanel, JTextArea jTextArea, JButton createNewLabel) {
        this.labels = labels;
        this.labelPanel = labelPanel;
        this.jTextArea = jTextArea;
        this.createNewLabel = createNewLabel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame jFrame = new JFrame();
        JPanel jPanel = new JPanel();
        JLabel jLabel = new JLabel("在下面分行输入要标注的类型");
        jPanel.add(jLabel);
        final JTextArea textArea = new JTextArea(10, 20);
        final StringBuilder labelText = new StringBuilder();
        for (String label : labels) {
            labelText.append(label);
            labelText.append("\n");
        }
        textArea.setText(labelText.toString());
        jPanel.add(textArea);
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textArea.getText();
                String[] split = text.split("\n");
                labels = new HashSet<>();
                for (String str : split) {
                    str = StringUtils.deleteWhitespace(str);
                    if (!StringUtils.isEmpty(str)) {
                        labels.add(str);
                    }
                }
                Kakarotto.fillLabelPanel(labels, jTextArea, labelPanel);
                logger.info("there are {} labels in the text", split.length);
            }
        });
        jPanel.add(saveButton);
        jFrame.getContentPane().add(jPanel);
        jFrame.pack();
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.setVisible(true);
    }
}
