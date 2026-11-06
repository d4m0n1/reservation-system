package learning.d4m0n1.reservation_system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ReservationSystemApplicationTests {

	@Test
	void contextLoads() {
	}

}
