package org.dpppt.backend.sdk.ws.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dpppt.backend.sdk.data.DPPPTDataService;
import org.dpppt.backend.sdk.model.ExposedOverview;
import org.dpppt.backend.sdk.model.Exposee;
import org.dpppt.backend.sdk.model.ExposeeAuthData;
import org.dpppt.backend.sdk.model.ExposeeRequest;
import org.dpppt.backend.sdk.ws.Application;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("dev")
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class DPPPTControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private DPPPTDataService dataService;

    @Captor private ArgumentCaptor<Exposee> exposeeCaptor;

    @Test
    void shouldSayHello() throws Exception {
        mockMvc.perform(get("/v1")).andExpect(status().isOk())
                .andExpect(content().string("Hello from DP3T WS"));
    }

    @Test
    void shouldAddExposed() throws Exception {
        // given
        ExposeeRequest request = getExposeeRequest();

        // when
        mockMvc.perform(post("/v1/exposed")
                .content(objectMapper.writeValueAsString(request))
                .contentType("application/json")
                .header("User-Agent", "user-agent")
        ).andExpect(status().isOk())
                .andExpect(content().string(""));

        // then
        verify(dataService, times(1)).upsertExposee(exposeeCaptor.capture(), anyString());

        Exposee exposee = exposeeCaptor.getValue();
        assertThat(exposee).isNotNull();
        assertThat(exposee.getId()).isNull();
        assertThat(exposee.getKey()).isEqualTo(request.getKey());
        assertThat(exposee.getOnset()).isEqualTo(request.getOnset());
    }

    @Test
    void shouldGetExposed() throws Exception {
        // given
        Exposee exposee = getExposee();
        String date = "2020-12-30";
        when(dataService.getMaxExposedIdForDay(any())).thenReturn(10);
        when(dataService.getSortedExposedForDay(any()))
                .thenReturn(Collections.singletonList(exposee));

        // when
        mockMvc.perform(get("/v1/exposed/" + date))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ExposedOverview(Collections.singletonList(exposee))))
                );
    }

    @Test
    void shouldReturnNotModifiedWithSameEtag() throws Exception {

        // given
        Exposee exposee = getExposee();
        String date = "2020-12-30";
        when(dataService.getMaxExposedIdForDay(any())).thenReturn(10);
        when(dataService.getSortedExposedForDay(any()))
                .thenReturn(Collections.singletonList(exposee));

        ExposeeRequest creationRequest = getExposeeRequest();

        // Create an exposee
        mockMvc.perform(post("/v1/exposed")
                .content(objectMapper.writeValueAsString(creationRequest))
                .contentType("application/json")
                .header("User-Agent", "user-agent")
        ).andExpect(status().isOk())
                .andExpect(content().string(""));

        // when (1)
        // request the list of exposees for the day without ETag, get an ETag
        MvcResult getWithoutETag = mockMvc.perform(
                get("/v1/exposed/" + date))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ExposedOverview(Collections.singletonList(exposee)))))
                .andExpect(header().exists(HttpHeaders.ETAG)).andReturn();

        String receivedETagValue = getWithoutETag.getResponse().getHeader(HttpHeaders.ETAG);
        assertThat(receivedETagValue).isNotEmpty();

        // and when (2)
        // request the expositions for the day with the received ETag, get a http 304 unmodified response
        mockMvc.perform(
                get("/v1/exposed/" + date)
                        .header("If-None-Match", receivedETagValue))
                .andExpect(status().isNotModified())
                .andExpect(jsonPath("$").doesNotExist())
                .andExpect(header().string(HttpHeaders.ETAG, receivedETagValue));
    }


    @Test
    void shouldReturnOKWithDifferentEtag() throws Exception {

        // given
        Exposee exposee = getExposee();
        String date = "2020-12-30";
        when(dataService.getMaxExposedIdForDay(any())).thenReturn(10);
        when(dataService.getSortedExposedForDay(any()))
                .thenReturn(Collections.singletonList(exposee));

        ExposeeRequest creationRequest = getExposeeRequest();

        // Create an exposee
        mockMvc.perform(post("/v1/exposed")
                .content(objectMapper.writeValueAsString(creationRequest))
                .contentType("application/json")
                .header("User-Agent", "user-agent")
        ).andExpect(status().isOk())
                .andExpect(content().string(""));

        // when (1)
        // request the list of exposees for the day without ETag, get an ETag
        MvcResult resultOfGetWithoutETag = mockMvc.perform(
                get("/v1/exposed/" + date))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ExposedOverview(Collections.singletonList(exposee)))))
                .andExpect(header().exists(HttpHeaders.ETAG)).andReturn();

        String receivedETagValue = resultOfGetWithoutETag.getResponse().getHeader(HttpHeaders.ETAG);
        assertThat(receivedETagValue).isNotEmpty();

        final String anotherETag = "\"--I'm-a-non-existing-ETag--\"";

        // and when (2)
        // request the expositions for the day with the received ETag, get a http 304 unmodified response
        MvcResult resultOfGetWithNonExistingETag = mockMvc.perform(
                get("/v1/exposed/" + date)
                        .header("If-None-Match", anotherETag))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, receivedETagValue))
                .andReturn();

        String receivedETagValue2 = resultOfGetWithNonExistingETag.getResponse().getHeader(HttpHeaders.ETAG);
        assertThat(receivedETagValue2).isNotEmpty();
        assertThat(anotherETag).isNotEqualTo(receivedETagValue2);

    }

    private Exposee getExposee() {
        Exposee exposee = new Exposee();
        exposee.setId(1);
        exposee.setKey("key");
        exposee.setOnset("onset");
        return exposee;
    }

    private ExposeeRequest getExposeeRequest() {
        ExposeeRequest creationRequest = new ExposeeRequest();
        creationRequest.setKey("key");
        creationRequest.setOnset("onset");
        creationRequest.setAuthData(new ExposeeAuthData());
        return creationRequest;
    }


}