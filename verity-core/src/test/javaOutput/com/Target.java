package com;import java.lang.*;public class Target extends Object{public Target(){} bar foo(){}public static void main(Array<String> args){ Person foo=PersonBuilder.build(new BuildFoo());return ;}} class Person extends Object{ String name; int age;public Person(String n,int a){name=n;age=a;}} class SetName extends Object{ SetName(){}} class SetAge extends Object{ SetAge(){}} class PersonBuilder extends Object{private String name;private int age;private PersonBuilder(){}public static Person build(BuildPerson bp){ PersonBuilder b=new PersonBuilder();bp.buildPerson(b);return new Person(b.name,b.age);}public void setName(String n){name=n;return ;}public void setAge(int a){age=a;return ;}}abstract class BuildPerson extends Object{ BuildPerson(){}abstract void buildPerson(PersonBuilder p);} class BuildFoo extends BuildPerson{ BuildFoo(){} void buildPerson(PersonBuilder p){p.setName("Foo");p.setAge(0);return ;}}