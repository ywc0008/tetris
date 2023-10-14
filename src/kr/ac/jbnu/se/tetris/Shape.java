package kr.ac.jbnu.se.tetris;

import java.util.Random;

public class Shape {

	private Tetrominoes pieceShape; //변수 선언
	private int coords[][]; //블럭의 좌표를 지정
	private int[][][] coordsTable; //모든 블록의 가능한 회전 상태를 저장하는 3차원 배열

	public Shape() {
		coords = new int[4][2];
		setShape(Tetrominoes.NoShape);
	}

	public void setShape(Tetrominoes shape) {

		coordsTable = new int[][][] 
				{ { { 0, 0 }, { 0, 0 }, { 0, 0 }, { 0, 0 } },
				{ { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 } }, 
				{ { 0, -1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } },
				{ { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } }, 
				{ { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 1 } },
				{ { 0, 0 }, { 1, 0 }, { 0, 1 }, { 1, 1 } }, 
				{ { -1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } },
				{ { 1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } } };

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; ++j) {
				coords[i][j] = coordsTable[shape.ordinal()][i][j];
			}
		}
		pieceShape = shape;

	}

	private void setX(int index, int x) {
		coords[index][0] = x; //특정좌표의 x값 설정
	}

	private void setY(int index, int y) {
		coords[index][1] = y; //특정 좌표의 y값 설정
	}

	public int x(int index) { //특정좌표의 x값 가져옴
		return coords[index][0];
	}

	public int y(int index) { //특정좌표의 y값 가져옴
		return coords[index][1];
	}

	public Tetrominoes getShape() {
		return pieceShape;
	}

	public void setRandomShape() {
		Random r = new Random();
		int x = Math.abs(r.nextInt()) % 7 + 1; //1에서 7사이의 무작위 정수를 나타내기 위함
		Tetrominoes[] values = Tetrominoes.values();
		setShape(values[x]);
	}

	public int minX() {
		int m = coords[0][0];
		for (int i = 0; i < 4; i++) {
			m = Math.min(m, coords[i][0]);
		}
		return m;
	}

	public int minY() {
		int m = coords[0][1];
		for (int i = 0; i < 4; i++) {
			m = Math.min(m, coords[i][1]);
		}
		return m;
	}

	public Shape rotateLeft() {
		if (pieceShape == Tetrominoes.SquareShape)
			return this;

		Shape result = new Shape();
		result.pieceShape = pieceShape;

		for (int i = 0; i < 4; ++i) {
			result.setX(i, y(i));
			result.setY(i, -x(i));
		}
		return result;
	}

	public Shape rotateRight() { //블록 좌표 좌회전 또는 우회전
		if (pieceShape == Tetrominoes.SquareShape)
			return this;

		Shape result = new Shape();
		result.pieceShape = pieceShape;

		for (int i = 0; i < 4; ++i) {
			result.setX(i, -y(i));
			result.setY(i, x(i));
		}
		return result;
	}
}