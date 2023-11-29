package kr.ac.jbnu.se.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class TetrisKeySetting extends JFrame {

    public static HashMap<String, Integer> keyMappings;
    private JButton moveLeftButton;
    private JButton moveRightButton;
    private JButton rotateRightButton;
    private JButton rotateLeftButton;
    private JButton moveDownButton;
    private JButton dropDownButton;
    private JButton changeButton;
    private JButton cancelButton;
    private int moveLeftKeyCode = KeyEvent.VK_LEFT;
    private int moveRightKeyCode = KeyEvent.VK_RIGHT;
    private int rotateLeftKeyCode = KeyEvent.VK_UP;
    private int rotateRightKeyCode = KeyEvent.VK_DOWN;
    private int moveDownKeyCode = KeyEvent.VK_D;
    private int dropDownKeyCode = KeyEvent.VK_SPACE;

    public TetrisKeySetting() {
        setTitle("Tetris Key Setting");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 600);

        keyMappings = new HashMap<>();

        keyMappings.put("MoveLeft", moveLeftKeyCode);
        keyMappings.put("MoveRight", moveRightKeyCode);
        keyMappings.put("RotateLeft", rotateLeftKeyCode);
        keyMappings.put("RotateRight", rotateRightKeyCode);
        keyMappings.put("MoveDown", moveDownKeyCode);
        keyMappings.put("DropDown", dropDownKeyCode);

        System.out.println(keyMappings);

        // Create a panel to hold the key settings components
        JPanel keySettingsPanel = new JPanel();
        keySettingsPanel.setLayout(new GridLayout(6, 2, 1, 1));

        moveLeftButton = new JButton();
        setKeyButtonText(moveLeftButton, moveLeftKeyCode, "MoveLeft");

        moveRightButton = new JButton();
        setKeyButtonText(moveRightButton, moveRightKeyCode, "MoveRight");

        rotateLeftButton = new JButton();
        setKeyButtonText(rotateLeftButton, rotateLeftKeyCode, "RotateLeft");

        rotateRightButton = new JButton();
        setKeyButtonText(rotateRightButton, rotateRightKeyCode, "RotateRight");

        moveDownButton = new JButton();
        setKeyButtonText(moveDownButton, moveDownKeyCode, "MoveDown");

        dropDownButton = new JButton();
        setKeyButtonText(dropDownButton, dropDownKeyCode, "DropDown");

        changeButton = new JButton();
        changeButton.setText("�1�3�7�4");

        cancelButton = new JButton();
        cancelButton.setText("�4�5�2�1");

        keySettingsPanel.add(new JLabel("�3�5�3�4�3�7�0�9 �3�3�9�4", SwingConstants.CENTER));
        keySettingsPanel.add(moveLeftButton);
        keySettingsPanel.add(new JLabel("�2�7�0�3�3�4�3�7�0�9 �3�3�9�4", SwingConstants.CENTER));
        keySettingsPanel.add(moveRightButton);
        keySettingsPanel.add(new JLabel("�2�7�0�3�3�4�3�7�0�9 �6�7�3�7", SwingConstants.CENTER));
        keySettingsPanel.add(rotateRightButton);
        keySettingsPanel.add(new JLabel("�3�5�3�4�3�7�0�9 �6�7�3�7", SwingConstants.CENTER));
        keySettingsPanel.add(rotateLeftButton);
        keySettingsPanel.add(new JLabel("�2�3�9�3�0�9 �3�3�9�4", SwingConstants.CENTER));
        keySettingsPanel.add(moveDownButton);
        keySettingsPanel.add(new JLabel("�4�5�6�9�9�3�3�7�0�9 �3�3�9�4", SwingConstants.CENTER));
        keySettingsPanel.add(dropDownButton);

        changeKeyButton(moveLeftButton, "MoveLeft");
        changeKeyButton(moveRightButton, "MoveRight");
        changeKeyButton(rotateRightButton, "RotateRight");
        changeKeyButton(rotateLeftButton, "RotateLeft");
        changeKeyButton(moveDownButton, "MoveDown");
        changeKeyButton(dropDownButton, "DropDown");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(changeButton);
        buttonPanel.add(cancelButton);

//        // Add key settings panel, separator, and button panel to the main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(keySettingsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
//
//        // frame�2�3 main �5�9�8�1 add
        add(mainPanel);

        setLocationRelativeTo(null);

        // �1�3�7�4 �6�2�3�7 button
        changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(null, "�5�5�0�7 �1�3�7�4�6�9�2�7�7�5�2�8�9�1�8�3?", "�5�5 �1�3�7�4 �6�2�3�7", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {

                    dispose();
                }
            }
        });

        // �1�3�7�4 �4�5�2�1 �0�3�5�1
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(null, "�1�3�7�4�1�7�6�0�3�3 �3�4�3�0�9�7�3�1 �2�9�2�8�9�1�9�9." + "\n" + "�3�4�0�1�0�9 �8�1�7�5�2�7�7�5�2�8�9�1�8�3?", "�8�1�7�5�8�5 �6�2�3�7", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    // If the user selects "�2�9", close the TetrisKeySetting frame
                    dispose();
                }
            }
        });
    }

    private void setKeyButtonText(JButton button, int keyCode, String keyName) {
        String keyText = KeyEvent.getKeyText(keyCode);
//        String mapping = keyMappings.get(keyCode);
        button.setText(" Key: " + keyText);
        keyMappings.put(keyName, keyCode);
        for (String key : keyMappings.keySet()) {
            String value = keyMappings.get(key).toString();
        }
    }

    public void changeKeyButton(JButton button, String keyName) {

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFrame frame = new JFrame("Key Change");
                frame.setSize(300, 200);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                frame.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        int changeKeyCode = e.getKeyCode();
                        System.out.println(KeyEvent.getKeyText(changeKeyCode) + "�5�5�7�5 �8�3�0�3�2�8�9�1�9�9. �5�5 �4�9�9�3: " + changeKeyCode);
                        setKeyButtonText(button, changeKeyCode, keyName);

                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        // �5�5 �0�3�0�5�2�1 �3�3�1�5�5�7 �4�9�0�5
                    }

                    @Override
                    public void keyTyped(KeyEvent e) {
                        // �5�5 �5�3�3�0 �3�3�1�5�5�7�0�7 �4�9�0�5
                    }
                });


                frame.setFocusable(true);
                frame.requestFocusInWindow();

                frame.setVisible(true);
            }
        });

    }

    public HashMap<String, Integer> getKeyMappings() {
        return this.keyMappings;
    }

    //�0�1�0�9 �6�2�3�7 �6�9�7�9 �2�3�3�5 �2�7 �0�3�3�7 �0�3�2�1�9�3 �3�5�1�0 �6�7�3�1
    public static void main(String[] args) {
        TetrisKeySetting keySettings = new TetrisKeySetting();
        keySettings.setVisible(true);
    }
}


