package br.com.taskmanager.exceptions;

public class NotEnoughPermissionsException extends Exception{

    public NotEnoughPermissionsException(String message){
        super(message);
    }

}
