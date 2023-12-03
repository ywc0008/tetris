package kr.ac.jbnu.se.tetris;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


import javax.swing.*;


public class Tetris extends JFrame {
    private static final int WIDTH_SIZE=800;
    private static final int HEIGHT_SIZE=800;

    JLabel statusbar; // 텍스트 정보를 표시
    JPanel lobbyPanel; // 로비 패널
    JButton startButton; // 게임 시작 버튼
    JButton settingButton;
    JButton loadButton;
    JButton rankButton;
    //bgm
    private transient Audio backgroundMusic;
    private TetrisKeySetting keySetting;
    private static final String ACTION_1 = "user.dir";  // Compliant
    String lobbywavpath=System.getProperty(ACTION_1)+"\\src\\kr\\ac\\jbnu\\se\\tetris\\files\\lobby.wav";
    String scoreRecord=System.getProperty(ACTION_1)+"\\src\\kr\\ac\\jbnu\\se\\tetris\\files\\score.txt";
    String timeModeScoreRecord=System.getProperty(ACTION_1)+"\\src\\kr\\ac\\jbnu\\se\\tetris\\files\\scoretimemode.txt";
    String pianowavpath=System.getProperty(ACTION_1)+"\\\\src\\\\kr\\\\ac\\\\jbnu\\\\se\\\\tetris\\\\files\\\\piano.wav";
    String savepath=System.getProperty(ACTION_1)+"\\\\src\\\\kr\\\\ac\\\\jbnu\\\\se\\\\tetris\\\\files\\\\load1.ser";
    String mainbackground=System.getProperty(ACTION_1)+"\\\\src\\\\kr\\\\ac\\\\jbnu\\\\se\\\\tetris\\\\files\\\\main.png";
    String rankbackgroundpath=System.getProperty(ACTION_1)+"\\src\\kr\\ac\\jbnu\\se\\tetris\\files\\rankbackground.png";
    ImageIcon icon;
    public Tetris() {
        icon = new ImageIcon(mainbackground);

        //배경 Panel 생성후 컨텐츠페인으로 지정
        lobbyPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                // Approach 1: Dispaly image at at full size
                g.drawImage(icon.getImage(), 0, 0, null);
                setOpaque(false); //그림을 표시하게 설정,투명하게 조절
                super.paintComponent(g);
            }
        };




        startButton = new JButton("게임 시작");
        settingButton=new JButton("환경 설정");
        loadButton=new JButton("불러오기");
        rankButton=new JButton("랭킹");
        Font buttonFont = new Font("Dialog", Font.BOLD, 20);
        startButton.setFont(buttonFont);
        settingButton.setFont(buttonFont);
        loadButton.setFont(buttonFont);
        rankButton.setFont(buttonFont);

// 텍스트 색상 설정
        Color textColor = Color.WHITE;
        startButton.setForeground(textColor);
        settingButton.setForeground(textColor);
        loadButton.setForeground(textColor);
        rankButton.setForeground(textColor);



// 배경 색상 설정
        Color backgroundColor = new Color(0, 0, 0);  // 예시 색상 (파란색)
        startButton.setBackground(backgroundColor);
        settingButton.setBackground(backgroundColor);
        loadButton.setBackground(backgroundColor);
        rankButton.setBackground(backgroundColor);

