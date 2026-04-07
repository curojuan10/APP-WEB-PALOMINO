package com.palomino;

import com.palomino.entity.Rol;
import com.palomino.entity.Usuario;
import com.palomino.repository.RolRepository;
import com.palomino.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class PalominoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PalominoBackendApplication.class, args);
	}

	/**
	 * CommandLineRunner para guardar un usuario de prueba en la BD
	 * Se ejecuta automáticamente al iniciar la aplicación
	 */
	@Bean
	public CommandLineRunner testDatabaseConnection(RolRepository rolRepository, 
													UsuarioRepository usuarioRepository) {
		return args -> {
			try {
				System.out.println("\n========================================");
				System.out.println("🔧 PROBANDO CONEXIÓN A BASE DE DATOS");
				System.out.println("========================================\n");

				// 1. Verificar si el rol CLIENTE existe
				Rol rolCliente = rolRepository.findByNombre("CLIENTE");
				if (rolCliente == null) {
					System.out.println("⚠️  Rol CLIENTE no encontrado. Creando...");
					rolCliente = new Rol();
					rolCliente.setNombre("CLIENTE");
					rolCliente = rolRepository.save(rolCliente);
					System.out.println("✅ Rol CLIENTE creado exitosamente (ID: " + rolCliente.getId() + ")");
				} else {
					System.out.println("✅ Rol CLIENTE encontrado (ID: " + rolCliente.getId() + ")");
				}

				// 2. Verificar si ya existe un usuario de prueba
				String emailPrueba = "prueba@palomino.com";
				if (usuarioRepository.existsByEmail(emailPrueba)) {
					System.out.println("ℹ️  Usuario de prueba ya existe. Saltando creación...");
				} else {
					System.out.println("\n📝 Creando usuario de prueba...");
					
					Usuario usuarioPrueba = new Usuario();
					usuarioPrueba.setNombre("Usuario Prueba");
					usuarioPrueba.setEmail(emailPrueba);
					// Encriptar password con BCrypt (simple para prueba)
					BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
					usuarioPrueba.setPassword(encoder.encode("password123"));
					usuarioPrueba.setRol(rolCliente);
					
					usuarioPrueba = usuarioRepository.save(usuarioPrueba);
					
					System.out.println("✅ Usuario de prueba creado exitosamente");
					System.out.println("   ID: " + usuarioPrueba.getId());
					System.out.println("   Nombre: " + usuarioPrueba.getNombre());
					System.out.println("   Email: " + usuarioPrueba.getEmail());
					System.out.println("   Rol: " + usuarioPrueba.getRol().getNombre());
					System.out.println("   Fecha Registro: " + usuarioPrueba.getFechaRegistro());
				}

				// 3. Mostrar total de usuarios
				long totalUsuarios = usuarioRepository.count();
				System.out.println("\n📊 Total de usuarios en BD: " + totalUsuarios);

				System.out.println("\n✅ CONEXIÓN A BD EXITOSA");
				System.out.println("========================================\n");

			} catch (Exception e) {
				System.out.println("\n❌ ERROR AL CONECTAR CON LA BD");
				System.out.println("Error: " + e.getMessage());
				System.out.println("========================================\n");
				e.printStackTrace();
			}
		};
	}
}
