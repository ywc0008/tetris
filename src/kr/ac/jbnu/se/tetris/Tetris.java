package kr.ac.jbnu.se.tetris;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;


import javax.swing.*;


public class Tetris extends JFrame implements Serializable {

    private static final long serialVersionUID = 1L;
    JLabel statusbar; // 텍스트 정보를 표시
    JPanel lobbyPanel; // 로비 패널
    JButton startButton; // 게임 시작 버튼
    JButton settingButton;
    JButton loadButton;
    JButton rankButton;
    //bgm
    private Audio backgroundMusic;
    private TetrisKeySetting keySetting;
    String lobbywavpath=System.getProperty("user.dir")+"\\src\\kr\\ac\\jbnu\\se\\tetris\\audio\\lobby.wav";
    String scoreRecord=System.getProperty("user.dir")+"\\src\\kr\\ac\\jbnu\\se\\tetris\\audio\\score.txt";
    String timeModeScoreRecord=System.getProperty("user.dir")+"\\src\\kr\\ac\\jbnu\\se\\tetris\\audio\\scoretimemode.txt";
    String pianowavpath=System.getProperty("user.dir")+"\\\\src\\\\kr\\\\ac\\\\jbnu\\\\se\\\\tetris\\\\audio\\\\piano.wav";
    String savepath=System.getProperty("user.dir")+"\\\\src\\\\kr\\\\ac\\\\jbnu\\\\se\\\\tetris\\\\audio\\\\load1.ser";

    public Tetris() {
        lobbyPanel = new JPanel(new GridLayout(4,1));

        Dimension buttonSize=new Dimension(200,50);
        startButton = new JButton("게임 시작");
        settingButton=new JButton("환경 설정");
        loadButton=new JButton("불러오기");
        rankButton=new JButton("랭킹");
        startButton.setPreferredSize(buttonSize);
        settingButton.setPreferredSize(buttonSize);
        loadButton.setPreferredSize(buttonSize);
        rankButton.setPreferredSize(buttonSize);




        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showStartConfirmationDialog(); // 게임 시작 버튼을 클릭하면 게임을 시작합니다.
            }

            private void showStartConfirmationDialog() {
                String[] mode={"일반모드","타임어택 모드"};
                int result = JOptionPane.showOptionDialog(null, "게임 모드를 선택해주세요", "게 임 모 드", 0,0,null,mode,mode[1]);
                if (result == 0) {
                    timemode=false;
                    startGame(); // "예"를 선택하면 게임을 시작합니다.
                }
                else if (result == 1) {
                    timemode=true;
                    startGame(); // "예"를 선택하면 게임을 시작합니다.
                }
            }
        });



        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadGameStart();
            }
        });


        rankButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadRank();
            }
        });

        settingButton = new JButton("환경 설정");
        settingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openSetting();
            }
        });

        lobbyPanel.add(startButton,BorderLayout.CENTER);
        lobbyPanel.add(settingButton,BorderLayout.CENTER);
        lobbyPanel.add(loadButton,BorderLayout.CENTER);
        lobbyPanel.add(rankButton,BorderLayout.CENTER);
        add(lobbyPanel, BorderLayout.CENTER);

        //bgm

        backgroundMusic=new Audio(lobbywavpath,true);
        backgroundMusic.bgmStart();

        setSize(400, 800);// 테트리스 창의 크기 설정
        setTitle("Tetris"); // 창의 제목을 Tetris로 설정
        setDefaultCloseOperation(EXIT_ON_CLOSE); // 창을 닫으면 프로그램 종료
        keySetting = new TetrisKeySetting();
    }

    public void showLobby() {
        backgroundMusic.bgmStop(); // 현재 재생 중인 BGM 중지
        backgroundMusic = new Audio(lobbywavpath, true); // 로비 BGM 재생
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

    public boolean timemode=false;

    public boolean getTimeMode()
    {
        return timemode;
    }

    static String[] record1=new String[3];
    static String[] record2=new String[3];
    public void loadRank()
    {
        try
        {
            File file=new File(scoreRecord);
            BufferedReader reader=new BufferedReader(new FileReader(file));
            for(int i=0;i<3;i++)
            {
                String line=reader.readLine();
                record1[i]=line;
            }
            reader.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        try
        {
            File file=new File(timeModeScoreRecord);
            BufferedReader reader=new BufferedReader(new FileReader(file));
            for(int i=0;i<3;i++)
            {
                String line=reader.readLine();
                record2[i]=line;
            }
            reader.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        JFrame frame=new JFrame("Rank");
        JLabel label1=new JLabel();
        JLabel label2=new JLabel();
        JButton button=new JButton("확인");

        frame.setSize(300,300);
        frame.setLayout(new BorderLayout()); // 레이아웃 관리자 설정
        label1.setText("<html>"+"NORMAL RANK"+"<br>"+record1[0]+"<br>"+record1[1]+"<br>"+record1[2]+"</html>");
        label2.setText("<html>"+"TIME ATTACK RANK"+"<br>"+record2[0]+"<br>"+record2[1]+"<br>"+record2[2]+"</html>");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // 팝업 창 닫기
            }
        });
        frame.add(label1,BorderLayout.WEST);
        frame.add(label2,BorderLayout.EAST);
        frame.add(button,BorderLayout.SOUTH);
        frame.setVisible(true);
    }


    private void startGame() {
        backgroundMusic.bgmStop();
        backgroundMusic=new Audio(pianowavpath,true);
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
        backgroundMusic=new Audio(pianowavpath,true);
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
//      TetrisKeySetting keySetting = new TetrisKeySetting();
        keySetting.setVisible(true);
    }

    public static void main(String[] args) {
        Tetris game = new Tetris();
        game.setLocationRelativeTo(null); // 게임 창을 중앙에 배치
        game.setVisible(true); // 게임 창을 표시
    }
}