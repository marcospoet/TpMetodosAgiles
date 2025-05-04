"use client"

import { useSearchParams } from "next/navigation"
import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { UserCircle, BadgeIcon as IdCard, Printer } from "lucide-react"
import Navigation from "@/components/navigation"

export default function Dashboard() {
  const searchParams = useSearchParams()
  const router = useRouter()
  const [role, setRole] = useState<string | null>(null)

  useEffect(() => {
    const userRole = searchParams.get("role")
    if (!userRole) {
      router.push("/")
      return
    }
    setRole(userRole)
  }, [searchParams, router])

  const navigateTo = (path: string) => {
    if (role) {
      router.push(`${path}?role=${role}`)
    }
  }

  if (!role) {
    return <div className="flex justify-center items-center h-screen">Cargando...</div>
  }

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-900">
      <Navigation role={role} />

      <main className="container mx-auto py-8 px-4">
        <h1 className="text-3xl font-bold mb-8 text-slate-800 dark:text-white">Panel de Control</h1>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <Card className="hover:shadow-md transition-shadow dark:border-slate-700 hover:scale-105 transition-transform duration-300">
            <CardHeader className="pb-2">
              <CardTitle className="text-xl">Gestión de Titulares</CardTitle>
              <CardDescription>Registro y administración de titulares</CardDescription>
            </CardHeader>
            <CardContent>
              <Button
                className="w-full flex items-center justify-center gap-2 h-16 text-lg"
                onClick={() => navigateTo("/dashboard/titulares")}
              >
                <UserCircle className="h-6 w-6" />
                <span>Alta de Titular</span>
              </Button>
            </CardContent>
          </Card>

          <Card className="hover:shadow-md transition-shadow dark:border-slate-700 hover:scale-105 transition-transform duration-300">
            <CardHeader className="pb-2">
              <CardTitle className="text-xl">Emisión de Licencias</CardTitle>
              <CardDescription>Emitir nuevas licencias de conducir</CardDescription>
            </CardHeader>
            <CardContent>
              <Button
                className="w-full flex items-center justify-center gap-2 h-16 text-lg"
                onClick={() => navigateTo("/dashboard/licencias/emitir")}
              >
                <IdCard className="h-6 w-6" />
                <span>Emitir Licencia</span>
              </Button>
            </CardContent>
          </Card>

          <Card className="hover:shadow-md transition-shadow dark:border-slate-700 hover:scale-105 transition-transform duration-300">
            <CardHeader className="pb-2">
              <CardTitle className="text-xl">Impresión de Licencias</CardTitle>
              <CardDescription>Imprimir licencias y comprobantes</CardDescription>
            </CardHeader>
            <CardContent>
              <Button
                className="w-full flex items-center justify-center gap-2 h-16 text-lg"
                onClick={() => navigateTo("/dashboard/licencias/imprimir")}
              >
                <Printer className="h-6 w-6" />
                <span>Imprimir Licencia</span>
              </Button>
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  )
}
