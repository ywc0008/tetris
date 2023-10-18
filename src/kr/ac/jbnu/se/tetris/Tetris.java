package kr.ac.jbnu.se.tetris;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JSeparator;




public class Tetris extends JFrame implements Serializable {

	private static final long serialVersionUID = 1L;
	private JLabel statusbar; // 텍스트 정보를 표시
    private JPanel lobbyPanel; // 로비 패널
    private JButton startButton; // 게임 시작 버튼

    private JButton twoPlayerStartButton; // 2인용 게임 시작 버튼

    private JButton settingButton;
    private JButton loadButton;
    private JButton rankButton;
    //bgm
    private Audio backgroundMusic;

    public Tetris() {
        lobbyPanel = new JPanel();
        
        startButton = new JButton("게임 시작");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame(); // 게임 시작 버튼을 클릭하면 게임을 시작합니다.
            }
        });

        twoPlayerStartButton = new JButton("2인용 게임 시작");
        twoPlayerStartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startTwoPlayerGame(); // 2인용 게임 시작 버튼을 클릭하면 2인용 게임을 시작합니다.
            }
        });

        lobbyPanel.add(twoPlayerStartButton,BorderLayout.SOUTH);
        
        settingButton=new JButton("환경 설정");
        
        loadButton=new JButton("불러오기");
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	loadGameStart();
            }
        });
        
        rankButton=new JButton("랭킹");
        rankButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	loadRank();
            }
        });

        lobbyPanel.add(startButton,BorderLayout.NORTH);
        lobbyPanel.add(settingButton,BorderLayout.CENTER);
        lobbyPanel.add(loadButton,BorderLayout.SOUTH);
        lobbyPanel.add(rankButton,BorderLayout.CENTER);
        add(lobbyPanel, BorderLayout.CENTER);
        
        //bgm
        String lobbywavpath=System.getProperty("user.dir")+"\\src\\kr\\ac\\jbnu\\se\\tetris\\audio\\lobby.wav";
        backgroundMusic=new Audio(lobbywavpath,true);
        backgroundMusic.bgmStart();
        
        setSize(200, 400);// 테트리스 창의 크기 설정
        setTitle("Tetris"); // 창의 제목을 Tetris로 설정
        setDefaultCloseOperation(EXIT_ON_CLOSE); // 창을 닫으면 프로그램 종료
    }

    public JLabel getStatusBar() { // status를 반환하는 매서드
        return statusbar;
    }
    static String[] record=new String[3];
    public void loadRank()
    {
		try
		{
			
			String scoreRecord=System.getProperty("user.dir")+"\\src\\kr\\ac\\jbnu\\se\\tetris\\audio\\score.txt";
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
    	String pianowavpath=System.getProperty("user.dir")+"\\\\src\\\\kr\\\\ac\\\\jbnu\\\\se\\\\tetris\\\\audio\\\\piano.wav";
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

    private void startTwoPlayerGame() {

        backgroundMusic.bgmStop();
        String pianowavpath = System.getProperty("user.dir") + "\\\\src\\\\kr\\\\ac\\\\jbnu\\\\se\\\\tetris\\\\audio\\\\piano.wav";
        backgroundMusic = new Audio(pianowavpath, true);
        backgroundMusic.bgmStart();
        remove(lobbyPanel); // 로비 패널을 제거

        JLabel statusbar1 = new JLabel("Player 1: 0");
        add(statusbar1, BorderLayout.NORTH);
        JLabel statusbar2 = new JLabel("Player 2: 0");
        add(statusbar2, BorderLayout.SOUTH);

        // 첫 번째 플레이어 게임 보드 생성 및 추가
        TwoPlayerBoard board1 = new TwoPlayerBoard(this);
        add(board1, BorderLayout.WEST);

        // 경계선 추가
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        add(separator, BorderLayout.CENTER);

        // 두 번째 플레이어 게임 보드 생성 및 추가
        TwoPlayerBoard board2 = new TwoPlayerBoard(this);
        add(board2, BorderLayout.EAST);

        board1.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        board2.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        board1.setPreferredSize(new Dimension(150, 400)); // assuming a width of 150 and height of 400 for example
        board2.setPreferredSize(new Dimension(150, 400));

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(1, 2)); // Use GridLayout to place boards side by side
        gamePanel.add(board1);
        gamePanel.add(board2);

        add(gamePanel);

        board1.start();
        board2.start();

        revalidate(); // 화면 다시 그리기
        repaint();
    }


    private void loadGameStart() {
        backgroundMusic.bgmStop();
    	String pianowavpath=System.getProperty("user.dir")+"\\\\src\\\\kr\\\\ac\\\\jbnu\\\\se\\\\tetris\\\\audio\\\\piano.wav";
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
    	String savepath=System.getProperty("user.dir")+"\\\\src\\\\kr\\\\ac\\\\jbnu\\\\se\\\\tetris\\\\audio\\\\load1.ser";
        board.loadGame(savepath);
    }


    public static void main(String[] args) {
        Tetris game = new Tetris();
        game.setLocationRelativeTo(null); // 게임 창을 중앙에 배치
        game.setVisible(true); // 게임 창을 표시
    }
}