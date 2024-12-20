package edu.ucsb.cs156.dining.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.springframework.boot.test.mock.mockito.MockBean;
import edu.ucsb.cs156.dining.services.wiremock.WiremockService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import java.time.LocalDateTime;

@RestClientTest(DiningCommonsService.class)
@AutoConfigureDataJpa
@ContextConfiguration(classes = {})
class DiningCommonsServiceTests {

  @MockBean
  private WiremockService wiremockService;

  @Autowired private MockRestServiceServer mockRestServiceServer;

  @Autowired private DiningCommonsService diningCommonsService;

  @Value("${app.ucsb.api.consumer_key}")
  private String apiKey;

  @Test
  void get_returns_all_dining_commons() throws Exception {

    String expectedURL = DiningCommonsService.NAMES_ENDPOINT;

    String expectedResult = """
[
  {
    "name": "Carrillo",
    "code": "carrillo"
  },
  {
    "name": "De La Guerra",
    "code": "de-la-guerra"
  },
  {
    "name": "Ortega",
    "code": "ortega"
  },
  {
    "name": "Portola",
    "code": "portola"
  }
]
            """;

    this.mockRestServiceServer
        .expect(requestTo(expectedURL))
        .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
        .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
        .andExpect(header("ucsb-api-version", "1.0"))
        .andExpect(header("ucsb-api-key", apiKey))
        .andRespond(withSuccess(expectedResult, MediaType.APPLICATION_JSON));

    String actualResult = diningCommonsService.getDiningCommonsJSON();
    assertEquals(expectedResult, actualResult);
  }

  @Test
  void get_returns_all_meals_by_date() throws Exception {

    LocalDateTime date = LocalDateTime.of(2021, 5, 1, 0, 0);
    String diningCommonsCode = "ortega";

    String expectedURL = DiningCommonsService.MEALS_BY_DATE_ENDPOINT + date.toString() + "/ortega";

    String expectedResult = "t";

    this.mockRestServiceServer
        .expect(requestTo(expectedURL))
        .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
        .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
        .andExpect(header("ucsb-api-version", "1.0"))
        .andExpect(header("ucsb-api-key", apiKey))
        .andRespond(withSuccess(expectedResult, MediaType.APPLICATION_JSON));

    String actualResult = diningCommonsService.getMealsByDateJSON(date, diningCommonsCode);
    assertEquals(expectedResult, actualResult);
  }

  @Test
  void get_returns_all_menuitems_by_meal_date_diningcommons() throws Exception {

    LocalDateTime date = LocalDateTime.of(2021, 5, 1, 0, 0);
    String diningCommonsCode = "ortega";
    String meal = "lunch";


    String expectedURL = DiningCommonsService.MEALS_BY_DATE_ENDPOINT + date.toString() + "/" + diningCommonsCode + "/" + meal;

    String expectedResult = "test2";

    this.mockRestServiceServer
        .expect(requestTo(expectedURL))
        .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
        .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
        .andExpect(header("ucsb-api-version", "1.0"))
        .andExpect(header("ucsb-api-key", apiKey))
        .andRespond(withSuccess(expectedResult, MediaType.APPLICATION_JSON));

    String actualResult = diningCommonsService.getMenuItemsByMealAndDateJSON(date, diningCommonsCode, meal);
    assertEquals(expectedResult, actualResult);
  }
}