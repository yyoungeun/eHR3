package com.ehr.reflect;

public class HelloUpperCase implements Hello {
	//target에게 그대로 위임하고 (toUpperCase)대문자로 변환하여 넘겨준다.
	
	private Hello hello;
	
	public HelloUpperCase() {}
	
	public HelloUpperCase(Hello hello) {
		this.hello = hello;
	}

	@Override
	public String sayHello(String name) {
		return hello.sayHello(name).toUpperCase();
	}

	@Override
	public String sayHi(String name) {
		return hello.sayHi(name).toUpperCase();
	}

	@Override
	public String sayThankYou(String name) {
		return hello.sayThankYou(name).toUpperCase();
	}

}
