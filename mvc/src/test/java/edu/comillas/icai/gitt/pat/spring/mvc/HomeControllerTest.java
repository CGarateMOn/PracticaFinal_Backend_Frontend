package edu.comillas.icai.gitt.pat.spring.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = HomeController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHomeEnRaiz_DevuelveVista() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("PistaPadel")));
    }

    @Test
    void testHomeEnRutaBase_DevuelveVista() throws Exception {
        mockMvc.perform(get("/pistaPadel"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("La manera más clara y elegante de empezar cada partido.")));
    }
}
