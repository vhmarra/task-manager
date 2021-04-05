package br.com.taskmanager.utils;

import lombok.experimental.FieldNameConstants;
import lombok.experimental.UtilityClass;

@UtilityClass
@FieldNameConstants
public class Constants {

    public static final Long ROLE_USER = 1L;
    public static final Long ROLE_ADM = 2L;
    public static final Long SUPER_ADM = 3L;

    public static final String WELCOME_SUBJECT_EMAIL = "Seja bem vindo";
    public static final String WELCOME_BODY_EMAIL = "Seja bem vindo user_name ao seus sistema de tasks preferido";

    public static final String CHANGE_PASSWORD_SUBJECT_EMAIL = "Solicitação de troca de senha";
    public static final String CHANGE_PASSWORD_BODY_EMAIL = "Ola user_name insira use este c_pass para fazer a troca de sua senha";

    public static final String CHANGE_PASSWORD_SUCCESSFULLY_SUBJECT_EMAIL = "Senha trocada com sucesso";
    public static final String CHANGE_PASSWORD_SUCCESSFULLY_BODY_EMAIL = "Ola user_name sua senha foi trocada com sucesso";

    public static final String BIRTHDAY_SUBJECT_EMAIL = "Feliz Aniversario!";
    public static final String BIRTHDAY_BODY_EMAIL = "user_name tenha um feliz aniversário cheio de sorrisos e gargalhadas, repleto de paz, amor e muita alegria. Parabéns por mais um ano de vida!";

    public static final String VIA_CEP_URL = "https://viacep.com.br/ws/"+"user_cep"+"/json/";
}
