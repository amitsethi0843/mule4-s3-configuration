package com.rwc.mule4.api;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Optional.of;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.lifecycle.InitialisationException;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationProperty;
import org.mule.runtime.config.api.dsl.model.properties.DefaultConfigurationPropertiesProvider;
import org.yaml.snakeyaml.Yaml;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class CustomConfigurationPropertiesProvider extends DefaultConfigurationPropertiesProvider {

	  protected static final String PROPERTIES_EXTENSION = ".properties";
	  protected static final String YAML_EXTENSION = ".yaml";
	  protected static final String UNKNOWN = "unknown";
	  
	  protected final Map<String, ConfigurationProperty> configurationAttributes = new HashMap<>();
	  protected String filePath;
	  protected String accessKey;
	  protected String accessSecret;
	  protected String bucketName;
	  protected String region;
	  protected AmazonS3 s3Client;
	  protected ResourceProvider resourceProvider;

	  
	public CustomConfigurationPropertiesProvider(String filePath,String accessKey, String accessSecret, String bucketName,String region, ResourceProvider resourceProvider) {
		super(filePath,resourceProvider);
		this.filePath=filePath;
		this.accessKey=accessKey;
		this.accessSecret=accessSecret;
		this.bucketName=bucketName;
		this.region=region;
		this.resourceProvider=resourceProvider;
		AWSCredentials credentials=new BasicAWSCredentials(accessKey, accessSecret);
		s3Client=AmazonS3ClientBuilder.standard()
		  .withCredentials(new AWSStaticCredentialsProvider(credentials))
		  .withRegion(Regions.valueOf(region))
		  .build();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	  public Optional<ConfigurationProperty> getConfigurationProperty(String configurationAttributeKey) {
	    return Optional.ofNullable(configurationAttributes.get(configurationAttributeKey));
	  }


	@Override
	 public void initialise() throws InitialisationException{
		if (!filePath.endsWith(PROPERTIES_EXTENSION) && !filePath.endsWith(YAML_EXTENSION)) {
		      throw new RuntimeException("Configuration properties file must end with yaml or properties extension");
		    }
		 try (InputStream is = getResourceInputStream()) {
		      if (is == null) {
		        throw new RuntimeException("Couldn't find configuration properties file neither on classpath or in file system");
		      }
		      readAttributesFromFile(is);
		    } 
		     catch (Exception ex) {
		     System.out.println(ex.getMessage());
		    }
	}
	
	@Override
	  public String getDescription() {
	    ComponentLocation location = (ComponentLocation) getAnnotation(LOCATION_KEY);
	    return format("<custom-configuration-properties file=\"%s\"> - file: %s, line number: %s", fileLocation,
	                  location.getFileName().orElse(UNKNOWN),
	                  location.getLineInFile().map(String::valueOf).orElse("unknown"));

	  }
	
	private InputStream getResourceInputStream() throws IOException {
		S3Object s3object = s3Client.getObject(bucketName, filePath);
		S3ObjectInputStream inputStream = s3object.getObjectContent();
		return inputStream;
	}
	
	private boolean isAbsolutePath(String file) {
	    return new File(file).isAbsolute();
	  }

	
	protected void readAttributesFromFile(InputStream is) throws IOException {
	    if (filePath.endsWith(PROPERTIES_EXTENSION)) {
	      Properties properties = new Properties();
	      properties.load(is);
	      properties.keySet().stream().map(key -> {
	        Object rawValue = properties.get(key);
	        rawValue = createValue((String) key, (String) rawValue);
	        return new CustomConfigurationProperty(of(this), (String) key, rawValue);
	      }).forEach(configurationAttribute -> {
	        configurationAttributes.put(configurationAttribute.getKey(), configurationAttribute);
	      });
	    } else {
	      Yaml yaml = new Yaml();
	      Iterable<Object> yamlObjects = yaml.loadAll(is);
	      yamlObjects.forEach(yamlObject -> {
	        createAttributesFromYamlObject(null, null, yamlObject);
	      });
	    }
	  }

	  protected void createAttributesFromYamlObject(String parentPath, Object parentYamlObject, Object yamlObject) {
	    if (yamlObject instanceof List) {
	      List list = (List) yamlObject;
	      if (list.get(0) instanceof Map) {
	        list.forEach(value -> createAttributesFromYamlObject(parentPath, yamlObject, value));
	      } else {
	        if (!(list.get(0) instanceof String)) {
	          throw new RuntimeException("List of complex objects are not supported as property values. Offending key is ");
	        }
	        String[] values = new String[list.size()];
	        list.toArray(values);
	        String value = join(",", list);
	        configurationAttributes.put(parentPath, new CustomConfigurationProperty(this, parentPath, value));
	      }
	    } else if (yamlObject instanceof Map) {
	      if (parentYamlObject instanceof List) {
	        throw new RuntimeException("Configuration properties does not support type a list of complex types. Complex type keys are: ");
	      }
	      Map<String, Object> map = (Map) yamlObject;
	      map.entrySet().stream()
	          .forEach(entry -> createAttributesFromYamlObject(createKey(parentPath, entry.getKey()), yamlObject, entry.getValue()));
	    } else {
	      if (!(yamlObject instanceof String)) {
	        throw new RuntimeException("YAML configuration properties only supports string values, make sure to wrap the value with so you force the value to be an string.");
	      }
	      String resultObject = createValue(parentPath, (String) yamlObject);
	      configurationAttributes.put(parentPath, new CustomConfigurationProperty(this, parentPath, resultObject));
	    }
	  }

	  protected String createKey(String parentKey, String key) {
	    if (parentKey == null) {
	      return key;
	    }
	    return parentKey + "." + key;
	  }

	  protected String createValue(String key, String value) {
	    return value;
	  }
	  
	  
	 
}
