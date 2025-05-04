"use client"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent } from "@/components/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { AlertCircle, CheckCircle2, Search, ArrowLeft } from "lucide-react"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Separator } from "@/components/ui/separator"

// Tipo para el titular
interface Titular {
  tipoDocumento: string
  numeroDocumento: string
  nombreApellido: string
  fechaNacimiento: string
  direccion: string
  grupoSanguineo: string
  factorRh: string
  donanteOrganos: string
  edad: number
}

// Base de datos simulada de titulares - Actualizada para ser consistente con licencia-data.ts
const titularesDB = [
  {
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
  {
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
  {
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
]

interface EmitirLicenciaFormProps {
  role: string
}

export default function EmitirLicenciaForm({ role }: EmitirLicenciaFormProps) {
  const router = useRouter()
  const [tipoDocumento, setTipoDocumento] = useState<string>("")
  const [numeroDocumento, setNumeroDocumento] = useState<string>("")
  const [claseLicencia, setClaseLicencia] = useState<string>("")
  const [titular, setTitular] = useState<Titular | null>(null)
  const [error, setError] = useState<string>("")
  const [success, setSuccess] = useState<boolean>(false)
  const [vigencia, setVigencia] = useState<number>(0)
  const [costo, setCosto] = useState<number>(0)
  const [errorEdad, setErrorEdad] = useState<string>("")

  useEffect(() => {
    if (claseLicencia && titular) {
      calcularVigenciaYCosto()
    }
  }, [claseLicencia, titular])

  const buscarTitular = () => {
    setError("")
    setErrorEdad("")
    setTitular(null)

    if (!tipoDocumento || !numeroDocumento) {
      setError("Debe completar tipo y número de documento")
      return
    }

    // Buscar en la base de datos simulada
    const titularEncontrado = titularesDB.find(
      (t) => t.tipoDocumento === tipoDocumento && t.numeroDocumento === numeroDocumento,
    )

    if (!titularEncontrado) {
      setError("No se encontró ningún titular con ese documento")
      return
    }

    setTitular(titularEncontrado)
  }

  const calcularVigenciaYCosto = () => {
    if (!titular || !claseLicencia) return

    setErrorEdad("")

    // Validar edad mínima según clase
    const edadMinima = claseLicencia === "A" ? 18 : 21
    if (titular.edad < edadMinima) {
      setErrorEdad(`La edad mínima para la clase ${claseLicencia} es de ${edadMinima} años`)
      setVigencia(0)
      setCosto(0)
      return
    }

    // Calcular vigencia según edad
    let vigenciaCalculada = 5 // Por defecto 5 años
    if (titular.edad >= 65) {
      vigenciaCalculada = 3
    } else if (titular.edad >= 45) {
      vigenciaCalculada = 4
    }

    // Calcular costo según clase y vigencia
    const costoBase = claseLicencia === "A" ? 1500 : 2000
    const costoCalculado = costoBase * vigenciaCalculada

    // Asegurarse de que los valores se actualicen correctamente
    console.log(`Calculando: Vigencia=${vigenciaCalculada}, Costo=${costoCalculado}`)

    setVigencia(vigenciaCalculada)
    setCosto(costoCalculado)
  }

  const emitirLicencia = () => {
    // En producción, aquí se enviarían los datos al backend
    console.log({
      titular,
      claseLicencia,
      vigencia,
      costo,
    })

    // Simulación de éxito
    setSuccess(true)

    // Redireccionar después de 2 segundos
    setTimeout(() => {
      router.push(`/dashboard/licencias/imprimir?role=${role}`)
    }, 2000)
  }

  return (
    <Card className="w-full dark:border-slate-700">
      <CardContent className="pt-6">
        {success ? (
          <Alert className="bg-green-50 border-green-200 mb-4 dark:bg-green-900 dark:border-green-800">
            <CheckCircle2 className="h-4 w-4 text-green-600 dark:text-green-400" />
            <AlertDescription className="text-green-600 dark:text-green-400">
              Licencia emitida correctamente. Redirigiendo a impresión...
            </AlertDescription>
          </Alert>
        ) : (
          <div className="space-y-6">
            <div className="space-y-4">
              <h2 className="text-xl font-semibold dark:text-white">Buscar Titular</h2>

              {error && (
                <Alert variant="destructive">
                  <AlertCircle className="h-4 w-4" />
                  <AlertDescription>{error}</AlertDescription>
                </Alert>
              )}

              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <Label htmlFor="tipoDocumento">Tipo de Documento</Label>
                  <Select value={tipoDocumento} onValueChange={setTipoDocumento}>
                    <SelectTrigger id="tipoDocumento">
                      <SelectValue placeholder="Seleccionar" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="DNI">DNI</SelectItem>
                      <SelectItem value="Pasaporte">Pasaporte</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div>
                  <Label htmlFor="numeroDocumento">Número de Documento</Label>
                  <Input
                    id="numeroDocumento"
                    value={numeroDocumento}
                    onChange={(e) => setNumeroDocumento(e.target.value)}
                    placeholder="Ingrese número"
                  />
                </div>

                <div className="flex items-end">
                  <Button onClick={buscarTitular} className="w-full transition-transform duration-300 hover:scale-105">
                    <Search className="h-4 w-4 mr-2" />
                    Buscar
                  </Button>
                </div>
              </div>
            </div>

            {titular && (
              <>
                <Separator className="dark:bg-slate-700" />

                <div className="space-y-4">
                  <h2 className="text-xl font-semibold dark:text-white">Datos del Titular</h2>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <Label>Nombre y Apellido</Label>
                      <div className="p-2 bg-slate-100 dark:bg-slate-800 rounded-md">{titular.nombreApellido}</div>
                    </div>

                    <div>
                      <Label>Fecha de Nacimiento</Label>
                      <div className="p-2 bg-slate-100 dark:bg-slate-800 rounded-md">
                        {new Date(titular.fechaNacimiento).toLocaleDateString("es-ES")} ({titular.edad} años)
                      </div>
                    </div>

                    <div>
                      <Label>Dirección</Label>
                      <div className="p-2 bg-slate-100 dark:bg-slate-800 rounded-md">{titular.direccion}</div>
                    </div>

                    <div>
                      <Label>Grupo Sanguíneo y Factor RH</Label>
                      <div className="p-2 bg-slate-100 dark:bg-slate-800 rounded-md">
                        {titular.grupoSanguineo} {titular.factorRh}
                      </div>
                    </div>
                  </div>

                  <Separator className="dark:bg-slate-700" />

                  <div className="space-y-4">
                    <h2 className="text-xl font-semibold dark:text-white">Emitir Licencia</h2>

                    {errorEdad && (
                      <Alert variant="destructive">
                        <AlertCircle className="h-4 w-4" />
                        <AlertDescription>{errorEdad}</AlertDescription>
                      </Alert>
                    )}

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                      <div>
                        <Label htmlFor="claseLicencia">Clase de Licencia</Label>
                        <Select
                          value={claseLicencia}
                          onValueChange={(value) => {
                            setClaseLicencia(value)
                            // Usar useEffect para manejar los cambios de claseLicencia en lugar de setTimeout
                          }}
                        >
                          <SelectTrigger id="claseLicencia">
                            <SelectValue placeholder="Seleccionar" />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="A">Clase A</SelectItem>
                            <SelectItem value="B">Clase B</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>

                      {claseLicencia && !errorEdad && (
                        <div className="col-span-2 grid grid-cols-2 gap-4">
                          <div>
                            <Label>Vigencia</Label>
                            <div className="p-2 bg-slate-100 dark:bg-slate-800 rounded-md">{vigencia} años</div>
                          </div>

                          <div>
                            <Label>Costo</Label>
                            <div className="p-2 bg-slate-100 dark:bg-slate-800 rounded-md">${costo}</div>
                          </div>
                        </div>
                      )}
                    </div>

                    <div className="flex justify-end gap-4 mt-6">
                      <Button
                        type="button"
                        variant="outline"
                        onClick={() => router.push(`/dashboard?role=${role}`)}
                        className="transition-transform duration-300 hover:scale-105"
                      >
                        <ArrowLeft className="h-4 w-4 mr-2" />
                        Volver
                      </Button>

                      {claseLicencia && !errorEdad && vigencia > 0 && (
                        <Button onClick={emitirLicencia} className="transition-transform duration-300 hover:scale-105">
                          Emitir Licencia
                        </Button>
                      )}
                    </div>
                  </div>
                </div>
              </>
            )}

            {!titular && (
              <div className="flex justify-end mt-6">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => router.push(`/dashboard?role=${role}`)}
                  className="transition-transform duration-300 hover:scale-105"
                >
                  <ArrowLeft className="h-4 w-4 mr-2" />
                  Volver
                </Button>
              </div>
            )}
          </div>
        )}
      </CardContent>
    </Card>
  )
}
