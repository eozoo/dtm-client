package com.cowave.zoo.dtm.client;

import com.cowave.zoo.dtm.client.tcc.TccMockController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author shanhuiming
 *
 */
@ContextConfiguration(classes = Application.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = DEFINED_PORT)
public class MockTccTest {

    @Autowired
    private TccMockController tccMockController;

    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(tccMockController).build();
    }

    @Test
    public void tcc1() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tcc/mock1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    /**
     * TODO <a href="https://github.com/dtm-labs/dtm/issues/545">BUG：这里期望直接失败执行补偿操作，但实际上一直重试</a>
     */
    @Test
    public void tcc2() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tcc/mock2")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    public void tcc3() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/tcc/mock3")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }
}
