package com.tpagiles.app_licencia.service;

import com.tpagiles.app_licencia.dto.UsuarioRecord;
import com.tpagiles.app_licencia.dto.UsuarioResponseRecord;
import com.tpagiles.app_licencia.dto.UsuarioUpdateRecord;
import com.tpagiles.app_licencia.model.Usuario;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IUsuarioService {
    UsuarioResponseRecord crearUsuario(UsuarioRecord usuario);
    List<UsuarioResponseRecord> listarTodos();
    UsuarioResponseRecord actualizarUsuario(Long id, UsuarioUpdateRecord usuario);
    void eliminarUsuario(Long id);
    UsuarioResponseRecord obtenerUsuarioPorId(Long id);
    UsuarioResponseRecord activarUsuario(Long id);
}