package com.ecomerce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ecomerce.model.Usuario;
import com.ecomerce.service.IUsuarioService;

@SpringBootApplication
public class EcomerceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(EcomerceApplication.class, args);
	}
	@Autowired
	private IUsuarioService usuarioService;


	@Override
	public void run(String... args) throws Exception {
		/*
		Usuario usu= new Usuario("Manuel", "Manuelon","manuelon_8","Paseo emilia Pardo","692526446", "USER", "123");
		Usuario usu2= new Usuario("Adriana", "Adrianita","adrianita","Chile","692526446", "ADMIN", "123");		
		usuarioService.save(usu);
		usuarioService.save(usu2);
		
		
		Empleado empleado = new Empleado();
		empleado.setNombre("Manuel");
		empleado.setApellido("Martinez");
		empleado.setEmailId("manuelon_8");

		repo.save(empleado);

		Empleado empleado2 = new Empleado();
		empleado2.setNombre("Adriana");
		empleado2.setApellido("Martinez");
		empleado2.setEmailId("Adri_8");
		repo.save(empleado2);*/
	}
}
