package com.si.servidor.service;


import com.si.servidor.ServidorApplication;
import com.si.servidor.entity.Compromisso;
import com.si.servidor.entity.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServidorService {

	public List<Usuario> listarUsuarios() {
		return ServidorApplication.usuarios;
	}

	public Usuario cadastrarUsuario(String nome_novo) {

		Usuario novo_us = new Usuario(nome_novo);

		for(Usuario u : ServidorApplication.usuarios) {
			if(u.getNome().equals(novo_us.getNome())) {
				String mensagem = "Usuario " + novo_us.getNome() + " ja cadastrado, abortando!";
				System.out.println("Servidor: " + mensagem);
				return null; //Usuario ja cadastrado
			}
		}

		ServidorApplication.usuarios.add(novo_us);

		System.out.println("Servidor: Usuario " + novo_us.getNome() + " cadastrado!");

		return novo_us;
	}
	
	public void cadastrarCompromisso(Compromisso novo_compromisso) {
		Usuario dono = getUsuario(novo_compromisso.getDono());

		for(Compromisso c : ServidorApplication.compromissos) {
			if(c.getNomeCompromisso().equals(novo_compromisso.getNomeCompromisso())) {
				String mensagem = "ERRO! Compromisso " + novo_compromisso.getNomeCompromisso() + " ja cadastrado!";
				System.out.println("Servidor: " + mensagem);
				dono.notificar(mensagem);
				return;
			}
		}

		novo_compromisso.setTimer();

		ServidorApplication.compromissos.add(novo_compromisso);

		try {
			if(novo_compromisso.getConvidados() != null
					&& !(novo_compromisso.getConvidados().isEmpty()) ) { // Se possui convidados, manda notificacao para todos
				for(String convi : novo_compromisso.getConvidados()) {

					Usuario convidado = getUsuario(convi);

					if(convidado == null) continue;

					String mensagem = "Voce foi convidado para o compromisso "
							+ novo_compromisso.getNomeCompromisso() + " por " + novo_compromisso.getDono() +
							", deseja aceitar?";

					convidado.notificar(mensagem);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String mensagem = "Servidor: Compromisso " + novo_compromisso.getNomeCompromisso() + " cadastrado!";
		dono.notificar(mensagem);
		System.out.println(mensagem);
	}
	
	public void excluirCompromisso(String nome) {

		Compromisso compromisso = ServidorApplication.compromissos.stream()
				  .filter(com -> nome.equals(com.getNomeCompromisso()))
				  .findAny()
				  .orElse(null);

		if(compromisso == null) {
			String mensagem = "Compromisso " + nome + " nao encontrado!";
			System.out.println("Servidor: " + mensagem);
			return;
		}

		Usuario dono = getUsuario(compromisso.getDono());

		compromisso.excluiCompromisso();

		ServidorApplication.compromissos.remove(compromisso);

		String mensagem = "Compromisso " + compromisso.getNomeCompromisso() + " excluido!";

		dono.notificar(mensagem);

		System.out.println(mensagem);
		
	}
	
	public void removerConvidado(String nomeCompromisso, String convidado) {
		
		for(Compromisso com : ServidorApplication.compromissos) {
			if(nomeCompromisso.equals(com.getNomeCompromisso())) {
				com.getConvidados().remove(convidado);
				System.out.println("Servidor: " +convidado+ " removido");
				return;
			}
		}

		String mensagem = "Compromisso " + nomeCompromisso + " nao encontrado!";
		System.out.println("Servidor: " + mensagem);
		
	}
	
	public List<Compromisso> consultarCompromissos(LocalDateTime data, Usuario usuario)  {
		
		List<Compromisso> compromissos = new ArrayList<Compromisso>();

		for(Compromisso c : ServidorApplication.compromissos) {
			if(estaNoCompromisso(c, usuario) && compararDatas(data, c.getDataHorario())) {
				compromissos.add(c);
			}
		}

		try {

			System.out.println(compromissos);

			StringBuilder mensagem = new StringBuilder("Lista de compromissos do dia: ");

			if(compromissos.isEmpty()) {
				usuario.notificar("Nao ha compromissos para o dia ainda");
				return null;
			}

			for(Compromisso c : compromissos) {
				mensagem.append(
						c.getNomeCompromisso())
						.append("(")
						.append(c.getDataHorario().getHour())
						.append(":").append(c.getDataHorario().getMinute())
						.append("); ");
			}

			usuario.notificar(mensagem.toString());
			return compromissos;

		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public SseEmitter cadastrarNotificacao(String nome) {
		final SseEmitter emitter = new SseEmitter(-1L);
		Usuario usuario = getUsuario(nome);
		usuario.setSseEmitter(emitter);
		return emitter;
	}
	
	private Usuario getUsuario(String nomeConvi) {
		return ServidorApplication.usuarios.stream()
				  .filter(u -> nomeConvi.equals(u.getNome()))
				  .findAny()
				  .orElse(null);
	}
	
	private boolean compararDatas(LocalDateTime data1, LocalDateTime data2) {
		
		LocalDate firstDate = LocalDate.of(data1.getYear(), data1.getMonth(), data1.getDayOfMonth());
		LocalDate secondDate = LocalDate.of(data2.getYear(), data2.getMonth(), data2.getDayOfMonth());
		
		return secondDate.isEqual(firstDate);
		
	}
	
	private boolean estaNoCompromisso(Compromisso com, Usuario usuario) {
		
		if(com.getConvidados() != null) {
			return (com.getDono().equals(usuario.getNome()) || com.getConvidados().contains(usuario.getNome()));
		}
		
		return com.getDono().equals(usuario.getNome());
	}

}
