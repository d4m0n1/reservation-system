package learning.d4m0n1.reservation_system.reservations;

import learning.d4m0n1.reservation_system.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ReservationControllerIntegrationTest {

    private static final LocalDate START = LocalDate.now().plusDays(1);
    private static final LocalDate END = LocalDate.now().plusDays(4);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void createReservation_returnsCreatedWithPendingStatus() throws Exception {
        var body = """
                {
                  "userId": 1,
                  "roomId": 5,
                  "startDate": "%s",
                  "endDate": "%s"
                }
                """.formatted(START, END);

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createReservation_whenEndDateBeforeStartDate_returnsBadRequest() throws Exception {
        var body = """
                {
                  "userId": 1,
                  "roomId": 5,
                  "startDate": "%s",
                  "endDate": "%s"
                }
                """.formatted(END, START);

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReservation_whenNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/reservations/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void approveReservation_whenOverlapsApproved_returnsConflict() throws Exception {
        var first = savePending(5L, START, END);
        var second = savePending(5L, START.plusDays(1), END.plusDays(1));

        mockMvc.perform(post("/reservations/{id}/approve", first.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(post("/reservations/{id}/approve", second.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    void searchReservations_returnsPaginationMetadata() throws Exception {
        savePending(1L, START, END);
        savePending(2L, START, END);
        savePending(3L, START, END);

        mockMvc.perform(get("/reservations").param("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    private ReservationEntity savePending(Long roomId, LocalDate startDate, LocalDate endDate) {
        return repository.save(new ReservationEntity(
                null, 1L, roomId, startDate, endDate, ReservationStatus.PENDING
        ));
    }
}
