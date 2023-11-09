package kr.ac.jbnu.se.tetris;
import java.io.Serializable;


public enum Tetrominoes implements Serializable { //열거형 사용
	NoShape, ZShape, SShape, LineShape, TShape, SquareShape, LShape, MirroredLShape;
	private static final long serialVersionUID = 1L;
}
