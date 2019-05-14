package com.mood.framework.exception;

@SuppressWarnings("serial")
public class MiniCatException extends RuntimeException{

	
	public MiniCatException(){
		super();
	}
	public MiniCatException(String msg){
		super(msg);
	}
}
