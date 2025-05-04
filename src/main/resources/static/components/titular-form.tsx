"use client"

import { useState, useEffect, useRef } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent } from "@/components/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group"
import { CheckCircle2, ArrowLeft } from "lucide-react"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import * as z from "zod"
import gsap from "gsap"

const formSchema = z.object({
  tipoDocumento: z.string().min(1, "Seleccione un tipo de documento"),
  numeroDocumento: z.string().min(1, "Ingrese un número de documento").regex(/^\d+$/, "Solo se permiten números"),
  nombreApellido: z.string().min(3, "Ingrese nombre y apellido completos"),
  fechaNacimiento: z.string().min(1, "Seleccione una fecha de nacimiento"),
  direccion: z.string().min(5, "Ingrese una dirección válida"),
  claseSolicitada: z.string().min(1, "Seleccione una clase"),
  grupoSanguineo: z.string().min(1, "Seleccione un grupo sanguíneo"),
  factorRh: z.string().min(1, "Seleccione un factor RH"),
  donanteOrganos: z.string().min(1, "Seleccione si es donante de órganos"),
})

interface TitularFormProps {
  role: string
}

export default function TitularForm({ role }: TitularFormProps) {
  const router = useRouter()
  const [success, setSuccess] = useState(false)
  const formFieldsRef = useRef<HTMLDivElement>(null)
  const buttonsRef = useRef<HTMLDivElement>(null)

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      tipoDocumento: "",
      numeroDocumento: "",
      nombreApellido: "",
      fechaNacimiento: "",
      direccion: "",
      claseSolicitada: "",
      grupoSanguineo: "",
      factorRh: "",
      donanteOrganos: "",
    },
  })

  useEffect(() => {
    // Animación de los campos del formulario
    if (formFieldsRef.current) {
      gsap.fromTo(
        formFieldsRef.current.children,
        { opacity: 0, y: 20 },
        {
          opacity: 1,
          y: 0,
          duration: 0.5,
          stagger: 0.1,
          ease: "power2.out",
        },
      )
    }

    // Animación de los botones
    if (buttonsRef.current) {
      gsap.fromTo(
        buttonsRef.current.children,
        { opacity: 0, scale: 0.9 },
        {
          opacity: 1,
          scale: 1,
          duration: 0.5,
          delay: 0.8,
          stagger: 0.15,
          ease: "back.out(1.7)",
        },
      )
    }
  }, [])

  const onSubmit = (values: z.infer<typeof formSchema>) => {
    // En producción, aquí se enviarían los datos al backend
    console.log(values)

    // Simulación de éxito
    setSuccess(true)

    // Redireccionar después de 2 segundos
    setTimeout(() => {
      router.push(`/dashboard?role=${role}`)
    }, 2000)
  }

  return (
    <Card className="w-full dark:border-slate-700">
      <CardContent className="pt-6">
        {success ? (
          <Alert className="bg-green-50 border-green-200 mb-4 dark:bg-green-900 dark:border-green-800">
            <CheckCircle2 className="h-4 w-4 text-green-600 dark:text-green-400" />
            <AlertDescription className="text-green-600 dark:text-green-400">
              Titular registrado correctamente. Redirigiendo...
            </AlertDescription>
          </Alert>
        ) : (
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
              <div ref={formFieldsRef} className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <FormField
                    control={form.control}
                    name="tipoDocumento"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Tipo de Documento *</FormLabel>
                        <Select onValueChange={field.onChange} defaultValue={field.value}>
                          <FormControl>
                            <SelectTrigger>
                              <SelectValue placeholder="Seleccionar" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            <SelectItem value="DNI">DNI</SelectItem>
                            <SelectItem value="Pasaporte">Pasaporte</SelectItem>
                          </SelectContent>
                        </Select>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="numeroDocumento"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Número de Documento *</FormLabel>
                        <FormControl>
                          <Input placeholder="Ingrese solo números" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>

                <FormField
                  control={form.control}
                  name="nombreApellido"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Nombre y Apellido *</FormLabel>
                      <FormControl>
                        <Input placeholder="Nombre y apellido completos" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <FormField
                    control={form.control}
                    name="fechaNacimiento"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Fecha de Nacimiento *</FormLabel>
                        <FormControl>
                          <Input type="date" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="direccion"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Dirección *</FormLabel>
                        <FormControl>
                          <Input placeholder="Dirección completa" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <FormField
                    control={form.control}
                    name="claseSolicitada"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Clase Solicitada *</FormLabel>
                        <Select onValueChange={field.onChange} defaultValue={field.value}>
                          <FormControl>
                            <SelectTrigger>
                              <SelectValue placeholder="Seleccionar" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            <SelectItem value="A">Clase A</SelectItem>
                            <SelectItem value="B">Clase B</SelectItem>
                          </SelectContent>
                        </Select>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="grupoSanguineo"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Grupo Sanguíneo *</FormLabel>
                        <Select onValueChange={field.onChange} defaultValue={field.value}>
                          <FormControl>
                            <SelectTrigger>
                              <SelectValue placeholder="Seleccionar" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            <SelectItem value="0">0</SelectItem>
                            <SelectItem value="A">A</SelectItem>
                            <SelectItem value="B">B</SelectItem>
                            <SelectItem value="AB">AB</SelectItem>
                          </SelectContent>
                        </Select>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <FormField
                    control={form.control}
                    name="factorRh"
                    render={({ field }) => (
                      <FormItem className="space-y-3">
                        <FormLabel>Factor RH *</FormLabel>
                        <FormControl>
                          <RadioGroup onValueChange={field.onChange} defaultValue={field.value} className="flex gap-6">
                            <div className="flex items-center space-x-2">
                              <RadioGroupItem value="+" id="rh-positivo" />
                              <Label htmlFor="rh-positivo">Positivo (+)</Label>
                            </div>
                            <div className="flex items-center space-x-2">
                              <RadioGroupItem value="-" id="rh-negativo" />
                              <Label htmlFor="rh-negativo">Negativo (-)</Label>
                            </div>
                          </RadioGroup>
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="donanteOrganos"
                    render={({ field }) => (
                      <FormItem className="space-y-3">
                        <FormLabel>Donante de Órganos *</FormLabel>
                        <FormControl>
                          <RadioGroup onValueChange={field.onChange} defaultValue={field.value} className="flex gap-6">
                            <div className="flex items-center space-x-2">
                              <RadioGroupItem value="Si" id="donante-si" />
                              <Label htmlFor="donante-si">Sí</Label>
                            </div>
                            <div className="flex items-center space-x-2">
                              <RadioGroupItem value="No" id="donante-no" />
                              <Label htmlFor="donante-no">No</Label>
                            </div>
                          </RadioGroup>
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>
              </div>

              <div ref={buttonsRef} className="flex justify-end gap-4">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => router.push(`/dashboard?role=${role}`)}
                  className="transition-transform duration-300 hover:scale-105"
                >
                  <ArrowLeft className="h-4 w-4 mr-2" />
                  Volver
                </Button>
                <Button type="submit" className="transition-transform duration-300 hover:scale-105">
                  Guardar Titular
                </Button>
              </div>
            </form>
          </Form>
        )}
      </CardContent>
    </Card>
  )
}
