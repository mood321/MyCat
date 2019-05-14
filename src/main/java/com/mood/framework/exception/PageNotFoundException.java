package com.mood.framework.exception;

@SuppressWarnings("serial")
public class PageNotFoundException extends MiniCatException{

	public PageNotFoundException(String msg){
		super(msg);
	}
}
