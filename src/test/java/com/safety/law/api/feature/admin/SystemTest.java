package com.safety.law.api.feature.admin;


import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.io.IOException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.AbstractFieldsSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safety.law.api.base.BaseTest;
import com.safety.law.domain.admin.controller.SystemController.EnumerationType;
import com.safety.law.domain.admin.model.system.EnumerationRS;
import com.safety.law.global.jpa.repository.UsersRepository;
import com.safety.law.global.security.model.TokenModel;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("local")
public class SystemTest extends BaseTest{

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String BASE_URL = "/api/v1/system";

    @Test
    public void enumeration() throws Exception{

        List<String> types = new ArrayList<>();

        for(EnumerationType enumeration : EnumerationType.values()){
            types.add(enumeration.name());
        }

        for(String type : types){
            this.baseEnumerationDocs(type);
        }
    }

    private void baseEnumerationDocs(String type) throws Exception {
        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        // request        
        ResultActions result = this.mockMvc.perform(
            get(this.BASE_URL + "/enumeration")
            .contextPath("/api")
            .param("type", type)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        );         

        MvcResult mvcResult = result.andReturn();         
        EnumerationRS enumDocsResponse = parseResult(mvcResult);

        result.andExpect(status().isOk())
            .andDo(print())
            .andDo(
                document(
                    "system/enumeration/" + type.toLowerCase(),
                    customResponseFields(
                        "enum-response", 
                        beneathPath("enumeration").withSubsectionId("enumeration"),
                        attributes(key("enumTypeName").value(type)),             
                        enumConvertFieldDescriptor((enumDocsResponse.getEnumeration()))                
                    )
                )
            );
    }

    // Map으로 넘어온 enumValue를 fieldWithPath로 변경하여 리턴    
    private static FieldDescriptor[] enumConvertFieldDescriptor(Map<String, String> enumValues) {
        return enumValues.entrySet().stream()
                .map(x -> fieldWithPath(x.getKey()).description(x.getValue()))
                .toArray(FieldDescriptor[]::new);    
    }

    // 응답값 Body를 파싱하여 다시 자바 객체로 직렬화    
    private EnumerationRS parseResult(MvcResult result) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
    
        return objectMapper.readValue(result.getResponse().getContentAsByteArray(), EnumerationRS.class);
    }
            
    // 커스텀 템플릿 사용을 위한 함수    
    public static CustomResponseFieldsSnippet customResponseFields (String type, PayloadSubsectionExtractor<?> subsectionExtractor, Map<String, Object> attributes, FieldDescriptor... descriptors) {
        return new CustomResponseFieldsSnippet(type, subsectionExtractor, Arrays.asList(descriptors), attributes, true);    
    }

    public static class CustomResponseFieldsSnippet extends AbstractFieldsSnippet {
        public CustomResponseFieldsSnippet(String type, PayloadSubsectionExtractor<?> subsectionExtractor, List<FieldDescriptor> descriptors, Map<String, Object> attributes, boolean ignoreUndocumentedFields) {
            super(type, descriptors, attributes, ignoreUndocumentedFields, subsectionExtractor);        
        }

        @Override
        protected MediaType getContentType(Operation operation) {
            return operation.getResponse().getHeaders().getContentType();        
        }

        @Override
        protected byte[] getContent(Operation operation) throws java.io.IOException {
            return operation.getResponse().getContent();        
        }
    }


}
