package com.saris.logitrack.repository;

import com.saris.logitrack.model.DetalleMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleMovimientoRepository extends JpaRepository<DetalleMovimiento, Long> {
}