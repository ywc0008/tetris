package kr.ac.jbnu.se.tetris;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;


import javax.swing.border.EmptyBorder;

import java.io.*;
import java.nio.file.Paths;


public class Board extends JPanel implements ActionListener,Serializable {

	private static final long serialVersionUID = 1L;
	static final int BOARD_WIDTH = 10; //게임 창 크기 가로 10
	static final int BOARD_HEIGHT = 22; //세로 22
	//타임어택모드
	public static boolean timemode;
	private int elapsedTime; // 추가: 경과 시간을 저장하는 변수
	private int timeLimitInSeconds = 7; // 추가: 시간 제한을 정의

	Timer timer;
	boolean isFallingFinished = false;
	boolean isStarted = false;
	boolean isPaused = false;
	int numLinesRemoved;
	int curX = 0; //현재 블록의 위치를 나타내는 변수
	int curY = 0; //현재 블록의 위치를 나타내는 변수
	JLabel statusbar; //게임 상태를 표시하는 레이블
	JLabel levelbar;
	Shape curPiece; //현재 Tetris블록을 나타내는 객체
	Tetrominoes[] shapeBoard;

	String baseDir = System.getProperty("user.dir");
	String commonPath = Paths.get(baseDir, "src", "kr", "ac", "jbnu", "se", "tetris", "audio").toString();

	String savestatusbarpath = Paths.get(commonPath, "savestatusbar.txt").toString();
	String scoreRecord = Paths.get(commonPath, "score.txt").toString();
	String timeModeScoreRecord = Paths.get(commonPath, "scoretimemode.txt").toString();
	String savepath = Paths.get(commonPath, "load1.ser").toString();
	String breakmusicpath = Paths.get(commonPath, "break.wav").toString();
	String gamebackgroundImagepath = Paths.get(commonPath, "backgroundimg.png").toString();
	String sidePanelBackgroundImagePath = Paths.get(commonPath, "sidepanelimg.png").toString();


	int level;
	private transient Image backgroundImage;
	Shape nextPiece; //다음 블럭을 나타냄

	private int rotateCount = 0;
	JLabel sideLabel1;
	JLabel sideLabel2;
	JLabel sideLabel3;
	int nextX=10;
	int nextY=10;

	Timer timeLimitTimer;

