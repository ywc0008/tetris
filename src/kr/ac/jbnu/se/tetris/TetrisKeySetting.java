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
        changeButton.setText("변경");

        cancelButton = new JButton();
        cancelButton.setText("취소");

        keySettingsPanel.add(new JLabel("왼쪽으로 이동", SwingConstants.CENTER));
        keySettingsPanel.add(moveLeftButton);
        keySettingsPanel.add(new JLabel("오른쪽으로 이동", SwingConstants.CENTER));
        keySettingsPanel.add(moveRightButton);
        keySettingsPanel.add(new JLabel("오른쪽으로 회전", SwingConstants.CENTER));
        keySettingsPanel.add(rotateRightButton);
        keySettingsPanel.add(new JLabel("왼쪽으로 회전", SwingConstants.CENTER));
        keySettingsPanel.add(rotateLeftButton);
        keySettingsPanel.add(new JLabel("아래로 이동", SwingConstants.CENTER));
        keySettingsPanel.add(moveDownButton);
        keySettingsPanel.add(new JLabel("최하단으로 이동", SwingConstants.CENTER));
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
//        // frame에 main 패널 add
        add(mainPanel);

        setLocationRelativeTo(null);

        // 변경 확인 button
        changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(null, "키를 변경하시겠습니까?", "키 변경 확인", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {

                    dispose();
                }
            }
        });

        // 변경 취소 버튼
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(null, "변경사항이 적용되지 않습니다." + "\n" + "정말로 나가시겠습니까?", "나가기 확인", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    // If the user selects "예", close the TetrisKeySetting frame
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
                        System.out.println(KeyEvent.getKeyText(changeKeyCode) + "키가 눌렸습니다. 키 코드: " + changeKeyCode);
                        setKeyButtonText(button, changeKeyCode, keyName);

                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        // 키 릴리스 이벤트 처리
                    }

                    @Override
                    public void keyTyped(KeyEvent e) {
                        // 키 타입 이벤트를 처리
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

    //바로 확인 하고 싶을 시 메인 메소드 주석 해제
    public static void main(String[] args) {
        TetrisKeySetting keySettings = new TetrisKeySetting();
        keySettings.setVisible(true);
    }
}


