package kr.ac.jbnu.se.tetris;


import javafx.scene.layout.BorderWidths;

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
import javax.swing.border.LineBorder;

import java.io.*;

public class Board extends JPanel implements ActionListener,Serializable {

	private static final long serialVersionUID = 1L;
	final int BoardWidth = 10; //게임 창 크기 가로 10
	final int BoardHeight = 22; //세로 22

	private JButton topButton; //下落的条状动画，用Button实现
	private JButton removeButton; //删除动画，用button实现

	Timer timer;
	boolean isFallingFinished = false;
	boolean isStarted = false;
	boolean isPaused = false;
	int numLinesRemoved;
	int curX = 0; //현재 블록의 위치를 나타내는 변수
	int curY = 0; //현재 블록의 위치를 나타내는 변수
	int beforeX = 0; //防止动画过慢，如果用了curX导致动画跟踪了下一个方块
	int beforeY = 0; //防止动画过慢，如果用了curY导致动画跟踪了下一个方块
	JLabel statusbar; //게임 상태를 표시하는 레이블
	JLabel levelbar;//게임 레벨을 나타내는 레이블
	Shape curPiece; //현재 Tetris블록을 나타내는 객체
	Shape beforePiece; //防止动画过慢,下坠时用于记录下坠方块
	Tetrominoes[] board;
	String savestatusbarpath=System.getProperty("user.dir")+"\\src\\\\kr\\\\ac\\\\jbnu\\\\se\\\\tetris\\\\audio\\\\savestatusbar.txt";
	String scoreRecord=System.getProperty("user.dir")+"\\src\\kr\\ac\\jbnu\\se\\tetris\\audio\\score.txt";
	String savepath=System.getProperty("user.dir")+"\\src\\\\kr\\\\ac\\\\jbnu\\\\se\\\\tetris\\\\audio\\\\load1.ser";
	String breakmusicpath=System.getProperty("user.dir")+"\\src\\\\kr\\\\ac\\\\jbnu\\\\se\\\\tetris\\\\audio\\\\break.wav";
	int line = -1;

	private static Timer anomiationTimer;	//动画计时器
	private static Timer removeTimer;	//动画计时器
	int level;
	static Shape[] nextPieces = new Shape[6];//预存5个待加载的俄罗斯方块

	private int rotateCount = 0;

	private final int DELAY = 1;//动画刷新间隔
	private int  x,moveX,y, moveY;

	int kko=0;

	private Tetris tetris;
	public Board(Tetris parent) { //Tetris에게 상속받음
		tetris=parent;
		//动画相关属性
		moveY = 30;
		moveX = 20;
		topButton = new JButton(" ");
		removeButton= new JButton(" ");
		topButton.setEnabled(false);
		removeButton.setEnabled(false);
		//初始置为不可见，需要加载动画的时候再可见
		removeButton.setVisible(false);
		topButton.setVisible(false);
		add(topButton);
		add(removeButton);
		setFocusable(true); //키보드 이벤트 처리 가능
		curPiece = new Shape(); //현재 테트리스 블록을 나타내는 curPiece변수에 할당
		for (int i = 0; i < nextPieces.length; i++) {
			nextPieces[i] = new Shape();
			nextPieces[i].setRandomShape();//预设5个随机方块
		}

		timer = new Timer(400, this); //테트리스가 떨어지는 시간
		timer.start(); //타이머 시작
		statusbar = parent.getStatusBar(); //부모클래스에서 상태창 가져옴
		levelbar=parent.getLevelBar();
		board = new Tetrominoes[BoardWidth * BoardHeight]; //보드 상태 저장
		addKeyListener(new TAdapter());//키를 통해 블록제어
		clearBoard(); //보드 초기화

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
		return (int) getSize().getWidth() / BoardWidth;
	}

	int squareHeight() { //화면 세로칸 계산해서 Tetris 게임 보드의 너비에 맞는 세로 길이 계산
		return (int) getSize().getHeight() / BoardHeight;
	}

