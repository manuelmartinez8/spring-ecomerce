package com.ecomerce.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecomerce.model.Usuario;

import jakarta.servlet.http.HttpSession;
/*
 * Esta clase permite buscar el usuario y pasarlos a otra clase que 
 * los valida
 * */
@Service
public class UserDetailServiceImpl  implements UserDetailsService{


	@Autowired
	private IUsuarioService usuarioService;
	
	//@Autowired
	//private BCryptPasswordEncoder bCrypt;
	//este metodo encripta y desencrypta la clave de usuario, 
	//tambien permite encryptar cla clave de usuario cuando el usuario se registra
	
	@Autowired
	HttpSession session;
	
	private Logger log = LoggerFactory.getLogger(UserDetailServiceImpl.class);

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Este es el username");
		Optional<Usuario> optionalUser=usuarioService.findByEmail(username);
		if (optionalUser.isPresent()) {
			Usuario usuario= optionalUser.get();
			log.info("Esto es el id del usuario: {}", optionalUser.get().getId());
			 // Guarda el ID en la sesión (opcional, pero válido si lo necesitas)
            session.setAttribute("idusuario", usuario.getId());
			
            return User.builder()
                    .username(usuario.getEmail()) // Email como identificador
                    .password(usuario.getPassword()) // La contraseña ya debe estar encriptada
                    .roles(usuario.getTipo()) // "ADMIN", "USER", etc.
                    .build();
		}else {
			throw new UsernameNotFoundException("Usuario no encontrado");			
		}
	}
}
