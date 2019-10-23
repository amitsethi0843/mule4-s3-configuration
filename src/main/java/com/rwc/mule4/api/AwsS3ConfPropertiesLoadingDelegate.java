package com.rwc.mule4.api;

import static org.mule.metadata.api.model.MetadataFormat.JAVA;
import static org.mule.runtime.api.meta.Category.SELECT;
import static org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED;

import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.runtime.api.meta.model.declaration.fluent.ConfigurationDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer;
import org.mule.runtime.api.meta.model.display.DisplayModel;
import org.mule.runtime.extension.api.loader.ExtensionLoadingContext;
import org.mule.runtime.extension.api.loader.ExtensionLoadingDelegate;

public class AwsS3ConfPropertiesLoadingDelegate implements ExtensionLoadingDelegate{

	public static final String EXTENSION_NAME = "s3-configuration";
    public static final String CONFIG_ELEMENT = "config";
    public static final String ACCESS_KEY = "accessKey";
    public static final String ACCESS_SECRET = "accessSecret";
    public static final String BUCKET_NAME = "bucketName";
    public static final String REGION = "region";
    public static final String FILE_PATH = "filePath";

    
	@Override
	public void accept(ExtensionDeclarer extensionDeclarer, ExtensionLoadingContext context) {
		 ConfigurationDeclarer configurationDeclarer = extensionDeclarer.named(EXTENSION_NAME)
			        .describedAs(String.format("Crafted %s Extension", EXTENSION_NAME))
			        .withCategory(SELECT)
			        .onVersion("1.0.0")
			        // TODO replace with you company name
			        .fromVendor("RWC")
			        // This defines a global element in the extension with name config
			        .withConfig(CONFIG_ELEMENT);
		  ParameterGroupDeclarer defaultParameterGroup = configurationDeclarer.onDefaultParameterGroup();
//		    // TODO you can add/remove configuration parameter using the code below.
		    defaultParameterGroup
		        .withRequiredParameter("path").ofType(BaseTypeBuilder.create(JAVA).stringType().build())
//		        .withDisplayModel(DisplayModel.builder().displayName("path").build())
		        .withExpressionSupport(NOT_SUPPORTED)
		        .describedAs("for loading properties from s3");
		    ParameterGroupDeclarer parameterGroupDeclarer = configurationDeclarer.onParameterGroup("s3conf").withDslInlineRepresentation(true);
		    parameterGroupDeclarer.withRequiredParameter(ACCESS_KEY).ofType(BaseTypeBuilder.create(JAVA).stringType().build());
		    parameterGroupDeclarer.withRequiredParameter(ACCESS_SECRET).ofType(BaseTypeBuilder.create(JAVA).stringType().build());
		    parameterGroupDeclarer.withRequiredParameter(BUCKET_NAME).ofType(BaseTypeBuilder.create(JAVA).stringType().build());
		    parameterGroupDeclarer.withRequiredParameter(FILE_PATH).ofType(BaseTypeBuilder.create(JAVA).stringType().build());
		    parameterGroupDeclarer.withRequiredParameter(REGION).ofType(BaseTypeBuilder.create(JAVA).stringType().
		    		enumOf("AP_EAST_1","AP_NORTHEAST_1","AP_NORTHEAST_2","AP_SOUTH_1","AP_SOUTHEAST_1",
		    	    		"AP_SOUTHEAST_2","CA_CENTRAL_1","CN_NORTH_1","CN_NORTHWEST_1","EU_CENTRAL_1","EU_NORTH_1",
		    	    		"EU_WEST_1","EU_WEST_2","EU_WEST_3","ME_SOUTH_1","SA_EAST_1","US_EAST_1","US_EAST_2",
		    	    		"US_GOV_EAST_1","US_WEST_1","US_WEST_2").build());
//		    withDisplayModel(DisplayModel.builder().displayName(FILE_PATH).build())
//	        .withExpressionSupport(NOT_SUPPORTED);

	}

}
