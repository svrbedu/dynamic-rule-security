The document assumes you are using Spring 2.5 and Drools 4.0.

# Spring Configuration #
You will need to add additional spring configuration. Below is a sample that configures the library to use the Spring Security permission resolver, and a thread-local acl context. Additionally, the security rules are stored in a file "security-rules.drl" located on the classpath:

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       	   xmlns:aop="http://www.springframework.org/schema/aop"
	   xmlns:security="http://www.springframework.org/schema/security"
	   xmlns:context="http://www.springframework.org/schema/context"
	   xmlns:jee="http://www.springframework.org/schema/jee"
	   xmlns:tx="http://www.springframework.org/schema/tx"
	   xmlns:util="http://www.springframework.org/schema/util"
       	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd
                           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.0.xsd
                           http://www.springframework.org/schema/security http://www.springframework.org/schema/security/spring-security-2.0.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd
                           http://www.springframework.org/schema/jee http://www.springframework.org/schema/jee/spring-jee-2.5.xsd
                           http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd
                           http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-2.5.xsd">

    <!-- AOP -->
    <aop:aspectj-autoproxy/>
    
    <bean id="aclContext" class="net.orangemile.security.acl.ThreadLocalAclContext" />

    <bean id="permissionResolver" class="net.orangemile.security.spring.SpringPermissionResolver">
    	<property name="permissionMap">
		<util:map>
		    <entry key="NONE" value="0" />
		    <entry key="ADMINISTRATION" value="1" />
		    <entry key="DELETE" value="2" />
		    <entry key="READ" value="4" />
		    <entry key="WRITE" value="8" />
		    <entry key="EXECUTE" value="16"/>
		    <entry key="CREATE" value="32"/>
		</util:map>
    	</property>
    </bean>

    <bean id="droolsSecurityResolver" class="net.orangemile.security.drools.DroolsSecurityResolver">
	<constructor-arg value="classpath:security-rules.drl" />
    	<property name="permissionResolver" ref="permissionResolver" />
	<property name="aclContext" ref="aclContext" />
    </bean>

    <bean id="securityAspect" class="net.orangemile.security.SecurityAspect">
    	<property name="securityResolver" ref="droolsSecurityResolver" />
    </bean>

</beans>
```

If you are using Spring Security, you should use the net.orangemile.security.spring.SpringPermissionResolver, otherwise, you will need to write your own. The role of the PermissionResolver is to hide the user security implementation from the rule engine be it Spring Security, or something else.

As far as the AclContext, the role of that class is to act as a gateway between the rule engine and the system. The rule engine will notify AclContext with ACL changes. The system should look to the AclContext to read the ACL. For example, there maybe a taglib which will read the AclContext to find out how to render a given object.

The SecurityResolver class is meant as an abstraction of the rule engine. It's plausible that in some cases, the rule engine may not be used. The current implementation only provides the DroolsSecurityResolver, which takes a DRL rule file.

# Rules #
The current implementation only supports a single rule file per system. Below is a sample rule file:

```
package net.orangemile.security;

global net.orangemile.security.SecuritySupport security;
import function net.orangemile.security.acl.Permission.*;

import net.orangemile.security.domain.Person;

rule "Rule 1"
	# If the persons age is greater than 25, sets read/write acess to username
	when
		person : Person(age > 25)
	then
		security.setAcl("userName", READ, WRITE );		
end

rule "Rule 2"
	# disallows access to a person younger than 15
	when
		person : Person(age <15)
	then
		retract(person)
end

rule "Rule 3"
	# disallows access to age, if user is not in role SEE_AGE or ADMIN
	when
		person : Person()
                eval( !(security.isUserInRole("SEE_AGE") || security.isUserInRole("ADMIN")) )
	then
		security.setAcl("age", NONE );
