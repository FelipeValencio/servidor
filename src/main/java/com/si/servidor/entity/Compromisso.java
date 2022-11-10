package com.si.servidor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.si.servidor.ServidorApplication;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
@Data
@NoArgsConstructor
public class Compromisso {
	
	private String nomeCompromisso;
	
	private String dono;
	
	private LocalDateTime dataHorario;
	
	private List<String> convidados;
	@JsonIgnore
	private ScheduledExecutorService ses;
	
	private static final Integer TEMPO_AVISO = 5;

	public String getDono() {
		return dono;
	}

	public void setDono(String dono) {
		this.dono = dono;
	}

	public LocalDateTime getDataHorario() {
		return dataHorario;
	}

	public void setDataHorario(LocalDateTime dataHorario) {
		this.dataHorario = dataHorario;
	}

	public List<String> getConvidados() {
		return convidados;
	}

	public void setConvidados(List<String> convidados) {
		this.convidados = convidados;
	}

	public String getNomeCompromisso() {
		return nomeCompromisso;
	}

	public void setNomeCompromisso(String nomeCompromisso) {
		this.nomeCompromisso = nomeCompromisso;
	}

	public ScheduledExecutorService getSes() {
		return ses;
	}

	public void setSes(ScheduledExecutorService ses) {
		this.ses = ses;
	}

	public void setTimer() {
		final LocalDateTime now = LocalDateTime.now();
		
		if(this.dataHorario.isBefore(now)) {
			return;
		}
		
		final LocalDateTime tempoParaNotificar = this.dataHorario.minusMinutes(TEMPO_AVISO);
		
		this.ses = Executors.newScheduledThreadPool(1);
		this.ses.schedule(this::enviaNotificacao,
			 now.until(tempoParaNotificar, ChronoUnit.MILLIS),
			 TimeUnit.MILLISECONDS);
	}
	
	private void enviaNotificacao() {
		String notifica = "Compromisso " + this.nomeCompromisso + " comecara em " + TEMPO_AVISO + " minutos!";
		Usuario dono = getConvidado(this.dono);
		dono.notificar(notifica);
		if(convidados != null) {
			for(String convi : convidados) {
				Usuario convidado = getConvidado(convi);
				convidado.notificar(notifica);
			}
		}
	    System.out.println(notifica);
	}
	
	public void excluiCompromisso() {
		this.ses.shutdownNow();
	}
	
	private Usuario getConvidado(String nomeConvi) {
		return ServidorApplication.usuarios.stream()
				  .filter(u -> nomeConvi.equals(u.getNome()))
				  .findAny()
				  .orElse(null);
	}
 	
}
