package id.go.jakarta.bksd.dokumen.mvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.ServletContext;

@WebAppConfiguration
@SpringBootTest
public class TestContextConfiguration {
    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    @Test
    public void testApplicationContextBeans() {
        ServletContext servletContext = ctx.getServletContext();
        assertNotNull(servletContext);
        assertNotNull(ctx.getBean("adminController"));
        assertNotNull(ctx.getBean("dokumenController"));
        assertNotNull(ctx.getBean("dokumenLuarNegeriController"));
        assertNotNull(ctx.getBean("kolaborasiController"));
        assertNotNull(ctx.getBean("laporanController"));
        assertNotNull(ctx.getBean("mainController"));
        assertNotNull(ctx.getBean("pelaksanaanController"));
        assertNotNull(ctx.getBean("pelaksanaanLuarNegeriController"));
        assertNotNull(ctx.getBean("pemakaiRestController"));
        assertNotNull(ctx.getBean("pemetaanController"));
        assertNotNull(ctx.getBean("userController"));
    }

    @Test
    public void testHomePage() throws Exception {
        mockMvc.perform(get("/")).andDo(print()).andExpect(view().name("home"));
    }
}
