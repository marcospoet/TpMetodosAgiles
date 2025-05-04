"use client"

import type React from "react"

import { useState, useRef, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { ArrowLeft, Download, Upload, Camera, X, Search } from "lucide-react"
import Image from "next/image"
import { jsPDF } from "jspdf"
import { Input } from "@/components/ui/input"
import { useIsMobile } from "@/hooks/use-mobile"
import { licenciasEmitidas } from "@/data/licencia-data" // Importar desde el archivo compartido
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Alert, AlertDescription } from "@/components/ui/alert"

interface ImprimirLicenciaFormProps {
  role: string
}

export default function ImprimirLicenciaForm({ role }: ImprimirLicenciaFormProps) {
  const router = useRouter()
  const isMobile = useIsMobile()
  const [generandoPDF, setGenerandoPDF] = useState(false)
  const [activeTab, setActiveTab] = useState("licencia")
  const [fotoTitular, setFotoTitular] = useState<string | null>(null)
  const [tomarFoto, setTomarFoto] = useState(false)
  const [camaraActiva, setCamaraActiva] = useState(false)
  const [errorCamara, setErrorCamara] = useState<string | null>(null)

  // Estados para el buscador
  const [tipoDocumento, setTipoDocumento] = useState<string>("")
  const [numeroDocumento, setNumeroDocumento] = useState<string>("")
  const [licenciaSeleccionada, setLicenciaSeleccionada] = useState<(typeof licenciasEmitidas)[0] | null>(null)
  const [resultadosBusqueda, setResultadosBusqueda] = useState<typeof licenciasEmitidas>([])
  const [errorBusqueda, setErrorBusqueda] = useState<string>("")
  const [busquedaRealizada, setBusquedaRealizada] = useState(false)

  const licenciaRef = useRef<HTMLDivElement>(null)
  const comprobanteRef = useRef<HTMLDivElement>(null)
  const videoRef = useRef<HTMLVideoElement>(null)
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  // Función para buscar licencias
  const buscarLicencia = () => {
    setErrorBusqueda("")
    setResultadosBusqueda([])
    setLicenciaSeleccionada(null)
    setBusquedaRealizada(true)

    if (!tipoDocumento || !numeroDocumento) {
      setErrorBusqueda("Debe completar tipo y número de documento")
      return
    }

    // Buscar en la base de datos simulada
    const resultados = licenciasEmitidas.filter(
      (licencia) =>
        licencia.titular.tipoDocumento === tipoDocumento && licencia.titular.numeroDocumento === numeroDocumento,
    )

    if (resultados.length === 0) {
      setErrorBusqueda("No se encontraron licencias con ese documento")
      return
    }

    setResultadosBusqueda(resultados)

    // Si solo hay un resultado, seleccionarlo automáticamente
    if (resultados.length === 1) {
      setLicenciaSeleccionada(resultados[0])
    }
  }

  // Función para seleccionar una licencia
  const seleccionarLicencia = (licencia: (typeof licenciasEmitidas)[0]) => {
    setLicenciaSeleccionada(licencia)
    setFotoTitular(null) // Resetear la foto al cambiar de licencia
  }

  // Iniciar/detener la cámara
  useEffect(() => {
    let stream: MediaStream | null = null

    const iniciarCamara = async () => {
      try {
        if (tomarFoto && !camaraActiva) {
          setErrorCamara(null)

          // Configuración optimizada para móviles y escritorio
          const constraints = {
            video: {
              facingMode: "user", // Siempre usar cámara frontal (selfie)
              width: { ideal: 640 },
              height: { ideal: 480 },
            },
            audio: false,
          }

          stream = await navigator.mediaDevices.getUserMedia(constraints)

          if (videoRef.current) {
            videoRef.current.srcObject = stream
            videoRef.current.onloadedmetadata = () => {
              if (videoRef.current) {
                videoRef.current
                  .play()
                  .then(() => {
                    setCamaraActiva(true)
                  })
                  .catch((err) => {
                    console.error("Error al reproducir el video:", err)
                    setErrorCamara("No se pudo iniciar la reproducción de video")
                  })
              }
            }
          }
        }
      } catch (error) {
        console.error("Error al acceder a la cámara:", error)
        setErrorCamara("No se pudo acceder a la cámara. Verifique los permisos o intente cargar una imagen.")
        setTomarFoto(false)
      }
    }

    if (tomarFoto) {
      iniciarCamara()
    } else {
      // Detener la cámara si estaba activa
      if (videoRef.current?.srcObject) {
        const videoStream = videoRef.current.srcObject as MediaStream
        videoStream.getTracks().forEach((track) => track.stop())
      }
      setCamaraActiva(false)
    }

    // Limpiar al desmontar
    return () => {
      if (stream) {
        stream.getTracks().forEach((track) => track.stop())
        setCamaraActiva(false)
      }
    }
  }, [tomarFoto, isMobile])

  const capturarFoto = () => {
    if (videoRef.current && canvasRef.current) {
      const video = videoRef.current
      const canvas = canvasRef.current
      const context = canvas.getContext("2d")

      if (context) {
        // Configurar el canvas con las dimensiones del video
        canvas.width = video.videoWidth
        canvas.height = video.videoHeight

        // Dibujar el frame actual del video en el canvas
        context.drawImage(video, 0, 0, canvas.width, canvas.height)

        // Convertir el canvas a una URL de datos
        const dataUrl = canvas.toDataURL("image/png")
        setFotoTitular(dataUrl)

        // Detener la cámara
        const stream = video.srcObject as MediaStream
        if (stream) {
          stream.getTracks().forEach((track) => track.stop())
        }

        setTomarFoto(false)
        setCamaraActiva(false)
      }
    }
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      const reader = new FileReader()
      reader.onload = (event) => {
        if (event.target?.result) {
          setFotoTitular(event.target.result as string)
        }
      }
      reader.readAsDataURL(file)
    }
  }

  const eliminarFoto = () => {
    setFotoTitular(null)
  }

  const abrirSelectorArchivos = () => {
    if (fileInputRef.current) {
      // En móviles, intentar abrir directamente la cámara
      if (isMobile) {
        fileInputRef.current.setAttribute("capture", "user")
      } else {
        fileInputRef.current.removeAttribute("capture")
      }
      fileInputRef.current.click()
    }
  }

  // Función para generar un PDF directamente sin usar html2canvas
  const generarPDFDirecto = async () => {
    if (!licenciaSeleccionada) {
      alert("Debe seleccionar una licencia primero")
      return
    }

    setGenerandoPDF(true)

    try {
      // Crear un nuevo documento PDF
      // Crear un nuevo documento PDF - Siempre usar orientación vertical para mejor compatibilidad móvil
      const pdf = new jsPDF({
        orientation: "portrait",
        unit: "mm",
        format: "a4",
      })

      // Obtener dimensiones de la página
      const pageWidth = pdf.internal.pageSize.getWidth()
      const pageHeight = pdf.internal.pageSize.getHeight()

      // Definir márgenes
      const margin = 10
      const contentWidth = pageWidth - 2 * margin
      const contentHeight = pageHeight - 2 * margin

      // Colores
      const colorPrimario = "#1e293b" // slate-800
      const colorTexto = "#000000"
      const colorFondo = "#ffffff"
      const colorBorde = "#e2e8f0" // slate-200

      if (activeTab === "licencia") {
        // ===== LICENCIA DE CONDUCIR =====
        // Usar diseño vertical para mejor compatibilidad con móviles

        // Fondo del encabezado
        pdf.setFillColor(colorPrimario)
        pdf.rect(margin, margin, contentWidth, 25, "F")

        // Título de la licencia
        pdf.setTextColor(255, 255, 255) // Texto blanco
        pdf.setFont("helvetica", "bold")
        pdf.setFontSize(14)
        pdf.text("MUNICIPALIDAD", pageWidth / 2, margin + 8, { align: "center" })
        pdf.setFontSize(12)
        pdf.text("LICENCIA DE CONDUCIR", pageWidth / 2, margin + 16, { align: "center" })

        // Número de licencia y clase
        pdf.setFontSize(10)
        pdf.text(`N° ${licenciaSeleccionada.numeroLicencia}`, pageWidth / 2, margin + 24, { align: "center" })

        // Contenido principal
        pdf.setTextColor(colorTexto)
        pdf.setFont("helvetica", "normal")
        pdf.setFontSize(10)

        // Posición inicial del contenido
        let y = margin + 35

        // Clase de licencia en un recuadro destacado
        pdf.setFillColor(220, 220, 220)
        pdf.rect(margin, y, contentWidth, 12, "F")
        pdf.setFont("helvetica", "bold")
        pdf.setFontSize(12)
        pdf.text(`CLASE ${licenciaSeleccionada.claseLicencia}`, pageWidth / 2, y + 8, { align: "center" })

        y += 20

        // Foto del titular - Versión mejorada (más grande)
        if (fotoTitular) {
          try {
            // Aumentar el tamaño de la foto y centrarla mejor
            const fotoWidth = 70 // Más ancho que antes
            const fotoHeight = 90 // Más alto que antes
            pdf.addImage(
              fotoTitular,
              "JPEG",
              pageWidth / 2 - fotoWidth / 2,
              y,
              fotoWidth,
              fotoHeight,
              undefined,
              "FAST",
            )
            y += fotoHeight + 10 // Ajustar la posición Y después de la foto
          } catch (error) {
            console.error("Error al añadir la imagen:", error)
            // Si hay error, mostrar el rectángulo de "SIN FOTO"
            pdf.setDrawColor(colorBorde)
            pdf.setFillColor(240, 240, 240)
            pdf.rect(pageWidth / 2 - 35, y, 70, 90, "FD")
            pdf.setFontSize(8)
            pdf.text("SIN FOTO", pageWidth / 2, y + 45, { align: "center" })
            y += 100 // Ajustar la posición Y
          }
        } else {
          // Rectángulo para la foto - Más grande
          pdf.setDrawColor(colorBorde)
          pdf.setFillColor(240, 240, 240)
          pdf.rect(pageWidth / 2 - 35, y, 70, 90, "FD")
          pdf.setFontSize(8)
          pdf.text("SIN FOTO", pageWidth / 2, y + 45, { align: "center" })
          y += 100 // Ajustar la posición Y
        }

        // Datos del titular - Centrados en la página
        const datosWidth = contentWidth - 20
        const datosX = margin + 10

        // Nombre y apellido
        pdf.setDrawColor(colorBorde)
        pdf.setFillColor(245, 245, 245)
        pdf.rect(datosX, y, datosWidth, 12, "FD")
        pdf.setFont("helvetica", "bold")
        pdf.setFontSize(10)
        pdf.text("APELLIDO Y NOMBRE", datosX + 5, y + 8)
        y += 12
        pdf.setFont("helvetica", "normal")
        pdf.text(licenciaSeleccionada.titular.nombreApellido, datosX + 5, y + 8)
        pdf.line(datosX, y, datosX + datosWidth, y)
        y += 12

        // Documento y Fecha de nacimiento en la misma línea
        pdf.setDrawColor(colorBorde)
        pdf.setFillColor(245, 245, 245)
        pdf.rect(datosX, y, datosWidth / 2 - 5, 12, "FD")
        pdf.rect(datosX + datosWidth / 2, y, datosWidth / 2, 12, "FD")
        pdf.setFont("helvetica", "bold")
        pdf.text("DOCUMENTO", datosX + 5, y + 8)
        pdf.text("FECHA NACIMIENTO", datosX + datosWidth / 2 + 5, y + 8)
        y += 12
        pdf.setFont("helvetica", "normal")
        pdf.text(
          `${licenciaSeleccionada.titular.tipoDocumento} ${licenciaSeleccionada.titular.numeroDocumento}`,
          datosX + 5,
          y + 8,
        )
        pdf.text(
          new Date(licenciaSeleccionada.titular.fechaNacimiento).toLocaleDateString("es-ES"),
          datosX + datosWidth / 2 + 5,
          y + 8,
        )
        pdf.line(datosX, y, datosX + datosWidth, y)
        y += 12

        // Domicilio
        pdf.setDrawColor(colorBorde)
        pdf.setFillColor(245, 245, 245)
        pdf.rect(datosX, y, datosWidth, 12, "FD")
        pdf.setFont("helvetica", "bold")
        pdf.text("DOMICILIO", datosX + 5, y + 8)
        y += 12
        pdf.setFont("helvetica", "normal")
        pdf.text(licenciaSeleccionada.titular.direccion, datosX + 5, y + 8)
        pdf.line(datosX, y, datosX + datosWidth, y)
        y += 12

        // Grupo sanguíneo, donante y vencimiento en la misma línea
        pdf.setDrawColor(colorBorde)
        pdf.setFillColor(245, 245, 245)
        pdf.rect(datosX, y, datosWidth / 3 - 5, 12, "FD")
        pdf.rect(datosX + datosWidth / 3, y, datosWidth / 3 - 5, 12, "FD")
        pdf.rect(datosX + (2 * datosWidth) / 3, y, datosWidth / 3 + 5, 12, "FD")
        pdf.setFont("helvetica", "bold")
        pdf.text("GRUPO SANG.", datosX + 5, y + 8)
        pdf.text("DONANTE", datosX + datosWidth / 3 + 5, y + 8)
        pdf.text("VENCIMIENTO", datosX + (2 * datosWidth) / 3 + 5, y + 8)
        y += 12
        pdf.setFont("helvetica", "normal")
        pdf.text(
          `${licenciaSeleccionada.titular.grupoSanguineo}${licenciaSeleccionada.titular.factorRh}`,
          datosX + 5,
          y + 8,
        )
        pdf.text(licenciaSeleccionada.titular.donanteOrganos, datosX + datosWidth / 3 + 5, y + 8)
        pdf.text(
          new Date(licenciaSeleccionada.fechaVencimiento).toLocaleDateString("es-ES"),
          datosX + (2 * datosWidth) / 3 + 5,
          y + 8,
        )

        // Borde alrededor de toda la licencia
        pdf.setDrawColor(colorBorde)
        pdf.rect(margin, margin, contentWidth, y + 15 - margin, "S")

        // Agregar pie de página
        y += 25
        pdf.setFontSize(8)
        pdf.setTextColor(100, 100, 100)
        pdf.text("DOCUMENTO VÁLIDO COMO LICENCIA DE CONDUCIR", pageWidth / 2, y, { align: "center" })
        pdf.text(`Emitido el: ${new Date().toLocaleDateString("es-ES")}`, pageWidth / 2, y + 5, { align: "center" })
      } else {
        // ===== COMPROBANTE DE PAGO =====
        // Mejorar diseño para mejor visualización

        // Encabezado
        pdf.setFillColor(colorPrimario)
        pdf.rect(margin, margin, contentWidth, 25, "F")

        // Título del comprobante
        pdf.setTextColor(255, 255, 255) // Texto blanco
        pdf.setFont("helvetica", "bold")
        pdf.setFontSize(14)
        pdf.text("MUNICIPALIDAD", pageWidth / 2, margin + 8, { align: "center" })
        pdf.setFontSize(12)
        pdf.text("COMPROBANTE DE PAGO", pageWidth / 2, margin + 16, { align: "center" })

        // Número de recibo y fecha
        pdf.setFontSize(10)
        pdf.text(`RECIBO N° R-${licenciaSeleccionada.numeroLicencia}`, pageWidth / 2, margin + 24, { align: "center" })

        // Posición inicial del contenido
        let y = margin + 35

        // Datos del titular
        pdf.setTextColor(colorTexto)
        pdf.setFillColor(240, 240, 240)
        pdf.rect(margin, y, contentWidth, 12, "F")
        pdf.setFont("helvetica", "bold")
        pdf.setFontSize(11)
        pdf.text("DATOS DEL TITULAR", margin + 5, y + 8)

        y += 20
        pdf.setFont("helvetica", "bold")
        pdf.setFontSize(10)
        pdf.text("APELLIDO Y NOMBRE:", margin + 5, y)
        pdf.setFont("helvetica", "normal")
        pdf.text(licenciaSeleccionada.titular.nombreApellido, margin + 60, y)

        y += 10
        pdf.setFont("helvetica", "bold")
        pdf.text("DOCUMENTO:", margin + 5, y)
        pdf.setFont("helvetica", "normal")
        pdf.text(
          `${licenciaSeleccionada.titular.tipoDocumento} ${licenciaSeleccionada.titular.numeroDocumento}`,
          margin + 60,
          y,
        )

        y += 10
        pdf.setFont("helvetica", "bold")
        pdf.text("FECHA DE EMISIÓN:", margin + 5, y)
        pdf.setFont("helvetica", "normal")
        pdf.text(new Date(licenciaSeleccionada.fechaEmision).toLocaleDateString("es-ES"), margin + 60, y)

        // Detalle de pago
        y += 20
        pdf.setFillColor(240, 240, 240)
        pdf.rect(margin, y, contentWidth, 12, "F")
        pdf.setFont("helvetica", "bold")
        pdf.setFontSize(11)
        pdf.text("DETALLE DE PAGO", margin + 5, y + 8)

        y += 20

        // Tabla de detalle con bordes completos
        const colWidth = [contentWidth * 0.7, contentWidth * 0.3]
        const rowHeight = 12

        // Encabezado de tabla
        pdf.setFillColor(245, 245, 245)
        pdf.rect(margin, y, colWidth[0], rowHeight, "F")
        pdf.rect(margin + colWidth[0], y, colWidth[1], rowHeight, "F")

        pdf.setDrawColor(colorBorde)
        pdf.rect(margin, y, colWidth[0], rowHeight, "S")
        pdf.rect(margin + colWidth[0], y, colWidth[1], rowHeight, "S")

        pdf.setFont("helvetica", "bold")
        pdf.setFontSize(10)
        pdf.text("Concepto", margin + 5, y + 8)
        pdf.text("Importe", margin + colWidth[0] + colWidth[1] - 5, y + 8, { align: "right" })

        // Contenido de tabla
        y += rowHeight
        pdf.setFont("helvetica", "normal")

        // Primera fila
        pdf.rect(margin, y, colWidth[0], rowHeight * 2, "S")
        pdf.rect(margin + colWidth[0], y, colWidth[1], rowHeight * 2, "S")

        pdf.text(`Emisión de Licencia Clase ${licenciaSeleccionada.claseLicencia}`, margin + 5, y + 8)
        pdf.text(`Vigencia: ${licenciaSeleccionada.vigencia} años`, margin + 10, y + 18)
        pdf.text(
          `$${licenciaSeleccionada.costo.toLocaleString("es-AR")}`,
          margin + colWidth[0] + colWidth[1] - 5,
          y + 8,
          {
            align: "right",
          },
        )

        y += rowHeight * 2

        // Total
        pdf.setFillColor(245, 245, 245)
        pdf.rect(margin, y, colWidth[0], rowHeight, "FD")
        pdf.rect(margin + colWidth[0], y, colWidth[1], rowHeight, "FD")

        pdf.setFont("helvetica", "bold")
        pdf.text("TOTAL", margin + 5, y + 8)
        pdf.text(
          `$${licenciaSeleccionada.costo.toLocaleString("es-AR")}`,
          margin + colWidth[0] + colWidth[1] - 5,
          y + 8,
          {
            align: "right",
          },
        )

        // Pie de página
        y += 30
        pdf.setDrawColor(colorBorde)
        pdf.line(margin, y, pageWidth - margin, y)

        y += 10
        pdf.setFont("helvetica", "bold")
        pdf.setFontSize(9)
        pdf.text("OPERADOR:", margin + 5, y)
        pdf.setFont("helvetica", "normal")
        pdf.text("Admin Sistema", margin + 30, y)

        pdf.setFont("helvetica", "bold")
        pdf.text("FECHA Y HORA:", pageWidth - margin - 80, y)
        pdf.setFont("helvetica", "normal")
        pdf.text(new Date().toLocaleString("es-ES"), pageWidth - margin - 5, y, { align: "right" })

        // Información adicional
        y += 20
        pdf.setFontSize(8)
        pdf.setTextColor(100, 100, 100)
        pdf.text("ESTE COMPROBANTE DEBE SER CONSERVADO COMO CONSTANCIA DE PAGO", pageWidth / 2, y, { align: "center" })
        pdf.text("Municipalidad - Sistema de Licencias", pageWidth / 2, y + 5, { align: "center" })

        // Borde alrededor de todo el comprobante
        pdf.setDrawColor(colorBorde)
        pdf.rect(margin, margin, contentWidth, y + 15 - margin, "S")
      }

      // Agregar metadatos al PDF
      pdf.setProperties({
        title:
          activeTab === "licencia"
            ? `Licencia ${licenciaSeleccionada.numeroLicencia}`
            : `Comprobante ${licenciaSeleccionada.numeroLicencia}`,
        subject: "Sistema Municipal de Licencias",
        author: "Municipalidad",
        keywords: "licencia, conducir, municipal",
        creator: "Sistema Municipal de Licencias",
      })

      // Descargar el PDF
      const fileName =
        activeTab === "licencia"
          ? `Licencia_${licenciaSeleccionada.numeroLicencia}.pdf`
          : `Comprobante_${licenciaSeleccionada.numeroLicencia}.pdf`

      pdf.save(fileName)
    } catch (error) {
      console.error("Error al generar el PDF:", error)
      alert("Hubo un error al generar el PDF. Por favor, intente nuevamente.")
    } finally {
      setGenerandoPDF(false)
    }
  }

  return (
    <Card className="w-full dark:border-slate-700">
      <CardContent className="pt-6">
        <Tabs defaultValue="licencia" value={activeTab} onValueChange={setActiveTab}>
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="licencia" className="transition-all duration-300">
              Licencia de Conducir
            </TabsTrigger>
            <TabsTrigger value="comprobante" className="transition-all duration-300">
              Comprobante de Pago
            </TabsTrigger>
          </TabsList>

          <TabsContent value="licencia" className="mt-6">
            <div className="space-y-6">
              {/* Buscador de licencias */}
              {!licenciaSeleccionada ? (
                <div className="border rounded-lg p-4 dark:border-slate-700 bg-slate-50 dark:bg-slate-800">
                  <h3 className="text-lg font-medium mb-4 dark:text-white">Buscar Licencia</h3>

                  {errorBusqueda && (
                    <Alert variant="destructive" className="mb-4">
                      <AlertDescription>{errorBusqueda}</AlertDescription>
                    </Alert>
                  )}

                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                    <div>
                      <Select value={tipoDocumento} onValueChange={setTipoDocumento}>
                        <SelectTrigger>
                          <SelectValue placeholder="Tipo de Documento" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="DNI">DNI</SelectItem>
                          <SelectItem value="Pasaporte">Pasaporte</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>

                    <div>
                      <Input
                        placeholder="Número de Documento"
                        value={numeroDocumento}
                        onChange={(e) => setNumeroDocumento(e.target.value)}
                      />
                    </div>

                    <div className="flex items-end">
                      <Button onClick={buscarLicencia} className="w-full">
                        <Search className="h-4 w-4 mr-2" />
                        Buscar
                      </Button>
                    </div>
                  </div>

                  {busquedaRealizada && resultadosBusqueda.length > 0 && (
                    <div className="mt-6">
                      <h4 className="font-medium mb-2 dark:text-white">Resultados de la búsqueda</h4>
                      <div className="space-y-2">
                        {resultadosBusqueda.map((licencia) => (
                          <div
                            key={licencia.numeroLicencia}
                            className="p-3 border rounded-md dark:border-slate-700 hover:bg-slate-100 dark:hover:bg-slate-700 cursor-pointer transition-colors"
                            onClick={() => seleccionarLicencia(licencia)}
                          >
                            <div className="flex justify-between items-center">
                              <div>
                                <p className="font-medium dark:text-white">{licencia.titular.nombreApellido}</p>
                                <p className="text-sm text-slate-500 dark:text-slate-400">
                                  {licencia.titular.tipoDocumento} {licencia.titular.numeroDocumento} - Clase{" "}
                                  {licencia.claseLicencia}
                                </p>
                              </div>
                              <div className="text-right">
                                <p className="text-sm dark:text-white">N° {licencia.numeroLicencia}</p>
                                <p className="text-xs text-slate-500 dark:text-slate-400">
                                  Vence: {new Date(licencia.fechaVencimiento).toLocaleDateString("es-ES")}
                                </p>
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              ) : (
                <>
                  {/* Sección para cargar/tomar foto */}
                  {!tomarFoto ? (
                    <div className="border rounded-lg p-4 dark:border-slate-700 bg-slate-50 dark:bg-slate-800">
                      <div className="flex justify-between items-center mb-4">
                        <h3 className="text-lg font-medium dark:text-white">Foto del Titular</h3>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => {
                            setLicenciaSeleccionada(null)
                            setFotoTitular(null)
                            setBusquedaRealizada(false)
                          }}
                        >
                          Cambiar Licencia
                        </Button>
                      </div>

                      <div className="mb-4 p-3 bg-slate-100 dark:bg-slate-700 rounded-md">
                        <div className="flex justify-between items-center">
                          <div>
                            <p className="font-medium dark:text-white">{licenciaSeleccionada.titular.nombreApellido}</p>
                            <p className="text-sm text-slate-500 dark:text-slate-400">
                              {licenciaSeleccionada.titular.tipoDocumento}{" "}
                              {licenciaSeleccionada.titular.numeroDocumento}
                            </p>
                          </div>
                          <div className="text-right">
                            <p className="text-sm dark:text-white">Licencia N° {licenciaSeleccionada.numeroLicencia}</p>
                            <p className="text-xs text-slate-500 dark:text-slate-400">
                              Clase {licenciaSeleccionada.claseLicencia}
                            </p>
                          </div>
                        </div>
                      </div>

                      {fotoTitular ? (
                        <div className="flex flex-col items-center">
                          <div className="relative w-32 h-40 mb-4">
                            <Image
                              src={fotoTitular || "/placeholder.svg"}
                              alt="Foto del titular"
                              fill
                              className="object-cover rounded-md"
                            />
                            <Button
                              variant="destructive"
                              size="icon"
                              className="absolute -top-2 -right-2 h-6 w-6 rounded-full"
                              onClick={eliminarFoto}
                            >
                              <X className="h-4 w-4" />
                            </Button>
                          </div>
                          <p className="text-sm text-slate-600 dark:text-slate-300 mb-2">Foto cargada correctamente</p>
                        </div>
                      ) : (
                        <div className="flex flex-col items-center">
                          <div className="w-32 h-40 bg-slate-200 dark:bg-slate-700 rounded-md flex items-center justify-center mb-4">
                            <Image
                              src="/placeholder.svg?height=160&width=120"
                              alt="Foto del titular"
                              width={120}
                              height={160}
                              className="rounded-md"
                            />
                          </div>
                          <p className="text-sm text-slate-600 dark:text-slate-300 mb-4">
                            Cargue una foto o tome una con la cámara
                          </p>
                          <div className="flex gap-4">
                            <Button
                              variant="outline"
                              onClick={abrirSelectorArchivos}
                              className="flex items-center gap-2"
                            >
                              <Upload className="h-4 w-4" />
                              <span>Cargar foto</span>
                            </Button>
                            <Input
                              ref={fileInputRef}
                              type="file"
                              accept="image/*"
                              className="hidden"
                              onChange={handleFileChange}
                            />
                            <Button
                              variant="outline"
                              onClick={() => setTomarFoto(true)}
                              className="flex items-center gap-2"
                            >
                              <Camera className="h-4 w-4" />
                              <span>Tomar foto</span>
                            </Button>
                          </div>
                        </div>
                      )}
                    </div>
                  ) : (
                    <div className="border rounded-lg p-4 dark:border-slate-700 bg-slate-50 dark:bg-slate-800">
                      <>
                        <h3 className="text-lg font-medium mb-4 dark:text-white">Tomar Foto</h3>
                        <div className="flex flex-col items-center">
                          {errorCamara ? (
                            <div className="text-center mb-4">
                              <p className="text-red-500 mb-2">{errorCamara}</p>
                              <Button variant="outline" onClick={() => setTomarFoto(false)}>
                                Volver
                              </Button>
                            </div>
                          ) : camaraActiva ? (
                            <>
                              <div className="relative w-full max-w-md mb-4 rounded-md overflow-hidden">
                                <video ref={videoRef} autoPlay playsInline muted className="w-full rounded-md" />
                              </div>
                              <canvas ref={canvasRef} className="hidden" />
                              <div className="flex gap-4">
                                <Button
                                  variant="outline"
                                  onClick={() => {
                                    if (videoRef.current?.srcObject) {
                                      const stream = videoRef.current.srcObject as MediaStream
                                      stream.getTracks().forEach((track) => track.stop())
                                    }
                                    setTomarFoto(false)
                                    setCamaraActiva(false)
                                  }}
                                >
                                  Cancelar
                                </Button>
                                <Button onClick={capturarFoto}>Capturar Foto</Button>
                              </div>
                            </>
                          ) : (
                            <div className="text-center">
                              <p className="text-slate-600 dark:text-slate-300 mb-4">
                                Si la cámara no se inicia automáticamente, intente usar este método alternativo:
                              </p>
                              <div className="flex justify-center">
                                <Button
                                  variant="default"
                                  onClick={abrirSelectorArchivos}
                                  className="flex items-center gap-2"
                                >
                                  <Camera className="h-4 w-4" />
                                  <span>Tomar foto con cámara</span>
                                </Button>
                              </div>
                            </div>
                          )}
                        </div>
                      </>
                    </div>
                  )}

                  {/* Previsualización de la licencia */}
                  <div ref={licenciaRef} className="border rounded-lg overflow-hidden dark:border-slate-700">
                    <div className="bg-slate-800 text-white p-4">
                      <div className="flex justify-between items-center">
                        <div className="flex items-center gap-3">
                          <div className="w-12 h-12 rounded-full bg-white/20 flex items-center justify-center overflow-hidden">
                            <Image
                              src="/images/logo-auto.png"
                              alt="Logo Municipal"
                              width={40}
                              height={40}
                              className="rounded-full object-contain"
                            />
                          </div>
                          <div>
                            <h3 className="font-bold">MUNICIPALIDAD</h3>
                            <p className="text-xs">LICENCIA DE CONDUCIR</p>
                          </div>
                        </div>
                        <div className="text-right">
                          <p className="text-sm">N° {licenciaSeleccionada.numeroLicencia}</p>
                          <p className="text-xs">CLASE {licenciaSeleccionada.claseLicencia}</p>
                        </div>
                      </div>
                    </div>

                    <div className="p-6 dark:bg-slate-800">
                      <div className="grid grid-cols-3 gap-6">
                        <div className="col-span-1">
                          <div className="w-full aspect-[3/4] bg-slate-200 dark:bg-slate-700 rounded flex items-center justify-center overflow-hidden">
                            {fotoTitular ? (
                              <div className="relative w-full h-full">
                                <Image
                                  src={fotoTitular || "/placeholder.svg"}
                                  alt="Foto del titular"
                                  fill
                                  className="object-cover"
                                />
                              </div>
                            ) : (
                              <Image
                                src="/placeholder.svg?height=150&width=120"
                                alt="Foto del titular"
                                width={120}
                                height={150}
                                className="rounded"
                              />
                            )}
                          </div>
                        </div>

                        <div className="col-span-2 space-y-4">
                          <div>
                            <p className="text-xs text-slate-500 dark:text-slate-400">APELLIDO Y NOMBRE</p>
                            <p className="font-semibold dark:text-white">
                              {licenciaSeleccionada.titular.nombreApellido}
                            </p>
                          </div>

                          <div className="grid grid-cols-2 gap-4">
                            <div>
                              <p className="text-xs text-slate-500 dark:text-slate-400">DOCUMENTO</p>
                              <p className="dark:text-white">
                                {licenciaSeleccionada.titular.tipoDocumento}{" "}
                                {licenciaSeleccionada.titular.numeroDocumento}
                              </p>
                            </div>

                            <div>
                              <p className="text-xs text-slate-500 dark:text-slate-400">FECHA NACIMIENTO</p>
                              <p className="dark:text-white">
                                {new Date(licenciaSeleccionada.titular.fechaNacimiento).toLocaleDateString("es-ES")}
                              </p>
                            </div>
                          </div>

                          <div>
                            <p className="text-xs text-slate-500 dark:text-slate-400">DOMICILIO</p>
                            <p className="dark:text-white">{licenciaSeleccionada.titular.direccion}</p>
                          </div>

                          <div className="grid grid-cols-3 gap-4">
                            <div>
                              <p className="text-xs text-slate-500 dark:text-slate-400">GRUPO SANG.</p>
                              <p className="dark:text-white">
                                {licenciaSeleccionada.titular.grupoSanguineo} {licenciaSeleccionada.titular.factorRh}
                              </p>
                            </div>

                            <div>
                              <p className="text-xs text-slate-500 dark:text-slate-400">DONANTE</p>
                              <p className="dark:text-white">{licenciaSeleccionada.titular.donanteOrganos}</p>
                            </div>

                            <div>
                              <p className="text-xs text-slate-500 dark:text-slate-400">VENCIMIENTO</p>
                              <p className="font-semibold dark:text-white">
                                {new Date(licenciaSeleccionada.fechaVencimiento).toLocaleDateString("es-ES")}
                              </p>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </>
              )}

              <div className="flex justify-end gap-4">
                <Button
                  variant="outline"
                  onClick={() => router.push(`/dashboard?role=${role}`)}
                  className="transition-transform duration-300 hover:scale-105"
                >
                  <ArrowLeft className="h-4 w-4 mr-2" />
                  Volver
                </Button>
                {licenciaSeleccionada && (
                  <Button
                    onClick={generarPDFDirecto}
                    disabled={generandoPDF}
                    className="transition-transform duration-300 hover:scale-105"
                  >
                    <Download className="h-4 w-4 mr-2" />
                    {generandoPDF ? "Generando PDF..." : "Descargar PDF"}
                  </Button>
                )}
              </div>
            </div>
          </TabsContent>

          <TabsContent value="comprobante" className="mt-6">
            <div className="space-y-6">
              {/* Buscador para el comprobante - similar al de licencia */}
              {!licenciaSeleccionada ? (
                <div className="border rounded-lg p-4 dark:border-slate-700 bg-slate-50 dark:bg-slate-800">
                  <h3 className="text-lg font-medium mb-4 dark:text-white">Buscar Comprobante</h3>

                  {errorBusqueda && (
                    <Alert variant="destructive" className="mb-4">
                      <AlertDescription>{errorBusqueda}</AlertDescription>
                    </Alert>
                  )}

                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                    <div>
                      <Select value={tipoDocumento} onValueChange={setTipoDocumento}>
                        <SelectTrigger>
                          <SelectValue placeholder="Tipo de Documento" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="DNI">DNI</SelectItem>
                          <SelectItem value="Pasaporte">Pasaporte</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>

                    <div>
                      <Input
                        placeholder="Número de Documento"
                        value={numeroDocumento}
                        onChange={(e) => setNumeroDocumento(e.target.value)}
                      />
                    </div>

                    <div className="flex items-end">
                      <Button onClick={buscarLicencia} className="w-full">
                        <Search className="h-4 w-4 mr-2" />
                        Buscar
                      </Button>
                    </div>
                  </div>

                  {busquedaRealizada && resultadosBusqueda.length > 0 && (
                    <div className="mt-6">
                      <h4 className="font-medium mb-2 dark:text-white">Resultados de la búsqueda</h4>
                      <div className="space-y-2">
                        {resultadosBusqueda.map((licencia) => (
                          <div
                            key={licencia.numeroLicencia}
                            className="p-3 border rounded-md dark:border-slate-700 hover:bg-slate-100 dark:hover:bg-slate-700 cursor-pointer transition-colors"
                            onClick={() => seleccionarLicencia(licencia)}
                          >
                            <div className="flex justify-between items-center">
                              <div>
                                <p className="font-medium dark:text-white">{licencia.titular.nombreApellido}</p>
                                <p className="text-sm text-slate-500 dark:text-slate-400">
                                  {licencia.titular.tipoDocumento} {licencia.titular.numeroDocumento} - Clase{" "}
                                  {licencia.claseLicencia}
                                </p>
                              </div>
                              <div className="text-right">
                                <p className="text-sm dark:text-white">Recibo N° R-{licencia.numeroLicencia}</p>
                                <p className="text-xs text-slate-500 dark:text-slate-400">
                                  Emitido: {new Date(licencia.fechaEmision).toLocaleDateString("es-ES")}
                                </p>
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              ) : (
                <div ref={comprobanteRef} className="border rounded-lg overflow-hidden dark:border-slate-700">
                  <div className="bg-slate-100 dark:bg-slate-800 p-4 border-b dark:border-slate-700">
                    <div className="flex justify-between items-center">
                      <div className="flex items-center gap-3">
                        <div className="w-12 h-12 rounded-full bg-white/20 flex items-center justify-center overflow-hidden">
                          <Image
                            src="/images/logo-auto.png"
                            alt="Logo Municipal"
                            width={40}
                            height={40}
                            className="rounded-full object-contain"
                          />
                        </div>
                        <div>
                          <h3 className="font-bold dark:text-white">MUNICIPALIDAD</h3>
                          <p className="text-xs dark:text-slate-300">COMPROBANTE DE PAGO</p>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="text-sm dark:text-white">RECIBO N° R-{licenciaSeleccionada.numeroLicencia}</p>
                        <p className="text-xs dark:text-slate-300">
                          FECHA: {new Date(licenciaSeleccionada.fechaEmision).toLocaleDateString("es-ES")}
                        </p>
                      </div>
                    </div>
                  </div>

                  <div className="p-6 dark:bg-slate-800">
                    <div className="space-y-6">
                      <div className="flex justify-between items-center">
                        <h3 className="font-semibold dark:text-white">DATOS DEL TITULAR</h3>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => {
                            setLicenciaSeleccionada(null)
                            setBusquedaRealizada(false)
                          }}
                        >
                          Cambiar Comprobante
                        </Button>
                      </div>
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                          <p className="text-xs text-slate-500 dark:text-slate-400">APELLIDO Y NOMBRE</p>
                          <p className="dark:text-white">{licenciaSeleccionada.titular.nombreApellido}</p>
                        </div>

                        <div>
                          <p className="text-xs text-slate-500 dark:text-slate-400">DOCUMENTO</p>
                          <p className="dark:text-white">
                            {licenciaSeleccionada.titular.tipoDocumento} {licenciaSeleccionada.titular.numeroDocumento}
                          </p>
                        </div>
                      </div>

                      <div>
                        <h3 className="font-semibold mb-2 dark:text-white">DETALLE DE PAGO</h3>
                        <div className="border rounded-md overflow-hidden dark:border-slate-700">
                          <table className="w-full">
                            <thead className="bg-slate-100 dark:bg-slate-700">
                              <tr>
                                <th className="text-left p-2 dark:text-white">Concepto</th>
                                <th className="text-right p-2 dark:text-white">Importe</th>
                              </tr>
                            </thead>
                            <tbody>
                              <tr className="border-t dark:border-slate-700">
                                <td className="p-2 dark:text-white">
                                  <p>Emisión de Licencia Clase {licenciaSeleccionada.claseLicencia}</p>
                                  <p className="text-xs text-slate-500 dark:text-slate-400">
                                    Vigencia: {licenciaSeleccionada.vigencia} años
                                  </p>
                                </td>
                                <td className="p-2 text-right dark:text-white">${licenciaSeleccionada.costo}</td>
                              </tr>
                              <tr className="border-t bg-slate-50 dark:bg-slate-700 dark:border-slate-600">
                                <td className="p-2 font-semibold dark:text-white">TOTAL</td>
                                <td className="p-2 text-right font-semibold dark:text-white">
                                  ${licenciaSeleccionada.costo}
                                </td>
                              </tr>
                            </tbody>
                          </table>
                        </div>
                      </div>

                      <div className="border-t pt-4 flex justify-between dark:border-slate-700">
                        <div>
                          <p className="text-xs text-slate-500 dark:text-slate-400">OPERADOR</p>
                          <p className="dark:text-white">Admin Sistema</p>
                        </div>
                        <div className="text-right">
                          <p className="text-xs text-slate-500 dark:text-slate-400">FECHA Y HORA</p>
                          <p className="dark:text-white">{new Date().toLocaleString("es-ES")}</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              )}

              <div className="flex justify-end gap-4">
                <Button
                  variant="outline"
                  onClick={() => router.push(`/dashboard?role=${role}`)}
                  className="transition-transform duration-300 hover:scale-105"
                >
                  <ArrowLeft className="h-4 w-4 mr-2" />
                  Volver
                </Button>
                {licenciaSeleccionada && (
                  <Button
                    onClick={generarPDFDirecto}
                    disabled={generandoPDF}
                    className="transition-transform duration-300 hover:scale-105"
                  >
                    <Download className="h-4 w-4 mr-2" />
                    {generandoPDF ? "Generando PDF..." : "Descargar PDF"}
                  </Button>
                )}
              </div>
            </div>
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  )
}
