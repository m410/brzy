package org.brzy.mvc.mock;


public class PojoFixture {
    private String name;

    public String hello(String name) {
        return "hello " + name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
