package com.ehr;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;

public class ReflectionTest {
	
	@Test
	public void invokeMethod() throws Exception{
		
		String name = "Spring";
		System.out.println(name.length());
		//length
		assertThat(name.length(), is(6));
		//리플렉션이랑 객체를 통해 클래스의 정보를 분석해 내는 프로그램 기법을 말한다.
		Method method = String.class.getMethod("length");
		
		System.out.println("method.invoke:" +method.invoke(name));
		assertThat(method.invoke(name),is(6));
		
		//charAt
		System.out.println(name.charAt(0));
		assertThat(name.charAt(0), is('S'));
		
		Method charAtmethod = String.class.getMethod("charAt", int.class); //int type
		System.out.println("method.invoke:" + charAtmethod.invoke(name, 0));
		assertThat(charAtmethod.invoke(name, 0), is('S'));
		
	}

}
