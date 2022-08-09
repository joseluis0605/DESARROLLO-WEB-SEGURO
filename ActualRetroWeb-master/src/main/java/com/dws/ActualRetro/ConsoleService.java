package com.dws.ActualRetro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ConsoleService {
    @Autowired
    ConsoleRepository consoleRepository;
}
