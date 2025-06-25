package com.ecomerce.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecomerce.model.DetalleOrden;
import com.ecomerce.model.Orden;
import com.ecomerce.model.Producto;
import com.ecomerce.model.Usuario;
import com.ecomerce.service.IDetalleOrdenService;
import com.ecomerce.service.IOrdenService;
import com.ecomerce.service.IUsuarioService;
import com.ecomerce.service.ProductoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class HomeController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordenService; 
	
	@Autowired
	private IDetalleOrdenService detalleOrdenService;
	
	//esta variable almacena los detalles de la orden
	List<DetalleOrden> detalles = new ArrayList<>();
	
	//datos de la orden
	Orden orden = new Orden();
	
	@GetMapping("")
	public String home(Model model, HttpSession session) {
		LOGGER.info("Sesio del usuario:  {}",session.getAttribute("idusuario"));
		
		model.addAttribute("productos", productoService.findAll());
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		return "usuario/home";
	}
	
	@GetMapping("productohome/{id}")
	public String productoHome(@PathVariable Integer id, Model model) {		
		LOGGER.info("Id producto enviado como parametro {}",id);
		Producto producto = new Producto();
		Optional<Producto> productoOptional = productoService.get(id);
		producto = productoOptional.get();

		model.addAttribute("producto", producto);
		return "usuario/productohome";
	}	
	
	@PostMapping("/cart")
	public String addCart(@RequestParam Integer id, @RequestParam Integer cantidad,Model model) {
		DetalleOrden detalleOrden = new DetalleOrden();
		Producto producto = new Producto();
		double sumaTotal = 0;
		Optional<Producto> optionalProducto = productoService.get(id);
		LOGGER.info("!!!!!!!!!!!!!!!!!!!!!!Producto añadido {}",optionalProducto.get());
		LOGGER.info("!!!!!!!!!!!!!!!!!!!!!!Cantidad!!! {}",cantidad);
		producto = optionalProducto.get();
		
		detalleOrden.setCantidad(cantidad);
		detalleOrden.setPrecio(producto.getPrecio());
		detalleOrden.setNombre(producto.getNombre());
		detalleOrden.setTotal(producto.getPrecio()*cantidad);
		detalleOrden.setProducto(producto);
		
		//validar que el producto se añada solo una vez
		
		Integer idProducto = producto.getId();
		boolean ingresado = detalles.stream().anyMatch(p -> p.getProducto().getId()==idProducto);
		
		if(!ingresado) {
		detalles.add(detalleOrden);
		}
		
		sumaTotal=detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);		
		return "usuario/carrito";
	}
	@GetMapping("/delete/cart/{id}")
	public String deleteProduct(@PathVariable Integer id, Model model) {
		//lista actuaalizada de productos
		double sumaTotal = 0;
		List<DetalleOrden> ordenesNuevas = new ArrayList<>();
		for(DetalleOrden detalleorden: detalles) {
			if(detalleorden.getProducto().getId()!=id) {
				ordenesNuevas.add(detalleorden);
			}
		}
		//le pasas a la lista global de productos la lista actualizada
		detalles = ordenesNuevas;
		
		sumaTotal=detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);	
		return "usuario/carrito";
	}
	
	@GetMapping("/getCart")
	public String getCart(Model model, HttpSession session) {
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);	
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		return "usuario/carrito";
	}
	
	@GetMapping("/order")
	public String order(Model model, HttpSession session) {
		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);	
		model.addAttribute("usuario", usuario);	
		return "usuario/resumenorden";
	}
	
	@GetMapping("/saveOrder")
	public String saveOrder(HttpSession session) {
		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		Date fechaCreacion = new Date();
		orden.setFechaCreacion(fechaCreacion);
		orden.setNumero(ordenService.generarNumeroOrden());
		orden.setUsuario(usuario);
		
		ordenService.save(orden);
		
		//model.addAttribute("usuario", usuario);
		//guardar detalles
		
		for(DetalleOrden dt:detalles) {
			dt.setOrden(orden);
			detalleOrdenService.save(dt);
		}
		
		//limpiar los valores de la lista para que el usuario siga comprando
		orden = new Orden();
		detalles.clear();
		
		return "redirect:/";
	}
	
	@PostMapping("/search")
	public String searchProduct(@RequestParam String nombre,Model model) {
		LOGGER.info("Nombre del Producto: {}", nombre);
		List<Producto> productos = productoService.findAll().stream().filter(p ->p.getNombre().contains(nombre)).collect(Collectors.toList());
		model.addAttribute("productos", productos);
		
	return "usuario/home";	
	}

}
