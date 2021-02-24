package br.com.taskmanager.utils;

import br.com.taskmanager.domain.UserEntity;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Random;
import java.util.UUID;

@UtilityClass
@FieldNameConstants
public class TokenUtils {

    public static String generateToken(){
        Random r = new Random();
        String token = DigestUtils.sha1Hex(r.ints(97, 122)
                .limit(30L)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString());
        return token;
    }

    public static String generateEmailToken(UserEntity user){
        return DigestUtils.md2Hex(UUID.randomUUID().toString()
                + DigestUtils.sha256Hex(user.getEmail() + user.getName())
                + DigestUtils.sha256(user.getName() + user.getEmail()));
    }



}
