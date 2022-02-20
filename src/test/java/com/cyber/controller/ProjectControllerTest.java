package com.cyber.controller;

import com.cyber.dto.ProjectDTO;
import com.cyber.dto.RoleDTO;
import com.cyber.dto.UserDTO;
import com.cyber.enums.Gender;
import com.cyber.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    MockMvc mockMvc;

    private final String adminToken = "eyJhbGciOiJIUzI1NiJ9.eyJmaXJzdE5hbWUiOiJhZG1pbiIsImxhc3ROYW1lIjoiYWRtaW4iLCJzdWIiOiJhZG1pbkBhZG1pbi5jb20iLCJpZCI6MSwiZXhwIjoxNjQ1NDIxNTUyLCJpYXQiOjE2NDUzODU1NTIsInVzZXJuYW1lIjoiYWRtaW5AYWRtaW4uY29tIn0.b8EgLZxPl_K1iBZ9Q2emfUuyc34aCj5VdKA-TmgieaE";

    static UserDTO userDTO;
    static ProjectDTO projectDTO;

    @BeforeAll
    static void setUp(){
        userDTO = UserDTO.builder()
                .id(2L)
                .firstName("mike")
                .lastName("smith")
                .userName("mike@ticketng.com")
                .passWord("abc123")
                .confirmPassword("abc123")
                .role(new RoleDTO(2L,"Manager"))
                .gender(Gender.MALE)
                .build();

        projectDTO = ProjectDTO.builder()
                .projectCode("Api1")
                .projectName("Api")
                .assignedManager(userDTO)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(5))
                .projectDetail("Api Test")
                .projectStatus(Status.OPEN)
                .completeTaskCount(0)
                .incompleteTaskCount(0)
                .build();
    }

    //test if security works, call end point without token
    @Test
    public void givenNoToken_whenGetSecureRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/project/Api1"))
                .andExpect(status().is4xxClientError());
    }

    //create one project, and do this test !! will pass!!
    //get project
    //Jayway JsonPath -- $.data[0].projectCode
    @Test
    public void givenToken_getAllProjects() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/project")
                        .header("Authorization",adminToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].projectCode").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].assignedManager.userName").isNotEmpty());
    }

    //create projectDTO
    @Test
    public void givenToken_createProject() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/project")
                        .header("Authorization", adminToken)
                        .content(toJsonString(projectDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.projectCode").isNotEmpty());
    }

    //update project
    @Test
    public void givenToken_updateProject() throws Exception {
        projectDTO.setId(1L);
        projectDTO.setProjectName("Api updated !!");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/project")
                        .header("Authorization", adminToken)
                        .content(toJsonString(projectDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Project is updated successfully"));
    }

    //delete project
    @Test
    public void givenToken_deleteProject() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/project/" + projectDTO.getProjectCode())
                        .header("Authorization", adminToken)
                        .content(toJsonString(projectDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Certain project is deleted successfully"));
    }

    //convert Json to String
    protected String toJsonString(final Object obj) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}