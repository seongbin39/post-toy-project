package store.sbin.postservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.sbin.postservice.dto.MailDto;
import store.sbin.postservice.service.mail.MailService;

import java.util.Random;

@Service
@Slf4j
public class AuthService {

    private final MailService mailService;
    private final RedisService redisService;

    public AuthService(MailService mailService, RedisService redisService) {
        this.mailService = mailService;
        this.redisService = redisService;
    }

    /**
     * 인증 메일 전송
     *
     * @param email 사용자 이메일
     */
    public void sendAuthMail(String email) {
        log.info("sendAuthMail email = {}", email);
        Random rand = new Random();
        int randomNum = rand.nextInt((999999 - 100000) + 1) + 100000;
        redisService.setDataExpire(email, String.valueOf(randomNum), 5 * 60);
        MailDto mailDto = MailDto.builder()
                .to(email)
                .subject("비밀번호 찾기")
                .content("비밀번호 찾기 메일입니다. 인증번호는 " + randomNum + " 입니다.")
                .build();
        mailService.sendMail(mailDto);
    }

    /**
     * 인증 메일 검증
     *
     * @param email    사용자 이메일
     * @param authCode 인증 코드
     * @return 인증 여부
     */
    public boolean validateAuthMail(String email, String authCode) {
        String redisAuthCode = redisService.getData(email);
        return authCode.equals(redisAuthCode);
    }
}
