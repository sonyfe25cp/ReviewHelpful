package labeltools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by OmarTech on 15-5-14.
 */
public class Kakarotto {
    static Logger logger = LoggerFactory.getLogger(Kakarotto.class);

    static Set<String> labels = new HashSet<>();

    static boolean openFolderFlag = false;
    static int currentFile = -1;
    static List<String> filePathList = null;

    public static JFrame createUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        final JFrame jFrame = new JFrame("Kakarotto -- a useful tool for data annotation");
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JPanel contentPanel = new JPanel();
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));


        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        final JTextArea textArea = new JTextArea(20, 80);
        textArea.setLineWrap(true);
        textArea.setAutoscrolls(true);
        mainPanel.add(textArea);


        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton nextOneLabel = new JButton("下一个");
        nextOneLabel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(currentFile);
                System.out.println("click");
                if (currentFile != -1) {//有文件
                    String old_path = filePathList.get(currentFile);
                    String new_path = old_path + ".ann";
                    String text = textArea.getText();
                    try {
                        FileUtils.write(new File(new_path), text);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (openFolderFlag) {
                    if (currentFile + 1 >= filePathList.size()) {
                        logger.info("文件都完成了");
                    } else {
                        currentFile++;
                        String s1 = readTextFromList(currentFile, filePathList);
                        textArea.setText(s1);
                    }
                }
            }
        });

        buttonPanel.add(nextOneLabel);

        JButton createNewLabel = new JButton("编辑标签");

        JPanel labelPanel = new JPanel();
        labelPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("标签们"),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        createNewLabel.addActionListener(new CreateButtonListener(labels, labelPanel, textArea, createNewLabel));
        buttonPanel.add(createNewLabel);
        buttonPanel.add(labelPanel);


        mainPanel.add(buttonPanel);
        contentPanel.add(mainPanel);

        JLabel statusLabel = new JLabel("状态栏:");
        contentPanel.add(statusLabel);


        JMenuBar menuBar = new JMenuBar();
        final JMenu openFolder = new JMenu("打开文件夹");
        openFolder.setMnemonic(KeyEvent.VK_F);
        openFolder.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("选择数据文件夹");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(fileChooser);
                switch (result) {
                    case JFileChooser.APPROVE_OPTION:
                        File selectedFile = fileChooser.getSelectedFile();
                        logger.info("数据文件夹:{}", selectedFile.getAbsolutePath());
                        openFolderFlag = true;
                        filePathList = new ArrayList<String>();
                        for (File file : selectedFile.listFiles()) {
                            String absolutePath = file.getAbsolutePath();
                            if (!absolutePath.endsWith(".ann")) {
                                filePathList.add(absolutePath);
                            }
                        }
                        currentFile = 0;
                        String s0 = readTextFromList(currentFile, filePathList);
                        textArea.setText(s0);
                        break;
                    default:
                        System.out.println("result: " + result);

                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });


        JMenu loadConfig = new JMenu("加载设置");
        loadConfig.setMnemonic(KeyEvent.VK_L);
        loadConfig.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("选择配置文件");
                FileFilter fileFilter = new FileNameExtensionFilter(".config", "config");
                fileChooser.setFileFilter(fileFilter);
                int result = fileChooser.showOpenDialog(fileChooser);
                switch (result) {
                    case JFileChooser.APPROVE_OPTION:
                        File selectedFile = fileChooser.getSelectedFile();
                        try {
                            java.util.List<String> strings = FileUtils.readLines(selectedFile);
                            for (String str : strings) {
                                System.out.println(str);
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        break;
                    default:
                        System.out.println("result: " + result);

                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });
        menuBar.add(openFolder);
        menuBar.add(loadConfig);
        jFrame.setJMenuBar(menuBar);


        jFrame.getContentPane().add(contentPanel);
        jFrame.pack();
        jFrame.setVisible(true);
        return jFrame;
    }

    public static void fillLabelPanel(Set<String> labels, JTextArea jTextArea, JPanel labelPanel) {
        labelPanel.removeAll();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        for (String str : labels) {
            str = StringUtils.deleteWhitespace(str);
            if (!StringUtils.isEmpty(str)) {
                JButton button = new JButton(str);
                button.addActionListener(new LabelButtonListener(jTextArea, str));
                labelPanel.add(button);
            }
        }
        labelPanel.revalidate();
        String configFilePath = "kakarotto.config";
        File configFile = new File(configFilePath);
        if (configFile.exists()) {
            configFile.delete();
        }
        for (String label : labels) {
            try {
                FileUtils.write(configFile, label + "\n", true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static String readTextFromList(int index, List<String> paths) {
        String filePath = paths.get(index);
        String text = "";
        try {
            text = FileUtils.readFileToString(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }


    public static void main(String[] args) {
        Kakarotto kakarotto = new Kakarotto();
        kakarotto.createUI();
    }

}
