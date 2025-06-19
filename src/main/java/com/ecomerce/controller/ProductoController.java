package com.ecomerce.controller;

import java.io.IOException;
import java.util.Optional;

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
import org.springframework.web.multipart.MultipartFile;

import com.ecomerce.model.Producto;
import com.ecomerce.model.Usuario;
import com.ecomerce.service.IUsuarioService;
import com.ecomerce.service.ProductoService;
import com.ecomerce.service.UploadFileService;

@Controller
@RequestMapping("/productos")
public class ProductoController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
	
	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private UploadFileService upload;
	
	@GetMapping("")
	public String show(Model model) {		
		model.addAttribute("productos", productoService.findAll());
		return "productos/show";
	}
	
	@GetMapping("create")
	public String create() {
		return "productos/create";
	}
	
	@PostMapping("/save")
	public String save(Producto producto, @RequestParam("img") MultipartFile file ) throws IOException {
		LOGGER.info("ESTE ES EL PRODUCTO{}", producto); 
		Usuario u = new Usuario(1);
		producto.setUsuario(u);		
		//imagen
		if(producto.getId() == null) { //en caso de que sea un nuevo producto
		String nombreImagen = upload.saveImage(file);
		producto.setImagen(nombreImagen);
		}else {

		}
		productoService.save(producto);
		return "redirect:/productos";
	}
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Integer id,Model model) {
		Producto producto = new Producto();
		Optional<Producto> optionalProducto = productoService.get(id);
		producto = optionalProducto.get();
		model.addAttribute("producto", producto);
		LOGGER.info("ESTE ES EL PRODUCTO BUSCADO{}", producto); 
		return "productos/edit";
	}
	
	@PostMapping("update")
	public String update(Producto producto,  @RequestParam("img") MultipartFile file) throws IOException {
		if(file.isEmpty()) {// opcion para cuando editamos el producto pero no cambiamos la imagen
			Producto p = new Producto();
			p= productoService.get(producto.getId()).get();
			producto.setImagen(p.getImagen());
		}else {//cuando tambien se edita la imagen
			Producto p = new Producto();
			p= productoService.get(producto.getId()).get();
			
			if(!p.getImagen().equals("default.jpg"))
			{
				upload.deleteImage(p.getImagen());
			}
			String nombreImagen = upload.saveImage(file);
			producto.setImagen(nombreImagen);				
		}
		productoService.update(producto);
		LOGGER.info("PRODUCTO ACTUALIZADO:{}", producto); 
		return "redirect:/productos";
	}
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {
		Producto p = new Producto();
		p = productoService.get(id).get();
		//elimina la imagen del producto si es diferente  a la por defecto
		if(!p.getImagen().equals("default.jpg"))
		{
			upload.deleteImage(p.getImagen());
		}
		productoService.delete(id);
		LOGGER.info("PRODUCTO ELIMINADO!"); 
		return "redirect:/productos";
	}
	
	

}
