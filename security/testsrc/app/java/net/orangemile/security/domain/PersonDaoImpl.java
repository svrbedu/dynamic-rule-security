package net.orangemile.security.domain;

import java.util.ArrayList;
import java.util.List;

import net.orangemile.security.Secure;

public class PersonDaoImpl {

	@Secure(secureResult=true)
	public List<Person> getPersons() {
		ArrayList<Person> persons = new ArrayList<Person>();
		persons.add( new Person("Matt", 55) );
		persons.add( new Person("Keith", 33) );
		return persons;		
	}
	
	@Secure(secureResult=true)
	public Person getPerson() {
		Person p = new Person();
		p.setName("jack");
		return p;
	}
	
}
