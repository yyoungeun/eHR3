package com.ehr;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.cglib.proxy.Proxy;

import com.ehr.reflect.Hello;
import com.ehr.reflect.HelloTarget;
import com.ehr.reflect.HelloUpperCase;
import com.ehr.reflect.UpperHandler_org01;

public class HelloTest {
	
	@Test
	@Ignore
	public void simpleProxy() {
		
		Hello hello = new HelloTarget();
		
		System.out.println(hello.sayHello("HR"));
		assertThat(hello.sayHello("HR"),is("Hello HR"));
		
		System.out.println(hello.sayHi("HR"));
		assertThat(hello.sayHi("HR"),is("Hi HR"));
		
		System.out.println(hello.sayThankYou("HR"));
		assertThat(hello.sayThankYou("HR"),is("Thank You HR"));
	}
	
	@Test
	@Ignore
	public void dynamicProxy() {
		Hello hello = new HelloUpperCase(new HelloTarget());
		System.out.println(hello.sayHello("HR"));
		System.out.println(hello.sayHi("HR"));
		System.out.println(hello.sayThankYou("HR"));
		
		assertThat(hello.sayHello("HR"),is("HELLO HR"));
		assertThat(hello.sayHi("HR"),is("HI HR"));
		assertThat(hello.sayThankYou("HR"),is("THANK YOU HR"));
	}
	//문제점: 상속받은  interface 모두 구현.
	//		toUpperCase()가 중복되서 나타남.
	
	//->다이내믹 프록시: reflection
	
	@Test
	public void dynamicProxyRelact() {
		//프록시 팩토리
		Hello proxiedHello = (Hello)Proxy.newProxyInstance( 
				//proxy class가져옴
				//UpperHander -> springframe import
				//동적으로 사용되는 다이내믹 프록시 클래스의 로딩에 사용될 클래스 로더
				getClass().getClassLoader() //Class loader
				//구현 인터페이스
				,new Class[] {Hello.class} //Proxy가 구현해야 할 interface
				//부가기능과 위임코드를 담은 InvocationHandler
				,new UpperHandler_org01(new HelloTarget())); //Dynamic Proxy가 호출해야 할 Handler클래스
		
		System.out.println(proxiedHello.sayHello("HR"));
		System.out.println(proxiedHello.sayHi("HR"));
		System.out.println(proxiedHello.sayThankYou("HR"));
		
		assertThat(proxiedHello.sayHello("HR"),is("HELLO HR"));
		assertThat(proxiedHello.sayHi("HR"),is("HI HR"));
		assertThat(proxiedHello.sayThankYou("HR"),is("THANK YOU HR"));	
	}
}
