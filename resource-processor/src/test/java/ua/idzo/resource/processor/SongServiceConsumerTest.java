package ua.idzo.resource.processor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.ResponseEntity;
import ua.idzo.resource.processor.dto.song.request.CreateSongRequest;
import ua.idzo.resource.processor.dto.song.response.CreateSongResponse;
import ua.idzo.resource.processor.integration.song.SongFeignClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "song.service-name=song"
})
@AutoConfigureMockMvc
@AutoConfigureStubRunner(
        ids = "ua.idzo.song:song:+:stubs:8080",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class SongServiceConsumerTest {

    @Autowired
    private SongFeignClient songFeignClient;

    @Test
    void createSong_shouldReturnId_whenContractIsMet() {
        // --- GIVEN ---
        CreateSongRequest request = new CreateSongRequest(
                1, "Test Title", "Test Artist", "Test Album",
                "03:30", "2025"
        );

        // --- WHEN ---
        ResponseEntity<CreateSongResponse> response = songFeignClient.createSong(request);

        // --- THEN ---
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().id());
    }
}