	Tetrominoes shapeAt(int x, int y) { //주어진 좌표에 있는 블록 모양을 가져옴
		return board[(y * BoardWidth) + x];
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
			statusbar.setText("paused");
		} else { //그렇지 않으면 시작
			timer.start();
			statusbar.setText(String.valueOf(numLinesRemoved)); //현재까지 제거된 줄 수 표시
		}
		repaint(); //화면을 다시그리도록 요청
	}

	public void paint(Graphics g) {
		super.paint(g); //부모 클래스의 paint를 호출하여 화면 지움(새로운 프레임 그릴 준비)
		Dimension size = getSize(); ////游戏屏幕大小
		int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();//屏高-板高*块大小

		for (int i = 0; i < BoardHeight; ++i) { //게임 보드를 순회하면서 각 위치에 블록이 있는지 확인
			for (int j = 0; j < BoardWidth; ++j) {
				Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
				if (shape != Tetrominoes.NoShape)
					drawSquare(g, 0 + j * squareWidth(), boardTop + i * squareHeight(), shape); //있으면 그림
			}
		}

		if (curPiece.getShape() != Tetrominoes.NoShape) { //현재 떨어지고 있는 블록이 모양이 없는것이 아니면
			for (int i = 0; i < 4; ++i) { //네개의 작은 블록들을 순회하면서
				int x = curX + curPiece.x(i); //작은 블록들의 x좌표를 구함
				int y = curY - curPiece.y(i); //작은 블록들의 y좌표를 구함
				drawSquare(g, 0 + x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(),
						curPiece.getShape()); //블록의 색상과 모양을 그리는 역할
			}
		}
	}


	private void dropDown() { //빨리 블록을 내리는 것
		int newY = curY; //현재 와이좌표를 저장
		beforeX = curX;
		beforeY = curY; //防止动画过慢，导致跟踪到下一个方块而将当前下坠方块坐标储藏
		beforePiece = curPiece;
		anomiationTimer = new Timer(DELAY, new ClearListener());
		anomiationTimer.start();//开始动画
		while (newY > 0) { //높이가 더 내릴수 있는 높이이면
			if (!tryMove(curPiece, curX, newY - 1))
				break;
			--newY; //높이를 내림
		}
	}

	private void oneLineDown() { //블럭을 한칸 밑으로 이동
		if (!tryMove(curPiece, curX, curY - 1))
			pieceDropped();
	}

	private void clearBoard() { //모든 블록을 비움
		for (int i = 0; i < BoardHeight * BoardWidth; ++i)
			board[i] = Tetrominoes.NoShape;
	}

	private void pieceDropped() { //블럭이 떨어진 후 호출되는 메서드
		for (int i = 0; i < 4; ++i) {
			int x = curX + curPiece.x(i); //블록의 각 부분 블록의 위치를 게임 보드 좌표로 변환
			int y = curY - curPiece.y(i);
			board[(y * BoardWidth) + x] = curPiece.getShape();
		}	//블록이 게임 보드에 떨어진 것을 나타냄

		removeFullLines(); //행이 가득 찼다면, 이 행을 제거하고 행이 제거된 후의 게임 상태를 업데이트

		if (!isFallingFinished)
			newPiece();
	}

	private void newPiece() { //새로운 Tetromino블록을 생성하고 아래로 이동시킴
		rotateCount = 0;
		Audio endMusic;
		curPiece = nextPieces[0];
		for (int i = 0; i < nextPieces.length -1; i++) {
			nextPieces[i] = nextPieces[i+1]; //将预存的下一个方块作为当前方块
		}
		nextPieces[nextPieces.length-1] = new Shape();
		nextPieces[nextPieces.length-1].setRandomShape();//由于这一轮抽取了一个方块走，需要补足最后一个方块
		curX = BoardWidth / 2 + 1; //변수를 게임 보드 중앙에서 시작하도록 설정
		curY = BoardHeight - 1 + curPiece.minY();//보드 상단에서 아래로 이동하도록 설정
		level=numLinesRemoved;
		if (level >= 2) {
			timer.setDelay(100); // level이 2이상일 때 빠른 속도로 설정
		}
		else if(level>=5)
		{
			timer.setDelay(50);
		}
		if (!tryMove(curPiece, curX, curY)) { //새로 생성된 우 초기 위치가 유효한지 나타냄
			curPiece.setShape(Tetrominoes.NoShape);
			timer.stop();
			isStarted = false;
			statusbar.setText("game over");
			endMusic=new Audio("src/kr/ac/jbnu/se/tetris/audio/end.wav",true);
			endMusic.bgmStart();
			saveScore(numLinesRemoved);
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
			levelbar.setText("Level"+Integer.toString(numLinesRemoved/3));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	String enteredName;
	public void saveScore(int numLinesRemoved)
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
		frame.setVisible(true);

		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 텍스트 필드의 내용을 String 객체에 저장

				enteredName = nameField.getText();
				saveScore2(enteredName);

			}
		});



	}


	private class RemoveListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			x += moveX;
			removeButton.setVisible(true);
			removeButton.setBackground(Color.BLACK);
			removeButton.setLayout(new FlowLayout());
			removeButton.setBounds(0, getHeight()-squareHeight()*(line+1), x, squareHeight());
			if (x >= getWidth()) {
				removeButton.setVisible(false);//将动画条不可见，直至下次Key:space的时候
				x = 0;//将状态置为开始
				removeTimer.stop();//动画结束，卸载监听防止
			}
			repaint();
		}
	}


	/**
	 * 动画效果：不断更新按钮大小，以达到下坠动画效果
	 */
	private class ClearListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			y += moveY;
			topButton.setVisible(true);
			//填充下落的背景条动画颜色
			Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
					new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102, 204, 204),
					new Color(218, 170, 0) };
			//锁定方块的颜色
			Color color = colors[beforePiece.getShape().ordinal()];
			int Xmin= 99,Xmax = 0;
			//获取当前形状的最左边和最右边坐标
			for (int i = 0; i < 4; ++i) {
				if (Xmin >= beforeX + beforePiece.x(i)){
					Xmin = beforeX + beforePiece.x(i);
				}
				if (Xmax <= beforeX + beforePiece.x(i)){
					Xmax = beforeX + beforePiece.x(i);
				}
			}
			//当前方块所在坐标getHeight()-(beforeY+1)*squareHeight()  拖尾从这里开始
			topButton.setBounds(Xmin*squareWidth(), getHeight()-(beforeY+1)*squareHeight(), (Xmax-Xmin+1)*squareWidth(), y);
			topButton.setBackground(color);
			if (y >= getHeight()) {
				topButton.setVisible(false);//将动画条不可见，直至下次Key:space的时候
				y = 0;//将状态置为开始
				anomiationTimer.stop();//动画结束，卸载监听防止
			}
			repaint();
		}
	}




	public void saveScore2(String enteredName)
	{

		try
		{

			File file=new File(scoreRecord);
			BufferedReader reader=new BufferedReader(new FileReader(file));
			String[] names=new String[3];
			int[] scores =new int[3];

			for(int i=0;i<3;i++)
			{
				String line=reader.readLine();
				String[] parts=line.split(" : ");
				names[i]=parts[0];
				scores[i]=Integer.parseInt(parts[1]);
			}
			reader.close();


			boolean updated=false;
			for(int i=0;i<3;i++)
			{
				if(numLinesRemoved>=scores[i])
				{
					scores[i]=numLinesRemoved;
					names[i]=enteredName;
					updated=true;
					break;

				}
			}
			if(updated)
			{


				BufferedWriter writer=new BufferedWriter(new FileWriter(file,false));
				for(int i=0;i<3;i++)
				{
					writer.write(names[i]+" : "+scores[i]);
					writer.newLine();
				}
				writer.close();
			}

		}
		catch(IOException e) {
			e.printStackTrace();
		}


	}

	private boolean tryMove(Shape newPiece, int newX, int newY) {//이동이 유효한지 판단
		for (int i = 0; i < 4; ++i) {
			int x = newX + newPiece.x(i); //블록의 x좌표를 newx에 더해 새로운 X좌표
			int y = newY - newPiece.y(i);
			if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
				return false; // 계산된 좌표가 보드를 벗어난 경우 false
			if (shapeAt(x, y) != Tetrominoes.NoShape) //이미 블록이 존재하는 경우, 이동을 실패하고 false
				return false;
		}

		curPiece = newPiece;
		curX = newX;
		curY = newY;
		repaint();
		return true;
	}

	private void removeFullLines() { //꽉 찬 줄 찾아 제거, 제거된 줄 업데이트
		Audio breakMusic;
		int numFullLines = 0; //꽉 찬 줄의 수를 저장 할 변수
		for (int i = BoardHeight - 1; i >= 0; --i) { //아래쪽부터 각 줄을 검사
			boolean lineIsFull = true; //현재 검사중인 줄을 꽉 찬 줄로 초기화
			for (int j = 0; j < BoardWidth; ++j) { //모든 열을 검사
				if (shapeAt(j, i) == Tetrominoes.NoShape) { //현재 열에 블록이 없다면 꽉 차지 않은 것으로 판단
					lineIsFull = false;
					break;
				}
			}

			if (lineIsFull) { //꽉 찬 줄일경우
				line = i;//记录要消失的行，用于加载动画
				removeTimer = new Timer(DELAY, new RemoveListener());
				removeTimer.start();//开始动画
				++numFullLines; //줄 수 증가
				for (int k = i; k < BoardHeight - 1; ++k) {
					for (int j = 0; j < BoardWidth; ++j)
						board[(k * BoardWidth) + j] = shapeAt(j, k + 1); //각 줄을 한칸씩 아래로 이동
				}
				breakMusic=new Audio(breakmusicpath,true);
				breakMusic.bgmStart();
			}
		}

		if (numFullLines > 0) { //한게 이상 제거 되었다면
			numLinesRemoved += numFullLines; //전체 제거된 수에 더함
			statusbar.setText(String.valueOf(numLinesRemoved)); //상태 표시줄에 추가
			if(level==0) level=1;
			levelbar.setText("Level"+Integer.toString(level));
			isFallingFinished = true; //블록이 더이상 떨어지지 않도록
			curPiece.setShape(Tetrominoes.NoShape); //새로운 블록 생성 준비
			repaint();
		}
	}

	private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
		Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
				new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102, 204, 204),
				new Color(218, 170, 0) };
		//블록 모양에 해당하는 색상을 배열에 저장
		Color color = colors[shape.ordinal()];
		//해당하는 색상을 선택



		if(level>=4)
		{
			g.setColor(new Color(238,238,238));
		}
		else
			g.setColor(color);


		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
		//사각형을 그림 , x,y는 왼쪽 상단 모서리의 좌표를 나타냄

		if(level<=3)
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
			outputStream.writeObject(this.board); // Board 클래스 자체를 직렬화하여 저장
			saveStatusBar(savestatusbarpath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void loadGame(String filename) {
		try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
			loadStatusbar(savestatusbarpath);
			this.board = (Tetrominoes[]) inputStream.readObject(); // 직렬화된 Board 클래스를 불러옴
			repaint();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	//esc 메뉴
	private void showPauseMenu() {
		JFrame frame = new JFrame("Pause Menu");
		frame.setSize(200, 150);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(this);
		frame.setResizable(false);

		JPanel pausePanel = new JPanel(new GridLayout(2, 1));
		pausePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		frame.add(pausePanel);

		JButton resumeButton = new JButton("계속하기");
		JButton saveButton = new JButton("저장 및 종료");
		JButton quitButton = new JButton("끝내기");
		JButton lobbyButton = new JButton("로비");

		pausePanel.add(resumeButton);
		pausePanel.add(saveButton);
		pausePanel.add(quitButton);
		pausePanel.add(lobbyButton);

		resumeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose(); // 팝업 창 닫기
				pause(); // 게임 일시정지 해제
			}
		});

		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveGame(savepath);
				System.exit(0); // 게임 종료
			}
		});

		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0); // 게임 종료
			}
		});
		lobbyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose(); // 팝업 창 닫기
				tetris.showLobby();
			}
		});
		frame.setVisible(true);
	}






	class TAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {


			int keycode = e.getKeyCode();

			if (keycode == 'p' || keycode == 'P') {
				pause();
				return;
			}
			if (keycode == 'q' || keycode == 'Q') {
				numLinesRemoved=10;
				return; //테스트용
			}
			if (isPaused)
				return;
			//재시작기능
			if (keycode == 'r' || keycode == 'R') {
				start();
				return;
			}
			//메뉴
			if (keycode == KeyEvent.VK_ESCAPE) {
				pause();
				showPauseMenu(); // ESC 키를 누르면 일시정지 메뉴 표시


				return;
			}

			if (keycode == TetrisKeySetting.keyMappings.get("MoveLeft")) {

				tryMove(curPiece, curX - 1, curY);

			} else if (keycode == TetrisKeySetting.keyMappings.get("MoveRight")) {

				tryMove(curPiece, curX + 1, curY);

			} else if (keycode == TetrisKeySetting.keyMappings.get("RotateLeft")) {
				if(rotateCount>3&&level>=3) {

				}
				else
				{
					tryMove(curPiece.rotateLeft(), curX, curY);
					rotateCount++;
				}

			} else if (keycode == TetrisKeySetting.keyMappings.get("RotateRight")) {
				if(rotateCount>3&&level>=3) {

				}
				else
				{
					tryMove(curPiece.rotateRight(), curX, curY);
					rotateCount++;
				}

			} else if (keycode == TetrisKeySetting.keyMappings.get("MoveDown")) {

				oneLineDown();

			} else if (keycode == TetrisKeySetting.keyMappings.get("DropDown")) {

				dropDown();
			}
		}
	}
}
//d
