package br.com.taskmanager.service;

import br.com.taskmanager.repository.LogRepository;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    //TODO comecar analise de logs

}
