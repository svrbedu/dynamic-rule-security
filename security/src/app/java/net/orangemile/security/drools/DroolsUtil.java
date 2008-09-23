package net.orangemile.security.drools;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.compiler.DroolsError;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.PackageBuilderErrors;

/**
 * @author Orange Mile, Inc
 */
public class DroolsUtil {

	// logging
	private static final Logger log = Logger.getLogger( DroolsUtil.class );
	
	/**
	 * Compiles the given rule strings into a single rulebase
	 * @param rules
	 * @return
	 * @throws Exception
	 */
	public static RuleBase compile( List<String> rules ) throws Exception 
    {
        Properties properties = new Properties();
        properties.setProperty( "drools.dialect.java.compiler", "JANINO" );
        properties.setProperty( "drools.dialect.default", "java" );
        properties.setProperty( "drools.dialect.java.compiler.lnglevel", "1.5" );
        
        // properties.setProperty("drools.dump.dir", "/tmp");
		// log.debug("drools.dump.dir value is set to " + dumpDir);

		PackageBuilderConfiguration conf = new PackageBuilderConfiguration(properties);
		PackageBuilder builder = new PackageBuilder(conf);
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();

		for (String rule : rules) {
			StringReader drl = new StringReader(rule);
			builder.addPackageFromDrl(drl);
			PackageBuilderErrors errors = builder.getErrors();
			for ( DroolsError error : errors.getErrors() ) {
				log.warn( error );
			}
		}        
		ruleBase.addPackage( builder.getPackage());
		return ruleBase;
	}
	
	/**
	 * Compiles the given DRL into a rulebase
	 * @param drlReader
	 * @return
	 * @throws Exception
	 */
	public static RuleBase compileDrl( Reader drlReader ) throws Exception {
		PackageBuilder builder = new PackageBuilder();
		builder.addPackageFromDrl( drlReader );
		org.drools.rule.Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage( pkg );
		return ruleBase;
	}

	/**
	 * Compiles the given xml stream into a rulebase
	 * @param xmlReader
	 * @return
	 * @throws Exception
	 */
	public static RuleBase compileXml( Reader xmlReader ) throws Exception {
		PackageBuilder builder = new PackageBuilder();
		builder.addPackageFromXml( xmlReader );
		org.drools.rule.Package pkg = builder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage( pkg );
		return ruleBase;
	}
	
}
