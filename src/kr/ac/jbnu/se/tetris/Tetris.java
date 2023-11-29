package kr.ac.jbnu.se.tetris;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


public class Tetris extends JFrame implements Serializable {

	private static final long serialVersionUID = 1L;
	JLabel levelbar;
	JLabel statusbar; // 텍스트 정보를 표시
    JPanel lobbyPanel; // 로비 패널
    JButton startButton; // 게임 시작 버튼
    JButton settingButton;
    JButton loadButton;
    JButton rankButton;
    LeftBoard leftPanel;
    RightBoard rightPanel;
    JPanel gamePanel;
    //bgm
    private Audio backgroundMusic;
    private TetrisKeySetting keySetting;
    String lobbywavpath=System.getProperty("user.dir")+"\\src\\kr\\ac\\jbnu\\se\\tetris\\audio\\lobby.wav";
    String scoreRecord=System.getProperty("user.dir")+"\\src\\kr\\ac\\jbnu\\se\\tetris\\audio\\score.txt";
    String pianowavpath=System.getProperty("user.dir")+"\\\\src\\\\kr\\\\ac\\\\jbnu\\\\se\\\\tetris\\\\audio\\\\piano.wav";
    String savepath=System.getProperty("user.dir")+"\\\\src\\\\kr\\\\ac\\\\jbnu\\\\se\\\\tetris\\\\audio\\\\load1.ser";

    public Tetris() {
    	lobbyPanel = new JPanel(new GridLayout(4,3));

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
                startGame(); // 게임 시작 버튼을 클릭하면 게임을 시작합니다.
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
        add(lobbyPanel,BorderLayout.CENTER);

        //bgm

        backgroundMusic=new Audio(lobbywavpath,true);
        backgroundMusic.bgmStart();

        setSize(600, 800);// 테트리스 창의 크기 설정
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
        remove(levelbar);
        remove(statusbar); // 점수 표시 제거
        revalidate(); // 화면 다시 그리기
        repaint();
    }


    public JLabel getStatusBar() { // status를 반환하는 매서드
        return statusbar;
    }
    public JLabel getLevelBar() { // status를 반환하는 매서드
        return levelbar;
    }

    static String[] record=new String[3];
    public void loadRank()
    {
		try
		{


			File file=new File(scoreRecord);
			BufferedReader reader=new BufferedReader(new FileReader(file));
			for(int i=0;i<3;i++)
			{
				String line=reader.readLine();
				record[i]=line;
			}
			reader.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
    	JFrame frame=new JFrame("Rank");
    	JLabel label=new JLabel();
    	JButton button=new JButton("확인");

    	frame.setSize(300,300);
    	frame.setLayout(new BorderLayout()); // 레이아웃 관리자 설정
    	label.setText("<html>"+"RANK"+"<br>"+record[0]+"<br>"+record[1]+"<br>"+record[2]+"</html>");;
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // 팝업 창 닫기
            }
        });
    	frame.add(label,BorderLayout.CENTER);
    	frame.add(button,BorderLayout.SOUTH);
        frame.setVisible(true);
    }


    private void startGame() {
    	backgroundMusic.bgmStop();
    	backgroundMusic=new Audio(pianowavpath,true);
        backgroundMusic.bgmStart();
        remove(lobbyPanel); // 로비 패널을 제거
        statusbar = new JLabel(" 0"); // 점수를 0으로 초기화
        levelbar=new JLabel("LEVEL 1");
        add(statusbar, BorderLayout.SOUTH); // 남쪽에 추가
        add(levelbar, BorderLayout.NORTH); // 남쪽에 추가
        Board board = new Board(this);
        board.setBorder(new LineBorder(Color.BLACK,2));
        add(board, BorderLayout.CENTER);
        board.start();
        leftPanel = new LeftBoard();
        rightPanel = new RightBoard();
        gamePanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(100,160));
        rightPanel.setPreferredSize(new Dimension(100,800));
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel,BorderLayout.EAST);
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
        board.setBorder(new LineBorder(Color.BLACK,2));
        add(board);
        board.start();
        leftPanel = new LeftBoard();
        rightPanel = new RightBoard();
        gamePanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(100,160));
        rightPanel.setPreferredSize(new Dimension(100,800));
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel,BorderLayout.EAST);
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
