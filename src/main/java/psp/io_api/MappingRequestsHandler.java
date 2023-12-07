package psp.io_api;
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

import psp.io_api.json_processing.data_objects.PSPCorrectMappingResponse;
import psp.io_api.json_processing.data_objects.psp_mapping_request.PSPMappingRequest;
import psp.mappings.PatternMapper;
import psp.sel.patterns.Pattern;
import psp.sel.scopes.Scope;
public class MappingRequestsHandler {
  public static void main(String args[]) {
        File file = new File("test_pattern_v2.json");
        PSPMappingRequest mapPSPRequestToTargetLogic = mapJsonToPSPRequest(file);
    }

  public static String mapPSPRequestToTargetLogic(String jsonPSPRequest) {
        PSPMappingRequest request = mapJsonToPSPRequest(jsonPSPRequest);
        Scope scope = request.getScope();
        Pattern pattern = request.getPattern();
        PatternMapper mapper = request.getMapper();
        String seg = pattern.getSpecificationAsSEL();
        String mapping = mapper.getMapping(scope, pattern);

        PSPCorrectMappingResponse response = new PSPCorrectMappingResponse(seg,mapping);

        return mapPSPMappingResultToJson(response);
  }
  public static String mapPSPRequestToTargetLogic(File pspRequest) {
        PSPMappingRequest request = mapJsonToPSPRequest(pspRequest);
        Scope scope = request.getScope();
        Pattern pattern = request.getPattern();
        PatternMapper mapper = request.getMapper();
        String seg = pattern.getSpecificationAsSEL();
        String mapping = mapper.getMapping(scope, pattern);

        PSPCorrectMappingResponse response = new PSPCorrectMappingResponse(seg,mapping);

        return mapPSPMappingResultToJson(response);
  }

  private static PSPMappingRequest mapJsonToPSPRequest(String jsonStr) {
    ObjectMapper mapper = new ObjectMapper();
    PSPMappingRequest psp;
    try {
      psp = mapper.readValue(jsonStr, PSPMappingRequest.class);
      return psp;
    } catch (IOException e){
      System.out.println(e);
      return null;
    }
  }

  private static PSPMappingRequest mapJsonToPSPRequest(File file) {
    ObjectMapper mapper = new ObjectMapper();
    PSPMappingRequest psp;
    try {
      psp = mapper.readValue(file, PSPMappingRequest.class);
      return psp;
    } catch (IOException e){

      System.out.println(e);
      return null;
    }
  }

  private static String mapPSPMappingResultToJson(PSPCorrectMappingResponse response){
    ObjectMapper mapper = new ObjectMapper();
    String psp;
    try {
      psp = mapper.writeValueAsString(response);
      return psp;
    } catch (IOException e){
      System.out.println(e);
      return null;
    }
  }

/*     private void updateSELandMapping() {
      StringBuilder sb = new StringBuilder();

      if (fSelectedScope != null && fSelectedPattern != null) {
          sb.append(fSelectedScope.getSpecificationAsSEL());
          sb.append(", ");
          sb.append(fSelectedPattern.getSpecificationAsSEL());
          sb.append('.');

          fSELP.setText(sb.toString());

          PatternMapper lMapper = (PatternMapper) fMappings.getSelectedItem();

          if (lMapper != null) {
              String lMapping = lMapper.getMapping(fSelectedScope, fSelectedPattern);

              if (!lMapping.isEmpty()) {
                  if (lMapper.hasMappingErrorOccurred())
                      fMapping.setForeground(Color.red);
                  else
                      fMapping.setForeground(Color.black);
                  fMapping.setText(lMapping);
              } else {
                  fMapping.setForeground(Color.red);
                  fMapping.setText(lMapper.getNotSupportedMessage());
              }
          }
      }
  } */


}