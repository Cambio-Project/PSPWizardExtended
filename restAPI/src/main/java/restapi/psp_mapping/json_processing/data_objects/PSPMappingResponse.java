package restapi.psp_mapping.json_processing.data_objects;

import com.fasterxml.jackson.core.JsonProcessingException;


/**
 * The {@code PSPMappingResponse} interface represents the response from a mapping operation,
 * which maps a PSP request to some logical formalities, and a SEG (structured English grammar)
 * representation of the provided pattern.
 * Implementations of this interface should provide a method to convert the
 * response to a JSON representation.
 *
 * @author Aref El-Maarawi
 *
 */
public interface PSPMappingResponse {

    /**
     * Converts the PSP mapping response to its JSON representation.
     *
     * @return A {@code String} containing the JSON representation of the PSP mapping response.
     * @throws JsonProcessingException If an error occurs during the JSON processing.
     */
    String toJSON() throws JsonProcessingException;
}
