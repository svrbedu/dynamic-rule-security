This project is an extension of Spring Security and JBoss Rules ( Drools ) and allows a system to have dynamic rule based security that's context and request aware. Dynamic rules can restrict access to specific fields, objects, and/or resources or setup custom ACL per field, object, and resource.

_For example:_

Imagine restricting access to a pojo based on the requested objects attributes.
```
when
   trade : Trade(traderName == "Jack" && amount > $50,000,000 )
then 
   retract(trade);
```

Imagine restricting access to a field based on the context of the call and the attributes of the requested object!

```
when 
   trade : Trade(traderName == "Jack" && currentUser != "Jack" ) 
then
   security.setAcl("amount", NONE );
   security.setAcl("account", READ | EDIT );
```

This project allows all the business security rules to be centrally managed, and alleviates the different system tiers from dealing with security. For example, the presentation tier only needs to worry about following the set ACL rather than having business logic that decides what ACL to set.

Additionally, the general premise of this project is to externalize the security rules from the code. This means the code should be rather free of the security logic. One way to do this is via AOP and annotations. The easiest way to start to use dynamic rules is by annotating your data retriever methods:

```
@Secure(secureResult=true)
public Trade getTrade( int tradeId ) {...}
```

AOP is then leveraged to intercept the return value, apply security rules on it, and then return the secured object.

We also include a JSTL Tag to simplify web development.
```
<auth:isGranted object="<%= trade %>" sid="userName" permission="READ">
    <%= trade.getUserName() %>
</auth:isGranted>
```

The reason to choose Drools is that it's a leading Open Source Rule Engine. The reason to choose Spring Security is that it provides a modularized, open implementation of a security framework. The project is not intending to reinvent the wheel with Drools or Spring, but rather build on top of what already exists and is generally accepted.