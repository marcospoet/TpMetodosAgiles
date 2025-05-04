"use client"

import type React from "react"

import { useState, useEffect, useRef } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent } from "@/components/ui/card"
import { AlertCircle } from "lucide-react"
import { Alert, AlertDescription } from "@/components/ui/alert"
import gsap from "gsap"

export default function LoginForm() {
  const router = useRouter()
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)

  const formRef = useRef(null)
  const titleRef = useRef(null)
  const inputsRef = useRef([])
  const buttonRef = useRef(null)

  useEffect(() => {
    // Animación de entrada con GSAP
    const tl = gsap.timeline({ defaults: { ease: "power3.out" } })

    // Animación de la tarjeta
    tl.fromTo(formRef.current, { y: 50, opacity: 0 }, { y: 0, opacity: 1, duration: 0.8 })

    // Animación del título
    tl.fromTo(titleRef.current, { y: -20, opacity: 0 }, { y: 0, opacity: 1, duration: 0.5 }, "-=0.4")

    // Animación de los inputs
    inputsRef.current.forEach((input, index) => {
      tl.fromTo(input, { x: -30, opacity: 0 }, { x: 0, opacity: 1, duration: 0.4 }, "-=0.2")
    })

    // Animación del botón
    tl.fromTo(buttonRef.current, { scale: 0.8, opacity: 0 }, { scale: 1, opacity: 1, duration: 0.5 }, "-=0.2")

    // Efecto de brillo en el botón
    gsap.to(buttonRef.current, {
      boxShadow: "0 0 15px rgba(59, 130, 246, 0.6)",
      repeat: -1,
      yoyo: true,
      duration: 1.5,
    })
  }, [])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError("")

    // Animación al hacer clic en el botón
    gsap.to(buttonRef.current, {
      scale: 0.95,
      duration: 0.1,
      onComplete: () => {
        gsap.to(buttonRef.current, {
          scale: 1,
          duration: 0.1,
        })
      },
    })

    // Simulación de login - En producción, esto se conectaría con el backend
    try {
      // Simulación de verificación
      if (email === "admin@municipio.gob" && password === "admin") {
        // Animación de salida antes de navegar
        gsap.to(formRef.current, {
          y: -30,
          opacity: 0,
          duration: 0.5,
          onComplete: () => {
            // Simular rol de administrador
            router.push("/dashboard?role=ADMIN")
          },
        })
      } else if (email === "operador@municipio.gob" && password === "operador") {
        // Animación de salida antes de navegar
        gsap.to(formRef.current, {
          y: -30,
          opacity: 0,
          duration: 0.5,
          onComplete: () => {
            // Simular rol de operador
            router.push("/dashboard?role=OPERADOR")
          },
        })
      } else {
        setError("Credenciales inválidas. Por favor, intente nuevamente.")
        // Animación de error
        gsap.fromTo(formRef.current, { x: -10 }, { x: 10, duration: 0.1, repeat: 5, yoyo: true })
        setLoading(false)
      }
    } catch (err) {
      setError("Error al iniciar sesión. Por favor, intente nuevamente.")
      setLoading(false)
    }
  }

  return (
    <Card className="w-full" ref={formRef}>
      <CardContent className="pt-6">
        <form onSubmit={handleSubmit} className="space-y-4">
          {error && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          <div className="space-y-2" ref={titleRef}>
            <Label htmlFor="email">Correo Electrónico</Label>
            <Input
              id="email"
              type="email"
              placeholder="correo@municipio.gob"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              ref={(el) => (inputsRef.current[0] = el)}
              className="transition-all focus:shadow-md focus:shadow-blue-200"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="password">Contraseña</Label>
            <Input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              ref={(el) => (inputsRef.current[1] = el)}
              className="transition-all focus:shadow-md focus:shadow-blue-200"
            />
          </div>

          <Button type="submit" className="w-full transition-all" disabled={loading} ref={buttonRef}>
            {loading ? "Iniciando sesión..." : "Iniciar Sesión"}
          </Button>

          <div className="text-sm text-center text-slate-500 mt-4">
            <p>Para fines de demostración:</p>
            <p>Admin: admin@municipio.gob / admin</p>
            <p>Operador: operador@municipio.gob / operador</p>
          </div>
        </form>
      </CardContent>
    </Card>
  )
}
