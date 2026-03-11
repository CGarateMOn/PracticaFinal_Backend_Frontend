package edu.comillas.icai.gitt.pat.spring.mvc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PistaService {
    @Autowired
    private PistaService pistaService;
    @Autowired
    private ReservaService reservaService;
    @Autowired
    private UsuarioService usuarioService;
}
