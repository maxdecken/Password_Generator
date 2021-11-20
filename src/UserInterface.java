import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;

public class UserInterface implements ActionListener {

    private Engine engine;

    private JFrame frame;
    private JTextField display, serviceName;
    private JLabel status;
    public static JPanel buttonPanel = new JPanel(new GridLayout(11, 2));
    public static JPanel buttonPanel2 = new JPanel(new GridLayout(11, 2));
    JToggleButton toggleButton;
    JButton generate, save;
    JTextField serviceText;
    JCheckBox checkNumber, checkLetter, checkSC, generateSwitch;
    JSlider slider;
    JTable passwordTable;
    JLabel numberDigits;
    private ChangeListener listener;

    private String enteredPasscode;
    private File passwordFile;

    public UserInterface(Engine e) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 6);
            UIManager.put("Component.arc", 6);
            UIManager.put("ProgressBar.arc", 6);
            UIManager.put("TextComponent.arc", 6);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        engine = e;
        listener = new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                // update text field when the slider value changes
                JSlider source = (JSlider) event.getSource();
                numberDigits.setText("Number of Digits: " + source.getValue());
            }
        };
        buttonPanel.setBackground(new Color(0, 0, 0, 0));
        buttonPanel2.setBackground(new Color(0, 0, 0, 0));
        makeFrame();
        frame.setVisible(true);
    }

    private void makeFrame() {
        frame = new JFrame("Password Generator v1.2");

        //Image Source: https://img.icons8.com/fluent/48/000000/key-security.png
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("icons8-schluessel-2-48.png")));

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel contentPane = (JPanel) frame.getContentPane();
        contentPane.setLayout(new BorderLayout(8, 8));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        /*toggleButton = new JToggleButton("Use own?");
        buttonPanel.add(toggleButton);
        toggleButton.addActionListener(this);*/
        generateSwitch = new JCheckBox("Generate password");
        generateSwitch.setSelected(true);
        buttonPanel.add(generateSwitch);
        generateSwitch.addActionListener(this);

        buttonPanel.add(new JLabel(""));

        slider = new JSlider(JSlider.HORIZONTAL, 4, 24, 12);
        numberDigits = new JLabel("Number of Digits: " + String.valueOf(slider.getValue()));
        buttonPanel.add(numberDigits);
        buttonPanel.add(slider);
        slider.addChangeListener(listener);

        buttonPanel.add(new JLabel(""));

        generate = addButton(buttonPanel, "Generate");

        checkNumber = new JCheckBox("numbers");
        checkNumber.setSelected(true);
        buttonPanel.add(checkNumber);

        checkLetter = new JCheckBox("letters");
        checkLetter.setSelected(true);
        buttonPanel.add(checkLetter);

        checkSC = new JCheckBox("special");
        checkSC.setSelected(true);
        buttonPanel.add(checkSC);

        buttonPanel.add(new JLabel(""));

        buttonPanel.add(new JLabel(""));
        buttonPanel.add(new JLabel(""));

        buttonPanel.add(new JLabel("Your new password:"));
        display = new JTextField();
        buttonPanel.add(display);

        buttonPanel.add(new JLabel(""));
        buttonPanel.add(new JLabel(""));

        buttonPanel.add(new JLabel("Service Name:"));
        serviceName = new JTextField();
        buttonPanel.add(serviceName);

        buttonPanel.add(new JLabel(""));

        save = addButton(buttonPanel, "Save");

        contentPane.add(buttonPanel, BorderLayout.CENTER);

        status = new JLabel("PW-Generator by Max Decken, ©2021");

        contentPane.add(status, BorderLayout.SOUTH);


        //Source: https://img.icons8.com/fluent/48/000000/add-key.png
        ImageIcon iconTab1 = createImageIcon("icons8-schlüssel-hinzufügen-48.png");
        tabbedPane.addTab("Generate", iconTab1, buttonPanel, "Generate and save your password here");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        addButton(buttonPanel2, "Open");
        addButton(buttonPanel2, "Refresh");

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Service");
        model.addColumn("Password");
        passwordTable = new JTable(model);

        JPanel p = new JPanel();
        JScrollPane scrollPane = new JScrollPane(passwordTable);
        scrollPane.setPreferredSize( new Dimension( 320, 250 ) );

        p.add(buttonPanel2);
        p.add(scrollPane);

        //Source: https://img.icons8.com/fluent/48/000000/safe-ok.png
        ImageIcon iconTab2 = createImageIcon("icons8-tresor-sicher-48.png");

        tabbedPane.addTab("Passwords", iconTab2, p, "View your saved passwords");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        //Add the tabbed pane to this panel.
        frame.add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        String result = "";

        if (command.equals("Generate")) {
            int digits = slider.getValue();
            result = engine.generatePassword(digits, checkNumber.isSelected(), checkLetter.isSelected(), checkSC.isSelected());
            redisplay(result);
        } else if (command.equals("Save")) {
            try {
                saveFile(display.getText());
            } catch (Exception ioException) {
                ioException.printStackTrace();
            }
        } else if (command.equals("Open")) {
            File toOpen = openFile();
            if (toOpen != null) {
                String passcode = JOptionPane.showInputDialog(frame, "Enter passcode:", null);
                enteredPasscode = passcode;
                passwordFile = toOpen;
                try {
                    fillTable(toOpen, passcode);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } else if (command.equals("Refresh")) {
            if (passwordFile != null) {
                if (enteredPasscode == null) {
                    String passcode = JOptionPane.showInputDialog(frame, "Enter passcode:", null);
                    enteredPasscode = passcode;
                }
                try {
                    fillTable(passwordFile, enteredPasscode);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } /*else if (command.equals("Use own?")) {
            toggleButton.setText("Generate?");
            ;
        } else if (command.equals("Generate?")) {
            toggleButton.setText("Use own?");
        }

        if (toggleButton.getText().equals("Use own?")) {
            enableSaveLayout();
        } else {
            disableSaveLayout();
        }*/

        if (generateSwitch.isSelected() == true) {
            enableSaveLayout();
        } else {
            disableSaveLayout();
        }
    }

    private void disableSaveLayout() {
        generate.setEnabled(false);
        slider.setEnabled(false);
        checkLetter.setEnabled(false);
        checkNumber.setEnabled(false);
        checkSC.setEnabled(false);
    }

    private void enableSaveLayout() {
        generate.setEnabled(true);
        slider.setEnabled(true);
        checkLetter.setEnabled(true);
        checkNumber.setEnabled(true);
        checkSC.setEnabled(true);
    }

    private JButton addButton(Container panel, String buttonText) {
        JButton button = new JButton(buttonText);
        button.addActionListener(this);
        panel.add(button);
        return button;
    }

    private void redisplay(String text) {
        display.setText(text);
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    private void saveFile(String password) throws Exception {
        JFileChooser fileChooser = new JFileChooser();
        if (passwordFile != null) {
            fileChooser.setCurrentDirectory(passwordFile);
        }
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Specify a file to save");

        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String passcode;
            if (fileToSave.exists()) {
                passcode = JOptionPane.showInputDialog(frame, "Enter passcode:", null);
            } else {
                passcode = JOptionPane.showInputDialog(frame, "Create a short passcode(IMPORTANT TO REMEMBER!):", null);
            }
            enteredPasscode = passcode;
            passwordFile = fileToSave;
            engine.writeFile(password, fileToSave, serviceName.getText(), passcode);
        }
    }

    private File openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        if (passwordFile != null) {
            fileChooser.setCurrentDirectory(passwordFile);
        }
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Specify a file to open");

        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }

    private void fillTable(File toOpen, String passcode) throws Exception {
        try {

            String[] decrypted = engine.openFile(toOpen, passcode);
            DefaultTableModel model = (DefaultTableModel) passwordTable.getModel();
            //clear before adding values again
            for (int i = model.getRowCount() - 1; i >= 0; i--) {
                model.removeRow(i);
            }
            for (int row = 0; row < decrypted.length / 2; row++) {
                String part1 = decrypted[row + row];
                String part2 = decrypted[row + row + 1];
                model.addRow(new Object[]{part1, part2});
            }

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }

    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = UserInterface.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;

        }
    }
}

