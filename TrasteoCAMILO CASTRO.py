class Caja:
    def __init__(self, codigo, contenido):
        self.codigo = codigo
        self.contenido = contenido
    
    def __str__(self):
        return f"[{self.codigo}: {self.contenido}]"


class Trasteo:
    def __init__(self, nombre, capacidad):
        self.nombre = nombre
        self.capacidad = capacidad
        self.cajas = []
    
    def push(self, caja):
        if len(self.cajas) >= self.capacidad:
            print(f"Trasteo lleno. No cabe {caja.codigo}")
            return False
        self.cajas.append(caja)
        print(f"Guardada: {caja}")
        return True
    
    def pop(self):
        if not self.cajas:
            print("Trasteo vacio")
            return None
        caja = self.cajas.pop()
        print(f"Sacada: {caja}")
        return caja
    
    def ver_tope(self):
        if self.cajas:
            return self.cajas[-1]
        return None
    
    def mostrar(self):
        print(f"\n=== {self.nombre} ===")
        if not self.cajas:
            print("[VACIO]")
            return
        
        print("TOPE (ultima en entrar):")
        for i in range(len(self.cajas)-1, -1, -1):
            marca = " <-- AQUI" if i == len(self.cajas)-1 else ""
            print(f"  {self.cajas[i]}{marca}")
        print("FONDO (primera en entrar)")


# Programa principal
print("REGISTRO DE TRASTEO")
print("=" * 30)

mi_trasteo = Trasteo("Mi Trasteo", 5)

caja1 = Caja("C001", "Libros")
caja2 = Caja("C002", "Ropa")
caja3 = Caja("C003", "Herramientas")

mi_trasteo.push(caja1)
mi_trasteo.push(caja2)
mi_trasteo.push(caja3)

mi_trasteo.mostrar()

print("\nSacando ultima caja:")
mi_trasteo.pop()

mi_trasteo.mostrar()