// Base de datos simulada de licencias emitidas (en producción vendría del backend)
export const licenciasEmitidas = [
  {
    numeroLicencia: "LC-2025-12345",
    titular: {
      tipoDocumento: "DNI",
      numeroDocumento: "12345678",
      nombreApellido: "Jose Cammisi",
      fechaNacimiento: "1990-05-15",
      direccion: "Av. Ojo Las Del 14",
      grupoSanguineo: "0",
      factorRh: "+",
      donanteOrganos: "Si",
      edad: 33,
    },
    claseLicencia: "B",
    fechaEmision: "2025-11-15",
    fechaVencimiento: "2028-11-15",
    vigencia: 5,
    costo: 10000,
  },
  {
    numeroLicencia: "LC-2025-67890",
    titular: {
      tipoDocumento: "DNI",
      numeroDocumento: "87654321",
      nombreApellido: "María González",
      fechaNacimiento: "1985-10-20",
      direccion: "Calle Secundaria 456",
      grupoSanguineo: "A",
      factorRh: "-",
      donanteOrganos: "No",
      edad: 38,
    },
    claseLicencia: "A",
    fechaEmision: "2025-10-10",
    fechaVencimiento: "2030-10-10",
    vigencia: 5,
    costo: 8500,
  },
  {
    numeroLicencia: "LC-2025-54321",
    titular: {
      tipoDocumento: "Pasaporte",
      numeroDocumento: "AB123456",
      nombreApellido: "Carlos Rodríguez",
      fechaNacimiento: "2000-03-10",
      direccion: "Pasaje Norte 789",
      grupoSanguineo: "B",
      factorRh: "+",
      donanteOrganos: "Si",
      edad: 23,
    },
    claseLicencia: "B",
    fechaEmision: "2025-09-05",
    fechaVencimiento: "2030-09-05",
    vigencia: 5,
    costo: 9500,
  },
]

// Licencia seleccionada por defecto (se actualizará cuando el usuario seleccione una)
export const licenciaEmitida = licenciasEmitidas[0]
