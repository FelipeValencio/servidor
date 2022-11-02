package com.si.servidor.controller;

import com.si.servidor.entity.Compromisso;
import com.si.servidor.entity.Usuario;
import com.si.servidor.service.ServidorService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/calendario")
public class Controller {
    private final ServidorService servidorService;

    public Controller(ServidorService servidorService) {
        this.servidorService = servidorService;
    }

    @PostMapping(path = "/usuario/{nome}")
    public Usuario cadastrarUsuario(@PathVariable("nome") String nome) {
        return servidorService.cadastrarUsuario(nome);
    }

    @GetMapping(path = "/usuario")
    @ResponseBody
    public List<Usuario> listarUsuario() {
        return servidorService.listarUsuarios();
    }

    @PostMapping(path = "/compromisso")
    @ResponseBody
    public void cadastrarCompromisso(@RequestBody Compromisso compromisso) {
        servidorService.cadastrarCompromisso(compromisso);
    }

    @DeleteMapping(path="/compromisso/{nome}")
    public void excluirCompromisso(@PathVariable("nome") String nome) {
        servidorService.excluirCompromisso(nome);
    }

    @DeleteMapping(path="/compromisso/{nomeCompromisso}/convidado/{nome}")
    public void removerConvidado(@PathVariable("nomeCompromisso") String nomeCompromisso, @PathVariable("nome") String nome) {
        servidorService.removerConvidado(nomeCompromisso, nome);
    }

    @GetMapping(path = "/compromisso")
    public List<Compromisso> consultarCompromissos(@RequestParam("data") String data, @RequestBody Usuario usuario) {
        String result = java.net.URLDecoder.decode(data, StandardCharsets.UTF_8);
        return servidorService.consultarCompromissos(LocalDateTime.parse(result), usuario);
    }

    @GetMapping(path = "/notificar/{nome}")
    public SseEmitter cadastrarNotificacao(@PathVariable("nome") String nome) {
        return servidorService.cadastrarNotificacao(nome);
    }
}
