"use client"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Input } from "@/components/ui/input"
import { Search, AlertTriangle, Calendar, ArrowUpDown, ArrowLeft } from "lucide-react"
import { licenciasEmitidas } from "@/data/licencia-data"

interface LicenciaVencida {
  numeroLicencia: string
  titular: {
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
  claseLicencia: string
  fechaEmision: string
  fechaVencimiento: string
  vigencia: number
  costo: number
  diasVencida: number
}

export default function LicenciasVencidasGrid({ role }: { role: string | null }) {
  const router = useRouter()
  const [licenciasVencidas, setLicenciasVencidas] = useState<LicenciaVencida[]>([])
  const [searchQuery, setSearchQuery] = useState("")
  const [sortConfig, setSortConfig] = useState<{
    key: keyof LicenciaVencida | "titular.nombreApellido" | "titular.numeroDocumento"
    direction: "asc" | "desc"
  }>({
    key: "diasVencida",
    direction: "desc",
  })

  useEffect(() => {
    // Filtrar licencias vencidas y calcular días vencidos
    const hoy = new Date()
    const vencidas = licenciasEmitidas
      .filter((licencia) => {
        const fechaVencimiento = new Date(licencia.fechaVencimiento)
        return fechaVencimiento < hoy
      })
      .map((licencia) => {
        const fechaVencimiento = new Date(licencia.fechaVencimiento)
        const diasVencida = Math.floor((hoy.getTime() - fechaVencimiento.getTime()) / (1000 * 60 * 60 * 24))
        return { ...licencia, diasVencida }
      })

    setLicenciasVencidas(vencidas)
  }, [])

  // Función para formatear fechas
  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleDateString("es-AR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    })
  }

  // Función para ordenar licencias
  const sortLicencias = (licencias: LicenciaVencida[]) => {
    return [...licencias].sort((a, b) => {
      let aValue: any
      let bValue: any

      // Manejar propiedades anidadas
      if (sortConfig.key === "titular.nombreApellido") {
        aValue = a.titular.nombreApellido
        bValue = b.titular.nombreApellido
      } else if (sortConfig.key === "titular.numeroDocumento") {
        aValue = a.titular.numeroDocumento
        bValue = b.titular.numeroDocumento
      } else {
        aValue = a[sortConfig.key as keyof LicenciaVencida]
        bValue = b[sortConfig.key as keyof LicenciaVencida]
      }

      if (aValue < bValue) {
        return sortConfig.direction === "asc" ? -1 : 1
      }
      if (aValue > bValue) {
        return sortConfig.direction === "asc" ? 1 : -1
      }
      return 0
    })
  }

  // Función para cambiar el orden
  const requestSort = (key: keyof LicenciaVencida | "titular.nombreApellido" | "titular.numeroDocumento") => {
    let direction: "asc" | "desc" = "asc"
    if (sortConfig.key === key && sortConfig.direction === "asc") {
      direction = "desc"
    }
    setSortConfig({ key, direction })
  }

  // Filtrar licencias por búsqueda
  const filteredLicencias = licenciasVencidas.filter(
    (licencia) =>
      licencia.titular.nombreApellido.toLowerCase().includes(searchQuery.toLowerCase()) ||
      licencia.titular.numeroDocumento.toLowerCase().includes(searchQuery.toLowerCase()) ||
      licencia.numeroLicencia.toLowerCase().includes(searchQuery.toLowerCase()),
  )

  // Ordenar licencias filtradas
  const sortedLicencias = sortLicencias(filteredLicencias)

  // Navegar a la página de emisión de licencias con los datos del titular
  const navigateToEmitir = (tipoDocumento: string, numeroDocumento: string) => {
    if (role) {
      router.push(
        `/dashboard/licencias/emitir?role=${role}&tipoDocumento=${tipoDocumento}&numeroDocumento=${numeroDocumento}&autoSearch=true`,
      )
    }
  }

  // Volver al dashboard
  const volverAlDashboard = () => {
    if (role) {
      router.push(`/dashboard?role=${role}`)
    } else {
      router.push("/dashboard")
    }
  }

  // Obtener clase de severidad según días vencidos
  const getSeverityClass = (diasVencida: number) => {
    if (diasVencida > 180) return "text-red-600 dark:text-red-400 font-medium"
    if (diasVencida > 90) return "text-amber-600 dark:text-amber-400 font-medium"
    return "text-orange-500 dark:text-orange-400 font-medium"
  }

  return (
    <Card className="w-full">
      <CardHeader className="pb-2">
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <div className="flex items-center gap-4">
            <Button
              variant="outline"
              size="sm"
              onClick={volverAlDashboard}
              className="transition-transform duration-300 hover:scale-105"
            >
              <ArrowLeft className="h-4 w-4 mr-2" />
              Volver
            </Button>
            <CardTitle className="text-xl flex items-center gap-2">
              <AlertTriangle className="h-5 w-5 text-red-500" />
              Licencias Vencidas
            </CardTitle>
          </div>
          <div className="relative w-full sm:w-64">
            <Input
              type="text"
              placeholder="Buscar por nombre o documento..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pr-8"
            />
            <Search className="absolute right-2 top-2.5 h-4 w-4 text-slate-400" />
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <div className="rounded-md border">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead
                  className="cursor-pointer hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                  onClick={() => requestSort("titular.nombreApellido")}
                >
                  <div className="flex items-center gap-1">
                    Titular
                    <ArrowUpDown className="h-3 w-3" />
                  </div>
                </TableHead>
                <TableHead
                  className="cursor-pointer hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                  onClick={() => requestSort("titular.numeroDocumento")}
                >
                  <div className="flex items-center gap-1">
                    Documento
                    <ArrowUpDown className="h-3 w-3" />
                  </div>
                </TableHead>
                <TableHead
                  className="cursor-pointer hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                  onClick={() => requestSort("claseLicencia")}
                >
                  <div className="flex items-center gap-1">
                    Clase
                    <ArrowUpDown className="h-3 w-3" />
                  </div>
                </TableHead>
                <TableHead
                  className="cursor-pointer hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                  onClick={() => requestSort("fechaVencimiento")}
                >
                  <div className="flex items-center gap-1">
                    Vencimiento
                    <ArrowUpDown className="h-3 w-3" />
                  </div>
                </TableHead>
                <TableHead
                  className="cursor-pointer hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                  onClick={() => requestSort("diasVencida")}
                >
                  <div className="flex items-center gap-1">
                    Días vencida
                    <ArrowUpDown className="h-3 w-3" />
                  </div>
                </TableHead>
                <TableHead>Acciones</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {sortedLicencias.length > 0 ? (
                sortedLicencias.map((licencia) => (
                  <TableRow key={licencia.numeroLicencia} className="hover:bg-slate-50 dark:hover:bg-slate-800/50">
                    <TableCell className="font-medium">{licencia.titular.nombreApellido}</TableCell>
                    <TableCell>
                      {licencia.titular.tipoDocumento}: {licencia.titular.numeroDocumento}
                    </TableCell>
                    <TableCell>
                      <Badge variant="outline" className="font-medium">
                        {licencia.claseLicencia}
                      </Badge>
                    </TableCell>
                    <TableCell className="whitespace-nowrap">
                      <div className="flex items-center gap-1">
                        <Calendar className="h-3.5 w-3.5 text-slate-400" />
                        {formatDate(licencia.fechaVencimiento)}
                      </div>
                    </TableCell>
                    <TableCell className={getSeverityClass(licencia.diasVencida)}>
                      {licencia.diasVencida} días
                    </TableCell>
                    <TableCell>
                      <Button
                        size="sm"
                        onClick={() =>
                          navigateToEmitir(licencia.titular.tipoDocumento, licencia.titular.numeroDocumento)
                        }
                        className="bg-green-600 hover:bg-green-700 text-white"
                      >
                        Renovar
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={6} className="h-24 text-center">
                    No se encontraron licencias vencidas
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </div>
      </CardContent>
    </Card>
  )
}
