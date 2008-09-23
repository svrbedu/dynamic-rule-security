package net.orangemile.security;


import net.orangemile.security.acl.Acl;
import net.orangemile.security.acl.AclContext;
import net.orangemile.security.acl.Permission;
import net.orangemile.security.acl.ThreadLocalAclContext;
import net.orangemile.security.domain.Person;
import net.orangemile.security.domain.PersonDaoImpl;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SecurityTest {

	private static ApplicationContext ctx;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.configure();
		ctx = new ClassPathXmlApplicationContext("classpath:/spring/*.xml");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void mainTest() throws Exception {
		PersonDaoImpl personDao = (PersonDaoImpl) ctx.getBean("personDao");
		Person p = personDao.getPerson();
		System.out.println( p );
		
		AclContext aclCtx = (AclContext) ctx.getBean("aclContext");
		System.out.println( ((ThreadLocalAclContext) aclCtx).getAll() );
		Acl acl = aclCtx.get( p );
		System.out.println( acl );
		if ( acl != null ) {
			System.out.println("IsGranted: " + acl.isGranted("userName", Permission.WRITE) );
		}
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

}
