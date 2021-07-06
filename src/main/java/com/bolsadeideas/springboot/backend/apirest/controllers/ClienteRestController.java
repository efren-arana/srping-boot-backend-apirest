package com.bolsadeideas.springboot.backend.apirest.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.models.service.IClienteService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;
	
	@GetMapping("/clientes")
	public ResponseEntity<?> index(){
		Map<String,Object> response = new HashMap<String, Object>();
		try {
			response.put("message", "Consulta realizada de manera exitosa");
			response.put("clientes", clienteService.findAll());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		} catch (DataAccessException e) {
			response.put("message", "Ocurrio un error al consultar la base de datos.");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Cliente cliente = null;
		Map<String,Object> response = new HashMap<String, Object>();
		
		try {
			cliente = clienteService.findById(id);
			
		} catch (DataAccessException e) {
			response.put("message", "Ocurrio un error al consultar la base de datos.");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			response.put("message", "Ocurrio un error interno en el servidor.");
			response.put("error", e.getMessage());
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		

		if (cliente == null) {
			response.put("message", "El cliente con el ID:".concat(id.toString().concat(" No existe en la base de datos.")));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Cliente>(cliente,HttpStatus.OK);
	}
	
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente,BindingResult result) {
		Cliente clienteNew =  null;
		Map<String,Object> response = new HashMap<String, Object>();
		
		if(result.hasErrors()) {
			
			List<String> listErrors = result.getFieldErrors()
					.stream()
					.map(fieldError -> "El campo '"+fieldError.getField()+"':"+fieldError.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", listErrors);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		
		try {
			clienteNew =  clienteService.save(cliente);
		} catch (DataAccessException e) {
			response.put("message", "Ocurrio un error al persistir la data en la base de datos.");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("message", "El cliente se ha creado con exito");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
 
	}	
	
	
	@PutMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@Valid @PathVariable(name = "id") Long id,BindingResult result,
			@RequestBody Cliente cliente) {
		Cliente clienteActual = null;
		Map<String,Object> response = new HashMap<String, Object>();
		
		
		if(result.hasErrors()) {
			
			List<String> listErrors = result.getFieldErrors()
					.stream()
					.map(fieldError -> "El campo '"+fieldError.getField()+"':"+fieldError.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", listErrors);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		try {
			
			clienteActual = clienteService.findById(id);
			
			if (clienteActual == null) {
				response.put("message", "El cliente con el ID:".concat(id.toString().concat(" No existe en la base de datos.")));
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
			}
			
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setNombre(cliente.getNombre());
			
			response.put("message", "Cliente actualizado de manera correcta en la base de datos!");
			response.put("cliente_updated", clienteService.save(clienteActual));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
		} catch (DataAccessException e) {
			response.put("message", "Ocurrio un error al actualizar el cliente en la base de datos.");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@DeleteMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
		Map<String,Object> response = new HashMap<String, Object>();
		try {
			clienteService.delete(id);
			response.put("message", "Cliente con el ID "+id+" ha sido eliminado de manera existosa!");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK); 
		} catch (DataAccessException e) {
			response.put("message", "Ocurrio un error al eliminar el cliente en la base de datos.");
			response.put("error", e.getMessage().concat(":").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	
	
	
	
}
