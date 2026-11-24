package com.campuserp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Smoke tests: app boots, seed data loads, auth + role rules work. */
@SpringBootTest
@AutoConfigureMockMvc
class CampusErpApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {}

    @Test
    void unauthenticatedApiIsRejected() throws Exception {
        mockMvc.perform(get("/api/students")).andExpect(status().isUnauthorized());
    }

    @Test
    void seedStudentsLoad() throws Exception {
        mockMvc.perform(get("/api/students").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(8));
    }

    @Test
    void studentRoleCannotCreateStudent() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/api/students").with(httpBasic("student", "student123"))
                        .contentType("application/json")
                        .content("{\"name\":\"X\",\"rollNo\":\"X-1\",\"email\":\"x@college.edu\",\"course\":\"BTech CSE\",\"semester\":6}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void dashboardCountsSeedData() throws Exception {
        mockMvc.perform(get("/api/dashboard").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.students").value(8))
                .andExpect(jsonPath("$.timetableEntries").value(15));
    }
}
