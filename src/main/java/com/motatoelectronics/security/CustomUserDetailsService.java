package com.motatoelectronics.security;

import com.motatoelectronics.entity.Usuario;
import com.motatoelectronics.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * ============================================================
 * SERVICIO: CustomUserDetailsService
 * ============================================================
 * Le dice a Spring Security cómo cargar un usuario desde
 * la base de datos MySQL.
 *
 * Spring Security usa este servicio cuando:
 *   - Alguien intenta loguearse.
 *   - Se valida un token JWT.
 * ============================================================
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

        // 1) Buscar el usuario por correo
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        // 2) Convertirlo en UserDetails (formato que entiende Spring Security)
        return new User(
                usuario.getCorreo(),
                usuario.getPassword(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + usuario.getRol())
                )
        );
    }
}

