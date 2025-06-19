package com.ecomerce.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecomerce.model.Orden;
import com.ecomerce.model.Usuario;
import com.ecomerce.service.IOrdenService;
import com.ecomerce.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/views/")
public class UsuarioController {
	
	
	private final Logger logger= LoggerFactory.getLogger(UsuarioController.class);
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordenService;
	
	@RequestMapping("listUser")
	public String getUser(Model model) {
		//List<User> allUser = new ArrayList<User>();
		//allUser=service.getAllUser();

		model.addAttribute("titulo", "Los Usuarios");
		model.addAttribute("listadeusuarios",  usuarioService.findAll());
		return "/views/listUser";
	}
	
	@GetMapping("nuv")
	public String nuevoUsuario(Model model) {
		Usuario u = new Usuario();		 		
		model.addAttribute("titulo", "Formulario Nuevo Usuario");
		model.addAttribute("usuario", u);
		return "/views/frmnuevousuario";
	}
	
	@PostMapping("/saveuser")
	public String Guardar(@ModelAttribute Usuario eu) {		 
		try {
//		Date d=	java.util.Date.from( Instant.now());
//		eu.setFechaNacimiento(d);			
			usuarioService.save(eu);
			System.out.println("USUARIO GUARDADO CON EXITO!!");			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return "redirect:/views/listUser";		
	} 
	
	@GetMapping("/login")
	public String login() {
		return "usuario/login";
	}
	@PostMapping("/acceder")
	public String acceder(Usuario usuario, HttpSession session) {
		logger.info("Accesos : {}", usuario);
		
		Optional<Usuario> user=usuarioService.findByEmail(usuario.getEmail());
		//logger.info("Usuario de db: {}", user.get());
		
		if (user.isPresent()) {
			session.setAttribute("idusuario", user.get().getId());
			
			if (user.get().getTipo().equals("ADMIN")) {
				return "redirect:/administrador";
			}else {
				return "redirect:/";
			}
		}else {
			logger.info("Usuario no existe");
		}
		
		return "redirect:/";
	}
	
	@GetMapping("/compras")
	public String obtenerCompras(Model model, HttpSession session) {
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		
		Usuario usuario= usuarioService.findById(  Integer.parseInt(session.getAttribute("idusuario").toString()) ).get();
		List<Orden> ordenes= ordenService.findByUsuario(usuario);
		logger.info("ordenes {}", ordenes);
		
		model.addAttribute("ordenes", ordenes);
		
		return "usuario/compras";
	}
	
	@GetMapping("/detalle/{id}")
	public String detalleCompra(@PathVariable Integer id, HttpSession session, Model model) {
		logger.info("Id de la orden: {}", id);
		Optional<Orden> orden=ordenService.findById(id);
		
		model.addAttribute("detalles", orden.get().getDetalle());
		
		
		//session
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		return "usuario/detallecompra";
	}
	
	@GetMapping("/cerrar")
	public String cerrarSesion( HttpSession session ) {
		session.removeAttribute("idusuario");
		return "redirect:/";
	}

}
