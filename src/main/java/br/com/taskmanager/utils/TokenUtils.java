package br.com.taskmanager.utils;

import br.com.taskmanager.domain.UserEntity;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Random;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@UtilityClass
@FieldNameConstants
public class TokenUtils {

    public static String generateToken(){
        return randomUUID().toString();
    }

    public static String generateEmailToken(UserEntity user){
        return DigestUtils.md2Hex(randomUUID().toString()
                + DigestUtils.sha256Hex(user.getEmail() + user.getName())
                + DigestUtils.sha256(user.getName() + user.getEmail()));
    }



}
