package com.si.servidor;

import com.si.servidor.entity.Compromisso;
import com.si.servidor.entity.Usuario;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ServidorApplication {

	public static List<Usuario> usuarios;

	public static List<Compromisso> compromissos;

	public static void main(String[] args) {
		usuarios = new ArrayList<>();

		compromissos = new ArrayList<>();

		SpringApplication.run(ServidorApplication.class, args);
	}

}
