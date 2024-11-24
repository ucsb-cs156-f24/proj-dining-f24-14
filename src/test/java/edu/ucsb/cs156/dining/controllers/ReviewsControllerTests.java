package edu.ucsb.cs156.dining.controllers;

import edu.ucsb.cs156.dining.repositories.UserRepository;
import edu.ucsb.cs156.dining.testconfig.TestConfig;
import edu.ucsb.cs156.dining.controllers.ReviewsController;
import edu.ucsb.cs156.dining.ControllerTestCase;
import edu.ucsb.cs156.dining.entities.Reviews;
import edu.ucsb.cs156.dining.repositories.ReviewsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ReviewsController.class)
@Import(TestConfig.class)
public class ReviewsControllerTests extends ControllerTestCase {

        @MockBean
        ReviewsRepository reviewsRepository;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/reviews/admin/all
        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/reviews/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @Test
        public void user_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/reviews/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void logged_in_admin_can_get_all() throws Exception {
                mockMvc.perform(get("/api/reviews/all"))
                                .andExpect(status().is(200)); // logged
        }

        @WithMockUser(roles = { "ADMIN" })
        @Test
        public void logged_in_admin_can_get_all_reviews() throws Exception {

                // arrange

                Reviews review1 = Reviews.builder()
                                .student_id(1)
                                .item_id(2)
                                .date_served("today")
                                /*
                                .status("working")
                                
                                .userid(1L)
                                .moderator_comments("test")
                                */
                                .created_date("today")
                                .last_edited_date("rn")
                                .build();

                Reviews review2 = Reviews.builder()
                                .student_id(3)
                                .item_id(1)
                                .date_served("today")
                                .status("working")
                                //.userid(1L)
                                .moderator_comments("test")
                                .created_date("today")
                                .last_edited_date("not rn")
                                .build();

                ArrayList<Reviews> expectedReviews = new ArrayList<>();
                expectedReviews.addAll(Arrays.asList(review1, review2));

                when(reviewsRepository.findAll()).thenReturn(expectedReviews);

                // act
                MvcResult response = mockMvc.perform(get("/api/reviews/all"))
                                .andExpect(status().is(200)).andReturn();

                // assert

                verify(reviewsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedReviews);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/reviews/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_empty_review() throws Exception {
                // arrange

                Reviews review = Reviews.builder()
                                .student_id(1)
                                .item_id(2)
                                .date_served("today")
                                .status("Awaiting Moderation")
                                .userId(1L)
                                .created_date("today")
                                .last_edited_date("rn")
                                .build();

                when(reviewsRepository.save(eq(review))).thenReturn(review);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/reviews/post?student_id=1&item_id=2&date_served=today&created_date=today&last_edited_date=rn")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(reviewsRepository, times(1)).save(review);
                String expectedJson = mapper.writeValueAsString(review);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_nonempty_review() throws Exception {
                // arrange

                Reviews review = Reviews.builder()
                                .student_id(1)
                                .item_id(2)
                                .date_served("today")
                                .status("working")
                                .userId(1L)
                                .moderator_comments("test")
                                .created_date("today")
                                .last_edited_date("rn")
                                .build();

                when(reviewsRepository.save(eq(review))).thenReturn(review);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/reviews/post?student_id=1&item_id=2&date_served=today&status=working&moderator_comments=test&created_date=today&last_edited_date=rn")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(reviewsRepository, times(1)).save(review);
                String expectedJson = mapper.writeValueAsString(review);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_gets_empty_response() throws Exception {
                mockMvc.perform(get("/api/reviews"))
                                .andExpect(status().is(200));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_gets_a_nonempty_response() throws Exception {
                        // arrange
                Long UserId = currentUserService.getCurrentUser().getUser().getId();
                Reviews review1 = Reviews.builder()
                        .student_id(1)
                        .item_id(2)
                        .date_served("today")
                        .status("working")
                        .userId(1L)
                        .moderator_comments("test")
                        .created_date("today")
                        .last_edited_date("rn")
                        .build();

                Reviews review2 = Reviews.builder()
                        .student_id(3)
                        .item_id(1)
                        .date_served("today")
                        .status("working")
                        .userId(1L)
                        .moderator_comments("test")
                        .created_date("today")
                        .last_edited_date("not rn")
                        .build();

                ArrayList<Reviews> expectedReviews = new ArrayList<>();
                expectedReviews.addAll(Arrays.asList(review1, review2));

                when(reviewsRepository.findByUserId(eq(UserId))).thenReturn(expectedReviews);

                // act
                MvcResult response = mockMvc.perform(get("/api/reviews"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(reviewsRepository, times(1)).findByUserId(eq(UserId));
                String expectedJson = mapper.writeValueAsString(expectedReviews);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);

        }

}
        

