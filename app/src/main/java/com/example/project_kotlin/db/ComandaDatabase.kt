package com.example.project_kotlin.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.project_kotlin.dao.*
import com.example.project_kotlin.entidades.*
import com.example.project_kotlin.entidades.firebase.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Database(
    entities = [Caja::class,
        Cargo::class, Comanda::class,
        Comprobante::class, Usuario::class,
        CategoriaPlato::class, Mesa::class,
        DetalleComanda::class,
        Empleado::class, Plato::class, Establecimiento::class,
        EstadoComanda::class, MetodoPago::class, TipoComprobante::class],
    version = 1,
    exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class ComandaDatabase : RoomDatabase() {
    abstract fun cajaDao() : CajaDao
    abstract fun cargoDao() : CargoDao
    abstract fun comandaDao() : ComandaDao
    abstract fun comprobanteDao() : ComprobanteDao
    abstract fun usuarioDao() : UsuarioDao
    abstract fun categoriaPlatoDao() : CategoriaPlatoDao
    abstract fun mesaDao() : MesaDao

    //Entidades de Gary Morales
    abstract fun metodoPagoDao() : MetodoPagoDao
    abstract fun tipoComprobanteDao() : TipoComprobanteDao
    abstract fun estadoComandaDao(): EstadoComandaDao
    abstract fun establecimientoDao() : EstablecimientoDao
    abstract fun platoDao() : PlatoDao
    abstract fun empleadoDao() : EmpleadoDao
    abstract fun detalleComandaDao() : DetalleComandaDao

    companion object {
        @Volatile
        private var INSTANCIA: ComandaDatabase? = null

        fun obtenerBaseDatos(context: Context): ComandaDatabase {
            return INSTANCIA ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ComandaDatabase::class.java,
                    "comanda_database"
                ) .addCallback(object : RoomDatabase.Callback() {
                    @Override
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        CoroutineScope(Dispatchers.IO).launch() {
                            val instancia = obtenerBaseDatos(context)
                            //Al crear ingresar datos
                            val estadosComandaDao = instancia?.estadoComandaDao()
                            val metodosPagoDao = instancia?.metodoPagoDao()
                            val categoriaPlatoDao = instancia?.categoriaPlatoDao()
                            val platoDao = instancia?.platoDao()
                            val establecimientoDao = instancia?.establecimientoDao()
                            val tipoComprobanteDao = instancia?.tipoComprobanteDao()
                            val empleadoDao = instancia?.empleadoDao()
                            val usuarioDao = instancia?.usuarioDao()
                            val cargoDao = obtenerBaseDatos(context).cargoDao()

                            var bdFirebase : DatabaseReference = FirebaseDatabase.getInstance().reference
                            bdFirebase.removeValue()
                            //Agregando cargos
                            cargoDao.guardar(Cargo(cargo= "ADMINISTRADOR"))
                            cargoDao.guardar(Cargo(cargo = "MESERO"))
                            cargoDao.guardar(Cargo(cargo= "CAJERO"))
                            cargoDao.guardar(Cargo(cargo="GERENTE"))
                            bdFirebase.child("cargo").child("1").setValue(CargoNoSql("ADMINISTRADOR"))
                            bdFirebase.child("cargo").child("2").setValue(CargoNoSql("MESERO"))
                            bdFirebase.child("cargo").child("3").setValue(CargoNoSql("CAJERO"))
                            bdFirebase.child("cargo").child("4").setValue(CargoNoSql("GERENTE"))

                            //Agregando estados
                            estadosComandaDao?.guardar(EstadoComanda(estadoComanda ="Generada"))
                            estadosComandaDao?.guardar(EstadoComanda(estadoComanda= "Pagada"))
                            bdFirebase.child("estadocomanda").child("1").setValue(EstadoComandaNoSql("Generada"))
                            bdFirebase.child("estadocomanda").child("2").setValue(EstadoComandaNoSql("Pagada"))

                            //Métodos de pago
                            metodosPagoDao?.registrar(MetodoPago(nombreMetodoPago =  "En efectivo"))
                            metodosPagoDao?.registrar(MetodoPago(nombreMetodoPago =  "BCP"))
                            metodosPagoDao?.registrar(MetodoPago(nombreMetodoPago =  "BBVA"))
                            metodosPagoDao?.registrar(MetodoPago(nombreMetodoPago =  "Scotiabank"))
                            metodosPagoDao?.registrar(MetodoPago(nombreMetodoPago =  "Interbank"))
                            bdFirebase.child("metodopago").child("1").setValue(MetodoPagoNoSql("En efectivo"))
                            bdFirebase.child("metodopago").child("2").setValue(MetodoPagoNoSql("BCP"))
                            bdFirebase.child("metodopago").child("3").setValue(MetodoPagoNoSql("BBVA"))
                            bdFirebase.child("metodopago").child("4").setValue(MetodoPagoNoSql("Scotiabank"))
                            bdFirebase.child("metodopago").child("5").setValue(MetodoPagoNoSql("Interbank"))

                            //Categoría Plato

                            categoriaPlatoDao?.guardar(CategoriaPlato("C-001", "Bebidas"))
                            categoriaPlatoDao?.guardar(CategoriaPlato("C-002", "Hamburguesas"))
                            categoriaPlatoDao?.guardar(CategoriaPlato("C-003", "Postres"))
                            categoriaPlatoDao?.guardar(CategoriaPlato("C-004", "Sopas"))
                            bdFirebase.child("categoria").child("1").setValue(CategoriaPlatoNoSql("Bebidas"))
                            bdFirebase.child("categoria").child("2").setValue(CategoriaPlatoNoSql("Hamburguesas"))
                            bdFirebase.child("categoria").child("3").setValue(CategoriaPlatoNoSql("Postres"))
                            bdFirebase.child("categoria").child("4").setValue(CategoriaPlatoNoSql("Sopas"))


                            //Establecimiento
                            establecimientoDao?.guardar(
                                Establecimiento(1, "Makako Burger",
                                    "942850902", "Av.Izaguirre", "20217382809")
                            )
                            //Tipo comprobante
                            tipoComprobanteDao?.guardar(TipoComprobante(tipo = "Nota de Venta"))
                            tipoComprobanteDao?.guardar(TipoComprobante(tipo = "Boleta"))
                            bdFirebase.child("tipocomprobante").child("1").setValue(TipoComprobanteNoSql("Nota de Venta"))
                            bdFirebase.child("tipocomprobante").child("2").setValue(TipoComprobanteNoSql("Boleta"))

                            //FECHA PARA EMPLEADO
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
                            val fechaActual = Date()
                            val fechaFormateada = dateFormat.format(fechaActual)

                            //CREAR EMPLEADOS 1
                            val usuario = Usuario(correo= "admin@admin.com", contrasena = "admin")
                            usuarioDao?.guardar(usuario)
                            val empleado = Empleado(nombreEmpleado = "Admin", apellidoEmpleado = "Admin", telefonoEmpleado = "999999999",
                                    dniEmpleado = "77777727", fechaRegistro = fechaFormateada, cargo_id = 1, usuario_id = 1)
                            val empleadoId1 = empleadoDao?.guardar(empleado)
                            val cargoNoSql1 = CargoNoSql("ADMINISTRADOR")
                            val usuarioNoSql1 = UsuarioNoSql(usuario.correo, usuario.contrasena)
                            val empleadoNoSql1 = EmpleadoNoSql("Admin", "Admin", "999999999", "77777727", fechaFormateada,
                            usuarioNoSql1, cargoNoSql1)
                            bdFirebase.child("empleado").child(empleadoId1.toString()).setValue(empleadoNoSql1)

                            //CREAR EMPLEADOS 2
                            val usuario2 = Usuario(correo= "mesero@mesero.com", contrasena = "mesero")
                            usuarioDao?.guardar(usuario2)
                            val empleado2 = Empleado(nombreEmpleado = "Mesero", apellidoEmpleado = "Mesero", telefonoEmpleado = "999999998",
                                dniEmpleado = "87777777", fechaRegistro = fechaFormateada, cargo_id = 2, usuario_id = 2)
                            val empleadoId2 = empleadoDao?.guardar(empleado2)
                            val cargoNoSql2 = CargoNoSql("MESERO")
                            val usuarioNoSql2 = UsuarioNoSql(usuario.correo, usuario.contrasena)
                            val empleadoNoSql2 = EmpleadoNoSql("Mesero", "Mesero", "999999998", "87777777", fechaFormateada,
                                usuarioNoSql2, cargoNoSql2)
                            bdFirebase.child("empleado").child(empleadoId2.toString()).setValue(empleadoNoSql2)

                            //CREAR EMPLEADOS 3
                            val usuario3 = Usuario(correo= "cajero@cajero.com", contrasena = "cajero")
                            usuarioDao?.guardar(usuario3)
                            val empleado3 = Empleado(nombreEmpleado = "Cajero", apellidoEmpleado = "Cajero", telefonoEmpleado = "999999997",
                                dniEmpleado = "27777777", fechaRegistro = fechaFormateada, cargo_id = 3, usuario_id = 3)
                            val empleadoId3 = empleadoDao?.guardar(empleado3)
                            val cargoNoSql3 = CargoNoSql("CAJERO")
                            val usuarioNoSql3 = UsuarioNoSql(usuario.correo, usuario.contrasena)
                            val empleadoNoSql3 = EmpleadoNoSql("Cajero", "Cajero", "999999997", "27777777", fechaFormateada,
                                usuarioNoSql3, cargoNoSql3)
                            bdFirebase.child("empleado").child(empleadoId3.toString()).setValue(empleadoNoSql3)


                            //CREAR EMPLEADOS 4
                            val usuario5 = Usuario(correo= "gerente@gerente.com", contrasena = "gerente")
                            usuarioDao?.guardar(usuario5)
                            val empleado5 = Empleado(nombreEmpleado = "Gerente", apellidoEmpleado = "Gerente", telefonoEmpleado = "999999992",
                                dniEmpleado = "55777777", fechaRegistro = fechaFormateada, cargo_id = 4, usuario_id = 4)
                            val empleadoId5 = empleadoDao?.guardar(empleado5)
                            val cargoNoSql5 = CargoNoSql("GERENTE")
                            val usuarioNoSql5 = UsuarioNoSql(usuario.correo, usuario.contrasena)
                            val empleadoNoSql5 = EmpleadoNoSql("Gerente", "Gerente", "999999992", "55777777", fechaFormateada,
                                usuarioNoSql5, cargoNoSql5)
                            bdFirebase.child("empleado").child(empleadoId5.toString()).setValue(empleadoNoSql5)

                        }
                    }
                })
                    .build()
                    .also { INSTANCIA = it }
            }
        }
    }
}

class DateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}