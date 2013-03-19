package builder.properties;

import java.util.Properties;

// This is a lame-o class that's kinda dirty.  maybe we can clean it up later, but we're using it across two scala versions right now.
public class BuilderProperties {

  private static Properties loadProperties() {
    Properties props = new Properties();
    java.io.InputStream in = BuilderProperties.class.getResourceAsStream("builder.properties");
    try {
      props.load(in);
    } catch(java.io.IOException e) { throw new RuntimeException(e); }
    finally { try { in.close(); } catch(java.io.IOException e) { throw new RuntimeException(e); }  }
    return props;
  }

  private static Properties props = loadProperties();
  /** Checks the system properties, before the environment, before the hard coded defaults. */
  private static String getProperty(String name) {
    String value = System.getProperty(name);
    if(value == null) {
      value = System.getenv(name);
    }
    if(value == null) {
      value = props.getProperty(name);
    }
    return value; 
  }
  
  /** Looks up a property value, and parses its value as appropriate. */
  private static String lookupOr(String name, String defaultValue) {
    String value = getProperty(name);
    if(value == null) {
      value = defaultValue;
    }
    return value;
  }

  public static String TEMPLATE_UUID_PROPERTY_NAME = "template.uuid";
  public static String BUILDER_ABI_VERSION_PROPERTY_NAME = "builder.abi.version";
  public static String SCRIPT_NAME = "builder";
  
  
  public static String APP_VERSION() {
    return props.getProperty("app.version");
  }

  public static String APP_ABI_VERSION() {
    // TODO - Encode ABI version in builder metadata...
    return APP_VERSION();
  }

  public static String APP_SCALA_VERSION() {
    return props.getProperty("app.scala.version");
  }
  
  public static String SBT_VERSION() {
    return props.getProperty("sbt.version");
  }

  public static String SBT_SCALA_VERSION() {
    return props.getProperty("sbt.scala.version");
  }

  public static String BUILDER_HOME() {
    return getProperty("builder.home");
  }

  public static String GLOBAL_USER_HOME() {
    return getProperty("user.home");
  }

  public static String BUILDER_USER_HOME() {
    return lookupOr("builder.user.home", getProperty("user.home") + "/.builder/" + APP_ABI_VERSION());
  }

  public static String BUILDER_TEMPLATE_CACHE() {
    return lookupOr("builder.template.cache", BUILDER_USER_HOME() + "/templates");
  }

  public static String BUILDER_TEMPLATE_LOCAL_REPO() {
    String defaultValue = BUILDER_HOME();
    if(defaultValue != null) {
      defaultValue = defaultValue + "/templates";
    }
    return lookupOr("builder.template.localrepo", defaultValue);
  }

  public static String BUILDER_LAUNCHER_JAR() {
    String value = BUILDER_HOME();
    String version = APP_VERSION();
    if(value != null && version != null) {
      // TODO - synch this with build in some better fashion!
      value = value+"/"+SCRIPT_NAME+"-launch-"+version+".jar";
    }
    return value;
  }

  public static String BUILDER_LAUNCHER_BAT() {
    String value = BUILDER_HOME();
    if(value != null) {
      value = value+"/"+SCRIPT_NAME+".bat";
    }
    return value;
  }
  public static String BUILDER_LAUNCHER_BASH() {
    String value = BUILDER_HOME();
    if(value != null) {
      value = value+"/"+SCRIPT_NAME;
    }
    return value;
  }
}