	private Tetris tetris;
	public Board(Tetris parent) { //Tetris에게 상속받음

		tetris=parent;
		setFocusable(true); //키보드 이벤트 처리 가능
		curPiece = new Shape(); //현재 테트리스 블록을 나타내는 curPiece변수에 할당

		//타임어택 모드
		timemode=parent.getTimeMode();


		timer = new Timer(400, this); //테트리스가 떨어지는 시간
		timer.start(); //타이머 시작


		statusbar = parent.getStatusBar(); //부모클래스에서 상태창 가져옴
		shapeBoard = new Tetrominoes[BOARD_WIDTH * BOARD_HEIGHT]; //보드 상태 저장
		addKeyListener(new TAdapter());//키를 통해 블록제어
		//시작 : 위치와 크기 임의로 조정하는거부터 시작하기
		FlowLayout layout=new FlowLayout(FlowLayout.CENTER,30,40);
		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(layout);

		ImageIcon sidePanelBackgroundImageIcon = new ImageIcon(sidePanelBackgroundImagePath);
		Image sidePanelBackgroundImage = sidePanelBackgroundImageIcon.getImage();

// sidePanel에 배경 이미지 설정
		sidePanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(sidePanelBackgroundImage, 0, 0, getWidth(), getHeight(), this);
			}
		};
		Dimension sideLabelSize=new Dimension(400,200);
		Font labelFont = new Font("Dialog", Font.PLAIN, 50);
		Font labelFont2 = labelFont.deriveFont(40f);
		sideLabel1= new JLabel();

		levelbar=new JLabel("LEVEL 1");
		sideLabel2= new JLabel();
		sideLabel3= new JLabel();
		if(timemode)
		{
			sideLabel1.setText("Time mode");
			sideLabel2.setText("Time remaining");
			sideLabel3.setText(timeLimitInSeconds + " seconds");
		}
		else
		{
			sideLabel1.setText("Normal mode");
			sideLabel2.setText("Delay : 400");
			sideLabel3.setText("남은 회전횟수:∞");
		}
		levelbar.setSize(sideLabelSize);
		sideLabel1.setSize(sideLabelSize);
		sideLabel2.setSize(sideLabelSize);
		sideLabel3.setSize(sideLabelSize);

		sideLabel1.setBounds(20,100,340,50);
		levelbar.setBounds(20,200,340,50);
		sideLabel2.setBounds(20,300,340,50);
		sideLabel3.setBounds(20,400,340,50);

		levelbar.setForeground(Color.WHITE);
		sideLabel1.setForeground(Color.WHITE);
		sideLabel2.setForeground(Color.WHITE);
		sideLabel3.setForeground(Color.WHITE);

		levelbar.setFont(labelFont);
		sideLabel1.setFont(labelFont);
		sideLabel2.setFont(labelFont2);
		sideLabel3.setFont(labelFont2);


		sideLabel1.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		levelbar.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		sideLabel2.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		sideLabel3.setBorder(BorderFactory.createLineBorder(Color.WHITE));

		sidePanel.add(sideLabel1);
		sidePanel.add(levelbar);
		sidePanel.add(sideLabel2);
		sidePanel.add(sideLabel3);

		sidePanel.setLayout(null);
		sidePanel.setSize(400,800);
		sidePanel.setLocation(400,0);
		add(sidePanel);

		setLayout(null);






		clearBoard(); //보드 초기화
		if(timemode) {
			timeLimitTimer = new Timer(1000, e -> {
                elapsedTime++;
                sideLabel3.setText(timeLimitInSeconds -elapsedTime + " seconds");
                if (elapsedTime >= timeLimitInSeconds) {
                    // 시간 제한을 초과하면 게임 종료 처리
                    timer.stop();
                    timeLimitTimer.stop();
                    isStarted = false;
                    statusbar.setText("Game over: Time limit exceeded");
                    // 추가: 시간 초기화
                    elapsedTime = 0;
                    // 추가: 다시 시간을 세도록 호출
                    saveScore();
                }
            });
			timeLimitTimer.start();
		}
		backgroundImage=new ImageIcon(gamebackgroundImagepath).getImage();
	}


	public void actionPerformed(ActionEvent e) {
		if (isFallingFinished) { //떨어지는게 끝났으면
			isFallingFinished = false; //false로 바꾸고
			newPiece(); //메서드 호출
		} else {
			oneLineDown(); //그렇지 않으면 한줄씩 내림
		}
	}

	int squareWidth() { //화면 가로칸 계산해서 Tetris 게임 보드의 너비에 맞는 가로 길이 계산
		return (int) getSize().getWidth() / BOARD_WIDTH /2;
	}

	int squareHeight() { //화면 세로칸 계산해서 Tetris 게임 보드의 너비에 맞는 세로 길이 계산
		return (int) getSize().getHeight() / BOARD_HEIGHT;
	}

	Tetrominoes shapeAt(int x, int y) { //주어진 좌표에 있는 블록 모양을 가져옴
		return shapeBoard[(y * BOARD_WIDTH) + x];
	}

	public void start() { //게임을 초기화하고 새로운 게임을 시작

		if (isPaused) //게임이 일시정지상태면 작업수행 x
			return;

		isStarted = true;
		isFallingFinished = false;
		numLinesRemoved = 0;
		clearBoard(); //게임 보드 초기화

		newPiece();
		timer.start();
	}
	public void restart() { //게임을 초기화하고 새로운 게임을 시작

		isPaused=true;

		isStarted = true;
		isFallingFinished = false;
		numLinesRemoved = 0;
		clearBoard(); //게임 보드 초기화

		newPiece();
		timer.start();
	}


	private void pause() {
		if (!isStarted)  //게임이 시작되지 않으면 일시정지 불가능
			return;

		isPaused = !isPaused; //일시중지 상태 여부
		if (isPaused) { //퍼즈면 시간 정지
			timer.stop();
			if(timemode) {
				timeLimitTimer.stop();
			}
			statusbar.setText("paused");
		} else { //그렇지 않으면 시작
			timer.start();
			statusbar.setText(String.valueOf(numLinesRemoved)); //현재까지 제거된 줄 수 표시
		}
		repaint(); //화면을 다시그리도록 요청
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g); //부모 클래스의 paint를 호출하여 화면 지움(새로운 프레임 그릴 준비)
		Dimension size = getSize(); ////게임 화면의 크기를 가져옴
		int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();//화면높이-보드 높이*블록 크기
		int panelWidth = getSize().width;
		int panelHeight = getSize().height;
		int dividingLineX = panelWidth / 2;
		g.drawImage(backgroundImage, 0, 0, dividingLineX, panelHeight, this);
		for (int i = 0; i < BOARD_HEIGHT; ++i) { //게임 보드를 순회하면서 각 위치에 블록이 있는지 확인
			for (int j = 0; j < BOARD_WIDTH; ++j) {
				Tetrominoes shape = shapeAt(j, BOARD_HEIGHT - i - 1);
				if (shape != Tetrominoes.NO_SHAPE)
					drawSquare(g, 0 + j * squareWidth(), boardTop + i * squareHeight(), shape); //있으면 그림
			}
		}

		if (curPiece.getShape() != Tetrominoes.NO_SHAPE) { //현재 떨어지고 있는 블록이 모양이 없는것이 아니면
			for (int i = 0; i < 4; ++i) { //네개의 작은 블록들을 순회하면서
				int x = curX + curPiece.x(i); //작은 블록들의 x좌표를 구함
				int y = curY - curPiece.y(i); //작은 블록들의 y좌표를 구함
				drawSquare(g, 0 + x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(),
						curPiece.getShape()); //블록의 색상과 모양을 그리는 역할
			}
		}
		g.setColor(Color.BLACK);
		g.drawLine(dividingLineX, 0, getSize().width / 2, getSize().height);
	}
	private Timer softDropDelayTimer;


	private void dropDown() { //빨리 블록을 내리는 것
		int newY = curY; //현재 와이좌표를 저장
		while (newY > 0) { //높이가 더 내릴수 있는 높이이면
			if (!tryMove(curPiece, curX, newY - 1))
				break;
			--newY; //높이를 내림
		}
		if (softDropDelayTimer != null) {
			softDropDelayTimer.stop();
		}
		softDropDelayTimer = new Timer(500, e -> {
            // 0.5초가 지난 후 블록이 자동으로 한 칸 아래로 떨어집니다.
            oneLineDown();
            softDropDelayTimer.stop(); // 타이머를 멈추게 함으로써 블록이 한 번만 내려가게 합니다.
        });
		softDropDelayTimer.start();

	}

	private void oneLineDown() { //블럭을 한칸 밑으로 이동
		if (!tryMove(curPiece, curX, curY - 1))
			pieceDropped();
	}

	private void clearBoard() { //모든 블록을 비움
		for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; ++i)
			shapeBoard[i] = Tetrominoes.NO_SHAPE;
	}

	private void pieceDropped() { //블럭이 떨어진 후 호출되는 메서드
		for (int i = 0; i < 4; ++i) {
			int x = curX + curPiece.x(i); //블록의 각 부분 블록의 위치를 게임 보드 좌표로 변환
			int y = curY - curPiece.y(i);
			shapeBoard[(y * BOARD_WIDTH) + x] = curPiece.getShape();
		}	//블록이 게임 보드에 떨어진 것을 나타냄

		removeFullLines(); //행이 가득 찼다면, 이 행을 제거하고 행이 제거된 후의 게임 상태를 업데이트

		if (!isFallingFinished)
			newPiece();
		//타임어택 모드
		if(timemode) {
			elapsedTime = 0;
		}
	}

	private void setLevelAndTimer() {
		if (level >= 5) {
			levelbar.setText("LEVEL 5");
			if (!timemode) {
				timer.setDelay(300);
				sideLabel2.setText("Delay : 300");
			}
		} else if (level >= 4) {
			levelbar.setText("LEVEL 4");
		} else if (level >= 3) {
			levelbar.setText("LEVEL 3");
		} else if (level >= 2) {
			levelbar.setText("LEVEL 2");
			if (!timemode) {
				timer.setDelay(400);
			}
		}
	}

	private void updateGameLevel() {
		level = numLinesRemoved / 3;
	}

	private void handleGameOver() {
		curPiece.setShape(Tetrominoes.NO_SHAPE);
		timer.stop();
		if (timemode) {
			timeLimitTimer.stop();
		}
		isStarted = false;
		statusbar.setText("game over");
		Audio endMusic = new Audio("src/kr/ac/jbnu/se/tetris/audio/end.wav");
		endMusic.bgmStart();
		saveScore();
	}

	private void newPiece() {
		rotateCount = 0;

		curPiece.setRandomShape();
		curX = BOARD_WIDTH / 2 + 1;
		curY = BOARD_HEIGHT - 1 + curPiece.minY();

		updateGameLevel();

		if (timemode) {
			if (level >= 5) {
				timeLimitInSeconds = 3;
			} else if (level == 4) {
				timeLimitInSeconds = 4;
			} else if (level == 3) {
				timeLimitInSeconds = 5;
			} else if (level == 2) {
				timeLimitInSeconds = 6;
			}
		}

		setLevelAndTimer();

		if (!tryMove(curPiece, curX, curY)) {
			handleGameOver();
		}
	}


	public void saveStatusBar(String filename) {
		try(BufferedWriter writer=new BufferedWriter(new FileWriter(filename))){
			writer.write(String.valueOf(numLinesRemoved));
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void loadStatusbar(String filename) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String statusBarText = reader.readLine();
			numLinesRemoved=Integer.parseInt(statusBarText);
			statusbar.setText(statusBarText);
			//여기 수정하기
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	String enteredName;
	public void saveScore()
	{
		JFrame frame=new JFrame("이름 입력");
		JPanel panel=new JPanel();
		JLabel nameLabel=new JLabel("이름: ");
		JTextField nameField=new JTextField(20);

		JButton submitButton=new JButton("입력 완료");
		panel.add(nameLabel);
		panel.add(nameField);
		frame.add(panel);
		panel.add(submitButton);


		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(300,300);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		submitButton.addActionListener(e -> {
            // 텍스트 필드의 내용을 String 객체에 저장

            enteredName = nameField.getText();
            saveScore2(enteredName);
            frame.dispose();
            tetris.showLobby();
        });



	}


	public void saveScore2(String enteredName) {
		String[] names = new String[3];
		int[] scores = new int[3];
		try {
			File file = new File(scoreRecord);
			if (timemode) {
				file = new File(timeModeScoreRecord);
			}

			// 예외 처리 추가
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {


				for (int i = 0; i < 3; i++) {
					String line = reader.readLine();
					String[] parts = line.split(" : ");
					names[i] = parts[0];
					scores[i] = Integer.parseInt(parts[1]);
				}
			}

			boolean updated = false;
			for (int i = 0; i < 3; i++) {
				if (numLinesRemoved >= scores[i]) {
					scores[i] = numLinesRemoved;
					names[i] = enteredName;
					updated = true;
					break;
				}
			}
			if (updated) {
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
					for (int i = 0; i < 3; i++) {
						writer.write(names[i] + " : " + scores[i]);
						writer.newLine();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private boolean tryMove(Shape newPiece, int newX, int newY) {//이동이 유효한지 판단
		for (int i = 0; i < 4; ++i) {
			int x = newX + newPiece.x(i); //블록의 x좌표를 newx에 더해 새로운 X좌표
			int y = newY - newPiece.y(i);
			if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT)
				return false; // 계산된 좌표가 보드를 벗어난 경우 false
			if (shapeAt(x, y) != Tetrominoes.NO_SHAPE) //이미 블록이 존재하는 경우, 이동을 실패하고 false
				return false;
		}

		curPiece = newPiece;
		curX = newX;
		curY = newY;
		repaint();
		return true;
	}

	private boolean isLineFull(int rowIndex) {
		for (int j = 0; j < BOARD_WIDTH; ++j) {
			if (shapeAt(j, rowIndex) == Tetrominoes.NO_SHAPE) {
				return false;
			}
		}
		return true;
	}

	private void moveLinesDown(int startRow) {
		for (int k = startRow; k < BOARD_HEIGHT - 1; ++k) {
			for (int j = 0; j < BOARD_WIDTH; ++j) {
				shapeBoard[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
			}
		}
	}

	private void removeFullLines() {
		Audio breakMusic;
		int numFullLines = 0;

		for (int i = BOARD_HEIGHT - 1; i >= 0; --i) {
			if (isLineFull(i)) {
				++numFullLines;
				moveLinesDown(i);

				breakMusic = new Audio(breakmusicpath);
				breakMusic.bgmStart();
			}
		}

		if (numFullLines > 0) {
			numLinesRemoved += numFullLines;
			statusbar.setText(String.valueOf(numLinesRemoved));
			if (level == 0) level = 1;
			isFallingFinished = true;
			curPiece.setShape(Tetrominoes.NO_SHAPE);
			repaint();
		}
	}


	private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
		Color[] colors = { new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
				new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102, 204, 204),
				new Color(218, 170, 0) };
		//블록 모양에 해당하는 색상을 배열에 저장
		Color color = colors[shape.ordinal()];
		//해당하는 색상을 선택



		if(!timemode&&level>=4)
		{
			g.setColor(new Color(238,238,238,0));
		}
		else
			g.setColor(color);


		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
		//사각형을 그림 , x,y는 왼쪽 상단 모서리의 좌표를 나타냄

		if(level<=3||timemode)
		{
			g.setColor(color.brighter()); //가장자리 강조
			g.drawLine(x, y + squareHeight() - 1, x, y);
			g.drawLine(x, y, x + squareWidth() - 1, y);

		}
		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
		g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
	}

	public void saveGame(String filename) {
		try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename))) {
			outputStream.writeObject(this.shapeBoard); // Board 클래스 자체를 직렬화하여 저장
			saveStatusBar(savestatusbarpath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void loadGame(String filename) {
		try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
			loadStatusbar(savestatusbarpath);
			this.shapeBoard = (Tetrominoes[]) inputStream.readObject(); // 직렬화된 Board 클래스를 불러옴
			repaint();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		level=numLinesRemoved/3;
		if (level >= 5) {
			levelbar.setText("LEVEL 5");
			if(!timemode) {
				timer.setDelay(300);
				sideLabel2.setText("Delay : 300");
			}
		} else if (level >= 4) {
			levelbar.setText("LEVEL 4");
		} else if (level >= 3) {
			levelbar.setText("LEVEL 3");
		} else if (level >= 2) {
			levelbar.setText("LEVEL 2");
			if(!timemode) {
				timer.setDelay(400);
			}
		}

	}
	//esc 메뉴
	private void showPauseMenu() {
		JFrame frame = new JFrame("Pause Menu");
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(this);
		frame.setResizable(false);

		JPanel pausePanel = new JPanel(new GridLayout(2, 1));
		pausePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		frame.add(pausePanel);
		Font buttonFont = new Font("Dialog", Font.BOLD, 15);
		JButton resumeButton = new JButton("계속하기");
		JButton saveButton = new JButton("저장 및 종료");
		JButton quitButton = new JButton("끝내기");
		JButton lobbyButton = new JButton("로비");

		resumeButton.setFont(buttonFont);
		saveButton.setFont(buttonFont);
		quitButton.setFont(buttonFont);
		lobbyButton.setFont(buttonFont);

		resumeButton.setForeground(Color.WHITE);
		saveButton.setForeground(Color.WHITE);
		quitButton.setForeground(Color.WHITE);
		lobbyButton.setForeground(Color.WHITE);
		Color color=new Color(20,25,80);
		resumeButton.setBackground(color);
		saveButton.setBackground(color);
		quitButton.setBackground(color);
		lobbyButton.setBackground(color);

		pausePanel.add(resumeButton);
		pausePanel.add(saveButton);
		pausePanel.add(quitButton);
		pausePanel.add(lobbyButton);

		resumeButton.addActionListener(e -> {
            frame.dispose(); // 팝업 창 닫기
            pause(); // 게임 일시정지 해제
        });

		saveButton.addActionListener(e -> {
            saveGame(savepath);
            System.exit(0); // 게임 종료
        });

		quitButton.addActionListener(e -> System.exit(0));

		lobbyButton.addActionListener(e -> {
            frame.dispose(); // 팝업 창 닫기
            tetris.showLobby();
        });



		frame.setVisible(true);
	}






	class TAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int keycode = e.getKeyCode();

			if (keycode == 'p' || keycode == 'P') {
				pause();
			} else if (keycode == 'q' || keycode == 'Q') {
				numLinesRemoved = 10; // 테스트용
			} else {
				handleGameActions(keycode);
			}
		}

		private void handleGameActions(int keycode) {
			int moveLeftKey = TetrisKeySetting.keyMappings.get("MoveLeft");
			int moveRightKey = TetrisKeySetting.keyMappings.get("MoveRight");
			int rotateLeftKey = TetrisKeySetting.keyMappings.get("RotateLeft");
			int rotateRightKey = TetrisKeySetting.keyMappings.get("RotateRight");
			int moveDownKey = TetrisKeySetting.keyMappings.get("MoveDown");
			int dropDownKey = TetrisKeySetting.keyMappings.get("DropDown");

			if (keycode == moveLeftKey) {
				tryMove(curPiece, curX - 1, curY);
			} else if (keycode == moveRightKey) {
				tryMove(curPiece, curX + 1, curY);
			} else if (keycode == rotateLeftKey) {
				handleRotation(curPiece.rotateLeft());
			} else if (keycode == rotateRightKey) {
				handleRotation(curPiece.rotateRight());
			} else if (keycode == moveDownKey) {
				oneLineDown();
			} else if (keycode == dropDownKey) {
				dropDown();
			} else if (keycode == KeyEvent.VK_ESCAPE) {
				pause();
				showPauseMenu();
			} else if (keycode == 'r' || keycode == 'R') {
				start();
			}

		}

		private void handleRotation(Shape rotatedPiece) {
			if (!(rotateCount > 3 && level >= 3 && !timemode)) {
				tryMove(rotatedPiece, curX, curY);
				rotateCount++;
				if (level >= 3) {
					sideLabel3.setText("남은 회전횟수:" + (4 - rotateCount));
				}
			}
		}
	}

}