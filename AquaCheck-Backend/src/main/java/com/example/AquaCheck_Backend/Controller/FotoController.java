package com.example.AquaCheck_Backend.Controller;

import org.springframework.http.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import java.io.IOException;

@RestController
@RequestMapping("/api/fotos")
public class FotoController {
    private final Path carpeta = Path.of(System.getenv().getOrDefault("AQUA_FOTOS_DIR", "uploads/fotos")).toAbsolutePath().normalize();

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> subir(@RequestParam("archivo") MultipartFile archivo) throws Exception {
        if (archivo.isEmpty() || archivo.getSize() > 10 * 1024 * 1024)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Archivo vacio o demasiado grande");
        byte[] datos = archivo.getBytes();
        String extension;
        if (datos.length >= 3 && (datos[0] & 255) == 255 && (datos[1] & 255) == 216 && (datos[2] & 255) == 255) extension = "jpg";
        else if (datos.length >= 8 && HexFormat.of().formatHex(datos, 0, 8).equals("89504e470d0a1a0a")) extension = "png";
        else throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Solo JPEG y PNG");
        String nombre = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(datos)) + "." + extension;
        Files.createDirectories(carpeta);
        Path temporal = Files.createTempFile(carpeta, "foto-", ".tmp");
        try {
            Files.write(temporal, datos);
            Files.move(temporal, carpeta.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
        } finally { Files.deleteIfExists(temporal); }
        return Map.of("url", "/api/fotos/" + nombre);
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<FileSystemResource> descargar(@PathVariable("nombre") String nombre) {
        if (!nombre.matches("[a-f0-9]{64}\\.(jpg|png)")) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Path ruta = carpeta.resolve(nombre);
        if (!Files.isRegularFile(ruta)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok().contentType(nombre.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG)
            .header("X-Content-Type-Options", "nosniff").body(new FileSystemResource(ruta));
    }
}
