package kr.ac.jbnu.se.tetris;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;





public class Tetris extends JFrame implements Serializable {

	private static final long serialVersionUID = 1L;
	private JLabel statusbar; // 텍스트 정보를 표시
    private JPanel lobbyPanel; // 로비 패널
    private JButton startButton; // 게임 시작 버튼
    private JButton settingButton;
    private JButton loadButton;
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
        
        
        settingButton=new JButton("환경 설정");
        
        loadButton=new JButton("불러오기");
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	loadGameStart();
            }
        });
        
        lobbyPanel.add(startButton);
        lobbyPanel.add(settingButton,BorderLayout.CENTER);
        lobbyPanel.add(loadButton,BorderLayout.CENTER);
        add(lobbyPanel, BorderLayout.CENTER);
        
        //bgm
        backgroundMusic=new Audio("src/kr/ac/jbnu/se/tetris/audio/lobby.wav",true);
        backgroundMusic.bgmStart();
        
        setSize(200, 400);// 테트리스 창의 크기 설정
        setTitle("Tetris"); // 창의 제목을 Tetris로 설정
        setDefaultCloseOperation(EXIT_ON_CLOSE); // 창을 닫으면 프로그램 종료
    }

    public JLabel getStatusBar() { // status를 반환하는 매서드
        return statusbar;
    }
    
    private void startGame() {
    	backgroundMusic.bgmStop();
    	backgroundMusic=new Audio("src/kr/ac/jbnu/se/tetris/audio/piano.wav",true);
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
    	backgroundMusic=new Audio("src/kr/ac/jbnu/se/tetris/audio/piano.wav",true);
        backgroundMusic.bgmStart();
        remove(lobbyPanel); // 로비 패널을 제거
        statusbar = new JLabel(" 0"); // 점수를 0으로 초기화
        add(statusbar, BorderLayout.SOUTH); // 남쪽에 추가
        Board board = Board.loadGame("C:\\Users\\USER\\eclipse-workspace\\tetris\\src\\kr\\ac\\jbnu\\se\\tetris\\audio\\load1.txt");
        add(board);
        board.start();
        revalidate(); // 화면 다시 그리기
        repaint();
        board.requestFocus(); // 게임 화면에 포커스 설정 중요 이거 없으면 없어진 로비창에서 계속 입력만됨 처리x
    }


    public static void main(String[] args) {
        Tetris game = new Tetris();
        game.setLocationRelativeTo(null); // 게임 창을 중앙에 배치
        game.setVisible(true); // 게임 창을 표시
    }
}