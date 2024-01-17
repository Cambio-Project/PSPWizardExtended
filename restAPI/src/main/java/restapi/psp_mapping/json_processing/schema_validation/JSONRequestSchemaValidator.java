package restapi.psp_mapping.json_processing.schema_validation;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * The {@code JSONRequestSchemaValidator} class is responsible for validating the JSON mapping request
 * against the predefined schema.
 *
 * @author Aref El-Maarawi
 */

public class JSONRequestSchemaValidator {
    private static final ObjectMapper mapper = new ObjectMapper();


    /**
     * Validates the given JSON input against a predefined schema.
     *
     * @param input The JSON input to be validated.
     * @return A {@code ProcessingReport} indicating the result of the validation.
     * @throws IOException If an I/O error occurs while reading the JSON input or schema.
     * @throws ProcessingException If an error occurs during JSON schema processing.
     */
    public static ProcessingReport validateSchema(String input) throws IOException, ProcessingException {
        JsonNode json_request = mapper.readTree(input);
        InputStream schema_stream = new ClassPathResource("request_schema.json").getInputStream();
        final JsonNode fstabSchema = JsonLoader.fromReader(new InputStreamReader( schema_stream, StandardCharsets.UTF_8));
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final JsonSchema schema = factory.getJsonSchema(fstabSchema);
        return schema.validate(json_request);
    }


}
