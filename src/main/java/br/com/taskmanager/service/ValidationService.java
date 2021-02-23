package br.com.taskmanager.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ValidationService {

    public Boolean validateCPF(String cpf){
        Pattern p = Pattern.compile("[0-9]{3}\\.?[0-9]{3}\\.?[0-9]{3}\\-?[0-9]{2}");
        Matcher m = p.matcher(cpf);
        if(m.matches()){
            return true;
        }else{
            return false;
        }
    }

    public Boolean validateEmail(String email){
        Pattern p = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        Matcher m = p.matcher(email);
        if(m.matches()){
            return true;
        }else{
            return false;
        }
    }

    public Boolean validateDate(String date){
        Pattern p = Pattern.compile("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$");
        Matcher m = p.matcher(date);
        if(m.matches()){
            return true;
        }else{
            return false;
        }
    }

}
