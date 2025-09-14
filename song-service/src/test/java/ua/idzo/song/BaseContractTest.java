package ua.idzo.song;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import ua.idzo.song.core.entity.SongEntity;
import ua.idzo.song.core.service.impl.SongServiceImpl;
import ua.idzo.song.web.controller.SongRestController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMessageVerifier
public abstract class BaseContractTest {

    @Autowired
    private SongRestController songController;

    @MockBean
    private SongServiceImpl songService;

    @BeforeEach
    public void setup() {
        SongEntity songEntity = new SongEntity();
        songEntity.setId(1);
        songEntity.setName("Test Title");
        songEntity.setArtist("Test Artist");
        songEntity.setAlbum("Test Album");
        songEntity.setDuration("03:30");
        songEntity.setYear("2025");
        when(songService.createSong(any())).thenReturn(songEntity);
        RestAssuredMockMvc.standaloneSetup(songController);
    }
}
