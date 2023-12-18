package psp.io_api;
import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import psp.io_api.exceptions.UnsupportedTypeException;
import psp.io_api.json_processing.data_objects.PSPCorrectMappingResponse;
import psp.io_api.json_processing.data_objects.PSPMappingError;
import psp.io_api.json_processing.data_objects.PSPMappingResponse;
import psp.io_api.json_processing.data_objects.PSPUnsupportedMappingResponse;
import psp.io_api.json_processing.data_objects.psp_mapping_request.PSPMappingRequest;
import psp.mappings.PatternMapper;
import psp.mappings.SELMapper;
import psp.sel.patterns.Pattern;
import psp.sel.scopes.Scope;
public class MappingRequestsHandler {
  public static void main(String[] args) {
        File file = new File("test_pattern_v2.json");
      PSPMappingResponse mapPSPRequestToTargetLogic = mapPSPRequestToTargetLogic(file);
      try {
          System.out.println(mapPSPRequestToTargetLogic.toJSON());
      } catch (JsonProcessingException e) {
          System.out.println(e.getMessage());
      }
    }

  public static PSPMappingResponse mapPSPRequestToTargetLogic(String jsonMappingRequest) {
      try {
          PSPMappingRequest request = mapJsonToPSPRequest(jsonMappingRequest);
          Scope scope = request.getScope();
          Pattern pattern = request.getPattern();
          PatternMapper requestedPSPMapper = request.getMapper();
          PatternMapper selMapper = new SELMapper();

          String seg =  selMapper.getMapping(scope, pattern);
          String mapping = requestedPSPMapper.getMapping(scope, pattern);

          if (mapping.isEmpty()){
              String error_message = requestedPSPMapper.getNotSupportedMessage();
              return new PSPCorrectMappingResponse(seg, error_message);
          }
          else {
              return new PSPCorrectMappingResponse(seg, mapping);
          }

      } catch (IOException e) {
          if (e instanceof UnsupportedTypeException) {
              return new PSPMappingError(e.getMessage());
          } else {
              return new PSPMappingError(String.format("Unexpected JSON serialization error: %s", e.getMessage()));
          }
      } catch (Exception e) {
          return new PSPMappingError(String.format("Unexpected PSP mapping error: %s", e.getMessage()));
      }
  }

    private static PSPMappingRequest mapJsonToPSPRequest(String jsonStr) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PSPMappingRequest psp;

        psp = mapper.readValue(jsonStr, PSPMappingRequest.class);
        return psp;
    }

  public static PSPMappingResponse mapPSPRequestToTargetLogic(File pspRequest) {
      try {
          PSPMappingRequest request = mapJsonToPSPRequest(pspRequest);
          Scope scope = request.getScope();
          Pattern pattern = request.getPattern();
          PatternMapper requestedPSPMapper = request.getMapper();
          PatternMapper selMapper = new SELMapper();

          String seg =  selMapper.getMapping(scope, pattern);
          String mapping = requestedPSPMapper.getMapping(scope, pattern);

          if (mapping.isEmpty()){
              String errorMessage = requestedPSPMapper.getNotSupportedMessage();
              return new PSPUnsupportedMappingResponse(errorMessage, seg);
          }
          else {
              return new PSPCorrectMappingResponse(seg, mapping);
          }

      } catch (IOException e) {
          if (e instanceof UnsupportedTypeException) {
              return new PSPMappingError(e.getMessage());
          } else {
              return new PSPMappingError(String.format("Unexpected JSON serialization error: %s", e.getMessage()));
          }
      } catch (Exception e) {
          return new PSPMappingError(String.format("Unexpected PSP mapping error: %s", e.getMessage()));
      }
  }

  private static PSPMappingRequest mapJsonToPSPRequest(File file) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(file, PSPMappingRequest.class);
  }

}