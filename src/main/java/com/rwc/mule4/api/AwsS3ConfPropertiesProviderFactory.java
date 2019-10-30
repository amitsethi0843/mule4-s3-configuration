package com.rwc.mule4.api;

import static com.rwc.mule4.api.AwsS3ConfPropertiesLoadingDelegate.ACCESS_KEY;
import static com.rwc.mule4.api.AwsS3ConfPropertiesLoadingDelegate.ACCESS_SECRET;
import static com.rwc.mule4.api.AwsS3ConfPropertiesLoadingDelegate.BUCKET_NAME;
import static com.rwc.mule4.api.AwsS3ConfPropertiesLoadingDelegate.CONFIG_ELEMENT;
import static com.rwc.mule4.api.AwsS3ConfPropertiesLoadingDelegate.EXTENSION_NAME;
import static com.rwc.mule4.api.AwsS3ConfPropertiesLoadingDelegate.FILE_PATH;
import static com.rwc.mule4.api.AwsS3ConfPropertiesLoadingDelegate.REGION;

import java.util.List;

import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.config.api.dsl.model.ConfigurationParameters;
import org.mule.runtime.config.api.dsl.model.ResourceProvider;
import org.mule.runtime.config.api.dsl.model.properties.ConfigurationPropertiesProviderFactory;

import javafx.util.Pair;


public class AwsS3ConfPropertiesProviderFactory implements ConfigurationPropertiesProviderFactory {
	
	public static final String EXTENSION_NAMESPACE = EXTENSION_NAME;
	public static final ComponentIdentifier CUSTOM_PROPERTIES_PROVIDER = 
			ComponentIdentifier.builder().namespace(EXTENSION_NAMESPACE).name(CONFIG_ELEMENT).build();
	@Override
	public ComponentIdentifier getSupportedComponentIdentifier() {
		// TODO Auto-generated method stub
		return CUSTOM_PROPERTIES_PROVIDER;
	}

	@Override
	public CustomConfigurationPropertiesProvider createProvider(ConfigurationParameters parameters,
			ResourceProvider externalResourceProvider) {

		ComponentIdentifier s3ConfComponentIdentifier =
				   ComponentIdentifier.builder().namespace(EXTENSION_NAME).name("s3conf").build();

		String filePath = parameters.getComplexConfigurationParameter(s3ConfComponentIdentifier).get(0).getStringParameter(FILE_PATH);
		String accessKey=parameters.getComplexConfigurationParameter(s3ConfComponentIdentifier).get(0).getStringParameter(ACCESS_KEY);
		String accessSecret=parameters.getComplexConfigurationParameter(s3ConfComponentIdentifier).get(0).getStringParameter(ACCESS_SECRET);
		String bucketName=parameters.getComplexConfigurationParameter(s3ConfComponentIdentifier).get(0).getStringParameter(BUCKET_NAME);
		String region=parameters.getComplexConfigurationParameter(s3ConfComponentIdentifier).get(0).getStringParameter(REGION);

		
		return new CustomConfigurationPropertiesProvider(filePath,accessKey,accessSecret,bucketName,region,externalResourceProvider);
	}

}
