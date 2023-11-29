package kr.ac.jbnu.se.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

/**
 * @author maybelence
 * @descrpition
 * @date 2023-11-28
 */
public class RightBoard extends JPanel implements ActionListener, Serializable {

    final int BoardWidth = 3; //게임 창 크기 가로 10
    final int BoardHeight = 22; //세로 22

    int nextX = 1;

    int nextY = 20;

    int squareHeight() { //화면 세로칸 계산해서 Tetris 게임 보드의 너비에 맞는 세로 길이 계산
        return (int) getSize().getHeight() / BoardHeight;
    }

    public void paint(Graphics g) {
        super.paint(g); //부모 클래스의 paint를 호출하여 화면 지움(새로운 프레임 그릴 준비)
        Dimension size = getSize(); ////游戏屏幕大小
        int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();//屏高-板高*块大小

        //依次画出除了第一个以外剩下的五个
        for (int i = 1; i < Board.nextPieces.length; i++) {
            if (Board.nextPieces[1].getShape() != Tetrominoes.NoShape) {
                for (int j = 0; j < 4; ++j) {
                    int x = nextX + Board.nextPieces[i].x(j);
                    int y = nextY - Board.nextPieces[i].y(j)-(i-1)*4; //-(i-1)*4 是方块高度的差值
                    drawSquare(g, 0 + x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(),
                            Board.nextPieces[i].getShape());
                }
            }
        }
        repaint(); //重新绘制，可以保证一直处于刷新状态
    }


    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color colors[] = {new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
                new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102, 204, 204),
                new Color(218, 170, 0)};
        //블록 모양에 해당하는 색상을 배열에 저장
        Color color = colors[shape.ordinal()];
        //해당하는 색상을 선택

        g.setColor(color);

        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
        //사각형을 그림 , x,y는 왼쪽 상단 모서리의 좌표를 나타냄


        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    int squareWidth() { //화면 가로칸 계산해서 Tetris 게임 보드의 너비에 맞는 가로 길이 계산
        return (int) getSize().getWidth() / BoardWidth;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
