package com.ehr;
/**
 * NextLevel
 * @author sist
 *
 */
public enum Level { //다음 레벨 결정
	
	GOLD(3,null), SILVER(2,GOLD), BASIC(1,SILVER);
	
	private final int value;
	private final Level nextLevel;
	
	
	Level(int value, Level next){
		this.value = value;
		nextLevel = next;
	}
	
	public Level getNextLevel() {
		return nextLevel;
	}
	
	//db에 insert할 때 사용
	public int intValue() {
		return value;
	}
	
	//db에서 가져올 때 사용
	public static Level valueOf(int value) {
		switch(value) {
			case 1: return BASIC;
			case 2: return SILVER;
			case 3: return GOLD;
			default: throw new AssertionError("Unknown value:"+value);
		}
	}
}
