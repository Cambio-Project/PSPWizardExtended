package restapi.psp_mapping.json_processing.schema_validation;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;

public class JSONRequestSchemaValidator {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ProcessingReport validateSchema(String input) throws IOException, ProcessingException {
        JsonNode json_request = mapper.readTree(input);
        File schema_file = new ClassPathResource("request_schema.json").getFile();
        final JsonNode fstabSchema = JsonLoader.fromFile(schema_file);
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final JsonSchema schema = factory.getJsonSchema(fstabSchema);
        return schema.validate(json_request);
    }


}
