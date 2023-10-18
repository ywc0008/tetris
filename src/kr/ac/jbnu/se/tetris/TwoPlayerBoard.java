package kr.ac.jbnu.se.tetris;

import java.awt.event.KeyAdapter;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
public class TwoPlayerBoard extends Board {

    private Tetrominoes[] board2;

    public TwoPlayerBoard(Tetris parent) {
        super(parent);
        board2 = new Tetrominoes[BoardWidth * BoardHeight];
        // ... 여기에 2인용 게임을 위한 추가 초기화 로직
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 첫 번째 플레이어의 보드 그리기 (상속된 Board의 paintComponent 메서드에서 처리)

        // 두 번째 플레이어에 대한 그리기 로직 추가
        for (int i = 0; i < BoardHeight; i++) {
            for (int j = 0; j < BoardWidth; j++) {
                Tetrominoes shape = shapeAt(j, BoardHeight - i - 1, board2);
                if (shape != Tetrominoes.NoShape)
                    drawSquare(g, j * squareWidth(), i * squareHeight(), shape);
            }
        }
    }
    @Override
    int squareWidth() {
        return (int) (getSize().getWidth() / 2) / BoardWidth;
    }

    @Override
    int squareHeight() {
        return (int) getSize().getHeight() / BoardHeight;
    }


    // ... 필요한 경우 기존 메소드들을 오버라이드하거나 새로운 메소드 추가
    class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            // ... Handle Player 1 keys (if needed)

            // Handle Player 2 keys
            if (!isPaused) {
                switch (e.getKeyCode()) {
                    // Example: WASD keys
                    case KeyEvent.VK_H:
                        // Handle move left for Player 2
                        break;
                    case KeyEvent.VK_J:
                        // Handle move down for Player 2
                        break;
                    case KeyEvent.VK_K:
                        // Handle move right for Player 2
                        break;
                    case KeyEvent.VK_L:
                        // Handle rotate for Player 2
                        break;
                }
            }
        }
}}
