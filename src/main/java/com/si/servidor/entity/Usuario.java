package com.si.servidor.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Data
@NoArgsConstructor
public class Usuario {

	private String nome;
	private SseEmitter sseEmitter;

	private int lastEventID = 0;
	
	public Usuario(String nome) {
		this.nome = nome;
		this.sseEmitter = null;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public SseEmitter getSseEmitter() {
		return sseEmitter;
	}

	public void setSseEmitter(SseEmitter sseEmitter) {
		this.sseEmitter = sseEmitter;
	}

	public void notificar(String mensagem) {
		SseEmitter.SseEventBuilder event = SseEmitter.event()
				//.data(mensagem)
				.id(String.valueOf(lastEventID))
				.name(mensagem);

		try {
			this.sseEmitter.send(event);
			lastEventID++;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
