package com.ecomerce.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecomerce.model.Producto;

 

@Repository
public interface IProductoRepository extends JpaRepository<Producto, Integer> {

}
