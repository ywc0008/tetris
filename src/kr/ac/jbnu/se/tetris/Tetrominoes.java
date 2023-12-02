package kr.ac.jbnu.se.tetris;
import java.io.Serializable;


public enum Tetrominoes implements Serializable { //열거형 사용
	NO_SHAPE, Z_SHAPE, S_SHAPE, LINE_SHAPE, T_SHAPE, SQUARE_SHAPE, L_SHAPE, MIRRORED_L_SHAPE;
	private static final long serialVersionUID = 1L;
}