// 테두리 없애기

        startButton.setBounds(100,450,200,100);
        settingButton.setBounds(100,600,200,100);
        loadButton.setBounds(500,450,200,100);
        rankButton.setBounds(500,600,200,100);
        lobbyPanel.add(startButton);
        lobbyPanel.add(settingButton);
        lobbyPanel.add(loadButton);
        lobbyPanel.add(rankButton);
        lobbyPanel.setLayout(null);
        add(lobbyPanel);


        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showStartConfirmationDialog(); // 게임 시작 버튼을 클릭하면 게임을 시작합니다.
            }

            private void showStartConfirmationDialog() {
                String[] mode = {"일반모드", "타임어택 모드"};

                JFrame modeFrame=new JFrame("게 임 모 드");
                // 프레임 설정
                modeFrame.setTitle("게임 모드");
                modeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                modeFrame.setSize(300, 150);
                modeFrame.setLocationRelativeTo(null);
                modeFrame.getContentPane().setBackground(Color.BLACK);
                modeFrame.setLayout(null);

                JLabel label=new JLabel("게임 모드를 선택하세요");
                label.setBounds(80,10,160,10);
                label.setForeground(Color.WHITE);
                modeFrame.add(label);

                // 버튼1 생성 및 설정
                JButton button1 = new JButton(mode[0]);
                button1.setBounds(20, 50, 110, 30);
                button1.setBackground(Color.WHITE);
                button1.setForeground(Color.BLACK);
                button1.addActionListener(e -> {
                    timemode = false;
                    startGame();
                });
                modeFrame.add(button1);

                // 버튼2 생성 및 설정
                JButton button2 = new JButton(mode[1]);
                button2.setBounds(170, 50, 110, 30);
                button2.setBackground(Color.WHITE);
                button2.setForeground(Color.BLACK);
                button2.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        timemode = true;
                        startGame();
                    }
                });
                modeFrame.add(button2);
                modeFrame.setVisible(true);
            }
        });



        loadButton.addActionListener(e -> loadGameStart());

        settingButton.addActionListener(e -> openSetting());

        rankButton.addActionListener(e -> loadRank());



        //bgm

        backgroundMusic=new Audio(lobbywavpath);
        backgroundMusic.bgmStart();

        setSize(WIDTH_SIZE, HEIGHT_SIZE);// 테트리스 창의 크기 설정
        setTitle("Tetris"); // 창의 제목을 Tetris로 설정

        setDefaultCloseOperation(EXIT_ON_CLOSE); // 창을 닫으면 프로그램 종료
        keySetting = new TetrisKeySetting();
    }

    public void showLobby() {
        backgroundMusic.bgmStop(); // 현재 재생 중인 BGM 중지
        backgroundMusic = new Audio(lobbywavpath); // 로비 BGM 재생
        backgroundMusic.bgmStart();
        getContentPane().removeAll(); // 현재 모든 구성 요소를 제거
        getContentPane().add(lobbyPanel); // 로비 패널 추가
        remove(statusbar); // 점수 표시 제거
        revalidate(); // 화면 다시 그리기
        repaint();
    }


    public JLabel getStatusBar() { // status를 반환하는 매서드
        return statusbar;
    }

    private boolean timemode=false;

    public boolean getTimeMode()
    {
        return timemode;
    }

    String[] record1=new String[3];
    String[] record2=new String[3];
    public void loadRank() {
        try (BufferedReader reader = new BufferedReader(new FileReader(scoreRecord))) {
            for (int i = 0; i < 3; i++) {
                String line = reader.readLine();
                record1[i] = line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(timeModeScoreRecord))) {
            for (int i = 0; i < 3; i++) {
                String line = reader.readLine();
                record2[i] = line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JFrame frame=new JFrame("Rank");
        JLabel label1=new JLabel();
        JLabel label2=new JLabel();
        JButton button=new JButton("확인");
        ImageIcon imageIcon = new ImageIcon(rankbackgroundpath); // 이미지 파일 경로로 변경
        // 이미지를 표시할 JLabel 생성
        JLabel imageLabel = new JLabel(imageIcon);
        frame.setSize(500,500);
        frame.setLayout(null); // 레이아웃 관리자 설정
        Font labelFont = new Font("Dialog", Font.BOLD, 20);
        label1.setFont(labelFont);
        label2.setFont(labelFont);
        // 폰트의 색을 흰색으로 설정
        label1.setForeground(Color.WHITE);
        label2.setForeground(Color.WHITE);
        label1.setText("<html>"+"NORMAL RANK"+"<br>"+"<br>"+record1[0]+"<br>"+record1[1]+"<br>"+record1[2]+"</html>");
        label2.setText("<html>"+"TIME ATTACK RANK"+"<br>"+"<br>"+record2[0]+"<br>"+record2[1]+"<br>"+record2[2]+"</html>");

        button.addActionListener(e -> frame.dispose());

        imageLabel.setBounds(0, 0, 500, 500);
        label1.setBounds(50, 50, 200, 200);
        label2.setBounds(270, 50, 200, 200);
        button.setBounds(200, 400, 100, 50);


        frame.add(label1);
        frame.add(label2);
        frame.add(imageLabel);
        frame.add(button);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    private void startGame() {
        backgroundMusic.bgmStop();
        backgroundMusic=new Audio(pianowavpath);
        backgroundMusic.bgmStart();
        remove(lobbyPanel); // 로비 패널을 제거
        statusbar = new JLabel(" 0"); // 점수를 0으로 초기화
        add(statusbar, BorderLayout.SOUTH); // 남쪽에 추가
        Board board = new Board(this);
        add(board);
        board.start();
        revalidate(); // 화면 다시 그리기
        repaint();
        board.requestFocus(); // 게임 화면에 포커스 설정 중요 이거 없으면 없어진 로비창에서 계속 입력만됨 처리x
    }



    private void loadGameStart() {
        backgroundMusic.bgmStop();
        backgroundMusic=new Audio(pianowavpath);
        backgroundMusic.bgmStart();
        remove(lobbyPanel); // 로비 패널을 제거
        statusbar = new JLabel(" 0"); // 점수를 0으로 초기화
        add(statusbar, BorderLayout.SOUTH); // 남쪽에 추가
        Board board = new Board(this);
        add(board);
        board.start();
        revalidate(); // 화면 다시 그리기
        repaint();
        board.requestFocus(); // 게임 화면에 포커스 설정 중요 이거 없으면 없어진 로비창에서 계속 입력만됨 처리x
        board.loadGame(savepath);
    }
    private void openSetting() { //Setting 화면 출력
        keySetting.setVisible(true);
    }

    public static void main(String[] args) {
        Tetris game = new Tetris();
        game.setLocationRelativeTo(null); // 게임 창을 중앙에 배치
        game.setVisible(true); // 게임 창을 표시
    }
}