end
```

The DRL rule file supports the full Jboss Drools authoring.

The READ, WRITE, etc... static permissions defined in the net.orangemile.security.acl.Permission class. If you have your own custom ACL permissions, you should add an additional function/static import. The security object is a special utility class that provides useful methods such as setting the object acl.

# AOP #
In order to enable rule security, you will need to mark you code with a @secure annotation.

```
	@Secure(secureResult=true)
	public Person getPerson() {
		Person p = new Person();
		p.setName("jack");
		return p;
	}
```
The value returned by the call getPerson will be run through dynamic security, before returning control to the system.

```
	@Secure(secureResult=true, secureCall=true)
	public Person getPerson() {
		Person p = new Person();
		p.setName("jack");
		return p;
	}
```
The call to the getPerson will be intercepted. The system will expose a special SecureJoinPoint class to the rule engine, which you can use to test whether the call is allowed. If the call is allowed, the returned result will be run through the dynamic security, before control is returned to the system.

# Example for Secure Call #

Assuming your method call looks like this:
```
	@Secure(secureCall=true)
	public Person getSecurePerson() {
		Person p = new Person();
		p.setName("secure-call");
		return p;	
	}
```

Your rule would look like this:
```
rule "Rule 2"
	when
		jp : SecureJoinPoint(signatureName == "getSecurePerson")
		eval( jp.getTarget().getClass().getName().equalsIgnoreCase("net.orangemile.security.domain.PersonDaoImpl") )

	then
		System.out.println(jp.getSignatureName());
		retract( jp );				
end
```

Before the call is made, the system checks whether a secureCall flag is set. If it is, it builds a SecureJoinPoint object with various parameters regarding the call. Take a look at the net.orangemile.security.SecureJoinPoint for more details. For how the class is used, take a look at the net.orangemile.security.SecurityAspect class.

# JSTL Tags #
You can use the isGranted tag in your JSP's.
```
<%@ taglib uri="http://www.orangemile.net/tags/security" prefix="auth" %>
```
```
<auth:isGranted object="<%= trade %>" sid="userName" permission="READ">
	<%= trade.getUserName() %>
</auth:isGranted>
```
The tag takes the root object on which rule security has already been run, a property of the object that the permissions apply for, and a comma delimited list of permissions. The isGranted tag acts like an if statement, if the permissions are granted, the body will be evaluated/rendered, otherwise, the body will be skipped.

The string "READ" is resolved to the actual permission mask via the PermissionResolver. Notice that in the spring configuration setup, the permission resolver was initialized with a map of String to int masks.

An important note is that the IsGrantedTag needs to have the bean "permissionResolver" and "aclContext" defined. This is the current work around due to the limitations of injecting dependencies into JSTL Tags libs.

# Samples #
A complete sample is available in the orangemile-security-test.war. Just download, and deploy to a webserver. All the source code is available in the main distribution under the testsrc/web/ directory.

To login, user: **rod**  password: **password**

Then navigate to: http://localhost:8080/orangemile-security-test/secure/secure.jsp
(or something similiar depending on your webserver configuration)


```
<%@ taglib uri="http://www.orangemile.net/tags/security" prefix="auth" %>
<%@ page import="net.orangemile.security.test.Trade, 
			 		org.springframework.web.context.WebApplicationContext,
					org.springframework.web.context.support.WebApplicationContextUtils,
					net.orangemile.security.test.TradeDao" %>
<html>
<head>
</head>
<body>

<%
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
		TradeDao tradeDao = (TradeDao) ctx.getBean("tradeDao");
		Trade trade = tradeDao.getTrade();
		System.out.println(trade);
%>

<auth:isGranted object="<%= trade %>" sid="userName" permission="READ">
	<%
		out.println("Secured Content - Username: " + trade.getUserName() + "<p>");
	%>
</auth:isGranted>

<auth:isGranted object="<%= trade %>" sid="price" permission="READ">
	<%
		out.println("Secured Content - Price: " + trade.getPrice() + "<p>");
	%>
</auth:isGranted>


</body>
</html>
```