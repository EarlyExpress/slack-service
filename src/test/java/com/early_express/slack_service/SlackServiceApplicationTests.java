package com.early_express.slack_service;

import com.early_express.slack_service.slack.domain.MessageSend;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class SlackServiceApplicationTests {

	@Test
	void contextLoads() {
	}

    @Autowired
    MessageSend messageSend;

    @Test
    void messageSendTest() {
        messageSend.send(List.of("U09V1GT3BH8"),"테스트 메세지, 잘 전송이 되나요?");
    }

}
